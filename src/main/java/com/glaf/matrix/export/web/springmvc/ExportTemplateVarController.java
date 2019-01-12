package com.glaf.matrix.export.web.springmvc;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

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
import com.glaf.core.config.ViewProperties;
import com.glaf.core.identity.User;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.Tools;

import com.glaf.matrix.export.domain.ExportTemplateVar;
import com.glaf.matrix.export.query.ExportTemplateVarQuery;
import com.glaf.matrix.export.service.ExportTemplateVarService;

/**
 * 
 * SpringMVC控制器
 *
 */

@Controller("/matrix/exportTemplateVar")
@RequestMapping("/matrix/exportTemplateVar")
public class ExportTemplateVarController {
	protected static final Log logger = LogFactory.getLog(ExportTemplateVarController.class);

	protected ExportTemplateVarService exportTemplateVarService;

	public ExportTemplateVarController() {

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
					ExportTemplateVar exportTemplateVar = exportTemplateVarService.getExportTemplateVar(x);
					if (exportTemplateVar != null
							&& (StringUtils.equals(exportTemplateVar.getCreateBy(), loginContext.getActorId())
									|| loginContext.isSystemAdministrator())) {
						exportTemplateVarService.deleteById(exportTemplateVar.getId());
					}
				}
			}
			return ResponseUtils.responseResult(true);
		} else if (id != null) {
			ExportTemplateVar exportTemplateVar = exportTemplateVarService.getExportTemplateVar(id);
			if (exportTemplateVar != null
					&& (StringUtils.equals(exportTemplateVar.getCreateBy(), loginContext.getActorId())
							|| loginContext.isSystemAdministrator())) {
				exportTemplateVarService.deleteById(exportTemplateVar.getId());
				return ResponseUtils.responseResult(true);
			}
		}
		return ResponseUtils.responseResult(false);
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		ExportTemplateVar exportTemplateVar = exportTemplateVarService
				.getExportTemplateVar(RequestUtils.getString(request, "id"));
		if (exportTemplateVar != null) {
			request.setAttribute("exportTemplateVar", exportTemplateVar);
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("exportTemplateVar.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/exportTemplateVar/edit", modelMap);
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, ModelMap modelMap) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ExportTemplateVarQuery query = new ExportTemplateVarQuery();
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
		int total = exportTemplateVarService.getExportTemplateVarCountByQueryCriteria(query);
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

			List<ExportTemplateVar> list = exportTemplateVarService.getExportTemplateVarsByQueryCriteria(start, limit,
					query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				result.put("rows", rowsJSON);

				for (ExportTemplateVar exportTemplateVar : list) {
					JSONObject rowJSON = exportTemplateVar.toJsonObject();
					rowJSON.put("id", exportTemplateVar.getId());
					rowJSON.put("itemId", exportTemplateVar.getId());
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

		return new ModelAndView("/matrix/exportTemplateVar/list", modelMap);
	}

	@RequestMapping("/query")
	public ModelAndView query(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}
		String x_view = ViewProperties.getString("exportTemplateVar.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/matrix/exportTemplateVar/query", modelMap);
	}

	@ResponseBody
	@RequestMapping("/save")
	public byte[] save(HttpServletRequest request) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ExportTemplateVar exportTemplateVar = new ExportTemplateVar();
		try {
			Tools.populate(exportTemplateVar, params);
			exportTemplateVar.setExpId(RequestUtils.getString(request, "expId"));
			exportTemplateVar.setDeploymentId(request.getParameter("deploymentId"));
			exportTemplateVar.setTitle(request.getParameter("title"));
			exportTemplateVar.setVarTemplate(request.getParameter("varTemplate"));
			exportTemplateVar.setLocked(RequestUtils.getInt(request, "locked"));
			exportTemplateVar.setCreateBy(actorId);
			this.exportTemplateVarService.save(exportTemplateVar);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.exportTemplateVarService")
	public void setExportTemplateVarService(ExportTemplateVarService exportTemplateVarService) {
		this.exportTemplateVarService = exportTemplateVarService;
	}

}
