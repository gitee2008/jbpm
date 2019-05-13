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

package com.glaf.matrix.sync.handler;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.Database;
import com.glaf.core.security.Authentication;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.StringTools;

import com.glaf.matrix.handler.DataExecutionHandler;
import com.glaf.matrix.sync.domain.SyncApp;
import com.glaf.matrix.sync.domain.SyncHistory;
import com.glaf.matrix.sync.service.SyncAppService;
import com.glaf.matrix.sync.service.SyncHistoryService;
import com.glaf.matrix.sync.task.SyncAppTask;

public class SqlToTableSyncHandler implements DataExecutionHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public void execute(Object id, Map<String, Object> context) {
		long syncId = Long.parseLong(id.toString());
		IDatabaseService databaseService = ContextFactory.getBean("databaseService");
		SyncAppService syncAppService = ContextFactory.getBean("com.glaf.matrix.data.sync.service.syncAppService");
		SyncHistoryService syncHistoryService = ContextFactory
				.getBean("com.glaf.matrix.data.sync.service.syncHistoryService");
		SyncApp syncApp = syncAppService.getSyncApp(syncId);
		if (syncApp == null || !StringUtils.equals(syncApp.getActive(), "Y")) {
			return;
		}
		List<Long> databaseIds = StringTools.splitToLong(syncApp.getTargetDatabaseIds());
		if (databaseIds != null && !databaseIds.isEmpty()) {
			String jobNo = null;
			Database database = null;
			SyncAppTask task = null;
			int runDay = DateUtils.getNowYearMonthDay();
			for (long targetDatabaseId : databaseIds) {
				jobNo = "sync_app_" + syncApp.getId() + "_" + targetDatabaseId + "_" + runDay;
				try {
					long start = System.currentTimeMillis();

					database = databaseService.getDatabaseById(targetDatabaseId);
					if (database != null) {
						task = new SyncAppTask(syncApp.getSrcDatabaseId(), targetDatabaseId, syncId, jobNo, context);
						SyncHistory his = new SyncHistory();
						if (Authentication.getAuthenticatedActorId() != null) {
							his.setCreateBy(Authentication.getAuthenticatedActorId());
						} else {
							his.setCreateBy("system");
						}
						his.setDatabaseId(targetDatabaseId);
						his.setDatabaseName(database.getTitle() + "[" + database.getHost() + "]");
						his.setDeploymentId(syncApp.getDeploymentId());
						his.setSyncId(syncApp.getId());
						if (!task.execute()) {
							his.setStatus(-1);
						} else {
							his.setStatus(1);
						}
						his.setTotalTime((int) (System.currentTimeMillis() - start));

						syncHistoryService.save(his);
					}
				} catch (Exception ex) {
				}
			}
		}
	}

}
