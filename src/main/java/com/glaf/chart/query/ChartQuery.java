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

public class ChartQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected List<String> chartIds;
	protected Long nodeId;
	protected String subjectLike;
	protected String chartName;
	protected String chartNameLike;
	protected List<String> chartNames;
	protected String chartTitle;
	protected String chartTitleLike;
	protected String chartType;
	protected String imageType;
	protected String mapping;
	protected String enableFlag;
	protected String keywordsLike;
	protected Date createDateGreaterThanOrEqual;
	protected Date createDateLessThanOrEqual;
	protected String createByLike;

	public ChartQuery() {

	}

	public ChartQuery chartIds(List<String> chartIds) {
		if (chartIds == null) {
			throw new RuntimeException("chartIds is empty ");
		}
		this.chartIds = chartIds;
		return this;
	}

	public ChartQuery chartName(String chartName) {
		if (chartName == null) {
			throw new RuntimeException("chartName is null");
		}
		this.chartName = chartName;
		return this;
	}

	public ChartQuery chartNameLike(String chartNameLike) {
		if (chartNameLike == null) {
			throw new RuntimeException("chartName is null");
		}
		this.chartNameLike = chartNameLike;
		return this;
	}

	public ChartQuery chartNames(List<String> chartNames) {
		if (chartNames == null) {
			throw new RuntimeException("chartNames is empty ");
		}
		this.chartNames = chartNames;
		return this;
	}

	public ChartQuery chartTitle(String chartTitle) {
		if (chartTitle == null) {
			throw new RuntimeException("chartTitle is null");
		}
		this.chartTitle = chartTitle;
		return this;
	}

	public ChartQuery chartTitleLike(String chartTitleLike) {
		if (chartTitleLike == null) {
			throw new RuntimeException("chartTitle is null");
		}
		this.chartTitleLike = chartTitleLike;
		return this;
	}

	public ChartQuery chartType(String chartType) {
		if (chartType == null) {
			throw new RuntimeException("chartType is null");
		}
		this.chartType = chartType;
		return this;
	}

	public ChartQuery createByLike(String createByLike) {
		if (createByLike == null) {
			throw new RuntimeException("createBy is null");
		}
		this.createByLike = createByLike;
		return this;
	}

	public ChartQuery createDateGreaterThanOrEqual(Date createDateGreaterThanOrEqual) {
		if (createDateGreaterThanOrEqual == null) {
			throw new RuntimeException("createDate is null");
		}
		this.createDateGreaterThanOrEqual = createDateGreaterThanOrEqual;
		return this;
	}

	public ChartQuery createDateLessThanOrEqual(Date createDateLessThanOrEqual) {
		if (createDateLessThanOrEqual == null) {
			throw new RuntimeException("createDate is null");
		}
		this.createDateLessThanOrEqual = createDateLessThanOrEqual;
		return this;
	}

	public ChartQuery enableFlag(String enableFlag) {
		if (enableFlag == null) {
			throw new RuntimeException("enableFlag is null");
		}
		this.enableFlag = enableFlag;
		return this;
	}

	public List<String> getChartIds() {
		return chartIds;
	}

	public String getChartName() {
		return chartName;
	}

	public String getChartNameLike() {
		if (chartNameLike != null && chartNameLike.trim().length() > 0) {
			if (!chartNameLike.startsWith("%")) {
				chartNameLike = "%" + chartNameLike;
			}
			if (!chartNameLike.endsWith("%")) {
				chartNameLike = chartNameLike + "%";
			}
		}
		return chartNameLike;
	}

	public List<String> getChartNames() {
		return chartNames;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public String getChartTitleLike() {
		if (chartTitleLike != null && chartTitleLike.trim().length() > 0) {
			if (!chartTitleLike.startsWith("%")) {
				chartTitleLike = "%" + chartTitleLike;
			}
			if (!chartTitleLike.endsWith("%")) {
				chartTitleLike = chartTitleLike + "%";
			}
		}
		return chartTitleLike;
	}

	public String getChartType() {
		return chartType;
	}

	public String getCreateByLike() {
		if (createByLike != null && createByLike.trim().length() > 0) {
			if (!createByLike.startsWith("%")) {
				createByLike = "%" + createByLike;
			}
			if (!createByLike.endsWith("%")) {
				createByLike = createByLike + "%";
			}
		}
		return createByLike;
	}

	public Date getCreateDateGreaterThanOrEqual() {
		return createDateGreaterThanOrEqual;
	}

	public Date getCreateDateLessThanOrEqual() {
		return createDateLessThanOrEqual;
	}

	public String getEnableFlag() {
		return enableFlag;
	}

	public String getImageType() {
		return imageType;
	}

	public String getKeywordsLike() {
		if (keywordsLike != null && keywordsLike.trim().length() > 0) {
			if (!keywordsLike.startsWith("%")) {
				keywordsLike = "%" + keywordsLike;
			}
			if (!keywordsLike.endsWith("%")) {
				keywordsLike = keywordsLike + "%";
			}
		}
		return keywordsLike;
	}

	public String getMapping() {
		return mapping;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public String getOrderBy() {
		if (sortColumn != null) {
			String a_x = " asc ";
			if (sortOrder != null) {
				a_x = sortOrder;
			}

			if ("queryId".equals(sortColumn)) {
				orderBy = "E.QUERYID_" + a_x;
			}

			if ("subject".equals(sortColumn)) {
				orderBy = "E.SUBJECT_" + a_x;
			}

			if ("chartName".equals(sortColumn)) {
				orderBy = "E.CHARTNAME_" + a_x;
			}

			if ("chartTitle".equals(sortColumn)) {
				orderBy = "E.CHARTTITLE_" + a_x;
			}

			if ("chartType".equals(sortColumn)) {
				orderBy = "E.CHARTTYPE_" + a_x;
			}

			if ("chartFont".equals(sortColumn)) {
				orderBy = "E.CHARTFONT_" + a_x;
			}

			if ("chartFontSize".equals(sortColumn)) {
				orderBy = "E.CHARTFONTSIZE_" + a_x;
			}

			if ("chartWidth".equals(sortColumn)) {
				orderBy = "E.CHARTWIDTH_" + a_x;
			}

			if ("chartHeight".equals(sortColumn)) {
				orderBy = "E.CHARTHEIGHT_" + a_x;
			}

			if ("imageType".equals(sortColumn)) {
				orderBy = "E.IMAGETYPE_" + a_x;
			}

			if ("legend".equals(sortColumn)) {
				orderBy = "E.LEGEND_" + a_x;
			}

			if ("tooltip".equals(sortColumn)) {
				orderBy = "E.TOOLTIP_" + a_x;
			}

			if ("coordinateX".equals(sortColumn)) {
				orderBy = "E.COORDINATEX_" + a_x;
			}

			if ("coordinateY".equals(sortColumn)) {
				orderBy = "E.COORDINATEY_" + a_x;
			}

			if ("plotOrientation".equals(sortColumn)) {
				orderBy = "E.PLOTORIENTATION_" + a_x;
			}

			if ("createDate".equals(sortColumn)) {
				orderBy = "E.CREATEDATE_" + a_x;
			}

			if ("createBy".equals(sortColumn)) {
				orderBy = "E.CREATEBY_" + a_x;
			}

		}
		return orderBy;
	}

	public String getSubjectLike() {
		if (subjectLike != null && subjectLike.trim().length() > 0) {
			if (!subjectLike.startsWith("%")) {
				subjectLike = "%" + subjectLike;
			}
			if (!subjectLike.endsWith("%")) {
				subjectLike = subjectLike + "%";
			}
		}
		return subjectLike;
	}

	public ChartQuery imageType(String imageType) {
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
		addColumn("queryId", "QUERYID_");
		addColumn("subject", "SUBJECT_");
		addColumn("chartName", "CHARTNAME_");
		addColumn("chartTitle", "CHARTTITLE_");
		addColumn("chartType", "CHARTTYPE_");
		addColumn("chartFont", "CHARTFONT_");
		addColumn("chartFontSize", "CHARTFONTSIZE_");
		addColumn("chartWidth", "CHARTWIDTH_");
		addColumn("chartHeight", "CHARTHEIGHT_");
		addColumn("imageType", "IMAGETYPE_");
		addColumn("legend", "LEGEND_");
		addColumn("tooltip", "TOOLTIP_");

		addColumn("coordinateX", "COORDINATEX_");
		addColumn("coordinateY", "COORDINATEY_");
		addColumn("plotOrientation", "PLOTORIENTATION_");
		addColumn("createDate", "CREATEDATE_");
		addColumn("createBy", "CREATEBY_");
	}

	public ChartQuery nodeId(Long nodeId) {
		if (nodeId == null) {
			throw new RuntimeException("nodeId is null");
		}
		this.nodeId = nodeId;
		return this;
	}

	public void setChartIds(List<String> chartIds) {
		this.chartIds = chartIds;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public void setChartNameLike(String chartNameLike) {
		this.chartNameLike = chartNameLike;
	}

	public void setChartNames(List<String> chartNames) {
		this.chartNames = chartNames;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public void setChartTitleLike(String chartTitleLike) {
		this.chartTitleLike = chartTitleLike;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public void setCreateByLike(String createByLike) {
		this.createByLike = createByLike;
	}

	public void setCreateDateGreaterThanOrEqual(Date createDateGreaterThanOrEqual) {
		this.createDateGreaterThanOrEqual = createDateGreaterThanOrEqual;
	}

	public void setCreateDateLessThanOrEqual(Date createDateLessThanOrEqual) {
		this.createDateLessThanOrEqual = createDateLessThanOrEqual;
	}

	public void setEnableFlag(String enableFlag) {
		this.enableFlag = enableFlag;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public void setKeywordsLike(String keywordsLike) {
		this.keywordsLike = keywordsLike;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	public void setSubjectLike(String subjectLike) {
		this.subjectLike = subjectLike;
	}

	public ChartQuery subjectLike(String subjectLike) {
		if (subjectLike == null) {
			throw new RuntimeException("subject is null");
		}
		this.subjectLike = subjectLike;
		return this;
	}

}