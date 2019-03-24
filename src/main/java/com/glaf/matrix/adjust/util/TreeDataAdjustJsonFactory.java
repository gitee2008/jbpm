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

package com.glaf.matrix.adjust.util;

import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.glaf.core.util.DateUtils;
import com.glaf.matrix.adjust.domain.*;

/**
 * 
 * JSON工厂类
 *
 */
public class TreeDataAdjustJsonFactory {

	public static java.util.List<TreeDataAdjust> arrayToList(JSONArray array) {
		java.util.List<TreeDataAdjust> list = new java.util.ArrayList<TreeDataAdjust>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			TreeDataAdjust model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	public static TreeDataAdjust jsonToObject(JSONObject jsonObject) {
		TreeDataAdjust model = new TreeDataAdjust();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getString("id"));
		}
		if (jsonObject.containsKey("name")) {
			model.setName(jsonObject.getString("name"));
		}
		if (jsonObject.containsKey("title")) {
			model.setTitle(jsonObject.getString("title"));
		}
		if (jsonObject.containsKey("databaseId")) {
			model.setDatabaseId(jsonObject.getLong("databaseId"));
		}
		if (jsonObject.containsKey("tableName")) {
			model.setTableName(jsonObject.getString("tableName"));
		}
		if (jsonObject.containsKey("primaryKey")) {
			model.setPrimaryKey(jsonObject.getString("primaryKey"));
		}
		if (jsonObject.containsKey("idColumn")) {
			model.setIdColumn(jsonObject.getString("idColumn"));
		}
		if (jsonObject.containsKey("parentIdColumn")) {
			model.setParentIdColumn(jsonObject.getString("parentIdColumn"));
		}
		if (jsonObject.containsKey("treeIdColumn")) {
			model.setTreeIdColumn(jsonObject.getString("treeIdColumn"));
		}
		if (jsonObject.containsKey("treeIdDelimiter")) {
			model.setTreeIdDelimiter(jsonObject.getString("treeIdDelimiter"));
		}
		if (jsonObject.containsKey("nameColumn")) {
			model.setNameColumn(jsonObject.getString("nameColumn"));
		}
		if (jsonObject.containsKey("adjustColumn")) {
			model.setAdjustColumn(jsonObject.getString("adjustColumn"));
		}
		if (jsonObject.containsKey("adjustType")) {
			model.setAdjustType(jsonObject.getString("adjustType"));
		}
		if (jsonObject.containsKey("connector")) {
			model.setConnector(jsonObject.getString("connector"));
		}
		if (jsonObject.containsKey("expression")) {
			model.setExpression(jsonObject.getString("expression"));
		}
		if (jsonObject.containsKey("sqlCriteria")) {
			model.setSqlCriteria(jsonObject.getString("sqlCriteria"));
		}
		if (jsonObject.containsKey("targetTableName")) {
			model.setTargetTableName(jsonObject.getString("targetTableName"));
		}
		if (jsonObject.containsKey("updateFlag")) {
			model.setUpdateFlag(jsonObject.getString("updateFlag"));
		}
		if (jsonObject.containsKey("deleteFetch")) {
			model.setDeleteFetch(jsonObject.getString("deleteFetch"));
		}
		if (jsonObject.containsKey("leafLimitFlag")) {
			model.setLeafLimitFlag(jsonObject.getString("leafLimitFlag"));
		}
		if (jsonObject.containsKey("forkJoinFlag")) {
			model.setForkJoinFlag(jsonObject.getString("forkJoinFlag"));
		}
		if (jsonObject.containsKey("preprocessFlag")) {
			model.setPreprocessFlag(jsonObject.getString("preprocessFlag"));
		}
		if (jsonObject.containsKey("scheduleFlag")) {
			model.setScheduleFlag(jsonObject.getString("scheduleFlag"));
		}
		if (jsonObject.containsKey("type")) {
			model.setType(jsonObject.getString("type"));
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
		if (jsonObject.containsKey("updateBy")) {
			model.setUpdateBy(jsonObject.getString("updateBy"));
		}
		if (jsonObject.containsKey("updateTime")) {
			model.setUpdateTime(jsonObject.getDate("updateTime"));
		}

		return model;
	}

	public static JSONArray listToArray(java.util.List<TreeDataAdjust> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (TreeDataAdjust model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static JSONObject toJsonObject(TreeDataAdjust model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getName() != null) {
			jsonObject.put("name", model.getName());
		}
		if (model.getTitle() != null) {
			jsonObject.put("title", model.getTitle());
		}
		jsonObject.put("databaseId", model.getDatabaseId());
		if (model.getTableName() != null) {
			jsonObject.put("tableName", model.getTableName());
		}
		if (model.getPrimaryKey() != null) {
			jsonObject.put("primaryKey", model.getPrimaryKey());
		}
		if (model.getIdColumn() != null) {
			jsonObject.put("idColumn", model.getIdColumn());
		}
		if (model.getParentIdColumn() != null) {
			jsonObject.put("parentIdColumn", model.getParentIdColumn());
		}
		if (model.getTreeIdColumn() != null) {
			jsonObject.put("treeIdColumn", model.getTreeIdColumn());
		}
		if (model.getTreeIdDelimiter() != null) {
			jsonObject.put("treeIdDelimiter", model.getTreeIdDelimiter());
		}
		if (model.getNameColumn() != null) {
			jsonObject.put("nameColumn", model.getNameColumn());
		}
		if (model.getAdjustColumn() != null) {
			jsonObject.put("adjustColumn", model.getAdjustColumn());
		}
		if (model.getAdjustType() != null) {
			jsonObject.put("adjustType", model.getAdjustType());
		}
		if (model.getConnector() != null) {
			jsonObject.put("connector", model.getConnector());
		}
		if (model.getExpression() != null) {
			jsonObject.put("expression", model.getExpression());
		}
		if (model.getSqlCriteria() != null) {
			jsonObject.put("sqlCriteria", model.getSqlCriteria());
		}
		if (model.getTargetTableName() != null) {
			jsonObject.put("targetTableName", model.getTargetTableName());
		}
		if (model.getUpdateFlag() != null) {
			jsonObject.put("updateFlag", model.getUpdateFlag());
		}
		if (model.getDeleteFetch() != null) {
			jsonObject.put("deleteFetch", model.getDeleteFetch());
		}
		if (model.getLeafLimitFlag() != null) {
			jsonObject.put("leafLimitFlag", model.getLeafLimitFlag());
		}
		if (model.getForkJoinFlag() != null) {
			jsonObject.put("forkJoinFlag", model.getForkJoinFlag());
		}
		if (model.getPreprocessFlag() != null) {
			jsonObject.put("preprocessFlag", model.getPreprocessFlag());
		}
		if (model.getScheduleFlag() != null) {
			jsonObject.put("scheduleFlag", model.getScheduleFlag());
		}
		if (model.getType() != null) {
			jsonObject.put("type", model.getType());
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
		if (model.getUpdateBy() != null) {
			jsonObject.put("updateBy", model.getUpdateBy());
		}
		if (model.getUpdateTime() != null) {
			jsonObject.put("updateTime", DateUtils.getDate(model.getUpdateTime()));
			jsonObject.put("updateTime_date", DateUtils.getDate(model.getUpdateTime()));
			jsonObject.put("updateTime_datetime", DateUtils.getDateTime(model.getUpdateTime()));
		}
		return jsonObject;
	}

	public static ObjectNode toObjectNode(TreeDataAdjust model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getName() != null) {
			jsonObject.put("name", model.getName());
		}
		if (model.getTitle() != null) {
			jsonObject.put("title", model.getTitle());
		}
		jsonObject.put("databaseId", model.getDatabaseId());
		if (model.getTableName() != null) {
			jsonObject.put("tableName", model.getTableName());
		}
		if (model.getPrimaryKey() != null) {
			jsonObject.put("primaryKey", model.getPrimaryKey());
		}
		if (model.getIdColumn() != null) {
			jsonObject.put("idColumn", model.getIdColumn());
		}
		if (model.getParentIdColumn() != null) {
			jsonObject.put("parentIdColumn", model.getParentIdColumn());
		}
		if (model.getTreeIdColumn() != null) {
			jsonObject.put("treeIdColumn", model.getTreeIdColumn());
		}
		if (model.getTreeIdDelimiter() != null) {
			jsonObject.put("treeIdDelimiter", model.getTreeIdDelimiter());
		}
		if (model.getNameColumn() != null) {
			jsonObject.put("nameColumn", model.getNameColumn());
		}
		if (model.getAdjustColumn() != null) {
			jsonObject.put("adjustColumn", model.getAdjustColumn());
		}
		if (model.getAdjustType() != null) {
			jsonObject.put("adjustType", model.getAdjustType());
		}
		if (model.getExpression() != null) {
			jsonObject.put("expression", model.getExpression());
		}
		if (model.getSqlCriteria() != null) {
			jsonObject.put("sqlCriteria", model.getSqlCriteria());
		}
		if (model.getTargetTableName() != null) {
			jsonObject.put("targetTableName", model.getTargetTableName());
		}
		if (model.getUpdateFlag() != null) {
			jsonObject.put("updateFlag", model.getUpdateFlag());
		}
		if (model.getDeleteFetch() != null) {
			jsonObject.put("deleteFetch", model.getDeleteFetch());
		}
		if (model.getConnector() != null) {
			jsonObject.put("connector", model.getConnector());
		}
		if (model.getLeafLimitFlag() != null) {
			jsonObject.put("leafLimitFlag", model.getLeafLimitFlag());
		}
		if (model.getForkJoinFlag() != null) {
			jsonObject.put("forkJoinFlag", model.getForkJoinFlag());
		}
		if (model.getPreprocessFlag() != null) {
			jsonObject.put("preprocessFlag", model.getPreprocessFlag());
		}
		if (model.getScheduleFlag() != null) {
			jsonObject.put("scheduleFlag", model.getScheduleFlag());
		}
		if (model.getType() != null) {
			jsonObject.put("type", model.getType());
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
		if (model.getUpdateBy() != null) {
			jsonObject.put("updateBy", model.getUpdateBy());
		}
		if (model.getUpdateTime() != null) {
			jsonObject.put("updateTime", DateUtils.getDate(model.getUpdateTime()));
			jsonObject.put("updateTime_date", DateUtils.getDate(model.getUpdateTime()));
			jsonObject.put("updateTime_datetime", DateUtils.getDateTime(model.getUpdateTime()));
		}
		return jsonObject;
	}

	private TreeDataAdjustJsonFactory() {

	}

}
