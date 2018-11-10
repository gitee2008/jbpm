/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.glaf.base.utils;

import com.glaf.base.modules.sys.model.SysUser;

import io.netty.util.concurrent.FastThreadLocal;

public class Authentication {

	static FastThreadLocal<String> authenticatedAccountThreadLocal = new FastThreadLocal<String>();

	static FastThreadLocal<SysUser> authenticatedUserThreadLocal = new FastThreadLocal<SysUser>();

	private Authentication() {

	}

	public static void clear() {
		authenticatedAccountThreadLocal.remove();
		authenticatedUserThreadLocal.remove();
	}

	public static String getAuthenticatedAccount() {
		return authenticatedAccountThreadLocal.get();
	}

	public static SysUser getAuthenticatedUser() {
		return authenticatedUserThreadLocal.get();
	}

	public static void setAuthenticatedAccount(String authenticatedAccount) {
		authenticatedAccountThreadLocal.set(authenticatedAccount);
	}

	public static void setAuthenticatedUser(SysUser authenticatedUser) {
		authenticatedUserThreadLocal.set(authenticatedUser);
	}
}
