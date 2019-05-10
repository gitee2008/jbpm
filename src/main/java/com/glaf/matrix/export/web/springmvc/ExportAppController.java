package com.glaf.matrix.export.web.springmvc;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.glaf.core.base.BaseItem;
import com.glaf.core.base.DataFile;
import com.glaf.core.config.DatabaseConnectionConfig;
import com.glaf.core.config.SystemConfig;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.domain.Database;
import com.glaf.core.el.ExpressionTools;
import com.glaf.core.identity.Role;
import com.glaf.core.security.IdentityFactory;
import com.glaf.core.security.LoginContext;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.ExcelToHtml;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.IOUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;
import com.glaf.core.util.ZipUtils;
import com.glaf.matrix.export.bean.ExportTask;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.handler.ExportHandler;
import com.glaf.matrix.export.handler.JxlsExportHandler;
import com.glaf.matrix.export.handler.WorkbookFactory;
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

		ConcurrentMap<String, String> nameMap02 = WorkbookFactory.getNameMap();

		List<BaseItem> allDataItems02 = new ArrayList<BaseItem>();
		List<BaseItem> selectedDataItems02 = new ArrayList<BaseItem>();
		List<String> selecteds02 = new ArrayList<String>();

		Set<Entry<String, String>> entrySet2 = nameMap02.entrySet();
		for (Entry<String, String> entry2 : entrySet2) {
			String key = entry2.getKey();
			String value = entry2.getValue();
			BaseItem item = new BaseItem();
			item.setName(key);
			item.setTitle(value);
			allDataItems02.add(item);
		}

		ExportApp exportApp = exportAppService.getExportApp(RequestUtils.getString(request, "id"));
		if (exportApp != null) {
			request.setAttribute("exportApp", exportApp);

			if (StringUtils.isNotEmpty(exportApp.getExcelProcessChains())) {
				List<String> chains = StringTools.split(exportApp.getExcelProcessChains());
				for (String name : chains) {
					BaseItem item = new BaseItem();
					item.setName(name);
					item.setTitle(nameMap02.get(name));
					// allDataItems02.remove(item);
					selectedDataItems02.add(item);
					selecteds02.add(name);
				}
			}
		}

		StringBuffer bufferx2 = new StringBuffer();
		StringBuffer buffery2 = new StringBuffer();

		for (int j = 0; j < allDataItems02.size(); j++) {
			BaseItem item = (BaseItem) allDataItems02.get(j);
			if (selecteds02.contains(item.getName())) {
				buffery2.append("\n<option value=\"").append(item.getName()).append("\">").append(item.getTitle())
						.append(" [").append(item.getName()).append("]").append("</option>");
			} else {
				bufferx2.append("\n<option value=\"").append(item.getName()).append("\">").append(item.getTitle())
						.append(" [").append(item.getName()).append("]").append("</option>");
			}
		}

		request.setAttribute("bufferx2", bufferx2.toString());
		request.setAttribute("buffery2", buffery2.toString());

		request.setAttribute("allDataItems02", allDataItems02);
		request.setAttribute("selectedDataItems02", selectedDataItems02);

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

	public byte[] execute(String url, String filename, byte[] data) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		InputStream inputStream = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("file", data, ContentType.MULTIPART_FORM_DATA, filename);// 文件流

			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			CloseableHttpResponse response = httpClient.execute(httpPost);// 执行提交
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				inputStream = responseEntity.getContent();
				return FileUtils.getBytes(inputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inputStream);
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@ResponseBody
	@RequestMapping("/exportXls")
	public void exportXls(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = getParameterMap(request);
		params.put("login_user", loginContext.getUser());
		params.put("login_userid", loginContext.getActorId());
		logger.debug("request params:" + params);
		String expId = RequestUtils.getString(request, "expId");
		String toPDF = RequestUtils.getString(request, "toPDF");
		String outputFormat = request.getParameter("outputFormat");
		int precision = RequestUtils.getInt(request, "precision", 2);
		InputStream is = null;
		PrintWriter writer = null;
		ByteArrayInputStream bais = null;
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
						if (data != null) {
							String filename = exportApp.getExportFileExpr();
							params.put("yyyyMMdd", DateUtils.getDateTime("yyyyMMdd", new Date()));
							params.put("yyyyMMddHHmm", DateUtils.getDateTime("yyyyMMddHHmm", new Date()));
							params.put("yyyyMMddHHmmss", DateUtils.getDateTime("yyyyMMddHHmmss", new Date()));

							filename = ExpressionTools.evaluate(filename, params);
							if (filename == null) {
								filename = exportApp.getTitle();
							}

							if (StringUtils.equals(toPDF, "Y")) {
								String url = SystemConfig.getString("pdf_convert_url");
								if (StringUtils.isNotEmpty(url)) {
									if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
										byte[] output = this.execute(url, filename + ".xlsx", data);
										if (output != null) {
											ResponseUtils.download(request, response, output, filename + ".pdf");
										}
									} else {
										byte[] output = this.execute(url, filename + ".xls", data);
										if (output != null) {
											ResponseUtils.download(request, response, output, filename + ".pdf");
										}
									}
								}
							} else {
								if (StringUtils.equals(outputFormat, "html")) {
									response.setContentType("text/html; charset=UTF-8");
									bais = new ByteArrayInputStream(data);
									is = new BufferedInputStream(bais);
									String content = new String(ExcelToHtml.toHtml(is, precision), "UTF-8");
									writer = response.getWriter();
									writer.write(content);
									writer.flush();
								} else {
									if (params.get("_zip_") != null) {
										ResponseUtils.download(request, response, data, filename + ".zip");
									} else {
										if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
											ResponseUtils.download(request, response, data, filename + ".xlsx");
										} else {
											ResponseUtils.download(request, response, data, filename + ".xls");
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(bais);
		}
	}

	@ResponseBody
	@RequestMapping("/exportZip")
	public void exportZip(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = getParameterMap(request);
		params.put("login_user", loginContext.getUser());
		params.put("login_userid", loginContext.getActorId());
		logger.debug("request params:" + params);
		String expIdx = RequestUtils.getString(request, "expIds");
		String toPDF = RequestUtils.getString(request, "toPDF");
		Map<String, byte[]> zipMap = new HashMap<String, byte[]>();
		List<String> expIds = StringTools.split(expIdx);
		InputStream is = null;
		ByteArrayInputStream bais = null;
		try {
			for (String expId : expIds) {
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

						if (!hasPerm) {
							continue;
						}

						Map<String, Object> parameter = new HashMap<String, Object>();
						parameter.putAll(params);

						ParameterFactory.getInstance().processAll(expId, parameter);
						ExportHandler handler = new JxlsExportHandler();
						byte[] data = handler.export(loginContext, expId, parameter);
						if (data != null) {
							String filename = exportApp.getExportFileExpr();
							parameter.put("yyyyMMdd", DateUtils.getDateTime("yyyyMMdd", new Date()));
							parameter.put("yyyyMMddHHmm", DateUtils.getDateTime("yyyyMMddHHmm", new Date()));
							parameter.put("yyyyMMddHHmmss", DateUtils.getDateTime("yyyyMMddHHmmss", new Date()));

							filename = ExpressionTools.evaluate(filename, parameter);
							if (filename == null) {
								filename = exportApp.getTitle();
							}

							if (StringUtils.equals(toPDF, "Y")) {
								String url = SystemConfig.getString("pdf_convert_url");
								if (StringUtils.isNotEmpty(url)) {
									if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
										byte[] output = this.execute(url, filename + ".xlsx", data);
										if (output != null) {
											zipMap.put(filename + ".pdf", output);
										}
									} else {
										byte[] output = this.execute(url, filename + ".xls", data);
										if (output != null) {
											zipMap.put(filename + ".pdf", output);
										}
									}
								}
							} else {
								if (parameter.get("_zip_") != null) {
									zipMap.put(filename + ".zip", data);
								} else {
									if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
										zipMap.put(filename + ".xlsx", data);
									} else {
										zipMap.put(filename + ".xls", data);
									}
								}
							}
						}
					}
				}
			}

			byte[] data = ZipUtils.toZipBytes(zipMap);
			ResponseUtils.download(request, response, data, DateUtils.getNowYearMonthDayHHmmss() + ".zip");

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		} finally {
			zipMap.clear();
			zipMap = null;
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(bais);
		}
	}

	@ResponseBody
	@RequestMapping("/exportZip2")
	public void exportZip2(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = getParameterMap(request);
		params.put("login_user", loginContext.getUser());
		params.put("login_userid", loginContext.getActorId());
		logger.debug("request params:" + params);
		String expIdx = RequestUtils.getString(request, "expIds");
		String toPDF = RequestUtils.getString(request, "toPDF");
		Map<String, byte[]> zipMap = new HashMap<String, byte[]>();
		List<String> expIds = StringTools.split(expIdx);
		ForkJoinPool pool = ForkJoinPool.commonPool();
		logger.info("准备执行并行任务...");
		try {
			for (String expId : expIds) {
				ExportTask task = new ExportTask(loginContext, expId, params, toPDF);
				Future<DataFile> result = pool.submit(task);
				if (result != null && result.get() != null) {
					DataFile dataFile = result.get();
					zipMap.put(dataFile.getFilename(), dataFile.getData());
				}
			}
			// 线程阻塞，等待所有任务完成
			try {
				pool.awaitTermination(500, TimeUnit.MILLISECONDS);
			} catch (InterruptedException ex) {
			}
			byte[] data = ZipUtils.toZipBytes(zipMap);
			ResponseUtils.download(request, response, data, DateUtils.getNowYearMonthDayHHmmss() + ".zip");
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		} finally {
			zipMap.clear();
			zipMap = null;
			pool.shutdown();
			logger.info("并行任务已经结束。");
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
			exportApp.setExcelProcessChains(request.getParameter("excelProcessChains"));
			exportApp.setPageHeight(RequestUtils.getInt(request, "pageHeight"));
			exportApp.setPageNumPerSheet(RequestUtils.getInt(request, "pageNumPerSheet"));
			exportApp.setPageVarName(request.getParameter("pageVarName"));
			exportApp.setHistoryFlag(request.getParameter("historyFlag"));
			exportApp.setMulitiFlag(request.getParameter("mulitiFlag"));
			exportApp.setShedulerFlag(request.getParameter("shedulerFlag"));
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
