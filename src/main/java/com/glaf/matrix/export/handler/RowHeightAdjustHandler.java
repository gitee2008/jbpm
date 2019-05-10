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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.glaf.matrix.export.domain.ExportApp;

public class RowHeightAdjustHandler implements WorkbookHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void processWorkbook(Workbook wb, ExportApp exportApp) {
		logger.debug("----------------------RowHeightAdjustHandler-------------------");
		int rowCnt = 0;
		int colCnt = 0;
		Row row = null;
		String json = null;
		String cellVal = null;
		JSONObject jsonObject = null;
		int sheetCnt = wb.getNumberOfSheets();
		for (int i = 0; i < sheetCnt; i++) {
			Sheet sheet = wb.getSheetAt(i);
			sheet.setAutobreaks(false);
			rowCnt = sheet.getLastRowNum();
			for (int rowIndex = 0; rowIndex <= rowCnt; rowIndex++) {
				if (rowIndex % 100 == 0) {
					//logger.debug("准备处理第" + rowIndex + "行...");
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
						if (StringUtils.isNotEmpty(str) && StringUtils.contains(str.trim(), "xe:rh")) {
							// cell.getCellComment().setVisible(false);
							cell.removeCellComment();
							// logger.debug("删除批注信息.");
							int beginIndex = str.indexOf("xe:rh{");
							int endIndex = str.indexOf("}", beginIndex);
							json = str.substring(beginIndex + 5, endIndex + 1);
							// logger.debug("json:" + json);
							jsonObject = JSON.parseObject(json);
							int charNumPerRow = jsonObject.getIntValue("charNumPerRow");
							float lineHeight = jsonObject.getFloatValue("lineHeight");
							cellVal = cell.getStringCellValue();
							if (StringUtils.isEmpty(cellVal)) {
								cell.setCellValue("  ");
							}
							if (charNumPerRow > 0 && lineHeight > 10 && cellVal != null) {
								int factor = (int) Math.ceil(cellVal.length() * 1.0d / charNumPerRow * 1.0d);
								if (factor > 1) {
									row.setHeightInPoints(factor * lineHeight);
									// logger.debug(rowIndex + "->[" + cellVal + "]行高已经调整因子:" + factor);
									// break;
								}
							}
						}
					}
				}
			}
		}
	}

}
