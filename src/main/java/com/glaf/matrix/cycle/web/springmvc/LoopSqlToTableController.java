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

package com.glaf.matrix.cycle.web.springmvc;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
import com.glaf.core.config.ViewProperties;
import com.glaf.core.domain.Database;
import com.glaf.core.query.DatabaseQuery;
import com.glaf.core.security.LoginContext;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.Tools;

import com.glaf.matrix.cycle.bean.LoopSqlToTableBatchBean;
import com.glaf.matrix.cycle.bean.LoopSqlToTableBean;
import com.glaf.matrix.cycle.domain.LoopSqlToTable;
import com.glaf.matrix.cycle.query.LoopSqlToTableQuery;
import com.glaf.matrix.cycle.service.LoopSqlToTableService;
import com.glaf.matrix.cycle.util.LoopSqlToTableJsonFactory;
import com.glaf.matrix.data.domain.ExecutionLog;
import com.glaf.matrix.data.service.ExecutionLogService;
import com.glaf.matrix.util.SysParams;

/**
 * 
 * SpringMVC控制器
 *
 */

@Controller("/sys/loopSqlToTable")
@RequestMapping("/sys/loopSqlToTable")
public class LoopSqlToTableController {
	protected static final Log logger = LogFactory.getLog(LoopSqlToTableController.class);

	protected static ConcurrentMap<String, String> concurrentMap = new ConcurrentHashMap<String, String>();

	protected IDatabaseService databaseService;

	protected LoopSqlToTableService loopSqlToTableService;

	protected ExecutionLogService executionLogService;

	public LoopSqlToTableController() {

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
					LoopSqlToTable loopSqlToTable = loopSqlToTableService.getLoopSqlToTable(String.valueOf(x));
					if (loopSqlToTable != null
							&& (StringUtils.equals(loopSqlToTable.getCreateBy(), loginContext.getActorId())
									|| loginContext.isSystemAdministrator())) {
						loopSqlToTableService.deleteById(x);
					}
				}
			}
			return ResponseUtils.responseResult(true);
		} else if (id != null) {
			LoopSqlToTable loopSqlToTable = loopSqlToTableService.getLoopSqlToTable(String.valueOf(id));
			if (loopSqlToTable != null && (StringUtils.equals(loopSqlToTable.getCreateBy(), loginContext.getActorId())
					|| loginContext.isSystemAdministrator())) {
				loopSqlToTableService.deleteById(id);
				return ResponseUtils.responseResult(true);
			}
		}
		return ResponseUtils.responseResult(false);
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		LoopSqlToTable loopSqlToTable = loopSqlToTableService.getLoopSqlToTable(request.getParameter("id"));
		if (loopSqlToTable != null) {
			request.setAttribute("loopSqlToTable", loopSqlToTable);
		}

		DatabaseQuery query = new DatabaseQuery();
		query.active("1");
		List<Database> databases = databaseService.list(query);
		request.setAttribute("databases", databases);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("loopSqlToTable.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/loopSqlToTable/edit", modelMap);
	}

	@ResponseBody
	@RequestMapping("/dropTable")
	public byte[] dropTable(HttpServletRequest request) {
		String syncId = RequestUtils.getString(request, "syncId");
		try {
			LoopSqlToTable app = loopSqlToTableService.getLoopSqlToTable(syncId);
			if (app != null && app.getLocked() == 0) {
				Database targetDatabase = databaseService.getDatabaseById(Long.parseLong(app.getTargetDatabaseId()));
				if (targetDatabase != null) {
					if (StringUtils.startsWithIgnoreCase(app.getTargetTableName(), "etl_")
							|| StringUtils.startsWithIgnoreCase(app.getTargetTableName(), "sync_")
							|| StringUtils.startsWithIgnoreCase(app.getTargetTableName(), "tmp")
							|| StringUtils.startsWithIgnoreCase(app.getTargetTableName(), "useradd_")
							|| StringUtils.startsWithIgnoreCase(app.getTargetTableName(), "tree_table_")) {
						if (DBUtils.tableExists(targetDatabase.getName(), app.getTargetTableName())) {
							String ddlStatements = " drop table " + app.getTargetTableName();
							logger.warn(ddlStatements);
							DBUtils.executeSchemaResource(targetDatabase.getName(), ddlStatements);
						}
					}
				}
				return ResponseUtils.responseJsonResult(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@ResponseBody
	@RequestMapping("/execute")
	public byte[] execute(HttpServletRequest request) {
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		SysParams.putInternalParams(params);
		String syncId = RequestUtils.getString(request, "syncId");
		if (concurrentMap.get(syncId) == null) {
			Connection srcConn = null;
			try {
				concurrentMap.put(syncId, syncId);
				LoopSqlToTable loopSqlToTable = loopSqlToTableService.getLoopSqlToTable(syncId);
				if (loopSqlToTable != null && loopSqlToTable.getLocked() == 0) {
					LoopSqlToTableBean bean = new LoopSqlToTableBean();
					LoopSqlToTableBatchBean batchBean = new LoopSqlToTableBatchBean();
					String content = "执行循环SQL到表[" + loopSqlToTable.getTitle() + "]";
					ExecutionLog executionLog = new ExecutionLog();
					executionLog.setBusinessKey("loop_sql_table_" + loopSqlToTable.getId());
					executionLog.setCreateBy(RequestUtils.getActorId(request));
					executionLog.setCreateTime(new Date());
					executionLog.setStartTime(new Date());
					executionLog.setType("loop_sql_table");
					executionLog.setTitle(loopSqlToTable.getTitle());
					long start = System.currentTimeMillis();
					boolean ret = false;
					if (StringUtils.equals(loopSqlToTable.getBatchFlag(), "Y")) {
						ret = batchBean.execute(loopSqlToTable.getId(), params);
					} else {
						ret = bean.execute(loopSqlToTable.getId(), params);
					}
					if (ret) {
						loopSqlToTable.setSyncStatus(9);
						executionLog.setStatus(9);
						content = content + " 执行成功！";
					} else {
						loopSqlToTable.setSyncStatus(-1);
						executionLog.setStatus(-1);
						content = content + " 执行失败！";
					}
					loopSqlToTable.setSyncTime(new Date());
					loopSqlToTableService.updateLoopSqlToTableSyncStatus(loopSqlToTable);
					executionLog.setRunTime(System.currentTimeMillis() - start);
					executionLog.setEndTime(new Date());
					executionLog.setContent(content);
					executionLogService.save(executionLog);

					return ResponseUtils.responseJsonResult(true);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.error(ex);
			} finally {
				concurrentMap.remove(syncId);
				JdbcUtils.close(srcConn);
			}
		} else {
			ResponseUtils.responseJsonResult(false, "已经有任务在执行中，请等待完成！");
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		LoopSqlToTableQuery query = new LoopSqlToTableQuery();
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
		int total = loopSqlToTableService.getLoopSqlToTableCountByQueryCriteria(query);
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

			List<LoopSqlToTable> list = loopSqlToTableService.getLoopSqlToTablesByQueryCriteria(start, limit, query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				for (LoopSqlToTable loopSqlToTable : list) {
					JSONObject rowJSON = loopSqlToTable.toJsonObject();
					rowJSON.put("id", loopSqlToTable.getId());
					rowJSON.put("loopSqlToTableId", loopSqlToTable.getId());
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

		return new ModelAndView("/matrix/loopSqlToTable/list", modelMap);
	}

	@RequestMapping("/query")
	public ModelAndView query(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}
		String x_view = ViewProperties.getString("loopSqlToTable.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/matrix/loopSqlToTable/query", modelMap);
	}

	@ResponseBody
	@RequestMapping("/save")
	public byte[] save(HttpServletRequest request, HttpServletResponse response) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String actorId = loginContext.getActorId();
		String json = request.getParameter("json");
		LoopSqlToTable loopSqlToTable = null;
		try {
			if (StringUtils.isNotEmpty(json)) {
				JSONObject jsonObject = JSON.parseObject(json);
				logger.debug(jsonObject.toJSONString());
				loopSqlToTable = LoopSqlToTableJsonFactory.jsonToObject(jsonObject);
				loopSqlToTable.setCreateBy(actorId);
				loopSqlToTable.setUpdateBy(actorId);
				this.loopSqlToTableService.save(loopSqlToTable);
				return ResponseUtils.responseJsonResult(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@ResponseBody
	@RequestMapping("/saveAs")
	public byte[] saveAs(HttpServletRequest request, HttpServletResponse response) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String actorId = loginContext.getActorId();
		String json = request.getParameter("json");
		LoopSqlToTable loopSqlToTable = null;
		try {
			if (StringUtils.isNotEmpty(json)) {
				JSONObject jsonObject = JSON.parseObject(json);
				logger.debug(jsonObject.toJSONString());
				loopSqlToTable = LoopSqlToTableJsonFactory.jsonToObject(jsonObject);
				loopSqlToTable.setCreateBy(actorId);
				loopSqlToTable.setUpdateBy(actorId);
				loopSqlToTable.setId(null);
				this.loopSqlToTableService.insert(loopSqlToTable);
				return ResponseUtils.responseJsonResult(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@ResponseBody
	@RequestMapping("/saveLoopSqlToTable")
	public byte[] saveLoopSqlToTable(HttpServletRequest request, HttpServletResponse response) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String actorId = loginContext.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		LoopSqlToTable loopSqlToTable = new LoopSqlToTable();
		try {
			Tools.populate(loopSqlToTable, params);
			loopSqlToTable.setName(request.getParameter("name"));
			loopSqlToTable.setTitle(request.getParameter("title"));
			loopSqlToTable.setType(request.getParameter("type"));
			loopSqlToTable.setSourceDatabaseId(request.getParameter("sourceDatabaseId"));
			loopSqlToTable.setPrimaryKey(request.getParameter("primaryKey"));
			loopSqlToTable.setSql(request.getParameter("sql"));
			loopSqlToTable.setTargetTableName(request.getParameter("targetTableName"));
			loopSqlToTable.setTargetDatabaseId(request.getParameter("targetDatabaseId"));
			loopSqlToTable.setRecentlyDay(RequestUtils.getInt(request, "recentlyDay"));
			loopSqlToTable.setStartDate(RequestUtils.getDate(request, "startDate"));
			loopSqlToTable.setStopDate(RequestUtils.getDate(request, "stopDate"));
			loopSqlToTable.setDeleteFetch(request.getParameter("deleteFetch"));
			loopSqlToTable.setSkipError(request.getParameter("skipError"));
			loopSqlToTable.setBatchFlag(request.getParameter("batchFlag"));
			loopSqlToTable.setScheduleFlag(request.getParameter("scheduleFlag"));
			loopSqlToTable.setSyncStatus(RequestUtils.getInt(request, "syncStatus"));
			loopSqlToTable.setSyncTime(RequestUtils.getDate(request, "syncTime"));
			loopSqlToTable.setSortNo(RequestUtils.getInt(request, "sortNo"));
			loopSqlToTable.setLocked(RequestUtils.getInt(request, "locked"));
			loopSqlToTable.setCreateBy(actorId);
			loopSqlToTable.setUpdateBy(actorId);

			this.loopSqlToTableService.save(loopSqlToTable);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.cycle.service.loopSqlToTableService")
	public void setLoopSqlToTableService(LoopSqlToTableService loopSqlToTableService) {
		this.loopSqlToTableService = loopSqlToTableService;
	}

	@javax.annotation.Resource
	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@javax.annotation.Resource
	public void setExecutionLogService(ExecutionLogService executionLogService) {
		this.executionLogService = executionLogService;
	}

}
