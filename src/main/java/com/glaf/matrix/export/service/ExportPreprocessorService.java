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

package com.glaf.matrix.export.service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.matrix.export.domain.*;
import com.glaf.matrix.export.query.*;

@Transactional(readOnly = true)
public interface ExportPreprocessorService {

	/**
	 * 根据查询参数获取记录列表
	 * 
	 * @return
	 */
	List<ExportPreprocessor> list(ExportPreprocessorQuery query);

	/**
	 * 保存记录
	 * 
	 * @return
	 */
	@Transactional
	void saveAll(String exportId, String currentStep, String currentType, List<ExportPreprocessor> rows);

}
