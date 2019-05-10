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
@Table(name = "SYS_EXPORT")
public class ExportApp implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", length = 50, nullable = false)
	protected String id;

	/**
	 * 节点编号
	 */
	@Column(name = "NODEID_")
	protected long nodeId;

	/**
	 * 部署编号
	 */
	@Column(name = "DEPLOYMENTID_", length = 50)
	protected String deploymentId;

	/**
	 * 标题
	 */
	@Column(name = "TITLE_", length = 200)
	protected String title;

	/**
	 * 同步标识
	 */
	@Column(name = "SYNCFLAG_", length = 50)
	protected String syncFlag;

	/**
	 * 来源库编号
	 */
	@Column(name = "SRCDATABASEID_")
	protected long srcDatabaseId;

	/**
	 * 类型
	 */
	@Column(name = "TYPE_", length = 50)
	protected String type;

	/**
	 * 允许角色
	 */
	@Column(name = "ALLOWROLES_", length = 4000)
	protected String allowRoles;

	/**
	 * 模板编号
	 */
	@Column(name = "TEMPLATEID_", length = 50)
	protected String templateId;

	/**
	 * 外部列定义
	 */
	@Column(name = "EXTERNALCOLUMNSFLAG_", length = 50)
	protected String externalColumnsFlag;

	/**
	 * 导出文件名（表达式）
	 */
	@Column(name = "EXPORTFILEEXPR_", length = 250)
	protected String exportFileExpr;

	/**
	 * Excel后置处理器
	 */
	@Column(name = "EXCELPROCESSCHAINS_", length = 500)
	protected String excelProcessChains;

	/**
	 * 页高
	 */
	@Column(name = "PAGEHEIGHT_")
	protected int pageHeight;

	/**
	 * 每个Sheet的Paging对象数
	 */
	@Column(name = "PAGENUMPERSHEET_")
	protected int pageNumPerSheet;

	/**
	 * 分页变量名称
	 */
	@Column(name = "PAGEVARNAME_", length = 100)
	protected String pageVarName;

	/**
	 * 是否生成历史文件
	 */
	@Column(name = "HISTORYFLAG_", length = 1)
	protected String historyFlag;

	/**
	 * 是否生成多个文件
	 */
	@Column(name = "MULITIFLAG_", length = 1)
	protected String mulitiFlag;

	/**
	 * 是否定时调度
	 */
	@Column(name = "SHEDULERFLAG_", length = 1)
	protected String shedulerFlag;

	/**
	 * 时间间隔
	 */
	@Column(name = "INTERVAL_")
	protected int interval;

	/**
	 * 顺序号
	 */
	@Column(name = "SORTNO_")
	protected int sortNo;

	/**
	 * 是否有效
	 */
	@Column(name = "ACTIVE_", length = 1)
	protected String active;

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
	 * 更新人
	 */
	@Column(name = "UPDATEBY_", length = 50)
	protected String updateBy;

	/**
	 * 更新时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATETIME_")
	protected Date updateTime;

	@javax.persistence.Transient
	protected String useExt;

	@javax.persistence.Transient
	protected List<ExportItem> items = new ArrayList<ExportItem>();

	@javax.persistence.Transient
	protected List<ExportTemplateVar> variables = new ArrayList<ExportTemplateVar>();

	public ExportApp() {

	}

	public void addItem(ExportItem item) {
		if (items == null) {
			items = new ArrayList<ExportItem>();
		}
		items.add(item);
	}

	public void addVariable(ExportTemplateVar var) {
		if (variables == null) {
			variables = new ArrayList<ExportTemplateVar>();
		}
		variables.add(var);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportApp other = (ExportApp) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getActive() {
		return active;
	}

	public String getAllowRoles() {
		return allowRoles;
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

	public String getDeploymentId() {
		return this.deploymentId;
	}

	public String getExcelProcessChains() {
		return excelProcessChains;
	}

	public String getExportFileExpr() {
		return exportFileExpr;
	}

	public String getExternalColumnsFlag() {
		return externalColumnsFlag;
	}

	public String getHistoryFlag() {
		return historyFlag;
	}

	public String getId() {
		return this.id;
	}

	public int getInterval() {
		return this.interval;
	}

	public List<ExportItem> getItems() {
		return items;
	}

	public String getMulitiFlag() {
		return mulitiFlag;
	}

	public long getNodeId() {
		return this.nodeId;
	}

	public int getPageHeight() {
		return pageHeight;
	}

	public int getPageNumPerSheet() {
		return pageNumPerSheet;
	}

	public String getPageVarName() {
		return pageVarName;
	}

	public String getShedulerFlag() {
		return shedulerFlag;
	}

	public int getSortNo() {
		return sortNo;
	}

	public long getSrcDatabaseId() {
		return this.srcDatabaseId;
	}

	public String getSyncFlag() {
		return this.syncFlag;
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public String getTitle() {
		return this.title;
	}

	public String getType() {
		return this.type;
	}

	public String getUpdateBy() {
		return this.updateBy;
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

	public String getUseExt() {
		return useExt;
	}

	public List<ExportTemplateVar> getVariables() {
		return variables;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public ExportApp jsonToObject(JSONObject jsonObject) {
		return ExportAppJsonFactory.jsonToObject(jsonObject);
	}

	public void setActive(String active) {
		this.active = active;
	}

	public void setAllowRoles(String allowRoles) {
		this.allowRoles = allowRoles;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public void setExcelProcessChains(String excelProcessChains) {
		this.excelProcessChains = excelProcessChains;
	}

	public void setExportFileExpr(String exportFileExpr) {
		this.exportFileExpr = exportFileExpr;
	}

	public void setExternalColumnsFlag(String externalColumnsFlag) {
		this.externalColumnsFlag = externalColumnsFlag;
	}

	public void setHistoryFlag(String historyFlag) {
		this.historyFlag = historyFlag;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setItems(List<ExportItem> items) {
		this.items = items;
	}

	public void setMulitiFlag(String mulitiFlag) {
		this.mulitiFlag = mulitiFlag;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public void setPageHeight(int pageHeight) {
		this.pageHeight = pageHeight;
	}

	public void setPageNumPerSheet(int pageNumPerSheet) {
		this.pageNumPerSheet = pageNumPerSheet;
	}

	public void setPageVarName(String pageVarName) {
		this.pageVarName = pageVarName;
	}

	public void setShedulerFlag(String shedulerFlag) {
		this.shedulerFlag = shedulerFlag;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	public void setSrcDatabaseId(long srcDatabaseId) {
		this.srcDatabaseId = srcDatabaseId;
	}

	public void setSyncFlag(String syncFlag) {
		this.syncFlag = syncFlag;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setUseExt(String useExt) {
		this.useExt = useExt;
	}

	public void setVariables(List<ExportTemplateVar> variables) {
		this.variables = variables;
	}

	public JSONObject toJsonObject() {
		return ExportAppJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return ExportAppJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
