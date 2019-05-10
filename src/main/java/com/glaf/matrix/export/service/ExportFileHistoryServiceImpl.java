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
import com.glaf.core.base.DataFile;
import com.glaf.core.dao.*;

import com.glaf.core.jdbc.DBConnectionFactory;

import com.glaf.core.util.*;

import com.glaf.matrix.export.mapper.*;
import com.glaf.matrix.data.domain.DataFileEntity;
import com.glaf.matrix.data.service.IDataFileService;
import com.glaf.matrix.export.domain.*;
import com.glaf.matrix.export.query.*;

@Service("com.glaf.matrix.export.service.exportFileHistoryService")
@Transactional(readOnly = true)
public class ExportFileHistoryServiceImpl implements ExportFileHistoryService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ExportFileHistoryMapper exportFileHistoryMapper;

	protected IDataFileService dataFileService;

	public ExportFileHistoryServiceImpl() {

	}

	@Transactional
	public void bulkInsert(List<ExportFileHistory> list) {
		for (ExportFileHistory exportFileHistory : list) {
			if (StringUtils.isEmpty(exportFileHistory.getId())) {
				exportFileHistory.setId(UUID32.generateShortUuid());
			}
		}

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

		for (ExportFileHistory model : list) {
			DataFile blob = new DataFileEntity();
			blob.setId(model.getId());
			blob.setCreateBy(model.getCreateBy());
			blob.setCreateDate(model.getCreateTime());
			blob.setFilename(model.getFilename());
			blob.setPath(model.getPath());
			blob.setData(model.getData());
			blob.setServiceKey("sys_export");
			blob.setBusinessKey(model.getExpId());
			blob.setLastModified(model.getLastModified());
			blob.setSize(model.getData().length);
			blob.setStatus(9);
			blob.setType("export");
			dataFileService.insertDataFile(null, blob);
		}
	}

	public int count(ExportFileHistoryQuery query) {
		return exportFileHistoryMapper.getExportFileHistoryCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			dataFileService.deleteById(null, id);
			exportFileHistoryMapper.deleteExportFileHistoryById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				dataFileService.deleteById(null, id);
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
			byte[] data = dataFileService.getBytesByFileId(null, id);
			exportFileHistory.setData(data);
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
		if (StringUtils.isEmpty(model.getId())) {
			model.setId(UUID32.generateShortUuid());
		}
		model.setCreateTime(new Date());
		exportFileHistoryMapper.insertExportFileHistory(model);

		DataFile blob = new DataFileEntity();
		blob.setId(model.getId());
		blob.setCreateBy(model.getCreateBy());
		blob.setCreateDate(model.getCreateTime());
		blob.setFilename(model.getFilename());
		blob.setPath(model.getPath());
		blob.setData(model.getData());
		blob.setServiceKey("sys_export");
		blob.setBusinessKey(model.getExpId());
		blob.setLastModified(model.getLastModified());
		blob.setSize(model.getData().length);
		blob.setStatus(9);
		blob.setType("export");
		dataFileService.insertDataFile(null, blob);
	}

	@javax.annotation.Resource
	public void setDataFileService(IDataFileService dataFileService) {
		this.dataFileService = dataFileService;
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
