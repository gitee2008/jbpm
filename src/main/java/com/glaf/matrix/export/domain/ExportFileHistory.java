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
@Table(name = "SYS_EXPORT_FILE_HISTORY")
public class ExportFileHistory implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", length = 64, nullable = false)
	protected String id;

	/**
	 * ExpId
	 */
	@Column(name = "EXPID_", length = 50)
	protected String expId;

	/**
	 * DeploymentId
	 */
	@Column(name = "DEPLOYMENTID_", length = 50)
	protected String deploymentId;

	/**
	 * 标题
	 */
	@Column(name = "TITLE_", length = 200)
	protected String title;

	/**
	 * 文件名
	 */
	@Column(name = "FILENAME_", length = 500)
	protected String filename;

	/**
	 * 报表路径
	 */
	@Column(name = "PATH_", length = 500)
	protected String path;

	@javax.persistence.Transient
	protected byte[] data;

	/**
	 * 批次编号
	 */
	@Column(name = "JOBNO_", length = 200)
	protected String jobNo;

	/**
	 * 生成日期
	 */
	@Column(name = "GENYMD_")
	protected int genYmd;

	@Column(name = "LASTMODIFIED_")
	protected long lastModified = -1;

	/**
	 * 排序号
	 */
	@Column(name = "SORTNO_")
	protected int sortNo;

	/**
	 * 查看次数
	 */
	@Column(name = "VIEWCOUNT_")
	protected int viewCount;

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

	public ExportFileHistory() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportFileHistory other = (ExportFileHistory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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

	public byte[] getData() {
		return data;
	}

	public String getDeploymentId() {
		return this.deploymentId;
	}

	public String getExpId() {
		return this.expId;
	}

	public String getFilename() {
		return this.filename;
	}

	public int getGenYmd() {
		return this.genYmd;
	}

	public String getId() {
		return this.id;
	}

	public String getJobNo() {
		return this.jobNo;
	}

	public long getLastModified() {
		return lastModified;
	}

	public String getPath() {
		return this.path;
	}

	public int getSortNo() {
		return this.sortNo;
	}

	public String getTitle() {
		return this.title;
	}

	public int getViewCount() {
		return this.viewCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public ExportFileHistory jsonToObject(JSONObject jsonObject) {
		return ExportFileHistoryJsonFactory.jsonToObject(jsonObject);
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public void setExpId(String expId) {
		this.expId = expId;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setGenYmd(int genYmd) {
		this.genYmd = genYmd;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setJobNo(String jobNo) {
		this.jobNo = jobNo;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public JSONObject toJsonObject() {
		return ExportFileHistoryJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return ExportFileHistoryJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
