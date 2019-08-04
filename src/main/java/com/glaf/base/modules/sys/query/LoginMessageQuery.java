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

public class LoginMessageQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected List<String> userIds;
	protected Collection<String> appActorIds;
	protected int day;
	protected String userId;
	protected String clientIP;
	protected String clientIPLike;
	protected Long loginTimeGreaterThanOrEqual;
	protected Long loginTimeLessThanOrEqual;
	protected Date createTimeGreaterThanOrEqual;
	protected Date createTimeLessThanOrEqual;

	public LoginMessageQuery() {

	}

	public LoginMessageQuery clientIP(String clientIP) {
		if (clientIP == null) {
			throw new RuntimeException("clientIP is null");
		}
		this.clientIP = clientIP;
		return this;
	}

	public LoginMessageQuery clientIPLike(String clientIPLike) {
		if (clientIPLike == null) {
			throw new RuntimeException("clientIP is null");
		}
		this.clientIPLike = clientIPLike;
		return this;
	}

	public LoginMessageQuery createTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		if (createTimeGreaterThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
		return this;
	}

	public LoginMessageQuery createTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		if (createTimeLessThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
		return this;
	}

	public Collection<String> getAppActorIds() {
		return appActorIds;
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

	public int getDay() {
		return day;
	}

	public Long getLoginTimeGreaterThanOrEqual() {
		return loginTimeGreaterThanOrEqual;
	}

	public Long getLoginTimeLessThanOrEqual() {
		return loginTimeLessThanOrEqual;
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

			if ("section".equals(sortColumn)) {
				orderBy = "E.SECTION_" + a_x;
			}

			if ("content".equals(sortColumn)) {
				orderBy = "E.CONTENT_" + a_x;
			}

			if ("timeLive".equals(sortColumn)) {
				orderBy = "E.TIMELIVE_" + a_x;
			}

			if ("loginTime".equals(sortColumn)) {
				orderBy = "E.LOGINTIME_" + a_x;
			}

			if ("createTime".equals(sortColumn)) {
				orderBy = "E.CREATETIME_" + a_x;
			}

		}
		return orderBy;
	}

	public String getUserId() {
		return userId;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	@Override
	public void initQueryColumns() {
		super.initQueryColumns();
		addColumn("userId", "USERID_");
		addColumn("clientIP", "CLIENTIP_");
		addColumn("token", "TOKEN_");
		addColumn("section", "SECTION_");
		addColumn("content", "CONTENT_");
		addColumn("timeLive", "TIMELIVE_");
		addColumn("loginTime", "LOGINTIME_");
		addColumn("createTime", "CREATETIME_");
	}

	public LoginMessageQuery loginTimeGreaterThanOrEqual(Long loginTimeGreaterThanOrEqual) {
		if (loginTimeGreaterThanOrEqual == null) {
			throw new RuntimeException("loginTime is null");
		}
		this.loginTimeGreaterThanOrEqual = loginTimeGreaterThanOrEqual;
		return this;
	}

	public LoginMessageQuery loginTimeLessThanOrEqual(Long loginTimeLessThanOrEqual) {
		if (loginTimeLessThanOrEqual == null) {
			throw new RuntimeException("loginTime is null");
		}
		this.loginTimeLessThanOrEqual = loginTimeLessThanOrEqual;
		return this;
	}

	public void setAppActorIds(Collection<String> appActorIds) {
		this.appActorIds = appActorIds;
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

	public void setDay(int day) {
		this.day = day;
	}

	public void setLoginTimeGreaterThanOrEqual(Long loginTimeGreaterThanOrEqual) {
		this.loginTimeGreaterThanOrEqual = loginTimeGreaterThanOrEqual;
	}

	public void setLoginTimeLessThanOrEqual(Long loginTimeLessThanOrEqual) {
		this.loginTimeLessThanOrEqual = loginTimeLessThanOrEqual;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

}