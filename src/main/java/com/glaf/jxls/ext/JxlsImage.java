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
package com.glaf.jxls.ext;

import org.apache.poi.ss.usermodel.Workbook;
import org.jxls.common.ImageType;

/**
 * <p>
 * 插入excel图片处理对象
 * </p>
 * 使用 {@link JxlsUtil#getJxlsImage(String)} 获取
 * 
 */
public class JxlsImage {
	public static final String IMAGE_SIZE_TYPE_AUTO = "auto";
	public static final String IMAGE_SIZE_TYPE_ORIGINAL = "original";
	private byte[] pictureData;
	private String pictureType;

	public byte[] getPictureData() {
		return pictureData;
	}

	public void setPictureData(byte[] pictureData) {
		this.pictureData = pictureData;
	}

	public String getPictureType() {
		return pictureType;
	}

	public void setPictureType(String pictureType) {
		this.pictureType = pictureType;
	}

	public int getWorkbookImageType() {
		switch (pictureType.toUpperCase()) {
		case "PNG":
			return Workbook.PICTURE_TYPE_PNG;
		case "EMF":
			return Workbook.PICTURE_TYPE_EMF;
		case "WMF":
			return Workbook.PICTURE_TYPE_WMF;
		case "PICT":
			return Workbook.PICTURE_TYPE_PICT;
		case "DIB":
			return Workbook.PICTURE_TYPE_DIB;
		default:
			return Workbook.PICTURE_TYPE_JPEG;
		}
	}

	public ImageType getJxlsImageType() {
		if ("jpg".equalsIgnoreCase(pictureType)) {
			return ImageType.JPEG;
		}
		return ImageType.valueOf(pictureType);
	}
}
