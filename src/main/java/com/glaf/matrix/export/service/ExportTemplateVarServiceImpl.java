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
import com.glaf.core.util.UUID32;
import com.glaf.matrix.export.domain.ExportTemplateVar;
import com.glaf.matrix.export.mapper.ExportTemplateVarMapper;
import com.glaf.matrix.export.query.ExportTemplateVarQuery;

@Service("com.glaf.matrix.export.service.exportTemplateVarService")
@Transactional(readOnly = true)
public class ExportTemplateVarServiceImpl implements ExportTemplateVarService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ExportTemplateVarMapper exportTemplateVarMapper;

	public ExportTemplateVarServiceImpl() {

	}

	public int count(ExportTemplateVarQuery query) {
		return exportTemplateVarMapper.getExportTemplateVarCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			exportTemplateVarMapper.deleteExportTemplateVarById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				exportTemplateVarMapper.deleteExportTemplateVarById(id);
			}
		}
	}

	public ExportTemplateVar getExportTemplateVar(String id) {
		if (id == null) {
			return null;
		}
		ExportTemplateVar exportTemplateVar = exportTemplateVarMapper.getExportTemplateVarById(id);
		return exportTemplateVar;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getExportTemplateVarCountByQueryCriteria(ExportTemplateVarQuery query) {
		return exportTemplateVarMapper.getExportTemplateVarCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<ExportTemplateVar> getExportTemplateVarsByQueryCriteria(int start, int pageSize, ExportTemplateVarQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<ExportTemplateVar> rows = sqlSessionTemplate.selectList("getExportTemplateVars", query, rowBounds);
		return rows;
	}

	public List<ExportTemplateVar> list(ExportTemplateVarQuery query) {
		List<ExportTemplateVar> list = exportTemplateVarMapper.getExportTemplateVars(query);
		return list;
	}

	@Transactional
	public void save(ExportTemplateVar exportTemplateVar) {
		if (exportTemplateVar.getId() == null) {
			exportTemplateVar.setId(UUID32.getUUID());
			exportTemplateVar.setCreateTime(new Date());

			exportTemplateVarMapper.insertExportTemplateVar(exportTemplateVar);
		} else {
			exportTemplateVarMapper.updateExportTemplateVar(exportTemplateVar);
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

	@javax.annotation.Resource(name = "com.glaf.matrix.export.mapper.ExportTemplateVarMapper")
	public void setExportTemplateVarMapper(ExportTemplateVarMapper exportTemplateVarMapper) {
		this.exportTemplateVarMapper = exportTemplateVarMapper;
	}

}
