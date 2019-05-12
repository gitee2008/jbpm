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
package com.glaf.chart.service.impl;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.core.id.*;
import com.glaf.core.util.UUID32;
import com.glaf.core.dao.*;

import com.glaf.chart.mapper.*;
import com.glaf.chart.domain.*;
import com.glaf.chart.query.*;
import com.glaf.chart.service.ChartCombinationService;

@Service("chartCombinationService")
@Transactional(readOnly = true)
public class ChartCombinationServiceImpl implements ChartCombinationService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ChartCombinationMapper chartCombinationMapper;

	public ChartCombinationServiceImpl() {

	}

	public int count(ChartCombinationQuery query) {
		return chartCombinationMapper.getChartCombinationCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			chartCombinationMapper.deleteChartCombinationById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				chartCombinationMapper.deleteChartCombinationById(id);
			}
		}
	}

	public ChartCombination getChartCombination(String id) {
		if (id == null) {
			return null;
		}
		ChartCombination chartCombination = chartCombinationMapper.getChartCombinationById(id);
		return chartCombination;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getChartCombinationCountByQueryCriteria(ChartCombinationQuery query) {
		return chartCombinationMapper.getChartCombinationCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<ChartCombination> getChartCombinationsByQueryCriteria(int start, int pageSize,
			ChartCombinationQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<ChartCombination> rows = sqlSessionTemplate.selectList("getChartCombinations", query, rowBounds);
		return rows;
	}

	public List<ChartCombination> list(ChartCombinationQuery query) {
		List<ChartCombination> list = chartCombinationMapper.getChartCombinations(query);
		return list;
	}

	@Transactional
	public void save(ChartCombination chartCombination) {
		if (chartCombination.getId() == null) {
			chartCombination.setId(UUID32.generateShortUuid());
			chartCombination.setCreateTime(new Date());
			chartCombinationMapper.insertChartCombination(chartCombination);
		} else {
			chartCombinationMapper.updateChartCombination(chartCombination);
		}
	}

	@javax.annotation.Resource
	public void setChartCombinationMapper(ChartCombinationMapper chartCombinationMapper) {
		this.chartCombinationMapper = chartCombinationMapper;
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
