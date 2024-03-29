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

package com.glaf.core.service;

import com.alibaba.fastjson.JSONObject;
import com.glaf.core.base.TableModel;
import com.glaf.core.domain.TableDefinition;
import com.glaf.core.id.Dbid;
import com.glaf.core.util.Paging;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
public interface ITableDataService {

    /**
     * 删除数据
     *
     * @param model
     */
    @Transactional
    void deleteTableData(TableModel model);

    /**
     * 删除数据
     *
     * @param rows
     */
    @Transactional
    void deleteTableDataList(List<TableModel> rows);

    List<Dbid> getAllDbids();

    /**
     * 获取一页数据
     *
     * @param pageNo
     * @param pageSize
     * @param model
     * @return
     */
    Paging getPageData(int pageNo, int pageSize, TableModel model);

    /**
     * 通过表及主键获取一条记录
     *
     * @param model
     * @return
     */
    Map<String, Object> getTableDataByPrimaryKey(TableModel model);

    /**
     * 获取某个表的主键集合
     *
     * @param model
     * @return
     */
    List<Map<String, Object>> getTableKeyMap(TableModel model);

    /**
     * 批量插入数据
     *
     * @param rows
     */
    @Transactional
    void insertAllTableData(List<TableModel> rows);

    /**
     * 批量插入数据
     *
     * @param tableDefinition
     * @param rows
     */
    @Transactional
    void insertTableData(TableDefinition tableDefinition, List<Map<String, Object>> rows);

    /**
     * 插入一条记录
     *
     * @param model
     */
    @Transactional
    void insertTableData(TableModel model);

    /**
     * 批量保存记录
     *
     * @param tableDefinition
     * @param rows
     */
    @Transactional
    Collection<TableModel> saveAll(TableDefinition tableDefinition, String seqNo, Collection<TableModel> rows);

    /**
     * 批量新增或修改记录，如果存在，可以选择是否更新
     *
     * @param tableDefinition
     * @param updatable
     * @param rows
     */
    @Transactional
    void saveOrUpdate(TableDefinition tableDefinition, boolean updatable, List<Map<String, Object>> rows);

    /**
     * 保存JSON数据到指定的表
     *
     * @param tableDefinition
     * @param jsonObject
     */
    @Transactional
    void saveTableData(TableDefinition tableDefinition, JSONObject jsonObject);

    /**
     * 更新序列
     *
     * @param rows
     */
    @Transactional
    void updateAllDbids(List<Dbid> rows);

    /**
     * 批量更新记录
     *
     * @param rows
     */
    @Transactional
    void updateAllTableData(List<TableModel> rows);

    @Transactional
    void updateTableData(TableDefinition tableDefinition, List<Map<String, Object>> rows);

    /**
     * 更新一条记录
     *
     * @param model
     */
    @Transactional
    void updateTableData(TableModel model);

    /**
     * 根据查询条件更新记录
     *
     * @param model
     */
    @Transactional
    void updateTableDataByWhere(TableModel model);
}