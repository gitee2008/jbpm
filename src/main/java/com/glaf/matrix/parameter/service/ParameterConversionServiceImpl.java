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

package com.glaf.matrix.parameter.service;

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

import com.glaf.matrix.parameter.mapper.*;
import com.glaf.matrix.parameter.domain.*;
import com.glaf.matrix.parameter.query.*;

@Service("com.glaf.matrix.parameter.service.parameterConversionService")
@Transactional(readOnly = true)
public class ParameterConversionServiceImpl implements ParameterConversionService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ParameterConversionMapper parameterConversionMapper;

	public ParameterConversionServiceImpl() {

	}

	@Transactional
	public void bulkInsert(List<ParameterConversion> list) {
		for (ParameterConversion parameterConversion : list) {
			if (StringUtils.isEmpty(parameterConversion.getId())) {
				parameterConversion.setId(idGenerator.getNextId("SYS_PARAMETER_CONVERSION"));
			}
		}

		int batch_size = 50;
		List<ParameterConversion> rows = new ArrayList<ParameterConversion>(batch_size);

		for (ParameterConversion bean : list) {
			rows.add(bean);
			if (rows.size() > 0 && rows.size() % batch_size == 0) {
				if (StringUtils.equals(DBUtils.ORACLE, DBConnectionFactory.getDatabaseType())) {
					parameterConversionMapper.bulkInsertParameterConversion_oracle(rows);
				} else {
					parameterConversionMapper.bulkInsertParameterConversion(rows);
				}
				rows.clear();
			}
		}

		if (rows.size() > 0) {
			if (StringUtils.equals(DBUtils.ORACLE, DBConnectionFactory.getDatabaseType())) {
				parameterConversionMapper.bulkInsertParameterConversion_oracle(rows);
			} else {
				parameterConversionMapper.bulkInsertParameterConversion(rows);
			}
			rows.clear();
		}
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			parameterConversionMapper.deleteParameterConversionById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				parameterConversionMapper.deleteParameterConversionById(id);
			}
		}
	}

	public int count(ParameterConversionQuery query) {
		return parameterConversionMapper.getParameterConversionCount(query);
	}
	
	/**
	 * 根据查询参数获取记录列表
	 * 
	 * @return
	 */
	public List<ParameterConversion> getParameterConversionsByKey(String key){
		return parameterConversionMapper.getParameterConversionsByKey(key);
	}

	public List<ParameterConversion> list(ParameterConversionQuery query) {
		List<ParameterConversion> list = parameterConversionMapper.getParameterConversions(query);
		return list;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getParameterConversionCountByQueryCriteria(ParameterConversionQuery query) {
		return parameterConversionMapper.getParameterConversionCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<ParameterConversion> getParameterConversionsByQueryCriteria(int start, int pageSize,
			ParameterConversionQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<ParameterConversion> rows = sqlSessionTemplate.selectList("getParameterConversions", query, rowBounds);
		return rows;
	}

	public ParameterConversion getParameterConversion(String id) {
		if (id == null) {
			return null;
		}
		ParameterConversion parameterConversion = parameterConversionMapper.getParameterConversionById(id);
		return parameterConversion;
	}

	@Transactional
	public void save(ParameterConversion parameterConversion) {
		if (StringUtils.isEmpty(parameterConversion.getId())) {
			parameterConversion.setId(UUID32.getUUID());
			parameterConversion.setCreateTime(new Date());

			parameterConversionMapper.insertParameterConversion(parameterConversion);
		} else {
			parameterConversionMapper.updateParameterConversion(parameterConversion);
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

	@javax.annotation.Resource(name = "com.glaf.matrix.parameter.mapper.ParameterConversionMapper")
	public void setParameterConversionMapper(ParameterConversionMapper parameterConversionMapper) {
		this.parameterConversionMapper = parameterConversionMapper;
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
