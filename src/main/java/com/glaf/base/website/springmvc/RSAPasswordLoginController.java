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

package com.glaf.base.website.springmvc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.glaf.base.callback.LoginCallbackThread;
import com.glaf.base.config.BaseConfiguration;
import com.glaf.base.modules.BaseDataManager;
import com.glaf.base.modules.sys.model.BaseDataInfo;
import com.glaf.base.modules.sys.model.LoginModule;
import com.glaf.base.modules.sys.model.SysUser;
import com.glaf.base.modules.sys.query.LoginModuleQuery;
import com.glaf.base.modules.sys.service.AuthorizeService;
import com.glaf.base.modules.sys.service.LoginMessageService;
import com.glaf.base.modules.sys.service.LoginModuleService;
import com.glaf.base.modules.sys.service.LoginTokenService;
import com.glaf.base.modules.sys.service.SysUserService;
import com.glaf.base.online.domain.UserOnline;
import com.glaf.base.online.service.UserOnlineLogService;
import com.glaf.base.online.service.UserOnlineService;
import com.glaf.base.utils.ContextUtil;

import com.glaf.core.cache.CacheFactory;
import com.glaf.core.config.Configuration;
import com.glaf.core.config.Environment;
import com.glaf.core.config.SystemConfig;
import com.glaf.core.domain.SystemProperty;
import com.glaf.core.security.IdentityFactory;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.ClassUtils;
import com.glaf.core.util.Constants;
import com.glaf.core.util.Hex;
import com.glaf.core.util.IOUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.web.callback.CallbackProperties;
import com.glaf.core.web.callback.LoginCallback;

@Controller("/rsa_pwd_login")
@RequestMapping("/rsa_pwd_login")
public class RSAPasswordLoginController {
	private static final Log logger = LogFactory.getLog(RSAPasswordLoginController.class);

	protected static Configuration conf = BaseConfiguration.create();

	protected AuthorizeService authorizeService;

	protected LoginModuleService loginModuleService;

	protected LoginTokenService loginTokenService;

	protected LoginMessageService loginMessageService;

	protected SysUserService sysUserService;

	protected UserOnlineService userOnlineService;

	protected UserOnlineLogService userOnlineLogService;

	@ResponseBody
	@RequestMapping("/getPublicKey")
	public void getPublicKey(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain; charset=UTF-8");
		String systemCode = request.getParameter("systemCode");
		String userId = request.getParameter("userId");
		if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(systemCode)) {
			PrintWriter writer = null;
			try {
				LoginModuleQuery query = new LoginModuleQuery();
				query.systemCode(systemCode);
				query.type("server_rsa");
				List<LoginModule> list = loginModuleService.list(query);
				if (list != null && !list.isEmpty()) {
					LoginModule loginModule = list.get(0);
					String text = loginModule.getPublicKey();
					writer = response.getWriter();
					writer.write(text);
					writer.flush();
				}
			} catch (Exception ex) {
				// //ex.printStackTrace();
				logger.error(ex);
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
	}

	/**
	 * RSA登录
	 * 
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 */
	@RequestMapping
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		logger.debug("---------------------doLogin--------------------");
		String responseDataType = request.getParameter("responseDataType");// 响应数据类型，可以是json
		String systemCode = request.getParameter("systemCode");// 系统代码，通过这个参数找到RSA密锁
		String userId = request.getParameter("userId");// 登录用户编号，通过RSA公钥加密的十六进制字符串
		String pwd = request.getParameter("password");// 登录密码，通过RSA公钥加密的十六进制字符串
		SysUser bean = null;
		byte[] data = null;
		String url = null;
		if (StringUtils.isNotEmpty(systemCode) && StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(pwd)) {
			try {
				LoginModuleQuery query = new LoginModuleQuery();
				query.systemCode(systemCode);
				query.type("server_rsa");
				List<LoginModule> list = loginModuleService.list(query);
				if (list != null && !list.isEmpty()) {
					LoginModule loginModule = list.get(0);
					url = loginModule.getUrl();
					data = loginModuleService.decryptText(loginModule.getId(), Hex.hex2byte(userId));
					userId = new String(data, "UTF-8");

					data = loginModuleService.decryptText(loginModule.getId(), Hex.hex2byte(pwd));
					pwd = new String(data, "UTF-8");

					if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(pwd)) {
						bean = authorizeService.login(userId, pwd);
						if (bean == null && StringUtils.isNotEmpty(responseDataType)
								&& StringUtils.equals(responseDataType, "json")) {
							OutputStream output = null;
							try {
								request.setCharacterEncoding("UTF-8");
								response.setCharacterEncoding("UTF-8");
								response.setContentType("application/json; charset=UTF-8");
								output = response.getOutputStream();
								byte[] bytes = ResponseUtils.responseJsonResult(10003, "用户名或密码不正确。");
								output.write(bytes);
								output.flush();
								return null;
							} catch (Exception e) {
							} finally {
								IOUtils.closeStream(output);
							}
						}
					}
				}
			} catch (Exception ex) {
				logger.error("RSA Login Error", ex);
			}
		}

		// 如果每天登录次数超过设置值，设置用户对象为空，防止恶意操作
		if (bean != null && userOnlineLogService.getLoginCount(userId) > conf.getInt("limit.loginCount", 5000)) {
			bean = null;
		}

		if (bean != null) {
			LoginContext loginContext = IdentityFactory.getLoginContext(bean.getActorId());
			/**
			 * 系统管理员不允许直接从RSA快捷登录
			 */
			if (loginContext.isSystemAdministrator()) {
				// 退出系统，清除session对象
				if (request.getSession(false) != null) {
					request.getSession().removeAttribute(
							Constants.LOGIN_INFO + "_" + StringTools.replace(request.getContextPath(), "/", ""));
				}
				try {
					String cacheKey = Constants.CACHE_LOGIN_CONTEXT_KEY + loginContext.getActorId();
					CacheFactory.remove("loginContext", cacheKey);
					cacheKey = Constants.CACHE_USER_KEY + loginContext.getActorId();
					CacheFactory.remove("user", cacheKey);
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
				return new ModelAndView("/modules/login");
			} else {
				String ipAddr = RequestUtils.getIPAddress(request);
				SystemProperty p = SystemConfig.getProperty("login_limit");
				if (!(StringUtils.equals(bean.getUserId(), "root") || StringUtils.equals(bean.getUserId(), "admin"))) {
					logger.debug("#################check login limit#####################");

					SystemProperty pt = SystemConfig.getProperty("login_time_check");
					int timeoutSeconds = 300;

					if (pt != null && pt.getValue() != null && StringUtils.isNumeric(pt.getValue())) {
						timeoutSeconds = Integer.parseInt(pt.getValue());
					}
					if (timeoutSeconds < 300) {
						timeoutSeconds = 300;
					}
					if (timeoutSeconds > 3600) {
						timeoutSeconds = 3600;
					}

					/**
					 * 检测是否限制一个用户只能在一个地方登录
					 */
					if (p != null && StringUtils.equals(p.getValue(), "true")) {
						logger.debug("#################3#########################");
						String loginIP = null;
						UserOnline userOnline = userOnlineService.getUserOnline(bean.getUserId());
						logger.debug("userOnline:" + userOnline);
						boolean timeout = false;
						if (userOnline != null) {
							loginIP = userOnline.getLoginIP();
							if (userOnline.getCheckDateMs() != 0 && System.currentTimeMillis()
									- userOnline.getCheckDateMs() > timeoutSeconds * 1000) {
								timeout = true;// 超时，说明登录已经过期
							}
							if (userOnline.getLoginDate() != null && System.currentTimeMillis()
									- userOnline.getLoginDate().getTime() > timeoutSeconds * 1000) {
								timeout = true;// 超时，说明登录已经过期
							}
						}
						logger.info("timeout:" + timeout);
						logger.info("login IP:" + loginIP);
					}
				}

				HttpSession session = request.getSession(true);// 重写Session
				logger.debug("重新创建会话:" + session.getId());

				Properties props = CallbackProperties.getProperties();
				if (props != null && props.keys().hasMoreElements()) {
					Enumeration<?> e = props.keys();
					while (e.hasMoreElements()) {
						String className = (String) e.nextElement();
						try {
							Object obj = ClassUtils.instantiateObject(className);
							if (obj instanceof LoginCallback) {
								LoginCallback callback = (LoginCallback) obj;
								if (StringUtils.equals(className, "com.glaf.shiro.ShiroLoginCallback")) {
									callback.afterLogin(bean.getUserId(), request, response);
								} else {
									LoginCallbackThread command = new LoginCallbackThread(callback, bean.getUserId(),
											request, response);
									com.glaf.core.util.ThreadFactory.execute(command);
									Thread.sleep(50);
								}
							}
						} catch (Exception ex) {
							// ex.printStackTrace();
							logger.error(ex);
						}
					}
				}
				// 登录成功，修改最近一次登录时间
				bean.setLastLoginDate(new Date());
				bean.setLastLoginIP(ipAddr);
				bean.setLockLoginTime(null);
				bean.setLoginRetry(0);
				sysUserService.updateUser(bean);

				ContextUtil.put(bean.getUserId(), bean);// 传入全局变量

				String systemName = Environment.getCurrentSystemName();

				if (StringUtils.isEmpty(systemName)) {
					systemName = "default";
				}

				RequestUtils.setLoginUser(request, response, systemName, bean.getUserId());

				try {
					UserOnline online = new UserOnline();
					online.setActorId(bean.getActorId());
					online.setName(bean.getName());
					online.setCheckDate(new Date());
					online.setLoginDate(new Date());
					if (!bean.isSystemAdministrator()) {
						online.setLoginIP(ipAddr);
					}
					userOnlineService.login(online);
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			}

			if (StringUtils.isNotEmpty(responseDataType) && StringUtils.equals(responseDataType, "json")) {
				OutputStream output = null;
				try {
					request.setCharacterEncoding("UTF-8");
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json; charset=UTF-8");
					output = response.getOutputStream();
					byte[] bytes = ResponseUtils.responseJsonResult(true, "登录成功！");
					output.write(bytes);
					output.flush();
					return null;
				} catch (Exception ex) {
				} finally {
					IOUtils.closeStream(output);
				}
			}

			String redirectUrl = request.getParameter("redirectUrl");
			if (StringUtils.isNotEmpty(redirectUrl)) {
				try {
					response.sendRedirect(redirectUrl);
					return null;
				} catch (IOException e) {
				}
			}

			String redirectUrl_enc = request.getParameter("redirectUrl_enc");
			if (StringUtils.isNotEmpty(redirectUrl_enc)) {
				try {
					response.sendRedirect(RequestUtils.decodeURL(redirectUrl_enc));
					return null;
				} catch (IOException e) {
				}
			}

			if (StringUtils.isNotEmpty(url)) {
				try {
					response.sendRedirect(url);
					return null;
				} catch (IOException e) {
				}
			}

			return new ModelAndView("/modules/main", modelMap);

		}

		// 处理登录不成功的情况
		if (bean == null) {
			if (StringUtils.isNotEmpty(responseDataType) && StringUtils.equals(responseDataType, "json")) {
				OutputStream output = null;
				try {
					request.setCharacterEncoding("UTF-8");
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json; charset=UTF-8");
					output = response.getOutputStream();
					byte[] bytes = ResponseUtils.responseJsonResult(10002, "登录信息不正确。");
					output.write(bytes);
					output.flush();
					return null;
				} catch (Exception ex) {
				} finally {
					IOUtils.closeStream(output);
				}
			}

			BaseDataManager manager = BaseDataManager.getInstance();
			BaseDataInfo loginInfo = manager.getBaseData("third_loginUrl", "login_param");
			if (loginInfo != null && StringUtils.isNotEmpty(loginInfo.getValue())) {
				try {
					response.sendRedirect(loginInfo.getValue());
					return null;
				} catch (IOException e) {
				}
			}
		}

		return this.prepareLogin(request, response, modelMap);
	}

	/**
	 * 准备登录
	 * 
	 * @param request
	 * @param modelMap
	 * @return
	 */
	public ModelAndView prepareLogin(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		logger.debug("---------------------prepareLogin--------------------");
		RequestUtils.setRequestParameterToAttribute(request);

		HttpSession session = request.getSession(true);
		java.util.Random random = new java.util.Random();
		String rand = Math.abs(random.nextInt(9999)) + com.glaf.core.util.UUID32.getUUID()
				+ Math.abs(random.nextInt(9999));
		String rand2 = Math.abs(random.nextInt(9999)) + com.glaf.core.util.UUID32.getUUID()
				+ Math.abs(random.nextInt(9999));
		if (session != null) {
			rand = Base64.encodeBase64String(rand.getBytes()) + com.glaf.core.util.UUID32.getUUID();
			rand2 = Base64.encodeBase64String(rand2.getBytes()) + com.glaf.core.util.UUID32.getUUID();
			session.setAttribute("x_y", rand);
			session.setAttribute("x_z", rand2);
		}

		String view = "/modules/login";

		if (StringUtils.isNotEmpty(SystemConfig.getString("login_view"))) {
			view = SystemConfig.getString("login_view");
		}

		if (RequestUtils.isMobile(request)) {
			// 手机版登录页面
			view = "/mobile/login";
		}

		// 显示登陆页面
		return new ModelAndView(view, modelMap);
	}

	@javax.annotation.Resource
	public void setAuthorizeService(AuthorizeService authorizeService) {
		this.authorizeService = authorizeService;
	}

	@javax.annotation.Resource
	public void setLoginMessageService(LoginMessageService loginMessageService) {
		this.loginMessageService = loginMessageService;
	}

	@javax.annotation.Resource
	public void setLoginModuleService(LoginModuleService loginModuleService) {
		this.loginModuleService = loginModuleService;
	}

	@javax.annotation.Resource
	public void setLoginTokenService(LoginTokenService loginTokenService) {
		this.loginTokenService = loginTokenService;
	}

	@javax.annotation.Resource
	public void setSysUserService(SysUserService sysUserService) {
		this.sysUserService = sysUserService;
	}

	@javax.annotation.Resource
	public void setUserOnlineLogService(UserOnlineLogService userOnlineLogService) {
		this.userOnlineLogService = userOnlineLogService;
	}

	@javax.annotation.Resource
	public void setUserOnlineService(UserOnlineService userOnlineService) {
		this.userOnlineService = userOnlineService;
	}

}
