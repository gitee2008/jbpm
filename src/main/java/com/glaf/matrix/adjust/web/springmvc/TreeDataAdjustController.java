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

package com.glaf.matrix.adjust.web.springmvc;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.glaf.core.security.Authentication;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;

import com.glaf.matrix.adjust.bean.TreeComponentAdjustBean;
import com.glaf.matrix.adjust.bean.TreeComponentNameChainBean;
import com.glaf.matrix.adjust.bean.TreeTableAggregateBean;
import com.glaf.matrix.adjust.domain.TreeDataAdjust;
import com.glaf.matrix.adjust.service.TreeDataAdjustService;
import com.glaf.matrix.data.domain.ExecutionLog;
import com.glaf.matrix.data.service.ExecutionLogService;

/**
 * 
 * SpringMVC控制器
 *
 */

@Controller("/matrix/treeDataAdjust")
@RequestMapping("/matrix/treeDataAdjust")
public class TreeDataAdjustController {
	protected static final Log logger = LogFactory.getLog(TreeDataAdjustController.class);

	protected static ConcurrentMap<String, String> concurrentMap = new ConcurrentHashMap<String, String>();

	protected static ConcurrentMap<String, Long> concurrentTimeMap = new ConcurrentHashMap<String, Long>();

	protected IDatabaseService databaseService;

	protected ExecutionLogService executionLogService;

	protected TreeDataAdjustService treeDataAdjustService;

	public TreeDataAdjustController() {

	}

	@ResponseBody
	@RequestMapping("/execute")
	public byte[] execute(HttpServletRequest request) {
		String adjustId = RequestUtils.getString(request, "adjustId");
		long databaseId = RequestUtils.getLong(request, "databaseId");
		Map<String, Object> parameter = RequestUtils.getParameterMap(request);
		logger.debug("parameter:" + parameter);
		if (StringUtils.isNotEmpty(adjustId)) {
			Long ts = concurrentTimeMap.get(adjustId);
			if (concurrentMap.get(adjustId) == null
					|| (ts != null && (System.currentTimeMillis() - ts > DateUtils.SECOND * 120))) {
				try {
					concurrentMap.put(adjustId, adjustId);
					concurrentTimeMap.put(adjustId, System.currentTimeMillis());
					TreeDataAdjust dataAdjust = treeDataAdjustService.getTreeDataAdjust(adjustId);
					if (dataAdjust != null && dataAdjust.getLocked() == 0) {
						if (databaseId == 0) {
							databaseId = dataAdjust.getDatabaseId();
						}
						logger.debug("dataAdjust:" + dataAdjust.toJsonObject().toJSONString());
						if (StringUtils.equals(dataAdjust.getAdjustType(), "dateLT")
								|| StringUtils.equals(dataAdjust.getAdjustType(), "dateGT")) {
							String index_id = RequestUtils.getString(request, "index_id");
							Date adjustDate = RequestUtils.getDate(request, "adjustDate");
							if (StringUtils.isNotEmpty(index_id) && adjustDate != null) {
								TreeComponentAdjustBean bean = new TreeComponentAdjustBean();
								bean.execute(databaseId, adjustId, index_id, adjustDate, parameter);
								return ResponseUtils.responseJsonResult(true);
							}
						} else if (StringUtils.equals(dataAdjust.getAdjustType(), "nameChain")) {
							TreeComponentNameChainBean bean = new TreeComponentNameChainBean();
							// bean.execute(databaseId, adjustId, parameter);
							String content = "执行树表调整[" + dataAdjust.getTitle() + "]";
							ExecutionLog executionLog = new ExecutionLog();
							long start = System.currentTimeMillis();
							boolean ret = false;
							try {
								executionLog.setBusinessKey("tree_adjust_" + dataAdjust.getId());
								if (Authentication.getAuthenticatedActorId() != null) {
									executionLog.setCreateBy(Authentication.getAuthenticatedActorId());
								} else {
									executionLog.setCreateBy("system");
								}
								executionLog.setCreateTime(new Date());
								executionLog.setStartTime(new Date());
								executionLog.setType("tree_adjust");
								executionLog.setTitle(dataAdjust.getTitle());
								bean.execute(databaseId, dataAdjust.getId(), parameter);
								ret = true;
							} catch (Exception ex) {
								ex.printStackTrace();
								logger.error(ex);
								ret = false;
							}
							try {
								if (ret) {
									executionLog.setStatus(9);
									content = content + " 执行成功！";
								} else {
									executionLog.setStatus(-1);
									content = content + " 执行失败！";
								}
								executionLog.setRunTime(System.currentTimeMillis() - start);
								executionLog.setEndTime(new Date());
								executionLog.setContent(content);
								executionLogService.save(executionLog);
							} catch (Exception ex) {
								// ex.printStackTrace();
								logger.error(ex);
							}
							return ResponseUtils.responseJsonResult(ret);

						} else if (StringUtils.equals(dataAdjust.getAdjustType(), "treeAggregate")) {
							TreeTableAggregateBean bean = new TreeTableAggregateBean();
							// bean.execute(databaseId, adjustId, parameter);
							String content = "执行树表逐级汇总[" + dataAdjust.getTitle() + "]";
							logger.debug(content);
							ExecutionLog executionLog = new ExecutionLog();
							long start = System.currentTimeMillis();
							boolean ret = false;
							try {
								executionLog.setBusinessKey("tree_adjust_" + dataAdjust.getId());
								if (Authentication.getAuthenticatedActorId() != null) {
									executionLog.setCreateBy(Authentication.getAuthenticatedActorId());
								} else {
									executionLog.setCreateBy("system");
								}
								executionLog.setCreateTime(new Date());
								executionLog.setStartTime(new Date());
								executionLog.setType("tree_adjust");
								executionLog.setTitle(dataAdjust.getTitle());
								bean.execute(databaseId, dataAdjust.getId(), parameter);
								ret = true;
							} catch (Exception ex) {
								ex.printStackTrace();
								logger.error(ex);
								ret = false;
							}
							try {
								if (ret) {
									executionLog.setStatus(9);
									content = content + " 执行成功！";
								} else {
									executionLog.setStatus(-1);
									content = content + " 执行失败！";
								}
								executionLog.setRunTime(System.currentTimeMillis() - start);
								executionLog.setEndTime(new Date());
								executionLog.setContent(content);
								executionLogService.save(executionLog);
							} catch (Exception ex) {
								// ex.printStackTrace();
								logger.error(ex);
							}
							return ResponseUtils.responseJsonResult(ret);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error(ex);
				} finally {
					concurrentMap.remove(adjustId);
					concurrentTimeMap.remove(adjustId);
				}
			} else {
				return ResponseUtils.responseJsonResult(false, "已经有任务在执行中，请等待完成！");
			}
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource
	public void setDatabaseService(IDatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	@javax.annotation.Resource
	public void setExecutionLogService(ExecutionLogService executionLogService) {
		this.executionLogService = executionLogService;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.adjust.service.treeDataAdjustService")
	public void setTreeDataAdjustService(TreeDataAdjustService treeDataAdjustService) {
		this.treeDataAdjustService = treeDataAdjustService;
	}

}
