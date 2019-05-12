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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.JFreeChart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.chart.bean.ChartDataManager;
import com.glaf.chart.domain.Chart;
import com.glaf.chart.gen.ChartGen;
import com.glaf.chart.gen.JFreeChartFactory;
import com.glaf.chart.query.ChartQuery;
import com.glaf.chart.service.ChartService;
import com.glaf.chart.util.ChartUtils;
import com.glaf.core.base.TableModel;
import com.glaf.core.config.DatabaseConnectionConfig;
import com.glaf.core.config.Environment;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.domain.Database;
import com.glaf.core.query.DatabaseQuery;
import com.glaf.core.security.LoginContext;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.service.ITablePageService;
import com.glaf.core.service.ITreeModelService;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.JsonUtils;
import com.glaf.core.util.LogUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.QueryUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;
import com.glaf.matrix.data.domain.SqlDefinition;
import com.glaf.matrix.data.query.SqlDefinitionQuery;
import com.glaf.matrix.data.service.SqlDefinitionService;

@Controller("/matrix/chart")
@RequestMapping("/matrix/chart")
public class ChartController {
	protected static final Log logger = LogFactory.getLog(ChartController.class);

	protected ChartService chartService;

	protected IDatabaseService databaseService;

	protected ITablePageService tablePageService;

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

	@RequestMapping("/chartTree")
	public ModelAndView chartTree(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		String x_view = ViewProperties.getString("chart.chartTree");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/chart/chart_tree", modelMap);
	}

	@RequestMapping("/checkSQL")
	public byte[] checkSQL(HttpServletRequest request) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		JSONObject result = new JSONObject();
		String querySQL = request.getParameter("querySQL");
		if (StringUtils.isNotEmpty(querySQL)) {
			if (!DBUtils.isLegalQuerySql(querySQL)) {
				return ResponseUtils.responseJsonResult(false, "SQL查询不合法！");
			}

			if (!DBUtils.isAllowedSql(querySQL)) {
				return ResponseUtils.responseJsonResult(false, "SQL查询不合法！");
			}

			Map<String, Object> paramMap = new java.util.HashMap<String, Object>();

			querySQL = QueryUtils.replaceSQLVars(querySQL);
			querySQL = QueryUtils.replaceSQLParas(querySQL, paramMap);
			TableModel rowMode = new TableModel();
			rowMode.setSql(querySQL);

			DatabaseConnectionConfig config = new DatabaseConnectionConfig();
			long databaseId = RequestUtils.getLong(request, "databaseId");
			Database currentDB = config.getDatabase(loginContext, databaseId);
			String systemName = Environment.getCurrentSystemName();
			try {
				if (currentDB != null) {
					Environment.setCurrentSystemName(currentDB.getName());
				}
				List<Map<String, Object>> rows = tablePageService.getListData(querySQL, paramMap);
				if (rows != null && !rows.isEmpty()) {
					logger.debug("chart rows size:" + rows.size());
					JSONArray arrayJSON = new JSONArray();
					for (Map<String, Object> dataMap : rows) {
						JSONObject row = new JSONObject();
						Set<Entry<String, Object>> entrySet = dataMap.entrySet();
						for (Entry<String, Object> entry : entrySet) {
							String name = entry.getKey();
							Object value = entry.getValue();
							if (value != null) {
								if (value instanceof Date) {
									Date d = (Date) value;
									row.put(name, DateUtils.getDate(d));
								} else if (value instanceof Boolean) {
									row.put(name, (Boolean) value);
								} else if (value instanceof Integer) {
									row.put(name, (Integer) value);
								} else if (value instanceof Long) {
									row.put(name, (Long) value);
								} else if (value instanceof Double) {
									row.put(name, (Double) value);
								} else {
									row.put(name, value.toString());
								}
							}
						}
						arrayJSON.add(row);
					}
					result.put("rows", arrayJSON);
					result.put("total", rows.size());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.error(ex);
			} finally {
				com.glaf.core.config.Environment.setCurrentSystemName(systemName);
			}
		} else {
			return ResponseUtils.responseJsonResult(false, "SQL查询不合法！");
		}

		return result.toString().getBytes("UTF-8");
	}

	@RequestMapping("/chooseQuery")
	public ModelAndView chooseQuery(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		request.removeAttribute("canSubmit");
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		String rowId = ParamUtils.getString(params, "chartId");
		SqlDefinitionQuery query = new SqlDefinitionQuery();
		List<SqlDefinition> list = sqlDefinitionService.list(query);
		List<String> selecteds = new java.util.ArrayList<String>();
		request.setAttribute("unselecteds", list);
		Chart chart = null;
		if (StringUtils.isNotEmpty(rowId)) {
			chart = chartService.getChart(rowId);
			request.setAttribute("chart", chart);
			if (StringUtils.isNotEmpty(chart.getQueryIds())) {
				StringBuffer sb01 = new StringBuffer();
				StringBuffer sb02 = new StringBuffer();

				for (SqlDefinition q : list) {
					if (StringUtils.contains(chart.getQueryIds(), q.getCode())) {
						selecteds.add(q.getCode());
						sb01.append(q.getCode()).append(",");
						sb02.append(q.getName()).append(",");
					}
				}
				if (sb01.toString().endsWith(",")) {
					sb01.delete(sb01.length() - 1, sb01.length());
				}
				if (sb02.toString().endsWith(",")) {
					sb02.delete(sb02.length() - 1, sb02.length());
				}
				request.setAttribute("selecteds", selecteds);
				request.setAttribute("queryIds", sb01.toString());

				request.setAttribute("queryNames", sb02.toString());
			}
		}

		StringBuffer bufferx = new StringBuffer();
		StringBuffer buffery = new StringBuffer();

		for (int j = 0; j < list.size(); j++) {
			SqlDefinition u = (SqlDefinition) list.get(j);
			if (selecteds != null && selecteds.contains(u.getCode())) {
				buffery.append("\n<option value=\"").append(u.getId()).append("\">").append(u.getId()).append(" [")
						.append(u.getTitle()).append("]").append("</option>");
			} else {
				bufferx.append("\n<option value=\"").append(u.getId()).append("\">").append(u.getId()).append(" [")
						.append(u.getTitle()).append("]").append("</option>");
			}
		}

		request.setAttribute("bufferx", bufferx.toString());
		request.setAttribute("buffery", buffery.toString());

		String x_view = ViewProperties.getString("chart.chooseQuery");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/chart/chooseQuery", modelMap);
	}

	@RequestMapping("/delete")
	public ModelAndView delete(HttpServletRequest request, ModelMap modelMap) {
		LoginContext securityContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		String rowId = ParamUtils.getString(params, "chartId");
		String rowIds = request.getParameter("chartIds");
		if (StringUtils.isNotEmpty(rowIds)) {
			StringTokenizer token = new StringTokenizer(rowIds, ",");
			while (token.hasMoreTokens()) {
				String x = token.nextToken();
				if (StringUtils.isNotEmpty(x)) {
					chartService.deleteById(x);
				}
			}
		} else if (StringUtils.isNotEmpty(rowId)) {
			Chart chart = chartService.getChart(rowId);
			if (chart != null && StringUtils.equals(chart.getCreateBy(), securityContext.getActorId())) {
				chartService.deleteById(chart.getId());
			}
		}

		return this.list(request, modelMap);
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
			String x_complex_query = request.getParameter("x_complex_query");
			if (StringUtils.isNotEmpty(x_complex_query)) {
				x_complex_query = RequestUtils.decodeString(x_complex_query);
				Map<String, Object> paramMap = JsonUtils.decode(x_complex_query);
				logger.debug("paramMap:" + paramMap);
				params.putAll(paramMap);
			}

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

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		LoginContext loginContext = RequestUtils.getLoginContext(request);

		DatabaseQuery query = new DatabaseQuery();
		query.active("1");
		List<Database> activeDatabases = new ArrayList<Database>();

		List<Database> databases = null;
		if (loginContext.isSystemAdministrator()) {
			databases = databaseService.list(query);
		} else {
			databases = databaseService.getDatabases(loginContext.getActorId());
		}

		if (databases != null && !databases.isEmpty()) {
			for (Database database : databases) {
				if ("1".equals(database.getActive())) {
					DatabaseConnectionConfig config = new DatabaseConnectionConfig();
					if (config.checkConfig(database)) {
						activeDatabases.add(database);
						logger.debug(database.getName() + " check connection ok.");
					}
				}
			}
		}

		if (!activeDatabases.isEmpty()) {
			request.setAttribute("databases", activeDatabases);
		}

		Map<String, Object> params = RequestUtils.getParameterMap(request);
		String chartId = ParamUtils.getString(params, "chartId");
		Chart chart = null;
		if (StringUtils.isNotEmpty(chartId)) {
			chart = chartService.getChart(chartId);
			request.setAttribute("chart", chart);
			if (StringUtils.isNotEmpty(chart.getQueryIds())) {
				List<String> queryIds = StringTools.split(chart.getQueryIds());
				StringBuffer sb01 = new StringBuffer();
				StringBuffer sb02 = new StringBuffer();
				for (String queryId : queryIds) {
					SqlDefinition queryDefinition = sqlDefinitionService.getSqlDefinitionByCode(queryId);
					if (queryDefinition != null) {
						sb01.append(queryDefinition.getId()).append(",");
						sb02.append(queryDefinition.getTitle()).append("[").append(queryDefinition.getId())
								.append("],");
					}
				}
				if (sb01.toString().endsWith(",")) {
					sb01.delete(sb01.length() - 1, sb01.length());
				}
				if (sb02.toString().endsWith(",")) {
					sb02.delete(sb02.length() - 1, sb02.length());
				}
				request.setAttribute("queryIds", sb01.toString());
				request.setAttribute("queryNames", sb02.toString());
			}

		}

		List<Integer> listx = new ArrayList<Integer>();
		List<Integer> listy = new ArrayList<Integer>();

		for (int i = 1; i <= 30; i++) {
			listx.add(i * 30);
		}

		for (int i = 1; i <= 60; i++) {
			listy.add(i * 30);
		}

		request.setAttribute("itemsH", listx);
		request.setAttribute("itemsW", listy);

		String category = request.getParameter("category");
		if (StringUtils.isEmpty(category)) {
			category = "report_category";
		}
		/**
		 * TreeModel treeModel = treeModelService.getTreeModelByCode(category); if
		 * (treeModel != null) { treeModel =
		 * treeModelService.getTreeModelWithAllChildren(treeModel.getId());
		 * List<TreeModel> treeModels = treeModel.getChildren();
		 * request.setAttribute("treeModels", treeModels); }
		 **/

		String x_view = ViewProperties.getString("chart.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/chart/edit", modelMap);
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, ModelMap modelMap) throws IOException {
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ChartQuery query = new ChartQuery();
		Tools.populate(query, params);

		String keywordsLike_base64 = request.getParameter("keywordsLike_base64");
		if (StringUtils.isNotEmpty(keywordsLike_base64)) {
			String keywordsLike = new String(Base64.decodeBase64(keywordsLike_base64));
			query.setKeywordsLike(keywordsLike);
		}

		Long nodeId = RequestUtils.getLong(request, "nodeId");
		if (nodeId != null && nodeId > 0) {
			query.nodeId(nodeId);
		}

		String gridType = ParamUtils.getString(params, "gridType");
		if (gridType == null) {
			gridType = "easyui";
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
		int total = chartService.getChartCountByQueryCriteria(query);
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

			List<Chart> list = chartService.getChartsByQueryCriteria(start, limit, query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();
				for (Chart chart : list) {
					JSONObject rowJSON = chart.toJsonObject();
					rowsJSON.add(rowJSON);
				}
				result.put("rows", rowsJSON);
			}
		} else {
			JSONArray rowsJSON = new JSONArray();
			result.put("rows", rowsJSON);
			result.put("total", total);
		}
		LogUtils.debug(result.toJSONString());
		return result.toString().getBytes("UTF-8");
	}

	@RequestMapping
	public ModelAndView list(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		return new ModelAndView("/matrix/chart/list", modelMap);
	}

	@RequestMapping("/query")
	public ModelAndView query(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}
		String x_view = ViewProperties.getString("chart.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/matrix/chart/query", modelMap);
	}

	@RequestMapping("/queryTree")
	public ModelAndView queryTree(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		String x_view = ViewProperties.getString("chart.queryTree");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/chart/query_tree", modelMap);
	}

	@ResponseBody
	@RequestMapping("/saveChart")
	public byte[] saveChart(HttpServletRequest request, ModelMap modelMap) {
		LoginContext securityContext = RequestUtils.getLoginContext(request);
		String actorId = securityContext.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		try {
			String chartId = request.getParameter("chartId");
			if (StringUtils.isEmpty(chartId)) {
				chartId = request.getParameter("id");
			}
			String name = request.getParameter("chartName");
			String mapping = request.getParameter("mapping");
			Chart chart = null;
			if (StringUtils.isNotEmpty(chartId)) {
				chart = chartService.getChart(chartId);
				if (chart != null) {
					if (StringUtils.isNotEmpty(name)) {
						Chart model = chartService.getChartByName(name);
						if (model != null && !StringUtils.equals(chart.getId(), model.getId())) {
							return ResponseUtils.responseJsonResult(false, "图表名称已经存在，请换个名称！");
						}
					}

					if (StringUtils.isNotEmpty(mapping)) {
						Chart model = chartService.getChartByMapping(mapping);
						if (model != null && !StringUtils.equals(chart.getId(), model.getId())) {
							return ResponseUtils.responseJsonResult(false, "图表别名已经存在，请换个别名！");
						}
					}
				}
			}

			if (chart == null) {
				chart = new Chart();

				if (StringUtils.isNotEmpty(name)) {
					Chart model = chartService.getChartByName(name);
					if (model != null) {
						return ResponseUtils.responseJsonResult(false, "图表名称已经存在，请换个名称！");
					}
				}

				if (StringUtils.isNotEmpty(mapping)) {
					Chart model = chartService.getChartByMapping(mapping);
					if (model != null) {
						return ResponseUtils.responseJsonResult(false, "图表别名已经存在，请换个别名！");
					}
				}

			}

			logger.debug("params:" + params);
			Tools.populate(chart, params);
			chart.setChartTitle(request.getParameter("chartTitle"));
			chart.setChartSubTitle(request.getParameter("chartSubTitle"));
			chart.setSecondCoordinateX(request.getParameter("secondCoordinateX"));
			chart.setSecondCoordinateY(request.getParameter("secondCoordinateY"));
			chart.setQueryIds(request.getParameter("queryIds"));
			chart.setDatasetIds(request.getParameter("datasetIds"));
			chart.setSecondDatasetIds(request.getParameter("secondDatasetIds"));
			chart.setThirdDatasetIds(request.getParameter("thirdDatasetIds"));

			String querySQL = request.getParameter("querySQL");
			if (StringUtils.isNotEmpty(querySQL)) {
				if (!DBUtils.isLegalQuerySql(querySQL)) {
					return ResponseUtils.responseJsonResult(false, "SQL查询不合法！");
				}
				// 增加检查SQL查询是否正确的逻辑
			}

			chart.setQuerySQL(querySQL);
			chart.setCreateBy(actorId);

			String queryIds = request.getParameter("queryIds");
			chart.setQueryIds(queryIds);

			this.chartService.save(chart);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ResponseUtils.responseJsonResult(false);
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
	public void setTablePageService(ITablePageService tablePageService) {
		this.tablePageService = tablePageService;
	}

	@javax.annotation.Resource
	public void setTreeModelService(ITreeModelService treeModelService) {
		this.treeModelService = treeModelService;
	}

	@RequestMapping("/showChart")
	public ModelAndView showChart(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		String chartId = ParamUtils.getString(params, "chartId");
		Chart chart = null;
		if (StringUtils.isNotEmpty(chartId)) {
			chart = chartService.getChart(chartId);
			request.setAttribute("chart", chart);
		}

		String x_view = ViewProperties.getString("chart.showChart");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/chart/showChart", modelMap);
	}

	@RequestMapping("/update")
	public ModelAndView update(HttpServletRequest request, ModelMap modelMap) {
		Map<String, Object> params = RequestUtils.getParameterMap(request);

		String chartId = ParamUtils.getString(params, "chartId");
		Chart chart = null;
		if (StringUtils.isNotEmpty(chartId)) {
			chart = chartService.getChart(chartId);
		}

		if (chart != null) {
			Tools.populate(chart, params);

			String querySQL = request.getParameter("querySQL");
			if (StringUtils.isNotEmpty(querySQL)) {
				if (!DBUtils.isLegalQuerySql(querySQL)) {
					throw new RuntimeException("SQL查询不合法！");
				}
				if (!DBUtils.isAllowedSql(querySQL)) {
					throw new RuntimeException("SQL查询不合法！");
				}
			}

			chartService.save(chart);
		}

		return this.list(request, modelMap);
	}

	@RequestMapping("/view")
	public ModelAndView view(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		String chartId = ParamUtils.getString(params, "chartId");
		Chart chart = null;
		if (StringUtils.isNotEmpty(chartId)) {
			chart = chartService.getChart(chartId);
			request.setAttribute("chart", chart);
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view);
		}

		String x_view = ViewProperties.getString("chart.view");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view);
		}

		return new ModelAndView("/matrix/chart/view");
	}

	@ResponseBody
	@RequestMapping("/viewChart")
	public void viewChart(HttpServletRequest request, HttpServletResponse response) {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		String chartId = ParamUtils.getString(params, "chartId");
		Chart chart = null;
		if (StringUtils.isNotEmpty(chartId)) {
			chart = chartService.getChart(chartId);
			if (chart != null) {
				String url = "";
				if (StringUtils.equals(chart.getImageType(), "png")) {
					url = "/mx/matrix/chart/showChart";
				} else if (StringUtils.equals(chart.getImageType(), "highcharts")) {
					url = "/mx/matrix/chart/highcharts/showChart";
				} else if (StringUtils.equals(chart.getImageType(), "kendo")) {
					url = "/mx/matrix/chart/kendo/showChart";
				} else if (StringUtils.equals(chart.getImageType(), "echarts")) {
					url = "/mx/matrix/chart/echarts/showChart";
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
					try {
						response.sendRedirect(url);
					} catch (IOException e) {
					}
				}
			}
		}
	}

}