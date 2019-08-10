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

import com.glaf.core.config.Environment;
import com.glaf.core.config.SystemProperties;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.Database;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.util.DBUtils;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.ParamUtils;
import com.glaf.jxls.ext.JxlsImage;
import com.glaf.jxls.ext.JxlsUtil;
import com.glaf.matrix.export.service.ExportAppService;

/**
 * A class to help execute SQL queries via JDBC
 */
public class JdbcTemplateHelper {
	protected final static Log logger = LogFactory.getLog(JdbcTemplateHelper.class);

	public final static String newline = System.getProperty("line.separator");

	protected final static int MAX_ROW_SIZE = 100000;

	protected final static int MAX_IMAGE_ROW_SIZE = 1000;

	private Database database;

	private ExportAppService exportAppService;

	public JdbcTemplateHelper(Database database) {
		this.database = database;
	}

	protected byte[] blobToBytes(java.sql.Blob blob) {
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

	protected String clobToString(java.sql.Clob clob) {
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

	public ExportAppService getExportAppService() {
		if (exportAppService == null) {
			exportAppService = ContextFactory.getBean("com.glaf.matrix.export.service.exportAppService");
		}
		return exportAppService;
	}

	public List<Map<String, Object>> getList(String mapping, String sql) {
		Object[] args = new Object[] {};
		IDatabaseService databaseService = ContextFactory.getBean("databaseService");
		try {
			Database database = databaseService.getDatabaseByMapping(mapping);
			if (database != null) {
				return this.queryInner(database, sql, args);
			}
		} catch (Exception ex) {
			throw new JxlsException("Failed to execute sql", ex);
		}

		return null;
	}

	public List<Map<String, Object>> images(String sql, Object... args) {
		return this.imagesInner(database, sql, args);
	}

	/**
	 * 
	 * @param database 数据库连接
	 * @param sql      SQL语句
	 * @param args     参数
	 * @return
	 */
	public List<Map<String, Object>> imagesInner(Database database, String sql, Object... args) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (database == null) {
			throw new JxlsException("Null jdbc connection");
		}

		if (sql == null) {
			throw new JxlsException("Null SQL statement");
		}
		if (!DBUtils.isLegalQuerySql(sql)) {
			throw new JxlsException("SQL statement Illegal");
		}

		sql = DBUtils.parseSQL(sql);
		logger.debug("sql:" + sql);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				logger.debug("param" + i + "=" + args[i]);
			}
		}

		String systemName = Environment.getCurrentSystemName();
		try {
			if (database != null) {
				Environment.setCurrentSystemName(database.getName());
			}
			result = getExportAppService().getResultList(sql, args);
		} catch (Exception e) {
			throw new JxlsException("Failed to execute sql", e);
		} finally {
			Environment.setCurrentSystemName(systemName);
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

	/**
	 * 根据标段查询数据
	 * 
	 * @param mapping 标段别名
	 * @param sql     SQL语句
	 * @param args    参数
	 * @return
	 */
	public List<Map<String, Object>> imagesx(String mapping, String sql, Object... args) {
		IDatabaseService databaseService = ContextFactory.getBean("databaseService");
		try {
			Database database = databaseService.getDatabaseByMapping(mapping);
			if (database != null) {
				return this.imagesInner(database, sql, args);
			}
		} catch (Exception ex) {
			throw new JxlsException("Failed to execute sql", ex);
		}

		return null;
	}

	public List<Map<String, Object>> query(String name) {
		Object[] args = new Object[] {};
		return this.queryInner(database, name, args);
	}

	public List<Map<String, Object>> query(String name, Object... args) {
		return this.queryInner(database, name, args);
	}

	public List<Map<String, Object>> queryInner(Database database, String sql, Object... args) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (database == null) {
			throw new JxlsException("Null jdbc connection");
		}

		if (sql == null) {
			throw new JxlsException("Null SQL statement");
		}

		if (!DBUtils.isLegalQuerySql(sql)) {
			throw new JxlsException("SQL statement Illegal");
		}

		sql = DBUtils.parseSQL(sql);

		logger.debug("sql:" + sql);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				logger.debug("param" + i + "=" + args[i]);
			}
		}

		String systemName = Environment.getCurrentSystemName();
		try {
			if (database != null) {
				Environment.setCurrentSystemName(database.getName());
			}
			result = getExportAppService().getResultList(sql, args);
		} catch (Exception e) {
			throw new JxlsException("Failed to execute sql", e);
		} finally {
			Environment.setCurrentSystemName(systemName);
		}

		return result;
	}

	/**
	 * 根据标段查询数据
	 * 
	 * @param mapping 标段别名
	 * @param name    SQL语句的名称
	 * @param args    参数
	 * @return
	 */
	public List<Map<String, Object>> queryx(String mapping, String name, Object... args) {
		IDatabaseService databaseService = ContextFactory.getBean("databaseService");
		try {
			Database database = databaseService.getDatabaseByMapping(mapping);
			if (database != null) {
				return this.queryInner(database, name, args);
			}
		} catch (Exception ex) {
			throw new JxlsException("Failed to execute sql", ex);
		}
		return null;
	}

}
