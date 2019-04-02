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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.matrix.export.domain.ExportApp;

public class CellMergeHandler implements WorkbookHandler {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public String getCellValue(Cell cell) {
		if (cell != null && cell.getCellComment() != null) {
			if (StringUtils.contains(cell.getCellComment().getString().getString(), "mergeCellHorz")) {
				return cell.getStringCellValue();
			}
		}
		return null;
	}

	public String getCellValue(Row row, int colIndex) {
		if (row != null) {
			Cell cell = row.getCell(colIndex);
			if (cell != null && cell.getCellComment() != null) {
				if (StringUtils.contains(cell.getCellComment().getString().getString(), "mergeCell")) {
					return cell.getStringCellValue();
				}
			}
		}
		return null;
	}

	public void mergeHorizontal(Workbook wb, ExportApp exportApp) {
		int sheetCnt = wb.getNumberOfSheets();
		for (int i = 0; i < sheetCnt; i++) {
			Sheet sheet = wb.getSheetAt(i);
			int rows = sheet.getLastRowNum();
			logger.debug("行记录数:" + (rows + 1));
			Row row = null;
			Cell cell = null;
			Cell tmpCell = null;
			int endCol = 0;
			Map<String, Integer> mergeMap = new HashMap<String, Integer>();
			for (int rowIndex = 0; rowIndex <= rows; rowIndex++) {
				row = sheet.getRow(rowIndex);
				logger.debug("rowIndex:" + rowIndex);
				if (row == null) {
					continue;
				}
				int cols = row.getLastCellNum();
				for (int colIndex = 0; colIndex < cols; colIndex++) {
					cell = row.getCell(colIndex);
					if (cell == null) {
						continue;
					}
					if (mergeMap.get(rowIndex + "_" + colIndex) != null) {
						continue;
					}
					endCol = colIndex;
					if (cell.getCellComment() != null) {
						String value = cell.getStringCellValue();
						if (StringUtils.contains(cell.getCellComment().getString().getString(), "mergeCellHorz")) {
							logger.debug(rowIndex + "行" + colIndex + "列读取到合并注释:"
									+ cell.getCellComment().getString().getString());
							endCol = colIndex;
							boolean mergeFlag = false;
							while (endCol < cols) {
								tmpCell = row.getCell(++endCol);
								String nextVal = getCellValue(tmpCell);
								if (nextVal == null) {
									--endCol;
									break;
								}
								if (StringUtils.equals(nextVal, value)) {
									mergeFlag = true;
									mergeMap.put(rowIndex + "_" + colIndex, colIndex);
									mergeMap.put(rowIndex + "_" + endCol, endCol);
									logger.debug("endCol:" + endCol);
								} else {
									--endCol;
									break;
								}
							}
							if (mergeFlag) {
								logger.debug("准备合并单元格值:" + value);
								try {
									String ikey = rowIndex + "_" + endCol;
									int newColIndex = endCol;
									if (mergeMap.get(ikey) != null) {
										newColIndex = mergeMap.get(ikey);
									}
									logger.debug("startRow:" + rowIndex + "->endRow:" + rowIndex);
									logger.debug("startCol:" + colIndex + "->endCol:" + newColIndex);
									// Thread.sleep(20);
									CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, colIndex,
											newColIndex);
									sheet.addMergedRegion(region);
									if (StringUtils.equals(cell.getCellComment().getString().getString().trim(),
											"mergeCellHorz")) {
										cell.removeCellComment();
									}
								} catch (Exception ex) {
									// e.printStackTrace();
									logger.error("merged region error", ex);
								}
							}
						}
					}
				}
			}
		}
	}

	public void mergeVertical(Workbook wb, ExportApp exportApp) {
		int sheetCnt = wb.getNumberOfSheets();
		for (int i = 0; i < sheetCnt; i++) {
			Sheet sheet = wb.getSheetAt(i);
			int rows = sheet.getLastRowNum();
			logger.debug("行记录数:" + (rows + 1));
			Row row = null;
			Row tmpRow = null;
			Cell cell = null;
			int endRow = 0;
			int[] rowBreaks = sheet.getRowBreaks();
			List<Integer> breaks = new ArrayList<Integer>();
			if (rowBreaks != null && rowBreaks.length > 0) {
				for (int xbreak : rowBreaks) {
					breaks.add(xbreak);
				}
			}
			Map<String, Integer> mergeMap = new HashMap<String, Integer>();
			for (int rowIndex = 0; rowIndex <= rows; rowIndex++) {
				if (breaks.contains(rowIndex)) {// 遇到分页符，跳过
					logger.debug(">>>>跳过分页符:" + rowIndex);
					continue;
				}
				row = sheet.getRow(rowIndex);
				// logger.debug("rowIndex:" + rowIndex);
				endRow = rowIndex;
				if (row == null) {
					continue;
				}
				int cols = row.getLastCellNum();
				for (int colIndex = 0; colIndex < cols; colIndex++) {
					cell = row.getCell(colIndex);
					if (cell == null) {
						continue;
					}
					if (mergeMap.get(rowIndex + "_" + colIndex) != null) {
						continue;
					}
					if (cell.getCellComment() != null) {
						String value = cell.getStringCellValue();
						if (StringUtils.contains(cell.getCellComment().getString().getString(), "mergeCell")) {
							logger.debug(rowIndex + "行" + colIndex + "列读取到合并注释:"
									+ cell.getCellComment().getString().getString());
							endRow = rowIndex;
							boolean mergeFlag = false;
							while (endRow < rows) {
								if (breaks.contains(endRow)) {// 遇到分页符，跳过
									logger.debug(">>>>@跳过分页符:" + endRow);
									break;
								}
								tmpRow = sheet.getRow(++endRow);
								String nextVal = getCellValue(tmpRow, colIndex);
								if (nextVal == null) {
									--endRow;
									break;
								}
								if (StringUtils.equals(nextVal, value)) {
									mergeFlag = true;
									mergeMap.put(rowIndex + "_" + colIndex, colIndex);
									mergeMap.put(endRow + "_" + colIndex, colIndex);
								} else {
									--endRow;
									break;
								}
							}
							if (mergeFlag) {
								logger.debug("准备合并单元格值:" + value);
								try {
									String ikey = rowIndex + "_" + colIndex;
									int newColIndex = colIndex;
									if (mergeMap.get(ikey) != null) {
										newColIndex = mergeMap.get(ikey);
									}
									logger.debug("rowIndex:" + rowIndex + "->endRow:" + endRow);
									logger.debug("colIndex:" + colIndex + "->newColIndex:" + newColIndex);
									// Thread.sleep(20);
									CellRangeAddress region = new CellRangeAddress(rowIndex, endRow, colIndex,
											newColIndex);
									sheet.addMergedRegion(region);
									if (StringUtils.equals(cell.getCellComment().getString().getString().trim(),
											"mergeCell")) {
										cell.removeCellComment();
									}
								} catch (Exception ex) {
									// ex.printStackTrace();
									logger.error("merged region error", ex);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void processWorkbook(Workbook wb, ExportApp exportApp) {
		logger.debug("----------------------CellMergeHandler-------------------");
		this.mergeHorizontal(wb, exportApp);
		this.mergeVertical(wb, exportApp);
	}

}
