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

package com.glaf.matrix.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.compress.utils.IOUtils;

public class ImageUtils {

	/**
	 * 合并任数量的图片成一张图片
	 * 
	 * @param imgs         待合并的图片数组
	 * @param isHorizontal true代表水平合并，fasle代表垂直合并
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage mergeImage(BufferedImage[] imgs, boolean isHorizontal) throws IOException {
		// 生成新图片
		BufferedImage destImage = null;
		// 计算新图片的长和高
		int allw = 0, allh = 0, allwMax = 0, allhMax = 0;
		// 获取总长、总宽、最长、最宽
		for (int i = 0; i < imgs.length; i++) {
			BufferedImage img = imgs[i];
			allw += img.getWidth();
			allh += img.getHeight();
			if (img.getWidth() > allwMax) {
				allwMax = img.getWidth();
			}
			if (img.getHeight() > allhMax) {
				allhMax = img.getHeight();
			}
		}
		// 创建新图片
		if (isHorizontal) {
			destImage = new BufferedImage(allw, allhMax, BufferedImage.TYPE_INT_ARGB);
		} else {
			destImage = new BufferedImage(allwMax, allh, BufferedImage.TYPE_INT_ARGB);
		}
		// destImage.createGraphics().setBackground(Color.WHITE);
		// 合并所有子图片到新图片
		int wx = 0, wy = 0;
		for (int i = 0; i < imgs.length; i++) {
			BufferedImage img = imgs[i];
			int w1 = img.getWidth();
			int h1 = img.getHeight();
			// 从图片中读取RGB
			int[] imageArrayOne = new int[w1 * h1];
			imageArrayOne = img.getRGB(0, 0, w1, h1, imageArrayOne, 0, w1); // 逐行扫描图像中各个像素的RGB到数组中
			if (isHorizontal) { // 水平方向合并
				destImage.setRGB(wx, 0, w1, h1, imageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
			} else { // 垂直方向合并
				destImage.setRGB(0, wy, w1, h1, imageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
			}
			wx += w1;
			wy += h1;
		}
		return destImage;
	}

	/**
	 * 拼接图片（注：图片需长宽一致）
	 * 
	 * @param files      img1 ，img2
	 * @param type       1：横向拼接 2：纵向拼接
	 * @param targetFile 合成新的图片地址
	 */
	public static void mergeImage(String[] files, int type, String targetFile) {
		int len = files.length;
		if (len < 1) {
			throw new RuntimeException("图片数量小于1");
		}
		File[] src = new File[len];
		BufferedImage[] images = new BufferedImage[len];
		int[][] ImageArrays = new int[len][];
		for (int i = 0; i < len; i++) {
			try {
				src[i] = new File(files[i]);
				images[i] = ImageIO.read(src[i]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			int width = images[i].getWidth();
			int height = images[i].getHeight();
			ImageArrays[i] = new int[width * height];
			ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);
		}
		int newHeight = 0;
		int newWidth = 0;
		for (int i = 0; i < images.length; i++) {
			// 横向
			if (type == 1) {
				newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();
				newWidth += images[i].getWidth();
			} else if (type == 2) {// 纵向
				newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();
				newHeight += images[i].getHeight();
			}
		}
		if (type == 1 && newWidth < 1) {
			return;
		}
		if (type == 2 && newHeight < 1) {
			return;
		}
		// 生成新图片
		try {
			BufferedImage ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			int height_i = 0;
			int width_i = 0;
			for (int i = 0; i < images.length; i++) {
				if (type == 1) {
					ImageNew.setRGB(width_i, 0, images[i].getWidth(), newHeight, ImageArrays[i], 0,
							images[i].getWidth());
					width_i += images[i].getWidth();
				} else if (type == 2) {
					ImageNew.setRGB(0, height_i, newWidth, images[i].getHeight(), ImageArrays[i], 0, newWidth);
					height_i += images[i].getHeight();
				}
			}
			// 输出想要的图片
			ImageIO.write(ImageNew, targetFile.split("\\.")[1], new File(targetFile));
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 图片缩放
	 * 
	 * @param img       原始图片
	 * @param width     宽带
	 * @param height    高度
	 * @param imageType 目标图片类型
	 * @return
	 */
	public static byte[] zoomImage(BufferedImage img, int width, int height, String imageType) {
		double wr = 0, hr = 0;
		Image tempImg = img.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);// 设置缩放目标图片模板
		wr = width * 1.0 / img.getWidth(); // 获取缩放比例
		hr = height * 1.0 / img.getHeight();
		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
		tempImg = ato.filter(img, null);
		ByteArrayOutputStream baos = null;
		BufferedOutputStream bos = null;
		try {
			baos = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(baos);
			ImageIO.write((BufferedImage) tempImg, imageType, bos);
			bos.flush();
			baos.flush();
			byte[] data = baos.toByteArray();
			return data;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(baos);
		}
	}

	/**
	 * 按比例对图片进行缩放.
	 * 
	 * @param img       原始图片
	 * @param scale     缩放比例
	 * @param imageType 目标图片类型
	 */
	public static byte[] zoomByScale(BufferedImage img, double scale, String imageType) {
		// BufferedImage img = ImageIO.read(new File(srcFile));
		// 获取图片的长和宽
		int width = img.getWidth();
		int height = img.getHeight();
		// 获取缩放后的长和宽
		int _width = (int) (scale * width);
		int _height = (int) (scale * height);
		ByteArrayOutputStream baos = null;
		BufferedOutputStream bos = null;
		try {
			// 获取缩放后的Image对象
			Image _img = img.getScaledInstance(_width, _height, Image.SCALE_DEFAULT);
			// 新建一个和Image对象相同大小的画布
			BufferedImage image = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
			// 获取画笔
			Graphics2D graphics = image.createGraphics();
			// 将Image对象画在画布上
			graphics.drawImage(_img, 0, 0, null);
			// 释放资源
			graphics.dispose();
			baos = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(baos);
			// 使用ImageIO的方法进行输出,记得关闭资源
			ImageIO.write(image, imageType, bos);
			bos.flush();
			baos.flush();
			byte[] data = baos.toByteArray();
			return data;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(baos);
		}
	}

}
