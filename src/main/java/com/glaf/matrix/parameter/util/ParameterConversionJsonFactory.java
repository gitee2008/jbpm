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

package com.glaf.matrix.parameter.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.core.util.DateUtils;
import com.glaf.matrix.parameter.domain.ParameterConversion;

/**
 * 
 * JSON工厂类
 *
 */
public class ParameterConversionJsonFactory {

	public static ParameterConversion jsonToObject(JSONObject jsonObject) {
		ParameterConversion model = new ParameterConversion();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getString("id"));
		}
		if (jsonObject.containsKey("key")) {
			model.setKey(jsonObject.getString("key"));
		}
		if (jsonObject.containsKey("title")) {
			model.setTitle(jsonObject.getString("title"));
		}
		if (jsonObject.containsKey("sourceName")) {
			model.setSourceName(jsonObject.getString("sourceName"));
		}
		if (jsonObject.containsKey("sourceType")) {
			model.setSourceType(jsonObject.getString("sourceType"));
		}
		if (jsonObject.containsKey("sourceListFlag")) {
			model.setSourceListFlag(jsonObject.getString("sourceListFlag"));
		}
		if (jsonObject.containsKey("targetName")) {
			model.setTargetName(jsonObject.getString("targetName"));
		}
		if (jsonObject.containsKey("targetType")) {
			model.setTargetType(jsonObject.getString("targetType"));
		}
		if (jsonObject.containsKey("targetListFlag")) {
			model.setTargetListFlag(jsonObject.getString("targetListFlag"));
		}
		if (jsonObject.containsKey("delimiter")) {
			model.setDelimiter(jsonObject.getString("delimiter"));
		}
		if (jsonObject.containsKey("convertCondition")) {
			model.setConvertCondition(jsonObject.getString("convertCondition"));
		}
		if (jsonObject.containsKey("convertType")) {
			model.setConvertType(jsonObject.getString("convertType"));
		}
		if (jsonObject.containsKey("convertExpression")) {
			model.setConvertExpression(jsonObject.getString("convertExpression"));
		}
		if (jsonObject.containsKey("convertTemplate")) {
			model.setConvertTemplate(jsonObject.getString("convertTemplate"));
		}
		if (jsonObject.containsKey("createBy")) {
			model.setCreateBy(jsonObject.getString("createBy"));
		}
		if (jsonObject.containsKey("createTime")) {
			model.setCreateTime(jsonObject.getDate("createTime"));
		}

		return model;
	}

	public static JSONObject toJsonObject(ParameterConversion model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getKey() != null) {
			jsonObject.put("key", model.getKey());
		}
		if (model.getTitle() != null) {
			jsonObject.put("title", model.getTitle());
		}
		if (model.getSourceName() != null) {
			jsonObject.put("sourceName", model.getSourceName());
		}
		if (model.getSourceType() != null) {
			jsonObject.put("sourceType", model.getSourceType());
		}
		if (model.getSourceListFlag() != null) {
			jsonObject.put("sourceListFlag", model.getSourceListFlag());
		}
		if (model.getTargetName() != null) {
			jsonObject.put("targetName", model.getTargetName());
		}
		if (model.getTargetType() != null) {
			jsonObject.put("targetType", model.getTargetType());
		}
		if (model.getTargetListFlag() != null) {
			jsonObject.put("targetListFlag", model.getTargetListFlag());
		}
		if (model.getDelimiter() != null) {
			jsonObject.put("delimiter", model.getDelimiter());
		}
		if (model.getConvertCondition() != null) {
			jsonObject.put("convertCondition", model.getConvertCondition());
		}
		if (model.getConvertType() != null) {
			jsonObject.put("convertType", model.getConvertType());
		}
		if (model.getConvertExpression() != null) {
			jsonObject.put("convertExpression", model.getConvertExpression());
		}
		if (model.getConvertTemplate() != null) {
			jsonObject.put("convertTemplate", model.getConvertTemplate());
		}
		if (model.getCreateBy() != null) {
			jsonObject.put("createBy", model.getCreateBy());
		}
		if (model.getCreateTime() != null) {
			jsonObject.put("createTime", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_date", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_datetime", DateUtils.getDateTime(model.getCreateTime()));
		}
		return jsonObject;
	}

	public static ObjectNode toObjectNode(ParameterConversion model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getKey() != null) {
			jsonObject.put("key", model.getKey());
		}
		if (model.getTitle() != null) {
			jsonObject.put("title", model.getTitle());
		}
		if (model.getSourceName() != null) {
			jsonObject.put("sourceName", model.getSourceName());
		}
		if (model.getSourceType() != null) {
			jsonObject.put("sourceType", model.getSourceType());
		}
		if (model.getSourceListFlag() != null) {
			jsonObject.put("sourceListFlag", model.getSourceListFlag());
		}
		if (model.getTargetName() != null) {
			jsonObject.put("targetName", model.getTargetName());
		}
		if (model.getTargetType() != null) {
			jsonObject.put("targetType", model.getTargetType());
		}
		if (model.getTargetListFlag() != null) {
			jsonObject.put("targetListFlag", model.getTargetListFlag());
		}
		if (model.getDelimiter() != null) {
			jsonObject.put("delimiter", model.getDelimiter());
		}
		if (model.getConvertCondition() != null) {
			jsonObject.put("convertCondition", model.getConvertCondition());
		}
		if (model.getConvertType() != null) {
			jsonObject.put("convertType", model.getConvertType());
		}
		if (model.getConvertExpression() != null) {
			jsonObject.put("convertExpression", model.getConvertExpression());
		}
		if (model.getConvertTemplate() != null) {
			jsonObject.put("convertTemplate", model.getConvertTemplate());
		}
		if (model.getCreateBy() != null) {
			jsonObject.put("createBy", model.getCreateBy());
		}
		if (model.getCreateTime() != null) {
			jsonObject.put("createTime", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_date", DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_datetime", DateUtils.getDateTime(model.getCreateTime()));
		}
		return jsonObject;
	}

	public static JSONArray listToArray(java.util.List<ParameterConversion> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (ParameterConversion model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static java.util.List<ParameterConversion> arrayToList(JSONArray array) {
		java.util.List<ParameterConversion> list = new java.util.ArrayList<ParameterConversion>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			ParameterConversion model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	private ParameterConversionJsonFactory() {

	}

}
