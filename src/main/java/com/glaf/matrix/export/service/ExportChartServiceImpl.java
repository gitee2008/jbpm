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
import com.glaf.core.util.UUID32;
import com.glaf.matrix.export.domain.ExportChart;
import com.glaf.matrix.export.mapper.ExportChartMapper;
import com.glaf.matrix.export.query.ExportChartQuery;

@Service("com.glaf.matrix.export.service.exportChartService")
@Transactional(readOnly = true)
public class ExportChartServiceImpl implements ExportChartService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ExportChartMapper exportChartMapper;

	public ExportChartServiceImpl() {

	}

	public int count(ExportChartQuery query) {
		return exportChartMapper.getExportChartCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			exportChartMapper.deleteExportChartById(id);
		}
	}

	@Transactional
	public void deleteExportChartsByExpId(String expId) {
		if (expId != null) {
			exportChartMapper.deleteExportChartsByExpId(expId);
		}
	}

	public ExportChart getExportChart(String id) {
		if (id == null) {
			return null;
		}
		ExportChart exportChart = exportChartMapper.getExportChartById(id);
		return exportChart;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getExportChartCountByQueryCriteria(ExportChartQuery query) {
		return exportChartMapper.getExportChartCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<ExportChart> getExportChartsByQueryCriteria(int start, int pageSize, ExportChartQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<ExportChart> rows = sqlSessionTemplate.selectList("getExportCharts", query, rowBounds);
		return rows;
	}

	public List<ExportChart> list(ExportChartQuery query) {
		List<ExportChart> list = exportChartMapper.getExportCharts(query);
		return list;
	}

	@Transactional
	public void save(ExportChart exportChart) {
		if (StringUtils.isEmpty(exportChart.getId())) {
			exportChart.setId(UUID32.generateShortUuid());
			exportChart.setCreateTime(new Date());
			exportChartMapper.insertExportChart(exportChart);
		} else {
			exportChartMapper.updateExportChart(exportChart);
		}
	}

	@javax.annotation.Resource
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.mapper.ExportChartMapper")
	public void setExportChartMapper(ExportChartMapper exportChartMapper) {
		this.exportChartMapper = exportChartMapper;
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
