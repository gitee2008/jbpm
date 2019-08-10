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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
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

public class QueryXHelper {
	protected final static Log logger = LogFactory.getLog(QueryXHelper.class);

	public final static String newline = System.getProperty("line.separator");

	protected final static int MAX_ROW_SIZE = 100000;

	protected final static int MAX_IMAGE_ROW_SIZE = 1000;

	private ExportApp exportApp;

	private Connection conn;

	public QueryXHelper(ExportApp exportApp, Connection conn) {
		this.exportApp = exportApp;
		this.conn = conn;
	}

	public List<Map<String, Object>> getList(String name) {
		Object[] args = new Object[] {};
		return this.query(conn, name, args);
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

	/**
	 * 
	 * @param connection 数据库连接
	 * @param name       SQL语句的名称
	 * @param params     参数
	 * @return
	 */
	public List<Map<String, Object>> query(java.sql.Connection connection, String name, Object... params) {
		List<Map<String, Object>> result = null;
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
			if (params[i] != null) {
				logger.debug("param class" + params[i].getClass().getName());
			}
		}
		ResultSetHandler<List<Map<String, Object>>> handler = new MapListHandler();
		QueryRunner runner = new QueryRunner();
		try {
			result = runner.query(connection, sql, handler, params);
			if (result != null && !result.isEmpty()) {
				for (Map<String, Object> dataMap : result) {
					Map<String, Object> rowMap = new CaseInsensitiveHashMap();
					Set<Entry<String, Object>> entrySet = dataMap.entrySet();
					for (Entry<String, Object> entry : entrySet) {
						String key = entry.getKey();
						Object value = entry.getValue();
						if (value != null) {
							if (value instanceof java.sql.Clob) {
								java.sql.Clob clob = (java.sql.Clob) value;
								String str = this.clobToString(clob);
								dataMap.put(key, str);
							}
							if (StringUtils.endsWith(key, "_newline")) {
								String str = value.toString();
								str = StringTools.replace(str, Constants.LINE_SP, newline);
								rowMap.put(key, str);
								rowMap.put(key.substring(0, key.length() - 8) + "_orig", str);
							}
						}
					}
					dataMap.putAll(rowMap);
				}
			}
		} catch (SQLException e) {
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
		List<Map<String, Object>> result = null;
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
			if (params[i] != null) {
				logger.debug("param class" + params[i].getClass().getName());
			}
		}
		ResultSetHandler<List<Map<String, Object>>> handler = new MapListHandler();
		QueryRunner runner = new QueryRunner();
		try {
			result = runner.query(connection, sql, handler, params);
		} catch (SQLException ex) {
			throw new JxlsException("Failed to execute sql", ex);
		}

		if (result != null && !result.isEmpty()) {
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

						if (StringUtils.endsWith(key, "_newline")) {
							String str = value.toString();
							str = StringTools.replace(str, Constants.LINE_SP, newline);
							newDataMap.put(key, str);
							newDataMap.put(key.substring(0, key.length() - 8) + "_orig", str);
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

}
