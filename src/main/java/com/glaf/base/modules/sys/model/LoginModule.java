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
import com.glaf.core.domain.ServerEntity;
import com.glaf.core.util.DateUtils;
import com.glaf.base.modules.sys.util.*;

/**
 * 登录模块-配置
 *
 */
@Entity
@Table(name = "LOGIN_MODULE")
public class LoginModule implements Serializable, JSONable {
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
	 * 标题
	 */
	@Column(name = "TITLE_", length = 200)
	protected String title;

	/**
	 * 内容
	 */
	@Column(name = "CONTENT_", length = 2000)
	protected String content;

	/**
	 * 应用编号
	 */
	@Column(name = "APPID_", length = 200)
	protected String appId;

	/**
	 * 应用密锁
	 */
	@Column(name = "APPSECRET_", length = 200)
	protected String appSecret;

	/**
	 * 公钥
	 */
	@Lob
	@Column(name = "PUBLICKEY_", length = 4000)
	protected String publicKey;

	/**
	 * 私钥
	 */
	@Lob
	@Column(name = "PRIVATEKEY_", length = 4000)
	protected String privateKey;

	/**
	 * 对方公钥
	 */
	@Lob
	@Column(name = "PEERPUBLICKEY_", length = 4000)
	protected String peerPublicKey;

	/**
	 * 加密令牌
	 */
	@Column(name = "TOKEN_", length = 200)
	protected String token;

	/**
	 * 登录链接地址
	 */
	@Column(name = "LOGINURL_", length = 500)
	protected String loginUrl;

	/**
	 * 指定登录账户
	 */
	@Column(name = "LOGINUSERID_", length = 50)
	protected String loginUserId;

	/**
	 * 登录后链接地址
	 */
	@Column(name = "URL_", length = 500)
	protected String url;

	/**
	 * 服务编号，关联登录服务器端配置如MQ信息或客户端配置
	 */
	@Column(name = "SERVERID_")
	protected Long serverId;

	/**
	 * 系统代码
	 */
	@Column(name = "SYSTEMCODE_", length = 50)
	protected String systemCode;

	/**
	 * 类型
	 */
	@Column(name = "TYPE_", length = 50)
	protected String type;

	/**
	 * 有效时长
	 */
	@Column(name = "TIMELIVE_")
	protected int timeLive;

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

	@javax.persistence.Transient
	protected ServerEntity serverEntity;

	public LoginModule() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginModule other = (LoginModule) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public String getContent() {
		return this.content;
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

	public String getId() {
		return this.id;
	}

	public int getLocked() {
		return locked;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public String getLoginUserId() {
		return loginUserId;
	}

	public String getPeerPublicKey() {
		return peerPublicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public ServerEntity getServerEntity() {
		return serverEntity;
	}

	public Long getServerId() {
		return serverId;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public String getTenantId() {
		return tenantId;
	}

	public int getTimeLive() {
		return this.timeLive;
	}

	public String getTitle() {
		return this.title;
	}

	public String getToken() {
		return this.token;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return this.url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public LoginModule jsonToObject(JSONObject jsonObject) {
		return LoginModuleJsonFactory.jsonToObject(jsonObject);
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public void setLoginUserId(String loginUserId) {
		this.loginUserId = loginUserId;
	}

	public void setPeerPublicKey(String peerPublicKey) {
		this.peerPublicKey = peerPublicKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public void setServerEntity(ServerEntity serverEntity) {
		this.serverEntity = serverEntity;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setTimeLive(int timeLive) {
		this.timeLive = timeLive;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JSONObject toJsonObject() {
		return LoginModuleJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return LoginModuleJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
