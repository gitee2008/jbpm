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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.matrix.export.domain.ExportApp;

public class AutoPageBreakHandler implements WorkbookHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void processWorkbook(Workbook wb, ExportApp exportApp) {
		logger.debug("----------------------AutoPageBreakHandler-----------------");
		float pageHeight = exportApp.getPageHeight();
		logger.debug("设置页高:" + pageHeight);
		if (pageHeight >= 400 && pageHeight <= 1000) {
			int sheetCnt = wb.getNumberOfSheets();
			Row row = null;
			float totalHeight = 0.0f;
			for (int i = 0; i < sheetCnt; i++) {
				Sheet sheet = wb.getSheetAt(i);
				int rows = sheet.getLastRowNum();
				//logger.debug("行记录数:" + (rows + 1));
				for (int rowIndex = 0; rowIndex <= rows; rowIndex++) {
					row = sheet.getRow(rowIndex);
					if (row == null) {
						continue;
					}
					//logger.debug("rowIndex:" + rowIndex + " height:" + row.getHeightInPoints());
					totalHeight = totalHeight + row.getHeightInPoints();
					float sumHeight = totalHeight + getRowHeight(sheet.getRow(rowIndex + 1));
					//logger.debug(rowIndex + "->sumHeight:" + sumHeight);
					if (sumHeight > pageHeight) {
						totalHeight = 0f;
						sheet.setRowBreak(rowIndex); // 设置分页符
						//logger.debug(">>>>在第" + rowIndex + "行设置分页符.");
					}
				}
			}
		}
	}

	public float getRowHeight(Row row) {
		if (row != null) {
			return row.getHeightInPoints();
		}
		return 0f;
	}
}
