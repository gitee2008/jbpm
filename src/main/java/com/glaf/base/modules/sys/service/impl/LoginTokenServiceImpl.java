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
import com.glaf.core.dao.*;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.util.*;

import com.glaf.base.modules.sys.mapper.*;
import com.glaf.base.modules.sys.model.*;
import com.glaf.base.modules.sys.query.*;
import com.glaf.base.modules.sys.service.*;

@Service("loginTokenService")
@Transactional(readOnly = true)
public class LoginTokenServiceImpl implements LoginTokenService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected LoginTokenMapper loginTokenMapper;

	public LoginTokenServiceImpl() {

	}

	@Transactional
	public void bulkInsert(List<LoginToken> list) {
		for (LoginToken loginToken : list) {
			if (StringUtils.isEmpty(loginToken.getId())) {
				loginToken.setId(idGenerator.getNextId("SYS_LOGIN_TOKEN"));
			}
		}
		if (StringUtils.equals(DBUtils.ORACLE, DBConnectionFactory.getDatabaseType())) {
			loginTokenMapper.bulkInsertLoginToken_oracle(list);
		} else {
			loginTokenMapper.bulkInsertLoginToken(list);
		}
	}

	public int count(LoginTokenQuery query) {
		return loginTokenMapper.getLoginTokenCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			loginTokenMapper.deleteLoginTokenById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				loginTokenMapper.deleteLoginTokenById(id);
			}
		}
	}

	@Transactional
	public void deleteLoginTokenByUserId(String userId) {
		loginTokenMapper.deleteLoginTokenByUserId(userId);
	}

	public LoginToken getLoginToken(String id) {
		if (id == null) {
			return null;
		}
		LoginToken loginToken = loginTokenMapper.getLoginTokenById(id);
		return loginToken;
	}

	public LoginToken getLoginTokenBySignature(String signature) {
		LoginToken loginToken = loginTokenMapper.getLoginTokenBySignature(signature);
		return loginToken;
	}

	public LoginToken getLoginTokenByToken(String token) {
		LoginToken loginToken = loginTokenMapper.getLoginTokenByToken(token);
		return loginToken;
	}

	public List<LoginToken> getLoginTokenByUserId(String userId) {
		LoginTokenQuery query = new LoginTokenQuery();
		query.userId(userId);
		List<LoginToken> list = loginTokenMapper.getLoginTokens(query);
		return list;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getLoginTokenCountByQueryCriteria(LoginTokenQuery query) {
		return loginTokenMapper.getLoginTokenCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<LoginToken> getLoginTokensByQueryCriteria(int start, int pageSize, LoginTokenQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<LoginToken> rows = sqlSessionTemplate.selectList("getLoginTokens", query, rowBounds);
		return rows;
	}

	public List<LoginToken> list(LoginTokenQuery query) {
		List<LoginToken> list = loginTokenMapper.getLoginTokens(query);
		return list;
	}

	@Transactional
	public void save(LoginToken loginToken) {
		loginToken.setId(DateUtils.getNowYearMonthDay() + "/" + idGenerator.getNextId("SYS_LOGIN_TOKEN"));
		loginToken.setCreateTime(new Date());
		loginTokenMapper.insertLoginToken(loginToken);
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
	public void setLoginTokenMapper(LoginTokenMapper loginTokenMapper) {
		this.loginTokenMapper = loginTokenMapper;
	}

	@javax.annotation.Resource
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Transactional
	public void update(LoginToken loginToken) {
		loginTokenMapper.updateLoginToken(loginToken);
	}

}
