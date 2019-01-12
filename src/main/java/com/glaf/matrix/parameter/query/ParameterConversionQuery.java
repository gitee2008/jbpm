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

package com.glaf.matrix.parameter.query;

import java.util.*;
import com.glaf.core.query.DataQuery;

public class ParameterConversionQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected String key;
	protected String titleLike;
	protected Date createTimeGreaterThanOrEqual;
	protected Date createTimeLessThanOrEqual;

	public ParameterConversionQuery() {

	}

	public ParameterConversionQuery createBy(String createBy) {
		if (createBy == null) {
			throw new RuntimeException("createBy is null");
		}
		this.createBy = createBy;
		return this;
	}

	public ParameterConversionQuery createTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		if (createTimeGreaterThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
		return this;
	}

	public ParameterConversionQuery createTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		if (createTimeLessThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
		return this;
	}

	public Date getCreateTimeGreaterThanOrEqual() {
		return createTimeGreaterThanOrEqual;
	}

	public Date getCreateTimeLessThanOrEqual() {
		return createTimeLessThanOrEqual;
	}

	public String getKey() {
		return key;
	}

	public String getOrderBy() {
		if (sortColumn != null) {
			String a_x = " asc ";
			if (sortOrder != null) {
				a_x = sortOrder;
			}

			if ("key".equals(sortColumn)) {
				orderBy = "E.KEY_" + a_x;
			}

			if ("title".equals(sortColumn)) {
				orderBy = "E.TITLE_" + a_x;
			}

			if ("sourceName".equals(sortColumn)) {
				orderBy = "E.SOURCENAME_" + a_x;
			}

			if ("sourceType".equals(sortColumn)) {
				orderBy = "E.SOURCETYPE_" + a_x;
			}

			if ("sourceListFlag".equals(sortColumn)) {
				orderBy = "E.SOURCELISTFLAG_" + a_x;
			}

			if ("targetName".equals(sortColumn)) {
				orderBy = "E.TARGETNAME_" + a_x;
			}

			if ("targetType".equals(sortColumn)) {
				orderBy = "E.TARGETTYPE_" + a_x;
			}

			if ("targetListFlag".equals(sortColumn)) {
				orderBy = "E.TARGETLISTFLAG_" + a_x;
			}

			if ("delimiter".equals(sortColumn)) {
				orderBy = "E.DELIMITER_" + a_x;
			}

			if ("convertTemplate".equals(sortColumn)) {
				orderBy = "E.CONVERTTEMPLATE_" + a_x;
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

	@Override
	public void initQueryColumns() {
		super.initQueryColumns();
		addColumn("id", "ID_");
		addColumn("key", "KEY_");
		addColumn("title", "TITLE_");
		addColumn("sourceName", "SOURCENAME_");
		addColumn("sourceType", "SOURCETYPE_");
		addColumn("sourceListFlag", "SOURCELISTFLAG_");
		addColumn("targetName", "TARGETNAME_");
		addColumn("targetType", "TARGETTYPE_");
		addColumn("targetListFlag", "TARGETLISTFLAG_");
		addColumn("delimiter", "DELIMITER_");
		addColumn("convertTemplate", "CONVERTTEMPLATE_");
		addColumn("createBy", "CREATEBY_");
		addColumn("createTime", "CREATETIME_");
	}

	public ParameterConversionQuery key(String key) {
		if (key == null) {
			throw new RuntimeException("key is null");
		}
		this.key = key;
		return this;
	}

	public void setCreateTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
	}

	public void setCreateTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setTitleLike(String titleLike) {
		this.titleLike = titleLike;
	}

	public ParameterConversionQuery titleLike(String titleLike) {
		if (titleLike == null) {
			throw new RuntimeException("title is null");
		}
		this.titleLike = titleLike;
		return this;
	}

}