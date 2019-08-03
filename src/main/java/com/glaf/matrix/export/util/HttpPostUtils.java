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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glaf.core.util.FileUtils;

public class HttpPostUtils {
	protected final static Logger logger = LoggerFactory.getLogger(HttpPostUtils.class);

	public static byte[] execute(String url, String filename, byte[] data) {
		logger.debug("准备调用远程服务：" + url);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		InputStream inputStream = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("file", data, ContentType.MULTIPART_FORM_DATA, filename);// 文件流

			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			CloseableHttpResponse response = httpClient.execute(httpPost);// 执行提交
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				inputStream = responseEntity.getContent();
				logger.debug("远程调用结束。");
				return FileUtils.getBytes(inputStream);
			}
		} catch (IOException ex) {
			////ex.printStackTrace();
		} catch (Exception ex) {
			////ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inputStream);
			try {
				httpClient.close();
			} catch (IOException ex) {
				////ex.printStackTrace();
			}
		}
		return null;
	}

}
