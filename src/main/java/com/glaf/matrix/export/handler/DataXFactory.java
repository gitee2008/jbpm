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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.util.StringTools;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportItem;

public class DataXFactory {
	protected static final Logger logger = LoggerFactory.getLogger(DataXFactory.class);

	protected static ConcurrentMap<String, DataXPreprocessor> preprocessorMap = new ConcurrentHashMap<String, DataXPreprocessor>();

	protected static ConcurrentMap<String, String> nameMap = new ConcurrentHashMap<String, String>();

	static {
		preprocessorMap.put("aggregate", new AggregateDataPreprocessor());
		preprocessorMap.put("paging", new PagingDataPreprocessor());
		preprocessorMap.put("image", new ImageDataPreprocessor());
		nameMap.put("aggregate", "数据分组分页汇总预处理器");
		nameMap.put("paging", "数据分页预处理器");
		nameMap.put("image", "图像数据预处理器");
	}

	public static ConcurrentMap<String, String> getNameMap() {
		return nameMap;
	}

	public static void preprocess(Map<String, Object> parameter, ExportApp exportApp) {
		for (ExportItem item : exportApp.getItems()) {
			String handlerChains = item.getDataHandlerChains();
			if (StringUtils.isNotEmpty(handlerChains)) {
				List<String> handlers = new ArrayList<String>();
				handlers.addAll(StringTools.split(handlerChains));
				Set<Entry<String, DataXPreprocessor>> entrySet = preprocessorMap.entrySet();
				for (Entry<String, DataXPreprocessor> entry : entrySet) {
					String key = entry.getKey();
					if (!handlers.isEmpty() && !handlers.contains(key)) {
						continue;
					}
					logger.debug(nameMap.get(key) + "开始处理...");
					long start = System.currentTimeMillis();
					DataXPreprocessor preprocessor = entry.getValue();
					preprocessor.preprocess(parameter, exportApp);
					long ts = System.currentTimeMillis() - start;
					logger.debug(nameMap.get(key) + "用时(ms):" + ts);
				}
			}
		}
	}

	private DataXFactory() {

	}

}
