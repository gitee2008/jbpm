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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.util.StringTools;
import com.glaf.matrix.export.domain.ExportApp;

public class WorkbookFactory {
	protected static final Logger logger = LoggerFactory.getLogger(WorkbookFactory.class);

	protected static ConcurrentMap<String, WorkbookHandler> handlerMap = new ConcurrentHashMap<String, WorkbookHandler>();

	protected static ConcurrentMap<String, String> nameMap = new ConcurrentHashMap<String, String>();

	protected static List<String> handerList = new CopyOnWriteArrayList<String>();

	static {
		handlerMap.put("cellMerge", new CellMergeHandler());
		handlerMap.put("autoPageBreak", new AutoPageBreakHandler());
		handlerMap.put("pageBreak", new PageBreakHandler());
		handlerMap.put("rowHeightAdjust", new RowHeightAdjustHandler());
		handlerMap.put("hiddenRow", new HiddenRowHandler());
		handlerMap.put("toDecimal", new TextToDecimalHandler());
		handlerMap.put("pageFooterBorder", new PageFooterBorderHandler());
		handlerMap.put("xRemoveComment", new RemoveCommentHandler());

		nameMap.put("pageBreak", "分页处理器");
		nameMap.put("autoPageBreak", "设置页分隔符");
		nameMap.put("cellMerge", "合并单元格处理器");
		nameMap.put("rowHeightAdjust", "行高调整处理器");
		nameMap.put("hiddenRow", "隐藏行处理器");
		nameMap.put("toDecimal", "文本转数值处理器");
		nameMap.put("pageFooterBorder", "设置页脚边框");
		nameMap.put("xRemoveComment", "去除标注");

		handerList.add("rowHeightAdjust");// 最先执行
		handerList.add("hiddenRow");
		handerList.add("toDecimal");
		handerList.add("autoPageBreak");
		handerList.add("pageBreak");
		handerList.add("pageFooterBorder");
		handerList.add("cellMerge");
		handerList.add("xRemoveComment");// 最后执行
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
		/**
		 * 按默认顺序执行，必须按先后顺序，否则引起错乱
		 */
		for (String hander : handerList) {
			if (handlers.contains(hander)) {
				if (handlerMap.get(hander) != null) {
					logger.debug(nameMap.get(hander) +"开始处理...");
					long start = System.currentTimeMillis();
					WorkbookHandler preprocessor = handlerMap.get(hander);
					preprocessor.processWorkbook(wb, exportApp);
					long ts = System.currentTimeMillis() - start;
					logger.debug(nameMap.get(hander) + "用时(ms):" + ts);
				}
			}
		}
	}

	private WorkbookFactory() {

	}

}
