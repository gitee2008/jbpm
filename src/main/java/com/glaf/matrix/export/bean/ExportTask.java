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

package com.glaf.matrix.export.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

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

import com.glaf.core.base.DataFile;
import com.glaf.core.config.SystemConfig;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.el.ExpressionTools;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.IOUtils;
import com.glaf.core.util.StringTools;

import com.glaf.matrix.data.domain.DataFileEntity;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.handler.ExportHandler;
import com.glaf.matrix.export.handler.JxlsExportHandler;
import com.glaf.matrix.export.service.ExportAppService;
import com.glaf.matrix.parameter.handler.ParameterFactory;
import com.glaf.template.Template;
import com.glaf.template.service.ITemplateService;

public class ExportTask extends RecursiveTask<DataFile> {
	protected static final Log logger = LogFactory.getLog(ExportTask.class);

	private static final long serialVersionUID = 1L;

	protected LoginContext loginContext;

	protected String expId;

	protected Map<String, Object> params;

	protected String toPDF;

	public ExportTask(LoginContext loginContext, String expId, Map<String, Object> params, String toPDF) {
		this.loginContext = loginContext;
		this.expId = expId;
		this.params = params;
		this.toPDF = toPDF;
	}

	@Override
	protected DataFile compute() {
		ITemplateService templateService = ContextFactory.getBean("templateService");
		ExportAppService exportAppService = ContextFactory.getBean("com.glaf.matrix.export.service.exportAppService");
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
					return null;
				}

				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.putAll(params);

				ParameterFactory.getInstance().processAll(exportApp.getId(), parameter);
				ExportHandler handler = new JxlsExportHandler();
				byte[] data = handler.export(loginContext, exportApp.getId(), parameter);
				if (data != null) {
					String filename = exportApp.getExportFileExpr();
					parameter.put("yyyyMMdd", DateUtils.getDateTime("yyyyMMdd", new Date()));
					parameter.put("yyyyMMddHHmm", DateUtils.getDateTime("yyyyMMddHHmm", new Date()));
					parameter.put("yyyyMMddHHmmss", DateUtils.getDateTime("yyyyMMddHHmmss", new Date()));

					filename = ExpressionTools.evaluate(filename, parameter);
					if (filename == null) {
						filename = exportApp.getTitle();
					}

					DataFile dataFile = new DataFileEntity();

					if (StringUtils.equals(toPDF, "Y")) {
						String url = SystemConfig.getString("pdf_convert_url");
						if (StringUtils.isNotEmpty(url)) {
							if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
								byte[] output = this.execute(url, filename + ".xlsx", data);
								if (output != null) {
									dataFile.setFilename(filename + ".pdf");
									dataFile.setData(output);

								}
							} else {
								byte[] output = this.execute(url, filename + ".xls", data);
								if (output != null) {
									dataFile.setFilename(filename + ".pdf");
									dataFile.setData(output);
								}
							}
						}
					} else {
						if (parameter.get("_zip_") != null) {
							dataFile.setFilename(filename + ".zip");
							dataFile.setData(data);
						} else {
							if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
								dataFile.setFilename(filename + ".xlsx");
								dataFile.setData(data);
							} else {
								dataFile.setFilename(filename + ".xls");
								dataFile.setData(data);
							}
						}
					}
					return dataFile;
				}
			}
		}

		return null;
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

}
