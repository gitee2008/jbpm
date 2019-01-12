package com.glaf.matrix.export.web.springmvc;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.config.DatabaseConnectionConfig;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.domain.Database;
import com.glaf.core.el.ExpressionTools;
import com.glaf.core.identity.Role;
import com.glaf.core.security.IdentityFactory;
import com.glaf.core.security.LoginContext;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.handler.ExportHandler;
import com.glaf.matrix.export.handler.JxlsExportHandler;
import com.glaf.matrix.export.query.ExportAppQuery;
import com.glaf.matrix.export.service.ExportAppService;
import com.glaf.matrix.export.service.ExportHistoryService;
import com.glaf.matrix.parameter.handler.ParameterFactory;
import com.glaf.template.Template;
import com.glaf.template.service.ITemplateService;

/**
 * 
 * SpringMVC控制器
 *
 */

@Controller("/matrix/exportApp")
@RequestMapping("/matrix/exportApp")
public class ExportAppController {
	protected static final Log logger = LogFactory.getLog(ExportAppController.class);

	protected IDatabaseService databaseService;

	protected ExportAppService exportAppService;

	protected ExportHistoryService exportHistoryService;

	protected ITemplateService templateService;

	public ExportAppController() {

	}

	@ResponseBody
	@RequestMapping("/delete")
	public byte[] delete(HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String id = RequestUtils.getString(request, "id");
		String ids = request.getParameter("ids");
		if (StringUtils.isNotEmpty(ids)) {
			StringTokenizer token = new StringTokenizer(ids, ",");
			while (token.hasMoreTokens()) {
				String x = token.nextToken();
				if (StringUtils.isNotEmpty(x)) {
					ExportApp exportApp = exportAppService.getExportApp(x);
					if (exportApp != null && (StringUtils.equals(exportApp.getCreateBy(), loginContext.getActorId())
							|| loginContext.isSystemAdministrator())) {

					}
				}
			}
			return ResponseUtils.responseResult(true);
		} else if (id != null) {
			ExportApp exportApp = exportAppService.getExportApp(id);
			if (exportApp != null && (StringUtils.equals(exportApp.getCreateBy(), loginContext.getActorId())
					|| loginContext.isSystemAdministrator())) {

				return ResponseUtils.responseResult(true);
			}
		}
		return ResponseUtils.responseResult(false);
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		ExportApp exportApp = exportAppService.getExportApp(RequestUtils.getString(request, "id"));
		if (exportApp != null) {
			request.setAttribute("exportApp", exportApp);
		}

		List<Role> roles = IdentityFactory.getRoles();
		request.setAttribute("roles", roles);

		Map<String, Template> templateMap = templateService.getAllTemplate();
		if (templateMap != null && !templateMap.isEmpty()) {
			request.setAttribute("templates", templateMap.values());
		}

		LoginContext loginContext = RequestUtils.getLoginContext(request);

		DatabaseConnectionConfig cfg = new DatabaseConnectionConfig();
		List<Database> activeDatabases = cfg.getActiveDatabases(loginContext);
		if (activeDatabases != null && !activeDatabases.isEmpty()) {

		}
		request.setAttribute("databases", activeDatabases);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("exportApp.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/exportApp/edit", modelMap);
	}

	@ResponseBody
	@RequestMapping("/exportXls")
	public void exportXls(HttpServletRequest request, HttpServletResponse response) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = getParameterMap(request);
		params.put("login_user", loginContext.getUser());
		params.put("login_userid", loginContext.getActorId());
		logger.debug("request params:" + params);
		String expId = RequestUtils.getString(request, "expId");
		try {
			ExportApp exportApp = exportAppService.getExportApp(expId);
			if (exportApp != null && StringUtils.equals(exportApp.getActive(), "Y")) {
				Template tpl = templateService.getTemplate(exportApp.getTemplateId());
				if (tpl != null && tpl.getData() != null) {
					boolean hasPerm = true;
					if (StringUtils.isNotEmpty(exportApp.getAllowRoles())) {
						hasPerm = false;
						List<String> roles = StringTools.split(exportApp.getAllowRoles());
						if (loginContext.isSystemAdministrator()) {
							hasPerm = true;
						}
						Collection<String> permissions = loginContext.getPermissions();
						for (String perm : permissions) {
							if (roles.contains(perm)) {
								hasPerm = true;
								break;
							}
						}
					}

					if (hasPerm) {
						ParameterFactory.getInstance().processAll(expId, params);
						ExportHandler handler = new JxlsExportHandler();
						byte[] data = handler.export(loginContext, expId, params);
						String filename = exportApp.getExportFileExpr();
						params.put("yyyyMMdd", DateUtils.getDateTime("yyyyMMdd", new Date()));
						params.put("yyyyMMddHHmm", DateUtils.getDateTime("yyyyMMddHHmm", new Date()));
						params.put("yyyyMMddHHmmss", DateUtils.getDateTime("yyyyMMddHHmmss", new Date()));

						filename = ExpressionTools.evaluate(filename, params);
						if (filename == null) {
							filename = exportApp.getTitle();
						}

						if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
							ResponseUtils.download(request, response, data, filename + ".xlsx");
						} else {
							ResponseUtils.download(request, response, data, filename + ".xls");
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
	}

	public Map<String, Object> getParameterMap(HttpServletRequest request) {
		Map<String, Object> parameter = new HashMap<String, Object>();
		Enumeration<?> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String paramName = (String) enumeration.nextElement();
			String paramValue = RequestUtils.getParameter(request, paramName);
			if (StringUtils.isNotEmpty(paramName) && StringUtils.isNotEmpty(paramValue)) {
				if (paramValue.equalsIgnoreCase("null")) {
					continue;
				}
				parameter.put(paramName, paramValue);
				parameter.put(paramName.toLowerCase(), paramValue);
				String tmp = paramName.trim().toLowerCase();
				if (StringUtils.endsWith(tmp, "date") && !StringUtils.equals(paramValue, "asc")
						&& !StringUtils.equals(paramValue, "desc")) {
					try {
						logger.debug(paramName + " value:" + paramValue);
						Date date = DateUtils.toDate(paramValue);
						parameter.put(tmp, date);
						parameter.put(tmp.toLowerCase(), date);
					} catch (java.lang.Throwable ex) {
					}
				}
			}
		}
		return parameter;
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, ModelMap modelMap) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ExportAppQuery query = new ExportAppQuery();
		Tools.populate(query, params);
		query.deleteFlag(0);
		query.setActorId(loginContext.getActorId());
		query.setLoginContext(loginContext);
		/**
		 * 此处业务逻辑需自行调整
		 */
		if (!loginContext.isSystemAdministrator()) {
			String actorId = loginContext.getActorId();
			query.createBy(actorId);
		}

		int start = 0;
		int limit = 10;
		String orderName = null;
		String order = null;

		int pageNo = ParamUtils.getInt(params, "page");
		limit = ParamUtils.getInt(params, "rows");
		start = (pageNo - 1) * limit;
		orderName = ParamUtils.getString(params, "sortName");
		order = ParamUtils.getString(params, "sortOrder");

		if (start < 0) {
			start = 0;
		}

		if (limit <= 0) {
			limit = Paging.DEFAULT_PAGE_SIZE;
		}

		JSONObject result = new JSONObject();
		int total = exportAppService.getExportAppCountByQueryCriteria(query);
		if (total > 0) {
			result.put("total", total);
			result.put("totalCount", total);
			result.put("totalRecords", total);
			result.put("start", start);
			result.put("startIndex", start);
			result.put("limit", limit);
			result.put("pageSize", limit);

			if (StringUtils.isNotEmpty(orderName)) {
				query.setSortOrder(orderName);
				if (StringUtils.equals(order, "desc")) {
					query.setSortOrder(" desc ");
				}
			}

			List<ExportApp> list = exportAppService.getExportAppsByQueryCriteria(start, limit, query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				result.put("rows", rowsJSON);

				for (ExportApp exportApp : list) {
					JSONObject rowJSON = exportApp.toJsonObject();
					rowJSON.put("id", exportApp.getId());
					rowJSON.put("exportAppId", exportApp.getId());
					rowJSON.put("startIndex", ++start);
					rowsJSON.add(rowJSON);
				}

			}
		} else {
			JSONArray rowsJSON = new JSONArray();
			result.put("rows", rowsJSON);
			result.put("total", total);
		}
		return result.toJSONString().getBytes("UTF-8");
	}

	@RequestMapping
	public ModelAndView list(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		return new ModelAndView("/matrix/exportApp/list", modelMap);
	}

	@RequestMapping("/query")
	public ModelAndView query(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}
		String x_view = ViewProperties.getString("exportApp.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/matrix/exportApp/query", modelMap);
	}

	@ResponseBody
	@RequestMapping("/save")
	public byte[] save(HttpServletRequest request) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		if (!loginContext.isSystemAdministrator()) {
			return ResponseUtils.responseJsonResult(false, "只有管理员才能操作");
		}
		String actorId = loginContext.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ExportApp exportApp = new ExportApp();
		try {
			Tools.populate(exportApp, params);
			exportApp.setTitle(request.getParameter("title"));
			exportApp.setNodeId(RequestUtils.getLong(request, "nodeId"));
			exportApp.setSrcDatabaseId(RequestUtils.getLong(request, "srcDatabaseId"));
			exportApp.setSyncFlag(request.getParameter("syncFlag"));
			exportApp.setType(request.getParameter("type"));
			exportApp.setActive(request.getParameter("active"));
			exportApp.setAllowRoles(request.getParameter("allowRoles"));
			exportApp.setTemplateId(request.getParameter("templateId"));
			exportApp.setExportFileExpr(request.getParameter("exportFileExpr"));
			exportApp.setExternalColumnsFlag(request.getParameter("externalColumnsFlag"));
			exportApp.setInterval(RequestUtils.getInt(request, "interval"));
			exportApp.setSortNo(RequestUtils.getInt(request, "sortNo"));
			exportApp.setCreateBy(actorId);
			exportApp.setUpdateBy(actorId);

			this.exportAppService.save(exportApp);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@ResponseBody
	@RequestMapping("/saveAs")
	public byte[] saveAs(HttpServletRequest request) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		if (!loginContext.isSystemAdministrator()) {
			return ResponseUtils.responseJsonResult(false, "只有管理员才能操作");
		}
		String actorId = loginContext.getActorId();
		try {
			String expId = RequestUtils.getString(request, "expId");
			if (expId != null) {
				String nid = exportAppService.saveAs(expId, actorId);
				if (nid != null) {
					return ResponseUtils.responseJsonResult(true);
				}
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource
	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.exportAppService")
	public void setExportAppService(ExportAppService exportAppService) {
		this.exportAppService = exportAppService;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.exportHistoryService")
	public void setExportHistoryService(ExportHistoryService exportHistoryService) {
		this.exportHistoryService = exportHistoryService;
	}

	@javax.annotation.Resource
	public void setTemplateService(ITemplateService templateService) {
		this.templateService = templateService;
	}

}
