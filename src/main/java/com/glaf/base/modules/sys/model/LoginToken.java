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

package com.glaf.base.modules.sys.model;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.glaf.core.base.*;
import com.glaf.core.util.DateUtils;
import com.glaf.base.modules.sys.util.*;

/**
 * 
 * 实体对象
 *
 */

@Entity
@Table(name = "SYS_LOGIN_TOKEN")
public class LoginToken implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", length = 64, nullable = false)
	protected String id;

	/**
	 * 租户编号
	 */
	@Column(name = "TENANT_ID_", length = 50)
	protected String tenantId;

	/**
	 * 服务编号，关联登录服务器端配置如MQ信息或客户端配置
	 */
	@Column(name = "SERVERID_")
	protected Long serverId;

	/**
	 * 用户编号
	 */
	@Column(name = "USERID_", length = 200)
	protected String userId;

	/**
	 * 客户端IP地址
	 */
	@Column(name = "CLIENTIP_", length = 200)
	protected String clientIP;

	/**
	 * 签名字符串
	 */
	@Column(name = "SIGNATURE_", length = 500)
	protected String signature;

	/**
	 * 随机字符串
	 */
	@Column(name = "NONCE_", length = 50)
	protected String nonce;

	/**
	 * 令牌
	 */
	@Column(name = "TOKEN_", length = 200)
	protected String token;

	@Column(name = "SYSCODE_", length = 50)
	protected String sysCode;

	/**
	 * 时间戳
	 */
	@Column(name = "TIMEMILLIS_")
	protected long timeMillis;

	/**
	 * 登录编号
	 */
	@Column(name = "LOGINMODULEID_", length = 50)
	protected String loginModuleId;

	/**
	 * 有效时长
	 */
	@Column(name = "TIMELIVE_")
	protected int timeLive;

	/**
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATETIME_")
	protected Date createTime;

	public LoginToken() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginToken other = (LoginToken) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getClientIP() {
		return this.clientIP;
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

	public String getId() {
		return this.id;
	}

	public String getLoginModuleId() {
		return this.loginModuleId;
	}

	public String getNonce() {
		return nonce;
	}

	public Long getServerId() {
		return serverId;
	}

	public String getSignature() {
		return signature;
	}

	public String getSysCode() {
		return sysCode;
	}

	public String getTenantId() {
		return tenantId;
	}

	public int getTimeLive() {
		return this.timeLive;
	}

	public long getTimeMillis() {
		return timeMillis;
	}

	public String getToken() {
		return this.token;
	}

	public String getUserId() {
		return userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public LoginToken jsonToObject(JSONObject jsonObject) {
		return LoginTokenJsonFactory.jsonToObject(jsonObject);
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLoginModuleId(String loginModuleId) {
		this.loginModuleId = loginModuleId;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setTimeLive(int timeLive) {
		this.timeLive = timeLive;
	}

	public void setTimeMillis(long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public JSONObject toJsonObject() {
		return LoginTokenJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return LoginTokenJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
