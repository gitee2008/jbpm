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

package com.glaf.matrix.adjust.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.job.BaseJob;
import com.glaf.core.security.Authentication;
import com.glaf.core.util.DateUtils;

import com.glaf.matrix.adjust.bean.TreeTableAggregateBean;
import com.glaf.matrix.adjust.domain.TreeDataAdjust;
import com.glaf.matrix.adjust.query.TreeDataAdjustQuery;
import com.glaf.matrix.adjust.service.TreeDataAdjustService;
import com.glaf.matrix.data.domain.ExecutionLog;
import com.glaf.matrix.data.service.ExecutionLogService;

public class TreeTableAggregateJob extends BaseJob {

	protected static AtomicLong lastExecuteTime = new AtomicLong(System.currentTimeMillis());

	protected static int F_STEP = 0;

	@Override
	public void runJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug("----------------------TreeTableAggregateJob--------------------");
		if (F_STEP > 0) {
			if ((System.currentTimeMillis() - lastExecuteTime.get()) < DateUtils.SECOND * 60) {
				logger.info("间隔时间未到，不执行。");
				return;
			}
		}
		try {
			TimeUnit.SECONDS.sleep(1 + new Random().nextInt(20));// 随机等待，避免Job同时执行
		} catch (InterruptedException e) {
		}
		F_STEP++;

		ExecutionLogService executionLogService = ContextFactory.getBean("executionLogService");
		TreeDataAdjustService treeDataAdjustService = ContextFactory
				.getBean("com.glaf.matrix.adjust.service.treeDataAdjustService");
		TreeDataAdjustQuery query = new TreeDataAdjustQuery();
		query.adjustType("treeAggregate");
		query.scheduleFlag("Y");
		query.locked(0);
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			List<TreeDataAdjust> list = treeDataAdjustService.list(query);
			if (list != null && !list.isEmpty()) {
				TreeTableAggregateBean bean = new TreeTableAggregateBean();
				for (TreeDataAdjust model : list) {
					String content = "执行树表逐级汇总[" + model.getTitle() + "]";
					logger.debug("准备执行树表逐级汇总调度:" + model.getTitle());
					ExecutionLog executionLog = new ExecutionLog();
					long start = System.currentTimeMillis();
					boolean ret = false;
					try {
						executionLog.setBusinessKey("tree_adjust_" + model.getId());
						if (Authentication.getAuthenticatedActorId() != null) {
							executionLog.setCreateBy(Authentication.getAuthenticatedActorId());
						} else {
							executionLog.setCreateBy("system");
						}
						executionLog.setCreateTime(new Date());
						executionLog.setStartTime(new Date());
						executionLog.setType("tree_adjust");
						executionLog.setTitle(model.getTitle());
						bean.execute(model.getDatabaseId(), model.getId(), params);
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
						ex.printStackTrace();
						logger.error(ex);
					}
				}
			}
		} finally {
			lastExecuteTime.set(System.currentTimeMillis());
		}
	}

}
