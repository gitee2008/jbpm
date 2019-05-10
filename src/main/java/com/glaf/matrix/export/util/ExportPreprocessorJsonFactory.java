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
public class ExportPreprocessorJsonFactory {

	public static ExportPreprocessor jsonToObject(JSONObject jsonObject) {
		ExportPreprocessor model = new ExportPreprocessor();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getString("id"));
		}
		if (jsonObject.containsKey("expId")) {
			model.setExpId(jsonObject.getString("expId"));
		}
		if (jsonObject.containsKey("currentStep")) {
			model.setCurrentStep(jsonObject.getString("currentStep"));
		}
		if (jsonObject.containsKey("currentType")) {
			model.setCurrentType(jsonObject.getString("currentType"));
		}
		if (jsonObject.containsKey("previousStep")) {
			model.setPreviousStep(jsonObject.getString("previousStep"));
		}
		if (jsonObject.containsKey("previousType")) {
			model.setPreviousType(jsonObject.getString("previousType"));
		}
		if (jsonObject.containsKey("nextStep")) {
			model.setNextStep(jsonObject.getString("nextStep"));
		}
		if (jsonObject.containsKey("nextType")) {
			model.setNextType(jsonObject.getString("nextType"));
		}
		if (jsonObject.containsKey("sort")) {
			model.setSort(jsonObject.getInteger("sort"));
		}

		return model;
	}

	public static JSONObject toJsonObject(ExportPreprocessor model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		jsonObject.put("expId", model.getExpId());

		if (model.getCurrentStep() != null) {
			jsonObject.put("currentStep", model.getCurrentStep());
		}
		if (model.getCurrentType() != null) {
			jsonObject.put("currentType", model.getCurrentType());
		}
		if (model.getPreviousStep() != null) {
			jsonObject.put("previousStep", model.getPreviousStep());
		}
		if (model.getPreviousType() != null) {
			jsonObject.put("previousType", model.getPreviousType());
		}
		if (model.getNextStep() != null) {
			jsonObject.put("nextStep", model.getNextStep());
		}
		if (model.getNextType() != null) {
			jsonObject.put("nextType", model.getNextType());
		}
		jsonObject.put("sort", model.getSort());
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

	public static ObjectNode toObjectNode(ExportPreprocessor model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		jsonObject.put("expId", model.getExpId());

		if (model.getCurrentStep() != null) {
			jsonObject.put("currentStep", model.getCurrentStep());
		}
		if (model.getCurrentType() != null) {
			jsonObject.put("currentType", model.getCurrentType());
		}
		if (model.getPreviousStep() != null) {
			jsonObject.put("previousStep", model.getPreviousStep());
		}
		if (model.getPreviousType() != null) {
			jsonObject.put("previousType", model.getPreviousType());
		}
		if (model.getNextStep() != null) {
			jsonObject.put("nextStep", model.getNextStep());
		}
		if (model.getNextType() != null) {
			jsonObject.put("nextType", model.getNextType());
		}
		jsonObject.put("sort", model.getSort());
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

	public static JSONArray listToArray(java.util.List<ExportPreprocessor> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (ExportPreprocessor model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static java.util.List<ExportPreprocessor> arrayToList(JSONArray array) {
		java.util.List<ExportPreprocessor> list = new java.util.ArrayList<ExportPreprocessor>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			ExportPreprocessor model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	private ExportPreprocessorJsonFactory() {

	}

}
