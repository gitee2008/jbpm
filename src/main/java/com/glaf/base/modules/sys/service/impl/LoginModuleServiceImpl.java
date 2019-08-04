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

package com.glaf.base.modules.sys.service.impl;

import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.core.id.*;
import com.glaf.core.service.IServerEntityService;
import com.glaf.core.util.RSA;
import com.glaf.core.util.UUID32;
import com.glaf.core.dao.*;
import com.glaf.core.domain.ServerEntity;
import com.glaf.base.modules.sys.mapper.*;
import com.glaf.base.modules.sys.model.LoginModule;
import com.glaf.base.modules.sys.query.*;
import com.glaf.base.modules.sys.service.LoginModuleService;

@Service("loginModuleService")
@Transactional(readOnly = true)
public class LoginModuleServiceImpl implements LoginModuleService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected LoginModuleMapper loginModuleMapper;

	protected IServerEntityService serverEntityService;

	public LoginModuleServiceImpl() {

	}

	public int count(LoginModuleQuery query) {
		return loginModuleMapper.getLoginModuleCount(query);
	}

	/**
	 * 用自己的私钥解密数据
	 * 
	 * @param id
	 * @param data
	 * @return
	 */
	public byte[] decryptText(String id, byte[] data) {
		String privateKey = loginModuleMapper.getPrivateKey(id);// 用自己的私钥解密数据
		if (StringUtils.isNotEmpty(privateKey)) {
			byte[] bytes = RSA.decryptByPrivateKey(data, privateKey);
			return bytes;
		}
		return null;
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			loginModuleMapper.deleteLoginModuleById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				loginModuleMapper.deleteLoginModuleById(id);
			}
		}
	}

	/**
	 * 用对方提供的公钥加密数据
	 * 
	 * @param id
	 * @param data
	 * @return
	 */
	public byte[] encryptText(String id, byte[] data) {
		LoginModule loginModule = loginModuleMapper.getLoginModuleById(id);
		String publicKey = loginModule.getPeerPublicKey();// 用对方提供的公钥加密数据
		if (StringUtils.isNotEmpty(publicKey)) {
			byte[] bytes = RSA.encryptByPublicKey(data, publicKey);
			return bytes;
		}
		return null;
	}

	public LoginModule getLoginModule(String id) {
		if (id == null) {
			return null;
		}
		LoginModule loginModule = loginModuleMapper.getLoginModuleById(id);
		if (loginModule != null) {
			if (loginModule.getServerId() != null && loginModule.getServerId() > 0) {
				ServerEntity serverEntity = serverEntityService.getServerEntityById(loginModule.getServerId());
				loginModule.setServerEntity(serverEntity);
			}
		}
		return loginModule;
	}

	public LoginModule getLoginModuleByAppId(String appId) {
		LoginModule loginModule = loginModuleMapper.getLoginModuleByAppId(appId);
		if (loginModule != null) {
			if (loginModule.getServerId() != null && loginModule.getServerId() > 0) {
				ServerEntity serverEntity = serverEntityService.getServerEntityById(loginModule.getServerId());
				loginModule.setServerEntity(serverEntity);
			}
		}
		return loginModule;
	}

	/**
	 * 根据sysCode获取一条记录
	 * 
	 * @return
	 */
	public LoginModule getLoginModuleBySysCode(String sysCode) {
		if (sysCode == null) {
			return null;
		}
		LoginModule loginModule = loginModuleMapper.getLoginModuleBySysCode(sysCode);
		if (loginModule != null) {
			if (loginModule.getServerId() != null && loginModule.getServerId() > 0) {
				ServerEntity serverEntity = serverEntityService.getServerEntityById(loginModule.getServerId());
				loginModule.setServerEntity(serverEntity);
			}
		}
		return loginModule;
	}

	/**
	 * 根据token获取一条记录
	 * 
	 * @return
	 */
	public LoginModule getLoginModuleByToken(String token) {
		if (token == null) {
			return null;
		}
		LoginModule loginModule = loginModuleMapper.getLoginModuleByToken(token);
		if (loginModule != null) {
			if (loginModule.getServerId() != null && loginModule.getServerId() > 0) {
				ServerEntity serverEntity = serverEntityService.getServerEntityById(loginModule.getServerId());
				loginModule.setServerEntity(serverEntity);
			}
		}
		return loginModule;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getLoginModuleCountByQueryCriteria(LoginModuleQuery query) {
		return loginModuleMapper.getLoginModuleCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<LoginModule> getLoginModulesByQueryCriteria(int start, int pageSize, LoginModuleQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<LoginModule> list = sqlSessionTemplate.selectList("getLoginModules", query, rowBounds);
		if (list != null && !list.isEmpty()) {
			for (LoginModule loginModule : list) {
				if (loginModule.getServerId() != null && loginModule.getServerId() > 0) {
					ServerEntity serverEntity = serverEntityService.getServerEntityById(loginModule.getServerId());
					loginModule.setServerEntity(serverEntity);
				}
			}
		}
		return list;
	}

	public List<LoginModule> list(LoginModuleQuery query) {
		List<LoginModule> list = loginModuleMapper.getLoginModules(query);
		if (list != null && !list.isEmpty()) {
			for (LoginModule loginModule : list) {
				if (loginModule.getServerId() != null && loginModule.getServerId() > 0) {
					ServerEntity serverEntity = serverEntityService.getServerEntityById(loginModule.getServerId());
					loginModule.setServerEntity(serverEntity);
				}
			}
		}
		return list;
	}

	@Transactional
	public void resetLoginAppSecret(String id) {
		LoginModule loginModule = this.getLoginModule(id);
		if (loginModule != null) {
			if (StringUtils.equals(loginModule.getType(), "server")) {
				loginModule.setAppId(UUID32.getUUID());
				loginModule.setAppSecret(UUID32.getUUID());
				loginModuleMapper.resetLoginAppSecret(loginModule);
			}
		}
	}

	@Transactional
	public void resetLoginAppToken(String id) {
		LoginModule loginModule = this.getLoginModule(id);
		if (loginModule != null) {
			if (StringUtils.equals(loginModule.getType(), "server")) {
				loginModule.setToken(UUID32.getUUID());
				loginModuleMapper.resetLoginAppToken(loginModule);
			}
		}
	}

	/**
	 * 重置公钥及私钥
	 * 
	 * @param id
	 */
	@Transactional
	public void resetLoginRSAKey(String id) {
		LoginModule loginModule = this.getLoginModule(id);
		if (StringUtils.equals(loginModule.getType(), "client_rsa")
				|| StringUtils.equals(loginModule.getType(), "server_rsa")) {
			// 生成公钥私钥
			Map<String, Object> map = RSA.init();
			String publicKey = RSA.getPublicKey(map);
			String privateKey = RSA.getPrivateKey(map);
			loginModule.setPublicKey(publicKey);
			loginModule.setPrivateKey(privateKey);
		}
		loginModuleMapper.resetLoginPublicKey(loginModule);
	}

	@Transactional
	public void save(LoginModule loginModule) {
		if (StringUtils.isEmpty(loginModule.getId())) {
			loginModule.setId(UUID32.getUUID());
			loginModule.setCreateTime(new Date());
			if (StringUtils.equals(loginModule.getType(), "server")) {
				loginModule.setAppId(UUID32.getUUID());
				loginModule.setAppSecret(UUID32.getUUID());
			}

			if (StringUtils.equals(loginModule.getType(), "client_rsa")
					|| StringUtils.equals(loginModule.getType(), "server_rsa")) {
				// 生成公钥私钥
				Map<String, Object> map = RSA.init();
				String publicKey = RSA.getPublicKey(map);
				String privateKey = RSA.getPrivateKey(map);
				loginModule.setPublicKey(publicKey);
				loginModule.setPrivateKey(privateKey);
			}

			loginModuleMapper.insertLoginModule(loginModule);
		} else {
			loginModuleMapper.updateLoginModule(loginModule);
		}
	}

	@javax.annotation.Resource
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@javax.annotation.Resource
	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	@javax.annotation.Resource
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@javax.annotation.Resource
	public void setLoginModuleMapper(LoginModuleMapper loginModuleMapper) {
		this.loginModuleMapper = loginModuleMapper;
	}

	@javax.annotation.Resource
	public void setServerEntityService(IServerEntityService serverEntityService) {
		this.serverEntityService = serverEntityService;
	}

	@javax.annotation.Resource
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

}
