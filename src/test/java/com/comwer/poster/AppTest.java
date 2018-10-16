package com.comwer.poster;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		String en = "Had I not seen the sun, I could have borne";
		System.out.println(en.length());
		System.out.println(en.substring(0, 34));
		System.out.println(en.substring(34));
		
		String en_t = makelinefeed(en);
		System.out.println(en_t);
		
		for (String en_ : en_t.split("\n")) {
			System.out.println(en_);
		}
		
		System.out.println(QrcodeUtils.makeZhLineFeed("我们笑着说再见，却深知再见遥遥无期。我们笑着说再见，却深知再见遥遥无期。我们笑着说再见，却深知再见遥遥无期。我们笑着说再见，却深知再见遥遥无期我们笑着说再见，却深知再见遥遥无期", 17));
		
	}

	public String makelinefeed(String s) {
		// 用空格作为分隔符，将单词存到字符数组里面
		String[] str = s.split(" ");
		// 利用StringBuffer对字符串进行修改
		StringBuffer buffer = new StringBuffer();
		// 判断单词长度，计算
		int len = 0;
		for (int i = 0; i < str.length; i++) {
			// System.out.println(str);
			// len = str[i].length();
			// 叠加
			len += str[i].length();
			// System.out.println(len);
			if (len > 34) {
				buffer.append("\n" + str[i] + " ");// 利用StringBuffer对字符串进行修改
				len = str[i].length() + 1;// +1为换行后读出空格一位
				// System.out.println(len);
			} else {
				buffer.append(str[i] + " ");
				len++;
				// System.out.println(len);
			}
		}
		return buffer.toString();
	}

}
