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

import com.glaf.matrix.cycle.mapper.*;
import com.glaf.matrix.cycle.domain.*;
import com.glaf.matrix.cycle.query.*;

@Service("com.glaf.matrix.cycle.service.loopSqlToTableService")
@Transactional(readOnly = true)
public class LoopSqlToTableServiceImpl implements LoopSqlToTableService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected LoopSqlToTableMapper loopSqlToTableMapper;

	public LoopSqlToTableServiceImpl() {

	}

	@Transactional
	public void bulkInsert(List<LoopSqlToTable> list) {
		for (LoopSqlToTable loopSqlToTable : list) {
			if (StringUtils.isEmpty(loopSqlToTable.getId())) {
				loopSqlToTable.setId(idGenerator.getNextId("SYS_LOOP_SQL_TO_TABLE"));
			}
		}

		int batch_size = 50;
		List<LoopSqlToTable> rows = new ArrayList<LoopSqlToTable>(batch_size);

		for (LoopSqlToTable bean : list) {
			rows.add(bean);
			if (rows.size() > 0 && rows.size() % batch_size == 0) {
				if (StringUtils.equals(DBUtils.ORACLE, DBConnectionFactory.getDatabaseType())) {
					loopSqlToTableMapper.bulkInsertLoopSqlToTable_oracle(rows);
				} else {
					loopSqlToTableMapper.bulkInsertLoopSqlToTable(rows);
				}
				rows.clear();
			}
		}

		if (rows.size() > 0) {
			if (StringUtils.equals(DBUtils.ORACLE, DBConnectionFactory.getDatabaseType())) {
				loopSqlToTableMapper.bulkInsertLoopSqlToTable_oracle(rows);
			} else {
				loopSqlToTableMapper.bulkInsertLoopSqlToTable(rows);
			}
			rows.clear();
		}
	}

	public int count(LoopSqlToTableQuery query) {
		return loopSqlToTableMapper.getLoopSqlToTableCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			loopSqlToTableMapper.deleteLoopSqlToTableById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				loopSqlToTableMapper.deleteLoopSqlToTableById(id);
			}
		}
	}

	public LoopSqlToTable getLoopSqlToTable(String id) {
		if (id == null) {
			return null;
		}
		LoopSqlToTable loopSqlToTable = loopSqlToTableMapper.getLoopSqlToTableById(id);
		return loopSqlToTable;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getLoopSqlToTableCountByQueryCriteria(LoopSqlToTableQuery query) {
		return loopSqlToTableMapper.getLoopSqlToTableCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<LoopSqlToTable> getLoopSqlToTablesByQueryCriteria(int start, int pageSize,
			LoopSqlToTableQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<LoopSqlToTable> rows = sqlSessionTemplate.selectList("getLoopSqlToTables", query, rowBounds);
		return rows;
	}

	/**
	 * 新增一条记录
	 * 
	 * @return
	 */
	@Transactional
	public void insert(LoopSqlToTable loopSqlToTable) {
		if (loopSqlToTable.getRecentlyDay() > 365) {
			loopSqlToTable.setRecentlyDay(365);
		}
		loopSqlToTable.setId(UUID32.getUUID());
		loopSqlToTable.setCreateTime(new Date());
		loopSqlToTableMapper.insertLoopSqlToTable(loopSqlToTable);
	}

	public List<LoopSqlToTable> list(LoopSqlToTableQuery query) {
		List<LoopSqlToTable> list = loopSqlToTableMapper.getLoopSqlToTables(query);
		return list;
	}

	@Transactional
	public void save(LoopSqlToTable loopSqlToTable) {
		if (loopSqlToTable.getRecentlyDay() > 365) {
			loopSqlToTable.setRecentlyDay(365);
		}
		if (StringUtils.isEmpty(loopSqlToTable.getId())) {
			loopSqlToTable.setId(UUID32.getUUID());
			loopSqlToTable.setCreateTime(new Date());
			loopSqlToTableMapper.insertLoopSqlToTable(loopSqlToTable);
		} else {
			loopSqlToTableMapper.updateLoopSqlToTable(loopSqlToTable);
		}
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.cycle.mapper.LoopSqlToTableMapper")
	public void setLoopSqlToTableMapper(LoopSqlToTableMapper loopSqlToTableMapper) {
		this.loopSqlToTableMapper = loopSqlToTableMapper;
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
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@Transactional
	public void updateLoopSqlToTableSyncStatus(LoopSqlToTable model) {
		loopSqlToTableMapper.updateLoopSqlToTableSyncStatus(model);
	}

}
