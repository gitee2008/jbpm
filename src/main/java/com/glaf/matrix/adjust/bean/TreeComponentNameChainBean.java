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
import java.sql.ResultSetMetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.ColumnDefinition;
import com.glaf.core.domain.Database;
import com.glaf.core.domain.TableDefinition;
import com.glaf.core.entity.SqlExecutor;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.jdbc.QueryConnectionFactory;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.tree.component.TreeComponent;
import com.glaf.core.tree.component.TreeRepository;
import com.glaf.core.tree.helper.JacksonTreeHelper;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.ParamUtils;

import com.glaf.matrix.adjust.domain.TreeDataAdjust;
import com.glaf.matrix.adjust.service.TreeDataAdjustService;
import com.glaf.template.util.TemplateUtils;

public class TreeComponentNameChainBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public void execute(long databaseId, String adjustId, Map<String, Object> parameter) {
		IDatabaseService databaseService = ContextFactory.getBean("databaseService");
		TreeDataAdjustService treeDataAdjustService = ContextFactory
				.getBean("com.glaf.matrix.adjust.service.treeDataAdjustService");
		TreeDataAdjust dataAdjust = null;
		Database database = null;
		Connection conn = null;
		try {
			dataAdjust = treeDataAdjustService.getTreeDataAdjust(adjustId);
			if (dataAdjust != null && dataAdjust.getLocked() == 0) {
				database = databaseService.getDatabaseById(databaseId);
				if (StringUtils.isNotEmpty(dataAdjust.getAdjustColumn())) {
					conn = DBConnectionFactory.getConnection(database.getName());
					logger.debug("--------------execute adjust----------------");
					List<ColumnDefinition> columns = DBUtils.getColumnDefinitions(conn, dataAdjust.getTableName());
					boolean treeIdColumnExists = false;
					for (ColumnDefinition column : columns) {
						if (StringUtils.equals(column.getColumnName().toLowerCase(),
								dataAdjust.getTreeIdColumn().toLowerCase())) {
							treeIdColumnExists = true;
							break;
						}
					}

					if (!treeIdColumnExists) {
						if (StringUtils.equals(dataAdjust.getPreprocessFlag(), "Y")) {
							ColumnDefinition column = new ColumnDefinition();
							column.setColumnName(dataAdjust.getTreeIdColumn());
							column.setJavaType("String");
							column.setLength(1000);
							columns.add(column);
							TableDefinition tableDefinition = new TableDefinition();
							tableDefinition.setTableName(dataAdjust.getTableName());
							tableDefinition.setColumns(columns);
							conn.setAutoCommit(false);
							DBUtils.alterTable(conn, tableDefinition);
							conn.commit();
						}
					}

					boolean adjustColumnExists = false;
					for (ColumnDefinition column : columns) {
						if (StringUtils.equals(column.getColumnName().toLowerCase(),
								dataAdjust.getAdjustColumn().toLowerCase())) {
							adjustColumnExists = true;
							break;
						}
					}

					if (!adjustColumnExists) {
						ColumnDefinition column = new ColumnDefinition();
						column.setColumnName(dataAdjust.getAdjustColumn());
						column.setJavaType("String");
						column.setLength(4000);
						columns.add(column);
						TableDefinition tableDefinition = new TableDefinition();
						tableDefinition.setTableName(dataAdjust.getTableName());
						tableDefinition.setColumns(columns);
						conn.setAutoCommit(false);
						DBUtils.alterTable(conn, tableDefinition);
						conn.commit();
					}

					List<TreeComponent> components = this.getTreeComponents(conn, dataAdjust, parameter);
					JdbcUtils.close(conn);// 关闭连接

					List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> updateDataList = new ArrayList<Map<String, Object>>();
					JacksonTreeHelper treeHelper = new JacksonTreeHelper();
					TreeRepository repository = treeHelper.buildTree(components);
					for (TreeComponent component : components) {
						dataList.add(component.getDataMap());
					}

					this.populate(dataAdjust, repository.getTopTrees(), updateDataList, parameter);

					if (updateDataList.size() > 0) {
						if (updateDataList.size() > 2000 && StringUtils.equals(dataAdjust.getForkJoinFlag(), "Y")) {
							int total = 0;
							int counter = 0;
							List<List<Map<String, Object>>> listArray = this.splitList(updateDataList, 2000);
							ForkJoinPool pool = ForkJoinPool.commonPool();
							logger.info("准备执行并行任务...");
							int len = listArray.size();
							logger.info("并行任务数:" + len);
							try {
								for (int i = 0; i < len; i++) {
									List<Map<String, Object>> updateList = listArray.get(i);
									total++;
									UpdateDataTask task = new UpdateDataTask(database, dataAdjust, columns, updateList);
									Future<Boolean> result = pool.submit(task);
									if (result != null && result.get() != null && result.get().booleanValue()) {
										counter++;
									}
									updateList.clear();
									updateList = null;
								}
								// 线程阻塞，等待所有任务完成
								try {
									pool.awaitTermination(50, TimeUnit.MILLISECONDS);
								} catch (InterruptedException ex) {
								}
							} catch (java.lang.Throwable ex) {
								logger.error("update data list error", ex);
							} finally {
								pool.shutdown();
								logger.info("并行任务已经结束。");
							}
							if (total == counter) {
								logger.debug("所有操作成功。");
							} else {
								logger.debug("操作失败。");
							}
						} else {
							conn = DBConnectionFactory.getConnection(database.getName());
							conn.setAutoCommit(false);
							this.updateData(conn, dataAdjust, columns, updateDataList);
							conn.commit();
							JdbcUtils.close(conn);// 关闭连接
						}
					}
				}
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error("execute adjust error", ex);
			throw new RuntimeException(ex);
		} finally {
			JdbcUtils.close(conn);
		}
	}

	public List<List<Map<String, Object>>> splitList(List<Map<String, Object>> srcList, int size) {
		List<List<Map<String, Object>>> listArr = new ArrayList<List<Map<String, Object>>>();
		// 获取被拆分的数组个数
		int arrSize = srcList.size() % size == 0 ? srcList.size() / size : srcList.size() / size + 1;
		for (int i = 0; i < arrSize; i++) {
			List<Map<String, Object>> sub = new ArrayList<Map<String, Object>>();
			// 把指定索引数据放入到list中
			for (int j = i * size; j <= size * (i + 1) - 1; j++) {
				if (j <= srcList.size() - 1) {
					sub.add(srcList.get(j));
				}
			}
			listArr.add(sub);
		}
		return listArr;
	}

	/**
	 * 选择定义的字段构建树结构
	 * 
	 * @param conn
	 * @param dataAdjust
	 * @param columns
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<TreeComponent> getTreeComponents(Connection conn, TreeDataAdjust dataAdjust,
			Map<String, Object> parameter) {

		if (StringUtils.isNotEmpty(dataAdjust.getSqlCriteria())) {
			if (!DBUtils.isAllowedSql(dataAdjust.getSqlCriteria())) {
				throw new RuntimeException("sql not allowed");
			}
		}
		StringBuilder buffer = new StringBuilder();

		if (StringUtils.isNotEmpty(dataAdjust.getSqlCriteria())) {
			buffer.append(dataAdjust.getSqlCriteria());
		} else {
			buffer.append(" select * from ").append(dataAdjust.getTableName());
		}

		String sql = buffer.toString();
		SqlExecutor sqlExecutor = null;
		if (StringUtils.isNotEmpty(dataAdjust.getSqlCriteria())) {
			sqlExecutor = DBUtils.replaceSQL(sql, parameter);
			sql = sqlExecutor.getSql();
		}

		logger.debug("sql:" + sql);

		List<TreeComponent> list = new ArrayList<TreeComponent>();
		PreparedStatement psmt = null;
		ResultSetMetaData rsmd = null;
		ResultSet rs = null;
		long ts = 0;
		try {
			ts = System.currentTimeMillis();
			QueryConnectionFactory.getInstance().register(ts, conn);
			psmt = conn.prepareStatement(sql);
			if (sqlExecutor != null && sqlExecutor.getParameter() != null) {
				List<Object> values = (List<Object>) sqlExecutor.getParameter();
				JdbcUtils.fillStatement(psmt, values);
			}
			rs = psmt.executeQuery();
			rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			List<String> columnNames = new ArrayList<String>();
			Map<String, String> columnMap = new HashMap<String, String>();
			for (int index = 1; index <= count; index++) {
				String columnName = rsmd.getColumnLabel(index);
				columnName = columnName.toLowerCase();
				columnNames.add(columnName);
				columnMap.put(columnName, columnName);
			}
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			while (rs.next()) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				for (String columnName : columnNames) {
					dataMap.put(columnName, rs.getObject(columnName));
				}
				dataList.add(dataMap);
			}

			String idColumn = columnMap.get(dataAdjust.getIdColumn().toLowerCase()).toLowerCase();
			String parentIdColumn = columnMap.get(dataAdjust.getParentIdColumn().toLowerCase()).toLowerCase();
			String treeIdColumn = null;
			if (dataAdjust.getTreeIdColumn() != null) {
				treeIdColumn = columnMap.get(dataAdjust.getTreeIdColumn().toLowerCase()).toLowerCase();
			}

			for (Map<String, Object> dataMap : dataList) {
				TreeComponent tree = new TreeComponent();
				tree.setId(ParamUtils.getString(dataMap, idColumn));
				tree.setParentId(ParamUtils.getString(dataMap, parentIdColumn));
				if (treeIdColumn != null) {
					tree.setTreeId(ParamUtils.getString(dataMap, treeIdColumn));
				}
				tree.setDataMap(dataMap);
				// logger.debug(tree.getId() + " " + tree.getParentId() + " " +
				// tree.getTreeId());
				list.add(tree);
			}

			return list;
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error("execute query error", ex);
			throw new RuntimeException(ex);
		} finally {
			if (conn != null) {
				QueryConnectionFactory.getInstance().unregister(ts, conn);
			}
			JdbcUtils.close(rs);
			JdbcUtils.close(psmt);
		}
	}

	protected void populate(TreeDataAdjust dataAdjust, List<TreeComponent> components,
			List<Map<String, Object>> updateDataList, Map<String, Object> parameter) {
		if (components != null && !components.isEmpty()) {
			Map<String, Object> context = new HashMap<String, Object>();
			for (TreeComponent component : components) {
				Map<String, Object> dataMap = component.getDataMap();
				// logger.debug("" + dataMap);
				if (component.getParent() != null) {
					context.put("parent", component.getParent());
				}
				context.putAll(parameter);
				context.putAll(dataMap);
				String text = TemplateUtils.process(context, dataAdjust.getExpression());
				if (component.getParent() != null) {
					Map<String, Object> parentDataMap = component.getParent().getDataMap();
					String parentText = ParamUtils.getString(parentDataMap, dataAdjust.getAdjustColumn());
					if (StringUtils.isNotEmpty(parentText)) {
						String tmp = parentText + (dataAdjust.getConnector() != null ? dataAdjust.getConnector() : "")
								+ text;
						text = tmp;
					}
				}
				// logger.debug(component.getTreeId() + "->" + text);
				dataMap.put(dataAdjust.getAdjustColumn(), text);
				component.setDataMap(dataMap);
				updateDataList.add(dataMap);// 加入修改对象
				List<TreeComponent> children = component.getComponents();
				if (children != null && children.size() > 0) {
					this.populate(dataAdjust, children, updateDataList, parameter);
				}
			}
		}
	}

	protected void updateData(Connection conn, TreeDataAdjust dataAdjust, List<ColumnDefinition> columns,
			List<Map<String, Object>> updateDataList) {
		if (updateDataList.size() > 0) {
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
			int index = 0;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			try {
				psmt = conn.prepareStatement(sql);
				for (Map<String, Object> dataMap : updateDataList) {
					psmt.setString(1, ParamUtils.getString(dataMap, adjustColumn));
					String key = ParamUtils.getString(dataMap, primaryKey);
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
					index++;
					if (index % 500 == 0) {
						psmt.executeBatch();
						logger.debug("execute batch:" + index);
					}
				}
				psmt.executeBatch();
				logger.debug("execute batch:" + index);
			} catch (Exception ex) {
				// ex.printStackTrace();
				logger.error("execute update error", ex);
				throw new RuntimeException(ex);
			} finally {
				JdbcUtils.close(rs);
				JdbcUtils.close(psmt);
			}
		}
	}

}
