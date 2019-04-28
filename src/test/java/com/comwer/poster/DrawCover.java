package com.comwer.poster;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.font.FontDesignMetrics;

public class DrawCover {
	private static Logger logger = LoggerFactory.getLogger(DrawCover.class);

	/**
	 * 留白
	 */
	private static final int MARGIN = 25;
	/**
	 * 使用的字体
	 */
	private static final String USE_FONT_NAME = "微软雅黑";

	public static void main(String[] args) throws IOException {
		logger.debug("将网络图片绘制成海报封面");

		// 背景
		File bgFile = FileUtil.read("bg_", ".jpg", "default_bgimg.jpg");
		// 请求在虚拟机终止时删除此抽象路径名所表示的文件或目录。
		bgFile.deleteOnExit();

		// BufferedImage 使用可访问的图像数据缓冲区描述图像，由颜色模型和图像数据栅格组成。
		// 所有 BufferedImage 对象的左上角坐标为(0，0)。
		BufferedImage bgImage = ImageIO.read(bgFile);

		// 绘制封面图
		Graphics2DPoster graphics2dPoster = drawImage(bgImage);

		String zh = "懦怯囚禁人的灵魂，希望可以令你感受自由。强者自救，圣者渡人。";
		graphics2dPoster.setZh(zh);
		drawZhString(graphics2dPoster);

		String en = "Fear can hold you prisoner. Hope can set you free. It takes a strong man to save himself, and a great man to save another.";
		graphics2dPoster.setEn(en);
		drawEnString(graphics2dPoster);

		// 二维码
		File qrcodeFile = FileUtil.read("qrcode_", ".jpg", "default_qrcodeimg.jpg");
		qrcodeFile.deleteOnExit();
		BufferedImage qrcodeImage = ImageIO.read(qrcodeFile);
		graphics2dPoster.setQrcodeImage(qrcodeImage);
		drawQrcode(graphics2dPoster);

		// 释放图形上下文，以及它正在使用的任何系统资源。
		graphics2dPoster.getGraphics2d().dispose();

		File posterFile = Files.createTempFile(FileUtil.DIRECTORY, "poster_", ".jpg").toFile();
		ImageIO.write(graphics2dPoster.getBgImage(), "jpg", posterFile);

		logger.debug("绘制好封面图的海报" + posterFile.getAbsolutePath());
	}

	private static void drawQrcode(Graphics2DPoster graphics2dPoster) {
		BufferedImage qrcodeImage = graphics2dPoster.getQrcodeImage();
		BufferedImage bgImage = graphics2dPoster.getBgImage();

		// 二维码起始坐标
		int qrcode_x = bgImage.getWidth() - qrcodeImage.getWidth() - MARGIN;
		int qrcode_y = bgImage.getHeight() - qrcodeImage.getHeight() - MARGIN;

		Graphics2D graphics2d = graphics2dPoster.getGraphics2d();
		graphics2d.drawImage(qrcodeImage, qrcode_x, qrcode_y, qrcodeImage.getWidth(), qrcodeImage.getHeight(), null);

		// 追加二维码描述文本
		graphics2d.setColor(new Color(71, 71, 71));
		Font font = new Font(USE_FONT_NAME, Font.PLAIN, 22);
		graphics2d.setFont(font);
		FontDesignMetrics metrics = FontDesignMetrics.getMetrics(graphics2d.getFont());

		graphics2d.drawString("沉默王二", MARGIN, bgImage.getHeight() - MARGIN - metrics.getHeight() * 2);
		graphics2d.drawString("一个幽默的程序员", MARGIN, bgImage.getHeight() - MARGIN - metrics.getDescent());
	}

	private static void drawEnString(Graphics2DPoster graphics2dPoster) throws IOException {
		// 设置封面图和下方中文之间的距离
		graphics2dPoster.addCurrentY(20);

		Graphics2D graphics2d = graphics2dPoster.getGraphics2d();
		graphics2d.setColor(new Color(157, 157, 157));

		FontDesignMetrics metrics = FontDesignMetrics.getMetrics(graphics2d.getFont());
		String enWrap = FontUtil.makeEnLineFeed(graphics2dPoster.getEn(), metrics, graphics2dPoster.getSuitableWidth());
		String[] enWraps = enWrap.split("\n");
		for (int i = 0; i < enWraps.length; i++) {
			graphics2dPoster.addCurrentY(metrics.getHeight());
			graphics2d.drawString(enWraps[i], MARGIN, graphics2dPoster.getCurrentY());
		}

	}

	private static Graphics2DPoster drawZhString(Graphics2DPoster graphics2dPoster) throws IOException {
		// 获取计算机上允许使用的中文字体
		List<String> fontNames = Arrays
				.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		if (fontNames == null || !fontNames.contains(USE_FONT_NAME)) {
			throw new RuntimeException("计算机上未安装" + USE_FONT_NAME + "的字体");
		}

		// 设置封面图和下方中文之间的距离
		graphics2dPoster.addCurrentY(30);

		Graphics2D graphics2d = graphics2dPoster.getGraphics2d();
		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Font 的构造参数依次是字体名字，字体式样，字体大小
		Font font = new Font(USE_FONT_NAME, Font.PLAIN, 28);
		graphics2d.setFont(font);
		graphics2d.setColor(new Color(71, 71, 71));

		FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
		graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

		String zhWrap = FontUtil.makeZhLineFeed(graphics2dPoster.getZh(), metrics, graphics2dPoster.getSuitableWidth());
		String[] zhWraps = zhWrap.split("\n");
		for (int i = 0; i < zhWraps.length; i++) {
			graphics2dPoster.addCurrentY(metrics.getHeight());
			graphics2d.drawString(zhWraps[i], MARGIN, graphics2dPoster.getCurrentY());
		}

		return graphics2dPoster;
	}

	private static Graphics2DPoster drawImage(BufferedImage bgImage) throws IOException {
		// 封面图
		File picFile = CapturePic.capture();
		picFile.deleteOnExit();

		BufferedImage picImage = ImageIO.read(picFile);

		// 封面图的起始坐标
		int pic_x = MARGIN, pic_y = MARGIN;
		// 封面图的宽度
		int pic_width = bgImage.getWidth() - MARGIN * 2;
		// 封面图的高度
		int pic_height = picImage.getHeight() * pic_width / picImage.getWidth();

		// Graphics2D 类扩展 Graphics 类，以提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制。
		Graphics2D graphics2d = bgImage.createGraphics();
		Graphics2DPoster graphics2dPoster = new Graphics2DPoster(graphics2d);
		// 海报可容纳的宽度
		graphics2dPoster.setSuitableWidth(pic_width);

		graphics2dPoster.setBgImage(bgImage);

		// 在背景上绘制封面图
		graphics2d.drawImage(picImage, pic_x, pic_y, pic_width, pic_height, null);

		// 记录此时的 y 坐标
		graphics2dPoster.setCurrentY(pic_y + pic_height);

		return graphics2dPoster;
	}

}
