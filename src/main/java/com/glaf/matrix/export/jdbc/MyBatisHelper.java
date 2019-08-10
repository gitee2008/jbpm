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

package com.glaf.matrix.export.jdbc;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxls.common.JxlsException;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.Database;
import com.glaf.core.entity.SqlExecutor;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.jdbc.QueryConnectionFactory;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.QueryUtils;

public class MyBatisHelper {

	protected final static Log logger = LogFactory.getLog(MyBatisHelper.class);

	protected final static int MAX_ROW_SIZE = 100000;

	protected final static int MAX_IMAGE_ROW_SIZE = 1000;

	private Connection conn;

	private Map<String, Object> parameter;

	public MyBatisHelper(Connection conn, Map<String, Object> parameter) {
		this.conn = conn;
		this.parameter = parameter;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getList(String sql) {
		Object[] args = new Object[] {};
		if (parameter != null) {
			SqlExecutor sqlExecutor = QueryUtils.replaceSQL(sql, parameter);
			sql = sqlExecutor.getSql();
			List<Object> values = (List<Object>) sqlExecutor.getParameter();
			if (values != null && values.size() > 0) {
				args = new Object[] { values.size() };
				int i = 0;
				for (Object value : values) {
					args[i++] = value;
				}
			}
		}
		return this.query(conn, sql, args);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getList(String mapping, String sql) {
		Object[] args = new Object[] {};
		if (parameter != null) {
			SqlExecutor sqlExecutor = QueryUtils.replaceSQL(sql, parameter);
			sql = sqlExecutor.getSql();
			List<Object> values = (List<Object>) sqlExecutor.getParameter();
			if (values != null && values.size() > 0) {
				args = new Object[] { values.size() };
				int i = 0;
				for (Object value : values) {
					args[i++] = value;
				}
			}
		}
		IDatabaseService databaseService = ContextFactory.getBean("databaseService");
		Database database = databaseService.getDatabaseByMapping(mapping);
		if (database != null) {
			long ts = System.currentTimeMillis();
			java.sql.Connection connection = null;
			try {
				connection = DBConnectionFactory.getConnection(database.getName());
				if (connection != null) {
					QueryConnectionFactory.getInstance().register(ts, connection);
					return this.query(connection, sql, args);
				}
			} catch (Exception ex) {
				throw new JxlsException("Failed to execute sql", ex);
			} finally {
				if (connection != null) {
					QueryConnectionFactory.getInstance().unregister(ts, connection);
					JdbcUtils.close(connection);
				}
			}
		}
		return null;
	}

	public List<Map<String, Object>> query(String sql, Object... params) {
		return this.query(conn, sql, params);
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
		IDatabaseService databaseService = ContextFactory.getBean("databaseService");
		Database database = databaseService.getDatabaseByMapping(mapping);
		if (database != null) {
			long ts = System.currentTimeMillis();
			java.sql.Connection connection = null;
			try {
				connection = DBConnectionFactory.getConnection(database.getName());
				if (connection != null) {
					QueryConnectionFactory.getInstance().register(ts, connection);
					return this.query(connection, sql, params);
				}
			} catch (Exception ex) {
				throw new JxlsException("Failed to execute sql", ex);
			} finally {
				if (connection != null) {
					QueryConnectionFactory.getInstance().unregister(ts, connection);
					JdbcUtils.close(connection);
				}
			}
		}
		return null;
	}

	public List<Map<String, Object>> query(java.sql.Connection connection, String sql, Object... params) {
		List<Map<String, Object>> result = null;
		if (connection == null) {
			throw new JxlsException("Null jdbc connection");
		}
		if (sql == null) {
			throw new JxlsException("Null SQL statement");
		}

		if (!DBUtils.isLegalQuerySql(sql)) {
			throw new JxlsException("SQL statement Illegal");
		}

		sql = DBUtils.parseSQL(sql);

		logger.debug("sql:" + sql);
		for (int i = 0; i < params.length; i++) {
			logger.debug("param" + i + "=" + params[i]);
		}

		try {
			SqlRunner sqlRunner = new SqlRunner(connection);
			result = sqlRunner.selectAll(sql, MAX_ROW_SIZE, params);
		} catch (Exception ex) {
			throw new JxlsException("Failed to execute sql", ex);
		}
		return result;
	}

}
