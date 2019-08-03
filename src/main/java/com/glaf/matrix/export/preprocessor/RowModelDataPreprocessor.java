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

package com.glaf.matrix.export.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.util.Paging;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportItem;
import com.glaf.matrix.export.domain.RowModel;

public class RowModelDataPreprocessor implements DataXPreprocessor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List copy(List datalist) {
		List newlist = new ArrayList();
		newlist.addAll(datalist);
		return newlist;
	}

	protected List<Map<String, Object>> copy(List<Map<String, Object>> datalist, int size) {
		List<Map<String, Object>> newlist = new ArrayList<Map<String, Object>>();
		newlist.addAll(datalist);
		int len = size - datalist.size();
		logger.debug("补空记录数:" + len + "-------------------------------------------");
		for (int i = 0; i < len; i++) {
			newlist.add(new HashMap<String, Object>());
		}
		return newlist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void preprocess(Map<String, Object> parameter, ExportApp exportApp) {
		for (ExportItem item : exportApp.getItems()) {
			logger.debug("item->" + item.toJsonObject().toJSONString());
			if (item.getRowSize() > 0 && item.getColSize() > 0) {
				List<Paging> pagingList = new ArrayList<Paging>();
				List<Object> rowModels = new ArrayList<Object>();
				List<Map<String, Object>> rowList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> dataMap : item.getDataList()) {
					rowList.add(dataMap);
					if (rowList.size() == item.getColSize()) {
						RowModel rowModel = new RowModel();
						rowModel.setColSize(rowList.size());
						rowModel.setItems(this.copy(rowList));
						rowModels.add(rowModel);
						rowList.clear();
					}
					/**
					 * 判断是否满一页
					 */
					if (rowModels.size() == item.getRowSize()) {
						Paging paging = new Paging();
						paging.setRows(this.copy(rowModels));
						pagingList.add(paging);
						rowModels.clear();
					}
				}

				RowModel rowModel = new RowModel();
				rowModel.setItems(this.copy(rowList));

				if (rowList.size() > 0 && StringUtils.equals(item.getGenEmptyFlag(), "Y")) {
					logger.debug("生成空行数据填充......");
					if (rowList.size() != item.getColSize()) {
						rowModel.setItems(this.copy(rowList, item.getColSize()));
					}
				}
				rowModel.setColSize(rowModel.getItems().size());
				rowModels.add(rowModel);

				Paging paging = new Paging();
				paging.setRows(this.copy(rowModels));
				pagingList.add(paging);
				rowModels.clear();

				logger.debug("page size:" + pagingList.size());
				parameter.put(item.getName() + "_paging", pagingList);
			}
		}
	}

}
