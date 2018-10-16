package com.comwer.poster;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 生成海报。
 *
 */
public class App {
	public static void main(String[] args) {
		try {
			String zh = "我本可以忍受黑暗，如果我不曾见过太阳。";
			String en = "We laughed and kept saying see you soon, but inside we both knew we'd never see each other again.";
			String imgURL = "";
			
			// 获取词霸的图片
			if (imgURL.equals("")) {
				String formatDate = DateFormatUtils.format(new Date(), "yyyyMMdd");
				imgURL = "http://cdn.iciba.com/news/word/big_" + formatDate + "b.jpg";
			}

			QrcodeUtils.createQrcodeFile(zh, en, imgURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
