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

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.core.dao.EntityDAO;
import com.glaf.core.id.IdGenerator;
import com.glaf.matrix.export.domain.ExportHistory;
import com.glaf.matrix.export.mapper.ExportHistoryMapper;
import com.glaf.matrix.export.query.ExportHistoryQuery;

@Service("com.glaf.matrix.export.service.exportHistoryService")
@Transactional(readOnly = true)
public class ExportHistoryServiceImpl implements ExportHistoryService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ExportHistoryMapper exportHistoryMapper;

	public ExportHistoryServiceImpl() {

	}

	public int count(ExportHistoryQuery query) {
		return exportHistoryMapper.getExportHistoryCount(query);
	}

	@Transactional
	public void deleteById(Long id) {
		if (id != null) {
			exportHistoryMapper.deleteExportHistoryById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<Long> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (Long id : ids) {
				exportHistoryMapper.deleteExportHistoryById(id);
			}
		}
	}

	public ExportHistory getExportHistory(Long id) {
		if (id == null) {
			return null;
		}
		ExportHistory exportHistory = exportHistoryMapper.getExportHistoryById(id);
		return exportHistory;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getExportHistoryCountByQueryCriteria(ExportHistoryQuery query) {
		return exportHistoryMapper.getExportHistoryCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<ExportHistory> getExportHistorysByQueryCriteria(int start, int pageSize,
			ExportHistoryQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<ExportHistory> rows = sqlSessionTemplate.selectList("getExportHistorys", query, rowBounds);
		return rows;
	}

	public ExportHistory getLatestExportHistory(long expId, long databaseId) {
		ExportHistoryQuery query = new ExportHistoryQuery();
		query.expId(expId);
		query.databaseId(databaseId);
		List<ExportHistory> list = exportHistoryMapper.getExportHistorys(query);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<ExportHistory> list(ExportHistoryQuery query) {
		List<ExportHistory> list = exportHistoryMapper.getExportHistorys(query);
		return list;
	}

	@Transactional
	public void save(ExportHistory exportHistory) {
		if (exportHistory.getId() == 0) {
			exportHistory.setId(idGenerator.nextId("SYS_EXPORT_HISTORY"));
			exportHistory.setCreateTime(new Date());

			exportHistoryMapper.insertExportHistory(exportHistory);
		} else {
			exportHistoryMapper.updateExportHistory(exportHistory);
		}
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.mapper.ExportHistoryMapper")
	public void setExportHistoryMapper(ExportHistoryMapper exportHistoryMapper) {
		this.exportHistoryMapper = exportHistoryMapper;
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

}
