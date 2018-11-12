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

package com.glaf.jbpm.web.springmvc;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.Tools;
import com.glaf.jbpm.context.Context;
import com.glaf.jbpm.factory.ProcessFactory;
import com.glaf.jbpm.manager.JbpmProcessManager;
import com.glaf.jbpm.query.ProcessQuery;

@Controller("/jbpm/processInstances")
@RequestMapping("/jbpm/processInstances")
public class JbpmProcessController {
	protected final static Log logger = LogFactory.getLog(JbpmProcessController.class);

	public JbpmProcessController() {

	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		Map<String, Object> params = RequestUtils.getParameterMap(request);

		int start = 0;
		int limit = 10;

		int pageNo = ParamUtils.getInt(params, "page");
		limit = ParamUtils.getInt(params, "rows");
		start = (pageNo - 1) * limit;

		if (start < 0) {
			start = 0;
		}

		if (limit <= 0) {
			limit = Paging.DEFAULT_PAGE_SIZE;
		}

		JSONObject result = new JSONObject();
		int total = 0;

		int currPageNo = 1;
		if (start > 0 && limit > 0) {
			currPageNo = start / limit + 1;
		}

		ProcessQuery query = new ProcessQuery();
		Tools.populate(query, params);
		Paging jpage = null;
		JbpmContext jbpmContext = null;
		try {
			jbpmContext = ProcessFactory.getContainer().createJbpmContext();
			JbpmProcessManager jbpmProcessManager = ProcessFactory.getContainer().getJbpmProcessManager();
			jpage = jbpmProcessManager.getPageProcessInstances(jbpmContext, currPageNo, limit, query);
			if (jpage.getRows() != null && jpage.getRows().size() > 0) {
				total = jpage.getTotal();
				result.put("code", 0);
				result.put("total", total);
				result.put("totalCount", total);
				result.put("totalRecords", total);
				result.put("start", start);
				result.put("startIndex", start);
				result.put("limit", limit);
				result.put("pageSize", limit);
				int startIndex = start;
				JSONArray rowsJSON = new JSONArray();
				Iterator<Object> iterator = jpage.getRows().iterator();
				while (iterator.hasNext()) {
					ProcessInstance processInstance = (ProcessInstance) iterator.next();
					JSONObject json = new JSONObject();
					json.put("id", processInstance.getId());
					json.put("key", processInstance.getKey());
					if (processInstance.isSuspended()) {
						json.put("isSuspended", "true");
						json.put("isEnd", "false");
					} else {
						json.put("isEnd", "false");
						json.put("isSuspended", "false");
					}
					if (processInstance.getStart() != null) {
						json.put("startDate", DateUtils.getDateTime(processInstance.getStart()));
					}
					if (processInstance.getEnd() != null) {
						json.put("isEnd", "true");
						json.put("isSuspended", "false");
						json.put("endDate", DateUtils.getDateTime(processInstance.getEnd()));
					}
					json.put("version", processInstance.getVersion());
					json.put("startIndex", ++startIndex);
					rowsJSON.add(json);
				}
				result.put("rows", rowsJSON);
			}
		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		} finally {
			Context.close(jbpmContext);
		}
		if (total > 0) {

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
		GraphSession graphSession = null;
		JbpmContext jbpmContext = null;
		try {
			jbpmContext = ProcessFactory.getContainer().createJbpmContext();
			graphSession = jbpmContext.getGraphSession();
			List<ProcessDefinition> processDefinitions = graphSession.findAllProcessDefinitions();
			modelMap.put("processDefinitions", processDefinitions);
		} catch (Exception ex) {
			logger.debug(ex);
		} finally {
			Context.close(jbpmContext);
		}

		String jx_view = request.getParameter("jx_view");

		if (StringUtils.isNotEmpty(jx_view)) {
			return new ModelAndView(jx_view, modelMap);
		}

		String x_view = ViewProperties.getString("jbpm_process.list");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/jbpm/process/list", modelMap);
	}

	@RequestMapping("/resume")
	@ResponseBody
	public byte[] resume(HttpServletRequest request) {
		Map<String, Object> paramMap = RequestUtils.getParameterMap(request);
		long processInstanceId = ParamUtils.getLong(paramMap, "processInstanceId");
		logger.debug("processInstanceId="+processInstanceId);
		if (processInstanceId > 0) {
			try {
				ProcessFactory.getContainer().resume(processInstanceId);
				logger.info("流程" + processInstanceId + "已经恢复。");
				return ResponseUtils.responseResult(true);
			} catch (Exception ex) {
				logger.error(ex);
			}
		}
		return ResponseUtils.responseResult(false);
	}

	@RequestMapping("/suspend")
	@ResponseBody
	public byte[] suspend(HttpServletRequest request) {
		Map<String, Object> paramMap = RequestUtils.getParameterMap(request);
		long processInstanceId = ParamUtils.getLong(paramMap, "processInstanceId");
		logger.debug("processInstanceId="+processInstanceId);
		if (processInstanceId > 0) {
			try {
				ProcessFactory.getContainer().suspend(processInstanceId);
				logger.info("流程" + processInstanceId + "已经挂起。");
				return ResponseUtils.responseResult(true);
			} catch (Exception ex) {
				logger.error(ex);
			}
		}
		return ResponseUtils.responseResult(false);
	}
}