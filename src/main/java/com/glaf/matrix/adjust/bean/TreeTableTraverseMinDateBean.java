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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.tree.component.TreeComponent;
import com.glaf.core.util.DateUtils;
import com.glaf.matrix.adjust.domain.TreeDataAdjust;

public class TreeTableTraverseMinDateBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 递归每个节点及所有子孙节点，找出比adjustTime更小的值
	 * 
	 * @param component
	 * @param adjustTime
	 * @return
	 */
	public long getChildrenMinValue(TreeComponent component, String index_id, long adjustTime) {
		long minTime = adjustTime;
		List<TreeComponent> children = component.getComponents();
		if (children != null && children.size() > 0) {
			if (children.size() == 1) {
				minTime = this.getChildrenMinValue(children.get(0), index_id, minTime);// 递归子节点
			} else {
				for (TreeComponent child : children) {
					if (!StringUtils.equals(index_id, child.getId())) {
						if (child.getLongValue() > 0) {
							if (child.getLongValue() < minTime) {
								minTime = child.getLongValue();// 取更新的时间
								logger.debug("取得更小的日期:" + child.getDataMap());
							}
						}
						minTime = this.getChildrenMinValue(child, index_id, minTime);// 递归子节点
					}
				}
				int countChild = 0;
				for (TreeComponent child : children) {
					if (child.getLongValue() > 0) {
						countChild++;
					}
				}
				if (countChild == 1) {
					minTime = adjustTime;
				}
			}
		}
		return minTime;
	}

	/**
	 * 循环parent所有子节点，找出比adjustTime更小的值
	 * 
	 * @param parent
	 * @param adjustTime
	 * @return
	 */
	public long getParentMinValue(TreeComponent parent, String index_id, long adjustTime) {
		logger.debug("adjustTime:" + DateUtils.getDate(DateUtils.toDate(adjustTime)));
		long minTime = adjustTime;
		List<TreeComponent> children = parent.getComponents();
		if (children != null && children.size() > 0) {
			for (TreeComponent child : children) {
				// logger.debug("ts:" + child.getLongValue());
				if (!StringUtils.equals(index_id, child.getId())) {
					if (child.getLongValue() > 0) {
						if (child.getLongValue() < minTime) {
							minTime = child.getLongValue();// 取更新的时间
							// logger.debug("取得更小的日期:" + child.getDataMap());
						}
					}
				}
			}
			int countChild = 0;
			for (TreeComponent child : children) {
				if (child.getLongValue() > 0) {
					countChild++;
				}
			}
			if (countChild == 1) {
				minTime = adjustTime;
			}
		}
		return minTime;
	}

	/**
	 * 循环parent所有子节点，找出比adjustTime更小的值
	 * 
	 * @param parent
	 * @param adjustTime
	 * @return
	 */
	public long getParentMinValue(TreeComponent parent, long adjustTime) {
		long minTime = adjustTime;
		List<TreeComponent> children = parent.getComponents();
		if (children != null && children.size() > 0) {
			for (TreeComponent child : children) {
				if (child.getLongValue() > 0) {
					if (child.getLongValue() < minTime) {
						minTime = child.getLongValue();
						// logger.debug("取得更小的日期:" + child.getDataMap());
					}
				}
			}
		}
		return minTime;
	}

	public void populate(TreeDataAdjust dataAdjust, List<TreeComponent> components,
			List<Map<String, Object>> updateDataList, String index_id, long adjustTime) {
		final long fTime = adjustTime;
		for (TreeComponent component : components) {
			if (StringUtils.equals(index_id, component.getId())) {
				if (StringUtils.equals(dataAdjust.getLeafLimitFlag(), "Y")) {
					List<TreeComponent> children = component.getComponents();
					if (children != null && !children.isEmpty()) {
						return;// 不是叶节点不处理
					}
				}
				String dateColumn = dataAdjust.getAdjustColumn().toLowerCase();
				component.getDataMap().put(dateColumn, new Date(adjustTime));// 调整选中节点自己的日期
				component.setLongValue(adjustTime);
				updateDataList.add(component.getDataMap());// 加入修改对象

				TreeComponent parent = component.getParent();
				if (parent != null) {// 如果有父节点，继续处理父节点
					long minTime = this.getParentMinValue(parent, adjustTime);// 找同层节点中最小日期作为调整日期
					if (minTime <= adjustTime) {
						adjustTime = minTime;
					}
					long tmpTime = this.getParentMinValue(parent, index_id, adjustTime);// 找出父节点的下级，即本节点同级的最小时间
					logger.debug("tmpTime:" + DateUtils.getDate(DateUtils.toDate(tmpTime)));
					if (tmpTime >= adjustTime) {
						logger.debug("继续递归父节点...");
						this.populateParent(dataAdjust, parent, updateDataList, index_id, adjustTime);
					} else {
						List<TreeComponent> children = parent.getComponents();
						if (children != null && children.size() > 0) {
							if (children.size() == 1) {// 唯一子节点，父节点日期修改成子节点的日期
								parent.getDataMap().put(dateColumn, new Date(adjustTime));// 调整选中节点父节点的日期
								parent.setLongValue(adjustTime);
								updateDataList.add(parent.getDataMap());// 加入修改对象
							} else {
								int countChild = 0;
								for (TreeComponent child : children) {
									if (child.getLongValue() > 0) {
										countChild++;
									}
								}
								if (countChild == 1) {// 唯一有数据的子节点
									parent.getDataMap().put(dateColumn, new Date(adjustTime));// 调整选中节点父节点的日期
									parent.setLongValue(adjustTime);
									updateDataList.add(parent.getDataMap());// 加入修改对象
								}
							}
						}
					}
				}

				/**
				 * 处理当前节点的唯一子节点的情况
				 */
				this.populateChilden(dataAdjust, component, updateDataList, fTime);

			} else {
				List<TreeComponent> children = component.getComponents();
				if (children != null && children.size() > 0) {
					/**
					 * 递归查找，寻找选中的节点
					 */
					this.populate(dataAdjust, children, updateDataList, index_id, fTime);
				}
			}
		}
	}

	/**
	 * 
	 * 处理当前节点的唯一子节点的情况
	 */
	public void populateChilden(TreeDataAdjust dataAdjust, TreeComponent component,
			List<Map<String, Object>> updateDataList, long adjustTime) {
		List<TreeComponent> children = component.getComponents();
		int countChild = 0;
		TreeComponent tmpNode = null;
		if (children != null && children.size() > 0) {
			for (TreeComponent child : children) {
				if (child.getLongValue() > 0) {
					countChild++;
					tmpNode = child;
				}
			}
		}
		if (countChild == 1) {
			TreeComponent child = tmpNode;// 唯一下级
			String dateColumn = dataAdjust.getAdjustColumn().toLowerCase();
			child.getDataMap().put(dateColumn, new Date(adjustTime));// 调整选中节点自己的日期
			child.setLongValue(adjustTime);
			updateDataList.add(child.getDataMap());// 加入修改对象
			this.populateChilden(dataAdjust, child, updateDataList, adjustTime);
		}
	}

	public void populateParent(TreeDataAdjust dataAdjust, TreeComponent parent,
			List<Map<String, Object>> updateDataList, String index_id, long adjustTime) {
		if (parent == null) {
			return;
		}
		long minTime = this.getChildrenMinValue(parent, index_id, adjustTime);// 取得parent节点所有子节点最小的日期值
		logger.debug(" parent minTime:" + DateUtils.getDate(DateUtils.toDate(minTime)));
		if (minTime < adjustTime) {// 如果其他节点的日期比调整日期小，保持不变
			return;
		}
		String dateColumn = dataAdjust.getAdjustColumn().toLowerCase();
		parent.setLongValue(adjustTime);
		parent.getDataMap().put(dateColumn, new Date(adjustTime));// 调整选中节点自己的日期
		updateDataList.add(parent.getDataMap());// 加入修改对象
		if (parent.getParent() != null) {
			this.populateParent(dataAdjust, parent.getParent(), updateDataList, index_id, adjustTime);// 再次向上找父节点
		}
	}

}
