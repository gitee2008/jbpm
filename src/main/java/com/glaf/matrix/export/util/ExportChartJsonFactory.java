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

package com.glaf.matrix.export.util;

import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.glaf.core.util.DateUtils;
import com.glaf.matrix.export.domain.*;

/**
 * 
 * JSON工厂类
 *
 */
public class ExportChartJsonFactory {

	public static java.util.List<ExportChart> arrayToList(JSONArray array) {
		java.util.List<ExportChart> list = new java.util.ArrayList<ExportChart>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			ExportChart model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	public static ExportChart jsonToObject(JSONObject jsonObject) {
		ExportChart model = new ExportChart();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getString("id"));
		}
		if (jsonObject.containsKey("expId")) {
			model.setExpId(jsonObject.getString("expId"));
		}
		if (jsonObject.containsKey("name")) {
			model.setName(jsonObject.getString("name"));
		}
		if (jsonObject.containsKey("title")) {
			model.setTitle(jsonObject.getString("title"));
		}
		if (jsonObject.containsKey("height")) {
			model.setHeight(jsonObject.getInteger("height"));
		}
		if (jsonObject.containsKey("width")) {
			model.setWidth(jsonObject.getInteger("width"));
		}
		if (jsonObject.containsKey("imageType")) {
			model.setImageType(jsonObject.getString("imageType"));
		}
		if (jsonObject.containsKey("chartId")) {
			model.setChartId(jsonObject.getString("chartId"));
		}
		if (jsonObject.containsKey("chartType")) {
			model.setChartType(jsonObject.getString("chartType"));
		}
		if (jsonObject.containsKey("chartUrl")) {
			model.setChartUrl(jsonObject.getString("chartUrl"));
		}
		if (jsonObject.containsKey("snapshotFlag")) {
			model.setSnapshotFlag(jsonObject.getString("snapshotFlag"));
		}
		if (jsonObject.containsKey("locked")) {
			model.setLocked(jsonObject.getInteger("locked"));
		}
		if (jsonObject.containsKey("createBy")) {
			model.setCreateBy(jsonObject.getString("createBy"));
		}
		if (jsonObject.containsKey("createTime")) {
			model.setCreateTime(jsonObject.getDate("createTime"));
		}

		return model;
	}

	public static JSONArray listToArray(java.util.List<ExportChart> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (ExportChart model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static JSONObject toJsonObject(ExportChart model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getExpId() != null) {
			jsonObject.put("expId", model.getExpId());
		}
		if (model.getName() != null) {
			jsonObject.put("name", model.getName());
		}
		if (model.getTitle() != null) {
			jsonObject.put("title", model.getTitle());
		}
		jsonObject.put("height", model.getHeight());
		jsonObject.put("width", model.getWidth());
		if (model.getImageType() != null) {
			jsonObject.put("imageType", model.getImageType());
		}
		if (model.getChartId() != null) {
			jsonObject.put("chartId", model.getChartId());
		}
		if (model.getChartType() != null) {
			jsonObject.put("chartType", model.getChartType());
		}
		if (model.getChartUrl() != null) {
			jsonObject.put("chartUrl", model.getChartUrl());
		}
		if (model.getSnapshotFlag() != null) {
			jsonObject.put("snapshotFlag", model.getSnapshotFlag());
		}
		jsonObject.put("locked", model.getLocked());
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

	public static ObjectNode toObjectNode(ExportChart model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getExpId() != null) {
			jsonObject.put("expId", model.getExpId());
		}
		if (model.getName() != null) {
			jsonObject.put("name", model.getName());
		}
		if (model.getTitle() != null) {
			jsonObject.put("title", model.getTitle());
		}
		jsonObject.put("height", model.getHeight());
		jsonObject.put("width", model.getWidth());
		if (model.getImageType() != null) {
			jsonObject.put("imageType", model.getImageType());
		}
		if (model.getChartId() != null) {
			jsonObject.put("chartId", model.getChartId());
		}
		if (model.getChartType() != null) {
			jsonObject.put("chartType", model.getChartType());
		}
		if (model.getChartUrl() != null) {
			jsonObject.put("chartUrl", model.getChartUrl());
		}
		if (model.getSnapshotFlag() != null) {
			jsonObject.put("snapshotFlag", model.getSnapshotFlag());
		}
		jsonObject.put("locked", model.getLocked());
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

	private ExportChartJsonFactory() {

	}

}
