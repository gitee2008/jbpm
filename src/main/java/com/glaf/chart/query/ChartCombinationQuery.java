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

public class ChartCombinationQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected Collection<String> appActorIds;
	protected String name;
	protected String nameLike;
	protected String titleLike;
	protected String type;
	protected String category;
	protected List<String> createBys;
	protected Date createTimeGreaterThanOrEqual;
	protected Date createTimeLessThanOrEqual;

	public ChartCombinationQuery() {

	}

	public ChartCombinationQuery category(String category) {
		if (category == null) {
			throw new RuntimeException("category is null");
		}
		this.category = category;
		return this;
	}

	public ChartCombinationQuery createBys(List<String> createBys) {
		if (createBys == null) {
			throw new RuntimeException("createBys is empty ");
		}
		this.createBys = createBys;
		return this;
	}

	public ChartCombinationQuery createTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		if (createTimeGreaterThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
		return this;
	}

	public ChartCombinationQuery createTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		if (createTimeLessThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
		return this;
	}

	public Collection<String> getAppActorIds() {
		return appActorIds;
	}

	public String getCategory() {
		return category;
	}

	public List<String> getCreateBys() {
		return createBys;
	}

	public Date getCreateTimeGreaterThanOrEqual() {
		return createTimeGreaterThanOrEqual;
	}

	public Date getCreateTimeLessThanOrEqual() {
		return createTimeLessThanOrEqual;
	}

	public String getName() {
		return name;
	}

	public String getNameLike() {
		if (nameLike != null && nameLike.trim().length() > 0) {
			if (!nameLike.startsWith("%")) {
				nameLike = "%" + nameLike;
			}
			if (!nameLike.endsWith("%")) {
				nameLike = nameLike + "%";
			}
		}
		return nameLike;
	}

	public String getOrderBy() {
		if (sortColumn != null) {
			String a_x = " asc ";
			if (sortOrder != null) {
				a_x = sortOrder;
			}

			if ("name".equals(sortColumn)) {
				orderBy = "E.NAME_" + a_x;
			}

			if ("title".equals(sortColumn)) {
				orderBy = "E.TITLE_" + a_x;
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

			if ("chartIds".equals(sortColumn)) {
				orderBy = "E.CHARTIDS_" + a_x;
			}

			if ("direction".equals(sortColumn)) {
				orderBy = "E.DIRECTION_" + a_x;
			}

			if ("createBy".equals(sortColumn)) {
				orderBy = "E.CREATEBY_" + a_x;
			}

			if ("createTime".equals(sortColumn)) {
				orderBy = "E.CREATETIME_" + a_x;
			}

		}
		return orderBy;
	}

	public String getServiceKey() {
		return serviceKey;
	}

	public String getTitleLike() {
		if (titleLike != null && titleLike.trim().length() > 0) {
			if (!titleLike.startsWith("%")) {
				titleLike = "%" + titleLike;
			}
			if (!titleLike.endsWith("%")) {
				titleLike = titleLike + "%";
			}
		}
		return titleLike;
	}

	public String getType() {
		return type;
	}

	@Override
	public void initQueryColumns() {
		super.initQueryColumns();
		addColumn("id", "ID_");
		addColumn("name", "NAME_");
		addColumn("title", "TITLE_");
		addColumn("serviceKey", "SERVICEKEY_");
		addColumn("type", "TYPE_");
		addColumn("category", "CATEGORY_");
		addColumn("chartIds", "CHARTIDS_");
		addColumn("direction", "DIRECTION_");
		addColumn("createBy", "CREATEBY_");
		addColumn("createTime", "CREATETIME_");
	}

	public ChartCombinationQuery name(String name) {
		if (name == null) {
			throw new RuntimeException("name is null");
		}
		this.name = name;
		return this;
	}

	public ChartCombinationQuery nameLike(String nameLike) {
		if (nameLike == null) {
			throw new RuntimeException("name is null");
		}
		this.nameLike = nameLike;
		return this;
	}

	public ChartCombinationQuery serviceKey(String serviceKey) {
		if (serviceKey == null) {
			throw new RuntimeException("serviceKey is null");
		}
		this.serviceKey = serviceKey;
		return this;
	}

	public void setAppActorIds(Collection<String> appActorIds) {
		this.appActorIds = appActorIds;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCreateBys(List<String> createBys) {
		this.createBys = createBys;
	}

	public void setCreateTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
	}

	public void setCreateTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNameLike(String nameLike) {
		this.nameLike = nameLike;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public void setTitleLike(String titleLike) {
		this.titleLike = titleLike;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ChartCombinationQuery titleLike(String titleLike) {
		if (titleLike == null) {
			throw new RuntimeException("title is null");
		}
		this.titleLike = titleLike;
		return this;
	}

	public ChartCombinationQuery type(String type) {
		if (type == null) {
			throw new RuntimeException("type is null");
		}
		this.type = type;
		return this;
	}

}