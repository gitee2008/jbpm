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
package com.glaf.matrix.export.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.ServerEntity;
import com.glaf.core.service.IServerEntityService;
import com.glaf.jxls.ext.JxlsImage;
import com.glaf.jxls.ext.JxlsUtil;
import com.glaf.matrix.export.bean.ExportChartTask;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportChart;
import com.glaf.matrix.export.query.ExportChartQuery;
import com.glaf.matrix.export.service.ExportChartService;

public class ExportChartFactory {

	protected final static Logger logger = LoggerFactory.getLogger(ExportChartFactory.class);

	private ExportChartFactory() {

	}

	public static void snapshot(ExportApp exportApp, Map<String, Object> parameter) {
		IServerEntityService serverEntityService = ContextFactory.getBean("serverEntityService");
		ExportChartService exportChartService = ContextFactory
				.getBean("com.glaf.matrix.export.service.exportChartService");
		ServerEntity serverEntity = serverEntityService.getServerEntityByMapping("snapshot");
		if (serverEntity != null) {
			ExportChartQuery query = new ExportChartQuery();
			query.expId(exportApp.getId());
			query.locked(0);
			List<ExportChart> charts = exportChartService.list(query);
			if (charts != null && !charts.isEmpty()) {
				Map<String, Object> imgMap = new HashMap<String, Object>();
				ForkJoinPool pool = ForkJoinPool.commonPool();
				logger.info("准备执行并行任务...");
				int len = charts.size();
				logger.info("并行任务数:" + len);
				try {
					for (ExportChart chart : charts) {
						ExportChartTask task = new ExportChartTask(serverEntity, chart, parameter);
						Future<byte[]> result = pool.submit(task);
						if (result != null && result.get() != null) {
							byte[] bytes = result.get();
							String filename = chart.getName() + "." + chart.getImageType();
							JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(bytes, filename);
							imgMap.put(chart.getName(), jxlsImage);
							imgMap.put(chart.getName() + "_img", jxlsImage);
							logger.debug(chart.getTitle() + "图像处理成功.");
						}
					}
					// 线程阻塞，等待所有任务完成
					try {
						pool.awaitTermination(50, TimeUnit.MILLISECONDS);
					} catch (InterruptedException ex) {
					}
				} catch (java.lang.Throwable ex) {
					logger.error("process chart list error", ex);
				} finally {
					pool.shutdown();
					logger.info("并行任务已经结束。");
				}
				if (imgMap.size() > 0) {
					logger.debug("image keys"+imgMap.keySet());
					parameter.putAll(imgMap);
					parameter.put("_useExt_", "Y");
				}
			}
		}
	}
}
