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
@Table(name = "SYS_EXPORT_CHART")
public class ExportChart implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", length = 50, nullable = false)
	protected String id;

	/**
	 * 导出编号
	 */
	@Column(name = "EXPID_", length = 50)
	protected String expId;

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
	 * 高度
	 */
	@Column(name = "HEIGHT_")
	protected int height;

	/**
	 * 宽度
	 */
	@Column(name = "WIDTH_")
	protected int width;

	/**
	 * 图像类型
	 */
	@Column(name = "IMAGETYPE_", length = 50)
	protected String imageType;

	/**
	 * 图表编号
	 */
	@Column(name = "CHARTID_", length = 50)
	protected String chartId;

	/**
	 * 图表类型
	 */
	@Column(name = "CHARTTYPE_", length = 50)
	protected String chartType;

	/**
	 * 图表地址
	 */
	@Column(name = "CHARTURL_", length = 1000)
	protected String chartUrl;

	/**
	 * 截图标记
	 */
	@Column(name = "SNAPSHOTFLAG_", length = 1)
	protected String snapshotFlag;

	/**
	 * 是否启用
	 */
	@Column(name = "LOCKED_")
	protected int locked;

	@Column(name = "CREATEBY_", length = 50)
	protected String createBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATETIME_")
	protected Date createTime;

	public ExportChart() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportChart other = (ExportChart) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getChartId() {
		return this.chartId;
	}

	public String getChartType() {
		return chartType;
	}

	public String getChartUrl() {
		return this.chartUrl;
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

	public String getExpId() {
		return this.expId;
	}

	public int getHeight() {
		return this.height;
	}

	public String getId() {
		return this.id;
	}

	public String getImageType() {
		return this.imageType;
	}

	public int getLocked() {
		return this.locked;
	}

	public String getName() {
		return this.name;
	}

	public String getSnapshotFlag() {
		return this.snapshotFlag;
	}

	public String getTitle() {
		return this.title;
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public ExportChart jsonToObject(JSONObject jsonObject) {
		return ExportChartJsonFactory.jsonToObject(jsonObject);
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public void setChartUrl(String chartUrl) {
		this.chartUrl = chartUrl;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setExpId(String expId) {
		this.expId = expId;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSnapshotFlag(String snapshotFlag) {
		this.snapshotFlag = snapshotFlag;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public JSONObject toJsonObject() {
		return ExportChartJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return ExportChartJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
