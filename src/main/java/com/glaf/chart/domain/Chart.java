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

package com.glaf.chart.domain;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.chart.util.ChartJsonFactory;
import com.glaf.core.base.ColumnModel;
import com.glaf.core.base.JSONable;

@Entity
@Table(name = "SYS_CHART")
public class Chart implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", length = 50, nullable = false)
	protected String id;

	@Column(name = "DATABASEID_")
	protected long databaseId;

	@Column(name = "NODEID_")
	protected long nodeId;

	/**
	 * 数据集编号
	 */
	@Column(name = "DATASETIDS_", length = 500)
	protected String datasetIds;

	/**
	 * 第二图表数据集编号
	 */
	@Column(name = "SECOND_DATASETIDS_", length = 500)
	protected String secondDatasetIds;

	/**
	 * 第三图表数据集编号
	 */
	@Column(name = "THIRD_DATASETIDS_", length = 500)
	protected String thirdDatasetIds;

	/**
	 * 查询编号
	 */
	@Column(name = "QUERYIDS_")
	protected String queryIds;

	/**
	 * 查询SQL
	 */
	@Lob
	@Column(name = "QUERYSQL_")
	protected String querySQL;

	/**
	 * 标题
	 */
	@Column(name = "SUBJECT_", length = 200)
	protected String subject;

	/**
	 * 图表名称
	 */
	@Column(name = "CHARTNAME_", length = 200)
	protected String chartName;

	/**
	 * 图表类型
	 */
	@Column(name = "CHARTTYPE_", length = 50)
	protected String chartType;

	/**
	 * 第二图表类型
	 */
	@Column(name = "SECOND_CHARTTYPE_", length = 50)
	protected String secondChartType;

	/**
	 * 第三图表类型
	 */
	@Column(name = "THIRD_CHARTTYPE_", length = 50)
	protected String thirdChartType;

	/**
	 * 图表字体
	 */
	@Column(name = "CHARTFONT_", length = 20)
	protected String chartFont;

	/**
	 * 图表字体大小
	 */
	@Column(name = "CHARTFONTSIZE_")
	protected int chartFontSize;

	/**
	 * 图表主题
	 */
	@Column(name = "CHARTTITLE_", length = 200)
	protected String chartTitle;

	/**
	 * 图表次主题
	 */
	@Column(name = "CHARTSUBTITLE_", length = 200)
	protected String chartSubTitle;

	/**
	 * 图表标题栏字体
	 */
	@Column(name = "CHARTTITLEFONT_", length = 20)
	protected String chartTitleFont;

	/**
	 * 图表标题栏字体大小
	 */
	@Column(name = "CHARTTITLEFONTSIZE_")
	protected int chartTitleFontSize;

	/**
	 * 图表次标题栏字体大小
	 */
	@Column(name = "CHARTSUBTITLEFONTSIZE_")
	protected int chartSubTitleFontSize;

	/**
	 * 图表宽带
	 */
	@Column(name = "CHARTWIDTH_")
	protected int chartWidth;

	/**
	 * 图表高度
	 */
	@Column(name = "CHARTHEIGHT_")
	protected int chartHeight;

	/**
	 * 是否显示图例
	 */
	@Column(name = "LEGEND_", length = 20)
	protected String legend;

	/**
	 * 是否显示tooltip
	 */
	@Column(name = "TOOLTIP_", length = 100)
	protected String tooltip;

	/**
	 * 映射名称
	 */
	@Column(name = "MAPPING_", length = 50)
	protected String mapping;

	/**
	 * X坐标标签
	 */
	@Column(name = "COORDINATEX_", length = 100)
	protected String coordinateX;

	/**
	 * Y坐标标签
	 */
	@Column(name = "COORDINATEY_", length = 100)
	protected String coordinateY;

	/**
	 * Secondary X坐标标签
	 */
	@Column(name = "SECOND_COORDINATEX_", length = 100)
	protected String secondCoordinateX;

	/**
	 * Secondary Y坐标标签
	 */
	@Column(name = "SECOND_COORDINATEY_", length = 100)
	protected String secondCoordinateY;

	/**
	 * 绘制方向
	 */
	@Column(name = "PLOTORIENTATION_", length = 20)
	protected String plotOrientation;

	/**
	 * 生成图像类型
	 */
	@Column(name = "IMAGETYPE_", length = 20)
	protected String imageType;

	/**
	 * 图表皮肤
	 */
	@Column(name = "THEME_", length = 50)
	protected String theme;

	/**
	 * 是否启用3D效果
	 */
	@Column(name = "ENABLE3DFLAG_", length = 1)
	protected String enable3DFlag;

	/**
	 * 是否启用
	 */
	@Column(name = "ENABLEFLAG_", length = 1)
	protected String enableFlag;

	/**
	 * 渐变效果
	 */
	@Column(name = "GRADIENTFLAG_", length = 1)
	protected String gradientFlag;

	@Column(name = "MAXROWCOUNT_")
	protected int maxRowCount;

	/**
	 * 最大刻度
	 */
	@Column(name = "MAXSCALE_")
	protected double maxScale;

	/**
	 * 最新刻度
	 */
	@Column(name = "MINSCALE_")
	protected double minScale;

	/**
	 * 刻度步长值
	 */
	@Column(name = "STEPSCALE_")
	protected double stepScale;

	/**
	 * 创建人
	 */
	@Column(name = "CREATEBY_", length = 50)
	protected String createBy;

	/**
	 * 创建日期
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATEDATE_")
	protected Date createDate;

	@javax.persistence.Transient
	protected String categoriesScripts;

	@javax.persistence.Transient
	protected String secondCategoriesScripts;

	@javax.persistence.Transient
	protected String thirdCategoriesScripts;

	@javax.persistence.Transient
	protected String seriesDataJson;

	@javax.persistence.Transient
	protected String secondSeriesDataJson;

	@javax.persistence.Transient
	protected String thirdSeriesDataJson;

	@javax.persistence.Transient
	protected String pieData;

	@javax.persistence.Transient
	protected String secondPieData;

	@javax.persistence.Transient
	protected String thirdPieData;

	@javax.persistence.Transient
	protected String jsonArrayData;

	@javax.persistence.Transient
	protected String secondJsonArrayData;

	@javax.persistence.Transient
	protected String thirdJsonArrayData;

	@javax.persistence.Transient
	public List<ColumnModel> columns = new java.util.ArrayList<ColumnModel>();

	@javax.persistence.Transient
	public List<ColumnModel> secondColumns = new java.util.ArrayList<ColumnModel>();

	@javax.persistence.Transient
	public List<ColumnModel> thirdColumns = new java.util.ArrayList<ColumnModel>();

	public Chart() {

	}

	public void addCellData(ColumnModel cell) {
		if (columns == null) {
			columns = new java.util.ArrayList<ColumnModel>();
		}
		columns.add(cell);
	}

	public void addSecondCellData(ColumnModel cell) {
		if (secondColumns == null) {
			secondColumns = new java.util.ArrayList<ColumnModel>();
		}
		secondColumns.add(cell);
	}

	public void addThirdCellData(ColumnModel cell) {
		if (thirdColumns == null) {
			thirdColumns = new java.util.ArrayList<ColumnModel>();
		}
		thirdColumns.add(cell);
	}

	public String getCategoriesScripts() {
		return categoriesScripts;
	}

	public String getChartFont() {
		return this.chartFont;
	}

	public int getChartFontSize() {
		if (chartFontSize == 0) {
			chartFontSize = 12;
		}
		return this.chartFontSize;
	}

	public int getChartHeight() {
		if (chartHeight == 0) {
			chartHeight = 300;
		}
		return this.chartHeight;
	}

	public String getChartName() {
		return this.chartName;
	}

	public String getChartSubTitle() {
		return chartSubTitle;
	}

	public int getChartSubTitleFontSize() {
		if (chartSubTitleFontSize == 0) {
			chartSubTitleFontSize = 10;
		}
		return chartSubTitleFontSize;
	}

	public String getChartTitle() {
		return this.chartTitle;
	}

	public String getChartTitleFont() {
		return chartTitleFont;
	}

	public int getChartTitleFontSize() {
		if (chartTitleFontSize == 0) {
			chartTitleFontSize = 15;
		}
		return chartTitleFontSize;
	}

	public String getChartType() {
		return this.chartType;
	}

	public int getChartWidth() {
		if (chartWidth == 0) {
			chartWidth = 500;
		}
		return this.chartWidth;
	}

	public List<ColumnModel> getColumns() {
		if (columns == null) {
			columns = new java.util.ArrayList<ColumnModel>();
		}
		return columns;
	}

	public String getCoordinateX() {
		return this.coordinateX;
	}

	public String getCoordinateY() {
		return this.coordinateY;
	}

	public String getCreateBy() {
		return this.createBy;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public long getDatabaseId() {
		return databaseId;
	}

	public String getDatasetIds() {
		return datasetIds;
	}

	public String getEnable3DFlag() {
		return enable3DFlag;
	}

	public String getEnableFlag() {
		return enableFlag;
	}

	public String getGradientFlag() {
		return gradientFlag;
	}

	public String getId() {
		return this.id;
	}

	public String getImageType() {
		return imageType;
	}

	public String getJsonArrayData() {
		return jsonArrayData;
	}

	public String getLegend() {
		return this.legend;
	}

	public String getMapping() {
		return mapping;
	}

	public int getMaxRowCount() {
		return maxRowCount;
	}

	public double getMaxScale() {
		return maxScale;
	}

	public double getMinScale() {
		return minScale;
	}

	public long getNodeId() {
		return nodeId;
	}

	public String getPieData() {
		return pieData;
	}

	public String getPlotOrientation() {
		return this.plotOrientation;
	}

	public String getQueryIds() {
		return this.queryIds;
	}

	public String getQuerySQL() {
		return querySQL;
	}

	public String getSecondCategoriesScripts() {
		return secondCategoriesScripts;
	}

	public String getSecondChartType() {
		return secondChartType;
	}

	public List<ColumnModel> getSecondColumns() {
		return secondColumns;
	}

	public String getSecondCoordinateX() {
		return secondCoordinateX;
	}

	public String getSecondCoordinateY() {
		return secondCoordinateY;
	}

	public String getSecondDatasetIds() {
		return secondDatasetIds;
	}

	public String getSecondJsonArrayData() {
		return secondJsonArrayData;
	}

	public String getSecondPieData() {
		return secondPieData;
	}

	public String getSecondSeriesDataJson() {
		return secondSeriesDataJson;
	}

	public String getSeriesDataJson() {
		return seriesDataJson;
	}

	public double getStepScale() {
		return stepScale;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getTheme() {
		return theme;
	}

	public String getThirdCategoriesScripts() {
		return thirdCategoriesScripts;
	}

	public String getThirdChartType() {
		return thirdChartType;
	}

	public List<ColumnModel> getThirdColumns() {
		return thirdColumns;
	}

	public String getThirdDatasetIds() {
		return thirdDatasetIds;
	}

	public String getThirdJsonArrayData() {
		return thirdJsonArrayData;
	}

	public String getThirdPieData() {
		return thirdPieData;
	}

	public String getThirdSeriesDataJson() {
		return thirdSeriesDataJson;
	}

	public String getTooltip() {
		return this.tooltip;
	}

	public Chart jsonToObject(JSONObject jsonObject) {
		return ChartJsonFactory.jsonToObject(jsonObject);
	}

	public void removeColumn(ColumnModel cell) {
		if (columns != null) {
			if (columns.contains(cell)) {
				columns.remove(cell);
			}
		}
	}

	public void setCategoriesScripts(String categoriesScripts) {
		this.categoriesScripts = categoriesScripts;
	}

	public void setChartFont(String chartFont) {
		this.chartFont = chartFont;
	}

	public void setChartFontSize(int chartFontSize) {
		this.chartFontSize = chartFontSize;
	}

	public void setChartHeight(int chartHeight) {
		this.chartHeight = chartHeight;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public void setChartSubTitle(String chartSubTitle) {
		this.chartSubTitle = chartSubTitle;
	}

	public void setChartSubTitleFontSize(int chartSubTitleFontSize) {
		this.chartSubTitleFontSize = chartSubTitleFontSize;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public void setChartTitleFont(String chartTitleFont) {
		this.chartTitleFont = chartTitleFont;
	}

	public void setChartTitleFontSize(int chartTitleFontSize) {
		this.chartTitleFontSize = chartTitleFontSize;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public void setChartWidth(int chartWidth) {
		this.chartWidth = chartWidth;
	}

	public void setColumns(List<ColumnModel> columns) {
		this.columns = columns;
	}

	public void setCoordinateX(String coordinateX) {
		this.coordinateX = coordinateX;
	}

	public void setCoordinateY(String coordinateY) {
		this.coordinateY = coordinateY;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setDatabaseId(long databaseId) {
		this.databaseId = databaseId;
	}

	public void setDatasetIds(String datasetIds) {
		this.datasetIds = datasetIds;
	}

	public void setEnable3DFlag(String enable3dFlag) {
		enable3DFlag = enable3dFlag;
	}

	public void setEnableFlag(String enableFlag) {
		this.enableFlag = enableFlag;
	}

	public void setGradientFlag(String gradientFlag) {
		this.gradientFlag = gradientFlag;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public void setJsonArrayData(String jsonArrayData) {
		this.jsonArrayData = jsonArrayData;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public void setMaxRowCount(int maxRowCount) {
		this.maxRowCount = maxRowCount;
	}

	public void setMaxScale(double maxScale) {
		this.maxScale = maxScale;
	}

	public void setMinScale(double minScale) {
		this.minScale = minScale;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public void setPieData(String pieData) {
		this.pieData = pieData;
	}

	public void setPlotOrientation(String plotOrientation) {
		this.plotOrientation = plotOrientation;
	}

	public void setQueryIds(String queryIds) {
		this.queryIds = queryIds;
	}

	public void setQuerySQL(String querySQL) {
		this.querySQL = querySQL;
	}

	public void setSecondCategoriesScripts(String secondCategoriesScripts) {
		this.secondCategoriesScripts = secondCategoriesScripts;
	}

	public void setSecondChartType(String secondChartType) {
		this.secondChartType = secondChartType;
	}

	public void setSecondColumns(List<ColumnModel> secondColumns) {
		this.secondColumns = secondColumns;
	}

	public void setSecondCoordinateX(String secondCoordinateX) {
		this.secondCoordinateX = secondCoordinateX;
	}

	public void setSecondCoordinateY(String secondCoordinateY) {
		this.secondCoordinateY = secondCoordinateY;
	}

	public void setSecondDatasetIds(String secondDatasetIds) {
		this.secondDatasetIds = secondDatasetIds;
	}

	public void setSecondJsonArrayData(String secondJsonArrayData) {
		this.secondJsonArrayData = secondJsonArrayData;
	}

	public void setSecondPieData(String secondPieData) {
		this.secondPieData = secondPieData;
	}

	public void setSecondSeriesDataJson(String secondSeriesDataJson) {
		this.secondSeriesDataJson = secondSeriesDataJson;
	}

	public void setSeriesDataJson(String seriesDataJson) {
		this.seriesDataJson = seriesDataJson;
	}

	public void setStepScale(double stepScale) {
		this.stepScale = stepScale;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public void setThirdCategoriesScripts(String thirdCategoriesScripts) {
		this.thirdCategoriesScripts = thirdCategoriesScripts;
	}

	public void setThirdChartType(String thirdChartType) {
		this.thirdChartType = thirdChartType;
	}

	public void setThirdColumns(List<ColumnModel> thirdColumns) {
		this.thirdColumns = thirdColumns;
	}

	public void setThirdDatasetIds(String thirdDatasetIds) {
		this.thirdDatasetIds = thirdDatasetIds;
	}

	public void setThirdJsonArrayData(String thirdJsonArrayData) {
		this.thirdJsonArrayData = thirdJsonArrayData;
	}

	public void setThirdPieData(String thirdPieData) {
		this.thirdPieData = thirdPieData;
	}

	public void setThirdSeriesDataJson(String thirdSeriesDataJson) {
		this.thirdSeriesDataJson = thirdSeriesDataJson;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public JSONObject toJsonObject() {
		return ChartJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return ChartJsonFactory.toObjectNode(this);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}