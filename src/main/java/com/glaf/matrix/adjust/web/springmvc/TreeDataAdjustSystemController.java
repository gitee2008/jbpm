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

package com.glaf.matrix.adjust.web.springmvc;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.glaf.core.config.DatabaseConnectionConfig;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.domain.Database;
import com.glaf.core.security.LoginContext;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.Tools;

import com.glaf.matrix.adjust.domain.TreeDataAdjust;
import com.glaf.matrix.adjust.query.TreeDataAdjustQuery;
import com.glaf.matrix.adjust.service.TreeDataAdjustService;
import com.glaf.matrix.adjust.util.TreeDataAdjustJsonFactory;

/**
 * 
 * SpringMVC控制器
 *
 */

@Controller("/sys/treeDataAdjust")
@RequestMapping("/sys/treeDataAdjust")
public class TreeDataAdjustSystemController {
	protected static final Log logger = LogFactory.getLog(TreeDataAdjustSystemController.class);

	protected IDatabaseService databaseService;

	protected TreeDataAdjustService treeDataAdjustService;

	public TreeDataAdjustSystemController() {

	}

	@ResponseBody
	@RequestMapping("/delete")
	public byte[] delete(HttpServletRequest request, HttpServletResponse response) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String id = RequestUtils.getString(request, "id");
		String ids = request.getParameter("ids");
		if (StringUtils.isNotEmpty(ids)) {
			StringTokenizer token = new StringTokenizer(ids, ",");
			while (token.hasMoreTokens()) {
				String x = token.nextToken();
				if (StringUtils.isNotEmpty(x)) {
					TreeDataAdjust treeDataAdjust = treeDataAdjustService.getTreeDataAdjust(x);
					if (treeDataAdjust != null
							&& (StringUtils.equals(treeDataAdjust.getCreateBy(), loginContext.getActorId())
									|| loginContext.isSystemAdministrator())) {
						treeDataAdjustService.deleteById(treeDataAdjust.getId());
					}
				}
			}
			return ResponseUtils.responseResult(true);
		} else if (id != null) {
			TreeDataAdjust treeDataAdjust = treeDataAdjustService.getTreeDataAdjust(id);
			if (treeDataAdjust != null && (StringUtils.equals(treeDataAdjust.getCreateBy(), loginContext.getActorId())
					|| loginContext.isSystemAdministrator())) {
				treeDataAdjustService.deleteById(treeDataAdjust.getId());
				return ResponseUtils.responseResult(true);
			}
		}
		return ResponseUtils.responseResult(false);
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		TreeDataAdjust treeDataAdjust = treeDataAdjustService.getTreeDataAdjust(request.getParameter("id"));
		if (treeDataAdjust != null) {
			request.setAttribute("treeDataAdjust", treeDataAdjust);
		}

		LoginContext loginContext = RequestUtils.getLoginContext(request);

		DatabaseConnectionConfig cfg = new DatabaseConnectionConfig();
		List<Database> activeDatabases = cfg.getActiveDatabases(loginContext);
		if (activeDatabases != null && !activeDatabases.isEmpty()) {

		}
		request.setAttribute("databases", activeDatabases);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("treeDataAdjust.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/treeDataAdjust/edit", modelMap);
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		TreeDataAdjustQuery query = new TreeDataAdjustQuery();
		Tools.populate(query, params);
		query.deleteFlag(0);
		query.setActorId(loginContext.getActorId());
		query.setLoginContext(loginContext);

		if (!loginContext.isSystemAdministrator()) {
			query.tenantId(loginContext.getTenantId());
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
		int total = treeDataAdjustService.getTreeDataAdjustCountByQueryCriteria(query);
		if (total > 0) {
			result.put("code", 0);
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

			List<TreeDataAdjust> list = treeDataAdjustService.getTreeDataAdjustsByQueryCriteria(start, limit, query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				for (TreeDataAdjust treeDataAdjust : list) {
					JSONObject rowJSON = treeDataAdjust.toJsonObject();
					rowJSON.put("id", treeDataAdjust.getId());
					rowJSON.put("treeDataAdjustId", treeDataAdjust.getId());
					rowJSON.put("startIndex", ++start);
					rowsJSON.add(rowJSON);
				}

				result.put("rows", rowsJSON);

			}
		} else {
			JSONArray rowsJSON = new JSONArray();
			result.put("rows", rowsJSON);
			result.put("total", total);
			result.put("code", 0);
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

		return new ModelAndView("/matrix/treeDataAdjust/list", modelMap);
	}

	@RequestMapping("/query")
	public ModelAndView query(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}
		String x_view = ViewProperties.getString("treeDataAdjust.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/matrix/treeDataAdjust/query", modelMap);
	}

	@ResponseBody
	@RequestMapping("/save")
	public byte[] save(HttpServletRequest request, HttpServletResponse response) {
		String targetTableName = request.getParameter("targetTableName");
		if (StringUtils.isNotEmpty(targetTableName)) {
			if (!(StringUtils.startsWithIgnoreCase(targetTableName, "etl_")
					|| StringUtils.startsWithIgnoreCase(targetTableName, "tmp")
					|| StringUtils.startsWithIgnoreCase(targetTableName, "sync")
					|| StringUtils.startsWithIgnoreCase(targetTableName, "useradd")
					|| StringUtils.startsWithIgnoreCase(targetTableName, "tree_table_"))) {
				return ResponseUtils.responseJsonResult(false, "目标表必须以etl_,tmp,sync,useradd,tree_table_前缀开头。");
			}
		}
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String actorId = loginContext.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		TreeDataAdjust treeDataAdjust = new TreeDataAdjust();
		try {
			Tools.populate(treeDataAdjust, params);
			treeDataAdjust.setName(request.getParameter("name"));
			treeDataAdjust.setTitle(request.getParameter("title"));
			treeDataAdjust.setDatabaseId(RequestUtils.getLong(request, "databaseId"));
			treeDataAdjust.setTableName(request.getParameter("tableName"));
			treeDataAdjust.setPrimaryKey(request.getParameter("primaryKey"));
			treeDataAdjust.setIdColumn(request.getParameter("idColumn"));
			treeDataAdjust.setParentIdColumn(request.getParameter("parentIdColumn"));
			treeDataAdjust.setTreeIdColumn(request.getParameter("treeIdColumn"));
			treeDataAdjust.setTreeIdDelimiter(request.getParameter("treeIdDelimiter"));
			treeDataAdjust.setNameColumn(request.getParameter("nameColumn"));
			treeDataAdjust.setAdjustColumn(request.getParameter("adjustColumn"));
			treeDataAdjust.setAdjustType(request.getParameter("adjustType"));
			treeDataAdjust.setConnector(request.getParameter("connector"));
			treeDataAdjust.setExpression(request.getParameter("expression"));
			treeDataAdjust.setSqlCriteria(request.getParameter("sqlCriteria"));
			treeDataAdjust.setTargetTableName(targetTableName);
			treeDataAdjust.setUpdateFlag(request.getParameter("updateFlag"));
			treeDataAdjust.setDeleteFetch(request.getParameter("deleteFetch"));
			treeDataAdjust.setLeafLimitFlag(request.getParameter("leafLimitFlag"));
			treeDataAdjust.setForkJoinFlag(request.getParameter("forkJoinFlag"));
			treeDataAdjust.setPreprocessFlag(request.getParameter("preprocessFlag"));
			treeDataAdjust.setScheduleFlag(request.getParameter("scheduleFlag"));
			treeDataAdjust.setType(request.getParameter("type"));
			treeDataAdjust.setLocked(RequestUtils.getInt(request, "locked"));
			treeDataAdjust.setCreateBy(actorId);
			treeDataAdjust.setUpdateBy(actorId);

			this.treeDataAdjustService.save(treeDataAdjust);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@ResponseBody
	@RequestMapping("/saveAs")
	public byte[] saveAs(HttpServletRequest request, HttpServletResponse response) {
		String targetTableName = request.getParameter("targetTableName");
		if (StringUtils.isNotEmpty(targetTableName)) {
			if (!(StringUtils.startsWithIgnoreCase(targetTableName, "etl_")
					|| StringUtils.startsWithIgnoreCase(targetTableName, "tmp")
					|| StringUtils.startsWithIgnoreCase(targetTableName, "sync")
					|| StringUtils.startsWithIgnoreCase(targetTableName, "useradd")
					|| StringUtils.startsWithIgnoreCase(targetTableName, "tree_table_"))) {
				return ResponseUtils.responseJsonResult(false, "目标表必须以etl_,tmp,sync,useradd,tree_table_前缀开头。");
			}
		}
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String actorId = loginContext.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		TreeDataAdjust treeDataAdjust = new TreeDataAdjust();
		try {
			Tools.populate(treeDataAdjust, params);
			treeDataAdjust.setName(request.getParameter("name"));
			treeDataAdjust.setTitle(request.getParameter("title"));
			treeDataAdjust.setDatabaseId(RequestUtils.getLong(request, "databaseId"));
			treeDataAdjust.setTableName(request.getParameter("tableName"));
			treeDataAdjust.setPrimaryKey(request.getParameter("primaryKey"));
			treeDataAdjust.setIdColumn(request.getParameter("idColumn"));
			treeDataAdjust.setParentIdColumn(request.getParameter("parentIdColumn"));
			treeDataAdjust.setTreeIdColumn(request.getParameter("treeIdColumn"));
			treeDataAdjust.setTreeIdDelimiter(request.getParameter("treeIdDelimiter"));
			treeDataAdjust.setNameColumn(request.getParameter("nameColumn"));
			treeDataAdjust.setAdjustColumn(request.getParameter("adjustColumn"));
			treeDataAdjust.setAdjustType(request.getParameter("adjustType"));
			treeDataAdjust.setConnector(request.getParameter("connector"));
			treeDataAdjust.setExpression(request.getParameter("expression"));
			treeDataAdjust.setSqlCriteria(request.getParameter("sqlCriteria"));
			treeDataAdjust.setTargetTableName(targetTableName);
			treeDataAdjust.setUpdateFlag(request.getParameter("updateFlag"));
			treeDataAdjust.setDeleteFetch(request.getParameter("deleteFetch"));
			treeDataAdjust.setLeafLimitFlag(request.getParameter("leafLimitFlag"));
			treeDataAdjust.setForkJoinFlag(request.getParameter("forkJoinFlag"));
			treeDataAdjust.setPreprocessFlag(request.getParameter("preprocessFlag"));
			treeDataAdjust.setScheduleFlag(request.getParameter("scheduleFlag"));
			treeDataAdjust.setType(request.getParameter("type"));
			treeDataAdjust.setLocked(RequestUtils.getInt(request, "locked"));
			treeDataAdjust.setCreateBy(actorId);
			treeDataAdjust.setUpdateBy(actorId);
			treeDataAdjust.setId(null);
			this.treeDataAdjustService.save(treeDataAdjust);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@ResponseBody
	@RequestMapping("/saveJson")
	public byte[] saveJson(HttpServletRequest request, HttpServletResponse response) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String actorId = loginContext.getActorId();
		String json = request.getParameter("json");
		TreeDataAdjust treeDataAdjust = null;
		try {
			if (StringUtils.isNotEmpty(json)) {
				JSONObject jsonObject = JSON.parseObject(json);
				treeDataAdjust = TreeDataAdjustJsonFactory.jsonToObject(jsonObject);
				treeDataAdjust.setCreateBy(actorId);
				treeDataAdjust.setUpdateBy(actorId);
				this.treeDataAdjustService.save(treeDataAdjust);

				return ResponseUtils.responseJsonResult(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource
	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.adjust.service.treeDataAdjustService")
	public void setTreeDataAdjustService(TreeDataAdjustService treeDataAdjustService) {
		this.treeDataAdjustService = treeDataAdjustService;
	}

}
