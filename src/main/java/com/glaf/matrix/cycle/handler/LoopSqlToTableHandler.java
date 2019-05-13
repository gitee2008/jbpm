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

package com.glaf.matrix.cycle.handler;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.security.Authentication;

import com.glaf.matrix.cycle.bean.LoopSqlToTableBatchBean;
import com.glaf.matrix.cycle.bean.LoopSqlToTableBean;
import com.glaf.matrix.cycle.domain.LoopSqlToTable;
import com.glaf.matrix.cycle.service.LoopSqlToTableService;
import com.glaf.matrix.data.domain.ExecutionLog;
import com.glaf.matrix.data.service.ExecutionLogService;
import com.glaf.matrix.handler.DataExecutionHandler;

public class LoopSqlToTableHandler implements DataExecutionHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public void execute(Object id, Map<String, Object> context) {
		ExecutionLogService executionLogService = ContextFactory.getBean("executionLogService");
		LoopSqlToTableService loopSqlToTableService = ContextFactory
				.getBean("com.glaf.matrix.cycle.service.loopSqlToTableService");
		LoopSqlToTable model = loopSqlToTableService.getLoopSqlToTable(id.toString());
		LoopSqlToTableBatchBean batchBean = new LoopSqlToTableBatchBean();
		LoopSqlToTableBean bean = new LoopSqlToTableBean();
		logger.debug("准备执行调度:" + model.getTitle());
		try {
			String content = "执行循环SQL到表[" + model.getTitle() + "]";
			ExecutionLog executionLog = new ExecutionLog();
			executionLog.setBusinessKey("loop_sql_to_table_" + model.getId());
			if (Authentication.getAuthenticatedActorId() != null) {
				executionLog.setCreateBy(Authentication.getAuthenticatedActorId());
			} else {
				executionLog.setCreateBy("system");
			}
			executionLog.setCreateTime(new Date());
			executionLog.setStartTime(new Date());
			executionLog.setType("loop_sql_to_table");
			executionLog.setTitle(model.getTitle());
			long start = System.currentTimeMillis();
			boolean ret = false;
			if (StringUtils.equals(model.getBatchFlag(), "Y")) {
				ret = batchBean.execute(model.getId(), context);
			} else {
				ret = bean.execute(model.getId(), context);
			}
			if (ret) {
				model.setSyncStatus(9);
				executionLog.setStatus(9);
				content = content + " 执行成功！";
			} else {
				model.setSyncStatus(-1);
				executionLog.setStatus(-1);
				content = content + " 执行失败！";
			}
			model.setSyncTime(new Date());
			loopSqlToTableService.updateLoopSqlToTableSyncStatus(model);
			executionLog.setRunTime(System.currentTimeMillis() - start);
			executionLog.setEndTime(new Date());
			executionLog.setContent(content);
			executionLogService.save(executionLog);
		} catch (Exception ex) {
			logger.error("loop sql table execute error", ex);
		}
	}

}
