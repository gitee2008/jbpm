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

package com.glaf.jbpm.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;

import com.glaf.core.util.LogUtils;

public class Context {

	protected final static Log logger = LogFactory.getLog(Context.class);

	private Context() {

	}

	public static void close(JbpmContext jbpmContext) {
		try {
			if (jbpmContext != null) {
				jbpmContext.close();
				jbpmContext = null;
			}
		} catch (Exception ex) {
			if (LogUtils.isDebug()) {
				ex.printStackTrace();
			}
			logger.error(ex);
			throw new JbpmException("Can't close jbpm context ", ex);
		} finally {
			jbpmContext = null;
		}
	}

}