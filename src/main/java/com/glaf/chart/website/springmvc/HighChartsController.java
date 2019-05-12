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

package com.glaf.chart.website.springmvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.chart.bean.ChartDataManager;
import com.glaf.chart.domain.Chart;
import com.glaf.chart.service.ChartService;
import com.glaf.core.base.ColumnModel;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.util.Hex;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;

@Controller("/public/highcharts")
@RequestMapping("/public/highcharts")
public class HighChartsController {
	protected static final Log logger = LogFactory.getLog(HighChartsController.class);

	protected ChartService chartService;

	public HighChartsController() {

	}

	@ResponseBody
	@RequestMapping("/json")
	public byte[] json(HttpServletRequest request, ModelMap modelMap) throws IOException {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		String chartId = ParamUtils.getString(params, "chartId");
		String mapping = ParamUtils.getString(params, "mapping");
		String mapping_enc = ParamUtils.getString(params, "mapping_enc");
		String name = ParamUtils.getString(params, "name");
		String name_enc = ParamUtils.getString(params, "name_enc");
		String param_hex = request.getParameter("json_params_hex");
		if (StringUtils.isNotEmpty(param_hex)) {
			try {
				String json = new String(Hex.hex2byte(param_hex), "UTF-8");
				JSONObject jsonObject = JSON.parseObject(json);
				params.putAll(jsonObject);
			} catch (IOException e) {
			}
		}
		Chart chart = null;
		if (StringUtils.isNotEmpty(chartId)) {
			chart = chartService.getChart(chartId);
		} else if (StringUtils.isNotEmpty(name)) {
			chart = chartService.getChartByName(name);
		} else if (StringUtils.isNotEmpty(name_enc)) {
			String str = RequestUtils.decodeString(name_enc);
			chart = chartService.getChartByName(str);
		} else if (StringUtils.isNotEmpty(mapping)) {
			chart = chartService.getChartByMapping(mapping);
		} else if (StringUtils.isNotEmpty(mapping_enc)) {
			String str = RequestUtils.decodeString(mapping_enc);
			chart = chartService.getChartByMapping(str);
		}
		JSONArray result = new JSONArray();
		if (chart != null) {
			String chartType = request.getParameter("chartType");
			if (StringUtils.isEmpty(chartType)) {
				chartType = chart.getChartType();
			}
			ChartDataManager manager = new ChartDataManager();
			chart = manager.getChartAndFetchDataById(chart.getId(), params, RequestUtils.getActorId(request));
			logger.debug("chart rows size:" + chart.getColumns().size());

			List<ColumnModel> columns = chart.getColumns();
			if (columns != null && !columns.isEmpty()) {
				double total = 0D;
				if (StringUtils.equalsIgnoreCase(chartType, "funnel")) {
					logger.debug("sort columns");
					Collections.sort(columns);
				}
				if (StringUtils.equalsIgnoreCase(chartType, "pie")
						|| StringUtils.equalsIgnoreCase(chartType, "donut")) {
					for (ColumnModel cm : columns) {
						total += cm.getDoubleValue();
					}
				}
				for (ColumnModel cm : columns) {
					JSONObject json = new JSONObject();
					json.put("category", cm.getCategory());
					json.put("series", cm.getSeries());
					json.put("value", cm.getDoubleValue());
					if (StringUtils.equalsIgnoreCase(chartType, "pie")
							|| StringUtils.equalsIgnoreCase(chartType, "donut")) {
						json.put("category", cm.getCategory());
						json.put("value", Math.round(cm.getDoubleValue() / total * 10000) / 100.0D);
					}
					result.add(json);
				}
			}
		}
		logger.debug("json:" + result.toJSONString());
		return result.toJSONString().getBytes("UTF-8");
	}

	@javax.annotation.Resource
	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}

	@RequestMapping("/showChart")
	public ModelAndView showChart(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		logger.debug("params:" + params);
		String chartId = ParamUtils.getString(params, "chartId");
		String mapping = ParamUtils.getString(params, "mapping");
		String mapping_enc = ParamUtils.getString(params, "mapping_enc");
		String name = ParamUtils.getString(params, "name");
		String name_enc = ParamUtils.getString(params, "name_enc");
		String param_hex = request.getParameter("json_params_hex");
		if (StringUtils.isNotEmpty(param_hex)) {
			try {
				String json = new String(Hex.hex2byte(param_hex), "UTF-8");
				JSONObject jsonObject = JSON.parseObject(json);
				params.putAll(jsonObject);
			} catch (IOException e) {
			}
		}
		Chart chart = null;
		if (StringUtils.isNotEmpty(chartId)) {
			chart = chartService.getChart(chartId);
		} else if (StringUtils.isNotEmpty(name)) {
			chart = chartService.getChartByName(name);
		} else if (StringUtils.isNotEmpty(name_enc)) {
			String str = RequestUtils.decodeString(name_enc);
			chart = chartService.getChartByName(str);
		} else if (StringUtils.isNotEmpty(mapping)) {
			chart = chartService.getChartByMapping(mapping);
		} else if (StringUtils.isNotEmpty(mapping_enc)) {
			String str = RequestUtils.decodeString(mapping_enc);
			chart = chartService.getChartByMapping(str);
		}
		if (chart != null) {
			String chartType = request.getParameter("chartType");
			if (StringUtils.isEmpty(chartType)) {
				chartType = chart.getChartType();
			}
			ChartDataManager manager = new ChartDataManager();
			chart = manager.getChartAndFetchDataById(chart.getId(), params, RequestUtils.getActorId(request));
			logger.debug("chart rows size:" + chart.getColumns().size());
			request.setAttribute("chart", chart);
			request.setAttribute("chartId", chart.getId());
			request.setAttribute("name", chart.getChartName());
			JSONArray result = new JSONArray();

			List<ColumnModel> columns = chart.getColumns();
			if (columns != null && !columns.isEmpty()) {
				double total = 0D;

				if (StringUtils.equalsIgnoreCase(chartType, "funnel")
						|| StringUtils.equalsIgnoreCase(chartType, "gauge")) {
					logger.debug("sort columns");
					Collections.sort(columns);
				}

				List<String> categories = new ArrayList<String>();

				if (StringUtils.equalsIgnoreCase(chartType, "pie")
						|| StringUtils.equalsIgnoreCase(chartType, "donut")) {
					JSONArray array = new JSONArray();
					for (ColumnModel cm : columns) {
						if (cm.getDoubleValue() != null) {
							List<Object> rows = new ArrayList<Object>();
							total += cm.getDoubleValue();
							rows.add(cm.getCategory());
							rows.add(cm.getDoubleValue());
							array.add(rows);
						}
					}
					request.setAttribute("pie_data", array.toJSONString());
				}

				for (ColumnModel cm : columns) {
					if (cm.getCategory() != null && !categories.contains("'" + cm.getCategory() + "'")) {
						categories.add("'" + cm.getCategory() + "'");
					}
				}

				request.setAttribute("categories", categories);
				request.setAttribute("categories_scripts", categories.toString());
				logger.debug("categories=" + categories);

				Map<String, List<Double>> seriesMap = new HashMap<String, List<Double>>();

				for (ColumnModel cm : columns) {
					String series = cm.getSeries();
					if (series != null) {
						List<Double> valueList = seriesMap.get(series);
						if (valueList == null) {
							valueList = new ArrayList<Double>();
						}
						if (cm.getDoubleValue() != null) {
							valueList.add(cm.getDoubleValue());
						} else {
							valueList.add(0D);
						}
						seriesMap.put(series, valueList);
					}
				}

				request.setAttribute("seriesMap", seriesMap);
				if (!seriesMap.isEmpty()) {
					JSONArray array = new JSONArray();
					Set<Entry<String, List<Double>>> entrySet = seriesMap.entrySet();
					for (Entry<String, List<Double>> entry : entrySet) {
						String key = entry.getKey();
						List<Double> valueList = entry.getValue();
						JSONObject json = new JSONObject();
						json.put("name", key);
						json.put("data", valueList);
						if (StringUtils.equalsIgnoreCase(chartType, "column_line")) {
							// json.put("color", "#4572A7");
							json.put("type", "column");
							json.put("yAxis", "1");
						}
						array.add(json);
					}
					logger.debug("seriesDataJson:" + array.toJSONString());
					request.setAttribute("seriesDataJson", array.toJSONString());
				}

				if (StringUtils.equalsIgnoreCase(chartType, "funnel")) {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					for (ColumnModel cm : columns) {
						JSONArray a = new JSONArray();
						a.add(cm.getCategory());
						a.add(cm.getDoubleValue());
						array.add(a);
					}
					json.put("name", chart.getChartName());
					json.put("data", array);
					request.setAttribute("seriesDataJson", "[" + json.toJSONString() + "]");
				}

				if (StringUtils.equalsIgnoreCase(chartType, "gauge")) {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					for (ColumnModel cm : columns) {
						array.add(cm.getDoubleValue());
						break;
					}
					json.put("name", chart.getChartName());
					json.put("data", array);
					request.setAttribute("seriesDataJson", "[" + json.toJSONString() + "]");
				}

				for (ColumnModel cm : columns) {
					JSONObject json = new JSONObject();
					json.put("category", cm.getCategory());
					json.put("series", cm.getSeries());
					json.put("value", cm.getDoubleValue());
					if (StringUtils.equalsIgnoreCase(chartType, "pie")
							|| StringUtils.equalsIgnoreCase(chartType, "donut")) {
						json.put("category", cm.getCategory());
						json.put("value", Math.round(cm.getDoubleValue() / total * 10000) / 100.0D);
					}
					result.add(json);
				}

			}

			if (StringUtils.equalsIgnoreCase(chartType, "column_line")) {

				JSONArray complexResult = new JSONArray();

				if (chart.getColumns() != null && !chart.getColumns().isEmpty()) {
					Map<String, List<Double>> dataMap = new HashMap<String, List<Double>>();

					for (ColumnModel cm : chart.getColumns()) {
						String key = cm.getSeries();
						if (key != null) {
							List<Double> valueList = dataMap.get(key);
							if (valueList == null) {
								valueList = new ArrayList<Double>();
							}
							if (cm.getDoubleValue() != null) {
								valueList.add(cm.getDoubleValue());
							} else {
								valueList.add(0D);
							}
							dataMap.put(key, valueList);
						}
					}

					if (!dataMap.isEmpty()) {
						Set<Entry<String, List<Double>>> entrySet = dataMap.entrySet();
						for (Entry<String, List<Double>> entry : entrySet) {
							String key = entry.getKey();
							List<Double> valueList = entry.getValue();
							JSONObject json = new JSONObject();
							json.put("name", key);
							json.put("type", "column");
							json.put("yAxis", 1);
							json.put("data", valueList);
							complexResult.add(json);
						}
					}
				}

				if (chart.getSecondColumns() != null && !chart.getSecondColumns().isEmpty()) {
					Map<String, List<Double>> dataMap = new HashMap<String, List<Double>>();

					for (ColumnModel cm : chart.getSecondColumns()) {
						String key = cm.getSeries();
						if (key != null) {
							List<Double> valueList = dataMap.get(key);
							if (valueList == null) {
								valueList = new ArrayList<Double>();
							}
							if (cm.getDoubleValue() != null) {
								valueList.add(cm.getDoubleValue());
							} else {
								valueList.add(0D);
							}
							dataMap.put(key, valueList);
						}
					}

					if (!dataMap.isEmpty()) {
						Set<Entry<String, List<Double>>> entrySet = dataMap.entrySet();
						for (Entry<String, List<Double>> entry : entrySet) {
							String key = entry.getKey();
							List<Double> valueList = entry.getValue();
							JSONObject json = new JSONObject();
							json.put("name", key);
							json.put("data", valueList);
							json.put("type", "line");
							complexResult.add(json);
						}
					}
				}

				request.setAttribute("complexJsonArray", complexResult.toJSONString());
			}

			request.setAttribute("jsonArray", result.toJSONString());

			if (StringUtils.equalsIgnoreCase(chartType, "pie")) {
				return new ModelAndView("/matrix/mychart/highcharts/pie", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "donut")) {
				return new ModelAndView("/matrix/mychart/highcharts/donut", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "funnel")) {
				return new ModelAndView("/matrix/mychart/highcharts/funnel", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "gauge")) {
				return new ModelAndView("/matrix/mychart/highcharts/gauge", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "line")) {
				return new ModelAndView("/matrix/mychart/highcharts/line", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "radarLine")) {
				return new ModelAndView("/matrix/mychart/highcharts/radarLine", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "area")) {
				return new ModelAndView("/matrix/mychart/highcharts/area", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "bar")) {
				return new ModelAndView("/matrix/mychart/highcharts/bar", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "column")) {
				return new ModelAndView("/matrix/mychart/highcharts/column", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "column_line")) {
				return new ModelAndView("/matrix/mychart/highcharts/column_line", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "stacked_area")) {
				return new ModelAndView("/matrix/mychart/highcharts/stacked_area", modelMap);
			} else if (StringUtils.equalsIgnoreCase(chartType, "stackedbar")) {
				return new ModelAndView("/matrix/mychart/highcharts/stackedbar", modelMap);
			}
		}
		String x_view = ViewProperties.getString("highcharts.chart");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/mychart/highcharts/chart", modelMap);
	}

}