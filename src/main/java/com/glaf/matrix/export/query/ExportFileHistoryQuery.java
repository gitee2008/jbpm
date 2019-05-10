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

package com.glaf.matrix.export.query;

import java.util.*;
import com.glaf.core.query.DataQuery;

public class ExportFileHistoryQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	protected String expId;
	protected String deploymentId;
	protected String jobNo;
	protected Integer genYmd;
	protected Date createTimeGreaterThanOrEqual;
	protected Date createTimeLessThanOrEqual;

	public ExportFileHistoryQuery() {

	}

	public ExportFileHistoryQuery createBy(String createBy) {
		if (createBy == null) {
			throw new RuntimeException("createBy is null");
		}
		this.createBy = createBy;
		return this;
	}

	public ExportFileHistoryQuery createTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		if (createTimeGreaterThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
		return this;
	}

	public ExportFileHistoryQuery createTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		if (createTimeLessThanOrEqual == null) {
			throw new RuntimeException("createTime is null");
		}
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
		return this;
	}

	public ExportFileHistoryQuery deploymentId(String deploymentId) {
		if (deploymentId == null) {
			throw new RuntimeException("deploymentId is null");
		}
		this.deploymentId = deploymentId;
		return this;
	}

	public ExportFileHistoryQuery expId(String expId) {
		if (expId == null) {
			throw new RuntimeException("expId is null");
		}
		this.expId = expId;
		return this;
	}

	public ExportFileHistoryQuery genYmd(Integer genYmd) {
		if (genYmd == null) {
			throw new RuntimeException("genYmd is null");
		}
		this.genYmd = genYmd;
		return this;
	}

	public Date getCreateTimeGreaterThanOrEqual() {
		return createTimeGreaterThanOrEqual;
	}

	public Date getCreateTimeLessThanOrEqual() {
		return createTimeLessThanOrEqual;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public String getExpId() {
		return expId;
	}

	public Integer getGenYmd() {
		return genYmd;
	}

	public String getJobNo() {
		return jobNo;
	}

	public String getOrderBy() {
		if (sortColumn != null) {
			String a_x = " asc ";
			if (sortOrder != null) {
				a_x = sortOrder;
			}

			if ("expId".equals(sortColumn)) {
				orderBy = "E.EXPID_" + a_x;
			}

			if ("deploymentId".equals(sortColumn)) {
				orderBy = "E.DEPLOYMENTID_" + a_x;
			}

			if ("title".equals(sortColumn)) {
				orderBy = "E.TITLE_" + a_x;
			}

			if ("filename".equals(sortColumn)) {
				orderBy = "E.FILENAME_" + a_x;
			}

			if ("path".equals(sortColumn)) {
				orderBy = "E.PATH_" + a_x;
			}

			if ("jobNo".equals(sortColumn)) {
				orderBy = "E.JOBNO_" + a_x;
			}

			if ("genYmd".equals(sortColumn)) {
				orderBy = "E.GENYMD_" + a_x;
			}

			if ("sortNo".equals(sortColumn)) {
				orderBy = "E.SORTNO_" + a_x;
			}

			if ("viewCount".equals(sortColumn)) {
				orderBy = "E.VIEWCOUNT_" + a_x;
			}

			if ("createBy".equals(sortColumn)) {
				orderBy = "E.CREATEBY_" + a_x;
			}

			if ("createTime".equals(sortColumn)) {
				orderBy = "E.CREATETIME_" + a_x;
			}

		}
		return orderBy;
	}

	@Override
	public void initQueryColumns() {
		super.initQueryColumns();
		addColumn("id", "ID_");
		addColumn("expId", "EXPID_");
		addColumn("deploymentId", "DEPLOYMENTID_");
		addColumn("title", "TITLE_");
		addColumn("filename", "FILENAME_");
		addColumn("path", "PATH_");
		addColumn("jobNo", "JOBNO_");
		addColumn("genYmd", "GENYMD_");
		addColumn("sortNo", "SORTNO_");
		addColumn("viewCount", "VIEWCOUNT_");
		addColumn("createBy", "CREATEBY_");
		addColumn("createTime", "CREATETIME_");
	}

	public ExportFileHistoryQuery jobNo(String jobNo) {
		if (jobNo == null) {
			throw new RuntimeException("jobNo is null");
		}
		this.jobNo = jobNo;
		return this;
	}

	public void setCreateTimeGreaterThanOrEqual(Date createTimeGreaterThanOrEqual) {
		this.createTimeGreaterThanOrEqual = createTimeGreaterThanOrEqual;
	}

	public void setCreateTimeLessThanOrEqual(Date createTimeLessThanOrEqual) {
		this.createTimeLessThanOrEqual = createTimeLessThanOrEqual;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public void setExpId(String expId) {
		this.expId = expId;
	}

	public void setGenYmd(Integer genYmd) {
		this.genYmd = genYmd;
	}

	public void setJobNo(String jobNo) {
		this.jobNo = jobNo;
	}

}