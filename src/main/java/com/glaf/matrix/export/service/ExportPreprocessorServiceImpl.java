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
import com.glaf.core.dao.*;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.util.*;

import com.glaf.matrix.export.mapper.*;
import com.glaf.matrix.export.domain.*;
import com.glaf.matrix.export.query.*;
import com.glaf.matrix.export.service.ExportPreprocessorService;

@Service("exportPreprocessorService")
@Transactional(readOnly = true)
public class ExportPreprocessorServiceImpl implements ExportPreprocessorService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ExportPreprocessorMapper exportPreprocessorMapper;

	public ExportPreprocessorServiceImpl() {

	}

	@Transactional
	public void bulkInsert(List<ExportPreprocessor> list) {
		for (ExportPreprocessor exportPreprocessor : list) {
			if (exportPreprocessor.getId() == null) {
				exportPreprocessor.setId(UUID32.getUUID());
			}
		}
		if (StringUtils.equals(DBUtils.ORACLE, DBConnectionFactory.getDatabaseType())) {
			exportPreprocessorMapper.bulkInsertExportPreprocessor_oracle(list);
		} else {
			exportPreprocessorMapper.bulkInsertExportPreprocessor(list);
		}
	}

	public int count(ExportPreprocessorQuery query) {
		return exportPreprocessorMapper.getExportPreprocessorCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			exportPreprocessorMapper.deleteExportPreprocessorById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				exportPreprocessorMapper.deleteExportPreprocessorById(id);
			}
		}
	}

	public ExportPreprocessor getExportPreprocessor(String id) {
		if (id == null) {
			return null;
		}
		ExportPreprocessor exportPreprocessor = exportPreprocessorMapper.getExportPreprocessorById(id);
		return exportPreprocessor;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getExportPreprocessorCountByQueryCriteria(ExportPreprocessorQuery query) {
		return exportPreprocessorMapper.getExportPreprocessorCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<ExportPreprocessor> getExportPreprocessorsByQueryCriteria(int start, int pageSize,
			ExportPreprocessorQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<ExportPreprocessor> rows = sqlSessionTemplate.selectList("getExportPreprocessors", query, rowBounds);
		return rows;
	}

	public List<ExportPreprocessor> list(ExportPreprocessorQuery query) {
		List<ExportPreprocessor> list = exportPreprocessorMapper.getExportPreprocessors(query);
		return list;
	}

	/**
	 * 保存记录
	 * 
	 * @return
	 */
	@Transactional
	public void saveAll(String exportId, String currentStep, String currentType, List<ExportPreprocessor> rows) {
		ExportPreprocessorQuery query = new ExportPreprocessorQuery();
		query.expId(exportId);
		query.currentStep(currentStep);
		query.currentType(currentType);
		List<ExportPreprocessor> list = this.list(query);
		if (list != null && !list.isEmpty()) {
			for (ExportPreprocessor preprocessor : list) {
				this.deleteById(preprocessor.getId());
			}
		}
		if (rows != null && !rows.isEmpty()) {
			for (ExportPreprocessor preprocessor : rows) {
				preprocessor.setId(UUID32.getUUID());
				preprocessor.setExpId(exportId);
				preprocessor.setCreateTime(new Date());
				preprocessor.setCurrentStep(currentStep);
				preprocessor.setCurrentType(currentType);
				exportPreprocessorMapper.insertExportPreprocessor(preprocessor);
			}
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
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	@javax.annotation.Resource
	public void setExportPreprocessorMapper(ExportPreprocessorMapper exportPreprocessorMapper) {
		this.exportPreprocessorMapper = exportPreprocessorMapper;
	}

}
