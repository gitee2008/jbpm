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

package com.glaf.matrix.adjust.service;

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

import com.glaf.matrix.adjust.domain.TreeDataAdjust;
import com.glaf.matrix.adjust.mapper.TreeDataAdjustMapper;
import com.glaf.matrix.adjust.query.TreeDataAdjustQuery;

@Service("com.glaf.matrix.adjust.service.treeDataAdjustService")
@Transactional(readOnly = true)
public class TreeDataAdjustServiceImpl implements TreeDataAdjustService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected TreeDataAdjustMapper treeDataAdjustMapper;

	public TreeDataAdjustServiceImpl() {

	}

	 

	public int count(TreeDataAdjustQuery query) {
		return treeDataAdjustMapper.getTreeDataAdjustCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			treeDataAdjustMapper.deleteTreeDataAdjustById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				treeDataAdjustMapper.deleteTreeDataAdjustById(id);
			}
		}
	}

	public TreeDataAdjust getTreeDataAdjust(String id) {
		if (id == null) {
			return null;
		}
		TreeDataAdjust treeDataAdjust = treeDataAdjustMapper.getTreeDataAdjustById(id);
		return treeDataAdjust;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getTreeDataAdjustCountByQueryCriteria(TreeDataAdjustQuery query) {
		return treeDataAdjustMapper.getTreeDataAdjustCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<TreeDataAdjust> getTreeDataAdjustsByQueryCriteria(int start, int pageSize, TreeDataAdjustQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<TreeDataAdjust> rows = sqlSessionTemplate.selectList("getTreeDataAdjusts", query, rowBounds);
		return rows;
	}

	public List<TreeDataAdjust> list(TreeDataAdjustQuery query) {
		List<TreeDataAdjust> list = treeDataAdjustMapper.getTreeDataAdjusts(query);
		return list;
	}

	@Transactional
	public void save(TreeDataAdjust treeDataAdjust) {
		if (StringUtils.isEmpty(treeDataAdjust.getId())) {
			treeDataAdjust.setId(UUID32.getUUID());
			treeDataAdjust.setCreateTime(new Date());
			// treeDataAdjust.setDeleteFlag(0);
			treeDataAdjustMapper.insertTreeDataAdjust(treeDataAdjust);
		} else {
			treeDataAdjust.setUpdateTime(new Date());
			treeDataAdjustMapper.updateTreeDataAdjust(treeDataAdjust);
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

	@javax.annotation.Resource(name = "com.glaf.matrix.adjust.mapper.TreeDataAdjustMapper")
	public void setTreeDataAdjustMapper(TreeDataAdjustMapper treeDataAdjustMapper) {
		this.treeDataAdjustMapper = treeDataAdjustMapper;
	}

}
