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

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.glaf.core.context.ContextFactory;
import com.glaf.core.util.ReflectUtils;
import com.glaf.matrix.combination.handler.TreeTableAggregateHandler;
import com.glaf.matrix.config.HandlerProperties;
import com.glaf.matrix.export.domain.ExportPreprocessor;
import com.glaf.matrix.export.query.ExportPreprocessorQuery;
import com.glaf.matrix.export.service.ExportPreprocessorService;
import com.glaf.matrix.handler.DataExecutionHandler;
import com.glaf.matrix.transform.handler.TableTransformHandler;

public class PreprocessorFactory {

	protected static ConcurrentMap<String, DataExecutionHandler> handlerMap = new ConcurrentHashMap<String, DataExecutionHandler>();

	protected static final Log logger = LogFactory.getLog(PreprocessorFactory.class);

	protected static volatile ExportPreprocessorService exportPreprocessorService;

	static {

		handlerMap.put("table_transform", new TableTransformHandler());
		handlerMap.put("treetable_aggregate", new TreeTableAggregateHandler());

		try {
			Properties props = HandlerProperties.getProperties();
			if (props != null) {
				Enumeration<?> e = props.keys();
				while (e.hasMoreElements()) {
					String key = (String) e.nextElement();
					String value = props.getProperty(key);
					DataExecutionHandler handler = (DataExecutionHandler) ReflectUtils.instantiate(value);
					handlerMap.put(key, handler);
				}
			}
		} catch (java.lang.Throwable ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 前置处理
	 * 
	 * @param expId
	 * @param context
	 */
	public static void doBefore(String expId, Map<String, Object> context) {
		if (expId != null) {
			String currentStep = "export_" + expId;
			ExportPreprocessorQuery query = new ExportPreprocessorQuery();
			query.expId(expId);
			query.currentStep(currentStep);
			List<ExportPreprocessor> preprocessors = getExportPreprocessorService().list(query);
			if (preprocessors != null && !preprocessors.isEmpty()) {
				for (ExportPreprocessor preprocessor : preprocessors) {
					if (StringUtils.equals(preprocessor.getCurrentType(), preprocessor.getPreviousType())) {
						DataExecutionHandler handler = handlerMap.get(preprocessor.getPreviousType());
						if (handler != null) {
							logger.debug("###############################doBefore############################");
							logger.debug("handler:" + handler.getClass().getName());
							context.put("expId", expId);
							context.put("_flow_", preprocessor);
							context.put("_preprocessor_", preprocessor);

							int retry = 0;
							int errorCount = 0;
							boolean success = false;
							Exception e = null;
							while (retry < 3 && !success) {
								try {
									retry++;
									handler.execute(preprocessor.getPreviousStep(), context);
									success = true;
								} catch (Exception ex) {
									e = ex;
									errorCount++;
								}
							}
							if (!success && errorCount > 0) {
								throw new RuntimeException(e);
							}
						}
					}
				}
			}
		}
	}

	public static ExportPreprocessorService getExportPreprocessorService() {
		if (exportPreprocessorService == null) {
			exportPreprocessorService = ContextFactory.getBean("exportPreprocessorService");
		}
		return exportPreprocessorService;
	}

	private PreprocessorFactory() {

	}

}
