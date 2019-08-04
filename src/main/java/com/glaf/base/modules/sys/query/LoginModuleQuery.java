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

public class LoginModuleQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected Collection<String> appActorIds;
	protected String appId;
	protected String appSecret;
	protected String systemCode;
	protected String titleLike;
	protected String contentLike;
	protected String urlLike;
	protected String type;
	protected List<String> createBys;
	protected Date createTimeGreaterThanOrEqual;
	protected Date createTimeLessThanOrEqual;

	public LoginModuleQuery() {

	}

	public LoginModuleQuery appId(String appId) {
		if (appId == null) {
			throw new RuntimeException("appId is null");
		}
		this.appId = appId;
		return this;
	}

	public LoginModuleQuery appSecret(String appSecret) {
		if (appSecret == null) {
			throw new RuntimeException("appSecret is null");
		}
		this.appSecret = appSecret;
		return this;
	}

	public LoginModuleQuery contentLike(String contentLike) {
		if (contentLike == null) {
			throw new RuntimeException("content is null");
		}
		this.contentLike = contentLike;
		return this;
	}

	public LoginModuleQuery createBy(String createBy) {
		if (createBy == null) {
			throw new RuntimeException("createBy is null");
		}
		this.createBy = createBy;
		return this;
	}

	public LoginModuleQuery createBys(List<String> createBys) {
		if (createBys == null) {
			throw new RuntimeException("createBys is empty ");
		}
		this.createBys = createBys;
		return this;
	}

	public LoginModuleQuery createTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		if (createTimeGreaterThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
		return this;
	}

	public LoginModuleQuery createTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		if (createTimeLessThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
		return this;
	}

	public Collection<String> getAppActorIds() {
		return appActorIds;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public String getContentLike() {
		if (contentLike != null && contentLike.trim().length() > 0) {
			if (!contentLike.startsWith("%")) {
				contentLike = "%" + contentLike;
			}
			if (!contentLike.endsWith("%")) {
				contentLike = contentLike + "%";
			}
		}
		return contentLike;
	}

	public List<String> getCreateBys() {
		return createBys;
	}

	public Date getCreateTimeGreaterThanOrEqual() {
		return createTimeGreaterThanOrEqual;
	}

	public Date getCreateTimeLessThanOrEqual() {
		return createTimeLessThanOrEqual;
	}

	public String getOrderBy() {
		if (sortColumn != null) {
			String a_x = " asc ";
			if (sortOrder != null) {
				a_x = sortOrder;
			}

			if ("title".equals(sortColumn)) {
				orderBy = "E.TITLE_" + a_x;
			}

			if ("content".equals(sortColumn)) {
				orderBy = "E.CONTENT_" + a_x;
			}

			if ("token".equals(sortColumn)) {
				orderBy = "E.TOKEN_" + a_x;
			}

			if ("url".equals(sortColumn)) {
				orderBy = "E.URL_" + a_x;
			}

			if ("serverId".equals(sortColumn)) {
				orderBy = "E.SERVERID_" + a_x;
			}

			if ("timeLive".equals(sortColumn)) {
				orderBy = "E.TIMELIVE_" + a_x;
			}

			if ("loginTime".equals(sortColumn)) {
				orderBy = "E.LOGINTIME_" + a_x;
			}

			if ("createBy".equals(sortColumn)) {
				orderBy = "E.CREATEBY_" + a_x;
			}

			if ("createTime".equals(sortColumn)) {
				orderBy = "E.CREATETIME_" + a_x;
			}

		}
		return orderBy;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public String getTitleLike() {
		if (titleLike != null && titleLike.trim().length() > 0) {
			if (!titleLike.startsWith("%")) {
				titleLike = "%" + titleLike;
			}
			if (!titleLike.endsWith("%")) {
				titleLike = titleLike + "%";
			}
		}
		return titleLike;
	}

	public String getType() {
		return type;
	}

	public String getUrlLike() {
		if (urlLike != null && urlLike.trim().length() > 0) {
			if (!urlLike.startsWith("%")) {
				urlLike = "%" + urlLike;
			}
			if (!urlLike.endsWith("%")) {
				urlLike = urlLike + "%";
			}
		}
		return urlLike;
	}

	@Override
	public void initQueryColumns() {
		super.initQueryColumns();
		addColumn("id", "ID_");
		addColumn("title", "TITLE_");
		addColumn("content", "CONTENT_");
		addColumn("token", "TOKEN_");
		addColumn("url", "URL_");
		addColumn("serverId", "SERVERID_");
		addColumn("timeLive", "TIMELIVE_");
		addColumn("loginTime", "LOGINTIME_");
		addColumn("createBy", "CREATEBY_");
		addColumn("createTime", "CREATETIME_");
	}

	public void setAppActorIds(Collection<String> appActorIds) {
		this.appActorIds = appActorIds;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public void setContentLike(String contentLike) {
		this.contentLike = contentLike;
	}

	public void setCreateBys(List<String> createBys) {
		this.createBys = createBys;
	}

	public void setCreateTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
	}

	public void setCreateTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public void setTitleLike(String titleLike) {
		this.titleLike = titleLike;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrlLike(String urlLike) {
		this.urlLike = urlLike;
	}

	public LoginModuleQuery systemCode(String systemCode) {
		if (systemCode == null) {
			throw new RuntimeException("systemCode is null");
		}
		this.systemCode = systemCode;
		return this;
	}

	public LoginModuleQuery titleLike(String titleLike) {
		if (titleLike == null) {
			throw new RuntimeException("title is null");
		}
		this.titleLike = titleLike;
		return this;
	}

	public LoginModuleQuery type(String type) {
		if (type == null) {
			throw new RuntimeException("type is null");
		}
		this.type = type;
		return this;
	}

	public LoginModuleQuery urlLike(String urlLike) {
		if (urlLike == null) {
			throw new RuntimeException("url is null");
		}
		this.urlLike = urlLike;
		return this;
	}

}