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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.ColumnDefinition;
import com.glaf.core.domain.Database;
import com.glaf.core.domain.TableDefinition;
import com.glaf.core.el.ExpressionTools;
import com.glaf.core.entity.SqlExecutor;
import com.glaf.core.jdbc.BulkInsertBean;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.jdbc.QueryConnectionFactory;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.tree.component.TreeComponent;
import com.glaf.core.tree.component.TreeRepository;
import com.glaf.core.tree.helper.JacksonTreeHelper;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.StringTools;
import com.glaf.matrix.adjust.domain.TreeDataAdjust;
import com.glaf.matrix.adjust.service.TreeDataAdjustService;
import com.glaf.matrix.util.SysParams;

/**
 * 树形结构逐级汇总
 *
 */
public class TreeTableAggregateBean {

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
					logger.debug("--------------execute aggregate----------------");
					List<ColumnDefinition> columns = DBUtils.getColumnDefinitions(conn, dataAdjust.getTableName());
					boolean treeIdColumnExists = false;
					for (ColumnDefinition column : columns) {
						if (StringUtils.equals(column.getColumnName().toLowerCase(),
								dataAdjust.getTreeIdColumn().toLowerCase())) {
							treeIdColumnExists = true;
							break;
						}
					}

					String targetTableName = dataAdjust.getTableName();
					if (StringUtils.isNotEmpty(dataAdjust.getTargetTableName())) {
						targetTableName = dataAdjust.getTargetTableName();
						if (StringUtils.contains(targetTableName, "$")) {
							Map<String, Object> parameter2 = new HashMap<String, Object>();
							SysParams.putInternalParams(parameter2);
							targetTableName = ExpressionTools.evaluate(targetTableName, parameter2);
						}
					}

					TableDefinition tableDefinition = new TableDefinition();
					tableDefinition.setTableName(targetTableName);
					tableDefinition.setColumns(columns);

					if (DBUtils.tableExists(conn, targetTableName)) {
						conn.setAutoCommit(false);
						DBUtils.alterTable(conn, tableDefinition);
						conn.commit();
					} else {
						conn.setAutoCommit(false);
						DBUtils.createTable(conn, tableDefinition);
						conn.commit();
					}

					if (!treeIdColumnExists) {
						if (StringUtils.equals(dataAdjust.getPreprocessFlag(), "Y")) {
							ColumnDefinition column = new ColumnDefinition();
							column.setColumnName(dataAdjust.getTreeIdColumn());
							column.setJavaType("String");
							column.setLength(1000);
							columns.add(column);
							tableDefinition = new TableDefinition();
							tableDefinition.setTableName(targetTableName);
							tableDefinition.setColumns(columns);
							conn.setAutoCommit(false);
							DBUtils.alterTable(conn, tableDefinition);
							conn.commit();
						}
					}

					boolean adjustColumnExists = false;
					String adjustColumn = dataAdjust.getAdjustColumn().toLowerCase();
					List<String> aggrColumns = StringTools.split(adjustColumn);

					for (ColumnDefinition column : columns) {
						if (StringUtils.equals(column.getColumnName().toLowerCase(),
								dataAdjust.getAdjustColumn().toLowerCase())) {
							adjustColumnExists = true;
							break;
						}
					}

					if (StringUtils.equals(dataAdjust.getAdjustType(), "treeAggregate")) {
						for (String aggrColumn : aggrColumns) {
							ColumnDefinition column = new ColumnDefinition();
							column.setColumnName(aggrColumn + "_sum");
							column.setJavaType("Double");
							if (!columns.contains(column)) {
								columns.add(column);
							}
						}
						tableDefinition = new TableDefinition();
						tableDefinition.setTableName(targetTableName);
						tableDefinition.setColumns(columns);
						conn.setAutoCommit(false);
						DBUtils.alterTable(conn, tableDefinition);
						conn.commit();
						adjustColumnExists = true;
					}

					if (!adjustColumnExists) {
						ColumnDefinition column = new ColumnDefinition();
						column.setColumnName(dataAdjust.getAdjustColumn());
						column.setJavaType("String");
						column.setLength(4000);
						columns.add(column);
						tableDefinition = new TableDefinition();
						tableDefinition.setTableName(targetTableName);
						tableDefinition.setColumns(columns);
						conn.setAutoCommit(false);
						DBUtils.alterTable(conn, tableDefinition);
						conn.commit();
					}

					List<TreeComponent> components = this.getTreeComponents(conn, dataAdjust, parameter, aggrColumns);
					JdbcUtils.close(conn);// 关闭连接

					List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
					Map<String, Map<String, Object>> updateDataMap = new HashMap<String, Map<String, Object>>();
					Map<String, Map<String, Object>> calDataMap = new HashMap<String, Map<String, Object>>();
					JacksonTreeHelper treeHelper = new JacksonTreeHelper();
					TreeRepository repository = treeHelper.buildTree(components);
					for (TreeComponent component : components) {
						dataList.add(component.getDataMap());
					}

					this.populate(dataAdjust, repository.getTopTrees(), updateDataMap, calDataMap, aggrColumns);

					List<Map<String, Object>> updateDataList = new ArrayList<Map<String, Object>>();
					Set<Entry<String, Map<String, Object>>> entrySet = updateDataMap.entrySet();
					for (Entry<String, Map<String, Object>> entry : entrySet) {
						Map<String, Object> value = entry.getValue();
						updateDataList.add(value);
					}

					if (updateDataList.size() > 0) {
						conn = DBConnectionFactory.getConnection(database.getName());
						conn.setAutoCommit(false);
						this.updateData(conn, dataAdjust, columns, updateDataList, aggrColumns);
						conn.commit();
						JdbcUtils.close(conn);// 关闭连接
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
			Map<String, Object> parameter, List<String> aggrColumns) {

		if (StringUtils.isNotEmpty(dataAdjust.getSqlCriteria())) {
			if (!DBUtils.isAllowedSql(dataAdjust.getSqlCriteria())) {
				throw new RuntimeException("sql not allowed");
			}
		}

		StringBuilder buffer = new StringBuilder();
		buffer.append(" select ");
		buffer.append(dataAdjust.getPrimaryKey()).append(", ");
		buffer.append(dataAdjust.getIdColumn()).append(", ");
		buffer.append(dataAdjust.getParentIdColumn());

		for (String aggrColumn : aggrColumns) {
			buffer.append(", ").append(aggrColumn);
		}

		if (StringUtils.isNotEmpty(dataAdjust.getTreeIdColumn())) {
			buffer.append(", ").append(dataAdjust.getTreeIdColumn());
		}

		if (StringUtils.isNotEmpty(dataAdjust.getNameColumn())) {
			buffer.append(", ").append(dataAdjust.getNameColumn());
		}
		buffer.append(" from ").append(dataAdjust.getTableName());
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
					if (!aggrColumns.contains(columnName + "_sum")) {// 排除sum列
						dataMap.put(columnName, rs.getObject(columnName));
					}
				}
				dataList.add(dataMap);
			}

			String idColumn = columnMap.get(dataAdjust.getIdColumn().toLowerCase()).toLowerCase();
			String parentIdColumn = columnMap.get(dataAdjust.getParentIdColumn().toLowerCase()).toLowerCase();
			String treeIdColumn = null;
			if (dataAdjust.getTreeIdColumn() != null) {
				treeIdColumn = columnMap.get(dataAdjust.getTreeIdColumn().toLowerCase());
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
			Map<String, Map<String, Object>> updateDataMap, Map<String, Map<String, Object>> calDataMap,
			List<String> aggrColumns) {
		if (components != null && !components.isEmpty()) {
			for (TreeComponent component : components) {
				List<TreeComponent> children = component.getComponents();
				if (children != null && children.size() > 0) {// 如果有下级节点就一直找
					this.populate(dataAdjust, children, updateDataMap, calDataMap, aggrColumns);
				}
				/**
				 * 没有下级节点了，即已经到叶节点了，累加
				 */
				TreeComponent parent = component.getParent();
				if (parent != null) {
					// logger.debug(ParamUtils.getString(parent.getDataMap(),
					// dataAdjust.getTreeIdColumn()) + "->"
					// + ParamUtils.getString(parent.getDataMap(), dataAdjust.getNameColumn()));
					Map<String, Object> dataMap = component.getDataMap();
					Map<String, Object> parentDataMap = parent.getDataMap();
					String id = ParamUtils.getString(dataMap, dataAdjust.getIdColumn());
					String parentId = ParamUtils.getString(parentDataMap, dataAdjust.getIdColumn());
					Map<String, Object> cmap = calDataMap.get(parentId);
					boolean cal = false;
					if (cmap == null) {
						cmap = new HashMap<String, Object>();
						cal = true;
					} else {
						if (cmap.get(id) == null) {
							cal = true;
						}
					}
					if (cal) {
						for (String aggrColumn : aggrColumns) {
							double val = ParamUtils.getDouble(dataMap, aggrColumn);
							double parentVal = ParamUtils.getDouble(parentDataMap, aggrColumn);
							double sumVal = parentVal + val;// 值累加到父节点
							parentDataMap.put(aggrColumn, sumVal);
						}
						parent.setDataMap(parentDataMap);

						updateDataMap.put(id, dataMap);
						updateDataMap.put(parentId, parentDataMap);
						cmap.put(id, id);
						calDataMap.put(parentId, cmap);
					}
					this.populateParent(dataAdjust, parent, updateDataMap, calDataMap, aggrColumns);
				}
			}
		}
	}

	protected void populateParent(TreeDataAdjust dataAdjust, TreeComponent pp,
			Map<String, Map<String, Object>> updateDataMap, Map<String, Map<String, Object>> calDataMap,
			List<String> aggrColumns) {
		if (pp == null) {
			return;
		}
		List<TreeComponent> children = pp.getComponents();
		if (children != null && children.size() > 0) {
			for (TreeComponent component : children) {
				if (component.getComponents() != null && component.getComponents().size() > 0) {
					this.populate(dataAdjust, component.getComponents(), updateDataMap, calDataMap, aggrColumns);
				} else {
					/**
					 * 没有下级节点了，即已经到叶节点了，累加
					 */
					TreeComponent parent = component.getParent();
					if (parent != null) {
						Map<String, Object> dataMap = component.getDataMap();
						Map<String, Object> parentDataMap = parent.getDataMap();
						String id = ParamUtils.getString(dataMap, dataAdjust.getIdColumn());
						String parentId = ParamUtils.getString(parentDataMap, dataAdjust.getIdColumn());
						Map<String, Object> cmap = calDataMap.get(parentId);
						boolean cal = false;
						if (cmap == null) {
							cmap = new HashMap<String, Object>();
							cal = true;
						} else {
							if (cmap.get(id) == null) {
								cal = true;
							}
						}
						if (cal) {
							for (String aggrColumn : aggrColumns) {
								double val = ParamUtils.getDouble(dataMap, aggrColumn);
								double parentVal = ParamUtils.getDouble(parentDataMap, aggrColumn);
								double sumVal = parentVal + val;// 值累加到父节点
								parentDataMap.put(aggrColumn, sumVal);
							}
							parent.setDataMap(parentDataMap);

							updateDataMap.put(id, dataMap);
							updateDataMap.put(parentId, parentDataMap);
							cmap.put(id, id);
							calDataMap.put(parentId, cmap);
						}
					}
				}
			}
		}
	}

	protected void updateData(Connection conn, TreeDataAdjust dataAdjust, List<ColumnDefinition> columns,
			List<Map<String, Object>> updateDataList, final List<String> aggrColumns) {
		if (updateDataList.size() > 0) {
			Map<String, String> typeMap = new HashMap<String, String>();
			for (ColumnDefinition column : columns) {
				typeMap.put(column.getColumnName().toLowerCase(), column.getJavaType());
			}
			String targetTableName = dataAdjust.getTableName();
			if (StringUtils.isNotEmpty(dataAdjust.getTargetTableName())) {
				targetTableName = dataAdjust.getTargetTableName();
				if (StringUtils.contains(targetTableName, "$")) {
					Map<String, Object> parameter = new HashMap<String, Object>();
					SysParams.putInternalParams(parameter);
					targetTableName = ExpressionTools.evaluate(targetTableName, parameter);
				}
			}
			List<Map<String, Object>> newUpdateList = new ArrayList<Map<String, Object>>();
			TableDefinition tableDefinition = new TableDefinition();
			tableDefinition.setTableName(targetTableName);
			tableDefinition.setColumns(columns);
			String primaryKey = dataAdjust.getPrimaryKey().toLowerCase();
			String typeName = typeMap.get(dataAdjust.getPrimaryKey().toLowerCase());

			// logger.debug("updateDataList:" + updateDataList);
			logger.debug("updateDataList size:" + updateDataList.size());
			int index = 0;
			int total = 0;

			ResultSetMetaData rsmd = null;
			PreparedStatement psmt = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				if (StringUtils.isNotEmpty(dataAdjust.getTargetTableName())) {
					if (!(StringUtils.startsWithIgnoreCase(dataAdjust.getTargetTableName(), "etl_")
							|| StringUtils.startsWithIgnoreCase(dataAdjust.getTargetTableName(), "tmp")
							|| StringUtils.startsWithIgnoreCase(dataAdjust.getTargetTableName(), "sync")
							|| StringUtils.startsWithIgnoreCase(dataAdjust.getTargetTableName(), "useradd")
							|| StringUtils.startsWithIgnoreCase(dataAdjust.getTargetTableName(), "tree_table_"))) {
						throw new RuntimeException("目标表必须以etl_,tmp,sync,useradd,tree_table_前缀开头。");
					}
					if (StringUtils.equals(dataAdjust.getDeleteFetch(), "Y")) {
						String targetTableName2 = dataAdjust.getTargetTableName();
						if (StringUtils.contains(targetTableName2, "$")) {
							Map<String, Object> parameter = new HashMap<String, Object>();
							SysParams.putInternalParams(parameter);
							targetTableName2 = ExpressionTools.evaluate(targetTableName2, parameter);
						}
						stmt = conn.createStatement();
						stmt.execute(" delete from " + targetTableName2);
						JdbcUtils.close(stmt);

						BulkInsertBean bulkInsertBean = new BulkInsertBean();
						bulkInsertBean.bulkInsert(null, conn, tableDefinition, updateDataList);
						logger.debug("执行批量插入->" + updateDataList.size());
						return;
					}
				}

				StringBuilder buffer = new StringBuilder();
				buffer.append(" update ").append(targetTableName).append(" set ");
				Iterator<String> iterator = aggrColumns.iterator();
				while (iterator.hasNext()) {
					buffer.append(iterator.next()).append("_sum = ? ");
					if (iterator.hasNext()) {
						buffer.append(",");
					}
				}
				buffer.append(" where ").append(dataAdjust.getPrimaryKey()).append("= ? ");
				String sql = buffer.toString();

				// logger.debug("update sql:" + sql);

				/**
				 * 是否为增量更新，即只修改变化的数据
				 */
				if (StringUtils.equals(dataAdjust.getUpdateFlag(), "A")) {
					buffer.delete(0, buffer.length());
					buffer.append(" select ");
					buffer.append(dataAdjust.getPrimaryKey());
					buffer.append(", ");
					iterator = aggrColumns.iterator();
					while (iterator.hasNext()) {
						String tmp = iterator.next();
						buffer.append(tmp).append(", ").append(tmp).append("_sum ");
						if (iterator.hasNext()) {
							buffer.append(",");
						}
					}
					buffer.append(" from ").append(targetTableName);
					logger.debug("->" + buffer.toString());
					psmt = conn.prepareStatement(buffer.toString());
					rs = psmt.executeQuery();

					rsmd = rs.getMetaData();
					int count = rsmd.getColumnCount();

					List<String> columnNames = new ArrayList<String>();
					Map<String, String> columnMap = new HashMap<String, String>();
					for (int k = 1; k <= count; k++) {
						String columnName = rsmd.getColumnLabel(k);
						columnName = columnName.toLowerCase();
						columnNames.add(columnName);
						columnMap.put(columnName, columnName);
					}
					String tmp = null;
					Map<String, Object> rowMap = new HashMap<String, Object>();
					Map<String, String> kvMap = new TreeMap<String, String>();
					while (rs.next()) {
						rowMap.clear();
						buffer.delete(0, buffer.length());
						for (String columnName : columnNames) {
							rowMap.put(columnName, rs.getObject(columnName));
						}
						Iterator<String> iterator2 = aggrColumns.iterator();
						while (iterator2.hasNext()) {
							tmp = iterator2.next();
							// logger.debug(tmp + "=" + ParamUtils.getDouble(rowMap, tmp));
							buffer.append(ParamUtils.getDouble(rowMap, (tmp + "_sum")));
							if (iterator2.hasNext()) {
								buffer.append("|");
							}
						}
						String id = ParamUtils.getString(rowMap, dataAdjust.getPrimaryKey());
						kvMap.put(id, buffer.toString());
					}
					JdbcUtils.close(rs);
					JdbcUtils.close(psmt);

					String val = null;
					for (Map<String, Object> dataMap : updateDataList) {
						buffer.delete(0, buffer.length());
						Iterator<String> iterator3 = aggrColumns.iterator();
						while (iterator3.hasNext()) {
							tmp = iterator3.next();
							// logger.debug(tmp + "=" + ParamUtils.getDouble(dataMap, tmp));
							buffer.append(ParamUtils.getDouble(dataMap, tmp));
							if (iterator3.hasNext()) {
								buffer.append("|");
							}
						}
						String id = ParamUtils.getString(dataMap, dataAdjust.getPrimaryKey());
						// md5 = DigestUtils.md5Hex(buffer.toString());
						val = buffer.toString();
						if (kvMap.get(id) != null && StringUtils.equals(kvMap.get(id), val)) {
							// logger.debug(id + " skip.");
							continue;// 如果记录的MD5值相同，说明值也相同，跳过
						}
						newUpdateList.add(dataMap);
					}
				} else {
					newUpdateList.addAll(updateDataList);
				}

				if (newUpdateList.size() > 0) {
					psmt = conn.prepareStatement(sql);
					for (Map<String, Object> dataMap : newUpdateList) {
						total++;
						index = 1;
						iterator = aggrColumns.iterator();
						while (iterator.hasNext()) {
							psmt.setDouble(index++, ParamUtils.getDouble(dataMap, iterator.next()));
						}
						String key = ParamUtils.getString(dataMap, primaryKey);
						switch (typeName) {
						case "Integer":
							psmt.setInt(index++, Integer.parseInt(key));
							break;
						case "Long":
							psmt.setLong(index++, Long.parseLong(key));
							break;
						default:
							psmt.setString(index++, key);
							break;
						}
						psmt.addBatch();
						if (total % 500 == 0) {
							psmt.executeBatch();
							logger.debug("execute batch:" + total);
						}
					}
					psmt.executeBatch();
					logger.debug("execute batch:" + total);
				} else {
					logger.debug("无记录更新。");
				}
			} catch (Exception ex) {
				// ex.printStackTrace();
				logger.error("execute update error", ex);
				throw new RuntimeException(ex);
			} finally {
				JdbcUtils.close(rs);
				JdbcUtils.close(stmt);
				JdbcUtils.close(psmt);
			}
		}
	}

}
