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

package com.glaf.matrix.sync.task;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.glaf.core.config.DatabaseConnectionConfig;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.Database;
import com.glaf.core.domain.SysDataLog;
import com.glaf.core.factory.SysLogFactory;
import com.glaf.core.security.Authentication;
import com.glaf.core.util.DateUtils;
import com.glaf.matrix.data.domain.ExecutionLog;
import com.glaf.matrix.data.service.ExecutionLogService;
import com.glaf.matrix.handler.ExecutionHandlerFactory;
import com.glaf.matrix.sync.bean.SqlToTableSyncBean;

public class SyncAppTask extends RecursiveTask<Boolean> {
	public final static Log logger = LogFactory.getLog(SyncAppTask.class);

	private static final long serialVersionUID = 1L;

	protected long srcDatabaseId;

	protected long targetDatabaseId;

	protected long syncId;

	protected String jobNo;

	protected Map<String, Object> parameter;

	public SyncAppTask(long srcDatabaseId, long targetDatabaseId, long syncId, String jobNo,
			Map<String, Object> parameter) {
		this.srcDatabaseId = srcDatabaseId;
		this.targetDatabaseId = targetDatabaseId;
		this.syncId = syncId;
		this.jobNo = jobNo;
		this.parameter = parameter;
	}

	@Override
	protected Boolean compute() {
		int retry = 0;
		boolean success = false;
		while (retry < 3 && !success) {
			try {
				retry++;
				success = this.execute();
				if (success) {
					return success;
				}
			} catch (Exception ex) {
				logger.error(ex);
				try {
					TimeUnit.MILLISECONDS.sleep(200 + new Random().nextInt(1000));
				} catch (InterruptedException e) {
				}
			}
		}
		return success;
	}

	public boolean execute() {
		long start = System.currentTimeMillis();
		ExecutionLog log = new ExecutionLog();
		log.setStartTime(new Date());
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("srcDatabaseId", srcDatabaseId);
		context.put("targetDatabaseId", targetDatabaseId);
		context.put("syncId", syncId);
		SqlToTableSyncBean bean = new SqlToTableSyncBean();
		boolean result = false;
		try {
			ExecutionHandlerFactory.doBefore("sync_app_" + syncId, context);
			bean.execute(srcDatabaseId, targetDatabaseId, syncId);
			result = true;
		} catch (Exception ex) {
			result = false;
			logger.error(ex);
			log.setExitMessage(ex.getMessage());
		} finally {
			if (result) {
				context.put("result", result);
				ExecutionHandlerFactory.doAfter("sync_app_" + syncId, context);
			}
		}
		InetAddress addr = null;
		String ipAddr = null;
		try {
			addr = InetAddress.getLocalHost();
			ipAddr = addr.getHostAddress().toString();
		} catch (UnknownHostException e) {
		}
		JSONObject json = new JSONObject();
		json.put("srcDatabaseId", srcDatabaseId);
		json.put("targetDatabaseId", targetDatabaseId);
		json.put("syncId", syncId);
		json.put("result", result);
		if (result) {
			json.put("message", "执行成功");
		} else {
			json.put("message", "执行失败");
		}

		try {
			DatabaseConnectionConfig cfg = new DatabaseConnectionConfig();
			Database database = cfg.getDatabase(targetDatabaseId);
			if (database != null) {
				json.put("title", database.getTitle());
				json.put("section", database.getSection());
				json.put("db", database.getDbname());
			}
		} catch (Exception ex) {
		}

		long ts = System.currentTimeMillis() - start;
		SysDataLog sysLog = new SysDataLog();
		sysLog.setIp(ipAddr);
		sysLog.setActorId("system");
		sysLog.setContent(json.toJSONString());
		sysLog.setTimeMS((int) ts);
		sysLog.setCreateTime(new Date());
		sysLog.setFlag(result ? 1 : -1);
		sysLog.setServiceKey("sync_app");
		sysLog.setModuleId("sync_app_" + syncId);
		sysLog.setBusinessKey("sync_app_" + syncId);
		SysLogFactory.getInstance().addLog(sysLog);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		log.setBusinessKey(String.valueOf(syncId));
		log.setContent(json.toJSONString());
		// log.setCreateBy("system");
		if (Authentication.getAuthenticatedActorId() != null) {
			log.setCreateBy(Authentication.getAuthenticatedActorId());
		} else {
			log.setCreateBy("system");
		}
		log.setEndTime(new Date());
		log.setRunTime(ts);
		log.setRunHour(calendar.get(Calendar.HOUR));
		log.setRunDay(DateUtils.getNowYearMonthDay());
		log.setJobNo(jobNo);
		log.setStatus(result ? 1 : -1);
		log.setType("sync_app");
		try {
			ExecutionLogService executionLogService = ContextFactory.getBean("executionLogService");
			executionLogService.save(log);
		} catch (Exception ex) {
		}

		return result;
	}

}
