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
package com.glaf.chart.web.springmvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.chart.bean.ChartDataManager;
import com.glaf.chart.domain.Chart;
import com.glaf.chart.domain.ChartCombination;
import com.glaf.chart.query.ChartCombinationQuery;
import com.glaf.chart.query.ChartQuery;
import com.glaf.chart.service.ChartCombinationService;
import com.glaf.chart.service.ChartService;
import com.glaf.core.base.ColumnModel;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.identity.User;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.JsonUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;

/**
 * 
 * SpringMVC控制器
 *
 */

@Controller("/chart/combination")
@RequestMapping("/chart/combination")
public class ChartCombinationController {
	protected static final Log logger = LogFactory.getLog(ChartCombinationController.class);

	protected ChartService chartService;

	protected ChartCombinationService chartCombinationService;

	public ChartCombinationController() {

	}

	@ResponseBody
	@RequestMapping("/delete")
	public byte[] delete(HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String id = RequestUtils.getString(request, "id");
		String ids = request.getParameter("ids");
		if (StringUtils.isNotEmpty(ids)) {
			StringTokenizer token = new StringTokenizer(ids, ",");
			while (token.hasMoreTokens()) {
				String x = token.nextToken();
				if (StringUtils.isNotEmpty(x)) {
					ChartCombination chartCombination = chartCombinationService.getChartCombination(x);
					/**
					 * 此处业务逻辑需自行调整
					 */
					if (chartCombination != null
							&& (StringUtils.equals(chartCombination.getCreateBy(), loginContext.getActorId())
									|| loginContext.isSystemAdministrator())) {
						chartCombinationService.deleteById(id);
					}
				}
			}
			return ResponseUtils.responseResult(true);
		} else if (id != null) {
			ChartCombination chartCombination = chartCombinationService.getChartCombination(id);
			/**
			 * 此处业务逻辑需自行调整
			 */
			if (chartCombination != null
					&& (StringUtils.equals(chartCombination.getCreateBy(), loginContext.getActorId())
							|| loginContext.isSystemAdministrator())) {
				chartCombinationService.deleteById(id);
				return ResponseUtils.responseResult(true);
			}
		}
		return ResponseUtils.responseResult(false);
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		ChartCombination chartCombination = chartCombinationService
				.getChartCombination(RequestUtils.getString(request, "id"));
		if (chartCombination != null) {
			request.setAttribute("chartCombination", chartCombination);

			if (StringUtils.isNotEmpty(chartCombination.getChartIds())) {
				StringBuffer sb01 = new StringBuffer();
				StringBuffer sb02 = new StringBuffer();
				List<Chart> selecteds = new java.util.ArrayList<Chart>();
				ChartQuery query = new ChartQuery();
				List<Chart> list = chartService.list(query);
				request.setAttribute("unselecteds", list);
				List<String> selected = StringTools.split(chartCombination.getChartIds());
				for (Chart c : list) {
					if (selected.contains(c.getId())) {
						selecteds.add(c);
						sb01.append(c.getId()).append(",");
						sb02.append(c.getSubject()).append(",");
					}
				}
				if (sb01.toString().endsWith(",")) {
					sb01.delete(sb01.length() - 1, sb01.length());
				}
				if (sb02.toString().endsWith(",")) {
					sb02.delete(sb02.length() - 1, sb02.length());
				}
				request.setAttribute("selecteds", selecteds);
				request.setAttribute("chartIds", sb01.toString());

				request.setAttribute("chartNames", sb02.toString());
			}

		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("chartCombination.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/chart/combination/edit", modelMap);
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, ModelMap modelMap) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ChartCombinationQuery query = new ChartCombinationQuery();
		Tools.populate(query, params);
		query.deleteFlag(0);
		query.setActorId(loginContext.getActorId());
		query.setLoginContext(loginContext);
		/**
		 * 此处业务逻辑需自行调整
		 */
		if (!loginContext.isSystemAdministrator()) {
			String actorId = loginContext.getActorId();
			query.createBy(actorId);
		}

		int start = 0;
		int limit = 10;
		String orderName = null;
		String order = null;

		int pageNo = ParamUtils.getInt(params, "page");
		limit = ParamUtils.getInt(params, "rows");
		start = (pageNo - 1) * limit;
		orderName = ParamUtils.getString(params, "sortName");
		order = ParamUtils.getString(params, "sortOrder");

		if (start < 0) {
			start = 0;
		}

		if (limit <= 0) {
			limit = Paging.DEFAULT_PAGE_SIZE;
		}

		JSONObject result = new JSONObject();
		int total = chartCombinationService.getChartCombinationCountByQueryCriteria(query);
		if (total > 0) {
			result.put("total", total);
			result.put("totalCount", total);
			result.put("totalRecords", total);
			result.put("start", start);
			result.put("startIndex", start);
			result.put("limit", limit);
			result.put("pageSize", limit);

			if (StringUtils.isNotEmpty(orderName)) {
				query.setSortOrder(orderName);
				if (StringUtils.equals(order, "desc")) {
					query.setSortOrder(" desc ");
				}
			}

			List<ChartCombination> list = chartCombinationService.getChartCombinationsByQueryCriteria(start, limit,
					query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				result.put("rows", rowsJSON);

				for (ChartCombination chartCombination : list) {
					JSONObject rowJSON = chartCombination.toJsonObject();
					rowJSON.put("id", chartCombination.getId());
					rowJSON.put("rowId", chartCombination.getId());
					rowJSON.put("combinationId", chartCombination.getId());
					rowJSON.put("startIndex", ++start);
					rowsJSON.add(rowJSON);
				}

			}
		} else {
			JSONArray rowsJSON = new JSONArray();
			result.put("rows", rowsJSON);
			result.put("total", total);
		}
		return result.toJSONString().getBytes("UTF-8");
	}

	@RequestMapping
	public ModelAndView list(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String x_query = request.getParameter("x_query");
		if (StringUtils.equals(x_query, "true")) {
			Map<String, Object> paramMap = RequestUtils.getParameterMap(request);
			String x_complex_query = JsonUtils.encode(paramMap);
			x_complex_query = RequestUtils.encodeString(x_complex_query);
			request.setAttribute("x_complex_query", x_complex_query);
		} else {
			request.setAttribute("x_complex_query", "");
		}

		String requestURI = request.getRequestURI();
		if (request.getQueryString() != null) {
			request.setAttribute("fromUrl", RequestUtils.encodeURL(requestURI + "?" + request.getQueryString()));
		} else {
			request.setAttribute("fromUrl", RequestUtils.encodeURL(requestURI));
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		return new ModelAndView("/matrix/chart/combination/list", modelMap);
	}

	@RequestMapping("/preview")
	public ModelAndView preview(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ChartCombination chartCombination = chartCombinationService
				.getChartCombination(RequestUtils.getString(request, "id"));
		if (chartCombination != null) {
			request.setAttribute("chartCombination", chartCombination);
			if (StringUtils.isNotEmpty(chartCombination.getChartIds())) {
				List<Chart> charts = new java.util.ArrayList<Chart>();
				List<Chart> charts2 = new java.util.ArrayList<Chart>();
				ChartQuery query = new ChartQuery();
				List<Chart> list = chartService.list(query);
				List<String> selectedIds = StringTools.split(chartCombination.getChartIds());
				for (Chart c : list) {
					if (selectedIds.contains(c.getId())) {
						charts.add(c);
					}
				}

				for (Chart chart : charts) {
					String chartType = chart.getChartType();
					ChartDataManager manager = new ChartDataManager();
					chart = manager.getChartAndFetchDataById(chart.getId(), params, RequestUtils.getActorId(request));
					logger.debug("chart rows size:" + chart.getColumns().size());

					JSONArray result = new JSONArray();
					List<ColumnModel> columns = chart.getColumns();
					if (columns != null && !columns.isEmpty()) {
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
							chart.setPieData(array.toJSONString());
							request.setAttribute("pie_data" + chart.getId(), array.toJSONString());
							logger.debug(chart.getId() + " pie_data=" + chart.getPieData());
						}

						for (ColumnModel cm : columns) {
							if (cm.getCategory() != null && !categories.contains("'" + cm.getCategory() + "'")) {
								categories.add("'" + cm.getCategory() + "'");
							}
						}

						request.setAttribute("categories" + chart.getId(), categories);
						request.setAttribute("categories_scripts" + chart.getId(), categories.toString());
						chart.setCategoriesScripts(categories.toString());
						logger.debug(chart.getId() + " categories=" + chart.getCategoriesScripts());

						Map<String, List<Double>> seriesMap = new HashMap<String, List<Double>>();

						for (ColumnModel cm : columns) {
							String series = cm.getCategory();
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

						request.setAttribute("seriesMap" + chart.getId(), seriesMap);
						if (!seriesMap.isEmpty()) {
							JSONArray array = new JSONArray();
							Set<Entry<String, List<Double>>> entrySet = seriesMap.entrySet();
							for (Entry<String, List<Double>> entry : entrySet) {
								String key = entry.getKey();
								List<Double> valueList = entry.getValue();
								JSONObject json = new JSONObject();
								json.put("name", key);
								json.put("data", valueList);
								array.add(json);
							}

							chart.setSeriesDataJson(array.toJSONString());
							request.setAttribute("seriesDataJson" + chart.getId(), array.toJSONString());
							logger.debug(chart.getId() + " seriesDataJson:" + chart.getSeriesDataJson());
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
					request.setAttribute("jsonArray" + chart.getId(), result.toJSONString());
					chart.setJsonArrayData(result.toJSONString());
					logger.debug(chart.getId() + " jsonArray:" + chart.getJsonArrayData());

					charts2.add(chart);
				}

				request.setAttribute("charts", charts2);
			}
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("chartCombination.preview");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/chart/combination/" + chartCombination.getType(), modelMap);
	}

	@RequestMapping("/query")
	public ModelAndView query(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}
		String x_view = ViewProperties.getString("chartCombination.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/matrix/chart/combination/query", modelMap);
	}

	 
	@RequestMapping("/save")
	public ModelAndView save(HttpServletRequest request, ModelMap modelMap) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);

		ChartCombination chartCombination = new ChartCombination();
		Tools.populate(chartCombination, params);

		chartCombination.setName(request.getParameter("name"));
		chartCombination.setTitle(request.getParameter("title"));
		chartCombination.setServiceKey(request.getParameter("serviceKey"));
		chartCombination.setType(request.getParameter("type"));
		chartCombination.setCategory(request.getParameter("category"));
		chartCombination.setChartIds(request.getParameter("chartIds"));
		chartCombination.setDirection(request.getParameter("direction"));
		chartCombination.setCreateBy(actorId);

		chartCombinationService.save(chartCombination);

		return this.list(request, modelMap);
	}

	@ResponseBody
	@RequestMapping("/saveChartCombination")
	public byte[] saveChartCombination(HttpServletRequest request) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		logger.debug("params:" + params);
		ChartCombination chartCombination = new ChartCombination();
		try {
			Tools.populate(chartCombination, params);
			chartCombination.setName(request.getParameter("name"));
			chartCombination.setTitle(request.getParameter("title"));
			chartCombination.setServiceKey(request.getParameter("serviceKey"));
			chartCombination.setType(request.getParameter("type"));
			chartCombination.setCategory(request.getParameter("category"));
			chartCombination.setChartIds(request.getParameter("chartIds"));
			chartCombination.setDirection(request.getParameter("direction"));
			chartCombination.setCreateBy(actorId);
			this.chartCombinationService.save(chartCombination);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public @ResponseBody ChartCombination saveOrUpdate(HttpServletRequest request,
			@RequestBody Map<String, Object> model) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		ChartCombination chartCombination = new ChartCombination();
		try {
			Tools.populate(chartCombination, model);
			chartCombination.setName(ParamUtils.getString(model, "name"));
			chartCombination.setTitle(ParamUtils.getString(model, "title"));
			chartCombination.setServiceKey(ParamUtils.getString(model, "serviceKey"));
			chartCombination.setType(ParamUtils.getString(model, "type"));
			chartCombination.setCategory(ParamUtils.getString(model, "category"));
			chartCombination.setChartIds(ParamUtils.getString(model, "chartIds"));
			chartCombination.setDirection(ParamUtils.getString(model, "direction"));
			chartCombination.setCreateBy(actorId);
			this.chartCombinationService.save(chartCombination);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return chartCombination;
	}

	@javax.annotation.Resource
	public void setChartCombinationService(ChartCombinationService chartCombinationService) {
		this.chartCombinationService = chartCombinationService;
	}

	@javax.annotation.Resource
	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}

	@RequestMapping("/update")
	public ModelAndView update(HttpServletRequest request, ModelMap modelMap) {
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		logger.debug("params:" + params);
		ChartCombination chartCombination = chartCombinationService
				.getChartCombination(RequestUtils.getString(request, "id"));

		Tools.populate(chartCombination, params);

		chartCombination.setName(request.getParameter("name"));
		chartCombination.setTitle(request.getParameter("title"));
		chartCombination.setServiceKey(request.getParameter("serviceKey"));
		chartCombination.setType(request.getParameter("type"));
		chartCombination.setCategory(request.getParameter("category"));
		chartCombination.setChartIds(request.getParameter("chartIds"));
		chartCombination.setDirection(request.getParameter("direction"));

		chartCombinationService.save(chartCombination);

		return this.list(request, modelMap);
	}

	@RequestMapping("/view")
	public ModelAndView view(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		ChartCombination chartCombination = chartCombinationService
				.getChartCombination(RequestUtils.getString(request, "id"));
		request.setAttribute("chartCombination", chartCombination);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view);
		}

		String x_view = ViewProperties.getString("chartCombination.view");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view);
		}

		return new ModelAndView("/matrix/chart/combination/view");
	}

}
