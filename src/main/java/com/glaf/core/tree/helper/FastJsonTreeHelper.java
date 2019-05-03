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

package com.glaf.core.tree.helper;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.tree.component.TreeComponent;
import com.glaf.core.tree.component.TreeRepository;
import com.glaf.core.util.DateUtils;

public class FastJsonTreeHelper {
	protected static final Log logger = LogFactory.getLog(FastJsonTreeHelper.class);

	protected void addDataMap(TreeComponent component, JSONObject row) {
		if (component != null && component.getDataMap() != null) {
			Map<String, Object> dataMap = component.getDataMap();
			Set<Entry<String, Object>> entrySet = dataMap.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				String name = entry.getKey();
				Object value = entry.getValue();
				if (value != null) {
					if (value instanceof Date) {
						Date d = (Date) value;
						row.put(name, DateUtils.getDate(d));
					} else if (value instanceof Boolean) {
						row.put(name, (Boolean) value);
					} else if (value instanceof Integer) {
						row.put(name, (Integer) value);
					} else if (value instanceof Long) {
						row.put(name, (Long) value);
					} else if (value instanceof Double) {
						row.put(name, (Double) value);
					} else if (value instanceof Float) {
						row.put(name, (Float) value);
					} else {
						row.put(name, value.toString());
					}
				}
			}
		}

		if (component != null && component.getTreeObject() != null) {
			if (component.getTreeObject() instanceof TreeComponent) {
				TreeComponent tree = (TreeComponent) component.getTreeObject();
				row.put("id", tree.getId());
				row.put("_id_", tree.getId());
				row.put("_oid_", tree.getId());
				row.put("parentId", tree.getParentId());
				row.put("_parentId", tree.getParentId());
				row.put("name", tree.getTitle());
				row.put("text", tree.getTitle());
				row.put("checked", tree.isChecked());
				row.put("level", tree.getLevel());
			}
		}
	}

	public TreeRepository build(List<TreeComponent> treeModels) {
		Map<String, TreeComponent> treeMap = new java.util.HashMap<String, TreeComponent>();
		Map<String, TreeComponent> lockedMap = new java.util.HashMap<String, TreeComponent>();

		for (int i = 0, len = treeModels.size(); i < len; i++) {
			TreeComponent treeModel = (TreeComponent) treeModels.get(i);
			if (treeModel != null && StringUtils.equals(treeModel.getId(), treeModel.getParentId())) {
				treeModel.setParentId(null);
			}
			if (treeModel != null && treeModel.getId() != null) {
				treeMap.put(treeModel.getId(), treeModel);
			}
			if (treeModel != null && treeModel.getLocked() != 0) {
				/**
				 * 记录已经禁用的节点
				 */
				lockedMap.put(treeModel.getId(), treeModel);
			}
		}

		int lenx = treeModels.size();
		if (lenx > 500) {
			lenx = 500;
		}
		for (int i = 0; i < lenx; i++) {
			for (int j = 0, len2 = treeModels.size(); j < len2; j++) {
				TreeComponent tree = treeModels.get(j);
				/**
				 * 找到某个节点的父节点，如果被禁用，那么当前节点也设置为禁用
				 */
				if (lockedMap.get(tree.getParentId()) != null) {
					tree.setLocked(1);
				}
				TreeComponent parent = treeMap.get(tree.getParentId());
				tree.setParent(parent);
			}
		}

		TreeRepository repository = new TreeRepository();
		for (int i = 0, len = treeModels.size(); i < len; i++) {
			TreeComponent treeModel = treeModels.get(i);
			if (treeModel == null) {
				continue;
			}
			if (treeModel.getLocked() != 0) {
				continue;
			}
			if (treeModel.getParent() != null && treeModel.getParent().getLocked() != 0) {
				continue;
			}
			TreeComponent component = new TreeComponent();
			component.setId(treeModel.getId());
			component.setCode(treeModel.getId());
			component.setTitle(treeModel.getTitle());
			component.setChecked(treeModel.isChecked());
			component.setTreeObject(treeModel);
			component.setDescription(treeModel.getDescription());
			component.setLocation(treeModel.getUrl());
			component.setUrl(treeModel.getUrl());
			component.setTreeId(treeModel.getTreeId());
			component.setDataMap(treeModel.getDataMap());
			repository.addTree(component);
		}

		for (int i = 0, len = treeModels.size(); i < len; i++) {
			TreeComponent treeModel = treeModels.get(i);
			if (treeModel == null) {
				continue;
			}
			if (treeModel.getLocked() != 0) {
				continue;
			}
			if (treeModel.getParent() != null && treeModel.getParent().getLocked() != 0) {
				continue;
			}

			TreeComponent component = repository.getTree(treeModel.getId());
			String parentId = treeModel.getParentId();
			if (treeMap.get(parentId) != null) {
				TreeComponent parentTree = repository.getTree(parentId);
				if (parentTree == null) {
					TreeComponent parent = treeMap.get(parentId);
					parentTree = new TreeComponent();
					parentTree.setId(parent.getId());
					parentTree.setCode(parent.getId());
					parentTree.setTitle(parent.getTitle());
					parentTree.setChecked(parent.isChecked());
					parentTree.setTreeObject(parent);
					parentTree.setDescription(parent.getDescription());
					parentTree.setLocation(parent.getUrl());
					parentTree.setUrl(parent.getUrl());
					parentTree.setTreeId(parent.getTreeId());
					parentTree.setDataMap(parent.getDataMap());
					// repository.addTree(parentTree);
				}
				component.setParent(parentTree);
			}
		}
		return repository;
	}

	public void buildTree(JSONObject row, TreeComponent treeComponent, Collection<String> checkedNodes,
			Map<String, TreeComponent> nodeMap) {
		if (treeComponent.getComponents() != null && treeComponent.getComponents().size() > 0) {
			JSONArray array = new JSONArray();
			Iterator<?> iterator = treeComponent.getComponents().iterator();
			while (iterator.hasNext()) {
				TreeComponent component = (TreeComponent) iterator.next();
				TreeComponent node = nodeMap.get(treeComponent.getId());
				JSONObject child = new JSONObject();
				this.addDataMap(component, child);
				child.put("id", component.getId());
				child.put("code", component.getCode());
				child.put("name", component.getTitle());
				child.put("text", component.getTitle());
				child.put("checked", component.isChecked());
				child.put("icon", component.getImage());
				child.put("img", component.getImage());
				child.put("image", component.getImage());

				if (node != null) {

				}
				if (checkedNodes.contains(component.getId())) {
					child.put("checked", true);
				} else {
					child.put("checked", false);
				}
				if (component.getComponents() != null && component.getComponents().size() > 0) {
					child.put("leaf", false);
					child.put("isParent", true);
				} else {
					child.put("leaf", true);
					child.put("isParent", false);
				}
				array.add(child);
				this.buildTree(child, component, checkedNodes, nodeMap);
			}
			row.put("children", array);
		}

	}

	public TreeRepository buildTree(List<TreeComponent> treeModels) {
		Map<String, TreeComponent> treeMap = new java.util.HashMap<String, TreeComponent>();
		Map<String, TreeComponent> lockedMap = new java.util.HashMap<String, TreeComponent>();

		for (int i = 0, len = treeModels.size(); i < len; i++) {
			TreeComponent treeModel = (TreeComponent) treeModels.get(i);
			if (treeModel != null && StringUtils.equals(treeModel.getId(), treeModel.getParentId())) {
				treeModel.setParentId(null);
			}
			if (treeModel != null && treeModel.getId() != null) {
				treeMap.put(treeModel.getId(), treeModel);
			}
			if (treeModel != null && treeModel.getLocked() != 0) {
				/**
				 * 记录已经禁用的节点
				 */
				lockedMap.put(treeModel.getId(), treeModel);
			}
		}

		int lenx = treeModels.size();
		if (lenx > 500) {
			lenx = 500;
		}
		for (int i = 0; i < lenx; i++) {
			for (int j = 0, len2 = treeModels.size(); j < len2; j++) {
				TreeComponent tree = treeModels.get(j);
				/**
				 * 找到某个节点的父节点，如果被禁用，那么当前节点也设置为禁用
				 */
				if (lockedMap.get(tree.getParentId()) != null) {
					tree.setLocked(1);
				}
				TreeComponent parent = treeMap.get(tree.getParentId());
				tree.setParent(parent);
			}
		}

		TreeRepository repository = new TreeRepository();
		for (int i = 0, len = treeModels.size(); i < len; i++) {
			TreeComponent treeModel = treeModels.get(i);
			if (treeModel == null) {
				continue;
			}
			if (treeModel.getLocked() != 0) {
				continue;
			}
			if (treeModel.getParent() != null && treeModel.getParent().getLocked() != 0) {
				continue;
			}
			TreeComponent component = new TreeComponent();
			component.setId(treeModel.getId());
			component.setCode(treeModel.getId());
			component.setTitle(treeModel.getTitle());
			component.setChecked(treeModel.isChecked());
			component.setTreeObject(treeModel);
			component.setImage(treeModel.getImage());
			component.setDescription(treeModel.getDescription());
			component.setLocation(treeModel.getUrl());
			component.setUrl(treeModel.getUrl());
			component.setTreeId(treeModel.getTreeId());
			component.setCls(treeModel.getCls());
			component.setIntValue(treeModel.getIntValue());
			component.setLongValue(treeModel.getLongValue());
			component.setDoubleValue(treeModel.getDoubleValue());
			component.setDateValue(treeModel.getDateValue());
			component.setDataMap(treeModel.getDataMap());
			repository.addTree(component);
		}

		for (int i = 0, len = treeModels.size(); i < len; i++) {
			TreeComponent treeModel = treeModels.get(i);
			if (treeModel == null) {
				continue;
			}
			if (treeModel.getLocked() != 0) {
				continue;
			}
			if (treeModel.getParent() != null && treeModel.getParent().getLocked() != 0) {
				continue;
			}

			TreeComponent component = repository.getTree(treeModel.getId());
			String parentId = treeModel.getParentId();
			if (treeMap.get(parentId) != null) {
				TreeComponent parentTree = repository.getTree(parentId);
				if (parentTree == null) {
					TreeComponent parent = treeMap.get(parentId);
					parentTree = new TreeComponent();
					parentTree.setId(parent.getId());
					parentTree.setCode(parent.getId());
					parentTree.setTitle(parent.getTitle());
					parentTree.setChecked(parent.isChecked());
					parentTree.setTreeObject(parent);
					parentTree.setImage(parent.getImage());
					parentTree.setDescription(parent.getDescription());
					parentTree.setLocation(parent.getUrl());
					parentTree.setUrl(parent.getUrl());
					parentTree.setTreeId(parent.getTreeId());
					parentTree.setCls(parent.getCls());
					parentTree.setIntValue(parent.getIntValue());
					parentTree.setLongValue(parent.getLongValue());
					parentTree.setDoubleValue(parent.getDoubleValue());
					parentTree.setDateValue(parent.getDateValue());
					parentTree.setDataMap(parent.getDataMap());
					// repository.addTree(parentTree);
				}
				component.setParent(parentTree);
			}
		}
		return repository;
	}

	public void buildTreeComponent(JSONObject row, TreeComponent treeComponent) {
		if (treeComponent.getComponents() != null && treeComponent.getComponents().size() > 0) {
			JSONArray array = new JSONArray();
			Iterator<?> iterator = treeComponent.getComponents().iterator();
			while (iterator.hasNext()) {
				TreeComponent component = (TreeComponent) iterator.next();
				JSONObject child = new JSONObject();
				this.addDataMap(component, child);
				child.put("id", component.getId());
				child.put("code", component.getCode());
				child.put("text", component.getTitle());
				child.put("name", component.getTitle());
				child.put("checked", component.isChecked());
				child.put("icon", component.getImage());
				child.put("img", component.getImage());
				child.put("image", component.getImage());

				if (component.getComponents() != null && component.getComponents().size() > 0) {
					child.put("leaf", false);
					child.put("isParent", true);
					array.add(child);
					this.buildTreeComponent(child, component);
				} else {
					child.put("leaf", true);
					child.put("isParent", false);
					array.add(child);
				}
			}
			row.put("children", array);
		}
	}

	public JSONObject getJsonCheckboxNode(TreeComponent root, List<TreeComponent> trees,
			List<TreeComponent> selectedNodes) {
		Collection<String> checkedNodes = new HashSet<String>();
		if (selectedNodes != null && selectedNodes.size() > 0) {
			for (int i = 0, len = selectedNodes.size(); i < len; i++) {
				TreeComponent treeNode = (TreeComponent) selectedNodes.get(i);
				checkedNodes.add(treeNode.getId());
			}
		}

		Map<String, TreeComponent> nodeMap = new java.util.HashMap<String, TreeComponent>();
		if (trees != null && trees.size() > 0) {
			for (int i = 0, len = trees.size(); i < len; i++) {
				TreeComponent treeNode = (TreeComponent) trees.get(i);
				nodeMap.put(treeNode.getId(), treeNode);
			}
		}

		JSONObject object = new JSONObject();

		if (root != null) {
			object.put("id", root.getId());
			object.put("code", root.getId());
			object.put("name", root.getTitle());
			object.put("text", root.getTitle());
			object.put("leaf", false);
			object.put("isParent", true);
			object.put("checked", root.isChecked());

			if (checkedNodes.contains(root.getId())) {
				object.put("checked", true);
			} else {
				object.put("checked", false);
			}
		}

		JSONArray array = new JSONArray();
		if (trees != null && trees.size() > 0) {
			TreeRepository repository = this.build(trees);
			if (repository != null) {
				List<?> topTrees = repository.getTopTrees();
				if (topTrees != null && topTrees.size() > 0) {
					if (topTrees.size() == 1) {
						TreeComponent component = (TreeComponent) topTrees.get(0);
						if (StringUtils.equals(component.getId(), root.getId())) {
							this.buildTree(object, component, checkedNodes, nodeMap);
						} else {
							JSONObject child = new JSONObject();
							this.addDataMap(component, child);
							child.put("id", component.getId());
							child.put("code", component.getCode());
							child.put("name", component.getTitle());
							child.put("text", component.getTitle());
							child.put("leaf", false);
							child.put("isParent", true);
							child.put("checked", component.isChecked());
							child.put("icon", component.getImage());
							child.put("img", component.getImage());
							child.put("image", component.getImage());

							if (checkedNodes.contains(component.getId())) {
								child.put("checked", true);
							} else {
								child.put("checked", false);
							}
							array.add(child);
							object.put("children", array);
							this.buildTree(child, component, checkedNodes, nodeMap);
						}
					} else {
						for (int i = 0, len = topTrees.size(); i < len; i++) {
							TreeComponent component = (TreeComponent) topTrees.get(i);
							TreeComponent node = (TreeComponent) nodeMap.get(component.getId());
							JSONObject child = new JSONObject();
							this.addDataMap(component, child);
							child.put("id", component.getId());
							child.put("code", component.getCode());
							child.put("name", component.getTitle());
							child.put("text", component.getTitle());
							child.put("checked", component.isChecked());
							child.put("icon", component.getImage());
							child.put("img", component.getImage());
							child.put("image", component.getImage());

							if (node != null) {

							}
							if (checkedNodes.contains(component.getId())) {
								child.put("checked", true);
							} else {
								child.put("checked", false);
							}
							if (component.getComponents() != null && component.getComponents().size() > 0) {
								child.put("leaf", false);
								child.put("isParent", true);
							} else {
								child.put("leaf", true);
								child.put("isParent", false);
							}
							array.add(child);
							this.buildTree(child, component, checkedNodes, nodeMap);
						}
						object.put("children", array);
					}
				}
			}
		}

		return object;
	}

	public JSONObject getTreeJson(List<TreeComponent> treeModels) {
		return this.getTreeJson(null, treeModels);
	}

	public JSONObject getTreeJson(List<TreeComponent> treeModels, boolean showParentIfNotChildren) {
		return this.getTreeJson(null, treeModels, showParentIfNotChildren);
	}

	public JSONObject getTreeJson(TreeComponent root, List<TreeComponent> treeModels) {
		return this.getTreeJson(root, treeModels, true);
	}

	public JSONObject getTreeJson(TreeComponent root, List<TreeComponent> treeModels, boolean showParentIfNotChildren) {
		JSONObject object = new JSONObject();
		if (root != null) {
			object.put("id", root.getId());
			object.put("code", root.getId());
			object.put("name", root.getTitle());
			object.put("text", root.getTitle());
			object.put("leaf", false);
			object.put("cls", "folder");
			object.put("isParent", true);
			object.put("checked", root.isChecked());
		}

		if (treeModels != null && treeModels.size() > 0) {
			JSONArray array = new JSONArray();
			TreeRepository repository = this.build(treeModels);
			if (repository != null) {
				List<?> topTrees = repository.getTopTrees();
				if (topTrees != null && topTrees.size() > 0) {
					if (topTrees.size() == 1) {
						TreeComponent component = (TreeComponent) topTrees.get(0);
						if (root != null) {
							if (StringUtils.equals(component.getId(), root.getId())) {
								this.buildTreeComponent(object, component);
							}
						} else {
							this.addDataMap(component, object);
							object.put("id", component.getId());
							object.put("code", component.getCode());
							object.put("name", component.getTitle());
							object.put("text", component.getTitle());
							object.put("leaf", false);
							object.put("cls", "folder");
							object.put("checked", component.isChecked());
							object.put("isParent", true);
							this.buildTreeComponent(object, component);
						}
					} else {
						for (int i = 0, len = topTrees.size(); i < len; i++) {
							TreeComponent component = (TreeComponent) topTrees.get(i);
							JSONObject child = new JSONObject();
							this.addDataMap(component, child);
							child.put("id", component.getId());
							child.put("code", component.getCode());
							child.put("name", component.getTitle());
							child.put("text", component.getTitle());
							child.put("checked", component.isChecked());
							child.put("icon", component.getImage());
							child.put("img", component.getImage());
							child.put("image", component.getImage());

							if (component.getComponents() != null && component.getComponents().size() > 0) {
								child.put("leaf", false);
								child.put("cls", "folder");
								child.put("isParent", true);
								array.add(child);
								this.buildTreeComponent(child, component);
							} else {
								if (showParentIfNotChildren) {
									child.put("leaf", true);
									child.put("isParent", false);
									array.add(child);
								}
							}
						}
						object.put("children", array);
					}
				}
			}
		}

		return object;
	}

	public JSONArray getTreeJSONArray(List<TreeComponent> treeModels) {
		return this.getTreeJSONArray(treeModels, true);
	}

	public JSONArray getTreeJSONArray(List<TreeComponent> treeModels, boolean showParentIfNotChildren) {
		JSONArray result = new JSONArray();
		if (treeModels != null && treeModels.size() > 0) {
			TreeRepository repository = this.build(treeModels);
			List<?> topTrees = repository.getTopTrees();
			logger.debug("topTrees:" + (topTrees != null ? topTrees.size() : 0));
			if (topTrees != null && topTrees.size() > 0) {
				for (int i = 0, len = topTrees.size(); i < len; i++) {
					TreeComponent component = (TreeComponent) topTrees.get(i);
					JSONObject child = new JSONObject();
					this.addDataMap(component, child);

					child.put("id", component.getId());
					child.put("code", component.getCode());
					child.put("text", component.getTitle());
					child.put("name", component.getTitle());
					child.put("checked", component.isChecked());
					child.put("icon", component.getImage());
					child.put("img", component.getImage());
					child.put("image", component.getImage());

					if (component.getComponents() != null && component.getComponents().size() > 0) {
						child.put("leaf", false);
						child.put("cls", "folder");
						child.put("isParent", true);
						child.put("classes", "folder");
						result.add(child);
						this.buildTreeComponent(child, component);
					} else {
						child.put("leaf", true);
						child.put("isParent", false);
						if (showParentIfNotChildren) {
							result.add(child);
						}
					}
				}
			}
		}

		return result;
	}

}
