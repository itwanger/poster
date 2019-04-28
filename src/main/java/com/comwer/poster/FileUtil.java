package com.comwer.poster;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static final Path DIRECTORY = Paths.get("D:\\test");

	public static void main(String[] args) throws IOException {
		read("bg_", ".jpg", "default_bgimg.jpg");
		read("qrcode_", ".jpg", "default_qrcodeimg.jpg");
	}

	public static File read(String prefix, String suffix, String resource) throws IOException {
		// ClassLoader.getResourceAsStream() 会从 classpath 的根路径下查找资源
		ClassLoader classLoader = FileUtil.class.getClassLoader();

		File file = Files.createTempFile(DIRECTORY, prefix, suffix).toFile();
		InputStream inputStream = classLoader.getResourceAsStream(resource);
		FileUtils.copyInputStreamToFile(inputStream, file);
		logger.debug("资源：" + file.getAbsolutePath());

		return file;
	}

}
