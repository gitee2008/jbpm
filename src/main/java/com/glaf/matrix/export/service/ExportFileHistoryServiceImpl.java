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

package com.glaf.matrix.export.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.core.dao.EntityDAO;
import com.glaf.core.id.IdGenerator;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.util.DBUtils;

import com.glaf.matrix.export.domain.ExportFileHistory;
import com.glaf.matrix.export.mapper.ExportFileHistoryMapper;
import com.glaf.matrix.export.query.ExportFileHistoryQuery;

@Service("com.glaf.matrix.export.service.exportFileHistoryService")
@Transactional(readOnly = true)
public class ExportFileHistoryServiceImpl implements ExportFileHistoryService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ExportFileHistoryMapper exportFileHistoryMapper;

	public ExportFileHistoryServiceImpl() {

	}

	public int count(ExportFileHistoryQuery query) {
		return exportFileHistoryMapper.getExportFileHistoryCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			exportFileHistoryMapper.deleteExportFileHistoryById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				exportFileHistoryMapper.deleteExportFileHistoryById(id);
			}
		}
	}

	public ExportFileHistory getExportFileHistory(String id) {
		if (id == null) {
			return null;
		}
		ExportFileHistory exportFileHistory = exportFileHistoryMapper.getExportFileHistoryById(id);
		if (exportFileHistory != null) {
		}
		return exportFileHistory;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getExportFileHistoryCountByQueryCriteria(ExportFileHistoryQuery query) {
		return exportFileHistoryMapper.getExportFileHistoryCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<ExportFileHistory> getExportFileHistorysByQueryCriteria(int start, int pageSize,
			ExportFileHistoryQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<ExportFileHistory> rows = sqlSessionTemplate.selectList("getExportFileHistorys", query, rowBounds);
		return rows;
	}

	public List<ExportFileHistory> list(ExportFileHistoryQuery query) {
		List<ExportFileHistory> list = exportFileHistoryMapper.getExportFileHistorys(query);
		return list;
	}

	@Transactional
	public void save(ExportFileHistory model) {
		model.setCreateTime(new Date());
		exportFileHistoryMapper.insertExportFileHistory(model);
	}

	@Transactional
	public void saveAll(List<ExportFileHistory> list) {
		int batch_size = 50;
		List<ExportFileHistory> rows = new ArrayList<ExportFileHistory>(batch_size);

		for (ExportFileHistory model : list) {
			rows.add(model);
			if (rows.size() > 0 && rows.size() % batch_size == 0) {
				if (StringUtils.equals(DBUtils.ORACLE, DBConnectionFactory.getDatabaseType())) {
					exportFileHistoryMapper.bulkInsertExportFileHistory_oracle(rows);
				} else {
					exportFileHistoryMapper.bulkInsertExportFileHistory(rows);
				}
				rows.clear();
			}
		}

		if (rows.size() > 0) {
			if (StringUtils.equals(DBUtils.ORACLE, DBConnectionFactory.getDatabaseType())) {
				exportFileHistoryMapper.bulkInsertExportFileHistory_oracle(rows);
			} else {
				exportFileHistoryMapper.bulkInsertExportFileHistory(rows);
			}
			rows.clear();
		}

	}

	@javax.annotation.Resource
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.mapper.ExportFileHistoryMapper")
	public void setExportFileHistoryMapper(ExportFileHistoryMapper exportFileHistoryMapper) {
		this.exportFileHistoryMapper = exportFileHistoryMapper;
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

}
