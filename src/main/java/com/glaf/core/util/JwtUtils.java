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

package com.glaf.core.util;

import java.security.Key;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.config.Environment;
import com.glaf.core.config.KeyHelper;
import com.glaf.core.domain.SysKey;
import com.glaf.core.util.UUID32;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.iharder.Base64;

public class JwtUtils {

	private static volatile byte[] SECRET;

	private final static int expiresSeconds = 7200;

	public static String createJWT(JSONObject jsonObject) {
		byte[] secret = getSecret();
		return createJWT(secret, null, jsonObject);
	}

	public static String createJWT(String userId, JSONObject jsonObject) {
		byte[] secret = getSecret();
		return createJWT(secret, userId, jsonObject);
	}

	public static String createJWT(byte[] secret, String userId, JSONObject jsonObject) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// 生成签名密钥
		byte[] apiKeySecretBytes = secret;
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// 添加构成JWT的参数
		JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT");
		if (StringUtils.isNotEmpty(userId)) {
			builder.claim("userId", userId);
		}
		Iterator<Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			String key = (String) entry.getKey();
			Object value = entry.getValue();
			if (value != null) {
				builder.claim(key, value);
			}
		}
		builder.signWith(signingKey, signatureAlgorithm);
		// 添加Token过期时间
		if (expiresSeconds >= 0) {
			long expMillis = nowMillis + expiresSeconds * 1000;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp).setNotBefore(now);
		}

		// 生成JWT
		return builder.compact();
	}

	public static byte[] getSecret() {
		if (SECRET != null) {
			return SECRET;
		}
		KeyHelper keyHelper = new KeyHelper();
		SysKey property = keyHelper.getSysKeyById(Environment.DEFAULT_SYSTEM_NAME, "JWT_SECRET");
		if (property != null && property.getData() != null) {
			SECRET = property.getData();
		} else {
			SECRET = Base64.encodeBytesToBytes((UUID32.getUUID() + UUID32.generateShortUuid()).getBytes());
			property = new SysKey();
			property.setId("JWT_SECRET");
			property.setType("SYS");
			property.setName("JWT_SECRET");
			property.setData(SECRET);
			property.setTitle("JWT_SECRET");
			property.setCreateBy("system");
			property.setCreateDate(new Date());
			keyHelper.save(Environment.DEFAULT_SYSTEM_NAME, property);
		}
		return SECRET;
	}

	public static void main(String[] args) throws Exception {
		byte[] secret = JwtUtils.getSecret();
		System.out.println(new String(Base64.decode(secret)));
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("key1", 123);
		JSONArray array = new JSONArray();
		array.add(11);
		array.add(12);
		array.add(13);
		jsonObject.put("array", array);

		String str = JwtUtils.createJWT(secret, "root", jsonObject);
		System.out.println(str);

		Claims claims = JwtUtils.parseJWT(secret, str);
		Set<String> set = claims.keySet();
		for (String key : set) {
			System.out.println(key + "=" + claims.get(key));
		}

		JSONObject json = JwtUtils.parseJSON(secret, str);
		System.out.println(json.toJSONString());
	}

	public static JSONObject parseJSON(byte[] secret, String jsonWebToken) {
		try {
			Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jsonWebToken).getBody();
			JSONObject jsonObject = new JSONObject();
			Set<String> set = claims.keySet();
			for (String key : set) {
				jsonObject.put(key, claims.get(key));
			}
			return jsonObject;
		} catch (Exception ex) {
			return null;
		}
	}

	public static JSONObject parseJSON(String jsonWebToken) {
		byte[] secret = getSecret();
		return parseJSON(secret, jsonWebToken);
	}

	public static Claims parseJWT(byte[] secret, String jsonWebToken) {
		try {
			Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jsonWebToken).getBody();
			return claims;
		} catch (Exception ex) {
			return null;
		}
	}

	public static Claims parseJWT(String jsonWebToken) {
		byte[] secret = getSecret();
		return parseJWT(secret, jsonWebToken);
	}

}
