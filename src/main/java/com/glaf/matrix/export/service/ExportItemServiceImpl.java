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
import com.glaf.matrix.export.domain.ExportItem;
import com.glaf.matrix.export.mapper.ExportItemMapper;
import com.glaf.matrix.export.query.ExportItemQuery;

@Service("com.glaf.matrix.export.service.exportItemService")
@Transactional(readOnly = true)
public class ExportItemServiceImpl implements ExportItemService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ExportItemMapper exportItemMapper;

	public ExportItemServiceImpl() {

	}

	public int count(ExportItemQuery query) {
		return exportItemMapper.getExportItemCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			exportItemMapper.deleteExportItemById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				exportItemMapper.deleteExportItemById(id);
			}
		}
	}

	public ExportItem getExportItem(String id) {
		if (id == null) {
			return null;
		}
		ExportItem exportItem = exportItemMapper.getExportItemById(id);
		return exportItem;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getExportItemCountByQueryCriteria(ExportItemQuery query) {
		return exportItemMapper.getExportItemCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<ExportItem> getExportItemsByQueryCriteria(int start, int pageSize, ExportItemQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<ExportItem> rows = sqlSessionTemplate.selectList("getExportItems", query, rowBounds);
		return rows;
	}

	public List<ExportItem> list(ExportItemQuery query) {
		List<ExportItem> list = exportItemMapper.getExportItems(query);
		return list;
	}

	@Transactional
	public void save(ExportItem exportItem) {
		if (exportItem.getImageWidth() > 5000 || exportItem.getImageHeight() > 5000) {
			throw new RuntimeException("image size error.");
		}
		if (exportItem.getId() == null) {
			exportItem.setId(UUID32.getUUID());
			exportItem.setCreateTime(new Date());

			exportItemMapper.insertExportItem(exportItem);
		} else {
			exportItemMapper.updateExportItem(exportItem);
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

	@javax.annotation.Resource(name = "com.glaf.matrix.export.mapper.ExportItemMapper")
	public void setExportItemMapper(ExportItemMapper exportItemMapper) {
		this.exportItemMapper = exportItemMapper;
	}

}
