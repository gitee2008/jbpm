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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.ColumnDefinition;
import com.glaf.core.domain.Database;
import com.glaf.core.domain.TableDefinition;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.tree.component.TreeComponent;
import com.glaf.core.tree.component.TreeRepository;
import com.glaf.core.tree.helper.JacksonTreeHelper;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.ParamUtils;
import com.glaf.matrix.adjust.domain.TreeDataAdjust;
import com.glaf.matrix.adjust.service.TreeDataAdjustService;

public class TreeComponentAdjustBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public void execute(long databaseId, String adjustId, String index_id, Date adjustDate,
			Map<String, Object> parameter) {
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
				if (StringUtils.isNotEmpty(index_id) && StringUtils.isNotEmpty(dataAdjust.getAdjustColumn())) {
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

					List<TreeComponent> components = this.getTreeComponents(conn, dataAdjust, columns);
					JdbcUtils.close(conn);// 关闭连接

					List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> updateDataList = new ArrayList<Map<String, Object>>();
					JacksonTreeHelper treeHelper = new JacksonTreeHelper();
					TreeRepository repository = treeHelper.buildTree(components);
					for (TreeComponent component : components) {
						dataList.add(component.getDataMap());
					}

					// this.populate(dataAdjust, repository.getTopTrees(), updateDataList, index_id,
					// adjustDate);
					if (StringUtils.equals(dataAdjust.getAdjustType(), "dateLT")) {
						TreeTableTraverseMinDateBean bean = new TreeTableTraverseMinDateBean();
						bean.populate(dataAdjust, repository.getTopTrees(), updateDataList, index_id,
								adjustDate.getTime());
					} else if (StringUtils.equals(dataAdjust.getAdjustType(), "dateGT")) {
						TreeTableTraverseMaxDateBean bean = new TreeTableTraverseMaxDateBean();
						bean.populate(dataAdjust, repository.getTopTrees(), updateDataList, index_id,
								adjustDate.getTime());
					}
					logger.debug("updateDataList size:" + updateDataList.size());

					if (updateDataList.size() > 0) {
						conn = DBConnectionFactory.getConnection(database.getName());
						conn.setAutoCommit(false);
						this.updateData(conn, dataAdjust, columns, updateDataList);
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
	public List<TreeComponent> getTreeComponents(Connection conn, TreeDataAdjust dataAdjust,
			List<ColumnDefinition> columns) {
		List<String> columnNames = new ArrayList<String>();
		columnNames.add(dataAdjust.getPrimaryKey());
		columnNames.add(dataAdjust.getIdColumn());
		columnNames.add(dataAdjust.getParentIdColumn());
		columnNames.add(dataAdjust.getAdjustColumn());
		StringBuilder buffer = new StringBuilder();
		buffer.append(" select ");
		buffer.append(dataAdjust.getPrimaryKey()).append(", ");
		buffer.append(dataAdjust.getIdColumn()).append(", ");
		buffer.append(dataAdjust.getParentIdColumn()).append(", ");
		buffer.append(dataAdjust.getAdjustColumn());
		if (StringUtils.isNotEmpty(dataAdjust.getTreeIdColumn())) {
			buffer.append(", ").append(dataAdjust.getTreeIdColumn());
			columnNames.add(dataAdjust.getTreeIdColumn());
		}
		if (StringUtils.isNotEmpty(dataAdjust.getNameColumn())) {
			buffer.append(", ").append(dataAdjust.getNameColumn());
			columnNames.add(dataAdjust.getNameColumn());
		}
		buffer.append(" from ").append(dataAdjust.getTableName());
		String sql = buffer.toString();
		logger.debug("sql:" + sql);

		List<TreeComponent> list = new ArrayList<TreeComponent>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			while (rs.next()) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				for (String columnName : columnNames) {
					dataMap.put(columnName.toLowerCase(), rs.getObject(columnName));
				}
				dataList.add(dataMap);
			}

			Map<String, String> columnMap = new HashMap<String, String>();
			for (ColumnDefinition column : columns) {
				columnMap.put(column.getColumnName().toLowerCase(), column.getColumnName().toLowerCase());
			}

			String idColumn = columnMap.get(dataAdjust.getIdColumn().toLowerCase()).toLowerCase();
			String parentIdColumn = columnMap.get(dataAdjust.getParentIdColumn().toLowerCase()).toLowerCase();
			//String nameColumn = columnMap.get(dataAdjust.getNameColumn().toLowerCase()).toLowerCase();
			String dateColumn = columnMap.get(dataAdjust.getAdjustColumn().toLowerCase()).toLowerCase();
			for (Map<String, Object> dataMap : dataList) {
				TreeComponent tree = new TreeComponent();
				tree.setId(ParamUtils.getString(dataMap, idColumn));
				tree.setParentId(ParamUtils.getString(dataMap, parentIdColumn));
				//tree.setTitle(ParamUtils.getString(dataMap, nameColumn));
				Date date = ParamUtils.getDate(dataMap, dateColumn);
				if (date != null) {
					tree.setDateValue(date);
					tree.setLongValue(date.getTime());
				}
				tree.setDataMap(dataMap);
				list.add(tree);
			}

			return list;
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error("execute query error", ex);
			throw new RuntimeException(ex);
		} finally {
			JdbcUtils.close(rs);
			JdbcUtils.close(psmt);
		}
	}

	protected void populate(TreeDataAdjust dataAdjust, List<TreeComponent> components,
			List<Map<String, Object>> updateDataList, String index_id, Date adjustDate) {
		for (TreeComponent component : components) {
			/**
			 * 找到index_id指定的树节点，判断：<br/>
			 * 1、是否是单一节点<br/>
			 * 2、是否上级也是单一节点<br/>
			 */
			if (StringUtils.equals(index_id, component.getId())) {
				if (StringUtils.equals(dataAdjust.getLeafLimitFlag(), "Y")) {
					List<TreeComponent> children = component.getComponents();
					if (children != null && !children.isEmpty()) {
						return;// 不是叶节点不处理
					}
				}
				String dateColumn = dataAdjust.getAdjustColumn().toLowerCase();
				component.getDataMap().put(dateColumn, adjustDate);// 调整选中节点自己的日期
				updateDataList.add(component.getDataMap());// 加入修改对象
				TreeComponent parent = component.getParent();
				if (parent != null) {// 如果有父节点，继续处理父节点
					this.populateParent(dataAdjust, parent, updateDataList, index_id, adjustDate);
				}
			} else {
				List<TreeComponent> children = component.getComponents();
				if (children != null && children.size() > 0) {
					/**
					 * 递归查找，寻找选中的节点
					 */
					this.populate(dataAdjust, children, updateDataList, index_id, adjustDate);
				}
			}
		}
	}

	public void populateParent(TreeDataAdjust dataAdjust, TreeComponent parent,
			List<Map<String, Object>> updateDataList, String index_id, Date adjustDate) {
		if (parent != null) {
			logger.debug("##parent id:" + parent.getId());
			String dateColumn = dataAdjust.getAdjustColumn().toLowerCase();
			Date parentDate = ParamUtils.getDate(parent.getDataMap(), dateColumn);
			if (parentDate != null) {// 如果要调整的日期比父节点更小
				logger.debug("#parentDate:" + DateUtils.getDate(parentDate));
				if (adjustDate.getTime() < parentDate.getTime()) {
					parent.getDataMap().put(dateColumn, adjustDate);// 如果要调整的日期更小，父节点调整成更小的日期
					updateDataList.add(parent.getDataMap());
					if (parent.getParent() != null) {
						this.populateParent(dataAdjust, parent.getParent(), updateDataList, parent.getId(), adjustDate);
					}
				} else {
					List<TreeComponent> childList = parent.getComponents();
					if (childList != null) {
						logger.debug("##parent node size:" + childList.size());
						if (childList.size() == 1) {
							parent.getDataMap().put(dateColumn, adjustDate);// 如果要调整的日期更小，父节点调整成更小的日期
							updateDataList.add(parent.getDataMap());
							if (parent.getParent() != null) {
								this.populateParent(dataAdjust, parent.getParent(), updateDataList, parent.getId(),
										adjustDate);
							}
						} else {// 子节点不止一个
							Date tmpDate = adjustDate;
							int childNodes = 0;
							boolean childExists = false;// 检查子节点日期是否存在
							for (TreeComponent node : childList) {
								Date date = ParamUtils.getDate(node.getDataMap(), dateColumn);
								if (date != null) {
									if (date.getTime() < tmpDate.getTime()) {
										tmpDate = date;// 取其中更小的值
									}
									childExists = true;
									childNodes++;
								}
							}
							logger.debug("childExists:" + childExists);
							adjustDate = tmpDate;
							if (childNodes == 1) {
								parent.getDataMap().put(dateColumn, adjustDate);// 如果要调整的日期更小，父节点调整成更小的日期
								updateDataList.add(parent.getDataMap());
								if (parent.getParent() != null) {
									this.populateParent(dataAdjust, parent.getParent(), updateDataList, parent.getId(),
											adjustDate);
								}
							}
						}
					}
				}
			} else {
				/**
				 * 处理父节点的兄弟节点
				 */
				if (parent.getParent() != null) {
					List<TreeComponent> nodeList = parent.getParent().getComponents();
					Date tmpDate = adjustDate;
					boolean childExists = false;// 检查子节点日期是否存在
					if (nodeList.size() > 1) {// 子节点不止一个
						for (TreeComponent node : nodeList) {
							Date date = ParamUtils.getDate(node.getDataMap(), dateColumn);
							if (date != null) {
								if (date.getTime() < tmpDate.getTime()) {
									tmpDate = date;// 取其中更小的值
								}
								childExists = true;
							}
						}
						adjustDate = tmpDate;
					} else {
						parentDate = adjustDate;
						parent.getDataMap().put(dateColumn, adjustDate);
						updateDataList.add(parent.getDataMap());
					}
					if (parentDate == null || (!childExists)) {
						parent.getDataMap().put(dateColumn, adjustDate);// 如果要调整的日期更小，父节点调整成更小的日期
						updateDataList.add(parent.getDataMap());
					}

					this.populateParent(dataAdjust, parent.getParent(), updateDataList, parent.getId(), adjustDate);

				} else {
					if (parentDate == null) {
						parent.getDataMap().put(dateColumn, adjustDate);// 如果要调整的日期更小，父节点调整成更小的日期
						updateDataList.add(parent.getDataMap());
						if (parent.getParent() != null) {
							this.populateParent(dataAdjust, parent.getParent(), updateDataList, parent.getId(),
									adjustDate);
						}
					}
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
			logger.debug("updateDataList:" + updateDataList);
			logger.debug("updateDataList size:" + updateDataList.size());
			Set<String> keys = new HashSet<String>();
			PreparedStatement psmt = null;
			ResultSet rs = null;
			int index = 0;
			try {
				psmt = conn.prepareStatement(sql);
				for (Map<String, Object> dataMap : updateDataList) {
					String key = ParamUtils.getString(dataMap, primaryKey);
					if (!keys.contains(key)) {
						psmt.setTimestamp(1, ParamUtils.getTimestamp(dataMap, adjustColumn));
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
