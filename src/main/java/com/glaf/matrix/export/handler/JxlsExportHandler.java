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

package com.glaf.matrix.export.handler;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.config.SystemProperties;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.ZipUtils;

import com.glaf.jxls.ext.JxlsBuilder;
import com.glaf.jxls.ext.JxlsImage;
import com.glaf.jxls.ext.JxlsUtil;
import com.glaf.matrix.export.bean.ExportAppBean;
import com.glaf.matrix.export.bean.ExportExcelTask;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportFileHistory;
import com.glaf.matrix.export.domain.ExportItem;
import com.glaf.matrix.export.domain.ExportTemplateVar;
import com.glaf.matrix.export.factory.ExportChartFactory;
import com.glaf.matrix.export.preprocessor.DataPreprocessorFactory;
import com.glaf.matrix.export.service.ExportAppService;
import com.glaf.matrix.export.service.ExportFileHistoryService;
import com.glaf.matrix.util.ImageUtils;
import com.glaf.matrix.util.SysParams;
import com.glaf.template.Template;
import com.glaf.template.service.ITemplateService;
import com.glaf.template.util.TemplateUtils;

public class JxlsExportHandler implements ExportHandler {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public byte[] export(LoginContext loginContext, String expId, Map<String, Object> params) {
		ITemplateService templateService = ContextFactory.getBean("templateService");
		ExportAppService exportAppService = ContextFactory.getBean("com.glaf.matrix.export.service.exportAppService");
		ExportFileHistoryService exportFileHistoryService = ContextFactory
				.getBean("com.glaf.matrix.export.service.exportFileHistoryService");
		String useExt = ParamUtils.getString(params, "useExt");
		Workbook wb = null;

		ByteArrayInputStream bais = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		BufferedOutputStream bos = null;
		try {
			ExportApp exportApp = exportAppService.getExportApp(expId);
			if (exportApp == null || !StringUtils.equals(exportApp.getActive(), "Y")) {
				return null;
			}

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

			Template tpl = templateService.getTemplate(exportApp.getTemplateId());
			if (tpl == null || tpl.getData() == null) {
				return null;
			}

			// int pageNo = 0;
			SysParams.putInternalParams(params);
			params.put("_ignoreImageMiss", Boolean.valueOf(true));// 图片不存在跳过
			params.put("_exp_", exportApp);

			/**
			 * 数据预处理器，预先处理数据
			 */
			PreprocessorFactory.doBefore(expId, params);

			/**
			 * 处理截图
			 */
			ExportChartFactory.snapshot(exportApp, params);
			if (ParamUtils.getString(params, "_useExt_") != null) {
				useExt = "Y";
			}

			Collection<Map<String, Object>> pageList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			ExportAppBean bean = new ExportAppBean();
			exportApp = bean.execute(exportApp.getId(), params);
			for (ExportItem item : exportApp.getItems()) {
				// pageNo = 0;

				/**
				 * 数据预处理程序
				 */
				DataPreprocessorFactory.prepare(item, params);

				dataList.clear();
				if (item.getDataList() != null && !item.getDataList().isEmpty()) {
					dataList.addAll(item.getDataList());
					if (StringUtils.equals(item.getResultFlag(), "O")) {
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
					pageList.add(rowMap);
					if (i > 0 && i % pageSize == 0) {
						// pageNo++;
						// params.put(item.getName() + "_rows" + pageNo, pageList);
						// params.put("rows_" + item.getName() + "_" + pageNo, pageList);
						pageList.clear();
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

			if (StringUtils.equals(exportApp.getHistoryFlag(), "Y")
					&& StringUtils.isNotEmpty(exportApp.getPageVarName()) && exportApp.getPageNumPerSheet() > 0
					&& exportApp.getPageNumPerSheet() <= 800) {
				List<Paging> pagingList = (List<Paging>) params.get(exportApp.getPageVarName() + "_paging");
				if (pagingList != null && pagingList.size() > 0) {
					int pSize = pagingList.size();
					int sortNo = 0;
					String jobNo = ParamUtils.getString(params, "jobNo");
					String fileExt = "xls";
					if (StringUtils.endsWithIgnoreCase(tpl.getDataFile(), ".xlsx")) {
						fileExt = "xlsx";
					}
					if (StringUtils.isEmpty(jobNo)) {
						jobNo = DateUtils.getNowYearMonthDayHHmmss() + "";
					}

					ForkJoinPool pool = ForkJoinPool.commonPool();
					logger.info("准备执行并行生成Excel...");
					List<ExportFileHistory> hisList = new ArrayList<ExportFileHistory>();
					List<Paging> newPagingList = new ArrayList<Paging>();
					List<List<Paging>> allPList = new ArrayList<List<Paging>>();
					try {
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
							ExportExcelTask task = new ExportExcelTask(loginContext, exportApp, newPagingList2, params,
									tpl, fileExt, jobNo, sortNo);
							Future<ExportFileHistory> result = pool.submit(task);
							if (result != null && result.get() != null) {
								ExportFileHistory his = result.get();
								hisList.add(his);
							}
							newPagingList2.clear();
						}

						if (newPagingList.size() > 0) {
							sortNo++;
							ExportExcelTask task = new ExportExcelTask(loginContext, exportApp, newPagingList, params,
									tpl, fileExt, jobNo, sortNo);
							Future<ExportFileHistory> result = pool.submit(task);
							if (result != null && result.get() != null) {
								ExportFileHistory his = result.get();
								hisList.add(his);
							}
							newPagingList.clear();
						}
						// 线程阻塞，等待所有任务完成
						try {
							pool.awaitTermination(500, TimeUnit.MILLISECONDS);
						} catch (InterruptedException ex) {
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						logger.error("export task error", ex);
					} finally {
						pool.shutdown();
						logger.info("并行生成Excel已经结束。");
					}

					Map<String, byte[]> zipMap = new HashMap<String, byte[]>();
					for (ExportFileHistory his : hisList) {
						zipMap.put(his.getFilename(), his.getData());
					}

					byte[] data = ZipUtils.toZipBytes(zipMap);

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
					hisList.add(his);

					exportFileHistoryService.bulkInsert(hisList);

					params.put("_zip_", true);

					return data;
				}
				return null;
			} else {
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

			return data;

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
