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

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;

import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;

import com.glaf.core.base.DataFile;
import com.glaf.core.config.SystemProperties;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.Database;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.jdbc.QueryConnectionFactory;
import com.glaf.core.security.LoginContext;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.ZipUtils;

import com.glaf.jxls.ext.JxlsBuilder;
import com.glaf.jxls.ext.JxlsImage;
import com.glaf.jxls.ext.JxlsUtil;
import com.glaf.matrix.data.domain.DataFileEntity;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportFileHistory;
import com.glaf.matrix.export.domain.ExportItem;
import com.glaf.matrix.export.domain.ExportTemplateVar;
import com.glaf.matrix.export.factory.ExportChartFactory;
import com.glaf.matrix.export.handler.WorkbookFactory;
import com.glaf.matrix.export.preprocessor.DataPreprocessorFactory;
import com.glaf.matrix.export.preprocessor.DataXFactory;
import com.glaf.matrix.export.sql.EntityHelper;
import com.glaf.matrix.export.sql.JdbcHelper;
import com.glaf.matrix.export.sql.MyBatisHelper;
import com.glaf.matrix.export.sql.QueryHelper;
import com.glaf.matrix.export.util.PDFUtils;
import com.glaf.matrix.util.ImageUtils;
import com.glaf.matrix.util.SysParams;
import com.glaf.template.Template;
import com.glaf.template.service.ITemplateService;
import com.glaf.template.util.TemplateUtils;

public class JxlsReportExportBean {

	protected static final Log logger = LogFactory.getLog(JxlsReportExportBean.class);

	@SuppressWarnings("unchecked")
	public DataFile export(ExportApp exportApp, LoginContext loginContext, Map<String, Object> params) {
		ITemplateService templateService = ContextFactory.getBean("templateService");

		String useExt = ParamUtils.getString(params, "useExt");
		String toPDF = ParamUtils.getString(params, "toPDF");
		String expId = exportApp.getId();

		long ts = 0;
		boolean hasPerm = true;
		Workbook wb = null;
		java.sql.Connection conn = null;
		ByteArrayInputStream bais = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		BufferedOutputStream bos = null;
		try {

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

			Template tpl = templateService.getTemplate(exportApp.getTemplateId());
			if (tpl == null || tpl.getData() == null) {
				return null;
			}

			String fileExt = "xls";
			if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
				fileExt = "xlsx";
			}

			// int pageNo = 0;
			SysParams.putInternalParams(params);
			params.put("_ignoreImageMiss", Boolean.valueOf(true));// 图片不存在跳过
			params.put("_exp_", exportApp);

			/**
			 * 处理截图
			 */
			ExportChartFactory.snapshot(exportApp, params);
			if (ParamUtils.getString(params, "_useExt_") != null) {
				useExt = "Y";
			}

			List<ExportFileHistory> historyList = new CopyOnWriteArrayList<ExportFileHistory>();
			String jobNo = ParamUtils.getString(params, "jobNo");
			// Collection<Map<String, Object>> pageList = new ArrayList<Map<String,
			// Object>>();
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			ExportAppBean bean = new ExportAppBean();
			exportApp = bean.execute(exportApp.getId(), params);
			for (ExportItem item : exportApp.getItems()) {

				/**
				 * 加载定制开发的类
				 */
				DataPreprocessorFactory.prepare(item, params);

				// pageNo = 0;
				dataList.clear();
				if (item.getDataList() != null && !item.getDataList().isEmpty()) {
					dataList.addAll(item.getDataList());
					if (StringUtils.equals(item.getResultFlag(), "O")) {
						params.put("obj_" + item.getName(), item.getDataList().iterator().next());
						params.put(item.getName() + "_one", item.getDataList().iterator().next());
					}
				}
				if (item.getJsonData() != null) {
					params.put(item.getName(), item.getJsonData());
				}

				params.put(item.getName(), item.getDataList());
				params.put(item.getName() + "_size", item.getDataList().size());

				int size = item.getDataList().size();
				if (size == 1) {
					Map<String, Object> rowMap = new HashMap<String, Object>();
					Iterator<Map<String, Object>> iterator = item.getDataList().iterator();
					while (iterator.hasNext()) {
						Map<String, Object> row = iterator.next();
						rowMap.putAll(row);
						break;
					}
					if (rowMap.get("_bytes_") != null) {
						logger.debug("取得图片数据。");
						Object object = rowMap.get("_bytes_");
						// logger.debug("class:" + object.getClass().getName());
						if (object instanceof InputStream) {
							InputStream input = (InputStream) object;
							byte[] bytes = FileUtils.getBytes(input);
							bais = new ByteArrayInputStream(bytes);
							bis = new BufferedInputStream(bais);
							if (item.getImageHeight() > 0 && item.getImageWidth() > 0) {
								BufferedImage img = ImageIO.read(bis);
								bytes = ImageUtils.zoomImage(img, item.getImageWidth(), item.getImageHeight(),
										item.getImageMergeTargetType());
							} else if (item.getImageScale() > 0 && item.getImageScale() < 1.0) {
								if (bytes.length > item.getImageScaleSize() * FileUtils.MB_SIZE) {
									BufferedImage img = ImageIO.read(bis);
									bytes = ImageUtils.zoomByScale(img, item.getImageScale(),
											item.getImageMergeTargetType());
								}
							}
							IOUtils.closeQuietly(bis);
							IOUtils.closeQuietly(bais);
							rowMap.put("_bytes_", bytes);

							if (StringUtils.isNotEmpty(item.getFileNameColumn())) {
								String filename = ParamUtils.getString(rowMap, item.getFileNameColumn().toLowerCase());
								if (StringUtils.isNotEmpty(filename)) {
									JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(bytes, filename);
									params.put(item.getName() + "_image", jxlsImage);
									useExt = "Y";
								}
							}
						} else if (object instanceof byte[]) {
							byte[] bytes = (byte[]) object;
							bais = new ByteArrayInputStream(bytes);
							bis = new BufferedInputStream(bais);
							if (item.getImageHeight() > 0 && item.getImageWidth() > 0) {
								BufferedImage img = ImageIO.read(bis);
								bytes = ImageUtils.zoomImage(img, item.getImageWidth(), item.getImageHeight(),
										item.getImageMergeTargetType());
								rowMap.put("_bytes_", bytes);
							} else if (item.getImageScale() > 0 && item.getImageScale() < 1.0) {
								if (bytes.length > item.getImageScaleSize() * FileUtils.MB_SIZE) {
									BufferedImage img = ImageIO.read(bis);
									bytes = ImageUtils.zoomByScale(img, item.getImageScale(),
											item.getImageMergeTargetType());
									rowMap.put("_bytes_", bytes);
								}
							}
							IOUtils.closeQuietly(bis);
							IOUtils.closeQuietly(bais);

							if (StringUtils.isNotEmpty(item.getFileNameColumn())) {
								String filename = ParamUtils.getString(rowMap, item.getFileNameColumn().toLowerCase());
								if (StringUtils.isNotEmpty(filename)) {
									JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(bytes, filename);
									params.put(item.getName() + "_image", jxlsImage);
									useExt = "Y";
								}
							}
						}
					} else {
						if (StringUtils.equals(item.getFileFlag(), "Y")
								&& StringUtils.isNotEmpty(item.getFilePathColumn())) {
							String filePath = ParamUtils.getString(rowMap, item.getFilePathColumn().toLowerCase());
							if (filePath != null) {
								String rootPath = item.getRootPath();
								if (StringUtils.startsWith(rootPath, "${ROOT_PATH}")) {
									rootPath = StringTools.replace(rootPath, "${ROOT_PATH}",
											SystemProperties.getAppPath());
								} else {
									rootPath = SystemProperties.getAppPath();
								}
								String imgPath = rootPath + "/" + filePath;
								JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(imgPath);
								params.put(item.getName() + "_image", jxlsImage);
								useExt = "Y";

								byte[] bytes = FileUtils.getBytes(imgPath);
								bais = new ByteArrayInputStream(bytes);
								bis = new BufferedInputStream(bais);
								if (item.getImageHeight() > 0 && item.getImageWidth() > 0) {
									BufferedImage img = ImageIO.read(bis);
									bytes = ImageUtils.zoomImage(img, item.getImageWidth(), item.getImageHeight(),
											item.getImageMergeTargetType());
									jxlsImage = JxlsUtil.me().getJxlsImage(bytes,
											item.getName() + "." + item.getImageMergeTargetType());
									params.put(item.getName() + "_image", jxlsImage);
								} else if (item.getImageScale() > 0 && item.getImageScale() < 1.0) {
									if (bytes.length > item.getImageScaleSize() * FileUtils.MB_SIZE) {
										BufferedImage img = ImageIO.read(bis);
										bytes = ImageUtils.zoomByScale(img, item.getImageScale(),
												item.getImageMergeTargetType());
										jxlsImage = JxlsUtil.me().getJxlsImage(bytes,
												item.getName() + "." + item.getImageMergeTargetType());
										params.put(item.getName() + "_image", jxlsImage);
									}
								}
							}
						}
					}
					params.put("obj_" + item.getName(), rowMap);
					params.put("map_" + item.getName(), rowMap);

					if (StringUtils.equals(item.getContextVarFlag(), "Y")) {
						Set<Entry<String, Object>> entrySet = rowMap.entrySet();
						for (Entry<String, Object> entry : entrySet) {
							String key = entry.getKey();
							Object value = entry.getValue();
							if (params.get(key) == null) {
								params.put(key, value);
							}
						}
					}
				}

				List<BufferedImage> imageList = new ArrayList<BufferedImage>();

				int pageSize = item.getPageSize();
				if (pageSize <= 0) {
					pageSize = 20;
				}
				for (int i = 0; i < size; i++) {
					Map<String, Object> rowMap = dataList.get(i);
					if (rowMap.get("_bytes_") != null) {
						logger.debug("取得图片数据。");
						Object object = rowMap.get("_bytes_");
						// logger.debug("class:" + object.getClass().getName());
						if (object instanceof InputStream) {
							InputStream input = (InputStream) object;
							byte[] bytes = FileUtils.getBytes(input);
							rowMap.put("_bytes_", bytes);

							if (StringUtils.isNotEmpty(item.getFileNameColumn())) {
								String filename = ParamUtils.getString(rowMap, item.getFileNameColumn().toLowerCase());
								if (StringUtils.isNotEmpty(filename)) {
									JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(bytes, filename);
									rowMap.put(item.getName() + "_image", jxlsImage);
									useExt = "Y";
								}
							}
							IOUtils.closeQuietly(input);

							bais = new ByteArrayInputStream(bytes);
							bis = new BufferedInputStream(bais);
							if (item.getImageHeight() > 0 && item.getImageWidth() > 0) {
								BufferedImage img = ImageIO.read(bis);
								bytes = ImageUtils.zoomImage(img, item.getImageWidth(), item.getImageHeight(),
										item.getImageMergeTargetType());
							} else if (item.getImageScale() > 0 && item.getImageScale() < 1.0) {
								if (bytes.length > item.getImageScaleSize() * FileUtils.MB_SIZE) {
									BufferedImage img = ImageIO.read(bis);
									bytes = ImageUtils.zoomByScale(img, item.getImageScale(),
											item.getImageMergeTargetType());
								}
							}
							IOUtils.closeQuietly(bis);
							IOUtils.closeQuietly(bais);

							if (StringUtils.equals(item.getImageMergeFlag(), "Y")) {
								bais = new ByteArrayInputStream(bytes);
								bis = new BufferedInputStream(bais);
								BufferedImage img = ImageIO.read(bis);
								imageList.add(img);
								IOUtils.closeQuietly(bis);
								IOUtils.closeQuietly(bais);
							}
						} else if (object instanceof byte[]) {
							byte[] bytes = (byte[]) object;
							bais = new ByteArrayInputStream(bytes);
							bis = new BufferedInputStream(bais);
							if (item.getImageHeight() > 0 && item.getImageWidth() > 0) {
								BufferedImage img = ImageIO.read(bis);
								bytes = ImageUtils.zoomImage(img, item.getImageWidth(), item.getImageHeight(),
										item.getImageMergeTargetType());
							} else if (item.getImageScale() > 0 && item.getImageScale() < 1.0) {
								if (bytes.length > item.getImageScaleSize() * FileUtils.MB_SIZE) {
									BufferedImage img = ImageIO.read(bis);
									bytes = ImageUtils.zoomByScale(img, item.getImageScale(),
											item.getImageMergeTargetType());
								}
							}
							IOUtils.closeQuietly(bis);
							IOUtils.closeQuietly(bais);

							if (StringUtils.equals(item.getImageMergeFlag(), "Y")) {
								bais = new ByteArrayInputStream(bytes);
								bis = new BufferedInputStream(bais);
								BufferedImage img = ImageIO.read(bis);
								imageList.add(img);
								IOUtils.closeQuietly(bis);
								IOUtils.closeQuietly(bais);
							}
							if (StringUtils.isNotEmpty(item.getFileNameColumn())) {
								String filename = ParamUtils.getString(rowMap, item.getFileNameColumn().toLowerCase());
								if (StringUtils.isNotEmpty(filename)) {
									JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(bytes, filename);
									rowMap.put(item.getName() + "_image", jxlsImage);
									useExt = "Y";
								}
							}
						}
					} else {
						if (StringUtils.equals(item.getFileFlag(), "Y")
								&& StringUtils.isNotEmpty(item.getFilePathColumn())) {
							String filePath = ParamUtils.getString(rowMap, item.getFilePathColumn().toLowerCase());
							if (filePath != null) {
								String rootPath = item.getRootPath();
								if (StringUtils.startsWith(rootPath, "${ROOT_PATH}")) {
									rootPath = StringTools.replace(rootPath, "${ROOT_PATH}",
											SystemProperties.getAppPath());
								} else {
									rootPath = SystemProperties.getAppPath();
								}
								String imgPath = rootPath + "/" + filePath;
								JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(imgPath);
								rowMap.put(item.getName() + "_image", jxlsImage);
								useExt = "Y";

								if (StringUtils.equals(item.getImageMergeFlag(), "Y")) {
									byte[] bytes = FileUtils.getBytes(imgPath);
									bais = new ByteArrayInputStream(bytes);
									bis = new BufferedInputStream(bais);
									if (item.getImageHeight() > 0 && item.getImageWidth() > 0) {
										BufferedImage img = ImageIO.read(bis);
										bytes = ImageUtils.zoomImage(img, item.getImageWidth(), item.getImageHeight(),
												item.getImageMergeTargetType());
									} else if (item.getImageScale() > 0 && item.getImageScale() < 1.0) {
										if (bytes.length > item.getImageScaleSize() * FileUtils.MB_SIZE) {
											BufferedImage img = ImageIO.read(bis);
											bytes = ImageUtils.zoomByScale(img, item.getImageScale(),
													item.getImageMergeTargetType());
										}
									}
									bais = new ByteArrayInputStream(bytes);
									bis = new BufferedInputStream(bais);
									BufferedImage img = ImageIO.read(bis);
									imageList.add(img);
									IOUtils.closeQuietly(bis);
									IOUtils.closeQuietly(bais);
								}
							}
						}
					}
					// pageList.add(rowMap);
					if (i > 0 && i % pageSize == 0) {
						// pageNo++;
						// params.put(item.getName() + "_rows" + pageNo, pageList);
						// params.put("rows_" + item.getName() + "_" + pageNo, pageList);
						// pageList.clear();
					}
				}

				if (imageList.size() > 0) {
					BufferedImage[] imgs = new BufferedImage[imageList.size()];
					for (int i = 0; i < imageList.size(); i++) {
						imgs[i] = imageList.get(i);
					}
					boolean isHorizontal = false;
					if (StringUtils.equals(item.getImageMergeDirection(), "H")) {
						isHorizontal = true;
					}
					BufferedImage targetImage = ImageUtils.mergeImage(imgs, isHorizontal);
					String imgType = item.getImageMergeTargetType();
					if (StringUtils.isEmpty(imgType)) {
						imgType = "png";
					}
					baos = new ByteArrayOutputStream();
					bos = new BufferedOutputStream(baos);
					ImageIO.write(targetImage, imgType, bos);
					bos.flush();
					baos.flush();
					byte[] bytes = baos.toByteArray();
					String filename = item.getName() + "." + imgType;
					JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(bytes, filename);
					params.put(item.getName() + "_image", jxlsImage);
					useExt = "Y";
					IOUtils.closeQuietly(bos);
					IOUtils.closeQuietly(baos);
				}
			}

			StringBuilder builder = new StringBuilder();
			StringTokenizer token = null;
			String text = null;
			for (ExportItem item : exportApp.getItems()) {
				builder.delete(0, builder.length());
				if (StringUtils.isNotEmpty(item.getVarTemplate())) {
					text = TemplateUtils.process(params, item.getVarTemplate());
					if (StringUtils.isNotEmpty(text)) {
						token = new StringTokenizer(text, "<br/>");
						while (token.hasMoreTokens()) {
							builder.append(token.nextToken());
							builder.append(FileUtils.newline);
						}
						text = builder.toString();
						params.put(item.getName() + "_tpl", text);
						params.put(item.getName() + "_var", text);
					}
				}
			}

			if (exportApp.getVariables() != null && !exportApp.getVariables().isEmpty()) {
				logger.debug("template vars:" + exportApp.getVariables().size());
				for (ExportTemplateVar var : exportApp.getVariables()) {
					builder.delete(0, builder.length());
					if (StringUtils.isNotEmpty(var.getVarTemplate())) {
						text = TemplateUtils.process(params, var.getVarTemplate());
						logger.debug(var.getName() + "=" + text);
						token = new StringTokenizer(text, "<br/>");
						while (token.hasMoreTokens()) {
							builder.append(token.nextToken());
							builder.append(FileUtils.newline);
						}
						text = builder.toString();
						params.put(var.getName(), text);
						params.put(var.getName() + "_var", text);
					}
				}
			}

			DataXFactory.preprocess(params, exportApp);

			if (StringUtils.equals(exportApp.getUseExt(), "Y")) {
				useExt = "Y";
			}

			// logger.debug("rpt params:" + params);

			if (StringUtils.equals(exportApp.getMulitiFlag(), "Y") && StringUtils.isNotEmpty(exportApp.getPageVarName())
					&& exportApp.getPageNumPerSheet() > 100 && exportApp.getPageNumPerSheet() <= 400) {
				List<Paging> pagingList = (List<Paging>) params.get(exportApp.getPageVarName() + "_paging");
				if (pagingList != null && pagingList.size() > 0) {
					int pSize = pagingList.size();
					int sortNo = 0;

					if (StringUtils.isEmpty(jobNo)) {
						jobNo = DateUtils.getNowYearMonthDayHHmmss() + "";
					}

					List<ExportFileHistory> historyList2 = new CopyOnWriteArrayList<ExportFileHistory>();
					ForkJoinPool pool = ForkJoinPool.commonPool();
					logger.info("准备执行并行生成Excel...");
					try {
						if (pSize > 1 && StringUtils.equals(exportApp.getParallelFlag(), "Y")) {
							ExportExcelAction action = new ExportExcelAction(loginContext, exportApp, params, tpl,
									fileExt, jobNo, sortNo, historyList2);
							pool.submit(action);
						} else {
							List<Paging> newPagingList = new ArrayList<Paging>();
							List<List<Paging>> allPList = new ArrayList<List<Paging>>();
							for (int i = 0; i < pSize; i++) {
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
							for (int i = 0; i < size; i++) {
								sortNo++;
								List<Paging> newPagingList2 = allPList.get(i);
								ExportExcelTask task = new ExportExcelTask(loginContext, exportApp, newPagingList2,
										params, tpl, fileExt, jobNo, sortNo);
								Future<ExportFileHistory> result = pool.submit(task);
								if (result != null && result.get() != null) {
									ExportFileHistory his = result.get();
									historyList.add(his);
								}
								newPagingList2.clear();
							}

							if (newPagingList.size() > 0) {
								sortNo++;
								ExportExcelTask task = new ExportExcelTask(loginContext, exportApp, newPagingList,
										params, tpl, fileExt, jobNo, sortNo);
								Future<ExportFileHistory> result = pool.submit(task);
								if (result != null && result.get() != null) {
									ExportFileHistory his = result.get();
									historyList.add(his);
								}
								newPagingList.clear();
							}
						}

						// 线程阻塞，等待所有任务完成
						try {
							pool.awaitTermination(500, TimeUnit.MILLISECONDS);
						} catch (InterruptedException ex) {
						}
					} catch (Exception ex) {
						// //ex.printStackTrace();
						logger.error("export task error", ex);
					} finally {
						pool.shutdown();
						logger.info("并行生成Excel已经结束。");
					}

					if (!historyList2.isEmpty()) {
						historyList.addAll(historyList2);
					}

					Collections.sort(historyList);// 按转换顺序排序
					Map<String, byte[]> zipMap = new LinkedHashMap<String, byte[]>();
					for (ExportFileHistory his : historyList) {
						if (his.getData() != null) {
							zipMap.put(his.getFilename(), his.getData());
						}
					}

					byte[] data = ZipUtils.toZipBytes(zipMap);

					DataFile dataFile = new DataFileEntity();
					dataFile.setFilename("export.zip");
					dataFile.setParameter(params);
					dataFile.setData(data);

					ExportFileHistory his = new ExportFileHistory();
					his.setExpId(expId);
					his.setDeploymentId(exportApp.getDeploymentId());
					his.setGenYmd(DateUtils.getNowYearMonthDay());
					his.setJobNo(jobNo);
					his.setSortNo(0);
					his.setFilename(exportApp.getExportFileExpr() + jobNo + ".zip");
					his.setData(data);
					his.setLastModified(System.currentTimeMillis());
					his.setCreateBy(loginContext.getActorId());
					his.setCreateTime(new java.util.Date(his.getLastModified()));
					historyList.add(his);

					byte[] byteArray = null;
					if (StringUtils.equals(toPDF, "Y") && StringUtils.equals(exportApp.getMergePDFFlag(), "Y")) {
						ExportFileHistory his2 = new ExportFileHistory();
						his2.setExpId(expId);
						his2.setDeploymentId(exportApp.getDeploymentId());
						his2.setGenYmd(DateUtils.getNowYearMonthDay());
						his2.setJobNo(jobNo);
						his2.setSortNo(99);
						his2.setFilename(exportApp.getExportFileExpr() + jobNo + ".pdf");
						his2.setLastModified(System.currentTimeMillis());
						his2.setCreateBy(loginContext.getActorId());
						his2.setCreateTime(new java.util.Date(his.getLastModified()));

						/**
						 * 如果已经在生成Excel是完成转换PDF功能了，只需要合并即可
						 */
						if (StringUtils.equals(exportApp.getGenerateFlag(), "ONE")) {
							List<byte[]> fileList = new ArrayList<byte[]>();
							for (ExportFileHistory hs : historyList) {
								if (hs.getPdfData() != null) {
									fileList.add(hs.getPdfData());
								}
							}
							if (fileList.size() > 0) {
								byte[] data2 = PDFUtils.merge(fileList);
								if (data2 != null && data2.length > 0) {
									logger.debug("生成PDF的文件大小:" + data2.length);
									his2.setData(data2);
									historyList.add(his2);

									byteArray = new byte[data2.length];
									System.arraycopy(data2, 0, byteArray, 0, data2.length);
									// data = data2;
									params.put("_pdf_", true);
									params.put("_zip_", false);
									dataFile.setData(byteArray);
									dataFile.setFilename("export.pdf");
								}
							}
						} else {
							PDFConverterBean bean2 = new PDFConverterBean();
							byte[] data2 = bean2.merge(exportApp, zipMap, fileExt);
							if (data2 != null && data2.length > 0) {
								logger.debug("生成PDF的文件大小:" + data2.length);
								his2.setData(data2);
								historyList.add(his2);

								byteArray = new byte[data2.length];
								System.arraycopy(data2, 0, byteArray, 0, data2.length);
								// data = data2;
								params.put("_pdf_", true);
								params.put("_zip_", false);
								dataFile.setData(byteArray);
								dataFile.setFilename("export.pdf");
							}
						}
					} else {
						params.put("_zip_", true);
						dataFile.setFilename("export.zip");
					}
					// exportFileHistoryService.bulkInsert(hisList);
					if (StringUtils.equals(exportApp.getHistoryFlag(), "Y")) {
						ExportDataSaveBean saveBean = new ExportDataSaveBean();
						saveBean.saveAll(exportApp, historyList);
					}
					if (byteArray != null) {
						dataFile.setData(byteArray);
					}
					return dataFile;
				} else {
					pagingList = new ArrayList<Paging>();
					Paging paging = new Paging();
					List<Map<String, Object>> dataListx = new java.util.ArrayList<Map<String, Object>>();
					for (ExportItem item : exportApp.getItems()) {
						if (StringUtils.equals(item.getName(), exportApp.getPageVarName())) {
							if (StringUtils.equals(item.getGenEmptyFlag(), "Y")) {
								for (int i = 0; i < item.getPageSize(); i++) {
									dataListx.add(new HashMap<String, Object>());
								}
								paging.setDataList(dataListx);
							}
						}
					}

					if (dataListx.size() > 0) {
						useExt = "Y";
						pagingList.add(paging);// 加空白，保持样式
						params.put(exportApp.getPageVarName() + "_paging", pagingList);
					}
				}
			}

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
					JdbcHelper jdbcHelper = new JdbcHelper(conn, params);
					QueryHelper queryHelper = new QueryHelper(conn, params);
					MyBatisHelper myBatisHelper = new MyBatisHelper(conn, params);
					EntityHelper entityHelper = new EntityHelper(database, params);
				
					params.put("jdbc", jdbcHelper);
					params.put("entity", entityHelper);
					params.put("dbutils", queryHelper);
					params.put("mybatis", myBatisHelper);
				}
			}

			if (StringUtils.equals(useExt, "Y")) {
				bais = new ByteArrayInputStream(tpl.getData());
				bis = new BufferedInputStream(bais);
				baos = new ByteArrayOutputStream();
				bos = new BufferedOutputStream(baos);
				JxlsBuilder jxlsBuilder = JxlsBuilder.getBuilder(bis).out(bos).putAll(params);
				jxlsBuilder.putVar("_ignoreImageMiss", Boolean.valueOf(true));
				jxlsBuilder.build();
			} else {
				bais = new ByteArrayInputStream(tpl.getData());
				bis = new BufferedInputStream(bais);
				baos = new ByteArrayOutputStream();
				bos = new BufferedOutputStream(baos);
				try {
					Context context2 = PoiTransformer.createInitialContext();

					Set<Entry<String, Object>> entrySet = params.entrySet();
					for (Entry<String, Object> entry : entrySet) {
						String key = entry.getKey();
						Object value = entry.getValue();
						context2.putVar(key, value);
					}
					JxlsHelper.getInstance().processTemplate(bis, bos, context2);
				} catch (Exception ex) {
					JxlsBuilder jxlsBuilder = JxlsBuilder.getBuilder(bis).out(bos).putAll(params);
					jxlsBuilder.putVar("_ignoreImageMiss", Boolean.valueOf(true));
					jxlsBuilder.build();
				}
			}

			if (StringUtils.equals(exportApp.getEnableSQLFlag(), "Y")) {
				if (conn != null) {
					QueryConnectionFactory.getInstance().unregister(ts, conn);
					JdbcUtils.close(conn);
					conn = null;
				}
			}

			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bais);

			bos.flush();
			baos.flush();
			byte[] data = baos.toByteArray();
			bais = new ByteArrayInputStream(data);
			bis = new BufferedInputStream(bais);

			ZipSecureFile.setMinInflateRatio(-1.0d);// 延迟解析比率
			wb = org.apache.poi.ss.usermodel.WorkbookFactory.create(bis);
			WorkbookFactory.process(wb, exportApp);
			baos = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(baos);
			wb.write(bos);
			bos.flush();
			baos.flush();
			data = baos.toByteArray();

			DataFile dataFile = new DataFileEntity();
			dataFile.setFilename("export." + fileExt);
			dataFile.setData(data);
			dataFile.setParameter(params);

			if (StringUtils.equals(exportApp.getHistoryFlag(), "Y")) {
				ExportFileHistory his = new ExportFileHistory();
				his.setExpId(expId);
				his.setDeploymentId(exportApp.getDeploymentId());
				his.setGenYmd(DateUtils.getNowYearMonthDay());
				his.setJobNo(jobNo);
				his.setSortNo(0);
				his.setFilename(exportApp.getExportFileExpr() + fileExt);
				his.setData(data);
				his.setLastModified(System.currentTimeMillis());
				his.setCreateBy(loginContext.getActorId());
				his.setCreateTime(new java.util.Date(his.getLastModified()));
				historyList.add(his);
				ExportDataSaveBean saveBean = new ExportDataSaveBean();
				saveBean.saveAll(exportApp, historyList);
			}

			return dataFile;

		} catch (Exception ex) {
			// //ex.printStackTrace();
			logger.error("export error", ex);
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
		return null;
	}

}
