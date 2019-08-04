/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.glaf.matrix.export.web.springmvc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.base.BaseItem;
import com.glaf.core.base.DataFile;
import com.glaf.core.config.DatabaseConnectionConfig;
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
import com.glaf.core.util.Hex;
import com.glaf.core.util.IOUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;
import com.glaf.core.util.UUID32;
import com.glaf.core.util.ZipUtils;

import com.glaf.jxls.ext.JxlsBuilder;
import com.glaf.matrix.export.bean.ExportTask;
import com.glaf.matrix.export.bean.JxlsReportExportBean;
import com.glaf.matrix.export.bean.PDFConverterBean;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportFileHistory;
import com.glaf.matrix.export.handler.WorkbookFactory;
import com.glaf.matrix.export.query.ExportAppQuery;
import com.glaf.matrix.export.service.ExportAppService;
import com.glaf.matrix.export.service.ExportFileHistoryService;
import com.glaf.matrix.export.service.ExportHistoryService;
import com.glaf.matrix.export.util.Constants;
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

	static {
		ByteArrayInputStream bais = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		BufferedOutputStream bos = null;
		try {
			Resource resource = new ClassPathResource("/com/glaf/matrix/export/template/template.xls");
			if (resource.exists() && resource.getInputStream() != null) {
				bais = new ByteArrayInputStream(FileUtils.getBytes(resource.getInputStream()));
				bis = new BufferedInputStream(bais);
				baos = new ByteArrayOutputStream();
				bos = new BufferedOutputStream(baos);

				Map<String, Object> parameter = new HashMap<String, Object>();
				JxlsBuilder jxlsBuilder = JxlsBuilder.getBuilder(bis).out(bos).putAll(parameter);
				jxlsBuilder.putVar("_ignoreImageMiss", Boolean.valueOf(true));
				jxlsBuilder.build();

				logger.info("报表处理器加载成功！");
			}
		} catch (Exception ex) {
			logger.error("报表处理器加载失败！", ex);
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(baos);
		}
	}

	protected static ConcurrentMap<String, String> paramsMap = new ConcurrentHashMap<String, String>();

	protected IDatabaseService databaseService;

	protected ExportAppService exportAppService;

	protected ExportHistoryService exportHistoryService;

	protected ExportFileHistoryService exportFileHistoryService;

	protected ITemplateService templateService;

	public ExportAppController() {

	}

	@ResponseBody
	@RequestMapping("/delete")
	public byte[] delete(HttpServletRequest request) {
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

	@ResponseBody
	@RequestMapping("/exportXls")
	public void exportXls(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = getParameterMap(request);
		String innerParams = request.getParameter(Constants.INNER_PARAMS);
		if (StringUtils.isNotEmpty(innerParams)) {
			String json = new String(Hex.hexToBytes(innerParams), "UTF-8");
			JSONObject jsonObject = JSON.parseObject(json);
			if (StringUtils.isNotEmpty(json)) {
				Set<Entry<String, Object>> entrySet = jsonObject.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (!params.containsKey(key)) {
						params.put(key, value);
					}
				}
			}
		}
		logger.debug("--------------------------exportXls------------------");
		logger.debug("params:" + params);
		params.put("login_user", loginContext.getUser());
		params.put("login_userid", loginContext.getActorId());
		logger.debug("request params:" + params);
		String expId = RequestUtils.getString(request, "expId");
		String toPDF = RequestUtils.getString(request, "toPDF");
		String filename = RequestUtils.getString(request, "filename");
		String viewMode = RequestUtils.getString(request, "viewMode");
		String fileHisId = RequestUtils.getString(request, "fileHisId");
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
						JxlsReportExportBean exportBean = new JxlsReportExportBean();
						DataFile dataFile = exportBean.export(exportApp, loginContext, params);
						if (dataFile != null) {
							/**
							 * 判断是否调试输出参数
							 */
							if (this.outputParameter(request, response, dataFile)) {
								return;
							}
							if (StringUtils.isEmpty(filename)) {
								filename = exportApp.getExportFileExpr();
							}
							params.put("yyyyMMdd", DateUtils.getDateTime("yyyyMMdd", new Date()));
							params.put("yyyyMMddHHmm", DateUtils.getDateTime("yyyyMMddHHmm", new Date()));
							params.put("yyyyMMddHHmmss", DateUtils.getDateTime("yyyyMMddHHmmss", new Date()));

							filename = ExpressionTools.evaluate(filename, params);
							if (filename == null) {
								filename = exportApp.getTitle();
							}

							if (StringUtils.equals(toPDF, "Y")) {
								String fileExt = null;
								if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
									fileExt = "xlsx";
								} else {
									fileExt = "xls";
								}
								if (StringUtils.endsWithIgnoreCase(dataFile.getFilename(), ".pdf")) {
									if (StringUtils.isNotEmpty(fileHisId)) {
										this.saveFileHistory(fileHisId, expId, loginContext.getActorId(), dataFile);
									}
									if (StringUtils.equals(viewMode, "online")) {
										ResponseUtils.output(request, response, dataFile.getData(), filename + ".pdf",
												"application/pdf");
									} else {
										ResponseUtils.download(request, response, dataFile.getData(),
												filename + ".pdf");
									}
								} else {
									PDFConverterBean bean = new PDFConverterBean();
									byte[] output = null;
									if (StringUtils.endsWithIgnoreCase(dataFile.getFilename(), fileExt)) {
										output = bean.convert(exportApp, dataFile.getData(), fileExt);
									}
									if (output != null) {
										if (StringUtils.isNotEmpty(fileHisId)) {
											this.saveFileHistory(fileHisId, expId, loginContext.getActorId(), dataFile);
										}
										if (StringUtils.equals(viewMode, "online")) {
											ResponseUtils.output(request, response, output, filename + ".pdf",
													"application/pdf");
										} else {
											ResponseUtils.download(request, response, output, filename + ".pdf");
										}
									}
								}
							} else {
								if (StringUtils.equals(outputFormat, "html")) {
									response.setContentType("text/html; charset=UTF-8");
									bais = new ByteArrayInputStream(dataFile.getData());
									is = new BufferedInputStream(bais);
									String content = new String(ExcelToHtml.toHtml(is, precision), "UTF-8");
									writer = response.getWriter();
									writer.write(content);
									writer.flush();
								} else {
									if (StringUtils.isNotEmpty(fileHisId)) {
										this.saveFileHistory(fileHisId, expId, loginContext.getActorId(), dataFile);
									}
									if (params.get("_pdf_") != null) {
										if (StringUtils.equals(viewMode, "online")) {
											ResponseUtils.output(request, response, dataFile.getData(),
													filename + ".pdf", "application/pdf");
										} else {
											ResponseUtils.download(request, response, dataFile.getData(),
													filename + ".pdf");
										}
									} else if (params.get("_zip_") != null) {
										ResponseUtils.download(request, response, dataFile.getData(),
												dataFile.getFilename());
									} else {
										if (StringUtils.endsWithIgnoreCase(dataFile.getFilename(), ".xlsx")) {
											ResponseUtils.download(request, response, dataFile.getData(),
													filename + ".xlsx");
										} else {
											ResponseUtils.download(request, response, dataFile.getData(),
													filename + ".xls");
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			// //ex.printStackTrace();
			logger.error(ex);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(bais);
		}
	}

	@ResponseBody
	@RequestMapping("/exportXlsV2/{key}")
	public void exportXlsV2(HttpServletRequest request, HttpServletResponse response, @PathVariable("key") String key)
			throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = getParameterMap(request);
		if (StringUtils.isNotEmpty(key)) {
			String json = new String(Hex.hex2byte(key), "UTF-8");
			if (json != null) {
				JSONObject jsonObject = JSON.parseObject(json);
				if (StringUtils.isNotEmpty(json)) {
					Set<Entry<String, Object>> entrySet = jsonObject.entrySet();
					for (Entry<String, Object> entry : entrySet) {
						String keyx = entry.getKey();
						Object value = entry.getValue();
						if (!params.containsKey(keyx)) {
							params.put(keyx, value);
						}
					}
				}
			}
		}
		logger.debug("--------------------------exportXlsV2------------------");
		logger.debug("params:" + params);
		params.put("login_user", loginContext.getUser());
		params.put("login_userid", loginContext.getActorId());
		logger.debug("request params:" + params);
		String expId = ParamUtils.getString(params, "expId");
		String toPDF = ParamUtils.getString(params, "toPDF");
		String filename = ParamUtils.getString(params, "filename");
		String viewMode = ParamUtils.getString(params, "viewMode");
		String fileHisId = ParamUtils.getString(params, "fileHisId");
		String outputFormat = ParamUtils.getString(params, "outputFormat");
		int precision = ParamUtils.getInt(params, "precision");

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
						JxlsReportExportBean exportBean = new JxlsReportExportBean();
						DataFile dataFile = exportBean.export(exportApp, loginContext, params);
						if (dataFile != null) {
							/**
							 * 判断是否调试输出参数
							 */
							if (this.outputParameter(request, response, dataFile)) {
								return;
							}
							if (StringUtils.isEmpty(filename)) {
								filename = exportApp.getExportFileExpr();
							}
							params.put("yyyyMMdd", DateUtils.getDateTime("yyyyMMdd", new Date()));
							params.put("yyyyMMddHHmm", DateUtils.getDateTime("yyyyMMddHHmm", new Date()));
							params.put("yyyyMMddHHmmss", DateUtils.getDateTime("yyyyMMddHHmmss", new Date()));

							filename = ExpressionTools.evaluate(filename, params);
							if (filename == null) {
								filename = exportApp.getTitle();
							}

							if (StringUtils.equals(toPDF, "Y")) {
								String fileExt = null;
								if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
									fileExt = "xlsx";
								} else {
									fileExt = "xls";
								}
								if (StringUtils.endsWithIgnoreCase(dataFile.getFilename(), ".pdf")) {
									if (StringUtils.isNotEmpty(fileHisId)) {
										this.saveFileHistory(fileHisId, expId, loginContext.getActorId(), dataFile);
									}
									if (StringUtils.equals(viewMode, "online")) {
										ResponseUtils.output(request, response, dataFile.getData(), filename + ".pdf",
												"application/pdf");
									} else {
										ResponseUtils.download(request, response, dataFile.getData(),
												filename + ".pdf");
									}
								} else {
									PDFConverterBean bean = new PDFConverterBean();
									byte[] output = null;
									if (StringUtils.endsWithIgnoreCase(dataFile.getFilename(), fileExt)) {
										output = bean.convert(exportApp, dataFile.getData(), fileExt);
									}
									if (output != null) {
										if (StringUtils.isNotEmpty(fileHisId)) {
											this.saveFileHistory(fileHisId, expId, loginContext.getActorId(), dataFile);
										}
										if (StringUtils.equals(viewMode, "online")) {
											ResponseUtils.output(request, response, output, filename + ".pdf",
													"application/pdf");
										} else {
											ResponseUtils.download(request, response, output, filename + ".pdf");
										}
									}
								}
							} else {
								if (StringUtils.equals(outputFormat, "html")) {
									response.setContentType("text/html; charset=UTF-8");
									bais = new ByteArrayInputStream(dataFile.getData());
									is = new BufferedInputStream(bais);
									String content = new String(ExcelToHtml.toHtml(is, precision), "UTF-8");
									writer = response.getWriter();
									writer.write(content);
									writer.flush();
								} else {
									if (StringUtils.isNotEmpty(fileHisId)) {
										this.saveFileHistory(fileHisId, expId, loginContext.getActorId(), dataFile);
									}
									if (params.get("_pdf_") != null) {
										if (StringUtils.equals(viewMode, "online")) {
											ResponseUtils.output(request, response, dataFile.getData(),
													filename + ".pdf", "application/pdf");
										} else {
											ResponseUtils.download(request, response, dataFile.getData(),
													filename + ".pdf");
										}
									} else if (params.get("_zip_") != null) {
										ResponseUtils.download(request, response, dataFile.getData(),
												dataFile.getFilename());
									} else {
										if (StringUtils.endsWithIgnoreCase(dataFile.getFilename(), ".xlsx")) {
											ResponseUtils.download(request, response, dataFile.getData(),
													filename + ".xlsx");
										} else {
											ResponseUtils.download(request, response, dataFile.getData(),
													filename + ".xls");
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			// //ex.printStackTrace();
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
		String innerParams = request.getParameter(Constants.INNER_PARAMS);
		if (StringUtils.isNotEmpty(innerParams)) {
			String json = new String(Hex.hexToBytes(innerParams), "UTF-8");
			JSONObject jsonObject = JSON.parseObject(json);
			if (StringUtils.isNotEmpty(json)) {
				Set<Entry<String, Object>> entrySet = jsonObject.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (!params.containsKey(key)) {
						params.put(key, value);
					}
				}
			}
		}
		params.put("login_user", loginContext.getUser());
		params.put("login_userid", loginContext.getActorId());
		logger.debug("request params:" + params);
		String expIdx = RequestUtils.getString(request, "expIds");
		String toPDF = RequestUtils.getString(request, "toPDF");
		String viewMode = RequestUtils.getString(request, "viewMode");
		String fileHisId = RequestUtils.getString(request, "fileHisId");
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
						JxlsReportExportBean exportBean = new JxlsReportExportBean();
						DataFile dataFile = exportBean.export(exportApp, loginContext, parameter);
						if (dataFile != null) {
							/**
							 * 判断是否调试输出参数
							 */
							if (this.outputParameter(request, response, dataFile)) {
								return;
							}
							String filename = exportApp.getExportFileExpr();
							parameter.put("yyyyMMdd", DateUtils.getDateTime("yyyyMMdd", new Date()));
							parameter.put("yyyyMMddHHmm", DateUtils.getDateTime("yyyyMMddHHmm", new Date()));
							parameter.put("yyyyMMddHHmmss", DateUtils.getDateTime("yyyyMMddHHmmss", new Date()));

							filename = ExpressionTools.evaluate(filename, parameter);
							if (filename == null) {
								filename = exportApp.getTitle();
							}

							if (StringUtils.equals(toPDF, "Y")) {
								String fileExt = null;
								if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
									fileExt = "xlsx";
								} else {
									fileExt = "xls";
								}
								PDFConverterBean bean = new PDFConverterBean();
								byte[] output = bean.convert(exportApp, dataFile.getData(), fileExt);
								if (output != null) {
									if (StringUtils.isNotEmpty(fileHisId)) {
										this.saveFileHistory(fileHisId, expId, loginContext.getActorId(), dataFile);
									}
									if (StringUtils.equals(viewMode, "online")) {
										ResponseUtils.output(request, response, output, filename + ".pdf",
												"application/pdf");
									} else {
										ResponseUtils.download(request, response, output, filename + ".pdf");
									}
								}
							} else {
								if (StringUtils.isNotEmpty(fileHisId)) {
									this.saveFileHistory(fileHisId, expId, loginContext.getActorId(), dataFile);
								}
								if (parameter.get("_zip_") != null) {
									zipMap.put(dataFile.getFilename(), dataFile.getData());
								} else {
									if (StringUtils.endsWithIgnoreCase(dataFile.getFilename(), ".xlsx")) {
										zipMap.put(filename + ".xlsx", dataFile.getData());
									} else {
										zipMap.put(filename + ".xls", dataFile.getData());
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
			// //ex.printStackTrace();
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
		String innerParams = request.getParameter(Constants.INNER_PARAMS);
		if (StringUtils.isNotEmpty(innerParams)) {
			String json = new String(Hex.hexToBytes(innerParams), "UTF-8");
			JSONObject jsonObject = JSON.parseObject(json);
			if (StringUtils.isNotEmpty(json)) {
				Set<Entry<String, Object>> entrySet = jsonObject.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (!params.containsKey(key)) {
						params.put(key, value);
					}
				}
			}
		}
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
			// //ex.printStackTrace();
			logger.error(ex);
		} finally {
			zipMap.clear();
			zipMap = null;
			pool.shutdown();
			logger.info("并行任务已经结束。");
		}
	}

	@ResponseBody
	@RequestMapping("/getHistoryStatus")
	public byte[] getHistoryStatus(HttpServletRequest request) {
		JSONObject result = new JSONObject();
		String id = RequestUtils.getString(request, "fileHisId");
		ExportFileHistory his = exportFileHistoryService.getExportFileHistory(id);
		if (his != null) {
			result.put("code", 200);
		} else {
			result.put("code", 404);
		}
		return result.toJSONString().getBytes();
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

	public boolean outputParameter(HttpServletRequest request, HttpServletResponse response, DataFile dataFile)
			throws IOException {
		String isDebug = request.getParameter(Constants.DEBUG_PARAMS);
		if (StringUtils.equals(isDebug, "Y")) {
			if (dataFile.getParameter() != null) {
				Map<String, Object> initParams = RequestUtils.getParameterMap(request);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("_initParams_", initParams);
				jsonObject.putAll(dataFile.getParameter());
				PrintWriter writer = null;
				try {
					response.setContentType("application/json; charset=UTF-8");
					writer = response.getWriter();
					writer.write(jsonObject.toJSONString());
					writer.flush();
					return true;
				} catch (IOException ex) {
					throw ex;
				}
			}
		}
		return false;
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
			exportApp.setExportPDFTool(request.getParameter("exportPDFTool"));
			exportApp.setMergePDFFlag(request.getParameter("mergePDFFlag"));
			exportApp.setExternalColumnsFlag(request.getParameter("externalColumnsFlag"));
			exportApp.setExcelProcessChains(request.getParameter("excelProcessChains"));
			exportApp.setPageHeight(RequestUtils.getInt(request, "pageHeight"));
			exportApp.setPageNumPerSheet(RequestUtils.getInt(request, "pageNumPerSheet"));
			exportApp.setPageVarName(request.getParameter("pageVarName"));
			exportApp.setHistoryFlag(request.getParameter("historyFlag"));
			exportApp.setMulitiFlag(request.getParameter("mulitiFlag"));
			exportApp.setEnableSQLFlag(request.getParameter("enableSQLFlag"));
			exportApp.setSaveDataFlag(request.getParameter("saveDataFlag"));
			exportApp.setGenerateFlag(request.getParameter("generateFlag"));
			exportApp.setGenTime(RequestUtils.getInt(request, "genTime"));
			exportApp.setParameterDatasetId(request.getParameter("parameterDatasetId"));
			exportApp.setOutParameterColumns(request.getParameter("outParameterColumns"));
			exportApp.setParallelFlag(request.getParameter("parallelFlag"));
			exportApp.setShedulerFlag(request.getParameter("shedulerFlag"));
			exportApp.setInterval(RequestUtils.getInt(request, "interval"));
			exportApp.setSortNo(RequestUtils.getInt(request, "sortNo"));
			exportApp.setCreateBy(actorId);
			exportApp.setUpdateBy(actorId);

			this.exportAppService.save(exportApp);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			// //ex.printStackTrace();
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
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		String actorId = loginContext.getActorId();
		try {
			String expId = RequestUtils.getString(request, "expId");
			if (expId != null) {
				String nid = exportAppService.saveAs(expId, actorId, params);
				if (nid != null) {
					return ResponseUtils.responseJsonResult(true);
				}
			}
		} catch (Exception ex) {
			// //ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	protected void saveFileHistory(String fileHisId, String expId, String userId, DataFile dataFile) {
		ExportFileHistory his = new ExportFileHistory();
		his.setId(fileHisId);
		his.setCreateBy(userId);
		his.setExpId(expId);
		his.setFilename(dataFile.getFilename());
		his.setGenYmd(DateUtils.getNowYearMonthDay());
		his.setLastModified(System.currentTimeMillis());
		his.setTitle("");
		try {
			exportFileHistoryService.save(his);
		} catch (Exception ex) {
			// //ex.printStackTrace();
			logger.error(ex);
		}
	}

	@javax.annotation.Resource
	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.exportAppService")
	public void setExportAppService(ExportAppService exportAppService) {
		this.exportAppService = exportAppService;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.exportFileHistoryService")
	public void setExportFileHistoryService(ExportFileHistoryService exportFileHistoryService) {
		this.exportFileHistoryService = exportFileHistoryService;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.exportHistoryService")
	public void setExportHistoryService(ExportHistoryService exportHistoryService) {
		this.exportHistoryService = exportHistoryService;
	}

	@javax.annotation.Resource
	public void setTemplateService(ITemplateService templateService) {
		this.templateService = templateService;
	}

	@RequestMapping("/viewPDF")
	public void viewPDF(HttpServletRequest request, HttpServletResponse response) throws IOException {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(params);
		jsonObject.put("toPDF", "Y");
		jsonObject.put("viewMode", "down");
		jsonObject.put("uid", UUID32.generateShortUuid());
		String key = Hex.byte2hex(jsonObject.toJSONString().getBytes("UTF-8"));
		// paramsMap.put(key, jsonObject.toJSONString());
		StringBuilder buffer = new StringBuilder();
		buffer.append(request.getContextPath()).append("/mx/matrix/exportApp/exportXlsV2/").append(key);
		response.sendRedirect(request.getContextPath() + "/viewer/viewer.html?uid=" + UUID32.getUUID() + "&file="
				+ buffer.toString());
	}

	@RequestMapping("/viewPDF2")
	public ModelAndView viewPDF2(HttpServletRequest request, ModelMap modelMap) throws IOException {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(params);
		request.setAttribute(Constants.INNER_PARAMS, Hex.bytesToHex(jsonObject.toJSONString().getBytes("UTF-8")));
		String x_view = ViewProperties.getString("exportApp.viewPDF2");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/exportApp/viewPDF2", modelMap);
	}

}
