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

package com.glaf.matrix.export.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.Database;
import com.glaf.core.el.ExpressionTools;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.tree.component.TreeComponent;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.LowerLinkedMap;
import com.glaf.core.util.ParamUtils;

import com.glaf.matrix.export.bean.XmlExportDataBean;
import com.glaf.matrix.export.domain.XmlExport;
import com.glaf.matrix.export.domain.XmlExportItem;
import com.glaf.matrix.export.factory.XmlExportFactory;

public class XmlExportTreeDataHandler implements TreeDataHandler {

	protected static final Log logger = LogFactory.getLog(XmlExportTreeDataHandler.class);

	protected XmlExportDataBean bean = new XmlExportDataBean();

	protected IDatabaseService databaseService;

	public XmlExportTreeDataHandler() {

	}

	/**
	 * 增加树节点
	 * 
	 * @param xmlExport  导出定义
	 * @param root       根节点
	 * @param databaseId 数据库编号
	 */
	@Override
	public void addChild(XmlExport xmlExport, TreeComponent root, long databaseId) {
		List<XmlExport> list = XmlExportFactory.getAllChildren(xmlExport.getNodeId());
		if (list != null && !list.isEmpty()) {
			for (XmlExport export : list) {
				List<XmlExport> children = XmlExportFactory.getChildrenWithItems(export.getNodeId());
				export.setChildren(children);
				if (export.getNodeParentId() == xmlExport.getNodeId()) {
					xmlExport.addChild(export);
				}
			}
		}

		List<XmlExportItem> items = XmlExportFactory.getXmlExportItemsByExpId(xmlExport.getId());
		xmlExport.setItems(items);
		Database srcDatabase = getDatabaseService().getDatabaseById(databaseId);

		if (xmlExport.getNodeParentId() == 0) {// 顶层节点，只能有一个根节点
			// 根据定义补上根节点的属性
			if (StringUtils.equals(xmlExport.getResultFlag(), "S")) {
				if (xmlExport.getItems() != null && !xmlExport.getItems().isEmpty()) {
					String value = null;
					try {
						Map<String, Object> dataMap = null;
						if (StringUtils.equals(xmlExport.getResultFlag(), "S")) {
							dataMap = bean.getMapData(xmlExport, databaseId);
						}
						for (XmlExportItem item : xmlExport.getItems()) {
							/**
							 * 处理属性
							 */
							if (StringUtils.equals(item.getTagFlag(), "A")) {
								if (StringUtils.isNotEmpty(item.getExpression())) {
									value = ExpressionTools.evaluate(item.getExpression(), dataMap);
								} else {
									value = ParamUtils.getString(dataMap, item.getName().toLowerCase());
								}
								if (StringUtils.isNotEmpty(value)) {
									root.getDataMap().put(item.getName(), value);
									root.getDataMap().put(item.getName().toLowerCase(), value);
								}
							} else {
								root.getDataMap().put(item.getName(), value);
								root.getDataMap().put(item.getName().toLowerCase(), value);
							}
						}
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				}
			}
		}

		List<XmlExport> children = XmlExportFactory.getChildrenWithItems(xmlExport.getNodeId());
		if (children != null && !children.isEmpty()) {
			xmlExport.setNode(root);
			logger.debug("---------------------------gen tree child ----------------------------");
			for (XmlExport child : children) {
				child.setParent(xmlExport);
				this.addChild(child, srcDatabase);
			}
		}
	}

	/**
	 * 增加树节点
	 * 
	 * @param current
	 * @param databaseId
	 */
	public void addChild(XmlExport current, Database srcDatabase) {
		String value = null;
		Map<String, Object> dataMap = null;
		List<Map<String, Object>> resultList = null;
		try {
			if (current.getItems() == null || current.getItems().isEmpty()) {
				List<XmlExportItem> items = XmlExportFactory.getXmlExportItemsByExpId(current.getId());
				current.setItems(items);
			}

			String sql = current.getSql();
			String datasetId = current.getDatasetId();
			if (current.getItems() != null && !current.getItems().isEmpty()
					&& ((StringUtils.isNotEmpty(sql) && DBUtils.isLegalQuerySql(sql))
							|| StringUtils.isNotEmpty(datasetId))) {

				if (StringUtils.equals(current.getResultFlag(), "S")) {
					dataMap = bean.getMapData(current, srcDatabase);
				} else {
					resultList = bean.getListData(current, srcDatabase);
					current.setDataList(resultList);
				}

				/**
				 * 处理单一记录
				 */
				if (StringUtils.equals(current.getResultFlag(), "S")) {
					TreeComponent node = new TreeComponent();
					node.setCode(current.getName());
					node.setParent(current.getParent().getNode());
					current.setNode(node);
					for (XmlExportItem item : current.getItems()) {
						/**
						 * 处理属性
						 */
						if (StringUtils.equals(item.getTagFlag(), "A")) {
							if (StringUtils.isNotEmpty(item.getExpression())) {
								value = ExpressionTools.evaluate(item.getExpression(), dataMap);
							} else {
								value = ParamUtils.getString(dataMap, item.getName().toLowerCase());
							}
							if (StringUtils.isNotEmpty(value)) {
								current.getNode().getDataMap().put(item.getName(), value);
								current.getNode().getDataMap().put(item.getName().toLowerCase(), value);
							}
						} else {
							current.getNode().getDataMap().put(item.getName(), value);
							current.getNode().getDataMap().put(item.getName().toLowerCase(), value);
						}
					}
				} else {
					if (resultList != null && !resultList.isEmpty()) {
						current.setDataList(resultList);
						TreeComponent node = new TreeComponent();
						node.setCode(current.getName());
						node.setParent(current.getParent().getNode());
						current.setNode(node);
						List<XmlExport> children = current.getChildren();
						for (Map<String, Object> rowMap : resultList) {
							TreeComponent component = new TreeComponent();
							LowerLinkedMap dataLowerMap = new LowerLinkedMap();
							dataLowerMap.putAll(rowMap);
							component.setParent(node);
							component.getDataMap().putAll(rowMap);
							component.getDataMap().putAll(dataLowerMap);
							current.getNode().addChild(component);

							if (children != null && !children.isEmpty()) {
								Map<String, Object> parameter = new HashMap<String, Object>();
								Set<Entry<String, Object>> entrySet0 = rowMap.entrySet();
								for (Entry<String, Object> entry : entrySet0) {
									String key = entry.getKey();
									Object val = entry.getValue();
									parameter.put(key, val);
								}

								if (StringUtils.isNotEmpty(current.getName())) {
									Set<Entry<String, Object>> entrySet = current.getParameter().entrySet();
									for (Entry<String, Object> entry : entrySet) {
										String key = entry.getKey();
										Object val = entry.getValue();
										parameter.put(current.getName() + "_" + key, val);
									}
								}

								if (StringUtils.isNotEmpty(current.getMapping())) {
									Set<Entry<String, Object>> entrySet = current.getParameter().entrySet();
									for (Entry<String, Object> entry : entrySet) {
										String key = entry.getKey();
										Object val = entry.getValue();
										parameter.put(current.getMapping() + "_" + key, val);
									}
								}

								if (StringUtils.isNotEmpty(current.getName())) {
									Set<Entry<String, Object>> entrySet = rowMap.entrySet();
									for (Entry<String, Object> entry : entrySet) {
										String key = entry.getKey();
										Object val = entry.getValue();
										parameter.put(current.getName() + "_" + key, val);
									}
								}

								if (StringUtils.isNotEmpty(current.getMapping())) {
									Set<Entry<String, Object>> entrySet = rowMap.entrySet();
									for (Entry<String, Object> entry : entrySet) {
										String key = entry.getKey();
										Object val = entry.getValue();
										parameter.put(current.getMapping() + "_" + key, val);
									}
								}

								for (XmlExport child : children) {
									child.setParent(current);
									child.setParameter(parameter);
									this.addChild(child, srcDatabase);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public IDatabaseService getDatabaseService() {
		if (databaseService == null) {
			databaseService = ContextFactory.getBean("databaseService");
		}
		return databaseService;
	}

	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

}
