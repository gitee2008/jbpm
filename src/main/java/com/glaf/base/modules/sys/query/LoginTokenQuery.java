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

public class LoginTokenQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected String clientIP;
	protected String clientIPLike;
	protected String loginModuleId;
	protected String sysCode;
	protected String token;
	protected String userId;
	protected Date createTimeGreaterThanOrEqual;
	protected Date createTimeLessThanOrEqual;

	public LoginTokenQuery() {

	}

	public LoginTokenQuery clientIP(String clientIP) {
		if (clientIP == null) {
			throw new RuntimeException("clientIP is null");
		}
		this.clientIP = clientIP;
		return this;
	}

	public LoginTokenQuery clientIPLike(String clientIPLike) {
		if (clientIPLike == null) {
			throw new RuntimeException("clientIP is null");
		}
		this.clientIPLike = clientIPLike;
		return this;
	}

	public LoginTokenQuery createTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		if (createTimeGreaterThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
		return this;
	}

	public LoginTokenQuery createTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		if (createTimeLessThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
		return this;
	}

	public String getClientIP() {
		return clientIP;
	}

	public String getClientIPLike() {
		if (clientIPLike != null && clientIPLike.trim().length() > 0) {
			if (!clientIPLike.startsWith("%")) {
				clientIPLike = "%" + clientIPLike;
			}
			if (!clientIPLike.endsWith("%")) {
				clientIPLike = clientIPLike + "%";
			}
		}
		return clientIPLike;
	}

	public Date getCreateTimeGreaterThanOrEqual() {
		return createTimeGreaterThanOrEqual;
	}

	public Date getCreateTimeLessThanOrEqual() {
		return createTimeLessThanOrEqual;
	}

	public String getLoginModuleId() {
		return loginModuleId;
	}

	public String getOrderBy() {
		if (sortColumn != null) {
			String a_x = " asc ";
			if (sortOrder != null) {
				a_x = sortOrder;
			}

			if ("clientIP".equals(sortColumn)) {
				orderBy = "E.CLIENTIP_" + a_x;
			}

			if ("token".equals(sortColumn)) {
				orderBy = "E.TOKEN_" + a_x;
			}

			if ("loginModuleId".equals(sortColumn)) {
				orderBy = "E.LOGINMODULEID_" + a_x;
			}

			if ("timeLive".equals(sortColumn)) {
				orderBy = "E.TIMELIVE_" + a_x;
			}

			if ("createTime".equals(sortColumn)) {
				orderBy = "E.CREATETIME_" + a_x;
			}

		}
		return orderBy;
	}

	public String getSysCode() {
		return sysCode;
	}

	public String getToken() {
		return token;
	}

	public String getUserId() {
		return userId;
	}

	@Override
	public void initQueryColumns() {
		super.initQueryColumns();
		addColumn("id", "ID_");
		addColumn("clientIP", "CLIENTIP_");
		addColumn("token", "TOKEN_");
		addColumn("loginModuleId", "LOGINMODULEID_");
		addColumn("timeLive", "TIMELIVE_");
		addColumn("createTime", "CREATETIME_");
	}

	public LoginTokenQuery loginModuleId(String loginModuleId) {
		if (loginModuleId == null) {
			throw new RuntimeException("loginModuleId is null");
		}
		this.loginModuleId = loginModuleId;
		return this;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public void setClientIPLike(String clientIPLike) {
		this.clientIPLike = clientIPLike;
	}

	public void setCreateTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
	}

	public void setCreateTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
	}

	public void setLoginModuleId(String loginModuleId) {
		this.loginModuleId = loginModuleId;
	}

	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public LoginTokenQuery sysCode(String sysCode) {
		if (sysCode == null) {
			throw new RuntimeException("sysCode is null");
		}
		this.sysCode = sysCode;
		return this;
	}

	public LoginTokenQuery token(String token) {
		if (token == null) {
			throw new RuntimeException("token is null");
		}
		this.token = token;
		return this;
	}

	public LoginTokenQuery userId(String userId) {
		if (userId == null) {
			throw new RuntimeException("userId is null");
		}
		this.userId = userId;
		return this;
	}

}