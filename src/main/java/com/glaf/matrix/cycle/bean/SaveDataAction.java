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

package com.glaf.matrix.cycle.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.domain.Database;
import com.glaf.core.domain.TableDefinition;
import com.glaf.core.jdbc.BulkInsertBean;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.jdbc.QueryHelper;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.LowerLinkedMap;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.QueryUtils;
import com.glaf.core.util.UUID32;
import com.glaf.matrix.cycle.domain.LoopSqlToTable;

public class SaveDataAction extends RecursiveAction {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static final long serialVersionUID = 1L;

	protected Database srcDatabase;

	protected Database targetDatabase;

	protected LoopSqlToTable app;

	protected Map<String, Object> parameter;

	protected TableDefinition tableDefinition;

	protected Date date;

	protected Map<String, String> keyMap;

	public SaveDataAction(Database srcDatabase, Database targetDatabase, LoopSqlToTable app,
			Map<String, Object> parameter, TableDefinition tableDefinition, Date date, Map<String, String> keyMap) {
		this.srcDatabase = srcDatabase;
		this.targetDatabase = targetDatabase;
		this.app = app;
		this.parameter = parameter;
		this.tableDefinition = tableDefinition;
		this.date = date;
		this.keyMap = keyMap;
	}

	@Override
	protected void compute() {
		this.saveDaily(srcDatabase, targetDatabase, app, parameter, tableDefinition, date, keyMap);
	}

	protected void saveDaily(Database srcDatabase, Database targetDatabase, LoopSqlToTable app,
			Map<String, Object> parameter, TableDefinition tableDefinition, Date date, Map<String, String> keyMap) {
		logger.debug("准备同步" + DateUtils.getDate(date) + "的数据......");
		Map<String, Map<String, Object>> dataListMap = new HashMap<String, Map<String, Object>>();
		List<Map<String, Object>> insertList = new ArrayList<Map<String, Object>>();
		QueryHelper helper = new QueryHelper();
		List<Map<String, Object>> resultList = null;
		LowerLinkedMap rowMap = null;
		Connection srcConn = null;
		Connection targetConn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		String primaryKey = app.getPrimaryKey();
		String sql = app.getSql();
		try {
			if (srcDatabase != null) {
				srcConn = DBConnectionFactory.getConnection(srcDatabase.getName());

				sql = QueryUtils.replaceSQLVars(sql, parameter);
				logger.debug("query sql:" + sql);
				if (StringUtils.equals(app.getSkipError(), "Y")) {
					try {
						resultList = helper.getResultList(srcConn, sql, parameter);
					} catch (java.lang.Throwable ex) {
					}
				} else {
					resultList = helper.getResultList(srcConn, sql, parameter);
				}
				JdbcUtils.close(srcConn);

				if (resultList != null && !resultList.isEmpty()) {
					for (Map<String, Object> dataMap : resultList) {
						rowMap = new LowerLinkedMap();
						rowMap.putAll(dataMap);
						if (StringUtils.isNotEmpty(primaryKey)) {
							String key = ParamUtils.getString(rowMap, primaryKey);
							if (keyMap.get(app.getId() + "_" + key) == null) {
								rowMap.put("ex_id_", app.getId() + "_" + key);
								rowMap.put("ex_syncid_", app.getId());
								rowMap.put("ex_databaseid_", srcDatabase.getId());
								rowMap.put("ex_discriminator_", srcDatabase.getDiscriminator());
								rowMap.put("ex_mapping_", srcDatabase.getMapping());
								rowMap.put("ex_date_", DateUtils.getDate(date));
								keyMap.put(app.getId() + "_" + key, srcDatabase.getId() + "_" + key);
								insertList.add(rowMap);
							}
						} else {
							rowMap.put("ex_id_", app.getId() + "_" + UUID32.getUUID());
							rowMap.put("ex_syncid_", app.getId());
							rowMap.put("ex_databaseid_", srcDatabase.getId());
							rowMap.put("ex_discriminator_", srcDatabase.getDiscriminator());
							rowMap.put("ex_mapping_", srcDatabase.getMapping());
							rowMap.put("ex_date_", DateUtils.getDate(date));
							insertList.add(rowMap);
						}
					}

					if (insertList.size() > 0) {
						if (targetDatabase != null) {
							targetConn = DBConnectionFactory.getConnection(targetDatabase.getName());
							logger.debug("targetConn:" + targetConn);
						} else {
							targetConn = DBConnectionFactory.getConnection();
						}
						BulkInsertBean insertBean = new BulkInsertBean();
						targetConn.setAutoCommit(false);
						if (StringUtils.equals(app.getDeleteFetch(), "Y")) {
							String day = DateUtils.getDate(date);
							String delSQL = " delete from " + app.getTargetTableName() + " where EX_SYNCID_ = '"
									+ app.getId() + "' and EX_DATE_ = '" + day + "' ";
							DBUtils.executeSchemaResource(targetConn, delSQL);
							logger.debug("delete sql:" + delSQL);
						}
						insertBean.bulkInsert(null, targetConn, tableDefinition, insertList);
						targetConn.commit();
						JdbcUtils.close(targetConn);
					}
					insertList.clear();
					dataListMap.clear();
					insertList = null;
					dataListMap = null;
				}
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error("execute sync error", ex);
			throw new RuntimeException(ex);
		} finally {
			JdbcUtils.close(rs);
			JdbcUtils.close(psmt);
			JdbcUtils.close(srcConn);
			JdbcUtils.close(targetConn);
		}
	}

}
