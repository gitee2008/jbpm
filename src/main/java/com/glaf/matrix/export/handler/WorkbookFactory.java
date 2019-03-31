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

package com.glaf.matrix.export.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import com.glaf.core.util.StringTools;
import com.glaf.matrix.export.domain.ExportApp;

public class WorkbookFactory {

	protected static ConcurrentMap<String, WorkbookHandler> handlerMap = new ConcurrentHashMap<String, WorkbookHandler>();

	protected static ConcurrentMap<String, String> nameMap = new ConcurrentHashMap<String, String>();

	static {
		handlerMap.put("cellMerge", new CellMergeHandler());
		handlerMap.put("pageBreak", new PageBreakHandler());
		handlerMap.put("rowHeightAdjust", new RowHeightAdjustHandler());
		nameMap.put("pageBreak", "分页处理器");
		nameMap.put("cellMerge", "合并单元格处理器");
		nameMap.put("rowHeightAdjust", "行高调整处理器");
	}

	public static ConcurrentMap<String, String> getNameMap() {
		return nameMap;
	}

	public static void process(Workbook wb, ExportApp exportApp) {
		String handlerChains = exportApp.getExcelProcessChains();
		List<String> handlers = new ArrayList<String>();
		if (StringUtils.isNotEmpty(handlerChains)) {
			handlers.addAll(StringTools.split(handlerChains));
		}
		Set<Entry<String, WorkbookHandler>> entrySet = handlerMap.entrySet();
		for (Entry<String, WorkbookHandler> entry : entrySet) {
			String key = entry.getKey();
			if (!handlers.isEmpty() && !handlers.contains(key)) {
				continue;
			}
			WorkbookHandler preprocessor = entry.getValue();
			preprocessor.processWorkbook(wb, exportApp);
		}
	}

	private WorkbookFactory() {

	}

}
