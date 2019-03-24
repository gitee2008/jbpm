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

package com.glaf.matrix.adjust.domain;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.glaf.core.base.*;
import com.glaf.core.util.DateUtils;
import com.glaf.matrix.adjust.util.*;

/**
 * 
 * 实体对象
 *
 */

@Entity
@Table(name = "SYS_TREE_DATA_ADJUST")
public class TreeDataAdjust implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", length = 64, nullable = false)
	protected String id;

	/**
	 * 名称
	 */
	@Column(name = "NAME_", length = 200)
	protected String name;

	/**
	 * 标题
	 */
	@Column(name = "TITLE_", length = 200)
	protected String title;

	/**
	 * 数据库编号
	 */
	@Column(name = "DATABASEID_")
	protected long databaseId;

	/**
	 * 表名
	 */
	@Column(name = "TABLENAME_", length = 50)
	protected String tableName;

	/**
	 * 主键列
	 */
	@Column(name = "PRIMARYKEY_", length = 50)
	protected String primaryKey;

	/**
	 * id列
	 */
	@Column(name = "IDCOLUMN_", length = 50)
	protected String idColumn;

	/**
	 * parentId列
	 */
	@Column(name = "PARENTIDCOLUMN_", length = 50)
	protected String parentIdColumn;

	/**
	 * treeId列
	 */
	@Column(name = "TREEIDCOLUMN_", length = 50)
	protected String treeIdColumn;

	/**
	 * treeId分隔符
	 */
	@Column(name = "TREEIDDELIMITER_", length = 50)
	protected String treeIdDelimiter;

	/**
	 * 名称列
	 */
	@Column(name = "NAMECOLUMN_", length = 50)
	protected String nameColumn;

	/**
	 * 调整列（多列之间用逗号,隔开）
	 */
	@Column(name = "ADJUSTCOLUMN_", length = 500)
	protected String adjustColumn;

	/**
	 * 调整类型
	 */
	@Column(name = "ADJUSTTYPE_", length = 50)
	protected String adjustType;

	/**
	 * 连接符
	 */
	@Column(name = "CONNECTOR_", length = 50)
	protected String connector;

	/**
	 * Freemarker表达式
	 */
	@Column(name = "EXPRESSION_", length = 500)
	protected String expression;

	/**
	 * And条件
	 */
	@Column(name = "SQLCRITERIA_", length = 4000)
	protected String sqlCriteria;

	/**
	 * 目标表名
	 */
	@Column(name = "TARGETTABLENAME_", length = 100)
	protected String targetTableName;

	/**
	 * 更新标识
	 */
	@Column(name = "UPDATEFLAG_", length = 1)
	protected String updateFlag;

	/**
	 * 每次插入前删除
	 */
	@Column(name = "DELETEFETCH_", length = 1)
	protected String deleteFetch;

	/**
	 * 限制叶节点
	 */
	@Column(name = "LEAFLIMITFLAG_", length = 1)
	protected String leafLimitFlag;

	/**
	 * 并行处理
	 */
	@Column(name = "FORKJOINFLAG_", length = 1)
	protected String forkJoinFlag;

	/**
	 * 预处理
	 */
	@Column(name = "PREPROCESSFLAG_", length = 1)
	protected String preprocessFlag;

	/**
	 * 是否自动调度
	 */
	@Column(name = "SCHEDULEFLAG_", length = 1)
	protected String scheduleFlag;

	/**
	 * 类型
	 */
	@Column(name = "TYPE_", length = 50)
	protected String type;

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

	/**
	 * 修改人
	 */
	@Column(name = "UPDATEBY_", length = 50)
	protected String updateBy;

	/**
	 * 修改时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATETIME_")
	protected Date updateTime;

	public TreeDataAdjust() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeDataAdjust other = (TreeDataAdjust) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getAdjustColumn() {
		if (adjustColumn != null) {
			adjustColumn = adjustColumn.toLowerCase().trim();
		}
		return this.adjustColumn;
	}

	public String getAdjustType() {
		return this.adjustType;
	}

	public String getConnector() {
		return connector;
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

	public long getDatabaseId() {
		return this.databaseId;
	}

	public String getDeleteFetch() {
		return deleteFetch;
	}

	public String getExpression() {
		return expression;
	}

	public String getForkJoinFlag() {
		return forkJoinFlag;
	}

	public String getId() {
		return this.id;
	}

	public String getIdColumn() {
		if (idColumn != null) {
			idColumn = idColumn.toLowerCase().trim();
		}
		return this.idColumn;
	}

	public String getLeafLimitFlag() {
		return leafLimitFlag;
	}

	public int getLocked() {
		return this.locked;
	}

	public String getName() {
		return this.name;
	}

	public String getNameColumn() {
		if (nameColumn != null) {
			nameColumn = nameColumn.toLowerCase().trim();
		}
		return nameColumn;
	}

	public String getParentIdColumn() {
		if (parentIdColumn != null) {
			parentIdColumn = parentIdColumn.toLowerCase().trim();
		}
		return this.parentIdColumn;
	}

	public String getPreprocessFlag() {
		return this.preprocessFlag;
	}

	public String getPrimaryKey() {
		if (primaryKey != null) {
			primaryKey = primaryKey.toLowerCase().trim();
		}
		return this.primaryKey;
	}

	public String getScheduleFlag() {
		return scheduleFlag;
	}

	public String getSqlCriteria() {
		return sqlCriteria;
	}

	public String getTableName() {
		return this.tableName;
	}

	public String getTargetTableName() {
		return targetTableName;
	}

	public String getTitle() {
		return this.title;
	}

	public String getTreeIdColumn() {
		if (treeIdColumn != null) {
			treeIdColumn = treeIdColumn.toLowerCase().trim();
		}
		return this.treeIdColumn;
	}

	public String getTreeIdDelimiter() {
		return this.treeIdDelimiter;
	}

	public String getType() {
		return this.type;
	}

	public String getUpdateBy() {
		return this.updateBy;
	}

	public String getUpdateFlag() {
		return updateFlag;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public String getUpdateTimeString() {
		if (this.updateTime != null) {
			return DateUtils.getDateTime(this.updateTime);
		}
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public TreeDataAdjust jsonToObject(JSONObject jsonObject) {
		return TreeDataAdjustJsonFactory.jsonToObject(jsonObject);
	}

	public void setAdjustColumn(String adjustColumn) {
		this.adjustColumn = adjustColumn;
	}

	public void setAdjustType(String adjustType) {
		this.adjustType = adjustType;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setDatabaseId(long databaseId) {
		this.databaseId = databaseId;
	}

	public void setDeleteFetch(String deleteFetch) {
		this.deleteFetch = deleteFetch;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setForkJoinFlag(String forkJoinFlag) {
		this.forkJoinFlag = forkJoinFlag;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIdColumn(String idColumn) {
		this.idColumn = idColumn;
	}

	public void setLeafLimitFlag(String leafLimitFlag) {
		this.leafLimitFlag = leafLimitFlag;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNameColumn(String nameColumn) {
		this.nameColumn = nameColumn;
	}

	public void setParentIdColumn(String parentIdColumn) {
		this.parentIdColumn = parentIdColumn;
	}

	public void setPreprocessFlag(String preprocessFlag) {
		this.preprocessFlag = preprocessFlag;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setScheduleFlag(String scheduleFlag) {
		this.scheduleFlag = scheduleFlag;
	}

	public void setSqlCriteria(String sqlCriteria) {
		this.sqlCriteria = sqlCriteria;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTreeIdColumn(String treeIdColumn) {
		this.treeIdColumn = treeIdColumn;
	}

	public void setTreeIdDelimiter(String treeIdDelimiter) {
		this.treeIdDelimiter = treeIdDelimiter;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public JSONObject toJsonObject() {
		return TreeDataAdjustJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return TreeDataAdjustJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
