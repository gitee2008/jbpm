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
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportItem;
import com.glaf.matrix.export.domain.ExportTemplateVar;
import com.glaf.matrix.export.mapper.ExportAppMapper;
import com.glaf.matrix.export.mapper.ExportItemMapper;
import com.glaf.matrix.export.mapper.ExportTemplateVarMapper;
import com.glaf.matrix.export.query.ExportAppQuery;
import com.glaf.matrix.export.query.ExportItemQuery;
import com.glaf.matrix.export.query.ExportTemplateVarQuery;
import com.glaf.matrix.parameter.domain.ParameterConversion;
import com.glaf.matrix.parameter.service.ParameterConversionService;

@Service("com.glaf.matrix.export.service.exportAppService")
@Transactional(readOnly = true)
public class ExportAppServiceImpl implements ExportAppService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected JdbcTemplate jdbcTemplate;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected ExportAppMapper exportAppMapper;

	protected ExportItemMapper exportItemMapper;

	protected ExportTemplateVarMapper exportTemplateVarMapper;

	protected ParameterConversionService parameterConversionService;

	public ExportAppServiceImpl() {

	}

	public int count(ExportAppQuery query) {
		return exportAppMapper.getExportAppCount(query);
	}

	@Transactional
	public void deleteById(String id) {
		if (id != null) {
			exportAppMapper.deleteExportAppById(id);
		}
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				exportAppMapper.deleteExportAppById(id);
			}
		}
	}

	public ExportApp getExportApp(String expId) {
		if (expId == null) {
			return null;
		}
		ExportApp exportApp = exportAppMapper.getExportAppById(expId);
		if (exportApp != null) {
			ExportItemQuery query = new ExportItemQuery();
			query.expId(expId);
			List<ExportItem> items = exportItemMapper.getExportItems(query);
			exportApp.setItems(items);

			ExportTemplateVarQuery query2 = new ExportTemplateVarQuery();
			query2.expId(expId);
			List<ExportTemplateVar> vars = exportTemplateVarMapper.getExportTemplateVars(query2);
			exportApp.setVariables(vars);
		}
		return exportApp;
	}

	/**
	 * 根据查询参数获取记录总数
	 * 
	 * @return
	 */
	public int getExportAppCountByQueryCriteria(ExportAppQuery query) {
		return exportAppMapper.getExportAppCount(query);
	}

	/**
	 * 根据查询参数获取一页的数据
	 * 
	 * @return
	 */
	public List<ExportApp> getExportAppsByQueryCriteria(int start, int pageSize, ExportAppQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<ExportApp> rows = sqlSessionTemplate.selectList("getExportApps", query, rowBounds);
		return rows;
	}

	public List<ExportApp> list(ExportAppQuery query) {
		List<ExportApp> list = exportAppMapper.getExportApps(query);
		return list;
	}

	@Transactional
	public void save(ExportApp exportApp) {
		if (exportApp.getId() == null) {
			exportApp.setId(UUID32.getUUID());
			exportApp.setCreateTime(new Date());
			if (StringUtils.isEmpty(exportApp.getDeploymentId())) {
				exportApp.setDeploymentId(UUID32.getUUID());
			}
			if (exportApp.getPageNumPerSheet() > 800) {
				exportApp.setPageNumPerSheet(800);
			}
			exportAppMapper.insertExportApp(exportApp);
		} else {
			if (exportApp.getPageNumPerSheet() > 800) {
				exportApp.setPageNumPerSheet(800);
			}
			exportAppMapper.updateExportApp(exportApp);
		}
	}

	@Transactional
	public String saveAs(String expId, String createBy) {
		ExportApp model = this.getExportApp(expId);
		if (model != null) {
			model.setId(UUID32.getUUID());
			model.setCreateTime(new Date());
			model.setCreateBy(createBy);
			exportAppMapper.insertExportApp(model);

			if (model.getItems() != null && !model.getItems().isEmpty()) {
				for (ExportItem item : model.getItems()) {
					item.setId(UUID32.getUUID());
					item.setCreateTime(new Date());
					item.setCreateBy(createBy);
					item.setExpId(model.getId());
					exportItemMapper.insertExportItem(item);
				}
			}

			if (model.getVariables() != null && !model.getVariables().isEmpty()) {
				for (ExportTemplateVar var : model.getVariables()) {
					var.setId(UUID32.getUUID());
					var.setCreateTime(new Date());
					var.setCreateBy(createBy);
					var.setExpId(model.getId());
					exportTemplateVarMapper.insertExportTemplateVar(var);
				}
			}

			List<ParameterConversion> conversions = parameterConversionService.getParameterConversionsByKey(expId);
			if (conversions != null && !conversions.isEmpty()) {
				for (ParameterConversion param : conversions) {
					param.setKey(model.getId());
				}
				parameterConversionService.bulkInsert(conversions);
			}

			return model.getId();
		}
		return null;
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

	@javax.annotation.Resource(name = "com.glaf.matrix.export.mapper.ExportAppMapper")
	public void setExportAppMapper(ExportAppMapper exportAppMapper) {
		this.exportAppMapper = exportAppMapper;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.mapper.ExportItemMapper")
	public void setExportItemMapper(ExportItemMapper exportItemMapper) {
		this.exportItemMapper = exportItemMapper;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.mapper.ExportTemplateVarMapper")
	public void setExportTemplateVarMapper(ExportTemplateVarMapper exportTemplateVarMapper) {
		this.exportTemplateVarMapper = exportTemplateVarMapper;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.parameter.service.parameterConversionService")
	public void setParameterConversionService(ParameterConversionService parameterConversionService) {
		this.parameterConversionService = parameterConversionService;
	}
}
