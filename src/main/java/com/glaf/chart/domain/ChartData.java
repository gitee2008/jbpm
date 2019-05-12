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

import javax.persistence.*;
import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.glaf.core.base.*;
import com.glaf.chart.util.*;

/**
 * 
 * 实体对象
 *
 */

@Entity
@Table(name = "SYS_CHART_DATA")
public class ChartData implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", length = 50, nullable = false)
	protected String id;

	@Column(name = "SERVICEKEY_", length = 50)
	protected String serviceKey;

	@Column(name = "TYPE_", length = 50)
	protected String type;

	@Column(name = "CATEGORY_", length = 200)
	protected String category;

	@Column(name = "SERIES_", length = 200)
	protected String series;

	@Column(name = "VALUE_")
	protected Double value;

	public ChartData() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChartData other = (ChartData) obj;
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

	public String getId() {
		return this.id;
	}

	public String getSeries() {
		return this.series;
	}

	public String getServiceKey() {
		return this.serviceKey;
	}

	public String getType() {
		return this.type;
	}

	public Double getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public ChartData jsonToObject(JSONObject jsonObject) {
		return ChartDataJsonFactory.jsonToObject(jsonObject);
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public JSONObject toJsonObject() {
		return ChartDataJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return ChartDataJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
