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

package com.glaf.matrix.cycle.service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.matrix.cycle.domain.*;
import com.glaf.matrix.cycle.query.*;

@Transactional(readOnly = true)
public interface LoopSqlToTableService {

	@Transactional
	void bulkInsert(List<LoopSqlToTable> list);

	/**
	 * 根据主键删除记录
	 * 
	 * @return
	 */
	@Transactional
	void deleteById(String id);

	/**
	 * 根据主键删除多条记录
	 * 
	 * @return
	 */
	@Transactional
	void deleteByIds(List<String> ids);

	/**
	 * 根据主键获取一条记录
	 * 
	 * @return
	 */
	LoopSqlToTable getLoopSqlToTable(String id);

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	int getLoopSqlToTableCountByQueryCriteria(LoopSqlToTableQuery query);

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	List<LoopSqlToTable> getLoopSqlToTablesByQueryCriteria(int start, int pageSize, LoopSqlToTableQuery query);

	/**
	 * 根据查询参数获取记录列表
	 * 
	 * @return
	 */
	List<LoopSqlToTable> list(LoopSqlToTableQuery query);
	
	/**
	 * 新增一条记录
	 * 
	 * @return
	 */
	@Transactional
	void insert(LoopSqlToTable loopSqlToTable);

	/**
	 * 保存一条记录
	 * 
	 * @return
	 */
	@Transactional
	void save(LoopSqlToTable loopSqlToTable);
	
	@Transactional
	void updateLoopSqlToTableSyncStatus(LoopSqlToTable model);

}
