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

package com.glaf.matrix.cycle.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.job.BaseJob;
import com.glaf.core.util.DateUtils;

import com.glaf.matrix.cycle.bean.LoopSqlToTableBatchBean;
import com.glaf.matrix.cycle.bean.LoopSqlToTableBean;
import com.glaf.matrix.cycle.domain.LoopSqlToTable;
import com.glaf.matrix.cycle.query.LoopSqlToTableQuery;
import com.glaf.matrix.cycle.service.LoopSqlToTableService;
import com.glaf.matrix.data.domain.ExecutionLog;
import com.glaf.matrix.data.service.ExecutionLogService;

public class LoopSqlToTableJob extends BaseJob {

	protected static AtomicLong lastExecuteTime = new AtomicLong(System.currentTimeMillis());

	protected static int F_STEP = 0;

	@Override
	public void runJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug("----------------------LoopSqlToTableJob--------------------");
		if (F_STEP > 0) {
			if ((System.currentTimeMillis() - lastExecuteTime.get()) < DateUtils.SECOND * 300) {
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
		LoopSqlToTableService loopSqlToTableService = ContextFactory
				.getBean("com.glaf.matrix.cycle.service.loopSqlToTableService");
		LoopSqlToTableQuery query = new LoopSqlToTableQuery();
		query.scheduleFlag("Y");
		query.locked(0);
		Map<String, Object> params = new HashMap<String, Object>();
		List<LoopSqlToTable> list = loopSqlToTableService.list(query);
		if (list != null && !list.isEmpty()) {
			LoopSqlToTableBatchBean batchBean = new LoopSqlToTableBatchBean();
			LoopSqlToTableBean bean = new LoopSqlToTableBean();
			for (LoopSqlToTable loopSqlToTable : list) {
				try {
					String content = "执行循环SQL到表[" + loopSqlToTable.getTitle() + "]";
					ExecutionLog executionLog = new ExecutionLog();
					executionLog.setBusinessKey("loop_sql_table_" + loopSqlToTable.getId());
					executionLog.setCreateBy("system");
					executionLog.setCreateTime(new Date());
					executionLog.setStartTime(new Date());
					executionLog.setType("loop_sql_table");
					executionLog.setTitle(loopSqlToTable.getTitle());
					long start = System.currentTimeMillis();
					boolean ret = false;
					if (StringUtils.equals(loopSqlToTable.getBatchFlag(), "Y")) {
						ret = batchBean.execute(loopSqlToTable.getId(), params);
					} else {
						ret = bean.execute(loopSqlToTable.getId(), params);
					}
					if (ret) {
						loopSqlToTable.setSyncStatus(9);
						executionLog.setStatus(9);
						content = content + " 执行成功！";
					} else {
						loopSqlToTable.setSyncStatus(-1);
						executionLog.setStatus(-1);
						content = content + " 执行失败！";
					}
					loopSqlToTable.setSyncTime(new Date());
					loopSqlToTableService.updateLoopSqlToTableSyncStatus(loopSqlToTable);
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
		lastExecuteTime.set(System.currentTimeMillis());
	}

}
