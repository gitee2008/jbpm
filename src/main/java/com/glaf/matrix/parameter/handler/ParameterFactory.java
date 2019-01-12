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
package com.glaf.matrix.parameter.handler;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.glaf.core.context.ContextFactory;
import com.glaf.matrix.parameter.domain.ParameterConversion;
import com.glaf.matrix.parameter.service.ParameterConversionService;

public class ParameterFactory {
	protected static final Log logger = LogFactory.getLog(ParameterFactory.class);

	private static class ParameterFactoryHolder {
		public static ParameterFactory instance = new ParameterFactory();
	}

	public static ParameterFactory getInstance() {
		return ParameterFactoryHolder.instance;
	}

	protected ParameterConversionService parameterConversionService;

	private ParameterFactory() {

	}

	public ParameterConversionService getParameterConversionService() {
		if (parameterConversionService == null) {
			parameterConversionService = ContextFactory
					.getBean("com.glaf.matrix.parameter.service.parameterConversionService");
		}
		return parameterConversionService;
	}

	public void processAll(String key, Map<String, Object> parameter) {
		try {
			List<ParameterConversion> conversions = getParameterConversionService().getParameterConversionsByKey(key);
			if (conversions != null && !conversions.isEmpty()) {
				IParameterHandler handler = new CommonParameterHandler();
				for (ParameterConversion conversion : conversions) {
					handler.process(conversion, parameter);
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	public void setParameterConversionService(ParameterConversionService parameterConversionService) {
		this.parameterConversionService = parameterConversionService;
	}
}