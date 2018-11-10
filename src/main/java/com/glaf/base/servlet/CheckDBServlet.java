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

package com.glaf.base.servlet;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.glaf.core.entity.jpa.EntitySchemaUpdate;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.util.DBUtils;

public class CheckDBServlet extends HttpServlet {

	private final static Log logger = LogFactory.getLog(CheckDBServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		System.out.println("---------------------CheckDBServlet----------------------");
		if (!DBConnectionFactory.checkConnection()) {
			logger.error("数据库连接错误，请检查配置！！！");
			return;
		}

		try {
			if (!DBUtils.tableExists("SYS_PROPERTY")) {
				EntitySchemaUpdate bean = new EntitySchemaUpdate();
				bean.updateDDL();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("初始化库表错误！");
		}
	}

}
