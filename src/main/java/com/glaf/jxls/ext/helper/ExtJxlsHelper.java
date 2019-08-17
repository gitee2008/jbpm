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

package com.glaf.jxls.ext.helper;

import org.apache.commons.codec.binary.StringUtils;
import org.jxls.expression.ExpressionEvaluator;
import org.jxls.expression.ExpressionEvaluatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.jxls.ext.expression.AviatorExpressionEvaluatorFactoryImpl;
import com.glaf.jxls.ext.expression.Mvel2ExpressionEvaluatorFactoryImpl;

public class ExtJxlsHelper extends org.jxls.util.JxlsHelper {

	public static ExtJxlsHelper getInstance() {
		return new ExtJxlsHelper();
	}

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String expressionFactory;

	protected ExpressionEvaluatorFactory myExpressionEvaluatorFactory;

	public ExtJxlsHelper() {

	}

	public ExtJxlsHelper(String expressionFactory) {
		this.expressionFactory = expressionFactory;
		logger.debug("-------------------expressionFactory:" + expressionFactory);
	}

	@Override
	public ExpressionEvaluator createExpressionEvaluator(final String expression) {
		logger.debug("--------------------createExpressionEvaluator----------");
		if (StringUtils.equals(expressionFactory, "mvel2")) {
			if (myExpressionEvaluatorFactory == null) {
				myExpressionEvaluatorFactory = new Mvel2ExpressionEvaluatorFactoryImpl();
			}
			logger.debug("---------------------MVEL--------------------------");
			return myExpressionEvaluatorFactory.createExpressionEvaluator(expression);
		} else if (StringUtils.equals(expressionFactory, "aviator")) {
			if (myExpressionEvaluatorFactory == null) {
				myExpressionEvaluatorFactory = new AviatorExpressionEvaluatorFactoryImpl();
			}
			logger.debug("---------------------aviator--------------------------");
			return myExpressionEvaluatorFactory.createExpressionEvaluator(expression);
		}
		return super.createExpressionEvaluator(expression);
	}

	@Override
	public ExpressionEvaluatorFactory getExpressionEvaluatorFactory() {
		logger.debug("--------------------getExpressionEvaluatorFactory----------");
		if (StringUtils.equals(expressionFactory, "mvel2")) {
			if (myExpressionEvaluatorFactory == null) {
				myExpressionEvaluatorFactory = new Mvel2ExpressionEvaluatorFactoryImpl();
			}
			logger.debug("---------------------MVEL--------------------------");
			return myExpressionEvaluatorFactory;
		} else if (StringUtils.equals(expressionFactory, "aviator")) {
			if (myExpressionEvaluatorFactory == null) {
				myExpressionEvaluatorFactory = new AviatorExpressionEvaluatorFactoryImpl();
			}
			logger.debug("---------------------aviator--------------------------");
			return myExpressionEvaluatorFactory;
		}
		return super.getExpressionEvaluatorFactory();
	}

}
