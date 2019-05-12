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
import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.glaf.core.base.*;
import com.glaf.core.util.DateUtils;
import com.glaf.chart.util.*;

/**
 * 
 * 实体对象
 *
 */

@Entity
@Table(name = "SYS_CHART_COMBINATION")
public class ChartCombination implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", length = 50, nullable = false)
	protected String id;

	@Column(name = "NAME_", length = 50)
	protected String name;

	@Column(name = "TITLE_", length = 200)
	protected String title;

	@Column(name = "SERVICEKEY_", length = 50)
	protected String serviceKey;

	@Column(name = "TYPE_", length = 50)
	protected String type;

	@Column(name = "CATEGORY_", length = 200)
	protected String category;

	@Column(name = "CHARTIDS_", length = 500)
	protected String chartIds;

	@Column(name = "DIRECTION_", length = 1)
	protected String direction;

	@Column(name = "CREATEBY_", length = 50)
	protected String createBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATETIME_")
	protected Date createTime;

	public ChartCombination() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChartCombination other = (ChartCombination) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getCategory() {
		return this.category;
	}

	public String getChartIds() {
		return this.chartIds;
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

	public String getDirection() {
		return this.direction;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getServiceKey() {
		return this.serviceKey;
	}

	public String getTitle() {
		return this.title;
	}

	public String getType() {
		return this.type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public ChartCombination jsonToObject(JSONObject jsonObject) {
		return ChartCombinationJsonFactory.jsonToObject(jsonObject);
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setChartIds(String chartIds) {
		this.chartIds = chartIds;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
	}

	public JSONObject toJsonObject() {
		return ChartCombinationJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return ChartCombinationJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
