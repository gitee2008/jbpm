package com.glaf.matrix.export.bean;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.glaf.core.base.DataFile;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.UUID32;
import com.glaf.matrix.data.domain.DataFileEntity;
import com.glaf.matrix.data.factory.DataFileFactory;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.domain.ExportFileHistory;

public class ExportDataSaveBean {
	protected final static Log logger = LogFactory.getLog(ExportDataSaveBean.class);

	public void saveAll(ExportApp exportApp, List<ExportFileHistory> list) {
		String date_str = String.valueOf(DateUtils.getNowYearMonthDayHHmmss());
		for (ExportFileHistory exportFileHistory : list) {
			if (StringUtils.isEmpty(exportFileHistory.getId())) {
				exportFileHistory.setId(date_str + "_" + UUID32.generateShortUuid());
			}
		}

		if (StringUtils.equals(exportApp.getSaveDataFlag(), "Y")) {
			for (ExportFileHistory model : list) {
				if (model.getData() != null) {
					DataFile dataFile = new DataFileEntity();
					dataFile.setId(model.getId());
					dataFile.setCreateBy(model.getCreateBy());
					dataFile.setCreateDate(model.getCreateTime());
					dataFile.setFilename(model.getFilename());
					dataFile.setPath(model.getPath());
					dataFile.setData(model.getData());
					dataFile.setServiceKey("sys_export");
					dataFile.setBusinessKey(model.getExpId());
					dataFile.setLastModified(model.getLastModified());
					dataFile.setSize(model.getData().length);
					dataFile.setStatus(9);
					dataFile.setType("export");
					try {
						DataFileFactory.getInstance().insertDataFile(null, dataFile, model.getData());
					} catch (Exception ex) {
						logger.error(ex);
					}
				}
			}
		}
	}

}
