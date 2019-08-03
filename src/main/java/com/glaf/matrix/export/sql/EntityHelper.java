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

package com.glaf.matrix.export.sql;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxls.common.JxlsException;

import com.glaf.core.config.Environment;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.Database;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.service.ITablePageService;
import com.glaf.core.util.DBUtils;

public class EntityHelper {
	protected final static Log logger = LogFactory.getLog(EntityHelper.class);

	protected final static int MAX_ROW_SIZE = 100000;

	protected final static int MAX_IMAGE_ROW_SIZE = 1000;

	protected Database database;

	protected Map<String, Object> parameter;

	protected ITablePageService tablePageService;

	public EntityHelper(Database database, Map<String, Object> parameter) {
		this.database = database;
		this.parameter = parameter;
	}

	public List<Map<String, Object>> getList(String sql) {
		if (sql == null) {
			throw new JxlsException("Null SQL statement");
		}

		if (!DBUtils.isLegalQuerySql(sql)) {
			throw new JxlsException("SQL statement Illegal");
		}

		sql = DBUtils.parseSQL(sql);

		String systemName = Environment.getCurrentSystemName();
		try {
			if (database != null) {
				Environment.setCurrentSystemName(database.getName());
			}
			return getTablePageService().getListData(sql, parameter, 0, MAX_ROW_SIZE);
		} catch (Exception ex) {
			throw new JxlsException("Failed to execute sql", ex);
		} finally {
			Environment.setCurrentSystemName(systemName);
		}
	}

	public ITablePageService getTablePageService() {
		if (tablePageService == null) {
			tablePageService = ContextFactory.getBean("tablePageService");
		}
		return tablePageService;
	}

	/**
	 * 根据标段查询数据
	 * 
	 * @param sql    SQL语句
	 * @param params 参数
	 * @return
	 */
	public List<Map<String, Object>> query(String sql, Object... params) {
		if (sql == null) {
			throw new JxlsException("Null SQL statement");
		}

		if (!DBUtils.isLegalQuerySql(sql)) {
			throw new JxlsException("SQL statement Illegal");
		}

		sql = DBUtils.parseSQL(sql);

		logger.debug("sql:" + sql);
		for (int i = 0; i < params.length; i++) {
			logger.debug("param_" + (i + 1) + "=" + params[i]);
			parameter.put("param_" + (i + 1), params[i]);
		}
		String systemName = Environment.getCurrentSystemName();
		try {
			if (database != null) {
				Environment.setCurrentSystemName(database.getName());
			}
			return getTablePageService().getListData(sql, parameter, 0, MAX_ROW_SIZE);
		} catch (Exception ex) {
			throw new JxlsException("Failed to execute sql", ex);
		} finally {
			Environment.setCurrentSystemName(systemName);
		}
	}

	/**
	 * 根据标段查询数据
	 * 
	 * @param mapping 标段别名
	 * @param sql     SQL语句
	 * @param params  参数
	 * @return
	 */
	public List<Map<String, Object>> queryx(String mapping, String sql, Object... params) {
		if (sql == null) {
			throw new JxlsException("Null SQL statement");
		}

		if (!DBUtils.isLegalQuerySql(sql)) {
			throw new JxlsException("SQL statement Illegal");
		}

		sql = DBUtils.parseSQL(sql);

		logger.debug("sql:" + sql);
		for (int i = 0; i < params.length; i++) {
			logger.debug("param_" + (i + 1) + "=" + params[i]);
			parameter.put("param_" + (i + 1), params[i]);
		}

		String systemName = Environment.getCurrentSystemName();
		try {
			IDatabaseService databaseService = ContextFactory.getBean("databaseService");
			Database database2 = databaseService.getDatabaseByMapping(mapping);
			if (database2 != null) {
				Environment.setCurrentSystemName(database2.getName());
				return getTablePageService().getListData(sql, parameter, 0, MAX_ROW_SIZE);
			}
		} catch (Exception ex) {
			throw new JxlsException("Failed to execute sql", ex);
		} finally {
			Environment.setCurrentSystemName(systemName);
		}
		return null;
	}

	public void setTablePageService(ITablePageService tablePageService) {
		this.tablePageService = tablePageService;
	}

}
