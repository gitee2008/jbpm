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

package com.glaf.core.tree.component;

import java.util.Date;

public abstract class TreeBase implements Component, java.lang.Comparable<TreeBase> {
	// ~ Instance fields
	// ========================================================

	/** Holds value of property action, that is, Struts Logical Action Name. */
	protected String action;

	/**
	 * Align menu 'left','right','top','bottom' ...and other alignment of particular
	 * menu system
	 */
	protected String align;

	/** Holds value of property altImage. */
	protected String altImage;

	protected boolean checked;

	private String cls;

	protected String code;

	/** Holds value of property description. */
	protected String description;

	/** Holds value of property forward. */
	protected String forward;

	/** Holds value of property height. */
	protected String height;

	/** Holds value of property name. */
	protected String id;

	/** Holds value of property image. */
	protected String image;

	/** Holds value of property location. */
	protected String location;

	/**
	 * Holds value of property module; a Struts module prefix that overrides the
	 * current module.
	 * 
	 * <p>
	 * The default module is specified by <code>""</code>. Any non-default module
	 * should begin with <code>"/"</code>.
	 */
	protected String module;

	/** Holds value of property onclick. */
	protected String onclick;

	/** Holds value of property onContextTree */
	protected String onContextTree;

	/** Holds value of property ondblclick. */
	protected String ondblclick;

	/** Holds value of property onmouseout. */
	protected String onmouseout;

	/** Holds value of property onmouseover. */
	protected String onmouseover;

	/** Holds value of property page. */
	protected String page;

	/** Holds value of property roles. */
	protected String roles;

	/** Holds value of property target. */
	protected String target;

	/** Holds value of property title. */
	protected String title;

	/** Holds value of property toolTip. */
	protected String toolTip;

	protected String treeId;

	/** Holds parsed (with variables) url that is used to render a link */
	private String url;

	/** Holds value of property width. */
	protected String width;

	protected int level;

	protected int sortNo;

	protected int locked;

	protected int intValue;

	protected long longValue;

	protected double doubleValue;

	protected Date dateValue;

	// ~ Methods
	// ================================================================

	public int compareTo(TreeBase o) {
		if (o == null) {
			return -1;
		}

		TreeBase obj = o;

		int l = this.sortNo - obj.getSortNo();

		int ret = 0;

		if (l > 0) {
			ret = 1;
		} else if (l < 0) {
			ret = -1;
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeBase other = (TreeBase) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Returns the value for action.
	 * 
	 * @return Value of property action.
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * Returns the value for align.
	 * 
	 * @return Value of property align.
	 */
	public String getAlign() {
		return align;
	}

	/**
	 * Getter for property altImage.
	 * 
	 * @return Value of property altImage.
	 */
	public String getAltImage() {
		return altImage;
	}

	public String getCls() {
		return cls;
	}

	public String getCode() {
		return code;
	}

	public Date getDateValue() {
		return dateValue;
	}

	/**
	 * Getter for property description.
	 * 
	 * @return Value of property description.
	 */
	public String getDescription() {
		return description;
	}

	public double getDoubleValue() {
		return doubleValue;
	}

	/**
	 * @return String
	 */
	public String getForward() {
		return forward;
	}

	/**
	 * @return
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * Getter for property name.
	 * 
	 * @return Value of property name.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Getter for property image.
	 * 
	 * @return Value of property image.
	 */
	public String getImage() {
		return image;
	}

	public int getIntValue() {
		return intValue;
	}

	public int getLevel() {
		return level;
	}

	/**
	 * Getter for property location.
	 * 
	 * @return Value of property location.
	 */
	public String getLocation() {
		return location;
	}

	public int getLocked() {
		return locked;
	}

	public long getLongValue() {
		return longValue;
	}

	public String getModule() {
		return module;
	}

	/**
	 * Getter for property onclick.
	 * 
	 * @return Value of property onclick.
	 */
	public String getOnclick() {
		return onclick;
	}

	/**
	 * @return
	 */
	public String getOnContextTree() {
		return onContextTree;
	}

	/**
	 * Returns the ondblclick.
	 * 
	 * @return String
	 */
	public String getOndblclick() {
		return ondblclick;
	}

	/**
	 * Getter for property onmouseout.
	 * 
	 * @return Value of property onmouseout.
	 */
	public String getOnmouseout() {
		return onmouseout;
	}

	/**
	 * Getter for property onmouseover.
	 * 
	 * @return Value of property onmouseover.
	 */
	public String getOnmouseover() {
		return onmouseover;
	}

	/**
	 * Returns the value for page.
	 * 
	 * @return Value of property page.
	 */
	public String getPage() {
		return this.page;
	}

	/**
	 * Returns the roles.
	 * 
	 * @return String
	 */
	public String getRoles() {
		return roles;
	}

	public int getSortNo() {
		return sortNo;
	}

	/**
	 * Getter for property target.
	 * 
	 * @return Value of property target.
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Getter for property title.
	 * 
	 * @return Value of property title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Getter for property toolTip.
	 * 
	 * @return Value of property toolTip.
	 */
	public String getToolTip() {
		return toolTip;
	}

	public String getTreeId() {
		return treeId;
	}

	/**
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return String
	 */
	public String getWidth() {
		return width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public boolean isChecked() {
		return checked;
	}

	/**
	 * Sets the value for action.
	 * 
	 * @param action New value of property action.
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Sets the value for align.
	 * 
	 * @param align New value of property align.
	 */
	public void setAlign(String align) {
		this.align = align;
	}

	/**
	 * Setter for property altImage.
	 * 
	 * @param altImage New value of property altImage.
	 */
	public void setAltImage(String altImage) {
		this.altImage = altImage;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	/**
	 * Setter for property description.
	 * 
	 * @param description New value of property description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}

	/**
	 * Sets the forward.
	 * 
	 * @param forward The forward to set
	 */
	public void setForward(String forward) {
		this.forward = forward;
	}

	/**
	 * @param height
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * Setter for property name.
	 * 
	 * @param name New value of property name.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Setter for property image.
	 * 
	 * @param image New value of property image.
	 */
	public void setImage(String image) {
		this.image = image;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Setter for property location.
	 * 
	 * @param location New value of property location.
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}

	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * Setter for property onclick.
	 * 
	 * @param onclick New value of property onclick.
	 */
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	/**
	 * @param string
	 */
	public void setOnContextTree(String string) {
		onContextTree = string;
	}

	/**
	 * Sets the ondblclick.
	 * 
	 * @param ondblclick The ondblclick to set
	 */
	public void setOndblclick(String ondblclick) {
		this.ondblclick = ondblclick;
	}

	/**
	 * Setter for property onmouseout.
	 * 
	 * @param onmouseout New value of property onmouseout.
	 */
	public void setOnmouseout(String onmouseout) {
		this.onmouseout = onmouseout;
	}

	/**
	 * Setter for property onmouseover.
	 * 
	 * @param onmouseover New value of property onmouseover.
	 */
	public void setOnmouseover(String onmouseover) {
		this.onmouseover = onmouseover;
	}

	/**
	 * Sets the value for page.
	 * 
	 * @param page New value of property page.
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * Sets the roles.
	 * 
	 * @param roles The roles to set
	 */
	public void setRoles(String roles) {
		this.roles = roles;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	/**
	 * Setter for property target.
	 * 
	 * @param target New value of property target.
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Setter for property title.
	 * 
	 * @param title New value of property title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Setter for property toolTip.
	 * 
	 * @param toolTip New value of property toolTip.
	 */
	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}

	/**
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Sets the width.
	 * 
	 * @param width The width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}
}