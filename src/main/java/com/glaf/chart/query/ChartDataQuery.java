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

package com.glaf.chart.query;

import java.util.*;
import com.glaf.core.query.DataQuery;

public class ChartDataQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected Collection<String> appActorIds;
	protected List<String> serviceKeys;
	protected String type;
	protected List<String> types;

	public ChartDataQuery() {

	}

	public Collection<String> getAppActorIds() {
		return appActorIds;
	}

	public void setAppActorIds(Collection<String> appActorIds) {
		this.appActorIds = appActorIds;
	}

	public List<String> getServiceKeys() {
		return serviceKeys;
	}

	public String getType() {
		return type;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setServiceKeys(List<String> serviceKeys) {
		this.serviceKeys = serviceKeys;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public ChartDataQuery serviceKeys(List<String> serviceKeys) {
		if (serviceKeys == null) {
			throw new RuntimeException("serviceKeys is empty ");
		}
		this.serviceKeys = serviceKeys;
		return this;
	}

	public ChartDataQuery type(String type) {
		if (type == null) {
			throw new RuntimeException("type is null");
		}
		this.type = type;
		return this;
	}

	public ChartDataQuery types(List<String> types) {
		if (types == null) {
			throw new RuntimeException("types is empty ");
		}
		this.types = types;
		return this;
	}

	public String getOrderBy() {
		if (sortColumn != null) {
			String a_x = " asc ";
			if (sortOrder != null) {
				a_x = sortOrder;
			}

			if ("serviceKey".equals(sortColumn)) {
				orderBy = "E.SERVICEKEY_" + a_x;
			}

			if ("type".equals(sortColumn)) {
				orderBy = "E.TYPE_" + a_x;
			}

			if ("category".equals(sortColumn)) {
				orderBy = "E.CATEGORY_" + a_x;
			}

			if ("series".equals(sortColumn)) {
				orderBy = "E.SERIES_" + a_x;
			}

			if ("value".equals(sortColumn)) {
				orderBy = "E.VALUE_" + a_x;
			}

		}
		return orderBy;
	}

	@Override
	public void initQueryColumns() {
		super.initQueryColumns();
		addColumn("id", "ID_");
		addColumn("serviceKey", "SERVICEKEY_");
		addColumn("type", "TYPE_");
		addColumn("category", "CATEGORY_");
		addColumn("series", "SERIES_");
		addColumn("value", "VALUE_");
	}

}