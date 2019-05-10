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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.core.util.DateUtils;
import com.glaf.matrix.export.domain.ExportItem;

/**
 * 
 * JSON工厂类
 *
 */
public class ExportItemJsonFactory {

	public static ExportItem jsonToObject(JSONObject jsonObject) {
		ExportItem model = new ExportItem();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getString("id"));
		}
		if (jsonObject.containsKey("expId")) {
			model.setExpId(jsonObject.getString("expId"));
		}
		if (jsonObject.containsKey("deploymentId")) {
			model.setDeploymentId(jsonObject.getString("deploymentId"));
		}
		if (jsonObject.containsKey("name")) {
			model.setName(jsonObject.getString("name"));
		}
		if (jsonObject.containsKey("title")) {
			model.setTitle(jsonObject.getString("title"));
		}
		if (jsonObject.containsKey("datasetId")) {
			model.setDatasetId(jsonObject.getString("datasetId"));
		}
		if (jsonObject.containsKey("xmlExpId")) {
			model.setXmlExpId(jsonObject.getString("xmlExpId"));
		}
		if (jsonObject.containsKey("sql")) {
			model.setSql(jsonObject.getString("sql"));
		}
		if (jsonObject.containsKey("recursionSql")) {
			model.setRecursionSql(jsonObject.getString("recursionSql"));
		}
		if (jsonObject.containsKey("recursionColumns")) {
			model.setRecursionColumns(jsonObject.getString("recursionColumns"));
		}
		if (jsonObject.containsKey("primaryKey")) {
			model.setPrimaryKey(jsonObject.getString("primaryKey"));
		}
		if (jsonObject.containsKey("expression")) {
			model.setExpression(jsonObject.getString("expression"));
		}
		if (jsonObject.containsKey("fileFlag")) {
			model.setFileFlag(jsonObject.getString("fileFlag"));
		}
		if (jsonObject.containsKey("filePathColumn")) {
			model.setFilePathColumn(jsonObject.getString("filePathColumn"));
		}
		if (jsonObject.containsKey("fileNameColumn")) {
			model.setFileNameColumn(jsonObject.getString("fileNameColumn"));
		}
		if (jsonObject.containsKey("imageMergeFlag")) {
			model.setImageMergeFlag(jsonObject.getString("imageMergeFlag"));
		}
		if (jsonObject.containsKey("imageMergeDirection")) {
			model.setImageMergeDirection(jsonObject.getString("imageMergeDirection"));
		}
		if (jsonObject.containsKey("imageMergeTargetType")) {
			model.setImageMergeTargetType(jsonObject.getString("imageMergeTargetType"));
		}
		if (jsonObject.containsKey("imageWidth")) {
			model.setImageWidth(jsonObject.getInteger("imageWidth"));
		}
		if (jsonObject.containsKey("imageHeight")) {
			model.setImageHeight(jsonObject.getInteger("imageHeight"));
		}
		if (jsonObject.containsKey("imageScale")) {
			model.setImageScale(jsonObject.getDouble("imageScale"));
		}
		if (jsonObject.containsKey("imageScaleSize")) {
			model.setImageScaleSize(jsonObject.getDouble("imageScaleSize"));
		}
		if (jsonObject.containsKey("imageNumPerUnit")) {
			model.setImageNumPerUnit(jsonObject.getInteger("imageNumPerUnit"));
		}
		if (jsonObject.containsKey("rootPath")) {
			model.setRootPath(jsonObject.getString("rootPath"));
		}
		if (jsonObject.containsKey("varTemplate")) {
			model.setVarTemplate(jsonObject.getString("varTemplate"));
		}
		if (jsonObject.containsKey("variantFlag")) {
			model.setVariantFlag(jsonObject.getString("variantFlag"));
		}
		if (jsonObject.containsKey("lineBreakColumn")) {
			model.setLineBreakColumn(jsonObject.getString("lineBreakColumn"));
		}
		if (jsonObject.containsKey("lineHeight")) {
			model.setLineHeight(jsonObject.getInteger("lineHeight"));
		}
		if (jsonObject.containsKey("charNumPerRow")) {
			model.setCharNumPerRow(jsonObject.getInteger("charNumPerRow"));
		}

		if (jsonObject.containsKey("contextVarFlag")) {
			model.setContextVarFlag(jsonObject.getString("contextVarFlag"));
		}
		if (jsonObject.containsKey("genEmptyFlag")) {
			model.setGenEmptyFlag(jsonObject.getString("genEmptyFlag"));
		}
		if (jsonObject.containsKey("resultFlag")) {
			model.setResultFlag(jsonObject.getString("resultFlag"));
		}
		if (jsonObject.containsKey("dataHandlerChains")) {
			model.setDataHandlerChains(jsonObject.getString("dataHandlerChains"));
		}
		if (jsonObject.containsKey("subTotalFlag")) {
			model.setSubTotalFlag(jsonObject.getString("subTotalFlag"));
		}
		if (jsonObject.containsKey("subTotalColumn")) {
			model.setSubTotalColumn(jsonObject.getString("subTotalColumn"));
		}
		if (jsonObject.containsKey("pageSize")) {
			model.setPageSize(jsonObject.getInteger("pageSize"));
		}
		if (jsonObject.containsKey("sortNo")) {
			model.setSortNo(jsonObject.getInteger("sortNo"));
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

	public static JSONObject toJsonObject(ExportItem model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		jsonObject.put("expId", model.getExpId());
		jsonObject.put("sortNo", model.getSortNo());
		jsonObject.put("locked", model.getLocked());

		if (model.getDeploymentId() != null) {
			jsonObject.put("deploymentId", model.getDeploymentId());
		}
		if (model.getName() != null) {
			jsonObject.put("name", model.getName());
		}
		if (model.getTitle() != null) {
			jsonObject.put("title", model.getTitle());
		}
		if (model.getDatasetId() != null) {
			jsonObject.put("datasetId", model.getDatasetId());
		}
		if (model.getXmlExpId() != null) {
			jsonObject.put("xmlExpId", model.getXmlExpId());
		}
		if (model.getSql() != null) {
			jsonObject.put("sql", model.getSql());
		}
		if (model.getRecursionSql() != null) {
			jsonObject.put("recursionSql", model.getRecursionSql());
		}
		if (model.getRecursionColumns() != null) {
			jsonObject.put("recursionColumns", model.getRecursionColumns());
		}
		if (model.getPrimaryKey() != null) {
			jsonObject.put("primaryKey", model.getPrimaryKey());
		}
		if (model.getExpression() != null) {
			jsonObject.put("expression", model.getExpression());
		}
		if (model.getFileFlag() != null) {
			jsonObject.put("fileFlag", model.getFileFlag());
		}
		if (model.getFilePathColumn() != null) {
			jsonObject.put("filePathColumn", model.getFilePathColumn());
		}
		if (model.getFileNameColumn() != null) {
			jsonObject.put("fileNameColumn", model.getFileNameColumn());
		}
		if (model.getImageMergeFlag() != null) {
			jsonObject.put("imageMergeFlag", model.getImageMergeFlag());
		}
		if (model.getImageMergeDirection() != null) {
			jsonObject.put("imageMergeDirection", model.getImageMergeDirection());
		}
		if (model.getImageMergeTargetType() != null) {
			jsonObject.put("imageMergeTargetType", model.getImageMergeTargetType());
		}
		jsonObject.put("imageWidth", model.getImageWidth());
		jsonObject.put("imageHeight", model.getImageHeight());
		jsonObject.put("imageScale", model.getImageScale());
		jsonObject.put("imageScaleSize", model.getImageScaleSize());
		jsonObject.put("imageNumPerUnit", model.getImageNumPerUnit());
		if (model.getRootPath() != null) {
			jsonObject.put("rootPath", model.getRootPath());
		}
		if (model.getContextVarFlag() != null) {
			jsonObject.put("contextVarFlag", model.getContextVarFlag());
		}
		if (model.getGenEmptyFlag() != null) {
			jsonObject.put("genEmptyFlag", model.getGenEmptyFlag());
		}
		if (model.getResultFlag() != null) {
			jsonObject.put("resultFlag", model.getResultFlag());
		}
		if (model.getDataHandlerChains() != null) {
			jsonObject.put("dataHandlerChains", model.getDataHandlerChains());
		}
		if (model.getSubTotalFlag() != null) {
			jsonObject.put("subTotalFlag", model.getSubTotalFlag());
		}
		if (model.getSubTotalColumn() != null) {
			jsonObject.put("subTotalColumn", model.getSubTotalColumn());
		}
		if (model.getVarTemplate() != null) {
			jsonObject.put("varTemplate", model.getVarTemplate());
		}
		if (model.getVariantFlag() != null) {
			jsonObject.put("variantFlag", model.getVariantFlag());
		}
		if (model.getLineBreakColumn() != null) {
			jsonObject.put("lineBreakColumn", model.getLineBreakColumn());
		}
		jsonObject.put("lineHeight", model.getLineHeight());
		jsonObject.put("charNumPerRow", model.getCharNumPerRow());
		jsonObject.put("pageSize", model.getPageSize());
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

	public static ObjectNode toObjectNode(ExportItem model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		jsonObject.put("expId", model.getExpId());
		jsonObject.put("sortNo", model.getSortNo());
		jsonObject.put("locked", model.getLocked());

		if (model.getDeploymentId() != null) {
			jsonObject.put("deploymentId", model.getDeploymentId());
		}
		if (model.getName() != null) {
			jsonObject.put("name", model.getName());
		}
		if (model.getTitle() != null) {
			jsonObject.put("title", model.getTitle());
		}
		if (model.getDatasetId() != null) {
			jsonObject.put("datasetId", model.getDatasetId());
		}
		if (model.getXmlExpId() != null) {
			jsonObject.put("xmlExpId", model.getXmlExpId());
		}
		if (model.getSql() != null) {
			jsonObject.put("sql", model.getSql());
		}
		if (model.getRecursionSql() != null) {
			jsonObject.put("recursionSql", model.getRecursionSql());
		}
		if (model.getRecursionColumns() != null) {
			jsonObject.put("recursionColumns", model.getRecursionColumns());
		}
		if (model.getPrimaryKey() != null) {
			jsonObject.put("primaryKey", model.getPrimaryKey());
		}
		if (model.getExpression() != null) {
			jsonObject.put("expression", model.getExpression());
		}
		if (model.getFileFlag() != null) {
			jsonObject.put("fileFlag", model.getFileFlag());
		}
		if (model.getFilePathColumn() != null) {
			jsonObject.put("filePathColumn", model.getFilePathColumn());
		}
		if (model.getFileNameColumn() != null) {
			jsonObject.put("fileNameColumn", model.getFileNameColumn());
		}
		if (model.getImageMergeFlag() != null) {
			jsonObject.put("imageMergeFlag", model.getImageMergeFlag());
		}
		if (model.getImageMergeDirection() != null) {
			jsonObject.put("imageMergeDirection", model.getImageMergeDirection());
		}
		if (model.getImageMergeTargetType() != null) {
			jsonObject.put("imageMergeTargetType", model.getImageMergeTargetType());
		}
		jsonObject.put("imageWidth", model.getImageWidth());
		jsonObject.put("imageHeight", model.getImageHeight());
		jsonObject.put("imageScale", model.getImageScale());
		jsonObject.put("imageScaleSize", model.getImageScaleSize());
		jsonObject.put("imageNumPerUnit", model.getImageNumPerUnit());
		if (model.getRootPath() != null) {
			jsonObject.put("rootPath", model.getRootPath());
		}
		if (model.getLineBreakColumn() != null) {
			jsonObject.put("lineBreakColumn", model.getLineBreakColumn());
		}
		jsonObject.put("lineHeight", model.getLineHeight());
		jsonObject.put("charNumPerRow", model.getCharNumPerRow());
		jsonObject.put("pageSize", model.getPageSize());

		if (model.getContextVarFlag() != null) {
			jsonObject.put("contextVarFlag", model.getContextVarFlag());
		}
		if (model.getGenEmptyFlag() != null) {
			jsonObject.put("genEmptyFlag", model.getGenEmptyFlag());
		}
		if (model.getResultFlag() != null) {
			jsonObject.put("resultFlag", model.getResultFlag());
		}
		if (model.getDataHandlerChains() != null) {
			jsonObject.put("dataHandlerChains", model.getDataHandlerChains());
		}
		if (model.getSubTotalFlag() != null) {
			jsonObject.put("subTotalFlag", model.getSubTotalFlag());
		}
		if (model.getSubTotalColumn() != null) {
			jsonObject.put("subTotalColumn", model.getSubTotalColumn());
		}
		if (model.getVariantFlag() != null) {
			jsonObject.put("variantFlag", model.getVariantFlag());
		}

		if (model.getVarTemplate() != null) {
			jsonObject.put("varTemplate", model.getVarTemplate());
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

	public static JSONArray listToArray(java.util.List<ExportItem> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (ExportItem model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static java.util.List<ExportItem> arrayToList(JSONArray array) {
		java.util.List<ExportItem> list = new java.util.ArrayList<ExportItem>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			ExportItem model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	private ExportItemJsonFactory() {

	}

}
