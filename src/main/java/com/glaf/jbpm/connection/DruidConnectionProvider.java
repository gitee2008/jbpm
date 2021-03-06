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
package com.glaf.jbpm.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.ConnectionProviderFactory;
import org.hibernate.util.PropertiesHelper;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.glaf.core.config.BaseConfiguration;
import com.glaf.core.config.Configuration;
import com.glaf.core.jdbc.connection.ConnectionConstants;
import com.glaf.core.util.ReflectUtils;

public class DruidConnectionProvider implements ConnectionProvider {

	private static final Logger log = LoggerFactory.getLogger(DruidConnectionProvider.class);

	protected static Configuration conf = BaseConfiguration.create();

	protected static int MAX_RETRIES = conf.getInt("jdbc.connection.retryCount", 10);

	private volatile DruidDataSource ds;

	private volatile Integer isolation;

	private volatile boolean autocommit;

	public void close() {
		try {
			ds.close();
		} catch (Exception sqle) {
			log.warn("could not destroy Druid connection pool", sqle);
		}
	}

	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	public void configure(Properties props) {
		Properties properties = new Properties();
		properties.putAll(props);

		for (Iterator<Object> ii = props.keySet().iterator(); ii.hasNext();) {
			String key = (String) ii.next();
			if (key.startsWith("druid.")) {
				String newKey = key.substring(6);
				properties.put(newKey, props.get(key));
			}
		}

		Properties connectionProps = ConnectionProviderFactory.getConnectionProperties(properties);
		log.info("Connection properties: " + PropertiesHelper.maskOut(connectionProps, Environment.PASS));

		String jdbcDriverClass = properties.getProperty(Environment.DRIVER);
		String jdbcUrl = properties.getProperty(Environment.URL);

		log.info("Druid using driver: " + jdbcDriverClass + " at URL: " + jdbcUrl);

		autocommit = PropertiesHelper.getBoolean(Environment.AUTOCOMMIT, properties);
		log.info("autocommit mode: " + autocommit);

		if (jdbcDriverClass == null) {
			log.warn("No JDBC Driver class was specified by property " + Environment.DRIVER);
		} else {
			try {
				Class.forName(jdbcDriverClass);
			} catch (ClassNotFoundException cnfe) {
				try {
					ReflectUtils.instantiate(jdbcDriverClass);
				} catch (Exception e) {
					String msg = "JDBC Driver class not found: " + jdbcDriverClass;
					log.error(msg, e);
					throw new RuntimeException(msg, e);
				}
			}
		}

		try {

			Integer maxPoolSize = PropertiesHelper.getInteger(ConnectionConstants.PROP_MAXACTIVE, properties);
			Integer maxStatements = PropertiesHelper.getInteger(ConnectionConstants.PROP_MAXSTATEMENTS, properties);

			Integer timeBetweenEvictionRuns = PropertiesHelper
					.getInteger(ConnectionConstants.PROP_TIMEBETWEENEVICTIONRUNS, properties);

			Integer maxWait = PropertiesHelper.getInteger(ConnectionConstants.PROP_MAXWAIT, properties);

			String validationQuery = properties.getProperty(ConnectionConstants.PROP_VALIDATIONQUERY);

			if (maxPoolSize == null) {
				maxPoolSize = 50;
			}

			if (timeBetweenEvictionRuns == null) {
				timeBetweenEvictionRuns = 60;
			}

			if (maxWait == null) {
				maxWait = 60;
			}

			String dbUser = properties.getProperty(Environment.USER);
			String dbPassword = properties.getProperty(Environment.PASS);

			if (dbUser == null) {
				dbUser = "";
			}

			if (dbPassword == null) {
				dbPassword = "";
			}

			ds = new DruidDataSource();

			DruidDataSourceFactory.config(ds, properties);
			ds.setConnectProperties(properties);
			ds.setDriverClassName(jdbcDriverClass);
			ds.setUrl(jdbcUrl);
			ds.setUsername(dbUser);
			ds.setPassword(dbPassword);

			ds.setInitialSize(1);
			ds.setMinIdle(3);
			ds.setMaxActive(maxPoolSize);
			ds.setMaxWait(maxWait * 1000L);

			ds.setConnectionErrorRetryAttempts(30);
			ds.setDefaultAutoCommit(true);

			ds.setTestOnReturn(false);
			ds.setTestOnBorrow(false);
			ds.setTestWhileIdle(false);

			if (StringUtils.isNotEmpty(validationQuery)) {
				log.debug("validationQuery:" + validationQuery);
				ds.setValidationQuery(validationQuery);
				ds.setTestWhileIdle(true);// 保证连接池内部定时检测连接的可用性，不可用的连接会被抛弃或者重建
			}

			ds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRuns * 1000L);// 间隔多久才进行一次检测，检测需要关闭的空闲连接
			ds.setMinEvictableIdleTimeMillis(1000L * 60L * 120L);// 配置一个连接在池中最小生存的时间，单位是毫秒

			if (maxStatements != null) {
				ds.setPoolPreparedStatements(true);
				ds.setMaxOpenPreparedStatements(maxStatements);
				ds.setMaxPoolPreparedStatementPerConnectionSize(200);
			}

			ds.setRemoveAbandoned(false);// 对于长时间不使用的连接强制关闭 true/false
			ds.setRemoveAbandonedTimeout(7200);// 超过120分钟开始关闭空闲连接
			ds.setLogAbandoned(true);// 将当前关闭动作记录到日志

			ds.init();
		} catch (Exception e) {
			log.error("could not instantiate Druid connection pool", e);
			throw new RuntimeException("Could not instantiate Druid connection pool", e);
		}

		String i = properties.getProperty(Environment.ISOLATION);
		if (i == null) {
			isolation = null;
		} else {
			isolation = new Integer(i);
		}

	}

	public Connection getConnection() throws SQLException {
		Connection connection = null;
		int retries = 0;
		while (retries < MAX_RETRIES) {
			try {
				connection = ds.getConnection();
				if (connection != null) {
					if (isolation != null) {
						connection.setTransactionIsolation(isolation.intValue());
					}
					if (connection.getAutoCommit() != autocommit) {
						connection.setAutoCommit(autocommit);
					}
					log.debug("druid connection: " + connection.toString());
					return connection;
				} else {
					retries++;
					try {
						TimeUnit.MILLISECONDS.sleep(200 + new Random().nextInt(1000));// 活锁，随机等待
					} catch (InterruptedException e) {
					}
				}
			} catch (SQLException ex) {
				if (retries++ == MAX_RETRIES) {
					throw new SQLException("druid can't getConnection", ex);
				}
				try {
					TimeUnit.MILLISECONDS.sleep(200 + new Random().nextInt(1000));// 活锁，随机等待
				} catch (InterruptedException e) {
				}
			}
		}
		return connection;
	}

	public DataSource getDataSource() {
		return ds;
	}

	public boolean supportsAggressiveRelease() {
		return false;
	}

}
