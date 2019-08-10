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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.util.IOUtils;
import org.jxls.common.JxlsException;
import org.jxls.jdbc.CaseInsensitiveHashMap;

import com.glaf.core.config.SystemProperties;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.Database;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.jdbc.QueryConnectionFactory;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.JdbcUtils;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.StringTools;
import com.glaf.jxls.ext.JxlsImage;
import com.glaf.jxls.ext.JxlsUtil;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportItem;
import com.glaf.matrix.export.util.Constants;

/**
 * A class to help execute SQL queries via JDBC
 */
public class JdbcXHelper {
	protected final static Log logger = LogFactory.getLog(JdbcXHelper.class);

	public final static String newline = System.getProperty("line.separator");

	protected final static int MAX_ROW_SIZE = 100000;

	protected final static int MAX_IMAGE_ROW_SIZE = 1000;

	private ExportApp exportApp;

	private Connection conn;

	public JdbcXHelper(ExportApp exportApp, Connection conn) {
		this.exportApp = exportApp;
		this.conn = conn;
	}

	public List<Map<String, Object>> getList(String mapping, String name) {
		Object[] args = new Object[] {};
		IDatabaseService databaseService = ContextFactory.getBean("databaseService");
		Database database = databaseService.getDatabaseByMapping(mapping);
		if (database != null) {
			long ts = System.currentTimeMillis();
			java.sql.Connection connection = null;
			try {
				connection = DBConnectionFactory.getConnection(database.getName());
				if (connection != null) {
					QueryConnectionFactory.getInstance().register(ts, connection);
					return this.query(connection, name, args);
				}
			} catch (Exception ex) {
				throw new JxlsException("Failed to execute sql", ex);
			} finally {
				if (connection != null) {
					QueryConnectionFactory.getInstance().unregister(ts, connection);
					JdbcUtils.close(connection);
				}
			}
		}
		return null;
	}

	public List<Map<String, Object>> query(String name, Object... params) {
		return this.query(conn, name, params);
	}

	/**
	 * 根据标段查询数据
	 * 
	 * @param mapping 标段别名
	 * @param name    SQL语句的名称
	 * @param params  参数
	 * @return
	 */
	public List<Map<String, Object>> queryx(String mapping, String name, Object... params) {
		IDatabaseService databaseService = ContextFactory.getBean("databaseService");
		Database database = databaseService.getDatabaseByMapping(mapping);
		if (database != null) {
			long ts = System.currentTimeMillis();
			java.sql.Connection connection = null;
			try {
				connection = DBConnectionFactory.getConnection(database.getName());
				if (connection != null) {
					QueryConnectionFactory.getInstance().register(ts, connection);
					return this.query(connection, name, params);
				}
			} catch (Exception ex) {
				throw new JxlsException("Failed to execute sql", ex);
			} finally {
				if (connection != null) {
					QueryConnectionFactory.getInstance().unregister(ts, connection);
					JdbcUtils.close(connection);
				}
			}
		}
		return null;
	}

	public List<Map<String, Object>> query(java.sql.Connection connection, String name, Object... params) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (connection == null) {
			throw new JxlsException("Null jdbc connection");
		}

		String sql = null;

		if (exportApp.getItems() != null && !exportApp.getItems().isEmpty()) {
			for (ExportItem item : exportApp.getItems()) {
				if (StringUtils.equals(name, item.getName())) {
					sql = item.getSql();
					break;
				}
			}
		}

		if (sql == null) {
			throw new JxlsException("Null SQL statement");
		}

		if (!DBUtils.isLegalQuerySql(sql)) {
			throw new JxlsException("SQL statement Illegal");
		}

		sql = DBUtils.parseSQL(sql);

		logger.debug("sql:" + sql);
		for (int i = 0; i < params.length; i++) {
			logger.debug("param" + i + "=" + params[i]);
		}

		int index = 0;
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			fillStatement(stmt, params);
			try (ResultSet rs = stmt.executeQuery()) {
				index++;
				if (index <= MAX_ROW_SIZE) {
					handle(result, rs);
				}
			}
		} catch (Exception e) {
			throw new JxlsException("Failed to execute sql", e);
		}
		return result;
	}

	public List<Map<String, Object>> images(String name, Object... params) {
		return this.images(conn, name, params);
	}

	/**
	 * 根据标段查询数据
	 * 
	 * @param mapping 标段别名
	 * @param name    SQL语句的名称
	 * @param params  参数
	 * @return
	 */
	public List<Map<String, Object>> imagesx(String mapping, String name, Object... params) {
		IDatabaseService databaseService = ContextFactory.getBean("databaseService");
		Database database = databaseService.getDatabaseByMapping(mapping);
		if (database != null) {
			long ts = System.currentTimeMillis();
			java.sql.Connection connection = null;
			try {
				connection = DBConnectionFactory.getConnection(database.getName());
				if (connection != null) {
					QueryConnectionFactory.getInstance().register(ts, connection);
					return this.images(connection, name, params);
				}
			} catch (Exception ex) {
				throw new JxlsException("Failed to execute sql", ex);
			} finally {
				if (connection != null) {
					QueryConnectionFactory.getInstance().unregister(ts, connection);
					JdbcUtils.close(connection);
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param connection 数据库连接
	 * @param name       SQL语句的名称
	 * @param params     参数
	 * @return
	 */
	public List<Map<String, Object>> images(java.sql.Connection connection, String name, Object... params) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (connection == null) {
			throw new JxlsException("Null jdbc connection");
		}

		String sql = null;

		if (exportApp.getItems() != null && !exportApp.getItems().isEmpty()) {
			for (ExportItem item : exportApp.getItems()) {
				if (StringUtils.equals(name, item.getName())) {
					sql = item.getSql();
					break;
				}
			}
		}

		if (sql == null) {
			throw new JxlsException("Null SQL statement");
		}
		if (!DBUtils.isLegalQuerySql(sql)) {
			throw new JxlsException("SQL statement Illegal");
		}

		sql = DBUtils.parseSQL(sql);
		logger.debug("sql:" + sql);
		for (int i = 0; i < params.length; i++) {
			logger.debug("param" + i + "=" + params[i]);
		}

		int index = 0;
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			fillStatement(stmt, params);
			try (ResultSet rs = stmt.executeQuery()) {
				index++;
				if (index <= MAX_IMAGE_ROW_SIZE) {
					handle(result, rs);
				}
			}
		} catch (Exception e) {
			throw new JxlsException("Failed to execute sql", e);
		}

		if (!result.isEmpty()) {
			// int index2 = 0;
			for (Map<String, Object> dataMap : result) {
				// if (index2 % 10 == 0) {
				// logger.debug(dataMap);
				// }
				// index2++;
				FileInputStream fin = null;
				Map<String, Object> newDataMap = new CaseInsensitiveHashMap();
				Set<Entry<String, Object>> entrySet = dataMap.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (value != null) {
						String imageName = ParamUtils.getString(dataMap, "filename");
						if (StringUtils.isNotEmpty(imageName)) {
							if (value instanceof java.sql.Clob) {
								java.sql.Clob clob = (java.sql.Clob) value;
								String str = this.clobToString(clob);
								newDataMap.put(key, str);
							} else if (value instanceof java.sql.Blob) {
								java.sql.Blob blob = (java.sql.Blob) value;
								byte[] data = this.blobToBytes(blob);
								try {
									JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(data, imageName);
									newDataMap.put(key + "_img", jxlsImage);
								} catch (IOException ex) {
								}
							} else if (value instanceof java.io.InputStream) {
								java.io.InputStream inStream = (java.io.InputStream) value;
								byte[] data = FileUtils.getBytes(inStream);
								try {
									JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(data, imageName);
									newDataMap.put(key + "_img", jxlsImage);
								} catch (IOException ex) {
								}
							} else if (value instanceof byte[]) {
								byte[] data = (byte[]) value;
								try {
									JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(data, imageName);
									newDataMap.put(key + "_img", jxlsImage);
								} catch (IOException ex) {
								}
							}
						}
						if (StringUtils.startsWith(key, "image_path")) {
							imageName = value.toString();
							String full_path = SystemProperties.getAppPath() + value.toString();
							logger.debug("读取图片:" + full_path);
							try {
								File file = new File(full_path);
								if (file.exists() && file.isFile()) {
									fin = new FileInputStream(file);
									byte[] data = FileUtils.getBytes(fin);
									JxlsImage jxlsImage = JxlsUtil.me().getJxlsImage(data, imageName);
									newDataMap.put(key + "_img", jxlsImage);
								}
							} catch (IOException ex) {
								logger.error("读取图片错误:" + ex.getMessage());
							} finally {
								IOUtils.closeQuietly(fin);
							}
						}
					}
				}
				if (newDataMap.size() > 0) {
					dataMap.putAll(newDataMap);
				}
			}
		}

		return result;
	}

	public String clobToString(java.sql.Clob clob) {
		Reader inStream = null;
		try {
			inStream = clob.getCharacterStream();
			char[] c = new char[(int) clob.length()];
			inStream.read(c);
			String data = new String(c);
			IOUtils.closeQuietly(inStream);
			return data;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			IOUtils.closeQuietly(inStream);
		}
	}

	public byte[] blobToBytes(java.sql.Blob blob) {
		byte[] data = null;
		InputStream inStream = null;
		try {
			inStream = blob.getBinaryStream();
			if (inStream != null) {
				data = FileUtils.getBytes(inStream);
				IOUtils.closeQuietly(inStream);
			}
			return data;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			IOUtils.closeQuietly(inStream);
		}
	}

	// The implementation is a slightly modified version of a similar method of
	// AbstractQueryRunner in Apache DBUtils
	private void fillStatement(PreparedStatement stmt, Object[] params) throws SQLException {
		// nothing to do here
		if (params == null) {
			return;
		}

		// check the parameter count, if we can
		ParameterMetaData pmd = null;
		boolean pmdKnownBroken = false;

		int stmtCount = 0;
		int paramsCount = 0;
		try {
			pmd = stmt.getParameterMetaData();
			stmtCount = pmd.getParameterCount();
			paramsCount = params.length;
		} catch (Exception e) {
			pmdKnownBroken = true;
		}

		if (stmtCount != paramsCount) {
			throw new SQLException("Wrong number of parameters: expected " + stmtCount + ", was given " + paramsCount);
		}

		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				stmt.setObject(i + 1, params[i]);
			} else {
				// VARCHAR works with many drivers regardless
				// of the actual column type. Oddly, NULL and
				// OTHER don't work with Oracle's drivers.
				int sqlType = Types.VARCHAR;
				if (!pmdKnownBroken) {
					try {
						/*
						 * It's not possible for pmdKnownBroken to change from true to false, (once
						 * true, always true) so pmd cannot be null here.
						 */
						sqlType = pmd.getParameterType(i + 1);
					} catch (SQLException e) {
						pmdKnownBroken = true;
					}
				}
				stmt.setNull(i + 1, sqlType);
			}
		}
	}

	public void handle(List<Map<String, Object>> result, ResultSet rs) throws SQLException {
		while (rs.next()) {
			result.add(handleRow(rs));
		}
	}

	private Map<String, Object> handleRow(ResultSet rs) throws SQLException {
		Map<String, Object> result = new CaseInsensitiveHashMap();
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		for (int i = 1; i <= cols; i++) {
			String columnName = rsmd.getColumnLabel(i);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(i);
			}

			Object value = rs.getObject(i);
			if (value instanceof java.sql.Clob) {
				java.sql.Clob clob = (java.sql.Clob) value;
				String str = this.clobToString(clob);
				value = str;
			}

			result.put(columnName, value);

			if (value != null && StringUtils.endsWith(columnName, "_newline")) {
				String str = value.toString();
				str = StringTools.replace(str, Constants.LINE_SP, newline);
				result.put(columnName, str);
				result.put(columnName.substring(0, columnName.length() - 8) + "_orig", str);
			}
		}
		return result;
	}
}
