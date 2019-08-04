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

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.base.modules.sys.mapper.LoginMessageMapper;
import com.glaf.base.modules.sys.model.LoginMessage;
import com.glaf.base.modules.sys.query.LoginMessageQuery;
import com.glaf.base.modules.sys.service.LoginMessageService;
import com.glaf.core.dao.EntityDAO;
import com.glaf.core.id.IdGenerator;
import com.glaf.core.util.DateUtils;

@Service("loginMessageService")
@Transactional(readOnly = true)
public class LoginMessageServiceImpl implements LoginMessageService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected LoginMessageMapper loginMessageMapper;

	public LoginMessageServiceImpl() {

	}

	public int count(LoginMessageQuery query) {
		return loginMessageMapper.getLoginMessageCount(query);
	}

	@Transactional
	public void deleteById(String token) {
		if (token != null) {
			loginMessageMapper.deleteLoginMessageByToken(token);
		}
	}

	/**
	 * 根据token值获取用户登录信息
	 * 
	 * @param token
	 * @return
	 */
	public LoginMessage getLoginMessageByToken(String token) {
		if (token == null) {
			return null;
		}
		LoginMessage loginMessage = loginMessageMapper.getLoginMessageByToken(token);
		return loginMessage;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getLoginMessageCountByQueryCriteria(LoginMessageQuery query) {
		return loginMessageMapper.getLoginMessageCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<LoginMessage> getLoginMessagesByQueryCriteria(int start, int pageSize, LoginMessageQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<LoginMessage> rows = sqlSessionTemplate.selectList("getLoginMessages", query, rowBounds);
		return rows;
	}

	/**
	 * 获取某个用户当天的登录次数
	 * 
	 * @param userId
	 * @return
	 */
	public int getTodayLoginCountByUserId(String userId) {
		LoginMessageQuery query = new LoginMessageQuery();
		query.setUserId(userId);
		query.setDay(DateUtils.getNowYearMonthDay());
		int count = loginMessageMapper.getLoginCountByUserId(query);
		return count;
	}

	public List<LoginMessage> list(LoginMessageQuery query) {
		List<LoginMessage> list = loginMessageMapper.getLoginMessages(query);
		return list;
	}

	@Transactional
	public void save(LoginMessage loginMessage) {
		LoginMessageQuery query = new LoginMessageQuery();
		query.setUserId(loginMessage.getUserId());
		query.setDay(DateUtils.getNowYearMonthDay());
		int count = loginMessageMapper.getLoginCountByUserId(query);
		if (count < 2000) {
			loginMessageMapper.deleteLoginMessageByToken(loginMessage.getToken());
			loginMessage.setCreateTime(new Date());
			loginMessage.setDay(DateUtils.getNowYearMonthDay());
			loginMessage.setLoginTime(System.currentTimeMillis());
			loginMessageMapper.insertLoginMessage(loginMessage);
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
	public void setLoginMessageMapper(LoginMessageMapper loginMessageMapper) {
		this.loginMessageMapper = loginMessageMapper;
	}

	@javax.annotation.Resource
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

}
