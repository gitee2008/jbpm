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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.UUID32;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportItem;

public class AggregateDataPreprocessor implements DataXPreprocessor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected void aggregate(Map<String, Object> aggrMap, Map<String, Object> dataMap) {
		Set<Entry<String, Object>> entrySet = dataMap.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value != null) {
				if (value instanceof Integer) {
					int intVal = (int) value;
					Integer subIntVal = (Integer) aggrMap.get(key);
					if (subIntVal == null) {
						subIntVal = new Integer(0);
					}
					subIntVal = subIntVal + intVal;
					aggrMap.put(key, subIntVal);
				} else if (value instanceof Long) {
					long longVal = (long) value;
					Long subLongVal = (Long) aggrMap.get(key);
					if (subLongVal == null) {
						subLongVal = new Long(0);
					}
					subLongVal = subLongVal + longVal;
					aggrMap.put(key, subLongVal);
				} else if (value instanceof Double) {
					double doubleVal = (double) value;
					Double subDoubleVal = (Double) aggrMap.get(key);
					if (subDoubleVal == null) {
						subDoubleVal = new Double(0.0);
					}
					subDoubleVal = subDoubleVal + doubleVal;
					aggrMap.put(key, subDoubleVal);
				}
			}
		}
	}

	protected List<Map<String, Object>> copy(List<Map<String, Object>> datalist) {
		List<Map<String, Object>> newlist = new ArrayList<Map<String, Object>>();
		newlist.addAll(datalist);
		return newlist;
	}

	protected List<Map<String, Object>> copy(List<Map<String, Object>> datalist, int pageSize) {
		List<Map<String, Object>> newlist = new ArrayList<Map<String, Object>>();
		newlist.addAll(datalist);
		int len = pageSize - datalist.size();
		logger.debug("补空行:" + len + "-------------------------------------------");
		for (int i = 0; i < len; i++) {
			newlist.add(new HashMap<String, Object>());
		}
		return newlist;
	}

	@Override
	public void preprocess(Map<String, Object> parameter, ExportApp exportApp) {
		for (ExportItem item : exportApp.getItems()) {
			logger.debug("item->" + item.toJsonObject().toJSONString());
			if (item.getPageSize() > 0) {
				int index = 0;
				int pageNoX = 0;
				int pageSizeX = 0;
				List<Paging> pagingList = new ArrayList<Paging>();
				Map<String, Object> totalMap = new HashMap<String, Object>();
				List<Map<String, Object>> onePageList = new ArrayList<Map<String, Object>>();
				Map<String, Map<String, Object>> aggregateMap = new LinkedHashMap<String, Map<String, Object>>();
				Map<String, List<Map<String, Object>>> groupListMap = new LinkedHashMap<String, List<Map<String, Object>>>();

				/**
				 * 预处理汇总数据,根据指定的字段做聚合及分组
				 */
				if (StringUtils.isNotEmpty(item.getSubTotalColumn())) {
					for (Map<String, Object> dataMap : item.getDataList()) {
						String splitColumn = ParamUtils.getString(dataMap, item.getSubTotalColumn());
						if (splitColumn != null) {
							splitColumn = splitColumn.trim().toLowerCase();
							Map<String, Object> aggrMap = aggregateMap.get(splitColumn);
							if (aggrMap == null) {
								aggrMap = new HashMap<String, Object>();
							}
							this.aggregate(aggrMap, dataMap);// 分组汇总
							this.aggregate(totalMap, dataMap);// 合计汇总
							aggregateMap.put(splitColumn, aggrMap);

							List<Map<String, Object>> subList = groupListMap.get(splitColumn);
							if (subList == null) {
								subList = new ArrayList<Map<String, Object>>();
								// logger.debug("splitColumn:" + splitColumn);
							}
							subList.add(dataMap);
							groupListMap.put(splitColumn, subList);// 数据分组
						}
					}

					logger.debug("groups:" + groupListMap.keySet());

					int recordTT = 0;
					int recordTotal = 0;
					List<Map<String, Object>> valueList = null;
					Set<Entry<String, List<Map<String, Object>>>> entrySet = groupListMap.entrySet();
					for (Entry<String, List<Map<String, Object>>> entry : entrySet) {
						index = 0;
						recordTT = 0;
						valueList = entry.getValue();
						recordTotal = valueList.size();
						Map<String, Object> subTotalMap = new HashMap<String, Object>();
						for (Map<String, Object> dataMap : valueList) {
							recordTT++;
							String str = null;
							if (StringUtils.isNotEmpty(item.getLineBreakColumn())) {
								str = ParamUtils.getString(dataMap, item.getLineBreakColumn());
							}
							if (str != null && item.getCharNumPerRow() > 0) {
								int row = (int) Math.ceil(str.length() * 1.0d / item.getCharNumPerRow());
								// logger.debug("[" + str + "]占行数:" + row);
								pageSizeX++;
								index = index + row;
								onePageList.add(dataMap);
								if (index > 0 && ((index % item.getPageSize() == 0) || index > item.getPageSize())) {
									Paging paging = new Paging();
									paging.setContextMap(parameter);
									paging.setParamMap(parameter);
									paging.setDataList(this.copy(onePageList));
									paging.setPageSize(pageSizeX);
									paging.setCurrentPage(++pageNoX);

									if (StringUtils.equals(item.getSubTotalFlag(), "Y")) {
										for (Map<String, Object> rowMap : onePageList) {
											this.aggregate(subTotalMap, rowMap);// 小计汇总
										}
									}

									if (recordTT == recordTotal) {// 是否是最后一条记录，如果是加上合计汇总
										paging.setSubTotalMap(subTotalMap);
									}

									pagingList.add(paging);
									onePageList.clear();
									pageSizeX = 0;
									index = 0;
									// logger.debug("dataList.size():" + paging.getDataList().size());
								}
							} else {
								pageSizeX++;
								index = index + 1;
								onePageList.add(dataMap);
								if (index > 0 && ((index % item.getPageSize() == 0) || index > item.getPageSize())) {
									Paging paging = new Paging();
									paging.setContextMap(parameter);
									paging.setParamMap(parameter);
									paging.setDataList(this.copy(onePageList));
									paging.setPageSize(pageSizeX);
									paging.setCurrentPage(++pageNoX);

									if (StringUtils.equals(item.getSubTotalFlag(), "Y")) {
										for (Map<String, Object> rowMap : onePageList) {
											this.aggregate(subTotalMap, rowMap);// 小计汇总
										}
									}

									if (recordTT == recordTotal) {// 是否是最后一条记录，如果是加上合计汇总
										paging.setSubTotalMap(subTotalMap);
									}
									pagingList.add(paging);
									onePageList.clear();
									pageSizeX = 0;
									index = 0;
									// logger.debug("dataList.size():" + paging.getDataList().size());
								}
							}

						}
						if (onePageList.size() > 0) {
							Paging paging = new Paging();
							paging.setContextMap(parameter);
							paging.setParamMap(parameter);
							paging.setDataList(this.copy(onePageList, item.getPageSize() - 2));// 不足一页，空行补齐
							paging.setPageSize(pageSizeX);
							paging.setCurrentPage(++pageNoX);

							if (StringUtils.equals(item.getSubTotalFlag(), "Y")) {
								for (Map<String, Object> rowMap : onePageList) {
									this.aggregate(subTotalMap, rowMap);// 小计汇总
								}
								paging.setSubTotalMap(subTotalMap);
							}
							pagingList.add(paging);
							onePageList.clear();
							// logger.debug("dataList.size():" + paging.getDataList().size());
						}
					}
				} else {
					/**
					 * 不做分组只分页的情况
					 */
					index = 0;
					for (Map<String, Object> dataMap : item.getDataList()) {
						String str = null;
						if (StringUtils.isNotEmpty(item.getLineBreakColumn())) {
							str = ParamUtils.getString(dataMap, item.getLineBreakColumn());
						}
						if (str != null && item.getCharNumPerRow() > 0) {
							int row = (int) Math.ceil(str.length() * 1.0d / item.getCharNumPerRow());
							index = index + row;
							// logger.debug("[" + str + "]占行数:" + row);
							onePageList.add(dataMap);
							pageSizeX++;
							if (index > 0 && ((index % item.getPageSize() == 0) || index > item.getPageSize())) {
								Paging paging = new Paging();
								paging.setContextMap(parameter);
								paging.setParamMap(parameter);
								paging.setDataList(this.copy(onePageList));
								paging.setPageSize(pageSizeX);
								paging.setCurrentPage(++pageNoX);

								if (StringUtils.equals(item.getSubTotalFlag(), "Y")) {
									Map<String, Object> subTotalMap = new HashMap<String, Object>();
									for (Map<String, Object> rowMap : onePageList) {
										this.aggregate(subTotalMap, rowMap);// 小计汇总
									}
									paging.setSubTotalMap(subTotalMap);
								}
								pagingList.add(paging);
								onePageList.clear();
								pageSizeX = 0;
								index = 0;
								// logger.debug("dataList.size():" + paging.getDataList().size());
							}
						} else {
							pageSizeX++;
							index = index + 1;
							onePageList.add(dataMap);
							if (index > 0 && ((index % item.getPageSize() == 0) || index > item.getPageSize())) {
								Paging paging = new Paging();
								paging.setContextMap(parameter);
								paging.setParamMap(parameter);
								paging.setDataList(this.copy(onePageList));
								paging.setPageSize(pageSizeX);
								paging.setCurrentPage(++pageNoX);

								if (StringUtils.equals(item.getSubTotalFlag(), "Y")) {
									Map<String, Object> subTotalMap = new HashMap<String, Object>();
									for (Map<String, Object> rowMap : onePageList) {
										this.aggregate(subTotalMap, rowMap);// 小计汇总
									}
									paging.setSubTotalMap(subTotalMap);
								}
								pagingList.add(paging);
								onePageList.clear();
								pageSizeX = 0;
								index = 0;
								// logger.debug("dataList.size():" + paging.getDataList().size());
							}
						}

						this.aggregate(totalMap, dataMap);// 合计汇总

					}

					if (onePageList.size() > 0) {
						Paging paging = new Paging();
						paging.setContextMap(parameter);
						paging.setParamMap(parameter);
						paging.setDataList(this.copy(onePageList, item.getPageSize() - 2));// 不足一页，空行补齐
						paging.setPageSize(pageSizeX);
						paging.setCurrentPage(++pageNoX);
						// logger.debug("dataList.size():" + paging.getDataList().size());

						if (StringUtils.equals(item.getSubTotalFlag(), "Y")) {
							Map<String, Object> subTotalMap = new HashMap<String, Object>();
							for (Map<String, Object> rowMap : onePageList) {
								this.aggregate(subTotalMap, rowMap);// 小计汇总
							}
							paging.setSubTotalMap(subTotalMap);
						}

						pagingList.add(paging);
						onePageList.clear();
					}
				}

				if (pagingList.size() > 0) {
					Paging paging = pagingList.get(pagingList.size() - 1);
					paging.setAggregateMap(totalMap);// 将总计写到最后一页上。
					logger.debug("pagingList.size():" + pagingList.size());
				} else {
					if (item.getDataList().size() == 0) {
						if (StringUtils.equals(item.getGenEmptyFlag(), "Y")) {
							logger.debug("生成空行数据填充......");
							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put(UUID32.generateShortUuid(), UUID32.getUUID());
							onePageList.add(dataMap);
							Paging paging = new Paging();
							paging.setContextMap(parameter);
							paging.setParamMap(parameter);
							paging.setDataList(onePageList);// 空行补齐
							paging.setPageSize(pageSizeX);
							paging.setCurrentPage(++pageNoX);
							logger.debug("dataList.size():" + paging.getDataList().size());
							pagingList.add(paging);
							// onePageList.clear();
						}
					}
				}

				parameter.put(item.getName() + "_paging", pagingList);
				parameter.put(item.getName() + "_total", totalMap);
				parameter.put(item.getName() + "_totalPage", pageNoX);
			}
		}
	}

}
