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

package com.glaf.matrix.export.web.springmvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.glaf.core.base.BaseItem;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.identity.User;
import com.glaf.core.security.LoginContext;
import com.glaf.core.service.ITablePageService;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.Tools;

import com.glaf.matrix.export.domain.ExportChart;
import com.glaf.matrix.export.query.ExportChartQuery;
import com.glaf.matrix.export.service.ExportChartService;

/**
 * 
 * SpringMVC控制器
 *
 */

@Controller("/matrix/exportChart")
@RequestMapping("/matrix/exportChart")
public class ExportChartController {
	protected static final Log logger = LogFactory.getLog(ExportChartController.class);

	protected ExportChartService exportChartService;

	protected ITablePageService tablePageService;

	public ExportChartController() {

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
					ExportChart exportChart = exportChartService.getExportChart(String.valueOf(x));
					if (exportChart != null && (StringUtils.equals(exportChart.getCreateBy(), loginContext.getActorId())
							|| loginContext.isSystemAdministrator())) {
						exportChartService.deleteById(exportChart.getExpId());
					}
				}
			}
			return ResponseUtils.responseResult(true);
		} else if (id != null) {
			ExportChart exportChart = exportChartService.getExportChart(String.valueOf(id));
			if (exportChart != null && (StringUtils.equals(exportChart.getCreateBy(), loginContext.getActorId())
					|| loginContext.isSystemAdministrator())) {
				exportChartService.deleteById(exportChart.getExpId());
				return ResponseUtils.responseResult(true);
			}
		}
		return ResponseUtils.responseResult(false);
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		request.removeAttribute("canSubmit");

		ExportChart exportChart = exportChartService.getExportChart(request.getParameter("id"));
		if (exportChart != null) {
			request.setAttribute("exportChart", exportChart);
		}

		List<Map<String, Object>> list = tablePageService
				.getListData(" select ID_ as id, SUBJECT_ as title from BI_CHART where ENABLEFLAG_ = '1' ", params);
		if (list != null && !list.isEmpty()) {
			logger.debug("chart list:" + list.size());
			List<BaseItem> charts = new ArrayList<BaseItem>();
			for (Map<String, Object> dataMap : list) {
				BaseItem item = new BaseItem();
				item.setName(ParamUtils.getString(dataMap, "id"));
				item.setValue(ParamUtils.getString(dataMap, "title"));
				charts.add(item);
			}
			request.setAttribute("charts", charts);
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("exportChart.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/exportChart/edit", modelMap);
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, ModelMap modelMap) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ExportChartQuery query = new ExportChartQuery();
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
		int total = exportChartService.getExportChartCountByQueryCriteria(query);
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

			List<ExportChart> list = exportChartService.getExportChartsByQueryCriteria(start, limit, query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				result.put("rows", rowsJSON);

				for (ExportChart exportChart : list) {
					JSONObject rowJSON = exportChart.toJsonObject();
					rowJSON.put("id", exportChart.getId());
					rowJSON.put("rowId", exportChart.getId());
					rowJSON.put("exportChartId", exportChart.getId());
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

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		return new ModelAndView("/matrix/exportChart/list", modelMap);
	}

	@ResponseBody
	@RequestMapping("/save")
	public byte[] save(HttpServletRequest request) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ExportChart exportChart = new ExportChart();
		try {
			Tools.populate(exportChart, params);
			exportChart.setExpId(request.getParameter("expId"));
			exportChart.setName(request.getParameter("name"));
			exportChart.setTitle(request.getParameter("title"));
			exportChart.setHeight(RequestUtils.getInt(request, "height"));
			exportChart.setWidth(RequestUtils.getInt(request, "width"));
			exportChart.setImageType(request.getParameter("imageType"));
			exportChart.setChartId(request.getParameter("chartId"));
			exportChart.setChartUrl(request.getParameter("chartUrl"));
			exportChart.setSnapshotFlag(request.getParameter("snapshotFlag"));
			exportChart.setLocked(RequestUtils.getInt(request, "locked"));
			exportChart.setCreateBy(actorId);
			this.exportChartService.save(exportChart);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public @ResponseBody ExportChart saveOrUpdate(HttpServletRequest request, @RequestBody Map<String, Object> model) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		ExportChart exportChart = new ExportChart();
		try {
			Tools.populate(exportChart, model);
			exportChart.setExpId(ParamUtils.getString(model, "expId"));
			exportChart.setName(ParamUtils.getString(model, "name"));
			exportChart.setTitle(ParamUtils.getString(model, "title"));
			exportChart.setHeight(ParamUtils.getInt(model, "height"));
			exportChart.setWidth(ParamUtils.getInt(model, "width"));
			exportChart.setImageType(ParamUtils.getString(model, "imageType"));
			exportChart.setChartId(ParamUtils.getString(model, "chartId"));
			exportChart.setChartType(request.getParameter("chartType"));
			exportChart.setChartUrl(ParamUtils.getString(model, "chartUrl"));
			exportChart.setSnapshotFlag(ParamUtils.getString(model, "snapshotFlag"));
			exportChart.setLocked(ParamUtils.getInt(model, "locked"));
			exportChart.setCreateBy(actorId);
			this.exportChartService.save(exportChart);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return exportChart;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.exportChartService")
	public void setExportChartService(ExportChartService exportChartService) {
		this.exportChartService = exportChartService;
	}

	@javax.annotation.Resource
	public void setTablePageService(ITablePageService tablePageService) {
		this.tablePageService = tablePageService;
	}

}
