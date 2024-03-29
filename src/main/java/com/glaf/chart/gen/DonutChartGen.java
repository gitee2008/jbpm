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

package com.glaf.chart.gen;

import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;

import com.glaf.chart.domain.Chart;
import com.glaf.chart.util.ChartUtils;
import com.glaf.core.base.ColumnModel;

public class DonutChartGen implements ChartGen {

	protected static final Log logger = LogFactory.getLog(DonutChartGen.class);

	public static void main(String[] args) {
		Chart chartModel = new Chart();
		chartModel.setChartFont("宋体");
		chartModel.setChartFontSize(12);
		chartModel.setChartHeight(400);
		chartModel.setChartWidth(400);
		chartModel.setChartTitle("测试环形图");
		chartModel.setImageType("png");
		chartModel.setChartName("pie");
		chartModel.setChartType("pie");
		java.util.Random rand = new java.util.Random();
		for (int i = 1; i < 6; i++) {
			ColumnModel cell = new ColumnModel();
			cell.setColumnName("col_" + i);
			cell.setSeries("条目" + i);
			cell.setDoubleValue(rand.nextInt(100) + 1.0);
			chartModel.addCellData(cell);
		}
		DonutChartGen chartDemo = new DonutChartGen();
		JFreeChart chart = chartDemo.createChart(chartModel);
		ChartUtils.createChart(".", chartModel, chart);
	}

	public JFreeChart createChart(Chart chartModel) {
		ChartUtils.setChartTheme(chartModel);
		// 用工厂类创建饼图
		JFreeChart localJFreeChart = null;
		if (StringUtils.equals(chartModel.getEnable3DFlag(), "1")) {
			localJFreeChart = ChartFactory.createRingChart(
					chartModel.getChartTitle(), createDataset(chartModel),
					true, true, false);
		} else {
			localJFreeChart = ChartFactory.createRingChart(
					chartModel.getChartTitle(), createDataset(chartModel),
					true, true, false);
		}
		localJFreeChart.setBackgroundPaint(Color.white);
		// RenderingHints做文字渲染参数的修改
		// VALUE_TEXT_ANTIALIAS_OFF表示将文字的抗锯齿关闭.
		localJFreeChart.getRenderingHints().put(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		// 得到图的Plot对象
		RingPlot plot = (RingPlot) localJFreeChart.getPlot();
		setSection(plot, chartModel);
		setLabel(plot, chartModel);
		plot.setLabelGap(0.02D);
		plot.setSectionDepth(0.65D);
		plot.setCircular(true);
		setNoDataMessage(plot, chartModel);
		setNullAndZeroValue(plot, chartModel);

		return localJFreeChart;
	}

	public DefaultPieDataset createDataset(Chart chartModel) {
		// 设置数据
		DefaultPieDataset pieDataset = new DefaultPieDataset();

		List<ColumnModel> columns = chartModel.getColumns();

		if (columns != null && !columns.isEmpty()) {
			Collections.sort(columns);
			if (columns.size() <= 5) {
				for (ColumnModel cell : columns) {
					if (cell.getDoubleValue() != null) {
						if (cell.getCategory() != null
								&& cell.getDoubleValue() > 0) {
							pieDataset.setValue(cell.getCategory(),
									cell.getDoubleValue());
							logger.debug("item:" + cell.getCategory() + "\t"
									+ cell.getDoubleValue());
						}
					}
				}
			} else {
				for (int i = 0; i < 5; i++) {
					ColumnModel cell = columns.get(i);
					if (cell.getDoubleValue() != null) {
						if (cell.getCategory() != null
								&& cell.getDoubleValue() > 0) {
							pieDataset.setValue(cell.getCategory(),
									cell.getDoubleValue());
							logger.debug("item:" + cell.getCategory() + "\t"
									+ cell.getDoubleValue());
						}
					}
				}

				double doubleValue = 0;
				for (int i = 5; i < columns.size(); i++) {
					ColumnModel cell = columns.get(i);
					if (cell.getDoubleValue() != null) {
						if (cell.getCategory() != null
								&& cell.getDoubleValue() > 0) {
							doubleValue += cell.getDoubleValue();
						}
					}
				}
				if (doubleValue > 0) {
					pieDataset.setValue("other", doubleValue);
				}
			}
		}
		return pieDataset;
	}

	public void setLabel(RingPlot plot, Chart chartModel) {
		// 设置扇区标签显示格式：关键字：值(百分比)
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}:{1}({2})"));
		// 设置扇区标签颜色
		plot.setLabelBackgroundPaint(new Color(255, 255, 255));
		plot.setLabelFont((new Font(chartModel.getChartFont(), Font.PLAIN,
				chartModel.getChartFontSize())));

	}

	public void setNoDataMessage(RingPlot plot, Chart chartModel) {
		// 设置没有数据时显示的信息
		plot.setNoDataMessage("无数据");
		// 设置没有数据时显示的信息的字体
		plot.setNoDataMessageFont(new Font(chartModel.getChartFont(),
				Font.BOLD, chartModel.getChartFontSize()));
		// 设置没有数据时显示的信息的颜色
		plot.setNoDataMessagePaint(Color.red);
	}

	public void set3DNullAndZeroValue(PiePlot3D piePlot, Chart chartModel) {
		// 设置是否忽略0和null值
		piePlot.setIgnoreNullValues(true);
		piePlot.setIgnoreZeroValues(true);
	}

	public void set3DLabel(PiePlot3D pieplot, Chart chartModel) {
		// 设置扇区标签显示格式：关键字：值(百分比)
		pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}:{1}({2})"));
		// 设置扇区标签颜色
		pieplot.setLabelBackgroundPaint(new Color(255, 255, 255));
		pieplot.setLabelFont((new Font(chartModel.getChartFont(), Font.PLAIN,
				chartModel.getChartFontSize())));

	}

	public void set3DNoDataMessage(PiePlot3D pieplot, Chart chartModel) {
		// 设置没有数据时显示的信息
		pieplot.setNoDataMessage("无数据");
		// 设置没有数据时显示的信息的字体
		pieplot.setNoDataMessageFont(new Font(chartModel.getChartFont(),
				Font.BOLD, chartModel.getChartFontSize()));
		// 设置没有数据时显示的信息的颜色
		pieplot.setNoDataMessagePaint(Color.red);
	}

	public void setNullAndZeroValue(PiePlot piePlot, Chart chartModel) {
		// 设置是否忽略0和null值
		piePlot.setIgnoreNullValues(true);
		piePlot.setIgnoreZeroValues(true);
	}

	public void setSection(PiePlot pieplot, Chart chartModel) {

	}

}