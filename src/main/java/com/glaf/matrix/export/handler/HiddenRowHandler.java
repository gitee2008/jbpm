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

import org.apache.commons.lang3.StringUtils;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.matrix.export.domain.ExportApp;

public class HiddenRowHandler implements WorkbookHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void processWorkbook(Workbook wb, ExportApp exportApp) {
		logger.debug("----------------------HiddenRowHandler-------------------");
		int sheetCnt = wb.getNumberOfSheets();
		Row row = null;
		Cell cell = null;
		for (int i = 0; i < sheetCnt; i++) {
			Sheet sheet = wb.getSheetAt(i);
			sheet.setAutobreaks(false);
			int rows = sheet.getLastRowNum();
			for (int rowIndex = 0; rowIndex <= rows; rowIndex++) {
				row = sheet.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				cell = row.getCell(0);
				if (cell == null) {
					continue;
				}
				if (cell.getCellComment() != null) {
					String str = cell.getCellComment().getString().getString();
					// logger.debug("读取到注释:" + str);
					if (str != null) {
						if (StringUtils.contains(str.trim(), "xe:hiddenRow")) {
							// sheet.setRowBreak(rowIndex); // 设置分页符
							// logger.debug(rowIndex + "行插入分页符.");
							// cell.removeCellComment();
							row.setHeightInPoints(0f);
						}
					}
				} else {
					String str = cell.getRichStringCellValue().getString();
					if (str != null) {
						if (StringUtils.contains(str.trim(), "xe:hiddenRow")) {
							// logger.debug("读取到分页标识:" + str);
							cell.setCellValue("");
							row.setHeightInPoints(0f);
							// sheet.setRowBreak(rowIndex); // 设置分页符
							// logger.debug(rowIndex + "行插入分页符!");
						}
					}
				}
			}
		}
	}

}
