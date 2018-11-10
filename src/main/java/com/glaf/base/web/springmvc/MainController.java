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

package com.glaf.base.web.springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.glaf.core.security.LoginContext;
import com.glaf.core.util.RequestUtils;

@Controller("/main.html")
@RequestMapping("/main.html")
public class MainController {
	protected static final Log logger = LogFactory.getLog(MainController.class);

	@RequestMapping
	public void mainPage(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
		logger.debug("-----------------------main page-------------------------");

		LoginContext loginContext = RequestUtils.getLoginContext(request);
		if (loginContext == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		response.sendRedirect(request.getContextPath() + "/my/home");
	}

}
