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

package com.glaf.matrix.export.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;

import com.glaf.core.util.IOUtils;

public class PDFUtils {
	protected final static Log logger = LogFactory.getLog(PDFUtils.class);

	/**
	 * 将多个PDF文档合并成一个
	 *
	 * @param fileList
	 * @param dest
	 * @throws IOException
	 */
	public static void execute(List<File> fileList, File dest) throws IOException {
		PdfDocument targetPdf = new PdfDocument(new PdfWriter(dest));
		PdfMerger merger = new PdfMerger(targetPdf);
		int startPage = 1;
		int toPage = 0;
		PdfDocument sourcePdf = null;
		for (File srcFle : fileList) {
			logger.debug("准备合并：" + srcFle.getAbsolutePath());
			try {
				sourcePdf = new PdfDocument(new PdfReader(srcFle));
				toPage = sourcePdf.getNumberOfPages();
				merger.merge(sourcePdf, 1, toPage);
				startPage = startPage + sourcePdf.getNumberOfPages();
			} catch (Exception ex) {
				////ex.printStackTrace();
				logger.error("merge pdf error", ex);
				throw new RuntimeException(ex);
			} finally {
				sourcePdf.close();
			}
		}
		targetPdf.close();
		logger.debug("合并后PDF文件大小:" + dest.length());
	}

	/**
	 * 将多个PDF文档合并成一个
	 *
	 * @param fileList
	 * @param dest
	 * @throws IOException
	 */
	public static byte[] merge(List<byte[]> fileList) {
		byte[] bytes = null;
		int startPage = 1;
		int toPage = 0;
		PdfDocument targetPdf = null;
		PdfDocument sourcePdf = null;
		BufferedInputStream bis = null;
		ByteArrayInputStream bais = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(baos);
			targetPdf = new PdfDocument(new PdfWriter(bos));
			PdfMerger merger = new PdfMerger(targetPdf);
			for (byte[] inputData : fileList) {
				// logger.debug("准备合并：" + srcFle.getAbsolutePath());
				try {
					bais = new ByteArrayInputStream(inputData);
					bis = new BufferedInputStream(bais);
					sourcePdf = new PdfDocument(new PdfReader(bis));
					toPage = sourcePdf.getNumberOfPages();
					merger.merge(sourcePdf, 1, toPage);
					startPage = startPage + sourcePdf.getNumberOfPages();
				} catch (Exception ex) {
					////ex.printStackTrace();
					logger.error("merge pdf error", ex);
					throw new RuntimeException(ex);
				} finally {
					IOUtils.closeStream(bais);
					IOUtils.closeStream(bis);
					if (sourcePdf != null) {
						sourcePdf.close();
					}
				}
			}
			bos.flush();
			baos.flush();
			targetPdf.close();// 先关闭PDF，再获取字节流
			bytes = baos.toByteArray();
		} catch (Exception ex) {
			////ex.printStackTrace();
			logger.error("merge pdf error", ex);
			throw new RuntimeException(ex);
		} finally {
			IOUtils.closeStream(baos);
			IOUtils.closeStream(bos);
			if (targetPdf != null) {
				targetPdf.close();
			}
		}
		if (bytes != null) {
			logger.debug("合并后PDF文件大小:" + bytes.length);
		}
		return bytes;
	}

	private PDFUtils() {

	}

	public static void main(String[] args) throws Exception {
		String targetDir = "C:/temp/pdf";
		File dir = new File(targetDir);
		File contents[] = dir.listFiles();
		if (contents != null) {
			List<File> fileList = new ArrayList<File>();
			int len = contents.length;
			for (int i = 0; i < len; i++) {
				if (contents[i].isFile()) {
					fileList.add(contents[i]);
				}
			}
			PDFUtils.execute(fileList, new File("C:/temp/target.pdf"));
		}
	}

}