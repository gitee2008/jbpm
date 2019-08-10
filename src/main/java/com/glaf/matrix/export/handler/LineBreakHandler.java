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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.util.ExcelUtils;
import com.glaf.core.util.StringTools;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.util.Constants;

/**
 * 自动换行处理器，自动将$$LSP$$变成换行符进行换行。
 *
 */
public class LineBreakHandler implements WorkbookHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public final static String newline = System.getProperty("line.separator");

	@Override
	public void processWorkbook(Workbook wb, ExportApp exportApp) {
		logger.debug("----------------------LineBreakHandler-------------------");
		int rowCnt = 0;
		int colCnt = 0;
		Row row = null;
		String cellVal = null;
		int sheetCnt = wb.getNumberOfSheets();
		for (int i = 0; i < sheetCnt; i++) {
			Sheet sheet = wb.getSheetAt(i);
			sheet.setAutobreaks(false);
			rowCnt = sheet.getLastRowNum();
			for (int rowIndex = 0; rowIndex <= rowCnt; rowIndex++) {
				if (rowIndex % 100 == 0) {
					// logger.debug("准备处理第" + rowIndex + "行...");
				}
				row = sheet.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				colCnt = row.getLastCellNum();
				// logger.debug("列数:" + colCnt);
				for (int k = 0; k < colCnt; k++) {
					Cell cell = row.getCell(k);
					if (cell == null) {
						continue;
					}
					if (cell.getCellComment() != null) {
						String str = cell.getCellComment().getString().getString();
						// logger.debug("读取到批注:" + str);
						if (StringUtils.isNotEmpty(str) && StringUtils.contains(str.trim(), "xe:linebreak")) {
							// cell.getCellComment().setVisible(false);
							cell.removeCellComment();
							cellVal = ExcelUtils.getCellValue(cell);
							if (StringUtils.isNotEmpty(cellVal)) {
								cellVal = StringTools.replace(cellVal, Constants.LINE_SP, newline);
								cell.setCellValue(cellVal);
							}
						}
					}
				}
			}
		}
	}

}
