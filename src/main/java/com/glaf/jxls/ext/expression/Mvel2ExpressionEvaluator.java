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

package com.glaf.jxls.ext.expression;

import org.jxls.expression.*;

import org.mvel2.MVEL;

import java.util.Map;


public class Mvel2ExpressionEvaluator implements ExpressionEvaluator {

	protected String expression;

	public Mvel2ExpressionEvaluator() {

	}

	public Mvel2ExpressionEvaluator(String expression) {
		this.expression = expression;
	}

	public Object evaluate(String expression, Map<String, Object> context) {
		try {
			return MVEL.eval(expression, context);
		} catch (Exception ex) {
		}
		return "";
	}

	public Object evaluate(Map<String, Object> context) {
		try {
			return MVEL.eval(expression, context);
		} catch (Exception ex) {
		}
		return "";
	}

	public String getExpression() {
		return expression;
	}

}
