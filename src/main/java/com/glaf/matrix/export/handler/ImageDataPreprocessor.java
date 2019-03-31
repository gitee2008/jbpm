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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.glaf.core.config.SystemProperties;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;
import com.glaf.jxls.ext.JxlsImage;
import com.glaf.jxls.ext.JxlsUtil;
import com.glaf.jxls.ext.model.DataModel;
import com.glaf.jxls.ext.model.ListModel;
import com.glaf.jxls.ext.model.PageListModel;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportItem;

public class ImageDataPreprocessor implements DataXPreprocessor {

	@Override
	public void preprocess(Map<String, Object> parameter, ExportApp exportApp) {
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

					String imagePath = "imagepath";
					if (StringUtils.isNotEmpty(item.getFilePathColumn())) {
						imagePath = item.getFilePathColumn().toLowerCase().trim();
					}

					int index = 0;
					int pageNox = 0;
					List<PageListModel> pageListx = new ArrayList<PageListModel>();
					List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
					List<DataModel> tmpList2 = new ArrayList<DataModel>();
					for (Map<String, Object> dataMap : item.getDataList()) {
						tmpList.add(dataMap);
						DataModel model = new DataModel();
						Tools.populate(model, dataMap);
						model.setData(dataMap);
						model.setStartIndex(startIndex++);
						index++;
						if (ParamUtils.getString(dataMap, imagePath) != null) {
							String imgPath = rootPath + "/" + ParamUtils.getString(dataMap, imagePath);
							JxlsImage jxlsImage = null;
							try {
								jxlsImage = JxlsUtil.me().getJxlsImage(imgPath);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							if (index <= item.getImageNumPerUnit()) {
								listModel.getImagelist1().add(jxlsImage);
							} else if (item.getImageNumPerUnit() < index && index <= item.getImageNumPerUnit() * 2) {
								listModel.getImagelist2().add(jxlsImage);
							} else if (item.getImageNumPerUnit() * 2 < index
									&& index <= item.getImageNumPerUnit() * 3) {
								listModel.getImagelist3().add(jxlsImage);
							} else if (item.getImageNumPerUnit() * 3 < index
									&& index <= item.getImageNumPerUnit() * 4) {
								listModel.getImagelist4().add(jxlsImage);
							} else if (item.getImageNumPerUnit() * 4 < index
									&& index <= item.getImageNumPerUnit() * 5) {
								listModel.getImagelist5().add(jxlsImage);
							}
							parameter.put(item.getName() + "_image_" + index, jxlsImage);
							exportApp.setUseExt("Y");
						}

						if (ParamUtils.getString(dataMap, "imagepath1") != null) {
							String imgPath1 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath1");
							JxlsImage jxlsImage1 = null;
							try {
								jxlsImage1 = JxlsUtil.me().getJxlsImage(imgPath1);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							model.setImage1(jxlsImage1);
							exportApp.setUseExt("Y");
						}

						if (ParamUtils.getString(dataMap, "imagepath2") != null) {
							String imgPath2 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath2");
							JxlsImage jxlsImage2 = null;
							try {
								jxlsImage2 = JxlsUtil.me().getJxlsImage(imgPath2);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							model.setImage2(jxlsImage2);
							exportApp.setUseExt("Y");
						}

						if (ParamUtils.getString(dataMap, "imagepath3") != null) {
							String imgPath3 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath3");
							JxlsImage jxlsImage3 = null;
							try {
								jxlsImage3 = JxlsUtil.me().getJxlsImage(imgPath3);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							model.setImage3(jxlsImage3);
							exportApp.setUseExt("Y");
						}

						if (ParamUtils.getString(dataMap, "imagepath4") != null) {
							String imgPath4 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath4");
							JxlsImage jxlsImage4 = null;
							try {
								jxlsImage4 = JxlsUtil.me().getJxlsImage(imgPath4);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							model.setImage4(jxlsImage4);
							exportApp.setUseExt("Y");
						}

						if (ParamUtils.getString(dataMap, "imagepath5") != null) {
							String imgPath5 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath5");
							JxlsImage jxlsImage5 = null;
							try {
								jxlsImage5 = JxlsUtil.me().getJxlsImage(imgPath5);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							model.setImage5(jxlsImage5);
							exportApp.setUseExt("Y");
						}

						if (ParamUtils.getString(dataMap, "imagepath6") != null) {
							String imgPath6 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath6");
							JxlsImage jxlsImage6 = null;
							try {
								jxlsImage6 = JxlsUtil.me().getJxlsImage(imgPath6);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							model.setImage6(jxlsImage6);
							exportApp.setUseExt("Y");
						}

						if (ParamUtils.getString(dataMap, "imagepath7") != null) {
							String imgPath7 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath7");
							JxlsImage jxlsImage7 = null;
							try {
								jxlsImage7 = JxlsUtil.me().getJxlsImage(imgPath7);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							model.setImage7(jxlsImage7);
							exportApp.setUseExt("Y");
						}

						if (ParamUtils.getString(dataMap, "imagepath8") != null) {
							String imgPath8 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath8");
							JxlsImage jxlsImage8 = null;
							try {
								jxlsImage8 = JxlsUtil.me().getJxlsImage(imgPath8);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							model.setImage8(jxlsImage8);
							exportApp.setUseExt("Y");
						}

						if (ParamUtils.getString(dataMap, "imagepath9") != null) {
							String imgPath9 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath9");
							JxlsImage jxlsImage9 = null;
							try {
								jxlsImage9 = JxlsUtil.me().getJxlsImage(imgPath9);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							model.setImage9(jxlsImage9);
							exportApp.setUseExt("Y");
						}

						if (ParamUtils.getString(dataMap, "imagepath10") != null) {
							String imgPath10 = rootPath + "/" + ParamUtils.getString(dataMap, "imagepath10");
							JxlsImage jxlsImage10 = null;
							try {
								jxlsImage10 = JxlsUtil.me().getJxlsImage(imgPath10);
							} catch (IOException ex) {
								throw new RuntimeException(ex);
							}
							model.setImage10(jxlsImage10);
							exportApp.setUseExt("Y");
						}

						listModel.addDataModel(model);
						tmpList2.add(model);
						if (index > 0 && item.getPageSize() > 0 && index % item.getPageSize() == 0) {
							pageNox++;
							PageListModel pageListModel = new PageListModel();
							pageListModel.setPageNo(pageNox);
							pageListModel.getList().addAll(tmpList);
							pageListModel.getDatalist().addAll(tmpList2);
							pageListx.add(pageListModel);
							tmpList.clear();
							tmpList2.clear();
						}
					}
					parameter.put(item.getName() + "_listmodel", listModel);
					parameter.put(item.getName() + "_pagelist", pageListx);
				}
			}
		}
	}

}
