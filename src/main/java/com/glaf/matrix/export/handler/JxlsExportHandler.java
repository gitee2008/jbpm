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
import java.util.Set;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.config.SystemProperties;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;
import com.glaf.jxls.ext.JxlsBuilder;
import com.glaf.jxls.ext.JxlsImage;
import com.glaf.jxls.ext.JxlsUtil;
import com.glaf.jxls.ext.model.DataModel;
import com.glaf.jxls.ext.model.ListModel;
import com.glaf.matrix.export.bean.ExportAppBean;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportItem;
import com.glaf.matrix.export.domain.ExportTemplateVar;
import com.glaf.matrix.export.service.ExportAppService;
import com.glaf.matrix.util.ImageUtils;
import com.glaf.matrix.util.SysParams;
import com.glaf.template.Template;
import com.glaf.template.service.ITemplateService;
import com.glaf.template.util.TemplateUtils;

public class JxlsExportHandler implements ExportHandler {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public byte[] export(LoginContext loginContext, String expId, Map<String, Object> params) {
		ITemplateService templateService = ContextFactory.getBean("templateService");
		ExportAppService exportAppService = ContextFactory.getBean("com.glaf.matrix.export.service.exportAppService");
		String useExt = ParamUtils.getString(params, "useExt");
		InputStream is = null;
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

			int pageNo = 0;
			SysParams.putInternalParams(params);
			params.put("_ignoreImageMiss", Boolean.valueOf(true));// 图片不存在跳过
			final Collection<Map<String, Object>> pageList = new ArrayList<Map<String, Object>>();
			final List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			ExportAppBean bean = new ExportAppBean();
			exportApp = bean.execute(exportApp.getId(), params);
			for (ExportItem item : exportApp.getItems()) {
				pageNo = 0;
				dataList.clear();
				dataList.addAll(item.getDataList());
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
								}
							}
						}
					}
					params.put(item.getName() + "_row", rowMap);
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
						pageNo++;
						params.put(item.getName() + "_rows" + pageNo, pageList);
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

			for (ExportItem item : exportApp.getItems()) {
				if (StringUtils.equals(item.getVariantFlag(), "Y")) {
					if (item.getDataList() != null && !item.getDataList().isEmpty()) {
						int startIndex = 1;
						String rootPath = item.getRootPath();
						if (StringUtils.startsWith(rootPath, "${ROOT_PATH}")) {
							rootPath = StringTools.replace(rootPath, "${ROOT_PATH}", SystemProperties.getAppPath());
						} else {
							rootPath = SystemProperties.getAppPath();
						}
						ListModel listModel = new ListModel();
						listModel.setGroupId(item.getName());
						listModel.setGroupName(item.getTitle());
						listModel.setJsonObject(item.toJsonObject());
						for (Map<String, Object> dataMap : item.getDataList()) {
							DataModel model = new DataModel();
							Tools.populate(model, dataMap);
							model.setData(dataMap);
							model.setStartIndex(startIndex++);
							if (ParamUtils.getString(dataMap, "imagepath1") != null) {
								String imgPath1 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath1");
								JxlsImage jxlsImage1 = JxlsUtil.me().getJxlsImage(imgPath1);
								model.setImage1(jxlsImage1);
								useExt = "Y";
							}

							if (ParamUtils.getString(dataMap, "imagepath2") != null) {
								String imgPath2 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath2");
								JxlsImage jxlsImage2 = JxlsUtil.me().getJxlsImage(imgPath2);
								model.setImage2(jxlsImage2);
								useExt = "Y";
							}

							if (ParamUtils.getString(dataMap, "imagepath3") != null) {
								String imgPath3 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath3");
								JxlsImage jxlsImage3 = JxlsUtil.me().getJxlsImage(imgPath3);
								model.setImage3(jxlsImage3);
								useExt = "Y";
							}

							if (ParamUtils.getString(dataMap, "imagepath4") != null) {
								String imgPath4 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath4");
								JxlsImage jxlsImage4 = JxlsUtil.me().getJxlsImage(imgPath4);
								model.setImage4(jxlsImage4);
								useExt = "Y";
							}

							if (ParamUtils.getString(dataMap, "imagepath5") != null) {
								String imgPath5 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath5");
								JxlsImage jxlsImage5 = JxlsUtil.me().getJxlsImage(imgPath5);
								model.setImage5(jxlsImage5);
								useExt = "Y";
							}

							listModel.addDataModel(model);
						}
						params.put(item.getName() + "_listmodel", listModel);
					}
				}
			}

			logger.debug("rpt params:" + params);

			bais = new ByteArrayInputStream(tpl.getData());
			is = new BufferedInputStream(bais);
			baos = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(baos);

			Context context2 = PoiTransformer.createInitialContext();

			Set<Entry<String, Object>> entrySet = params.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				String key = entry.getKey();
				Object value = entry.getValue();
				context2.putVar(key, value);
			}

			if (StringUtils.equals(useExt, "Y")) {
				JxlsBuilder jxlsBuilder = JxlsBuilder.getBuilder(is).out(bos).putAll(params);
				jxlsBuilder.putVar("_ignoreImageMiss", Boolean.valueOf(true));
				jxlsBuilder.build();
			} else {
				try {
					JxlsHelper.getInstance().processTemplate(is, bos, context2);
				} catch (Exception ex) {
					JxlsBuilder jxlsBuilder = JxlsBuilder.getBuilder(is).out(bos).putAll(params);
					jxlsBuilder.putVar("_ignoreImageMiss", Boolean.valueOf(true));
					jxlsBuilder.build();
				}
			}

			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(bais);

			bos.flush();
			baos.flush();
			byte[] data = baos.toByteArray();
			return data;

		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error("export error", ex);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(baos);
		}
		return null;
	}

}
