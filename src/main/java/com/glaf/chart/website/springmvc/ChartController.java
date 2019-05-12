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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.JFreeChart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.glaf.chart.bean.ChartDataManager;
import com.glaf.chart.domain.Chart;
import com.glaf.chart.gen.ChartGen;
import com.glaf.chart.gen.JFreeChartFactory;
import com.glaf.chart.service.ChartService;
import com.glaf.chart.util.ChartUtils;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.service.ITreeModelService;
import com.glaf.core.util.Hex;
import com.glaf.core.util.JwtUtils;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.matrix.data.service.SqlDefinitionService;

@Controller("/public/chart")
@RequestMapping("/public/chart")
public class ChartController {
	protected static final Log logger = LogFactory.getLog(ChartController.class);

	protected ChartService chartService;

	protected IDatabaseService databaseService;

	protected ITreeModelService treeModelService;

	protected SqlDefinitionService sqlDefinitionService;

	public ChartController() {

	}

	@ResponseBody
	@RequestMapping("/chart")
	public byte[] chart(HttpServletRequest request, HttpServletResponse response) {
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
		if (chart != null) {
			String chartType = request.getParameter("chartType");
			if (StringUtils.isEmpty(chartType)) {
				chartType = chart.getChartType();
			}
			if (StringUtils.isEmpty(chartType)) {
				chartType = "column";
			}
			ChartDataManager manager = new ChartDataManager();
			chart = manager.getChartAndFetchDataById(chart.getId(), params, RequestUtils.getActorId(request));
			logger.debug("chart rows size:" + chart.getColumns().size());
			ChartGen chartGen = JFreeChartFactory.getChartGen(chartType);
			if (chartGen != null) {
				JFreeChart jchart = chartGen.createChart(chart);
				byte[] bytes = ChartUtils.createChart(chart, jchart);
				return bytes;
			}
		}
		return null;
	}

	@ResponseBody
	@RequestMapping("/download")
	public void download(HttpServletRequest request, HttpServletResponse response) {
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
		if (chart != null) {

			ChartDataManager manager = new ChartDataManager();
			chart = manager.getChartAndFetchDataById(chart.getId(), params, RequestUtils.getActorId(request));
			logger.debug("chart rows size:" + chart.getColumns().size());
			String filename = "chart.png";
			String contentType = "image/png";
			if (StringUtils.equalsIgnoreCase(chart.getImageType(), "jpeg")) {
				filename = "chart.jpg";
				contentType = "image/jpeg";
			}
			String chartType = request.getParameter("chartType");
			if (StringUtils.isEmpty(chartType)) {
				chartType = chart.getChartType();
			}
			if (StringUtils.isEmpty(chartType)) {
				chartType = "column";
			}
			ChartGen chartGen = JFreeChartFactory.getChartGen(chartType);
			if (chartGen != null) {
				JFreeChart jchart = chartGen.createChart(chart);
				byte[] bytes = ChartUtils.createChart(chart, jchart);
				try {
					ResponseUtils.output(request, response, bytes, filename, contentType);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@javax.annotation.Resource
	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}

	@javax.annotation.Resource
	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@javax.annotation.Resource
	public void setSqlDefinitionService(SqlDefinitionService sqlDefinitionService) {
		this.sqlDefinitionService = sqlDefinitionService;
	}

	@javax.annotation.Resource
	public void setTreeModelService(ITreeModelService treeModelService) {
		this.treeModelService = treeModelService;
	}

	@RequestMapping("/showChart")
	public ModelAndView showChart(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		String param_hex = request.getParameter("json_params_hex");
		if (StringUtils.isNotEmpty(param_hex)) {
			try {
				String json = new String(Hex.hex2byte(param_hex), "UTF-8");
				JSONObject jsonObject = JSON.parseObject(json);
				params.putAll(jsonObject);
			} catch (IOException e) {
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(params);
		String jwt = JwtUtils.createJWT(jsonObject);
		String chartId = ParamUtils.getString(params, "chartId");
		Chart chart = null;
		if (StringUtils.isNotEmpty(chartId)) {
			chart = chartService.getChart(chartId);
			request.setAttribute("chart", chart);
			request.setAttribute("jwt", jwt);
		}

		String x_view = ViewProperties.getString("chart.showChart");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/mychart/showChart", modelMap);
	}

	@ResponseBody
	@RequestMapping("/viewChart")
	public void viewChart(HttpServletRequest request, HttpServletResponse response) {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		String param_hex = request.getParameter("json_params_hex");
		if (StringUtils.isNotEmpty(param_hex)) {
			try {
				String json = new String(Hex.hex2byte(param_hex), "UTF-8");
				JSONObject jsonObject = JSON.parseObject(json);
				params.putAll(jsonObject);
			} catch (IOException e) {
			}
		}
		String chartId = ParamUtils.getString(params, "chartId");
		Chart chart = null;
		if (StringUtils.isNotEmpty(chartId)) {
			chart = chartService.getChart(chartId);
			if (chart != null) {
				String url = "";
				if (StringUtils.equals(chart.getImageType(), "jfreecharts")) {
					url = "/website/public/jfreecharts/showChart";
				} else if (StringUtils.equals(chart.getImageType(), "highcharts")) {
					url = "/website/public/highcharts/showChart";
				} else if (StringUtils.equals(chart.getImageType(), "kendo")) {
					url = "/website/public/kendo/showChart";
				} else if (StringUtils.equals(chart.getImageType(), "echarts")) {
					url = "/website/public/echarts/showChart";
				}
				if (StringUtils.isNotEmpty(url)) {
					url = url + "?chartId=" + chart.getId();
					if (chart.getDatabaseId() > 0) {
						url = url + "&databaseId=" + chart.getDatabaseId();
					}
					url = request.getContextPath() + url;
					if (StringUtils.isNotEmpty(chart.getTheme())) {
						url = url + "&charts_theme=" + chart.getTheme() + "&theme=" + chart.getTheme();
					}
					JSONObject jsonObject = new JSONObject();
					jsonObject.putAll(params);
					String jwt = JwtUtils.createJWT(jsonObject);
					url = url + "&jwt=" + jwt;
					try {
						response.sendRedirect(url);
					} catch (IOException e) {
					}
				}
			}
		}
	}

}