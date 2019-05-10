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

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

 
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;

import com.glaf.core.config.SystemConfig;
import com.glaf.core.domain.ServerEntity;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.FileUtils;
import com.glaf.core.util.Hex;
import com.glaf.core.util.IOUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.UUID32;
import com.glaf.matrix.export.domain.ExportChart;

public class ExportChartTask extends RecursiveTask<byte[]> {
	protected static final Log logger = LogFactory.getLog(ExportChartTask.class);

	private static final long serialVersionUID = 1L;

	protected ExportChart chart;

	protected Map<String, Object> parameter;

	protected ServerEntity serverEntity;

	static {

	}

	public ExportChartTask(ServerEntity serverEntity, ExportChart chart, Map<String, Object> parameter) {
		this.serverEntity = serverEntity;
		this.chart = chart;
		this.parameter = parameter;
	}

	protected AsyncHttpClientConfig.Builder getBuilder() {
		AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
		builder.setMaxRequestRetry(3);
		builder.setConnectTimeout(5000);
		builder.setCompressionEnforced(false);
		builder.setAllowPoolingSslConnections(false);
		builder.setAllowPoolingConnections(true);
		builder.setFollowRedirect(true);
		builder.setMaxConnectionsPerHost(50);
		builder.setMaxConnections(5000);
		builder.setAcceptAnyCertificate(true);
		return builder;
	}

	@Override
	protected byte[] compute() {
		String requestUrl = serverEntity.getPath();
		String address = chart.getChartUrl();
		boolean isSSL = false;
		if (StringUtils.startsWithIgnoreCase(requestUrl, "https://")) {
			isSSL = true;
		}

		JSONObject jsonObject = new JSONObject();
		Set<Entry<String, Object>> entrySet = parameter.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (key != null && value != null) {
				if (value instanceof Integer) {
					jsonObject.put(key, value);
				} else if (value instanceof Long) {
					jsonObject.put(key, value);
				} else if (value instanceof Double) {
					jsonObject.put(key, value);
				} else if (value instanceof Date) {
					jsonObject.put(key, value);
				} else if (value instanceof String) {
					jsonObject.put(key, value);
				}
			}
		}
		long start = System.currentTimeMillis();
		try {
			String param_hex = Hex.byte2hex(jsonObject.toJSONString().getBytes("UTF-8"));
			if (StringUtils.isEmpty(address)) {
				String chartType = "echarts";
				if (StringUtils.isNotEmpty(chart.getChartType())) {
					chartType = chart.getChartType();
				}
				if (StringUtils.isNotEmpty(chart.getChartId())) {
					address = SystemConfig.getServiceUrl() + "/website/public/" + chartType + "/showChart?chartId="
							+ chart.getChartId() + "&json_params_hex=" + param_hex;
				} else {
					address = StringTools.replace(address, "#{service_url}", SystemConfig.getServiceUrl());
					if (StringUtils.isNotEmpty(SystemConfig.getString("app_url"))) {
						address = StringTools.replace(address, "#{app_url}", SystemConfig.getString("app_url"));
					}
				}
			} else {
				if (StringUtils.containsAny(address, "?")) {
					address += "&json_params_hex=" + param_hex + "&_rand=" + UUID32.generateShortUuid();
				} else {
					address += "?json_params_hex=" + param_hex + "&_rand=" + UUID32.generateShortUuid();
				}
			}
			logger.debug("requestUrl:" + requestUrl);
			logger.debug("截图地址:" + address);
			byte[] bytes = this.doPost(chart, requestUrl, "POST", address, parameter, isSSL);
			return bytes;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			long ts = System.currentTimeMillis() - start;
			logger.debug("截图用时(ms):" + ts);
		}
	}

	public byte[] doPost(ExportChart chart, String requestUrl, String requestMethod, String address,
			Map<String, Object> parameter, boolean isSSL) {
		byte[] bytes = null;
		AsyncHttpClient client = null;
		InputStream inputStream = null;

		StringBuilder buffer = new StringBuilder();
		buffer.append(requestUrl);
		if (!StringUtils.containsAny(requestUrl, "?")) {
			buffer.append("?q=1");
		}
		// buffer.append("&address=").append(address);
		logger.debug("->address:" + address);

		try {

			buffer.append("&address_hex=").append(Hex.byte2hex(address.getBytes("UTF-8")));
			buffer.append("&height=").append(String.valueOf(chart.getHeight()));
			buffer.append("&width=").append(String.valueOf(chart.getWidth()));
			buffer.append("&imageType=").append(chart.getImageType());
			buffer.append("&_rand=").append(UUID32.generateShortUuid());

			Set<Entry<String, Object>> entrySet = parameter.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (key != null && value != null) {
					if (value instanceof Integer) {
						buffer.append("&").append(key).append(value.toString());
					} else if (value instanceof Long) {
						buffer.append("&").append(key).append(value.toString());
					} else if (value instanceof Double) {
						buffer.append("&").append(key).append(value.toString());
					} else if (value instanceof Date) {
						Date date = (Date) value;
						String val = DateUtils.getDateTime(date);
						buffer.append("&").append(key).append(val);
					} else if (value instanceof String) {
						buffer.append("&").append(key).append(value.toString());
					}
				}
			}

			if (isSSL) {
				// 创建SSLContext对象，并使用我们指定的信任管理器初始化
				javax.net.ssl.TrustManager[] tm = { new com.glaf.core.util.MyX509TrustManager() };
				javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("SSL", "SunJSSE");
				sslContext.init(null, tm, new java.security.SecureRandom());
				getBuilder().setSSLContext(sslContext);
				client = new AsyncHttpClient(getBuilder().build());
			} else {
				client = new AsyncHttpClient(getBuilder().build());
			}
			Future<Response> future = null;
			if ("GET".equalsIgnoreCase(requestMethod)) {
				future = client.prepareGet(buffer.toString()).execute();
			} else {
				logger.debug("准备请求......");
				logger.debug(buffer.toString());
				future = client.preparePost(buffer.toString()).execute();
			}
			Response response = (Response) future.get();
			inputStream = response.getResponseBodyAsStream();
			bytes = FileUtils.getBytes(inputStream);
			if (bytes.length > 0) {
				logger.debug("请求完成,获取到字节流大小:" + bytes.length);
				return bytes;
			} else {
				logger.debug("请求未获取到字节流.");
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.error("http request error:{}", ex);
		} finally {
			IOUtils.closeQuietly(inputStream);
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

}
