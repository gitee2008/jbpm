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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.JSONObject;
import com.glaf.jxls.ext.JxlsImage;

public class ListModel implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String groupId;

	protected String groupName;

	protected JSONObject jsonObject;

	protected List<DataModel> list = new ArrayList<DataModel>();

	protected List<JxlsImage> imagelist1 = new ArrayList<JxlsImage>();

	protected List<JxlsImage> imagelist2 = new ArrayList<JxlsImage>();

	protected List<JxlsImage> imagelist3 = new ArrayList<JxlsImage>();

	protected List<JxlsImage> imagelist4 = new ArrayList<JxlsImage>();

	protected List<JxlsImage> imagelist5 = new ArrayList<JxlsImage>();

	public ListModel() {

	}

	public void addDataModel(DataModel model) {
		if (list == null) {
			list = new ArrayList<DataModel>();
		}
		list.add(model);
	}

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public List<JxlsImage> getImagelist1() {
		return imagelist1;
	}

	public List<JxlsImage> getImagelist2() {
		return imagelist2;
	}

	public List<JxlsImage> getImagelist3() {
		return imagelist3;
	}

	public List<JxlsImage> getImagelist4() {
		return imagelist4;
	}

	public List<JxlsImage> getImagelist5() {
		return imagelist5;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public List<DataModel> getList() {
		return list;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setImagelist1(List<JxlsImage> imagelist1) {
		this.imagelist1 = imagelist1;
	}

	public void setImagelist2(List<JxlsImage> imagelist2) {
		this.imagelist2 = imagelist2;
	}

	public void setImagelist3(List<JxlsImage> imagelist3) {
		this.imagelist3 = imagelist3;
	}

	public void setImagelist4(List<JxlsImage> imagelist4) {
		this.imagelist4 = imagelist4;
	}

	public void setImagelist5(List<JxlsImage> imagelist5) {
		this.imagelist5 = imagelist5;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public void setList(List<DataModel> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
