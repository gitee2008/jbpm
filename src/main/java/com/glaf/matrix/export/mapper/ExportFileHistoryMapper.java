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

package com.glaf.matrix.export.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.glaf.matrix.export.domain.ExportFileHistory;
import com.glaf.matrix.export.query.ExportFileHistoryQuery;

/**
 * 
 * Mapper接口
 *
 */

@Component("com.glaf.matrix.export.mapper.ExportFileHistoryMapper")
public interface ExportFileHistoryMapper {

	void bulkInsertExportFileHistory(List<ExportFileHistory> list);

	void bulkInsertExportFileHistory_oracle(List<ExportFileHistory> list);

	void deleteExportFileHistorys(ExportFileHistoryQuery query);

	void deleteExportFileHistoryById(String id);

	ExportFileHistory getExportFileHistoryById(String id);

	int getExportFileHistoryCount(ExportFileHistoryQuery query);

	List<ExportFileHistory> getExportFileHistorys(ExportFileHistoryQuery query);

	void insertExportFileHistory(ExportFileHistory model);

	void updateExportFileHistory(ExportFileHistory model);

}
