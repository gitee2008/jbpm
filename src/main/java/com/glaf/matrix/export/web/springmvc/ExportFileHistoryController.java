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
import java.util.Collection;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.glaf.core.security.LoginContext;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportFileHistory;
import com.glaf.matrix.export.query.ExportFileHistoryQuery;
import com.glaf.matrix.export.service.ExportAppService;
import com.glaf.matrix.export.service.ExportFileHistoryService;

/**
 * 
 * SpringMVC控制器
 *
 */

@Controller("/matrix/exportFileHistory")
@RequestMapping("/matrix/exportFileHistory")
public class ExportFileHistoryController {
	protected static final Log logger = LogFactory.getLog(ExportFileHistoryController.class);

	protected ExportAppService exportAppService;

	protected ExportFileHistoryService exportFileHistoryService;

	public ExportFileHistoryController() {

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
					ExportFileHistory exportFileHistory = exportFileHistoryService.getExportFileHistory(x);
					if (exportFileHistory != null
							&& (StringUtils.equals(exportFileHistory.getCreateBy(), loginContext.getActorId())
									|| loginContext.isSystemAdministrator())) {
						exportFileHistoryService.deleteById(x);
					}
				}
			}
			return ResponseUtils.responseResult(true);
		} else if (id != null) {
			ExportFileHistory exportFileHistory = exportFileHistoryService.getExportFileHistory(id);
			if (exportFileHistory != null
					&& (StringUtils.equals(exportFileHistory.getCreateBy(), loginContext.getActorId())
							|| loginContext.isSystemAdministrator())) {
				exportFileHistoryService.deleteById(id);
				return ResponseUtils.responseResult(true);
			}
		}
		return ResponseUtils.responseResult(false);
	}

	@ResponseBody
	@RequestMapping("/download")
	public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String id = request.getParameter("id");
		if (StringUtils.isNotEmpty(id)) {
			logger.debug("id:" + id);
			ExportFileHistory exportFileHistory = exportFileHistoryService.getExportFileHistory(id);
			if (exportFileHistory != null && exportFileHistory.getData() != null) {
				try {
					ExportApp exportApp = exportAppService.getExportApp(exportFileHistory.getExpId());

					boolean hasPerm = true;
					if (StringUtils.isNotEmpty(exportApp.getAllowRoles())) {
						hasPerm = false;
						List<String> roles = StringTools.split(exportApp.getAllowRoles());
						if (loginContext.isSystemAdministrator()) {
							hasPerm = true;
						}
						Collection<String> permissions = loginContext.getPermissions();
						for (String perm : permissions) {
							if (roles.contains(perm)) {
								hasPerm = true;
								break;
							}
						}
					}

					if (hasPerm) {
						ResponseUtils.download(request, response, exportFileHistory.getData(),
								exportFileHistory.getFilename());
					}
				} catch (Exception ex) {
				}
			}
		}
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		logger.debug("params:" + params);
		ExportFileHistoryQuery query = new ExportFileHistoryQuery();
		Tools.populate(query, params);
		query.deleteFlag(0);
		query.setActorId(loginContext.getActorId());
		query.setLoginContext(loginContext);

		if (!loginContext.isSystemAdministrator()) {
			query.tenantId(loginContext.getTenantId());
		}

		String expId = request.getParameter("expId");
		query.expId(expId);

		ExportApp exportApp = exportAppService.getExportApp(expId);

		boolean hasPerm = true;
		if (StringUtils.isNotEmpty(exportApp.getAllowRoles())) {
			hasPerm = false;
			List<String> roles = StringTools.split(exportApp.getAllowRoles());
			if (loginContext.isSystemAdministrator()) {
				hasPerm = true;
			}
			Collection<String> permissions = loginContext.getPermissions();
			for (String perm : permissions) {
				if (roles.contains(perm)) {
					hasPerm = true;
					break;
				}
			}
		}
		if (!hasPerm) {
			JSONObject result = new JSONObject();
			JSONArray rowsJSON = new JSONArray();
			result.put("rows", rowsJSON);
			result.put("total", 0);
			result.put("code", 0);
			return result.toJSONString().getBytes("UTF-8");
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
		int total = exportFileHistoryService.getExportFileHistoryCountByQueryCriteria(query);
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

			List<ExportFileHistory> list = exportFileHistoryService.getExportFileHistorysByQueryCriteria(start, limit,
					query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				for (ExportFileHistory exportFileHistory : list) {
					JSONObject rowJSON = exportFileHistory.toJsonObject();
					rowJSON.put("id", exportFileHistory.getId());
					rowJSON.put("historyId", exportFileHistory.getId());
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

		return new ModelAndView("/matrix/exportFileHistory/list", modelMap);
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.exportAppService")
	public void setExportAppService(ExportAppService exportAppService) {
		this.exportAppService = exportAppService;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.exportFileHistoryService")
	public void setExportFileHistoryService(ExportFileHistoryService exportFileHistoryService) {
		this.exportFileHistoryService = exportFileHistoryService;
	}

}
