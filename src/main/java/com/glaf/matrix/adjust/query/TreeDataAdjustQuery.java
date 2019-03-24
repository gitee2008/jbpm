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

package com.glaf.matrix.adjust.query;

import java.util.*;
import com.glaf.core.query.DataQuery;

public class TreeDataAdjustQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected String name;
	protected String nameLike;
	protected String titleLike;
	protected String type;
	protected String adjustType;
	protected String scheduleFlag;
	protected Date createTimeGreaterThanOrEqual;
	protected Date createTimeLessThanOrEqual;

	public TreeDataAdjustQuery() {

	}

	public TreeDataAdjustQuery adjustType(String adjustType) {
		if (adjustType == null) {
			throw new RuntimeException("adjustType is null");
		}
		this.adjustType = adjustType;
		return this;
	}

	public TreeDataAdjustQuery createBy(String createBy) {
		if (createBy == null) {
			throw new RuntimeException("createBy is null");
		}
		this.createBy = createBy;
		return this;
	}

	public TreeDataAdjustQuery createTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		if (createTimeGreaterThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
		return this;
	}

	public TreeDataAdjustQuery createTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		if (createTimeLessThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
		return this;
	}

	public String getAdjustType() {
		return adjustType;
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

			if ("databaseId".equals(sortColumn)) {
				orderBy = "E.DATABASEID_" + a_x;
			}

			if ("tableName".equals(sortColumn)) {
				orderBy = "E.TABLENAME_" + a_x;
			}

			if ("primaryKey".equals(sortColumn)) {
				orderBy = "E.PRIMARYKEY_" + a_x;
			}

			if ("idColumn".equals(sortColumn)) {
				orderBy = "E.IDCOLUMN_" + a_x;
			}

			if ("parentIdColumn".equals(sortColumn)) {
				orderBy = "E.PARENTIDCOLUMN_" + a_x;
			}

			if ("treeIdColumn".equals(sortColumn)) {
				orderBy = "E.TREEIDCOLUMN_" + a_x;
			}

			if ("treeIdDelimiter".equals(sortColumn)) {
				orderBy = "E.TREEIDDELIMITER_" + a_x;
			}

			if ("adjustColumn".equals(sortColumn)) {
				orderBy = "E.ADJUSTCOLUMN_" + a_x;
			}

			if ("adjustType".equals(sortColumn)) {
				orderBy = "E.ADJUSTTYPE_" + a_x;
			}

			if ("preprocessFlag".equals(sortColumn)) {
				orderBy = "E.PREPROCESSFLAG_" + a_x;
			}

			if ("type".equals(sortColumn)) {
				orderBy = "E.TYPE_" + a_x;
			}

			if ("locked".equals(sortColumn)) {
				orderBy = "E.LOCKED_" + a_x;
			}

			if ("createBy".equals(sortColumn)) {
				orderBy = "E.CREATEBY_" + a_x;
			}

			if ("createTime".equals(sortColumn)) {
				orderBy = "E.CREATETIME_" + a_x;
			}

			if ("updateBy".equals(sortColumn)) {
				orderBy = "E.UPDATEBY_" + a_x;
			}

			if ("updateTime".equals(sortColumn)) {
				orderBy = "E.UPDATETIME_" + a_x;
			}

		}
		return orderBy;
	}

	public String getScheduleFlag() {
		return scheduleFlag;
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
		addColumn("databaseId", "DATABASEID_");
		addColumn("tableName", "TABLENAME_");
		addColumn("primaryKey", "PRIMARYKEY_");
		addColumn("idColumn", "IDCOLUMN_");
		addColumn("parentIdColumn", "PARENTIDCOLUMN_");
		addColumn("treeIdColumn", "TREEIDCOLUMN_");
		addColumn("treeIdDelimiter", "TREEIDDELIMITER_");
		addColumn("adjustColumn", "ADJUSTCOLUMN_");
		addColumn("adjustType", "ADJUSTTYPE_");
		addColumn("preprocessFlag", "PREPROCESSFLAG_");
		addColumn("type", "TYPE_");
		addColumn("locked", "LOCKED_");
		addColumn("createBy", "CREATEBY_");
		addColumn("createTime", "CREATETIME_");
		addColumn("updateBy", "UPDATEBY_");
		addColumn("updateTime", "UPDATETIME_");
	}

	public TreeDataAdjustQuery locked(Integer locked) {
		if (locked == null) {
			throw new RuntimeException("locked is null");
		}
		this.locked = locked;
		return this;
	}

	public TreeDataAdjustQuery name(String name) {
		if (name == null) {
			throw new RuntimeException("name is null");
		}
		this.name = name;
		return this;
	}

	public TreeDataAdjustQuery nameLike(String nameLike) {
		if (nameLike == null) {
			throw new RuntimeException("name is null");
		}
		this.nameLike = nameLike;
		return this;
	}

	public TreeDataAdjustQuery scheduleFlag(String scheduleFlag) {
		if (scheduleFlag == null) {
			throw new RuntimeException("scheduleFlag is null");
		}
		this.scheduleFlag = scheduleFlag;
		return this;
	}

	public void setAdjustType(String adjustType) {
		this.adjustType = adjustType;
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

	public void setScheduleFlag(String scheduleFlag) {
		this.scheduleFlag = scheduleFlag;
	}

	public void setTitleLike(String titleLike) {
		this.titleLike = titleLike;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TreeDataAdjustQuery titleLike(String titleLike) {
		if (titleLike == null) {
			throw new RuntimeException("title is null");
		}
		this.titleLike = titleLike;
		return this;
	}

	public TreeDataAdjustQuery type(String type) {
		if (type == null) {
			throw new RuntimeException("type is null");
		}
		this.type = type;
		return this;
	}

}