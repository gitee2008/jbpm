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

package com.glaf.matrix.parameter.web.springmvc;

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
import com.glaf.core.config.ViewProperties;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.Tools;
import com.glaf.matrix.parameter.domain.ParameterConversion;
import com.glaf.matrix.parameter.query.ParameterConversionQuery;
import com.glaf.matrix.parameter.service.ParameterConversionService;
import com.glaf.matrix.parameter.util.ParameterConversionJsonFactory;

/**
 * 
 * SpringMVC控制器
 *
 */

@Controller("/matrix/parameterConversion")
@RequestMapping("/matrix/parameterConversion")
public class ParameterConversionController {
	protected static final Log logger = LogFactory.getLog(ParameterConversionController.class);

	protected ParameterConversionService parameterConversionService;

	public ParameterConversionController() {

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
					ParameterConversion parameterConversion = parameterConversionService
							.getParameterConversion(String.valueOf(x));
					if (parameterConversion != null
							&& (StringUtils.equals(parameterConversion.getCreateBy(), loginContext.getActorId())
									|| loginContext.isSystemAdministrator())) {
						parameterConversionService.deleteById(x);
					}
				}
			}
			return ResponseUtils.responseResult(true);
		} else if (id != null) {
			ParameterConversion parameterConversion = parameterConversionService
					.getParameterConversion(String.valueOf(id));
			if (parameterConversion != null
					&& (StringUtils.equals(parameterConversion.getCreateBy(), loginContext.getActorId())
							|| loginContext.isSystemAdministrator())) {
				parameterConversionService.deleteById(id);
				return ResponseUtils.responseResult(true);
			}
		}
		return ResponseUtils.responseResult(false);
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		ParameterConversion parameterConversion = parameterConversionService
				.getParameterConversion(request.getParameter("id"));
		if (parameterConversion != null) {
			request.setAttribute("parameterConversion", parameterConversion);
			request.setAttribute("key", parameterConversion.getKey());
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("parameterConversion.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/parameterConversion/edit", modelMap);
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ParameterConversionQuery query = new ParameterConversionQuery();
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
		int total = parameterConversionService.getParameterConversionCountByQueryCriteria(query);
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

			List<ParameterConversion> list = parameterConversionService.getParameterConversionsByQueryCriteria(start,
					limit, query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				for (ParameterConversion parameterConversion : list) {
					JSONObject rowJSON = parameterConversion.toJsonObject();
					rowJSON.put("id", parameterConversion.getId());
					rowJSON.put("parameterConversionId", parameterConversion.getId());
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

		return new ModelAndView("/matrix/parameterConversion/list", modelMap);
	}

	@RequestMapping("/query")
	public ModelAndView query(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}
		String x_view = ViewProperties.getString("parameterConversion.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/matrix/parameterConversion/query", modelMap);
	}

	@ResponseBody
	@RequestMapping("/save")
	public byte[] save(HttpServletRequest request, HttpServletResponse response) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		if (!loginContext.isSystemAdministrator()) {
			return ResponseUtils.responseJsonResult(false, "只有管理员才能操作");
		}
		String actorId = loginContext.getActorId();
		String json = request.getParameter("json");
		ParameterConversion parameterConversion = null;
		try {
			if (StringUtils.isNotEmpty(json)) {
				JSONObject jsonObject = JSON.parseObject(json);
				parameterConversion = ParameterConversionJsonFactory.jsonToObject(jsonObject);
				parameterConversion.setCreateBy(actorId);
				this.parameterConversionService.save(parameterConversion);

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
		if (!loginContext.isSystemAdministrator()) {
			return ResponseUtils.responseJsonResult(false, "只有管理员才能操作");
		}
		String actorId = loginContext.getActorId();
		String json = request.getParameter("json");
		ParameterConversion parameterConversion = null;
		try {
			if (StringUtils.isNotEmpty(json)) {
				JSONObject jsonObject = JSON.parseObject(json);
				parameterConversion = ParameterConversionJsonFactory.jsonToObject(jsonObject);
				parameterConversion.setId(null);
				parameterConversion.setCreateBy(actorId);
				this.parameterConversionService.save(parameterConversion);

				return ResponseUtils.responseJsonResult(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@ResponseBody
	@RequestMapping("/saveAsParameterConversion")
	public byte[] saveAsParameterConversion(HttpServletRequest request, HttpServletResponse response) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String actorId = loginContext.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ParameterConversion parameterConversion = new ParameterConversion();
		try {
			Tools.populate(parameterConversion, params);
			parameterConversion.setKey(request.getParameter("key"));
			parameterConversion.setTitle(request.getParameter("title"));
			parameterConversion.setSourceName(request.getParameter("sourceName"));
			parameterConversion.setSourceType(request.getParameter("sourceType"));
			parameterConversion.setSourceListFlag(request.getParameter("sourceListFlag"));
			parameterConversion.setTargetName(request.getParameter("targetName"));
			parameterConversion.setTargetType(request.getParameter("targetType"));
			parameterConversion.setTargetListFlag(request.getParameter("targetListFlag"));
			parameterConversion.setDelimiter(request.getParameter("delimiter"));
			parameterConversion.setConvertCondition(request.getParameter("convertCondition"));
			parameterConversion.setConvertType(request.getParameter("convertType"));
			parameterConversion.setConvertExpression(request.getParameter("convertExpression"));
			parameterConversion.setConvertTemplate(request.getParameter("convertTemplate"));
			parameterConversion.setCreateBy(actorId);
			parameterConversion.setId(null);
			this.parameterConversionService.save(parameterConversion);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@ResponseBody
	@RequestMapping("/saveParameterConversion")
	public byte[] saveParameterConversion(HttpServletRequest request, HttpServletResponse response) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String actorId = loginContext.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ParameterConversion parameterConversion = new ParameterConversion();
		try {
			Tools.populate(parameterConversion, params);
			parameterConversion.setKey(request.getParameter("key"));
			parameterConversion.setTitle(request.getParameter("title"));
			parameterConversion.setSourceName(request.getParameter("sourceName"));
			parameterConversion.setSourceType(request.getParameter("sourceType"));
			parameterConversion.setSourceListFlag(request.getParameter("sourceListFlag"));
			parameterConversion.setTargetName(request.getParameter("targetName"));
			parameterConversion.setTargetType(request.getParameter("targetType"));
			parameterConversion.setTargetListFlag(request.getParameter("targetListFlag"));
			parameterConversion.setDelimiter(request.getParameter("delimiter"));
			parameterConversion.setConvertCondition(request.getParameter("convertCondition"));
			parameterConversion.setConvertType(request.getParameter("convertType"));
			parameterConversion.setConvertExpression(request.getParameter("convertExpression"));
			parameterConversion.setConvertTemplate(request.getParameter("convertTemplate"));
			parameterConversion.setCreateBy(actorId);

			this.parameterConversionService.save(parameterConversion);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.parameter.service.parameterConversionService")
	public void setParameterConversionService(ParameterConversionService parameterConversionService) {
		this.parameterConversionService = parameterConversionService;
	}

}
