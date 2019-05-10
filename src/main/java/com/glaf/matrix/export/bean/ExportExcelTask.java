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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;

import com.glaf.core.security.LoginContext;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.IOUtils;

import com.glaf.jxls.ext.JxlsBuilder;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportFileHistory;
import com.glaf.matrix.export.handler.WorkbookFactory;
import com.glaf.template.Template;

public class ExportExcelTask extends RecursiveTask<ExportFileHistory> {
	protected static final Log logger = LogFactory.getLog(ExportExcelTask.class);

	private static final long serialVersionUID = 1L;

	protected LoginContext loginContext;

	protected ExportApp exportApp;

	protected List<Paging> pagingList;

	protected Map<String, Object> params;

	protected Template tpl;

	protected String fileExt;

	protected String jobNo;

	protected int sortNo;

	public ExportExcelTask(LoginContext loginContext, ExportApp exportApp, List<Paging> pagingList,
			Map<String, Object> params, Template tpl, String fileExt, String jobNo, int sortNo) {
		this.loginContext = loginContext;
		this.exportApp = exportApp;
		this.pagingList = pagingList;
		this.params = params;
		this.tpl = tpl;
		this.jobNo = jobNo;
		this.sortNo = sortNo;
		this.fileExt = fileExt;
	}

	@Override
	protected ExportFileHistory compute() {
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.putAll(params);
		parameter.put(exportApp.getPageVarName() + "_pagex", pagingList);
		parameter.put(exportApp.getPageVarName() + "_paging", pagingList);
		ZipSecureFile.setMinInflateRatio(-1.0d);// 延迟解析比率
		byte[] data = null;
		Workbook wb = null;
		ByteArrayInputStream bais = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		BufferedOutputStream bos = null;
		try {
			bais = new ByteArrayInputStream(tpl.getData());
			bis = new BufferedInputStream(bais);
			baos = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(baos);

			JxlsBuilder jxlsBuilder = JxlsBuilder.getBuilder(bis).out(bos).putAll(parameter);
			jxlsBuilder.putVar("_ignoreImageMiss", Boolean.valueOf(true));
			jxlsBuilder.build();

			bos.flush();
			baos.flush();
			data = baos.toByteArray();

			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(baos);

			bais = new ByteArrayInputStream(data);
			bis = new BufferedInputStream(bais);

			wb = org.apache.poi.ss.usermodel.WorkbookFactory.create(bis);
			WorkbookFactory.process(wb, exportApp);
			baos = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(baos);
			wb.write(bos);
			bos.flush();
			baos.flush();
			data = baos.toByteArray();

			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(baos);

			ExportFileHistory his = new ExportFileHistory();
			his.setExpId(exportApp.getId());
			his.setDeploymentId(exportApp.getDeploymentId());
			his.setGenYmd(DateUtils.getNowYearMonthDay());
			his.setJobNo(jobNo);
			his.setSortNo(sortNo);
			his.setFilename(exportApp.getExportFileExpr() + jobNo + "_" + sortNo + "." + fileExt);
			his.setData(data);
			his.setLastModified(System.currentTimeMillis());
			his.setCreateBy(loginContext.getActorId());
			his.setCreateTime(new java.util.Date(his.getLastModified()));
			return his;
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error("export error", ex);
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(baos);
		}
		return null;
	}

}
