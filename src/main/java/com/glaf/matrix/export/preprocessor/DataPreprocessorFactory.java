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

package com.glaf.matrix.export.preprocessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.glaf.core.util.ReflectUtils;
import com.glaf.matrix.export.domain.ExportItem;

public class DataPreprocessorFactory {

	protected static final Log logger = LogFactory.getLog(DataPreprocessorFactory.class);

	private DataPreprocessorFactory() {

	}

	/**
	 * 数据处理器，提供给编程接口使用
	 * 
	 * @param item
	 * @param parameter
	 * @return
	 */
	public static void prepare(ExportItem item, Map<String, Object> parameter) {
		if (StringUtils.isNotEmpty(item.getPreprocessors())) {
			int index = 0;
			StringTokenizer token = new StringTokenizer(item.getPreprocessors(), ",");
			while (token.hasMoreTokens()) {
				index++;
				String className = token.nextToken();
				if (StringUtils.isNotEmpty(className)) {
					Class<?> clazz = null;
					try {
						clazz = Class.forName(className);
						Object object = ReflectUtils.newInstance(clazz);
						if (object instanceof IDataPreprocessor) {
							IDataPreprocessor bean = (IDataPreprocessor) object;
							Map<String, Object> params = new HashMap<String, Object>();
							params.putAll(parameter);// 新建参数，避免覆盖原来的
							Object val = bean.prepare(item, params);
							if (val != null) {
								if (StringUtils.equals(item.getResultFlag(), "O")) {
									if (val instanceof Collection) {
										Collection<?> coll = (Collection<?>) val;
										if (!coll.isEmpty()) {
											Object obj = coll.iterator().next();
											parameter.put(item.getName() + "_one", obj);
											parameter.put(item.getName() + "_var", obj);
										}
									}
								} else {
									parameter.put(item.getName() + "_var", val);
									parameter.put(item.getName() + "_" + index + "_var", val);
								}
							}
						}
					} catch (ClassNotFoundException ex) {
					}
				}
			}
		}
	}

}
