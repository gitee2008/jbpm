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

package com.glaf.matrix.export.domain;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.glaf.core.base.*;
import com.glaf.core.util.DateUtils;
import com.glaf.matrix.export.util.*;

/**
 * 
 * 实体对象
 *
 */

@Entity
@Table(name = "SYS_EXPORT_ITEM")
public class ExportItem implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", length = 50, nullable = false)
	protected String id;

	/**
	 * 主数据编号
	 */
	@Column(name = "EXPID_", length = 50)
	protected String expId;

	/**
	 * Xml导出编号
	 */
	@Column(name = "XMLEXPID_", length = 50)
	protected String xmlExpId;

	/**
	 * 部署编号
	 */
	@Column(name = "DEPLOYMENTID_", length = 50)
	protected String deploymentId;

	/**
	 * 名称
	 */
	@Column(name = "NAME_", length = 50)
	protected String name;

	/**
	 * 标题
	 */
	@Column(name = "TITLE_", length = 200)
	protected String title;

	/**
	 * 数据集编号
	 */
	@Column(name = "DATASETID_", length = 50)
	protected String datasetId;

	/**
	 * SQL语句
	 */
	@Lob
	@Column(name = "SQL_")
	protected String sql;

	/**
	 * 遍历SQL
	 */
	@Lob
	@Column(name = "RECURSIONSQL_", length = 4000)
	protected String recursionSql;

	/**
	 * 循环列,写到目标表的条件列
	 */
	@Column(name = "RECURSIONCOLUMNS_", length = 4000)
	protected String recursionColumns;

	/**
	 * 主键
	 */
	@Column(name = "PRIMARYKEY_", length = 50)
	protected String primaryKey;

	/**
	 * 表达式
	 */
	@Column(name = "EXPRESSION_", length = 500)
	protected String expression;

	/**
	 * 文件标识
	 */
	@Column(name = "FILEFLAG_", length = 1)
	protected String fileFlag;

	/**
	 * 文件路径列
	 */
	@Column(name = "FILEPATHCOLUMN_", length = 50)
	protected String filePathColumn;

	/**
	 * 文件名
	 */
	@Column(name = "FILENAMECOLUMN_", length = 50)
	protected String fileNameColumn;

	/**
	 * 图片合并标识
	 */
	@Column(name = "IMAGEMERGEFLAG_", length = 1)
	protected String imageMergeFlag;

	/**
	 * 图片合并方向
	 */
	@Column(name = "IMAGEMERGEDIRECTION_", length = 50)
	protected String imageMergeDirection;

	/**
	 * 合并后的图片类型
	 */
	@Column(name = "IMAGEMERGETARGETTYPE_", length = 20)
	protected String imageMergeTargetType;

	/**
	 * 加工后单张图片宽度
	 */
	@Column(name = "IMAGEWIDTH_")
	protected int imageWidth;

	/**
	 * 加工后单张图片高度
	 */
	@Column(name = "IMAGEHEIGHT_")
	protected int imageHeight;

	/**
	 * 图片的缩放比例
	 */
	@Column(name = "IMAGESCALE_")
	protected double imageScale;

	/**
	 * 图片的大小，超过这个大小的才缩放，默认单位为MB
	 */
	@Column(name = "IMAGESCALESIZE_")
	protected double imageScaleSize;

	/**
	 * 每单位图片数量
	 */
	@Column(name = "IMAGENUMPERUNIT_")
	protected int imageNumPerUnit;

	/**
	 * 根路径
	 */
	@Column(name = "ROOTPATH_", length = 200)
	protected String rootPath;

	/**
	 * 预处理程序
	 */
	@Column(name = "PREPROCESSORS_", length = 2000)
	protected String preprocessors;

	/**
	 * 变量模板
	 */
	@Lob
	@Column(name = "VAR_TEMPLATE_")
	protected String varTemplate;

	/**
	 * 变长区标识
	 */
	@Column(name = "VARIANTFLAG_", length = 1)
	protected String variantFlag;

	/**
	 * 上下文变量标识
	 */
	@Column(name = "CONTEXTVARFLAG_", length = 1)
	protected String contextVarFlag;

	/**
	 * 生成空白数据标识
	 */
	@Column(name = "GENEMPTYFLAG_", length = 1)
	protected String genEmptyFlag;

	/**
	 * 结果标识（O-单一记录，M-多条记录）
	 */
	@Column(name = "RESULTFLAG_", length = 1)
	protected String resultFlag;

	/**
	 * 数据处理器
	 */
	@Column(name = "DATAHANDLERCHAINS_", length = 500)
	protected String dataHandlerChains;

	/**
	 * 小计汇总标识
	 */
	@Column(name = "SUBTOTALFLAG_", length = 1)
	protected String subTotalFlag;

	/**
	 * 小计汇总列
	 */
	@Column(name = "SUBTOTALCOLUMN_", length = 50)
	protected String subTotalColumn;

	/**
	 * 自动换行列
	 */
	@Column(name = "LINEBREAKCOLUMN_", length = 50)
	protected String lineBreakColumn;

	/**
	 * 默认行高
	 */
	@Column(name = "LINEHEIGHT_")
	protected int lineHeight;

	/**
	 * 自动换行后每行字符数
	 */
	@Column(name = "CHARNUMPERROW_")
	protected int charNumPerRow;

	@Column(name = "PAGESIZE_")
	protected int pageSize;

	/**
	 * 顺序号
	 */
	@Column(name = "SORTNO_")
	protected int sortNo;

	/**
	 * 是否锁定
	 */
	@Column(name = "LOCKED_")
	protected int locked;

	/**
	 * 创建人
	 */
	@Column(name = "CREATEBY_", length = 50)
	protected String createBy;

	/**
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATETIME_")
	protected Date createTime;

	@javax.persistence.Transient
	protected Collection<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

	@javax.persistence.Transient
	protected JSONObject jsonData = null;

	public ExportItem() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportItem other = (ExportItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public int getCharNumPerRow() {
		return charNumPerRow;
	}

	public String getContextVarFlag() {
		return contextVarFlag;
	}

	public String getCreateBy() {
		return this.createBy;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public String getCreateTimeString() {
		if (this.createTime != null) {
			return DateUtils.getDateTime(this.createTime);
		}
		return "";
	}

	public String getDataHandlerChains() {
		return dataHandlerChains;
	}

	public Collection<Map<String, Object>> getDataList() {
		return dataList;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public String getDeploymentId() {
		return this.deploymentId;
	}

	public String getExpId() {
		return this.expId;
	}

	public String getExpression() {
		return expression;
	}

	public String getFileFlag() {
		return fileFlag;
	}

	public String getFileNameColumn() {
		if (fileNameColumn != null) {
			fileNameColumn = fileNameColumn.trim();
		}
		return fileNameColumn;
	}

	public String getFilePathColumn() {
		if (filePathColumn != null) {
			filePathColumn = filePathColumn.trim();
		}
		return filePathColumn;
	}

	public String getGenEmptyFlag() {
		return genEmptyFlag;
	}

	public String getId() {
		return this.id;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public String getImageMergeDirection() {
		return imageMergeDirection;
	}

	public String getImageMergeFlag() {
		return imageMergeFlag;
	}

	public String getImageMergeTargetType() {
		return imageMergeTargetType;
	}

	public int getImageNumPerUnit() {
		return imageNumPerUnit;
	}

	public double getImageScale() {
		return imageScale;
	}

	public double getImageScaleSize() {
		return imageScaleSize;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public JSONObject getJsonData() {
		return jsonData;
	}

	public String getLineBreakColumn() {
		return lineBreakColumn;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public int getLocked() {
		return locked;
	}

	public String getName() {
		if (name != null) {
			name = name.trim();
		}
		return name;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getPreprocessors() {
		return preprocessors;
	}

	public String getPrimaryKey() {
		if (primaryKey != null) {
			primaryKey = primaryKey.trim().toLowerCase();
		}
		return primaryKey;
	}

	public String getRecursionColumns() {
		if (recursionColumns != null) {
			recursionColumns = recursionColumns.trim().toLowerCase();
		}
		return recursionColumns;
	}

	public String getRecursionSql() {
		return recursionSql;
	}

	public String getResultFlag() {
		return resultFlag;
	}

	public String getRootPath() {
		return rootPath;
	}

	public int getSortNo() {
		return sortNo;
	}

	public String getSql() {
		return this.sql;
	}

	public String getSubTotalColumn() {
		return subTotalColumn;
	}

	public String getSubTotalFlag() {
		return subTotalFlag;
	}

	public String getTitle() {
		return title;
	}

	public String getVariantFlag() {
		return variantFlag;
	}

	public String getVarTemplate() {
		return varTemplate;
	}

	public String getXmlExpId() {
		return xmlExpId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public ExportItem jsonToObject(JSONObject jsonObject) {
		return ExportItemJsonFactory.jsonToObject(jsonObject);
	}

	public void setCharNumPerRow(int charNumPerRow) {
		this.charNumPerRow = charNumPerRow;
	}

	public void setContextVarFlag(String contextVarFlag) {
		this.contextVarFlag = contextVarFlag;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setDataHandlerChains(String dataHandlerChains) {
		this.dataHandlerChains = dataHandlerChains;
	}

	public void setDataList(Collection<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public void setExpId(String expId) {
		this.expId = expId;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setFileFlag(String fileFlag) {
		this.fileFlag = fileFlag;
	}

	public void setFileNameColumn(String fileNameColumn) {
		this.fileNameColumn = fileNameColumn;
	}

	public void setFilePathColumn(String filePathColumn) {
		this.filePathColumn = filePathColumn;
	}

	public void setGenEmptyFlag(String genEmptyFlag) {
		this.genEmptyFlag = genEmptyFlag;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public void setImageMergeDirection(String imageMergeDirection) {
		this.imageMergeDirection = imageMergeDirection;
	}

	public void setImageMergeFlag(String imageMergeFlag) {
		this.imageMergeFlag = imageMergeFlag;
	}

	public void setImageMergeTargetType(String imageMergeTargetType) {
		this.imageMergeTargetType = imageMergeTargetType;
	}

	public void setImageNumPerUnit(int imageNumPerUnit) {
		this.imageNumPerUnit = imageNumPerUnit;
	}

	public void setImageScale(double imageScale) {
		this.imageScale = imageScale;
	}

	public void setImageScaleSize(double imageScaleSize) {
		this.imageScaleSize = imageScaleSize;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public void setJsonData(JSONObject jsonData) {
		this.jsonData = jsonData;
	}

	public void setLineBreakColumn(String lineBreakColumn) {
		this.lineBreakColumn = lineBreakColumn;
	}

	public void setLineHeight(int lineHeight) {
		this.lineHeight = lineHeight;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPreprocessors(String preprocessors) {
		this.preprocessors = preprocessors;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setRecursionColumns(String recursionColumns) {
		this.recursionColumns = recursionColumns;
	}

	public void setRecursionSql(String recursionSql) {
		this.recursionSql = recursionSql;
	}

	public void setResultFlag(String resultFlag) {
		this.resultFlag = resultFlag;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setSubTotalColumn(String subTotalColumn) {
		this.subTotalColumn = subTotalColumn;
	}

	public void setSubTotalFlag(String subTotalFlag) {
		this.subTotalFlag = subTotalFlag;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setVariantFlag(String variantFlag) {
		this.variantFlag = variantFlag;
	}

	public void setVarTemplate(String varTemplate) {
		this.varTemplate = varTemplate;
	}

	public void setXmlExpId(String xmlExpId) {
		this.xmlExpId = xmlExpId;
	}

	public JSONObject toJsonObject() {
		return ExportItemJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return ExportItemJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
