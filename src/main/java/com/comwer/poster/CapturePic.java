package com.comwer.poster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
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

public class CapturePic {
	private static Logger logger = LoggerFactory.getLogger(CapturePic.class);
	
	private static String handleResponse(final HttpResponse response, Charset charset) throws IOException {
		HttpEntity entity = handleResponse(response);
		return entity == null ? null : EntityUtils.toString(entity, charset);
	}

	private static HttpEntity handleResponse(final HttpResponse response) throws IOException {
		final StatusLine statusLine = response.getStatusLine();
		final HttpEntity entity = response.getEntity();
		if (statusLine.getStatusCode() >= 300) {
			EntityUtils.consume(entity);
			throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}
		return entity;
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

	public static void main(String[] args) throws IOException {
		logger.debug("根据输入的路径采集网络图片");
		
		// 根据路径发起 HTTP get 请求
		HttpGet httpget = new HttpGet(args[0]);
		httpget.addHeader("Content-Type", "text/html;charset=UTF-8");
		
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(500).setConnectTimeout(500)
				.setSocketTimeout(500).build();
		httpget.setConfig(requestConfig);

		File pic = null;

		// 
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try (CloseableHttpResponse response = httpclient.execute(httpget);
				InputStream headimgStream = handleResponse(response).getContent();) {

			Header[] contentTypeHeader = response.getHeaders("Content-Type");
			if (contentTypeHeader != null && contentTypeHeader.length > 0) {
				if (contentTypeHeader[0].getValue().startsWith(ContentType.APPLICATION_JSON.getMimeType())) {

					// application/json; encoding=utf-8 下载媒体文件出错
					String responseContent = handleResponse(response, Consts.UTF_8);

					logger.warn("下载网络顶部图出错{}", responseContent);
				}
			}

			pic = createTmpFile(headimgStream, "headimg_" + IdGen.uuid(), "jpg");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			pic = Files.createTempFile("headimg_" + IdGen.uuid(), ".jpg").toFile();

			ClassLoader classLoader = CapturePic.class.getClassLoader();
			InputStream headimgStream1 = classLoader.getResourceAsStream("default_headimg.jpg");
			FileUtils.copyInputStreamToFile(headimgStream1, pic);
		} finally {
			httpget.releaseConnection();
		}

		if (pic != null && !pic.exists()) {
			throw new IllegalArgumentException("请提供正确的网络图片路径！");
		}
	}

}
