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

package com.glaf.test;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.glaf.core.util.UUID32;
import com.glaf.search.util.ElasticSearchUtils;

public class SearchTest {

	public static void main(String args[]) {
		String server = "http://192.168.10.121:9200";
		String indexName = "index";
		String typeName = "test";
		String id = UUID32.getUUID();
		String jsonData = "{\"content\":\"中美贸易战，中国不惧美国\"}";
		ElasticSearchUtils.createDocument(server, indexName, typeName, id, jsonData);
		String queryJson = "{\"query\":\"中国\"}";
		JSONObject json = ElasticSearchUtils.searchPaging(server, indexName, typeName, queryJson, 1, 10);
		if (json != null) {
			System.out.println(json.toJSONString());
		}
		List<String> fields = new ArrayList<String>();
		fields.add("content");
		String queryStr = ElasticSearchUtils.getMultiMatchQueryAllJsonString("中美", "most_fields", fields, fields,
				"red-highlight");
		json = ElasticSearchUtils.searchPaging(server, indexName, typeName, queryStr, 1, 10);
		if (json != null) {
			System.out.println(json.toJSONString());
		}
	}

}
