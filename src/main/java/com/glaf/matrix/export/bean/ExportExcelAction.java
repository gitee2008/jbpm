package com.glaf.matrix.export.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.Database;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.jdbc.QueryConnectionFactory;
import com.glaf.core.security.LoginContext;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.IOUtils;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.jxls.ext.JxlsBuilder;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportFileHistory;
import com.glaf.matrix.export.handler.WorkbookFactory;
import com.glaf.matrix.export.sql.EntityHelper;
import com.glaf.matrix.export.sql.JdbcHelper;
import com.glaf.matrix.export.sql.MyBatisHelper;
import com.glaf.matrix.export.sql.QueryHelper;
import com.glaf.template.Template;

public class ExportExcelAction extends RecursiveAction {
	protected static final Log logger = LogFactory.getLog(ExportExcelAction.class);

	private static final long serialVersionUID = 1L;

	protected LoginContext loginContext;

	protected ExportApp exportApp;

	protected Map<String, Object> params;

	protected Template tpl;

	protected String fileExt;

	protected String jobNo;

	protected int sortNo;

	protected List<Paging> pagingList;

	protected List<ExportFileHistory> historyList;

	public ExportExcelAction(LoginContext loginContext, ExportApp exportApp, Map<String, Object> params, Template tpl,
			String fileExt, String jobNo, int sortNo, List<ExportFileHistory> historyList) {
		this.loginContext = loginContext;
		this.exportApp = exportApp;
		this.params = params;
		this.tpl = tpl;
		this.jobNo = jobNo;
		this.sortNo = sortNo;
		this.fileExt = fileExt;
		this.historyList = historyList;
	}

	public ExportExcelAction(LoginContext loginContext, ExportApp exportApp, Map<String, Object> params, Template tpl,
			String fileExt, String jobNo, int sortNo, List<Paging> pagingList, List<ExportFileHistory> historyList) {
		this.loginContext = loginContext;
		this.exportApp = exportApp;
		this.params = params;
		this.tpl = tpl;
		this.jobNo = jobNo;
		this.sortNo = sortNo;
		this.fileExt = fileExt;
		this.pagingList = pagingList;
		this.historyList = historyList;
	}

	@Override
	protected void compute() {
		if (pagingList != null && !pagingList.isEmpty()) {
			int retry = 0;
			boolean success = false;
			while (retry < 3 && !success) {
				try {
					retry++;
					ExportFileHistory his = this.execute();
					if (his != null) {
						historyList.add(his);
						success = true;
						retry = 3;
						break;
					}
				} catch (Exception ex) {
					// logger.error(ex);
					try {
						TimeUnit.MILLISECONDS.sleep(200 + new Random().nextInt(100));
					} catch (InterruptedException e) {
					}
				}
			}
		} else {
			this.split();
		}
	}

	@SuppressWarnings("unchecked")
	protected void split() {
		List<Paging> pagingList = (List<Paging>) params.get(exportApp.getPageVarName() + "_paging");
		if (pagingList != null && pagingList.size() > 0) {
			int sortNoX = 0;
			int pSizeX = pagingList.size();
			List<Paging> newPagingList = new ArrayList<Paging>();
			List<List<Paging>> allPList = new ArrayList<List<Paging>>();
			for (int i = 0; i < pSizeX; i++) {
				newPagingList.add(pagingList.get(i));
				if (i > 0 && i % exportApp.getPageNumPerSheet() == 0) {
					List<Paging> newPagingList2 = new ArrayList<Paging>();
					int size = newPagingList.size();
					for (int k = 0; k < size; k++) {
						newPagingList2.add(newPagingList.get(k));
					}
					allPList.add(newPagingList2);
					newPagingList.clear();
				}
			}

			int size = allPList.size();
			List<ExportExcelAction> tasks = new ArrayList<ExportExcelAction>();
			for (int i = 0; i < size; i++) {
				sortNoX++;
				List<Paging> newPagingList2 = allPList.get(i);
				ExportExcelAction action = new ExportExcelAction(loginContext, exportApp, params, tpl, fileExt, jobNo,
						sortNoX, newPagingList2, historyList);
				tasks.add(action);
			}

			if (newPagingList.size() > 0) {
				sortNoX++;
				ExportExcelAction action = new ExportExcelAction(loginContext, exportApp, params, tpl, fileExt, jobNo,
						sortNoX, newPagingList, historyList);
				tasks.add(action);
			}

			if (!tasks.isEmpty()) {
				int taskSile = tasks.size();

				invokeAll(tasks);

				logger.debug("待生成文件份数:" + taskSile);
				int retry = 0;
				int limit = 20 * taskSile;
				boolean success = false;
				while (retry < limit && !success) {
					try {
						retry++;
						if (historyList.size() == taskSile) {
							success = true;
							break;
						}
						logger.debug("任务等待中,已经完成:" + historyList.size());
						TimeUnit.MILLISECONDS.sleep(500 + new Random().nextInt(50));
					} catch (Exception ex) {
					}
				}
			}
		}
	}

	protected ExportFileHistory execute() {
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.putAll(params);
		parameter.put(exportApp.getPageVarName() + "_pagex", pagingList);
		parameter.put(exportApp.getPageVarName() + "_paging", pagingList);
		ZipSecureFile.setMinInflateRatio(-1.0d);// 延迟解析比率

		long ts = 0;
		byte[] data = null;
		Workbook wb = null;
		java.sql.Connection conn = null;
		ByteArrayInputStream bais = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		BufferedOutputStream bos = null;
		try {
			bais = new ByteArrayInputStream(tpl.getData());
			bis = new BufferedInputStream(bais);
			baos = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(baos);

			ts = System.currentTimeMillis();
			if (StringUtils.equals(exportApp.getEnableSQLFlag(), "Y")) {
				Database database = null;
				if (exportApp.getSrcDatabaseId() > 0) {
					IDatabaseService databaseService = ContextFactory.getBean("databaseService");
					database = databaseService.getDatabaseById(exportApp.getSrcDatabaseId());
					if (database != null) {
						conn = DBConnectionFactory.getConnection(database.getName());
					}
				} else {
					conn = DBConnectionFactory.getConnection();
				}
				if (conn != null) {
					QueryConnectionFactory.getInstance().register(ts, conn);
					JdbcHelper jdbcHelper = new JdbcHelper(conn, parameter);
					QueryHelper queryHelper = new QueryHelper(conn, parameter);
					MyBatisHelper myBatisHelper = new MyBatisHelper(conn, parameter);
					EntityHelper entityHelper = new EntityHelper(database, parameter);

					parameter.put("jdbc", jdbcHelper);
					parameter.put("entity", entityHelper);
					parameter.put("dbutils", queryHelper);
					parameter.put("mybatis", myBatisHelper);
				}
			}

			JxlsBuilder jxlsBuilder = JxlsBuilder.getBuilder(bis).out(bos).putAll(parameter);
			jxlsBuilder.putVar("_ignoreImageMiss", Boolean.valueOf(true));
			jxlsBuilder.build();

			if (StringUtils.equals(exportApp.getEnableSQLFlag(), "Y")) {
				if (conn != null) {
					QueryConnectionFactory.getInstance().unregister(ts, conn);
					JdbcUtils.close(conn);
					conn = null;
				}
			}

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

			String toPDF = ParamUtils.getString(params, "toPDF");
			if (StringUtils.equals(toPDF, "Y") && StringUtils.equals(exportApp.getGenerateFlag(), "ONE")) {
				PDFConverterBean bean = new PDFConverterBean();
				byte[] pdfData = bean.convert(exportApp, data, fileExt);
				his.setPdfData(pdfData);
			}

			return his;
		} catch (Exception ex) {
			// //ex.printStackTrace();
			logger.error("export error", ex);
			throw new RuntimeException(ex);
		} finally {
			if (conn != null) {
				QueryConnectionFactory.getInstance().unregister(ts, conn);
				JdbcUtils.close(conn);
			}
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(baos);
		}
	}

}
