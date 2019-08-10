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

package com.glaf.matrix.export.jdbc;

import java.sql.Connection;
import java.util.Map;

import com.glaf.core.domain.Database;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.jdbc.EntityHelper;
import com.glaf.matrix.export.jdbc.JdbcHelper;
import com.glaf.matrix.export.jdbc.JdbcTemplateHelper;
import com.glaf.matrix.export.jdbc.JdbcTemplateXHelper;
import com.glaf.matrix.export.jdbc.JdbcXHelper;
import com.glaf.matrix.export.jdbc.MyBatisHelper;
import com.glaf.matrix.export.jdbc.QueryHelper;
import com.glaf.matrix.export.jdbc.QueryXHelper;

public class ContextHelperFactory {

	private ContextHelperFactory() {

	}

	public static void put(ExportApp exportApp, Database database, Connection conn, Map<String, Object> parameter) {
		JdbcHelper jdbcHelper = new JdbcHelper(conn, parameter);
		JdbcXHelper jdbcXHelper = new JdbcXHelper(exportApp, conn);
		QueryHelper queryHelper = new QueryHelper(conn, parameter);
		QueryXHelper queryXHelper = new QueryXHelper(exportApp, conn);
		MyBatisHelper myBatisHelper = new MyBatisHelper(conn, parameter);
		EntityHelper entityHelper = new EntityHelper(database, parameter);
		JdbcTemplateHelper jdbcTemplateHelper = new JdbcTemplateHelper(database);
		JdbcTemplateXHelper jdbcTemplateXHelper = new JdbcTemplateXHelper(exportApp, database);
		 
		parameter.put("jdbc", jdbcHelper);
		parameter.put("jdbcX", jdbcXHelper);// SQL语句配置在模板外面
		parameter.put("entity", entityHelper);
		parameter.put("dbutils", queryHelper); // DBUtils
		parameter.put("dbutilsX", queryXHelper);// SQL语句配置在模板外面
		parameter.put("mybatis", myBatisHelper);
		parameter.put("jdbcTemplate", jdbcTemplateHelper);// Spring jdbcTemplate
		parameter.put("jdbcTemplateX", jdbcTemplateXHelper);// Spring jdbcTemplate
	}

}
