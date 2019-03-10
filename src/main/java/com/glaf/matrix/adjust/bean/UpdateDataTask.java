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

package com.glaf.matrix.adjust.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.domain.ColumnDefinition;
import com.glaf.core.domain.Database;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.ParamUtils;
import com.glaf.matrix.adjust.domain.TreeDataAdjust;
import com.zaxxer.hikari.HikariDataSource;

public class UpdateDataTask extends RecursiveTask<Boolean> {
	private static final long serialVersionUID = 1L;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected Database database;

	protected TreeDataAdjust dataAdjust;

	protected List<ColumnDefinition> columns;

	protected List<Map<String, Object>> updateDataList;

	public UpdateDataTask(Database database, TreeDataAdjust dataAdjust, List<ColumnDefinition> columns,
			List<Map<String, Object>> updateDataList) {
		this.database = database;
		this.dataAdjust = dataAdjust;
		this.columns = columns;
		this.updateDataList = updateDataList;
	}

	@Override
	protected Boolean compute() {
		return this.updateData(database, dataAdjust, columns, updateDataList);
	}

	protected boolean updateData(Database database, TreeDataAdjust dataAdjust, List<ColumnDefinition> columns,
			List<Map<String, Object>> updateDataList) {
		Map<String, String> typeMap = new HashMap<String, String>();
		for (ColumnDefinition column : columns) {
			typeMap.put(column.getColumnName().toLowerCase(), column.getJavaType());
		}
		String primaryKey = dataAdjust.getPrimaryKey().toLowerCase();
		String adjustColumn = dataAdjust.getAdjustColumn().toLowerCase();
		String typeName = typeMap.get(dataAdjust.getPrimaryKey().toLowerCase());
		String sql = " update " + dataAdjust.getTableName() + " set " + dataAdjust.getAdjustColumn() + " = ? where "
				+ dataAdjust.getPrimaryKey() + " = ? ";
		logger.debug("update sql:" + sql);
		// logger.debug("updateDataList:" + updateDataList);
		logger.debug("updateDataList size:" + updateDataList.size());
		Set<String> keys = new HashSet<String>();
		HikariDataSource ds = null;
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		int index = 0;
		try {
			ds = DBConnectionFactory.getDataSource(database);
			logger.debug("不使用数据库连接池更新数据......");
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			psmt = conn.prepareStatement(sql);
			for (Map<String, Object> dataMap : updateDataList) {
				String key = ParamUtils.getString(dataMap, primaryKey);
				if (!keys.contains(key)) {
					psmt.setString(1, ParamUtils.getString(dataMap, adjustColumn));
					switch (typeName) {
					case "Integer":
						psmt.setInt(2, Integer.parseInt(key));
						break;
					case "Long":
						psmt.setLong(2, Long.parseLong(key));
						break;
					default:
						psmt.setString(2, key);
						break;
					}
					psmt.addBatch();
					keys.add(key);
					index++;
					if (index % 500 == 0) {
						psmt.executeBatch();
						logger.debug("execute batch:" + index);
					}
				}
			}
			psmt.executeBatch();
			conn.commit();
			JdbcUtils.close(conn);// 关闭连接
			logger.debug("execute batch:" + index);
			return true;
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error("execute update error", ex);
			throw new RuntimeException(ex);
		} finally {
			JdbcUtils.close(rs);
			JdbcUtils.close(psmt);
			JdbcUtils.close(conn);// 关闭连接
			if (ds != null) {
				ds.close();
			}
		}
	}

}
