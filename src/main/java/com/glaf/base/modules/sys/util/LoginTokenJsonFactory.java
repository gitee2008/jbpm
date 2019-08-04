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

package com.glaf.base.modules.sys.util;

import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.base.modules.sys.model.LoginToken;
import com.glaf.core.util.DateUtils;

/**
 * 
 * JSON工厂类
 *
 */
public class LoginTokenJsonFactory {

	public static LoginToken jsonToObject(JSONObject jsonObject) {
		LoginToken model = new LoginToken();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getString("id"));
		}
		if (jsonObject.containsKey("serverId")) {
			model.setServerId(jsonObject.getLong("serverId"));
		}
		if (jsonObject.containsKey("userId")) {
			model.setUserId(jsonObject.getString("userId"));
		}
		if (jsonObject.containsKey("clientIP")) {
			model.setClientIP(jsonObject.getString("clientIP"));
		}
		if (jsonObject.containsKey("signature")) {
			model.setSignature(jsonObject.getString("signature"));
		}
		if (jsonObject.containsKey("token")) {
			model.setToken(jsonObject.getString("token"));
		}
		if (jsonObject.containsKey("nonce")) {
			model.setNonce(jsonObject.getString("nonce"));
		}
		if (jsonObject.containsKey("sysCode")) {
			model.setSysCode(jsonObject.getString("sysCode"));
		}
		if (jsonObject.containsKey("loginModuleId")) {
			model.setLoginModuleId(jsonObject.getString("loginModuleId"));
		}
		if (jsonObject.containsKey("timeLive")) {
			model.setTimeLive(jsonObject.getInteger("timeLive"));
		}
		if (jsonObject.containsKey("timeMillis")) {
			model.setTimeMillis(jsonObject.getLong("timeMillis"));
		}
		if (jsonObject.containsKey("tenantId")) {
			model.setTenantId(jsonObject.getString("tenantId"));
		}
		if (jsonObject.containsKey("createTime")) {
			model.setCreateTime(jsonObject.getDate("createTime"));
		}

		return model;
	}

	public static JSONObject toJsonObject(LoginToken model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getServerId() != null) {
			jsonObject.put("serverId", model.getServerId());
		}
		if (model.getUserId() != null) {
			jsonObject.put("userId", model.getUserId());
		}
		if (model.getClientIP() != null) {
			jsonObject.put("clientIP", model.getClientIP());
		}
		if (model.getSignature() != null) {
			jsonObject.put("signature", model.getSignature());
		}
		if (model.getToken() != null) {
			jsonObject.put("token", model.getToken());
		}
		if (model.getNonce() != null) {
			jsonObject.put("nonce", model.getNonce());
		}
		if (model.getSysCode() != null) {
			jsonObject.put("sysCode", model.getSysCode());
		}
		if (model.getLoginModuleId() != null) {
			jsonObject.put("loginModuleId", model.getLoginModuleId());
		}
		if (model.getTenantId() != null) {
			jsonObject.put("tenantId", model.getTenantId());
		}
		jsonObject.put("timeLive", model.getTimeLive());
		jsonObject.put("timeMillis", model.getTimeMillis());
		if (model.getCreateTime() != null) {
			jsonObject.put("createTime", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_date", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_datetime", DateUtils.getDateTime(model.getCreateTime()));
		}
		return jsonObject;
	}

	public static ObjectNode toObjectNode(LoginToken model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getServerId() != null) {
			jsonObject.put("serverId", model.getServerId());
		}
		if (model.getUserId() != null) {
			jsonObject.put("userId", model.getUserId());
		}
		if (model.getClientIP() != null) {
			jsonObject.put("clientIP", model.getClientIP());
		}
		if (model.getSignature() != null) {
			jsonObject.put("signature", model.getSignature());
		}
		if (model.getToken() != null) {
			jsonObject.put("token", model.getToken());
		}
		if (model.getNonce() != null) {
			jsonObject.put("nonce", model.getNonce());
		}
		if (model.getSysCode() != null) {
			jsonObject.put("sysCode", model.getSysCode());
		}
		if (model.getLoginModuleId() != null) {
			jsonObject.put("loginModuleId", model.getLoginModuleId());
		}
		if (model.getTenantId() != null) {
			jsonObject.put("tenantId", model.getTenantId());
		}
		jsonObject.put("timeLive", model.getTimeLive());
		jsonObject.put("timeMillis", model.getTimeMillis());
		if (model.getCreateTime() != null) {
			jsonObject.put("createTime", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_date", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_datetime", DateUtils.getDateTime(model.getCreateTime()));
		}
		return jsonObject;
	}

	public static JSONArray listToArray(java.util.List<LoginToken> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (LoginToken model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static java.util.List<LoginToken> arrayToList(JSONArray array) {
		java.util.List<LoginToken> list = new java.util.ArrayList<LoginToken>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			LoginToken model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	private LoginTokenJsonFactory() {

	}

}
