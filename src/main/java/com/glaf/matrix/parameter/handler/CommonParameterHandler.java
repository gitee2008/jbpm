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
package com.glaf.matrix.parameter.handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;

import com.glaf.core.el.Mvel2ExpressionEvaluator;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.StringTools;
import com.glaf.matrix.parameter.domain.ParameterConversion;
import com.glaf.template.util.TemplateUtils;

public class CommonParameterHandler implements IParameterHandler {

	@Override
	public void process(ParameterConversion conversion, Map<String, Object> parameter) {
		boolean boolValue = true;
		if (StringUtils.isNotEmpty(conversion.getConvertCondition())) {
			boolValue = false;
			try {
				Object objectValue = Mvel2ExpressionEvaluator.evaluate(conversion.getConvertCondition(), parameter);
				if (objectValue != null) {
					if (objectValue instanceof Boolean) {
						boolValue = (Boolean) objectValue;
						if (!boolValue) {
							return;
						}
					}
				}
			} catch (Exception ex) {
			}
		}
		if (boolValue) {
			if (StringUtils.isNotEmpty(conversion.getConvertExpression())) {
				try {
					Object objectValue = Mvel2ExpressionEvaluator.evaluate(conversion.getConvertExpression(),
							parameter);
					String targetName = conversion.getTargetName();
					parameter.put(targetName, objectValue);
					parameter.put(targetName.toLowerCase(), objectValue);
				} catch (Exception ex) {
				}
			} else if (StringUtils.isNotEmpty(conversion.getConvertTemplate())) {
				String text = TemplateUtils.process(parameter, conversion.getConvertTemplate());
				String targetName = conversion.getTargetName();
				parameter.put(targetName, text);
				parameter.put(targetName.toLowerCase(), text);
			} else {
				int year = 0;
				int month = 0;
				int day = 0;
				int quarter = 0;
				String text = null;
				SimpleDateFormat formatter = null;
				Calendar calendar = Calendar.getInstance();
				String name = conversion.getSourceName();
				String type = conversion.getConvertType();
				String targetName = conversion.getTargetName();
				if (StringUtils.startsWith(type, "date_")) {
					Date dateValue = ParamUtils.getDate(parameter, name);
					if (dateValue != null) {
						calendar.setTime(dateValue);
						year = calendar.get(Calendar.YEAR);
						month = calendar.get(Calendar.MONTH) + 1;
						day = calendar.get(Calendar.DAY_OF_MONTH);
						if (month <= 3) {
							quarter = 1;
						} else if (month > 3 && month <= 6) {
							quarter = 2;
						}
						if (month > 6 && month <= 9) {
							quarter = 3;
						}
						if (month > 9) {
							quarter = 4;
						}
						if (StringUtils.equals("date_yyyyMMddHHmmss", type)) {
							formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
							text = formatter.format(dateValue);
							parameter.put(targetName, text);
							parameter.put(targetName.toLowerCase(), text);
						} else if (StringUtils.equals("date_yyyyMMddHHmm", type)) {
							formatter = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
							text = formatter.format(dateValue);
							parameter.put(targetName, text);
							parameter.put(targetName.toLowerCase(), text);
						} else if (StringUtils.equals("date_yyyyMMddHH", type)) {
							formatter = new SimpleDateFormat("yyyyMMddHH", Locale.getDefault());
							text = formatter.format(dateValue);
							parameter.put(targetName, text);
							parameter.put(targetName.toLowerCase(), text);
						} else if (StringUtils.equals("date_yyyyMMdd", type)) {
							formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
							text = formatter.format(dateValue);
							parameter.put(targetName, text);
							parameter.put(targetName.toLowerCase(), text);
						} else if (StringUtils.equals("date_yyyyMM", type)) {
							formatter = new SimpleDateFormat("yyyyMM", Locale.getDefault());
							text = formatter.format(dateValue);
							parameter.put(targetName, text);
							parameter.put(targetName.toLowerCase(), text);
						} else if (StringUtils.equals("date_yyyy", type)) {
							parameter.put(targetName, String.valueOf(year));
							parameter.put(targetName.toLowerCase(), String.valueOf(year));
						} else if (StringUtils.equals("date_MM", type)) {
							parameter.put(targetName, String.valueOf(month));
							parameter.put(targetName.toLowerCase(), String.valueOf(month));
						} else if (StringUtils.equals("date_dd", type)) {
							parameter.put(targetName, String.valueOf(day));
							parameter.put(targetName.toLowerCase(), String.valueOf(day));
						} else if (StringUtils.equals("date_yyyyquarter", type)) {
							parameter.put(targetName, year + "Q" + quarter);
							parameter.put(targetName.toLowerCase(), year + "Q" + quarter);
						} else if (StringUtils.equals("date_quarterQ", type)) {
							parameter.put(targetName, "Q" + quarter);
							parameter.put(targetName.toLowerCase(), "Q" + quarter);
						} else if (StringUtils.equals("date_quarter", type)) {
							parameter.put(targetName, quarter);
							parameter.put(targetName.toLowerCase(), quarter);
						} else {
							parameter.put(targetName, dateValue);
							parameter.put(targetName.toLowerCase(), dateValue);
						}
					}
				} else if (StringUtils.equals(type, "countSplitByComma")) {
					String strValue = ParamUtils.getString(parameter, name);
					if (StringUtils.isNotEmpty(strValue)) {
						int count = 0;
						StringTokenizer token = new StringTokenizer(strValue, ",");
						while (token.hasMoreTokens()) {
							String tmp = token.nextToken();
							if (StringUtils.isNotEmpty(tmp)) {
								count++;
							}
						}
						parameter.put(targetName, count);
						parameter.put(targetName.toLowerCase(), count);
					}
				} else if (StringUtils.equals(type, "removeHtml")) {
					String strValue = ParamUtils.getString(parameter, name);
					if (StringUtils.isNotEmpty(strValue)) {
						String text2 = Jsoup.parse(strValue).text();
						parameter.put(targetName, text2);
						parameter.put(targetName.toLowerCase(), text2);
					}
				} else if (StringUtils.equals(type, "removeHtml2")) {
					String strValue = ParamUtils.getString(parameter, name);
					if (StringUtils.isNotEmpty(strValue)) {
						strValue = StringTools.replaceIgnoreCase(strValue, "<br>", "\n\r");
						strValue = StringTools.replaceIgnoreCase(strValue, "<br/>", "\n\r");
						String text2 = Jsoup.parse(strValue).text();
						parameter.put(targetName, text2);
						parameter.put(targetName.toLowerCase(), text2);
					}
				} else if (StringUtils.equals(type, "removeBlank")) {
					String strValue = ParamUtils.getString(parameter, name);
					if (StringUtils.isNotEmpty(strValue)) {
						strValue = StringTools.replace(strValue, " ", "");
						strValue = StringTools.replace(strValue, " ", "");
						parameter.put(targetName, strValue);
						parameter.put(targetName.toLowerCase(), strValue);
					}
				} else if (StringUtils.equals(type, "removeQuot")) {
					String strValue = ParamUtils.getString(parameter, name);
					if (StringUtils.isNotEmpty(strValue)) {
						strValue = StringTools.replace(strValue, "'", "");
						parameter.put(targetName, strValue);
						parameter.put(targetName.toLowerCase(), strValue);
					}
				} else {
					if (StringUtils.equals(conversion.getSourceType(), "String")
							&& StringUtils.equals(conversion.getSourceListFlag(), "Y")
							&& StringUtils.isNotEmpty(conversion.getDelimiter())) {
						String strValue = ParamUtils.getString(parameter, name);
						if (StringUtils.isNotEmpty(strValue)) {
							Collection<Object> values = new ArrayList<Object>();
							StringTokenizer token = new StringTokenizer(strValue, conversion.getDelimiter());
							while (token.hasMoreTokens()) {
								String tmp = token.nextToken();
								if (StringUtils.isNotEmpty(tmp)) {
									if (StringUtils.equals(conversion.getTargetType(), "Integer")) {
										values.add(Integer.parseInt(tmp));
									} else if (StringUtils.equals(conversion.getTargetType(), "Long")) {
										values.add(Long.parseLong(tmp));
									} else if (StringUtils.equals(conversion.getTargetType(), "Double")) {
										values.add(Double.parseDouble(tmp));
									} else if (StringUtils.equals(conversion.getTargetType(), "Date")) {
										values.add(DateUtils.toDate(tmp));
									} else {
										values.add(tmp);
									}
								}
							}
							parameter.put(targetName, values);
							parameter.put(targetName.toLowerCase(), values);
						}
					} else {
						if (StringUtils.equals(conversion.getTargetType(), "Integer")) {
							Integer x = ParamUtils.getIntValue(parameter, conversion.getSourceName());
							parameter.put(targetName, x);
							parameter.put(targetName.toLowerCase(), x);
						} else if (StringUtils.equals(conversion.getTargetType(), "Long")) {
							Long x = ParamUtils.getLongValue(parameter, conversion.getSourceName());
							parameter.put(targetName, x);
							parameter.put(targetName.toLowerCase(), x);
						} else if (StringUtils.equals(conversion.getTargetType(), "Double")) {
							Double x = ParamUtils.getDoubleValue(parameter, conversion.getSourceName());
							parameter.put(targetName, x);
							parameter.put(targetName.toLowerCase(), x);
						} else if (StringUtils.equals(conversion.getTargetType(), "Date")) {
							Date x = ParamUtils.getDate(parameter, conversion.getSourceName());
							parameter.put(targetName, x);
							parameter.put(targetName.toLowerCase(), x);
						} else if (StringUtils.equals(conversion.getTargetType(), "String")) {
							String x = ParamUtils.getString(parameter, conversion.getSourceName());
							parameter.put(targetName, x);
							parameter.put(targetName.toLowerCase(), x);
						} else {
							Object object = ParamUtils.getObject(parameter, conversion.getSourceName());
							parameter.put(targetName, object);
							parameter.put(targetName.toLowerCase(), object);
						}
					}
				}
			}
		}
	}

}
