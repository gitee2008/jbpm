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
import com.glaf.core.util.DateUtils;
import com.glaf.chart.domain.Chart;

public class ChartJsonFactory {

	public static java.util.List<Chart> arrayToList(JSONArray array) {
		java.util.List<Chart> list = new java.util.ArrayList<Chart>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			Chart model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	public static Chart jsonToObject(JSONObject jsonObject) {
		Chart model = new Chart();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getString("id"));
		}
		if (jsonObject.containsKey("nodeId")) {
			model.setNodeId(jsonObject.getLong("nodeId"));
		}
		if (jsonObject.containsKey("datasetIds")) {
			model.setDatasetIds(jsonObject.getString("datasetIds"));
		}
		if (jsonObject.containsKey("secondDatasetIds")) {
			model.setSecondDatasetIds(jsonObject.getString("secondDatasetIds"));
		}
		if (jsonObject.containsKey("thirdDatasetIds")) {
			model.setThirdDatasetIds(jsonObject.getString("thirdDatasetIds"));
		}
		if (jsonObject.containsKey("queryIds")) {
			model.setQueryIds(jsonObject.getString("queryIds"));
		}
		if (jsonObject.containsKey("querySQL")) {
			model.setQuerySQL(jsonObject.getString("querySQL"));
		}
		if (jsonObject.containsKey("subject")) {
			model.setSubject(jsonObject.getString("subject"));
		}
		if (jsonObject.containsKey("chartName")) {
			model.setChartName(jsonObject.getString("chartName"));
		}
		if (jsonObject.containsKey("chartType")) {
			model.setChartType(jsonObject.getString("chartType"));
		}
		if (jsonObject.containsKey("secondChartType")) {
			model.setSecondChartType(jsonObject.getString("secondChartType"));
		}
		if (jsonObject.containsKey("thirdChartType")) {
			model.setThirdChartType(jsonObject.getString("thirdChartType"));
		}
		if (jsonObject.containsKey("chartFont")) {
			model.setChartFont(jsonObject.getString("chartFont"));
		}
		if (jsonObject.containsKey("chartTitle")) {
			model.setChartTitle(jsonObject.getString("chartTitle"));
		}
		if (jsonObject.containsKey("chartSubTitle")) {
			model.setChartTitle(jsonObject.getString("chartSubTitle"));
		}
		if (jsonObject.containsKey("chartTitleFont")) {
			model.setChartTitleFont(jsonObject.getString("chartTitleFont"));
		}
		if (jsonObject.containsKey("chartTitleFontSize")) {
			model.setChartTitleFontSize(jsonObject.getInteger("chartTitleFontSize"));
		}
		if (jsonObject.containsKey("chartSubTitleFontSize")) {
			model.setChartSubTitleFontSize(jsonObject.getInteger("chartSubTitleFontSize"));
		}
		if (jsonObject.containsKey("legend")) {
			model.setLegend(jsonObject.getString("legend"));
		}
		if (jsonObject.containsKey("tooltip")) {
			model.setTooltip(jsonObject.getString("tooltip"));
		}
		if (jsonObject.containsKey("mapping")) {
			model.setMapping(jsonObject.getString("mapping"));
		}
		if (jsonObject.containsKey("coordinateX")) {
			model.setCoordinateX(jsonObject.getString("coordinateX"));
		}
		if (jsonObject.containsKey("coordinateY")) {
			model.setCoordinateY(jsonObject.getString("coordinateY"));
		}
		if (jsonObject.containsKey("secondCoordinateX")) {
			model.setSecondCoordinateX(jsonObject.getString("secondCoordinateX"));
		}
		if (jsonObject.containsKey("secondCoordinateY")) {
			model.setSecondCoordinateY(jsonObject.getString("secondCoordinateY"));
		}
		if (jsonObject.containsKey("plotOrientation")) {
			model.setPlotOrientation(jsonObject.getString("plotOrientation"));
		}
		if (jsonObject.containsKey("imageType")) {
			model.setImageType(jsonObject.getString("imageType"));
		}
		if (jsonObject.containsKey("theme")) {
			model.setTheme(jsonObject.getString("theme"));
		}
		if (jsonObject.containsKey("enableFlag")) {
			model.setEnableFlag(jsonObject.getString("enableFlag"));
		}
		if (jsonObject.containsKey("enable3DFlag")) {
			model.setEnable3DFlag(jsonObject.getString("enable3DFlag"));
		}
		if (jsonObject.containsKey("gradientFlag")) {
			model.setGradientFlag(jsonObject.getString("gradientFlag"));
		}
		if (jsonObject.containsKey("databaseId")) {
			model.setDatabaseId(jsonObject.getLong("databaseId"));
		}
		if (jsonObject.containsKey("maxRowCount")) {
			model.setMaxRowCount(jsonObject.getInteger("maxRowCount"));
		}
		if (jsonObject.containsKey("maxScale")) {
			model.setMaxScale(jsonObject.getDouble("maxScale"));
		}
		if (jsonObject.containsKey("minScale")) {
			model.setMinScale(jsonObject.getDouble("minScale"));
		}
		if (jsonObject.containsKey("stepScale")) {
			model.setStepScale(jsonObject.getDouble("stepScale"));
		}

		return model;
	}

	public static JSONArray listToArray(java.util.List<Chart> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (Chart model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static JSONObject toJsonObject(Chart model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getDatasetIds() != null) {
			jsonObject.put("datasetIds", model.getDatasetIds());
		}
		if (model.getSecondDatasetIds() != null) {
			jsonObject.put("secondDatasetIds", model.getSecondDatasetIds());
		}
		if (model.getThirdDatasetIds() != null) {
			jsonObject.put("thirdDatasetIds", model.getThirdDatasetIds());
		}
		if (model.getQueryIds() != null) {
			jsonObject.put("queryIds", model.getQueryIds());
		}
		if (model.getQuerySQL() != null) {
			jsonObject.put("querySQL", model.getQuerySQL());
		}
		if (model.getSubject() != null) {
			jsonObject.put("subject", model.getSubject());
		}
		if (model.getChartName() != null) {
			jsonObject.put("chartName", model.getChartName());
		}
		if (model.getChartType() != null) {
			jsonObject.put("chartType", model.getChartType());
		}
		if (model.getSecondChartType() != null) {
			jsonObject.put("secondChartType", model.getSecondChartType());
		}
		if (model.getThirdChartType() != null) {
			jsonObject.put("thirdChartType", model.getThirdChartType());
		}
		if (model.getChartFont() != null) {
			jsonObject.put("chartFont", model.getChartFont());
		}
		if (model.getChartTitle() != null) {
			jsonObject.put("chartTitle", model.getChartTitle());
		}
		if (model.getChartSubTitle() != null) {
			jsonObject.put("chartSubTitle", model.getChartSubTitle());
		}
		if (model.getChartTitleFont() != null) {
			jsonObject.put("chartTitleFont", model.getChartTitleFont());
		}
		if (model.getChartTitleFontSize() != 0) {
			jsonObject.put("chartTitleFontSize", model.getChartTitleFontSize());
		}
		if (model.getChartSubTitleFontSize() != 0) {
			jsonObject.put("chartSubTitleFontSize", model.getChartSubTitleFontSize());
		}
		if (model.getLegend() != null) {
			jsonObject.put("legend", model.getLegend());
		}
		if (model.getTooltip() != null) {
			jsonObject.put("tooltip", model.getTooltip());
		}
		if (model.getMapping() != null) {
			jsonObject.put("mapping", model.getMapping());
		}
		if (model.getCoordinateX() != null) {
			jsonObject.put("coordinateX", model.getCoordinateX());
		}
		if (model.getCoordinateY() != null) {
			jsonObject.put("coordinateY", model.getCoordinateY());
		}
		if (model.getSecondCoordinateX() != null) {
			jsonObject.put("secondCoordinateX", model.getSecondCoordinateX());
		}
		if (model.getSecondCoordinateY() != null) {
			jsonObject.put("secondCoordinateY", model.getSecondCoordinateY());
		}
		if (model.getPlotOrientation() != null) {
			jsonObject.put("plotOrientation", model.getPlotOrientation());
		}
		if (model.getImageType() != null) {
			jsonObject.put("imageType", model.getImageType());
		}
		if (model.getTheme() != null) {
			jsonObject.put("theme", model.getTheme());
		}
		if (model.getEnableFlag() != null) {
			jsonObject.put("enableFlag", model.getEnableFlag());
		}
		if (model.getEnable3DFlag() != null) {
			jsonObject.put("enable3DFlag", model.getEnable3DFlag());
		}
		if (model.getGradientFlag() != null) {
			jsonObject.put("gradientFlag", model.getGradientFlag());
		}
		jsonObject.put("databaseId", model.getDatabaseId());
		jsonObject.put("nodeId", model.getNodeId());
		jsonObject.put("maxRowCount", model.getMaxRowCount());
		jsonObject.put("maxScale", model.getMaxScale());
		jsonObject.put("minScale", model.getMinScale());
		jsonObject.put("stepScale", model.getStepScale());

		if (model.getCreateDate() != null) {
			jsonObject.put("createDate", DateUtils.getDate(model.getCreateDate()));
			jsonObject.put("createDate_date", DateUtils.getDate(model.getCreateDate()));
			jsonObject.put("createDate_datetime", DateUtils.getDateTime(model.getCreateDate()));
		}
		if (model.getCreateBy() != null) {
			jsonObject.put("createBy", model.getCreateBy());
		}
		return jsonObject;
	}

	public static ObjectNode toObjectNode(Chart model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getDatasetIds() != null) {
			jsonObject.put("datasetIds", model.getDatasetIds());
		}
		if (model.getSecondDatasetIds() != null) {
			jsonObject.put("secondDatasetIds", model.getSecondDatasetIds());
		}
		if (model.getThirdDatasetIds() != null) {
			jsonObject.put("thirdDatasetIds", model.getThirdDatasetIds());
		}
		if (model.getQueryIds() != null) {
			jsonObject.put("queryIds", model.getQueryIds());
		}
		if (model.getQuerySQL() != null) {
			jsonObject.put("querySQL", model.getQuerySQL());
		}
		if (model.getSubject() != null) {
			jsonObject.put("subject", model.getSubject());
		}
		if (model.getChartName() != null) {
			jsonObject.put("chartName", model.getChartName());
		}
		if (model.getChartType() != null) {
			jsonObject.put("chartType", model.getChartType());
		}
		if (model.getSecondChartType() != null) {
			jsonObject.put("secondChartType", model.getSecondChartType());
		}
		if (model.getThirdChartType() != null) {
			jsonObject.put("thirdChartType", model.getThirdChartType());
		}
		if (model.getChartFont() != null) {
			jsonObject.put("chartFont", model.getChartFont());
		}
		if (model.getChartTitle() != null) {
			jsonObject.put("chartTitle", model.getChartTitle());
		}
		if (model.getChartSubTitle() != null) {
			jsonObject.put("chartSubTitle", model.getChartSubTitle());
		}
		if (model.getChartTitleFont() != null) {
			jsonObject.put("chartTitleFont", model.getChartTitleFont());
		}
		if (model.getChartTitleFontSize() != 0) {
			jsonObject.put("chartTitleFontSize", model.getChartTitleFontSize());
		}
		if (model.getChartSubTitleFontSize() != 0) {
			jsonObject.put("chartSubTitleFontSize", model.getChartSubTitleFontSize());
		}
		if (model.getLegend() != null) {
			jsonObject.put("legend", model.getLegend());
		}
		if (model.getTooltip() != null) {
			jsonObject.put("tooltip", model.getTooltip());
		}
		if (model.getMapping() != null) {
			jsonObject.put("mapping", model.getMapping());
		}
		if (model.getCoordinateX() != null) {
			jsonObject.put("coordinateX", model.getCoordinateX());
		}
		if (model.getCoordinateY() != null) {
			jsonObject.put("coordinateY", model.getCoordinateY());
		}
		if (model.getSecondCoordinateX() != null) {
			jsonObject.put("secondCoordinateX", model.getSecondCoordinateX());
		}
		if (model.getSecondCoordinateY() != null) {
			jsonObject.put("secondCoordinateY", model.getSecondCoordinateY());
		}
		if (model.getPlotOrientation() != null) {
			jsonObject.put("plotOrientation", model.getPlotOrientation());
		}
		if (model.getImageType() != null) {
			jsonObject.put("imageType", model.getImageType());
		}
		if (model.getTheme() != null) {
			jsonObject.put("theme", model.getTheme());
		}
		if (model.getEnableFlag() != null) {
			jsonObject.put("enableFlag", model.getEnableFlag());
		}
		if (model.getEnable3DFlag() != null) {
			jsonObject.put("enable3DFlag", model.getEnable3DFlag());
		}
		if (model.getGradientFlag() != null) {
			jsonObject.put("gradientFlag", model.getGradientFlag());
		}
		jsonObject.put("databaseId", model.getDatabaseId());
		jsonObject.put("nodeId", model.getNodeId());
		jsonObject.put("maxRowCount", model.getMaxRowCount());
		jsonObject.put("maxScale", model.getMaxScale());
		jsonObject.put("minScale", model.getMinScale());
		jsonObject.put("stepScale", model.getStepScale());

		if (model.getCreateDate() != null) {
			jsonObject.put("createDate", DateUtils.getDate(model.getCreateDate()));
			jsonObject.put("createDate_date", DateUtils.getDate(model.getCreateDate()));
			jsonObject.put("createDate_datetime", DateUtils.getDateTime(model.getCreateDate()));
		}
		if (model.getCreateBy() != null) {
			jsonObject.put("createBy", model.getCreateBy());
		}
		return jsonObject;
	}

	private ChartJsonFactory() {

	}

}
