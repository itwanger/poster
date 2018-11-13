package com.comwer.poster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.font.FontDesignMetrics;

public class QrcodeUtils {
	private static Logger logger = LoggerFactory.getLogger(QrcodeUtils.class);

	// 外边距
	private static int margin = 25;

	public static String createQrcodeFile(String zh, String en, String headimgUrl, int flag) throws Exception {
		ClassLoader classLoader = QrcodeUtils.class.getClassLoader();
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpGet httpget = new HttpGet(headimgUrl);
		httpget.addHeader("Content-Type", "text/html;charset=UTF-8");
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(500).setConnectTimeout(500).setSocketTimeout(500)
				.build();
		httpget.setConfig(requestConfig);

		File headimgFile = null;

		try (CloseableHttpResponse response = httpclient.execute(httpget); InputStream headimgStream = handleResponse(response);) {

			Header[] contentTypeHeader = response.getHeaders("Content-Type");
			if (contentTypeHeader != null && contentTypeHeader.length > 0) {
				if (contentTypeHeader[0].getValue().startsWith(ContentType.APPLICATION_JSON.getMimeType())) {

					// application/json; encoding=utf-8 下载媒体文件出错
					String responseContent = handleUTF8Response(response);

					logger.warn("下载网络顶部图出错{}", responseContent);
				}
			}

			headimgFile = createTmpFile(headimgStream, "headimg_" + IdGen.uuid(), "jpg");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			headimgFile = Files.createTempFile("headimg_" + IdGen.uuid(), ".jpg").toFile();

			InputStream headimgStream1 = classLoader.getResourceAsStream("default_headimg.jpg");
			FileUtils.copyInputStreamToFile(headimgStream1, headimgFile);
		} finally {
			httpget.releaseConnection();
		}

		if (headimgFile != null && !headimgFile.exists()) {
			throw new IllegalArgumentException("请提供正确的头部文件！");
		}

		File bgFile = Files.createTempFile("bg_", ".jpg").toFile();
		InputStream inputStream = classLoader.getResourceAsStream("default_bgimg.jpg");
		FileUtils.copyInputStreamToFile(inputStream, bgFile);

		File qrcodeFile = Files.createTempFile("qrcode_", ".jpg").toFile();
		InputStream qrcodeInputStream = classLoader.getResourceAsStream("default_qrcodeimg.jpg");
		FileUtils.copyInputStreamToFile(qrcodeInputStream, qrcodeFile);

		return increasingImage(qrcodeFile, bgFile, headimgFile, zh, en, flag);
	}

	public static String createPoster2File(String title, String content, String memo) throws Exception {
		ClassLoader classLoader = QrcodeUtils.class.getClassLoader();
		File bgFile = Files.createTempFile("bg_", ".jpg").toFile();
		InputStream inputStream = classLoader.getResourceAsStream("default_book_bgimg.jpg");
		FileUtils.copyInputStreamToFile(inputStream, bgFile);

		BufferedImage bg = ImageIO.read(bgFile);
		Graphics2D g = bg.createGraphics();

		// 消除字体模糊，消除抗锯齿
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// 绘制标题和备注
		g.setColor(new Color(71, 71, 71));
		Font font = new Font("微软雅黑", Font.BOLD, 36);
		g.setFont(font);
		FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);

		int title_width = 0;
		for (int i = 0; i < title.length(); i++) {
			char c = title.charAt(i);
			int char_width = metrics.charWidth(c);
			// 叠加，当前单词的长度
			title_width += char_width;
		}

		int title_x = (bg.getWidth() - title_width) / 2;
		g.drawString(title, title_x, 110);
		g.drawString(memo, 46, bg.getHeight() - 137);

		// 绘制内容文字
		font = new Font("微软雅黑", Font.PLAIN, 36);
		g.setFont(font);

		// 字体
		metrics = FontDesignMetrics.getMetrics(font);

		// 获取字体高度
		int zh_line_height = metrics.getHeight() + 20;

		// 中文字的起始Y坐标
		int zh_y = 200;
		// 起始X坐标
		int zh_x = 120;

		String[] rows = makeZhLineFeed(content, metrics, bg.getWidth() - zh_x * 2).split("\n");
		for (int i = 0; i < rows.length; i++) {
			g.drawString(rows[i], zh_x, zh_y + zh_line_height * i);
		}

		g.dispose();

		File poster = Files.createTempFile("poster_" + IdGen.uuid(), ".jpg").toFile();
		logger.info(poster.getAbsolutePath());

		ImageIO.write(bg, "jpg", poster);

		return poster.getAbsolutePath();
	}

	private static String increasingImage(File qrcodeFile, File bgFile, File headimgFile, String zh, String en, int flag) throws Exception {
		try {
			BufferedImage qrcode = ImageIO.read(qrcodeFile);
			BufferedImage bg = ImageIO.read(bgFile);
			BufferedImage headimg = ImageIO.read(headimgFile);

			Graphics2D g = bg.createGraphics();

			// 图片的起始坐标
			int img_x = margin, img_y = margin;

			// 绘制顶部图
			// 图片的最大宽度和高度
			int img_max_width = bg.getWidth() - margin * 2, img_max_height = 400;
			// 图片的最适宽度和高度
			int img_suitable_width = img_max_width, img_suitable_height = 0;

			if (headimg.getWidth() < img_max_width) {
				img_suitable_width = headimg.getWidth();
				img_suitable_height = headimg.getHeight();

				img_x = (bg.getWidth() - img_suitable_width) / 2;
			} else {
				img_suitable_height = headimg.getHeight() * img_max_width / headimg.getWidth();
			}

			if (img_suitable_height > img_max_height) {
				img_suitable_height = img_max_height;
			}

			g.drawImage(headimg, img_x, img_y, img_suitable_width, img_suitable_height, null);

			// 绘制二维码
			// 二维码宽高
			int qrcode_width = 128, qrcode_height = qrcode_width, qrcode_margin = 15;
			// 二维码起始坐标
			int qrcode_x = bg.getWidth() - qrcode_width - qrcode_margin;
			int qrcode_y = bg.getHeight() - qrcode_height - qrcode_margin;

			g.drawImage(qrcode, qrcode_x, qrcode_y, qrcode_width, qrcode_height, null);

			// 消除字体模糊，消除抗锯齿
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// 绘制中文文字
			g.setColor(new Color(71, 71, 71));
			Font font = new Font("微软雅黑", Font.PLAIN, 28);
			g.setFont(font);

			// 字体
			FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);

			// 获取字体高度
			int zh_line_height = metrics.getHeight();

			// 中文字的起始Y坐标
			int zh_y = img_suitable_height + img_y + 60;
			// 起始X坐标
			int zh_x = margin;

			String[] rows = makeZhLineFeed(zh, metrics, bg.getWidth() - margin * 2).split("\n");
			for (int i = 0; i < rows.length; i++) {
				g.drawString(rows[i], zh_x, zh_y + zh_line_height * i);
			}

			if (StringUtils.isNotEmpty(en)) {
				if (flag == 1) {
					drawEn(g, en, zh_y + zh_line_height * rows.length + 10);
				} else {
					zh_y +=  zh_line_height * rows.length + 10;
					
					g.setColor(new Color(157, 157, 157));
					font = new Font("微软雅黑", Font.PLAIN, 26);
					g.setFont(font);
					metrics = FontDesignMetrics.getMetrics(font);
					int en_line_height = FontDesignMetrics.getMetrics(font).getHeight();
					
					String[] rows1 = makeZhLineFeed(en, metrics, bg.getWidth() - margin * 2).split("\n");
					for (int i = 0; i < rows1.length; i++) {
						g.drawString(rows1[i], zh_x, zh_y + en_line_height * i);
					}
				}
				
			}

			// 个人简介
			g.setColor(new Color(71, 71, 71));
			font = new Font("微软雅黑", Font.PLAIN, 22);
			g.setFont(font);

			// 获取字体高度
			int brief_line_height = FontDesignMetrics.getMetrics(font).getHeight();
			int brief_x = margin;

			g.drawString("我是沉默王二(微信号：qing_gee)", brief_x, bg.getHeight() - margin - brief_line_height * 2 - 10);
			g.drawString("一个不止写程序的全栈工程师", brief_x, bg.getHeight() - margin - brief_line_height - 5);
			g.drawString("只写有趣的文字，给不喜欢严肃的你", brief_x, bg.getHeight() - margin);

			g.dispose();

			File poster = Files.createTempFile("poster_" + IdGen.uuid(), ".jpg").toFile();
			logger.info(poster.getAbsolutePath());

			ImageIO.write(bg, "jpg", poster);

			return poster.getAbsolutePath();
		} catch (Exception e) {
			throw new Exception("海报二维码生成时发生异常！", e);
		}
	}
	
	private static void drawEn(Graphics2D g, String en, int y_start) {
		g.setColor(new Color(157, 157, 157));
		Font font = new Font("微软雅黑", Font.PLAIN, 28);
		g.setFont(font);
		int en_line_height = FontDesignMetrics.getMetrics(font).getHeight();

		// 对英文单词进行自动换行分组
		String[] rows = makeEnLineFeed(en, 42).split("\n");

		for (int i = 0; i < rows.length; i++) {
			g.drawString(rows[i], margin, y_start + en_line_height * i);
		}
	}

	private static String makeEnLineFeed(String str, int max_chars) {
		// 用空格作为分隔符，将单词存到字符数组里面
		String[] strs = str.split(" ");
		// 利用StringBuffer对字符串进行修改
		StringBuilder sb = new StringBuilder();
		// 判断单词长度，计算
		int len = 0;
		for (int i = 0; i < strs.length; i++) {
			// 叠加，当前单词的长度
			len += strs[i].length();

			if (len > max_chars) {
				sb.append("\n" + strs[i] + " ");// 利用StringBuffer对字符串进行修改
				// 另起一行
				len = strs[i].length() + 1;// +1为换行后读出空格一位
			} else {
				sb.append(strs[i] + " ");
				len++;// 多了一个空格
			}
		}
		return sb.toString();
	}

	private static String makeZhLineFeed(String str, FontDesignMetrics metrics, int max_width) {
		StringBuilder sb = new StringBuilder();
		int line_width = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			sb.append(c);
			if (sb.toString().endsWith("\n")) {
				line_width = 0;
				continue;
			}

			int char_width = metrics.charWidth(c);
			line_width += char_width;
			if (line_width >= max_width - char_width) {
				line_width = 0;
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private static File createTmpFile(InputStream inputStream, String name, String ext) throws IOException {
		File tmpFile = File.createTempFile(name, '.' + ext);

		tmpFile.deleteOnExit();

		try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
			int read = 0;
			byte[] bytes = new byte[1024 * 100];
			while ((read = inputStream.read(bytes)) != -1) {
				fos.write(bytes, 0, read);
			}

			fos.flush();
			return tmpFile;
		}
	}

	private static String handleUTF8Response(final HttpResponse response) throws IOException {
		final StatusLine statusLine = response.getStatusLine();
		final HttpEntity entity = response.getEntity();
		if (statusLine.getStatusCode() >= 300) {
			EntityUtils.consume(entity);
			throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}
		return entity == null ? null : EntityUtils.toString(entity, Consts.UTF_8);
	}

	private static InputStream handleResponse(final HttpResponse response) throws IOException {
		final StatusLine statusLine = response.getStatusLine();
		final HttpEntity entity = response.getEntity();
		if (statusLine.getStatusCode() >= 300) {
			EntityUtils.consume(entity);
			throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}
		return entity == null ? null : entity.getContent();
	}
	
}
