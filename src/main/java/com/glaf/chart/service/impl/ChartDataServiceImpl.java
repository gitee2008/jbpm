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
import com.glaf.chart.service.ChartDataService;

@Service("chartDataService")
@Transactional(readOnly = true)
public class ChartDataServiceImpl implements ChartDataService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ChartDataMapper chartDataMapper;

	public ChartDataServiceImpl() {

	}

	public int count(ChartDataQuery query) {
		return chartDataMapper.getChartDataCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			chartDataMapper.deleteChartDataById(id);
		}
	}

	@Transactional
	public void deleteChartDataByServiceKey(String serviceKey) {
		chartDataMapper.deleteChartDataByServiceKey(serviceKey);
	}

	public ChartData getChartData(String id) {
		if (id == null) {
			return null;
		}
		ChartData chartData = chartDataMapper.getChartDataById(id);
		return chartData;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getChartDataCountByQueryCriteria(ChartDataQuery query) {
		return chartDataMapper.getChartDataCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<ChartData> getChartDataListByQueryCriteria(int start, int pageSize, ChartDataQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<ChartData> rows = sqlSessionTemplate.selectList("getChartDataList", query, rowBounds);
		return rows;
	}

	public List<ChartData> getChartDataListByServiceKey(String serviceKey) {
		ChartDataQuery query = new ChartDataQuery();
		query.serviceKey(serviceKey);
		List<ChartData> list = chartDataMapper.getChartDataList(query);
		return list;
	}

	public List<ChartData> list(ChartDataQuery query) {
		List<ChartData> list = chartDataMapper.getChartDataList(query);
		return list;
	}

	@Transactional
	public void save(ChartData chartData) {
		if (chartData.getId() == null) {
			chartData.setId(UUID32.generateShortUuid());
			chartDataMapper.insertChartData(chartData);
		}
	}

	@javax.annotation.Resource
	public void setChartDataMapper(ChartDataMapper chartDataMapper) {
		this.chartDataMapper = chartDataMapper;
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
