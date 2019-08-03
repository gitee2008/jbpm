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

package com.glaf.matrix.export.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 每行的记录集
 * 
 */
@SuppressWarnings("rawtypes")
public class RowModel {

	protected int colSize;

	protected List items = new ArrayList();

	protected List items1 = new ArrayList();

	protected List items2 = new ArrayList();

	protected List items3 = new ArrayList();

	protected List items4 = new ArrayList();

	protected List items5 = new ArrayList();

	protected List items6 = new ArrayList();

	protected List items7 = new ArrayList();

	protected List items8 = new ArrayList();

	public RowModel() {

	}

	public int getColSize() {
		return colSize;
	}

	public List getItems() {
		return items;
	}

	public List getItems1() {
		return items1;
	}

	public List getItems2() {
		return items2;
	}

	public List getItems3() {
		return items3;
	}

	public List getItems4() {
		return items4;
	}

	public List getItems5() {
		return items5;
	}

	public List getItems6() {
		return items6;
	}

	public List getItems7() {
		return items7;
	}

	public List getItems8() {
		return items8;
	}

	public void setColSize(int colSize) {
		this.colSize = colSize;
	}

	public void setItems(List items) {
		this.items = items;
	}

	public void setItems1(List items1) {
		this.items1 = items1;
	}

	public void setItems2(List items2) {
		this.items2 = items2;
	}

	public void setItems3(List items3) {
		this.items3 = items3;
	}

	public void setItems4(List items4) {
		this.items4 = items4;
	}

	public void setItems5(List items5) {
		this.items5 = items5;
	}

	public void setItems6(List items6) {
		this.items6 = items6;
	}

	public void setItems7(List items7) {
		this.items7 = items7;
	}

	public void setItems8(List items8) {
		this.items8 = items8;
	}

}
