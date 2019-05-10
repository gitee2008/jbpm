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

package com.glaf.matrix.export.query;

import java.util.*;
import com.glaf.core.query.DataQuery;

public class ExportChartQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected String expId;
	protected List<String> expIds;
	protected String nameLike;
	protected String titleLike;
	protected String imageType;
	protected Date createTimeGreaterThanOrEqual;
	protected Date createTimeLessThanOrEqual;

	public ExportChartQuery() {

	}

	public ExportChartQuery createBy(String createBy) {
		if (createBy == null) {
			throw new RuntimeException("createBy is null");
		}
		this.createBy = createBy;
		return this;
	}

	public ExportChartQuery createTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		if (createTimeGreaterThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
		return this;
	}

	public ExportChartQuery createTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		if (createTimeLessThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
		return this;
	}

	public ExportChartQuery expId(String expId) {
		if (expId == null) {
			throw new RuntimeException("expId is null");
		}
		this.expId = expId;
		return this;
	}

	public ExportChartQuery expIds(List<String> expIds) {
		if (expIds == null) {
			throw new RuntimeException("expIds is empty ");
		}
		this.expIds = expIds;
		return this;
	}

	public Date getCreateTimeGreaterThanOrEqual() {
		return createTimeGreaterThanOrEqual;
	}

	public Date getCreateTimeLessThanOrEqual() {
		return createTimeLessThanOrEqual;
	}

	public String getExpId() {
		return expId;
	}

	public List<String> getExpIds() {
		return expIds;
	}

	public String getImageType() {
		return imageType;
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

			if ("expId".equals(sortColumn)) {
				orderBy = "E.EXPID_" + a_x;
			}

			if ("name".equals(sortColumn)) {
				orderBy = "E.NAME_" + a_x;
			}

			if ("title".equals(sortColumn)) {
				orderBy = "E.TITLE_" + a_x;
			}

			if ("height".equals(sortColumn)) {
				orderBy = "E.HEIGHT_" + a_x;
			}

			if ("width".equals(sortColumn)) {
				orderBy = "E.WIDTH_" + a_x;
			}

			if ("imageType".equals(sortColumn)) {
				orderBy = "E.IMAGETYPE_" + a_x;
			}

			if ("chartId".equals(sortColumn)) {
				orderBy = "E.CHARTID_" + a_x;
			}

			if ("chartUrl".equals(sortColumn)) {
				orderBy = "E.CHARTURL_" + a_x;
			}

			if ("snapshotFlag".equals(sortColumn)) {
				orderBy = "E.SNAPSHOTFLAG_" + a_x;
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

	public ExportChartQuery imageType(String imageType) {
		if (imageType == null) {
			throw new RuntimeException("imageType is null");
		}
		this.imageType = imageType;
		return this;
	}

	@Override
	public void initQueryColumns() {
		super.initQueryColumns();
		addColumn("id", "ID_");
		addColumn("expId", "EXPID_");
		addColumn("name", "NAME_");
		addColumn("title", "TITLE_");
		addColumn("height", "HEIGHT_");
		addColumn("width", "WIDTH_");
		addColumn("imageType", "IMAGETYPE_");
		addColumn("chartId", "CHARTID_");
		addColumn("chartUrl", "CHARTURL_");
		addColumn("snapshotFlag", "SNAPSHOTFLAG_");
		addColumn("locked", "LOCKED_");
		addColumn("createBy", "CREATEBY_");
		addColumn("createTime", "CREATETIME_");
	}

	public ExportChartQuery nameLike(String nameLike) {
		if (nameLike == null) {
			throw new RuntimeException("name is null");
		}
		this.nameLike = nameLike;
		return this;
	}

	public void setCreateTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
	}

	public void setCreateTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
	}

	public void setExpId(String expId) {
		this.expId = expId;
	}

	public void setExpIds(List<String> expIds) {
		this.expIds = expIds;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public void setNameLike(String nameLike) {
		this.nameLike = nameLike;
	}

	public void setTitleLike(String titleLike) {
		this.titleLike = titleLike;
	}

	public ExportChartQuery titleLike(String titleLike) {
		if (titleLike == null) {
			throw new RuntimeException("title is null");
		}
		this.titleLike = titleLike;
		return this;
	}

}