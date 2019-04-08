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

import java.io.File;
import java.net.URL;

public class JxlsConfig {
	/** 模板存放目录 */
	private static String templateRoot;
	/** 默认图片目录 */
	private static String imageRoot;
	/** 忽略warn警告 */
	private static boolean silent = false;
	/** 锁住配置 */
	private static boolean lock = false;

	static {
		JxlsConfig.config()
		// 锁住配置，不能再修改
//                .lock()
				// 设置模板文件根目录
				.templateRoot("jxls_templates")
				// 设置图片路径根目录
				.imageRoot("jxls_images");
	}

	private JxlsConfig() {
	}

	public static JxlsConfig config() {
		if (lock) {
			throw new IllegalArgumentException("jxls配置已设置锁住，不能再修改");
		}
		return new JxlsConfig();
	}

	public JxlsConfig templateRoot(String templateRoot) {
		JxlsConfig.templateRoot = getRealPath(templateRoot);
		return this;
	}

	public JxlsConfig imageRoot(String imageRoot) {
		JxlsConfig.imageRoot = getRealPath(imageRoot);
		return this;
	}

	public JxlsConfig silent(boolean silent) {
		JxlsConfig.silent = silent;
		return this;
	}

	public JxlsConfig lock() {
		lock = true;
		return this;
	}

	private static String getRealPath(String originalPath) {
		if (!JxlsUtil.me().isAbsolutePath(originalPath) && !originalPath.startsWith("classpath:/")) {
			URL resource = JxlsConfig.class.getClassLoader().getResource(originalPath);
			if (resource != null) {
				String path = resource.getPath();
				if (path.contains(".jar")) {
					int index = path.lastIndexOf("/", path.indexOf("!/BOOT-INF"));
					path = path.substring(0, index).replaceFirst("file:/", "");
					File file = new File(path + "/" + originalPath);
					if (file.exists()) {
						return file.getAbsolutePath();
					}
					originalPath = "classpath:/" + originalPath;
				} else {
					originalPath = path;
				}
			}
		}
		return originalPath;
	}

	public static String getTemplateRoot() {
		return templateRoot;
	}

	public static String getImageRoot() {
		return imageRoot;
	}

	public static boolean getSilent() {
		return silent;
	}
}
