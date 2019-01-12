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

package com.glaf.matrix.parameter.domain;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.glaf.core.base.*;
import com.glaf.core.util.DateUtils;
import com.glaf.matrix.parameter.util.*;

/**
 * 
 * 实体对象
 *
 */

@Entity
@Table(name = "SYS_PARAMETER_CONVERSION")
public class ParameterConversion implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", length = 64, nullable = false)
	protected String id;

	/**
	 * 业务Key
	 */
	@Column(name = "KEY_", length = 64)
	protected String key;

	/**
	 * 标题
	 */
	@Column(name = "TITLE_", length = 200)
	protected String title;

	/**
	 * 来源名称
	 */
	@Column(name = "SOURCENAME_", length = 200)
	protected String sourceName;

	/**
	 * 来源类型
	 */
	@Column(name = "SOURCETYPE_", length = 50)
	protected String sourceType;

	/**
	 * 集合标识
	 */
	@Column(name = "SOURCELISTFLAG_", length = 1)
	protected String sourceListFlag;

	/**
	 * 目标名称
	 */
	@Column(name = "TARGETNAME_", length = 200)
	protected String targetName;

	/**
	 * 目标类型
	 */
	@Column(name = "TARGETTYPE_", length = 50)
	protected String targetType;

	/**
	 * 集合标识
	 */
	@Column(name = "TARGETLISTFLAG_", length = 1)
	protected String targetListFlag;

	/**
	 * 分隔符
	 */
	@Column(name = "DELIMITER_", length = 50)
	protected String delimiter;

	/**
	 * 转换条件
	 */
	@Column(name = "CONVERTCONDITION_", length = 200)
	protected String convertCondition;

	/**
	 * 转换类型
	 */
	@Column(name = "CONVERTTYPE_", length = 200)
	protected String convertType;

	/**
	 * 转换表达式
	 */
	@Column(name = "CONVERTEXPRESSION_", length = 2000)
	protected String convertExpression;

	/**
	 * 转换模板
	 */
	@Column(name = "CONVERTTEMPLATE_", length = 2000)
	protected String convertTemplate;

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

	public ParameterConversion() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParameterConversion other = (ParameterConversion) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getConvertCondition() {
		return convertCondition;
	}

	public String getConvertExpression() {
		return convertExpression;
	}

	public String getConvertTemplate() {
		return this.convertTemplate;
	}

	public String getConvertType() {
		return convertType;
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

	public String getDelimiter() {
		return this.delimiter;
	}

	public String getId() {
		return this.id;
	}

	public String getKey() {
		return this.key;
	}

	public String getSourceListFlag() {
		return this.sourceListFlag;
	}

	public String getSourceName() {
		return this.sourceName;
	}

	public String getSourceType() {
		return this.sourceType;
	}

	public String getTargetListFlag() {
		return this.targetListFlag;
	}

	public String getTargetName() {
		return this.targetName;
	}

	public String getTargetType() {
		return this.targetType;
	}

	public String getTitle() {
		return this.title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public ParameterConversion jsonToObject(JSONObject jsonObject) {
		return ParameterConversionJsonFactory.jsonToObject(jsonObject);
	}

	public void setConvertCondition(String convertCondition) {
		this.convertCondition = convertCondition;
	}

	public void setConvertExpression(String convertExpression) {
		this.convertExpression = convertExpression;
	}

	public void setConvertTemplate(String convertTemplate) {
		this.convertTemplate = convertTemplate;
	}

	public void setConvertType(String convertType) {
		this.convertType = convertType;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setSourceListFlag(String sourceListFlag) {
		this.sourceListFlag = sourceListFlag;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public void setTargetListFlag(String targetListFlag) {
		this.targetListFlag = targetListFlag;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public JSONObject toJsonObject() {
		return ParameterConversionJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return ParameterConversionJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
