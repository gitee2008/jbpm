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

@Controller("/public/echarts")
@RequestMapping("/public/echarts")
public class EChartsController {
	protected static final Log logger = LogFactory.getLog(EChartsController.class);

	protected ChartService chartService;

	public EChartsController() {

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
			} catch (IOException ex) {
				ex.printStackTrace();
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

		String chartType = request.getParameter("chartType");
		if (StringUtils.isEmpty(chartType)) {
			chartType = chart.getChartType();
		}
		ChartDataManager manager = new ChartDataManager();
		if (chart != null) {
			chart = manager.getChartAndFetchDataById(chart.getId(), params, RequestUtils.getActorId(request));
			logger.debug("chart rows size:" + chart.getColumns().size());
			request.setAttribute("chart", chart);
			request.setAttribute("chartId", chart.getId());
			request.setAttribute("name", chart.getChartName());

			if (chart.getColumns() != null && !chart.getColumns().isEmpty()) {
				JSONArray result = new JSONArray();
				List<ColumnModel> columns = chart.getColumns();

				double total = 0D;
				List<String> categories = new ArrayList<String>();

				if (StringUtils.equalsIgnoreCase(chartType, "pie") || StringUtils.equalsIgnoreCase(chartType, "bar")
						|| StringUtils.equalsIgnoreCase(chartType, "donut")) {
					JSONArray array = new JSONArray();
					JSONArray array2 = new JSONArray();
					JSONArray array3 = new JSONArray();
					for (ColumnModel cm : columns) {
						if (cm.getDoubleValue() != null) {
							List<Object> rows = new ArrayList<Object>();
							total += cm.getDoubleValue();
							rows.add(cm.getCategory());
							rows.add(cm.getDoubleValue());
							JSONObject json = new JSONObject();
							json.put("name", cm.getCategory());
							json.put("value", cm.getDoubleValue());
							array.add(rows);
							array2.add(cm.getDoubleValue());
							array3.add(json);
						}
					}
					request.setAttribute("pie_data", array.toJSONString());
					request.setAttribute("pie_json", array3.toJSONString());
					request.setAttribute("two_d_json", array3.toJSONString());
					request.setAttribute("barSeriesDataArray", array2.toJSONString());
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

				JSONArray legendArray = new JSONArray();

				request.setAttribute("seriesMap", seriesMap);
				if (!seriesMap.isEmpty()) {
					JSONArray array = new JSONArray();
					Set<Entry<String, List<Double>>> entrySet = seriesMap.entrySet();
					for (Entry<String, List<Double>> entry : entrySet) {
						String key = entry.getKey();
						List<Double> valueList = entry.getValue();
						JSONObject json = new JSONObject();
						legendArray.add(key);
						json.put("name", key);
						json.put("data", valueList);
						if (StringUtils.equalsIgnoreCase(chartType, "bar")
								|| StringUtils.equalsIgnoreCase(chartType, "column")) {
							json.put("type", "bar");
						} else if (StringUtils.equalsIgnoreCase(chartType, "line")) {
							json.put("type", "line");
						} else if (StringUtils.equalsIgnoreCase(chartType, "pie")) {
							json.put("type", "pie");
						} else if (StringUtils.equalsIgnoreCase(chartType, "gauge")) {
							json.put("type", "gauge");
						} else if (StringUtils.equalsIgnoreCase(chartType, "radarLine")) {
							json.put("type", "radar");
						}
						array.add(json);
					}
					logger.debug("seriesDataJson:" + array.toJSONString());
					request.setAttribute("seriesDataJson", array.toJSONString());
				}

				request.setAttribute("legendArrayJson", legendArray.toJSONString());

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
				request.setAttribute("jsonArray", result.toJSONString());
			} else if (chart.getSecondColumns() != null && !chart.getSecondColumns().isEmpty()) {
				JSONArray result = new JSONArray();
				List<ColumnModel> columns = chart.getSecondColumns();

				double total = 0D;
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
					request.setAttribute("second_pie_data", array.toJSONString());
				}

				for (ColumnModel cm : columns) {
					if (cm.getCategory() != null && !categories.contains("'" + cm.getCategory() + "'")) {
						categories.add("'" + cm.getCategory() + "'");
					}
				}

				request.setAttribute("second_categories", categories);
				request.setAttribute("second_categories_scripts", categories.toString());
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

				JSONArray legendArray = new JSONArray();

				request.setAttribute("second_seriesMap", seriesMap);
				if (!seriesMap.isEmpty()) {
					JSONArray array = new JSONArray();
					Set<Entry<String, List<Double>>> entrySet = seriesMap.entrySet();
					for (Entry<String, List<Double>> entry : entrySet) {
						String key = entry.getKey();
						List<Double> valueList = entry.getValue();
						JSONObject json = new JSONObject();
						legendArray.add(key);
						json.put("name", key);
						json.put("data", valueList);
						if (StringUtils.equalsIgnoreCase(chartType, "bar")
								|| StringUtils.equalsIgnoreCase(chartType, "column")) {
							json.put("type", "bar");
						} else if (StringUtils.equalsIgnoreCase(chartType, "line")) {
							json.put("type", "line");
						} else if (StringUtils.equalsIgnoreCase(chartType, "pie")) {
							json.put("type", "pie");
						} else if (StringUtils.equalsIgnoreCase(chartType, "gauge")) {
							json.put("type", "gauge");
						} else if (StringUtils.equalsIgnoreCase(chartType, "radarLine")) {
							json.put("type", "radar");
						}
						array.add(json);
					}
					logger.debug("second_seriesDataJson:" + array.toJSONString());
					request.setAttribute("second_seriesDataJson", array.toJSONString());
				}

				request.setAttribute("second_legendArrayJson", legendArray.toJSONString());

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
				request.setAttribute("second_jsonArray", result.toJSONString());
			} else if (chart.getThirdColumns() != null && !chart.getThirdColumns().isEmpty()) {
				JSONArray result = new JSONArray();
				List<ColumnModel> columns = chart.getThirdColumns();

				double total = 0D;
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
					request.setAttribute("third_pie_data", array.toJSONString());
				}

				for (ColumnModel cm : columns) {
					if (cm.getCategory() != null && !categories.contains("'" + cm.getCategory() + "'")) {
						categories.add("'" + cm.getCategory() + "'");
					}
				}

				request.setAttribute("third_categories", categories);
				request.setAttribute("third_categories_scripts", categories.toString());
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

				JSONArray legendArray = new JSONArray();

				request.setAttribute("third_seriesMap", seriesMap);
				if (!seriesMap.isEmpty()) {
					JSONArray array = new JSONArray();
					Set<Entry<String, List<Double>>> entrySet = seriesMap.entrySet();
					for (Entry<String, List<Double>> entry : entrySet) {
						String key = entry.getKey();
						List<Double> valueList = entry.getValue();
						JSONObject json = new JSONObject();
						legendArray.add(key);
						json.put("name", key);
						json.put("data", valueList);
						if (StringUtils.equalsIgnoreCase(chartType, "bar")
								|| StringUtils.equalsIgnoreCase(chartType, "column")) {
							json.put("type", "bar");
						} else if (StringUtils.equalsIgnoreCase(chartType, "line")) {
							json.put("type", "line");
						} else if (StringUtils.equalsIgnoreCase(chartType, "pie")) {
							json.put("type", "pie");
						} else if (StringUtils.equalsIgnoreCase(chartType, "gauge")) {
							json.put("type", "gauge");
						} else if (StringUtils.equalsIgnoreCase(chartType, "radarLine")) {
							json.put("type", "radar");
						}
						array.add(json);
					}
					logger.debug("third_seriesDataJson:" + array.toJSONString());
					request.setAttribute("third_seriesDataJson", array.toJSONString());
				}

				request.setAttribute("third_legendArrayJson", legendArray.toJSONString());

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
				request.setAttribute("third_jsonArray", result.toJSONString());
			}
		}

		if (StringUtils.equalsIgnoreCase(chartType, "bar")) {
			String x_view = "/matrix/mychart/echarts/bar";
			if (StringUtils.isNotEmpty(x_view)) {
				return new ModelAndView(x_view, modelMap);
			}
		}

		if (StringUtils.equalsIgnoreCase(chartType, "pie")) {
			String x_view = "/matrix/mychart/echarts/pie";
			if (StringUtils.isNotEmpty(x_view)) {
				return new ModelAndView(x_view, modelMap);
			}
		}

		if (StringUtils.equalsIgnoreCase(chartType, "donut")) {
			String x_view = "/matrix/mychart/echarts/donut";
			if (StringUtils.isNotEmpty(x_view)) {
				return new ModelAndView(x_view, modelMap);
			}
		}

		String x_view = ViewProperties.getString("echarts.chart");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/mychart/echarts/chart", modelMap);
	}

}