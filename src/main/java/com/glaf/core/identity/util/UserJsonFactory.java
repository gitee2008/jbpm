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

package com.glaf.core.identity.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.core.identity.User;
import com.glaf.core.identity.impl.UserImpl;
import com.glaf.core.util.DateUtils;

public class UserJsonFactory {

	public static User jsonToObject(JSONObject jsonObject) {
		UserImpl model = new UserImpl();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getIntValue("id"));
		}
		if (jsonObject.containsKey("actorId")) {
			model.setActorId(jsonObject.getString("actorId"));
		}

		if (jsonObject.containsKey("account")) {
			model.setActorId(jsonObject.getString("account"));
		}
		if (jsonObject.containsKey("name")) {
			model.setName(jsonObject.getString("name"));
		}

		if (jsonObject.containsKey("userType")) {
			model.setUserType(jsonObject.getIntValue("userType"));
		}

		if (jsonObject.containsKey("accountType")) {
			model.setAccountType(jsonObject.getIntValue("accountType"));
		}

		if (jsonObject.containsKey("createDate")) {
			model.setCreateDate(jsonObject.getDate("createDate"));
		}

		if (jsonObject.containsKey("tenantId")) {
			model.setTenantId(jsonObject.getString("tenantId"));
		}

		if (jsonObject.containsKey("adminFlag")) {
			model.setAdminFlag(jsonObject.getString("adminFlag"));
		}

		if (jsonObject.containsKey("remark")) {
			model.setRemark(jsonObject.getString("remark"));
		}

		if (jsonObject.containsKey("organizationId")) {
			model.setOrganizationId(jsonObject.getInteger("organizationId"));
		}
		if (jsonObject.containsKey("isBind")) {
			model.setIsBind(jsonObject.getString("isBind"));
		}
		if (jsonObject.containsKey("computerId")) {
			model.setComputerId(jsonObject.getString("computerId"));
		}
		if (jsonObject.containsKey("mail")) {
			model.setMail(jsonObject.getString("mail"));
		}

		if (jsonObject.containsKey("mobile")) {
			model.setMobile(jsonObject.getString("mobile"));
		}

		if (jsonObject.containsKey("domainIndex")) {
			model.setDomainIndex(jsonObject.getInteger("domainIndex"));
		}

		if (jsonObject.containsKey("token")) {
			model.setToken(jsonObject.getString("token"));
		}

		return model;
	}

	public static JSONObject toJsonObject(User user) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", user.getId());
		jsonObject.put("actorId", user.getActorId());
		jsonObject.put("name", user.getName());
		jsonObject.put("locked", user.getLocked());
		jsonObject.put("organizationId", user.getOrganizationId());
		jsonObject.put("tenantId", user.getTenantId());
		jsonObject.put("accountType", user.getAccountType());
		jsonObject.put("userType", user.getUserType());
		jsonObject.put("loginRetry", user.getLoginRetry());
		jsonObject.put("fax", user.getFax());
		jsonObject.put("remark", user.getRemark());

		if (user.getMail() != null) {
			jsonObject.put("mail", user.getMail());
		}
		if (user.getMobile() != null) {
			jsonObject.put("mobile", user.getMobile());
		}
		if (user.getLastLoginDate() != null) {
			jsonObject.put("lastLoginDate", user.getLastLoginDate());
		}
		if (user.getLoginIP() != null) {
			jsonObject.put("loginIP", user.getLoginIP());
		}
		if (user.getToken() != null) {
			jsonObject.put("token", user.getToken());
		}

		return jsonObject;
	}

	public static ObjectNode toObjectNode(User user) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", user.getId());
		jsonObject.put("actorId", user.getActorId());
		jsonObject.put("name", user.getName());
		jsonObject.put("locked", user.getLocked());
		jsonObject.put("organizationId", user.getOrganizationId());
		jsonObject.put("tenantId", user.getTenantId());
		jsonObject.put("accountType", user.getAccountType());
		jsonObject.put("userType", user.getUserType());
		jsonObject.put("loginRetry", user.getLoginRetry());
		jsonObject.put("fax", user.getFax());
		jsonObject.put("remark", user.getRemark());

		if (user.getMail() != null) {
			jsonObject.put("mail", user.getMail());
		}
		if (user.getMobile() != null) {
			jsonObject.put("mobile", user.getMobile());
		}
		if (user.getLastLoginDate() != null) {
			jsonObject.put("lastLoginDate", DateUtils.getDateTime(user.getLastLoginDate()));
		}
		if (user.getLoginIP() != null) {
			jsonObject.put("loginIP", user.getLoginIP());
		}
		if (user.getToken() != null) {
			jsonObject.put("token", user.getToken());
		}
		return jsonObject;
	}

}
