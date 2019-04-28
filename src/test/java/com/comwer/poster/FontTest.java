package com.comwer.poster;

import java.awt.GraphicsEnvironment;

public class FontTest {

	public static void main(String[] args) {
String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

for (String fontName : fontNames) {
	System.out.println(fontName);
}

System.out.println(fontNames.length);
System.out.println("The function returns the string, but with line breaks".length());
	}

}
