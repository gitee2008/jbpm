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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import com.glaf.chart.domain.Chart;
import com.glaf.chart.util.ChartUtils;
import com.glaf.core.base.ColumnModel;

public class ColumnLineChartGen implements ChartGen {
	protected static final Log logger = LogFactory.getLog(ColumnLineChartGen.class);

	public static void main(String[] paramArrayOfString) {
		ColumnLineChartGen chartDemo = new ColumnLineChartGen();
		Chart chartModel = new Chart();
		chartModel.setChartFont("宋体");
		chartModel.setChartFontSize(25);
		chartModel.setChartHeight(544);
		chartModel.setChartWidth(1566);
		chartModel.setChartTitle("双Y轴柱状曲线图");
		chartModel.setChartTitleFont("宋体");
		chartModel.setChartTitleFontSize(72);
		chartModel.setImageType("png");
		chartModel.setChartName("column_line");
		chartModel.setChartType("column_line");
		chartModel.setCoordinateX("日期");
		chartModel.setCoordinateY("量产");
		java.util.Random rand = new java.util.Random();

		for (int i = 1; i <= 30; i++) {

			System.out.println("---------------------------");
			ColumnModel cell1 = new ColumnModel();
			cell1.setCategory(String.valueOf(i));
			cell1.setIntValue(i);
			cell1.setColumnName("col_" + i);
			cell1.setSeries("Cross");
			if (i <= 20) {
				cell1.setDoubleValue(Math.abs(rand.nextInt(10)) * 1.0D);
			}
			chartModel.addCellData(cell1);
			System.out.println(cell1.getDoubleValue());

			ColumnModel cell2 = new ColumnModel();
			cell2.setColumnName("col2_" + i);
			cell2.setCategory(String.valueOf(i));
			cell2.setSeries("Fit");
			if (i <= 20) {
				cell2.setDoubleValue(Math.abs(rand.nextInt(10)) * 1.0D);
			}
			chartModel.addCellData(cell2);
			System.out.println(cell2.getDoubleValue());

			ColumnModel cell3 = new ColumnModel();
			cell3.setColumnName("col3_" + i);
			cell3.setCategory(String.valueOf(i));
			cell3.setSeries("Accord");
			if (i > 20) {
				cell3.setDoubleValue(null);
			} else {
				cell3.setDoubleValue(
						(Math.abs(rand.nextInt(20)) + 20D - cell2.getDoubleValue() - cell1.getDoubleValue()) * 1.0D);
			}
			chartModel.addCellData(cell3);
			System.out.println(cell3.getDoubleValue());
		}

		JFreeChart chart = chartDemo.createChart(chartModel);
		ChartUtils.createChart(".", chartModel, chart);
	}

	public JFreeChart createChart(Chart chartModel) {
		logger.debug("------------ColumnLineChartGen.createChart-----------------");
		ChartUtils.setChartTheme(chartModel);
		CategoryDataset localDefaultCategoryDataset1 = this.createDataset(chartModel);
		CategoryPlot localCategoryPlot = new CategoryPlot();

		BarRenderer localExtendedStackedBarRenderer = new BarRenderer();
		localExtendedStackedBarRenderer.setBaseItemLabelsVisible(true);
		localExtendedStackedBarRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		localExtendedStackedBarRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		localExtendedStackedBarRenderer.setDrawBarOutline(false);
		localExtendedStackedBarRenderer.setBaseItemLabelsVisible(true);
		localCategoryPlot.setRenderer(localExtendedStackedBarRenderer);

		DecimalFormat localDecimalFormat2 = new DecimalFormat("####");
		localDecimalFormat2.setNegativePrefix("");
		localDecimalFormat2.setNegativeSuffix("");

		localExtendedStackedBarRenderer
				.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", localDecimalFormat2));

		localExtendedStackedBarRenderer.setBaseItemLabelsVisible(true);
		Color[] color = new Color[8];
		color[0] = Color.red;
		color[1] = Color.blue;
		color[2] = Color.yellow;
		color[3] = Color.green;
		color[4] = Color.red;
		color[5] = Color.blue;
		color[6] = Color.yellow;
		color[7] = Color.green;

		// 设置柱子上比例数值的显示，如果按照默认方式显示，数值为方向正常显示
		// 设置柱子上显示的数据旋转90度,最后一个参数为旋转的角度值/3.14
		ItemLabelPosition itemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER,
				TextAnchor.CENTER, -1.57D);

		// 下面的设置是为了解决，当柱子的比例过小，而导致表示该柱子比例的数值无法显示的问题
		// 设置不能在柱子上正常显示的那些数值的显示方式，将这些数值显示在柱子外面
		ItemLabelPosition itemLabelPositionFallback = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
				TextAnchor.BASELINE_LEFT, TextAnchor.HALF_ASCENT_LEFT, -1.57D);

		localExtendedStackedBarRenderer.setBasePositiveItemLabelPosition(itemLabelPosition);
		localExtendedStackedBarRenderer.setBaseNegativeItemLabelPosition(itemLabelPositionFallback);
		for (int i = 0; i < 5; i++) {
			if (chartModel.getChartFont() != null && chartModel.getChartFontSize() > 0) {
				localExtendedStackedBarRenderer.setSeriesItemLabelFont(i,
						new Font(chartModel.getChartFont(), Font.PLAIN, chartModel.getChartFontSize()));
				localExtendedStackedBarRenderer.setSeriesItemLabelsVisible(i, true);
			} else {
				localExtendedStackedBarRenderer.setSeriesItemLabelFont(i, new Font("宋体", Font.PLAIN, 16));
				localExtendedStackedBarRenderer.setSeriesItemLabelsVisible(i, true);
			}
			localExtendedStackedBarRenderer.setSeriesPaint(i, color[i]);
			// 设置正常显示的柱子label的position
			localExtendedStackedBarRenderer.setSeriesPositiveItemLabelPosition(i, itemLabelPosition);
			localExtendedStackedBarRenderer.setSeriesNegativeItemLabelPosition(i, itemLabelPosition);
		}

		// 设置不能正常显示的柱子label的position
		localExtendedStackedBarRenderer.setPositiveItemLabelPositionFallback(itemLabelPositionFallback);
		localExtendedStackedBarRenderer.setNegativeItemLabelPositionFallback(itemLabelPositionFallback);

		localCategoryPlot.setDomainAxis(new CategoryAxis(chartModel.getCoordinateX()));
		localCategoryPlot.setRangeAxis(new NumberAxis(chartModel.getCoordinateY()));
		localCategoryPlot.setOrientation(PlotOrientation.VERTICAL);
		localCategoryPlot.setBackgroundPaint(Color.lightGray);
		localCategoryPlot.setRangeGridlinesVisible(true);
		localCategoryPlot.setDomainGridlinesVisible(true);

		CategoryDataset localDefaultCategoryDataset2 = this.createLineDataset(chartModel);
		LineAndShapeRenderer localLineAndShapeRenderer = new LineAndShapeRenderer();

		localLineAndShapeRenderer.setBaseShapesVisible(true);
		localLineAndShapeRenderer.setDrawOutlines(true);
		localLineAndShapeRenderer.setUseFillPaint(true);
		localLineAndShapeRenderer.setBaseItemLabelsVisible(true);// 显示数值

		for (int i = 0; i <= 6; i++) {
			localLineAndShapeRenderer.setSeriesPaint(i, color[i]);
			localLineAndShapeRenderer.setSeriesStroke(i, new BasicStroke(3.0F));
			localLineAndShapeRenderer.setSeriesOutlineStroke(i, new BasicStroke(2.0F));
			localLineAndShapeRenderer.setSeriesShape(i, new Ellipse2D.Double(-2.0D, -2.0D, 4.0D, 4.0D));
			if (chartModel.getChartFont() != null && chartModel.getChartFontSize() > 0) {
				localLineAndShapeRenderer.setSeriesItemLabelFont(i,
						new Font(chartModel.getChartFont(), Font.PLAIN, chartModel.getChartFontSize()));
				localLineAndShapeRenderer.setSeriesItemLabelsVisible(i, true);
			} else {
				localLineAndShapeRenderer.setSeriesItemLabelFont(i, new Font("宋体", Font.PLAIN, 16));
				localLineAndShapeRenderer.setSeriesItemLabelsVisible(i, true);
			}
		}

		localLineAndShapeRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());

		NumberAxis localNumberAxis = new NumberAxis("");
		localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		DecimalFormat localDecimalFormat3 = new DecimalFormat("####");
		localDecimalFormat3.setNegativePrefix("");
		localDecimalFormat3.setNegativeSuffix("");

		localNumberAxis.setNumberFormatOverride(localDecimalFormat3);
		localCategoryPlot.setRangeAxis(1, localNumberAxis);

		localCategoryPlot.setDataset(1, localDefaultCategoryDataset1);
		localCategoryPlot.setRenderer(1, localExtendedStackedBarRenderer);

		localCategoryPlot.setDataset(localDefaultCategoryDataset2);
		localCategoryPlot.setRenderer(localLineAndShapeRenderer);
		localCategoryPlot.mapDatasetToRangeAxis(1, 1);

		CategoryAxis domainAxis = localCategoryPlot.getDomainAxis();
		ValueAxis rangeAxis = localCategoryPlot.getRangeAxis();
		ValueAxis rangeAxis1 = localCategoryPlot.getRangeAxisForDataset(1);
		if (chartModel.getChartFont() != null && chartModel.getChartFontSize() > 0) {
			domainAxis.setLabelFont(new Font(chartModel.getChartFont(), Font.PLAIN, chartModel.getChartFontSize()));
			domainAxis.setTickLabelFont(new Font(chartModel.getChartFont(), Font.PLAIN, chartModel.getChartFontSize()));
			rangeAxis.setLabelFont(new Font(chartModel.getChartFont(), Font.PLAIN, chartModel.getChartFontSize()));
			rangeAxis.setTickLabelFont(new Font(chartModel.getChartFont(), Font.PLAIN, chartModel.getChartFontSize()));
			if (rangeAxis1 != null) {
				rangeAxis1.setLabelFont(new Font(chartModel.getChartFont(), Font.PLAIN, chartModel.getChartFontSize()));
				rangeAxis1.setTickLabelFont(
						new Font(chartModel.getChartFont(), Font.PLAIN, chartModel.getChartFontSize()));
			}
		} else {
			domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 16));
			domainAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 16));
			rangeAxis.setLabelFont(new Font("宋体", Font.PLAIN, 16));
			rangeAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 16));
			if (rangeAxis1 != null) {
				rangeAxis1.setLabelFont(new Font("宋体", Font.PLAIN, 16));
				rangeAxis1.setTickLabelFont(new Font("宋体", Font.PLAIN, 16));
			}
		}
		JFreeChart localJFreeChart = new JFreeChart(localCategoryPlot);
		LegendTitle localLegendTitle = localJFreeChart.getLegend();
		if (chartModel.getChartFont() != null && chartModel.getChartFontSize() > 0) {
			localLegendTitle
					.setItemFont(new Font(chartModel.getChartFont(), Font.PLAIN, chartModel.getChartFontSize()));

		} else
			localLegendTitle.setItemFont(new Font("宋体", Font.PLAIN, 16));

		TextTitle texttile = new TextTitle(chartModel.getChartTitle(),
				new Font(chartModel.getChartTitleFont(), Font.PLAIN, chartModel.getChartTitleFontSize()));
		localJFreeChart.setTitle(texttile);

		// ChartUtilities.applyCurrentTheme(localJFreeChart);
		return localJFreeChart;
	}

	public CategoryDataset createDataset(Chart chartModel) {
		DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
		for (ColumnModel cell : chartModel.getColumns()) {
			if (cell.getSeries() != null && cell.getCategory() != null) {
				if (cell.getDoubleValue() != null && cell.getDoubleValue() != 0D) {
					localDefaultCategoryDataset.addValue(cell.getDoubleValue(), cell.getSeries(), cell.getCategory());
				} else {
					localDefaultCategoryDataset.addValue(null, cell.getSeries(), cell.getCategory());
				}
			}
		}
		return localDefaultCategoryDataset;
	}

	public CategoryDataset createLineDataset(Chart chartModel) {
		DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
		if (chartModel.getSecondColumns() != null && !chartModel.getSecondColumns().isEmpty()) {
			for (ColumnModel cell : chartModel.getSecondColumns()) {
				if (cell.getSeries() != null && cell.getCategory() != null) {
					if (cell.getDoubleValue() != null) {
						localDefaultCategoryDataset.addValue(cell.getDoubleValue(), cell.getSeries().trim(),
								cell.getCategory());
					} else {
						localDefaultCategoryDataset.addValue(null, cell.getSeries().trim(), cell.getCategory());
					}
				}
			}
		}
		return localDefaultCategoryDataset;
	}

}
