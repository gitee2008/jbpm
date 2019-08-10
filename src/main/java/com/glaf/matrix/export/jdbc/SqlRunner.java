/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.glaf.matrix.export.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.Null;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.jxls.jdbc.CaseInsensitiveHashMap;

/**
 * @author Clinton Begin
 */
public class SqlRunner {

	public static final int NO_GENERATED_KEY = Integer.MIN_VALUE + 1001;

	private final Connection connection;
	private final TypeHandlerRegistry typeHandlerRegistry;

	public SqlRunner(Connection connection) {
		this.connection = connection;
		this.typeHandlerRegistry = new TypeHandlerRegistry();
	}

	/**
	 * Executes a SELECT statement that returns multiple rows.
	 *
	 * @param sql  The SQL
	 * @param args The arguments to be set on the statement.
	 * @return The list of rows expected.
	 * @throws SQLException If statement preparation or execution fails
	 */
	public List<Map<String, Object>> selectAll(String sql, int limit, Object... args) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(sql);
		try {
			setParameters(ps, args);
			ResultSet rs = ps.executeQuery();
			return getResults(rs, limit);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				// ignore
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setParameters(PreparedStatement ps, Object... args) throws SQLException {
		for (int i = 0, n = args.length; i < n; i++) {
			if (args[i] == null) {
				throw new SQLException(
						"SqlRunner requires an instance of Null to represent typed null values for JDBC compatibility");
			} else if (args[i] instanceof Null) {
				((Null) args[i]).getTypeHandler().setParameter(ps, i + 1, null, ((Null) args[i]).getJdbcType());
			} else {
				TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(args[i].getClass());
				if (typeHandler == null) {
					throw new SQLException("SqlRunner could not find a TypeHandler instance for " + args[i].getClass());
				} else {
					typeHandler.setParameter(ps, i + 1, args[i], null);
				}
			}
		}
	}

	private List<Map<String, Object>> getResults(ResultSet rs, int limit) throws SQLException {
		try {
			List<Map<String, Object>> list = new ArrayList<>();
			List<String> columns = new ArrayList<>();
			List<TypeHandler<?>> typeHandlers = new ArrayList<>();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 0, n = rsmd.getColumnCount(); i < n; i++) {
				columns.add(rsmd.getColumnLabel(i + 1));
				try {
					Class<?> type = Resources.classForName(rsmd.getColumnClassName(i + 1));
					TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(type);
					if (typeHandler == null) {
						typeHandler = typeHandlerRegistry.getTypeHandler(Object.class);
					}
					typeHandlers.add(typeHandler);
				} catch (Exception e) {
					typeHandlers.add(typeHandlerRegistry.getTypeHandler(Object.class));
				}
			}
			int index = 0;
			while (rs.next() && index < limit) {
				index++;
				Map<String, Object> row = new CaseInsensitiveHashMap();
				for (int i = 0, n = columns.size(); i < n; i++) {
					String name = columns.get(i);
					TypeHandler<?> handler = typeHandlers.get(i);
					Object value = handler.getResult(rs, name);
					row.put(name, value);
					row.put(name.toLowerCase(), value);
				}
				list.add(row);
			}
			return list;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

}
