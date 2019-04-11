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
package com.glaf.core.config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.domain.SysKey;
import com.glaf.core.jdbc.DBConnectionFactory;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.JdbcUtils;

public class KeyHelper {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 获取密锁列表
	 * 
	 * @return
	 */
	public List<SysKey> getAllSystemProperties(String systemName) {
		List<SysKey> rows = new ArrayList<SysKey>();
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection(systemName);
			psmt = conn.prepareStatement(" select * from SYS_KEY ");
			psmt.setInt(1, 0);
			rs = psmt.executeQuery();
			while (rs.next()) {
				SysKey model = new SysKey();
				this.populdate(rs, model);
				rows.add(model);
			}
		} catch (SQLException ex) {
			logger.error("get key list error", ex);
			throw new RuntimeException(ex);
		} finally {
			JdbcUtils.close(rs);
			JdbcUtils.close(psmt);
			JdbcUtils.close(conn);
		}
		return rows;
	}

	/**
	 * 获取系统密锁
	 * 
	 * @param id 密锁编号
	 * @return
	 */
	public SysKey getSysKeyById(String systemName, String id) {
		SysKey property = null;
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection(systemName);
			psmt = conn.prepareStatement(" select * from SYS_KEY where ID_ = ? ");
			psmt.setString(1, id);
			rs = psmt.executeQuery();
			if (rs.next()) {
				property = new SysKey();
				this.populdate(rs, property);
			}
		} catch (SQLException ex) {
			logger.error("get key error", ex);
			throw new RuntimeException(ex);
		} finally {
			JdbcUtils.close(rs);
			JdbcUtils.close(psmt);
			JdbcUtils.close(conn);
		}
		return property;
	}

	/**
	 * 获取系统密锁
	 * 
	 * @param key 密锁Key
	 * @return
	 */
	public SysKey getSysKeyByKey(String systemName, String key) {
		SysKey property = null;
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection(systemName);
			psmt = conn.prepareStatement(" select * from SYS_KEY where NAME_ = ? ");
			psmt.setString(1, key);
			rs = psmt.executeQuery();
			if (rs.next()) {
				property = new SysKey();
				this.populdate(rs, property);
			}
		} catch (SQLException ex) {
			logger.error("get key error", ex);
			throw new RuntimeException(ex);
		} finally {
			JdbcUtils.close(rs);
			JdbcUtils.close(psmt);
			JdbcUtils.close(conn);
		}
		return property;
	}

	public void populdate(ResultSet rs, SysKey model) throws SQLException {
		model.setId(rs.getString("ID_"));
		model.setTitle(rs.getString("TITLE_"));
		model.setType(rs.getString("TYPE_"));
		model.setName(rs.getString("NAME_"));
		model.setPath(rs.getString("PATH_"));
		model.setCreateBy(rs.getString("CREATEBY_"));
		model.setCreateDate(rs.getTimestamp("CREATEDATE_"));
		model.setData(rs.getBytes("DATA_"));
	}

	public void save(String systemName, SysKey model) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" insert into SYS_KEY ");
		buffer.append(" (ID_, NAME_, TITLE_, TYPE_, PATH_, DATA_, CREATEBY_, CREATEDATE_)");
		buffer.append(" values (?, ?, ?, ?, ?, ?, ?, ?) ");
		int index = 1;
		Connection conn = null;
		PreparedStatement psmt = null;
		try {
			conn = DBConnectionFactory.getConnection(systemName);
			conn.setAutoCommit(false);
			psmt = conn.prepareStatement(buffer.toString());
			psmt.setString(index++, model.getId());
			psmt.setString(index++, model.getName());
			psmt.setString(index++, model.getTitle());
			psmt.setString(index++, model.getType());
			psmt.setString(index++, model.getPath());
			psmt.setBytes(index++, model.getData());
			psmt.setString(index++, model.getCreateBy());
			psmt.setTimestamp(index++, DateUtils.toTimestamp(model.getCreateDate()));

			psmt.executeUpdate();
			psmt.close();
			conn.commit();
			logger.debug("--------save key ok.");
		} catch (SQLException ex) {
			logger.error("save key error", ex);
			throw new RuntimeException(ex);
		} finally {
			JdbcUtils.close(psmt);
			JdbcUtils.close(conn);
		}
	}

	public void update(String systemName, SysKey model) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" update SYS_KEY set DATA_ = ? where ID_ = ? ");
		int index = 1;
		Connection conn = null;
		PreparedStatement psmt = null;
		try {
			conn = DBConnectionFactory.getConnection(systemName);
			conn.setAutoCommit(false);
			psmt = conn.prepareStatement(buffer.toString());
			psmt.setBytes(index++, model.getData());
			psmt.setString(index++, model.getId());
			psmt.executeUpdate();
			psmt.close();
			conn.commit();
			logger.debug("--------update key ok.");
		} catch (SQLException ex) {
			logger.error("update key error", ex);
			throw new RuntimeException(ex);
		} finally {
			JdbcUtils.close(psmt);
			JdbcUtils.close(conn);
		}
	}

}
