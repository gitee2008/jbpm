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

package com.glaf.jxls.ext.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageListModel {

	protected int pageNo;

	protected List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	protected List<DataModel> datalist = new ArrayList<DataModel>();

	public PageListModel() {

	}

	public List<DataModel> getDatalist() {
		return datalist;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setDatalist(List<DataModel> datalist) {
		this.datalist = datalist;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

}
