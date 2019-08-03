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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.glaf.core.base.DataFile;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.el.ExpressionTools;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.StringTools;
import com.glaf.matrix.data.domain.DataFileEntity;
import com.glaf.matrix.export.domain.ExportApp;
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
				JxlsReportExportBean exportBean = new JxlsReportExportBean();
				DataFile myDataFile = exportBean.export(exportApp, loginContext,  parameter);
				if (myDataFile != null) {
					String filename = exportApp.getExportFileExpr();
					parameter.put("yyyyMMdd", DateUtils.getDateTime("yyyyMMdd", new Date()));
					parameter.put("yyyyMMddHHmm", DateUtils.getDateTime("yyyyMMddHHmm", new Date()));
					parameter.put("yyyyMMddHHmmss", DateUtils.getDateTime("yyyyMMddHHmmss", new Date()));

					filename = ExpressionTools.evaluate(filename, parameter);
					if (filename == null) {
						filename = exportApp.getTitle();
					}

					String fileExt = "xls";
					if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
						fileExt = "xlsx";
					}

					DataFile dataFile = new DataFileEntity();

					if (StringUtils.equals(toPDF, "Y")) {
						PDFConverterBean bean = new PDFConverterBean();
						byte[] output = bean.convert(exportApp, myDataFile.getData(), fileExt);
						if (output != null) {
							dataFile.setFilename(filename + ".pdf");
							dataFile.setData(output);
						}
					} else {
						if (parameter.get("_zip_") != null) {
							dataFile.setFilename(filename + ".zip");
							dataFile.setData(myDataFile.getData());
						} else {
							if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
								dataFile.setFilename(filename + ".xlsx");
								dataFile.setData(myDataFile.getData());
							} else {
								dataFile.setFilename(filename + ".xls");
								dataFile.setData(myDataFile.getData());
							}
						}
					}
					return dataFile;
				}
			}
		}

		return null;
	}

}
