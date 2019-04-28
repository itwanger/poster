package com.comwer.poster;

import java.awt.Font;

import sun.font.FontDesignMetrics;

public class FontUtil {

	public static void main(String[] args) {
		Font font = new Font("微软雅黑", Font.PLAIN, 28);
		FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);

		String en = "Fear can hold you prisoner. Hope can set you free. It takes a strong man to save himself, and a great man to save another.";
		String[] rows = makeEnLineFeed(en, metrics, 600).split("\n");
		for (int i = 0; i < rows.length; i++) {
			System.out.println(rows[i]);
		}

	}

	/**
	 * 英文换行
	 * 
	 * @param en
	 * @param metrics
	 * @param max_width
	 * @return
	 */
	public static String makeEnLineFeed(String en, FontDesignMetrics metrics, int max_width) {
		// 每个单词后追加空格
		char space = ' ';
		int spaceWidth = metrics.charWidth(space);

		// 按照空格对英文文本进行拆分
		String[] words = en.split(String.valueOf(space));
		// 利用 StringBuilder 对字符串进行修改
		StringBuilder sb = new StringBuilder();
		// 每行文本的宽度
		int len = 0;

		for (int i = 0; i < words.length; i++) {
			String word = words[i];

			int wordWidth = metrics.stringWidth(word);
			// 叠加当前单词的宽度
			len += wordWidth;

			// 超出最大宽度，进行换行
			if (len > max_width) {
				sb.append("\n");
				sb.append(word);
				sb.append(space);

				// 下一行的起始宽度
				len = wordWidth + spaceWidth;
			} else {
				sb.append(word);
				sb.append(space);

				// 多了一个空格
				len += spaceWidth;
			}
		}
		return sb.toString();
	}

	/**
	 * 中文换行
	 * 
	 * @param zh
	 * @param metrics
	 * @param max_width
	 * @return
	 */
	public static String makeZhLineFeed(String zh, FontDesignMetrics metrics, int max_width) {
		StringBuilder sb = new StringBuilder();
		int line_width = 0;
		for (int i = 0; i < zh.length(); i++) {
			char c = zh.charAt(i);
			sb.append(c);

			// 如果主动换行则跳过
			if (sb.toString().endsWith("\n")) {
				line_width = 0;
				continue;
			}

			// FontDesignMetrics 的 charWidth() 方法可以计算字符的宽度
			int char_width = metrics.charWidth(c);
			line_width += char_width;

			// 如果当前字符的宽度加上之前字符串的已有宽度超出了海报的最大宽度，则换行
			if (line_width >= max_width - char_width) {
				line_width = 0;
				sb.append("\n");
			}
		}
		return sb.toString();
	}

}
