package com.comwer.poster;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Graphics2DPoster {
	private Graphics2D graphics2d;
	private int suitableWidth;
	private int currentY;
	private String zh;
	private String en;
	private BufferedImage bgImage;
	private BufferedImage qrcodeImage;
	private BufferedImage picImage;

	public BufferedImage getBgImage() {
		return bgImage;
	}

	public void setBgImage(BufferedImage bgImage) {
		this.bgImage = bgImage;
	}

	public BufferedImage getQrcodeImage() {
		return qrcodeImage;
	}

	public void setQrcodeImage(BufferedImage qrcodeImage) {
		this.qrcodeImage = qrcodeImage;
	}

	public Graphics2DPoster(Graphics2D graphics2d) {
		super();
		this.graphics2d = graphics2d;
	}

	public Graphics2D getGraphics2d() {
		return graphics2d;
	}

	public int getCurrentY() {
		return currentY;
	}

	public void setCurrentY(int currentY) {
		this.currentY = currentY;
	}

	public void addCurrentY(int y) {
		setCurrentY(getCurrentY() + y);
	}

	public String getZh() {
		return zh;
	}

	public void setZh(String zh) {
		this.zh = zh;
	}

	public int getSuitableWidth() {
		return suitableWidth;
	}

	public void setSuitableWidth(int suitableWidth) {
		this.suitableWidth = suitableWidth;
	}

	public String getEn() {
		return en;
	}

	public void setEn(String en) {
		this.en = en;
	}

	public BufferedImage getPicImage() {
		return picImage;
	}

	public void setPicImage(BufferedImage picImage) {
		this.picImage = picImage;
	}
}
