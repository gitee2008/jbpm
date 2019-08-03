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

package com.glaf.matrix.export.bean;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.glaf.core.config.SystemConfig;
import com.glaf.core.util.ZipUtils;
import com.glaf.matrix.export.domain.ExportApp;
import com.glaf.matrix.export.util.HttpPostUtils;

public class PDFConverterBean {

	/**
	 * Excel转PDF
	 * 
	 * @param provider  工具类型 (aspose,wps,office)
	 * @param inputData 字节流（Excel或压缩的多个Excel流）
	 * @param fileExt   文件扩展名
	 * @return
	 */
	public byte[] convert(String provider, byte[] inputData, String fileExt) {
		String url = SystemConfig.getString("pdf_convert_url");
		if (StringUtils.isNotEmpty(url)) {
			String filename = "export." + fileExt;
			if (StringUtils.contains(url, "?")) {
				url = url + "&fileExt=" + fileExt;
			} else {
				url = url + "?fileExt=" + fileExt;
			}
			if (StringUtils.equals(provider, "wps")) {
				url = url + "?q=1&provider=wps";
			}
			byte[] bytes = HttpPostUtils.execute(url, filename, inputData);
			return bytes;
		}
		return null;
	}

	public byte[] convert(ExportApp exportApp, byte[] inputData, String fileExt) {
		String url = SystemConfig.getString("pdf_convert_url");
		if (StringUtils.isNotEmpty(url)) {
			String filename = "export." + fileExt;
			if (StringUtils.contains(url, "?")) {
				url = url + "&fileExt=" + fileExt;
			} else {
				url = url + "?fileExt=" + fileExt;
			}
			if (StringUtils.equals(exportApp.getExportPDFTool(), "wps")) {
				url = url + "?q=1&provider=wps";
			}
			byte[] bytes = HttpPostUtils.execute(url, filename, inputData);
			return bytes;
		}
		return null;
	}

	public byte[] merge(ExportApp exportApp, Map<String, byte[]> bytesMap, String fileExt) {
		byte[] data = ZipUtils.toZipBytes(bytesMap);
		String url = SystemConfig.getString("pdf_convert_url");
		if (StringUtils.isNotEmpty(url)) {
			String filename = "export.zip";
			if (StringUtils.contains(url, "?")) {
				url = url + "&fileExt=" + fileExt;
			} else {
				url = url + "?fileExt=" + fileExt;
			}
			if (StringUtils.equals(exportApp.getExportPDFTool(), "wps")) {
				url = url + "?q=1&provider=wps";
			}
			byte[] bytes = HttpPostUtils.execute(url, filename, data);
			return bytes;
		}
		return null;
	}

}
