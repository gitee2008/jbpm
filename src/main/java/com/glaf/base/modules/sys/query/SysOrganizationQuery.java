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

package com.glaf.base.modules.sys.query;

import java.util.*;
import com.glaf.core.query.DataQuery;

public class SysOrganizationQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected String code;
	protected String code2;
	protected String code2LeftLike;
	protected String code2Like;
	protected String code2RightLike;
	protected String codeLeftLike;
	protected String codeLike;
	protected String codeRightLike;
	protected List<String> codes;
	protected Date createTimeGreaterThanOrEqual;
	protected Date createTimeLessThanOrEqual;
	protected String descriptionLike;
	protected String fincode;
	protected String fincodeLeftLike;
	protected String fincodeLike;
	protected String fincodeRightLike;
	protected Integer level;
	protected Integer levelGreaterThanOrEqual;
	protected Integer levelLessThanOrEqual;
	protected String name;
	protected String nameLike;
	protected String namePinyinLike;
	protected List<String> names;
	protected String type;
	protected String no;
	protected List<Long> organizationIds;
	protected String noLeftLike;
	protected String noLike;
	protected String noRightLike;
	protected Integer sort;
	protected Integer sortGreaterThan;
	protected Integer sortGreaterThanOrEqual;
	protected Integer sortLessThan;
	protected Integer sortLessThanOrEqual;
	protected String treeIdLeftLike;
	protected String treeIdRightLike;

	public SysOrganizationQuery() {

	}

	public SysOrganizationQuery code(String code) {
		if (code == null) {
			throw new RuntimeException("code is null");
		}
		this.code = code;
		return this;
	}

	public SysOrganizationQuery code2(String code2) {
		if (code2 == null) {
			throw new RuntimeException("code2 is null");
		}
		this.code2 = code2;
		return this;
	}

	public SysOrganizationQuery codeLike(String codeLike) {
		if (codeLike == null) {
			throw new RuntimeException("code is null");
		}
		this.codeLike = codeLike;
		return this;
	}

	public SysOrganizationQuery codes(List<String> codes) {
		if (codes == null) {
			throw new RuntimeException("codes is empty ");
		}
		this.codes = codes;
		return this;
	}

	public SysOrganizationQuery createTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		if (createTimeGreaterThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
		return this;
	}

	public SysOrganizationQuery createTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		if (createTimeLessThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
		return this;
	}

	public SysOrganizationQuery descriptionLike(String descriptionLike) {
		if (descriptionLike == null) {
			throw new RuntimeException("description is null");
		}
		this.descriptionLike = descriptionLike;
		return this;
	}

	public SysOrganizationQuery fincode(String fincode) {
		if (fincode == null) {
			throw new RuntimeException("fincode is null");
		}
		this.fincode = fincode;
		return this;
	}

	public SysOrganizationQuery fincodeLike(String fincodeLike) {
		if (fincodeLike == null) {
			throw new RuntimeException("fincode is null");
		}
		this.fincodeLike = fincodeLike;
		return this;
	}

	public String getCode() {
		return code;
	}

	public String getCode2() {
		return code2;
	}

	public String getCode2LeftLike() {
		if (code2LeftLike != null && code2LeftLike.trim().length() > 0) {
			if (!code2LeftLike.endsWith("%")) {
				code2LeftLike = codeLeftLike + "%";
			}
		}
		return code2LeftLike;
	}

	public String getCode2Like() {
		if (code2Like != null && code2Like.trim().length() > 0) {
			if (!code2Like.startsWith("%")) {
				code2Like = "%" + code2Like;
			}
			if (!code2Like.endsWith("%")) {
				code2Like = code2Like + "%";
			}
		}
		return code2Like;
	}

	public String getCode2RightLike() {
		if (code2RightLike != null && code2RightLike.trim().length() > 0) {
			if (!code2RightLike.startsWith("%")) {
				code2RightLike = "%" + code2RightLike;
			}
		}
		return code2RightLike;
	}

	public String getCodeLeftLike() {
		if (codeLeftLike != null && codeLeftLike.trim().length() > 0) {
			if (!codeLeftLike.endsWith("%")) {
				codeLeftLike = codeLeftLike + "%";
			}
		}
		return codeLeftLike;
	}

	public String getCodeLike() {
		if (codeLike != null && codeLike.trim().length() > 0) {
			if (!codeLike.startsWith("%")) {
				codeLike = "%" + codeLike;
			}
			if (!codeLike.endsWith("%")) {
				codeLike = codeLike + "%";
			}
		}
		return codeLike;
	}

	public String getCodeRightLike() {
		if (codeRightLike != null && codeRightLike.trim().length() > 0) {
			if (!codeRightLike.startsWith("%")) {
				codeRightLike = "%" + codeRightLike;
			}
		}
		return codeRightLike;
	}

	public List<String> getCodes() {
		return codes;
	}

	public Date getCreateTimeGreaterThanOrEqual() {
		return createTimeGreaterThanOrEqual;
	}

	public Date getCreateTimeLessThanOrEqual() {
		return createTimeLessThanOrEqual;
	}

	public String getDescriptionLike() {
		if (descriptionLike != null && descriptionLike.trim().length() > 0) {
			if (!descriptionLike.startsWith("%")) {
				descriptionLike = "%" + descriptionLike;
			}
			if (!descriptionLike.endsWith("%")) {
				descriptionLike = descriptionLike + "%";
			}
		}
		return descriptionLike;
	}

	public String getFincode() {
		return fincode;
	}

	public String getFincodeLeftLike() {
		if (fincodeLeftLike != null && fincodeLeftLike.trim().length() > 0) {
			if (!fincodeLeftLike.endsWith("%")) {
				fincodeLeftLike = fincodeLeftLike + "%";
			}
		}
		return fincodeLeftLike;
	}

	public String getFincodeLike() {
		if (fincodeLike != null && fincodeLike.trim().length() > 0) {
			if (!fincodeLike.startsWith("%")) {
				fincodeLike = "%" + fincodeLike;
			}
			if (!fincodeLike.endsWith("%")) {
				fincodeLike = fincodeLike + "%";
			}
		}
		return fincodeLike;
	}

	public String getFincodeRightLike() {
		if (fincodeRightLike != null && fincodeRightLike.trim().length() > 0) {
			if (!fincodeRightLike.startsWith("%")) {
				fincodeRightLike = "%" + fincodeRightLike;
			}
		}
		return fincodeRightLike;
	}

	public Integer getLevel() {
		return level;
	}

	public Integer getLevelGreaterThanOrEqual() {
		return levelGreaterThanOrEqual;
	}

	public Integer getLevelLessThanOrEqual() {
		return levelLessThanOrEqual;
	}

	public String getName() {
		return name;
	}

	public String getNameLike() {
		if (nameLike != null && nameLike.trim().length() > 0) {
			if (!nameLike.startsWith("%")) {
				nameLike = "%" + nameLike;
			}
			if (!nameLike.endsWith("%")) {
				nameLike = nameLike + "%";
			}
		}
		return nameLike;
	}

	public String getNamePinyinLike() {
		if (namePinyinLike != null && namePinyinLike.trim().length() > 0) {
			if (!namePinyinLike.endsWith("%")) {
				namePinyinLike = namePinyinLike + "%";
			}
		}
		return namePinyinLike;
	}

	public List<String> getNames() {
		return names;
	}

	public String getNo() {
		return no;
	}

	public String getNoLeftLike() {
		if (noLeftLike != null && noLeftLike.trim().length() > 0) {
			if (!noLeftLike.endsWith("%")) {
				noLeftLike = noLeftLike + "%";
			}
		}
		return noLeftLike;
	}

	public String getNoLike() {
		if (noLike != null && noLike.trim().length() > 0) {
			if (!noLike.startsWith("%")) {
				noLike = "%" + noLike;
			}
			if (!noLike.endsWith("%")) {
				noLike = noLike + "%";
			}
		}
		return noLike;
	}

	public String getNoRightLike() {
		if (noRightLike != null && noRightLike.trim().length() > 0) {
			if (!noRightLike.startsWith("%")) {
				noRightLike = "%" + noLike;
			}
		}
		return noRightLike;
	}

	public String getOrderBy() {
		if (sortColumn != null) {
			String a_x = " asc ";
			if (sortOrder != null) {
				a_x = sortOrder;
			}

			if ("name".equals(sortColumn)) {
				orderBy = "E.NAME" + a_x;
			}

			if ("description".equals(sortColumn)) {
				orderBy = "E.ORG_DESC" + a_x;
			}

			if ("createTime".equals(sortColumn)) {
				orderBy = "E.CREATETIME" + a_x;
			}

			if ("sort".equals(sortColumn)) {
				orderBy = "E.SORTNO" + a_x;
			}

			if ("no".equals(sortColumn)) {
				orderBy = "E.ORG_NO" + a_x;
			}

			if ("code".equals(sortColumn)) {
				orderBy = "E.CODE" + a_x;
			}

			if ("code2".equals(sortColumn)) {
				orderBy = "E.CODE2" + a_x;
			}

			if ("locked".equals(sortColumn)) {
				orderBy = "E.LOCKED" + a_x;
			}

			if ("fincode".equals(sortColumn)) {
				orderBy = "E.FINCODE" + a_x;
			}

		}
		return orderBy;
	}

	public List<Long> getOrganizationIds() {
		return organizationIds;
	}

	public Integer getSort() {
		return sort;
	}

	public Integer getSortGreaterThan() {
		return sortGreaterThan;
	}

	public Integer getSortGreaterThanOrEqual() {
		return sortGreaterThanOrEqual;
	}

	public Integer getSortLessThan() {
		return sortLessThan;
	}

	public Integer getSortLessThanOrEqual() {
		return sortLessThanOrEqual;
	}

	public String getTreeIdLeftLike() {
		if (treeIdLeftLike != null && treeIdLeftLike.trim().length() > 0) {
			if (!treeIdLeftLike.endsWith("%")) {
				treeIdLeftLike = treeIdLeftLike + "%";
			}
		}

		return treeIdLeftLike;
	}

	public String getTreeIdRightLike() {
		if (treeIdRightLike != null && treeIdRightLike.trim().length() > 0) {
			if (!treeIdRightLike.startsWith("%")) {
				treeIdRightLike = "%" + treeIdRightLike;
			}
		}

		return treeIdRightLike;
	}

	public String getType() {
		return type;
	}

	@Override
	public void initQueryColumns() {
		super.initQueryColumns();
		addColumn("id", "ID");
		addColumn("name", "NAME");
		addColumn("description", "ORG_DESC");
		addColumn("createTime", "CREATETIME");
		addColumn("sort", "SORTNO");
		addColumn("no", "ORG_NO");
		addColumn("code", "CODE");
		addColumn("code2", "CODE2");
		addColumn("locked", "LOCKED");
		addColumn("fincode", "FINCODE");
		addColumn("nodeId", "NODEID");
	}

	public SysOrganizationQuery level(Integer level) {
		if (level == null) {
			throw new RuntimeException("level is null");
		}
		this.level = level;
		return this;
	}

	public SysOrganizationQuery levelGreaterThanOrEqual(Integer levelGreaterThanOrEqual) {
		if (levelGreaterThanOrEqual == null) {
			throw new RuntimeException("level is null");
		}
		this.levelGreaterThanOrEqual = levelGreaterThanOrEqual;
		return this;
	}

	public SysOrganizationQuery levelLessThanOrEqual(Integer levelLessThanOrEqual) {
		if (levelLessThanOrEqual == null) {
			throw new RuntimeException("level is null");
		}
		this.levelLessThanOrEqual = levelLessThanOrEqual;
		return this;
	}

	public SysOrganizationQuery name(String name) {
		if (name == null) {
			throw new RuntimeException("name is null");
		}
		this.name = name;
		return this;
	}

	public SysOrganizationQuery nameLike(String nameLike) {
		if (nameLike == null) {
			throw new RuntimeException("name is null");
		}
		this.nameLike = nameLike;
		return this;
	}

	public SysOrganizationQuery namePinyinLike(String namePinyinLike) {
		if (namePinyinLike == null) {
			throw new RuntimeException("namePinyinLike is null");
		}
		this.namePinyinLike = namePinyinLike;
		return this;
	}

	public SysOrganizationQuery names(List<String> names) {
		if (names == null) {
			throw new RuntimeException("names is empty ");
		}
		this.names = names;
		return this;
	}

	public SysOrganizationQuery no(String no) {
		if (no == null) {
			throw new RuntimeException("no is null");
		}
		this.no = no;
		return this;
	}

	public SysOrganizationQuery noLike(String noLike) {
		if (noLike == null) {
			throw new RuntimeException("no is null");
		}
		this.noLike = noLike;
		return this;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCode2(String code2) {
		this.code2 = code2;
	}

	public void setCode2LeftLike(String code2LeftLike) {
		this.code2LeftLike = code2LeftLike;
	}

	public void setCode2Like(String code2Like) {
		this.code2Like = code2Like;
	}

	public void setCode2RightLike(String code2RightLike) {
		this.code2RightLike = code2RightLike;
	}

	public void setCodeLeftLike(String codeLeftLike) {
		this.codeLeftLike = codeLeftLike;
	}

	public void setCodeLike(String codeLike) {
		this.codeLike = codeLike;
	}

	public void setCodeRightLike(String codeRightLike) {
		this.codeRightLike = codeRightLike;
	}

	public void setCodes(List<String> codes) {
		this.codes = codes;
	}

	public void setCreateTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
	}

	public void setCreateTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
	}

	public void setDescriptionLike(String descriptionLike) {
		this.descriptionLike = descriptionLike;
	}

	public void setFincode(String fincode) {
		this.fincode = fincode;
	}

	public void setFincodeLeftLike(String fincodeLeftLike) {
		this.fincodeLeftLike = fincodeLeftLike;
	}

	public void setFincodeLike(String fincodeLike) {
		this.fincodeLike = fincodeLike;
	}

	public void setFincodeRightLike(String fincodeRightLike) {
		this.fincodeRightLike = fincodeRightLike;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public void setLevelGreaterThanOrEqual(Integer levelGreaterThanOrEqual) {
		this.levelGreaterThanOrEqual = levelGreaterThanOrEqual;
	}

	public void setLevelLessThanOrEqual(Integer levelLessThanOrEqual) {
		this.levelLessThanOrEqual = levelLessThanOrEqual;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNameLike(String nameLike) {
		this.nameLike = nameLike;
	}

	public void setNamePinyinLike(String namePinyinLike) {
		this.namePinyinLike = namePinyinLike;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public void setNoLeftLike(String noLeftLike) {
		this.noLeftLike = noLeftLike;
	}

	public void setNoLike(String noLike) {
		this.noLike = noLike;
	}

	public void setNoRightLike(String noRightLike) {
		this.noRightLike = noRightLike;
	}

	public void setOrganizationIds(List<Long> organizationIds) {
		this.organizationIds = organizationIds;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public void setSortGreaterThan(Integer sortGreaterThan) {
		this.sortGreaterThan = sortGreaterThan;
	}

	public void setSortGreaterThanOrEqual(Integer sortGreaterThanOrEqual) {
		this.sortGreaterThanOrEqual = sortGreaterThanOrEqual;
	}

	public void setSortLessThan(Integer sortLessThan) {
		this.sortLessThan = sortLessThan;
	}

	public void setSortLessThanOrEqual(Integer sortLessThanOrEqual) {
		this.sortLessThanOrEqual = sortLessThanOrEqual;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setStatusGreaterThanOrEqual(Integer statusGreaterThanOrEqual) {
		this.statusGreaterThanOrEqual = statusGreaterThanOrEqual;
	}

	public void setStatusLessThanOrEqual(Integer statusLessThanOrEqual) {
		this.statusLessThanOrEqual = statusLessThanOrEqual;
	}

	public void setTreeIdLeftLike(String treeIdLeftLike) {
		this.treeIdLeftLike = treeIdLeftLike;
	}

	public void setTreeIdRightLike(String treeIdRightLike) {
		this.treeIdRightLike = treeIdRightLike;
	}

	public void setType(String type) {
		this.type = type;
	}

	public SysOrganizationQuery sort(Integer sort) {
		if (sort == null) {
			throw new RuntimeException("sort is null");
		}
		this.sort = sort;
		return this;
	}

	public SysOrganizationQuery sortGreaterThanOrEqual(Integer sortGreaterThanOrEqual) {
		if (sortGreaterThanOrEqual == null) {
			throw new RuntimeException("sort is null");
		}
		this.sortGreaterThanOrEqual = sortGreaterThanOrEqual;
		return this;
	}

	public SysOrganizationQuery sortLessThanOrEqual(Integer sortLessThanOrEqual) {
		if (sortLessThanOrEqual == null) {
			throw new RuntimeException("sort is null");
		}
		this.sortLessThanOrEqual = sortLessThanOrEqual;
		return this;
	}

}