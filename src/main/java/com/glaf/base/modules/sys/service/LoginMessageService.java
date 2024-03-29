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

package com.glaf.base.modules.sys.service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.base.modules.sys.model.LoginMessage;
import com.glaf.base.modules.sys.query.*;

@Transactional(readOnly = true)
public interface LoginMessageService {

	/**
	 * 根据主键删除记录
	 * 
	 * @return
	 */
	@Transactional
	void deleteById(String token);

	/**
	 * 根据token值获取用户登录信息
	 * 
	 * @param token
	 * @return
	 */
	LoginMessage getLoginMessageByToken(String token);

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	int getLoginMessageCountByQueryCriteria(LoginMessageQuery query);

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	List<LoginMessage> getLoginMessagesByQueryCriteria(int start, int pageSize, LoginMessageQuery query);

	/**
	 * 获取某个用户当天的登录次数
	 * 
	 * @param userId
	 * @return
	 */
	int getTodayLoginCountByUserId(String userId);

	/**
	 * 根据查询参数获取记录列表
	 * 
	 * @return
	 */
	List<LoginMessage> list(LoginMessageQuery query);

	/**
	 * 保存一条记录
	 * 
	 * @return
	 */
	@Transactional
	void save(LoginMessage loginMessage);

}
