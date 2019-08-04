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

import com.glaf.core.util.DateUtils;
import com.glaf.base.modules.sys.model.LoginMessage;

/**
 * 
 * JSON工厂类
 *
 */
public class LoginMessageJsonFactory {

	public static LoginMessage jsonToObject(JSONObject jsonObject) {
		LoginMessage model = new LoginMessage();
		if (jsonObject.containsKey("serverId")) {
			model.setServerId(jsonObject.getLong("serverId"));
		}
		if (jsonObject.containsKey("userId")) {
			model.setUserId(jsonObject.getString("userId"));
		}
		if (jsonObject.containsKey("clientIP")) {
			model.setClientIP(jsonObject.getString("clientIP"));
		}
		if (jsonObject.containsKey("token")) {
			model.setToken(jsonObject.getString("token"));
		}
		if (jsonObject.containsKey("section")) {
			model.setSection(jsonObject.getString("section"));
		}
		if (jsonObject.containsKey("content")) {
			model.setContent(jsonObject.getString("content"));
		}
		if (jsonObject.containsKey("uid")) {
			model.setUid(jsonObject.getString("uid"));
		}
		if (jsonObject.containsKey("flowid")) {
			model.setFlowid(jsonObject.getString("flowid"));
		}
		if (jsonObject.containsKey("cellTreedotIndex")) {
			model.setCellTreedotIndex(jsonObject.getString("cellTreedotIndex"));
		}
		if (jsonObject.containsKey("position")) {
			model.setPosition(jsonObject.getString("position"));
		}
		if (jsonObject.containsKey("timeLive")) {
			model.setTimeLive(jsonObject.getInteger("timeLive"));
		}
		if (jsonObject.containsKey("tenantId")) {
			model.setTenantId(jsonObject.getString("tenantId"));
		}
		if (jsonObject.containsKey("loginTime")) {
			model.setLoginTime(jsonObject.getLong("loginTime"));
		}

		return model;
	}

	public static JSONObject toJsonObject(LoginMessage model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userId", model.getUserId());
		jsonObject.put("_userId_", model.getUserId());
		if (model.getServerId() != 0) {
			jsonObject.put("serverId", model.getServerId());
		}
		if (model.getClientIP() != null) {
			jsonObject.put("clientIP", model.getClientIP());
		}
		if (model.getToken() != null) {
			jsonObject.put("token", model.getToken());
		}
		if (model.getSection() != null) {
			jsonObject.put("section", model.getSection());
		}
		if (model.getUid() != null) {
			jsonObject.put("uid", model.getUid());
		}
		if (model.getFlowid() != null) {
			jsonObject.put("flowid", model.getFlowid());
		}
		if (model.getCellTreedotIndex() != null) {
			jsonObject.put("cellTreedotIndex", model.getCellTreedotIndex());
		}
		if (model.getPosition() != null) {
			jsonObject.put("position", model.getPosition());
		}
		if (model.getUid() != null) {
			jsonObject.put("uid", model.getUid());
		}
		if (model.getTenantId() != null) {
			jsonObject.put("tenantId", model.getTenantId());
		}
		jsonObject.put("timeLive", model.getTimeLive());
		jsonObject.put("loginTime", model.getLoginTime());
		if (model.getCreateTime() != null) {
			jsonObject.put("createTime", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_date", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_datetime", DateUtils.getDateTime(model.getCreateTime()));
		}
		return jsonObject;
	}

	public static ObjectNode toObjectNode(LoginMessage model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("userId", model.getUserId());
		jsonObject.put("_userId_", model.getUserId());
		if (model.getServerId() != 0) {
			jsonObject.put("serverId", model.getServerId());
		}
		if (model.getClientIP() != null) {
			jsonObject.put("clientIP", model.getClientIP());
		}
		if (model.getToken() != null) {
			jsonObject.put("token", model.getToken());
		}
		if (model.getSection() != null) {
			jsonObject.put("section", model.getSection());
		}
		if (model.getContent() != null) {
			jsonObject.put("content", model.getContent());
		}
		if (model.getUid() != null) {
			jsonObject.put("uid", model.getUid());
		}
		if (model.getFlowid() != null) {
			jsonObject.put("flowid", model.getFlowid());
		}
		if (model.getCellTreedotIndex() != null) {
			jsonObject.put("cellTreedotIndex", model.getCellTreedotIndex());
		}
		if (model.getPosition() != null) {
			jsonObject.put("position", model.getPosition());
		}
		if (model.getTenantId() != null) {
			jsonObject.put("tenantId", model.getTenantId());
		}
		jsonObject.put("timeLive", model.getTimeLive());
		jsonObject.put("loginTime", model.getLoginTime());
		if (model.getCreateTime() != null) {
			jsonObject.put("createTime", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_date", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_datetime", DateUtils.getDateTime(model.getCreateTime()));
		}
		return jsonObject;
	}

	public static JSONArray listToArray(java.util.List<LoginMessage> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (LoginMessage model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static java.util.List<LoginMessage> arrayToList(JSONArray array) {
		java.util.List<LoginMessage> list = new java.util.ArrayList<LoginMessage>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			LoginMessage model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	private LoginMessageJsonFactory() {

	}

}
