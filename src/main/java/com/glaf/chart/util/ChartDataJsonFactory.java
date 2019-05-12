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
package com.glaf.chart.util;

import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.glaf.chart.domain.*;

/**
 * 
 * JSON工厂类
 *
 */
public class ChartDataJsonFactory {

	public static ChartData jsonToObject(JSONObject jsonObject) {
		ChartData model = new ChartData();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getString("id"));
		}
		if (jsonObject.containsKey("serviceKey")) {
			model.setServiceKey(jsonObject.getString("serviceKey"));
		}
		if (jsonObject.containsKey("type")) {
			model.setType(jsonObject.getString("type"));
		}
		if (jsonObject.containsKey("category")) {
			model.setCategory(jsonObject.getString("category"));
		}
		if (jsonObject.containsKey("series")) {
			model.setSeries(jsonObject.getString("series"));
		}
		if (jsonObject.containsKey("value")) {
			model.setValue(jsonObject.getDouble("value"));
		}

		return model;
	}

	public static JSONObject toJsonObject(ChartData model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getServiceKey() != null) {
			jsonObject.put("serviceKey", model.getServiceKey());
		}
		if (model.getType() != null) {
			jsonObject.put("type", model.getType());
		}
		if (model.getCategory() != null) {
			jsonObject.put("category", model.getCategory());
		}
		if (model.getSeries() != null) {
			jsonObject.put("series", model.getSeries());
		}
		jsonObject.put("value", model.getValue());
		return jsonObject;
	}

	public static ObjectNode toObjectNode(ChartData model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getServiceKey() != null) {
			jsonObject.put("serviceKey", model.getServiceKey());
		}
		if (model.getType() != null) {
			jsonObject.put("type", model.getType());
		}
		if (model.getCategory() != null) {
			jsonObject.put("category", model.getCategory());
		}
		if (model.getSeries() != null) {
			jsonObject.put("series", model.getSeries());
		}
		jsonObject.put("value", model.getValue());
		return jsonObject;
	}

	public static JSONArray listToArray(java.util.List<ChartData> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (ChartData model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static java.util.List<ChartData> arrayToList(JSONArray array) {
		java.util.List<ChartData> list = new java.util.ArrayList<ChartData>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			ChartData model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	private ChartDataJsonFactory() {

	}

}
