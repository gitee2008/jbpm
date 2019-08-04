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

package com.glaf.base.modules.sys.mapper;

import java.util.List;

import org.springframework.stereotype.Component;
import com.glaf.base.modules.sys.model.LoginModule;
import com.glaf.base.modules.sys.query.*;

/**
 * 
 * Mapper接口
 *
 */

@Component
public interface LoginModuleMapper {

	void deleteLoginModuleById(String id);

	LoginModule getLoginModuleByAppId(String appId);

	LoginModule getLoginModuleById(String id);
	
	LoginModule getLoginModuleBySysCode(String sysCode);

	LoginModule getLoginModuleByToken(String token);

	int getLoginModuleCount(LoginModuleQuery query);

	List<LoginModule> getLoginModules(LoginModuleQuery query);

	String getPrivateKey(String id);

	void insertLoginModule(LoginModule model);

	void resetLoginAppSecret(LoginModule model);

	void resetLoginAppToken(LoginModule model);

	void resetLoginPublicKey(LoginModule model);

	void updateLoginModule(LoginModule model);

}
