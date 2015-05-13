package com.juchao.upg.android.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class CustomHttpURLConnection {
	private static String TAG = CustomHttpURLConnection.class.getSimpleName();
	private static HttpURLConnection conn;
	private static final String CHARSET_UTF8 = HTTP.UTF_8;

	public CustomHttpURLConnection() {
	}

	public static String doGet(String strUrl, Map<String, String> params) {
		String result = "";
		URL url = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(strUrl);
			if (params != null && params.size() > 0) {
				sb.append("?");
				int i = 0;
				for (Map.Entry<String, String> m : params.entrySet()) {
					if (i > 0) {
						sb.append("&");
					}
					i++;
					sb.append(String.format("%s=%s", m.getKey(), m.getValue()));
				}
			}
			Log.d(TAG, "request url : " + sb.toString());

			url = new URL(sb.toString());

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(10 *1000);
			conn.setReadTimeout(10 * 1000);
			conn.setRequestProperty("accept", "*/*");
			// int resCode=conn.getResponseCode();
			conn.connect();
			Log.d(TAG, (new Date() + ""));
			InputStream stream = conn.getInputStream();
			Log.d(TAG, (new Date() + ""));
			InputStreamReader inReader = new InputStreamReader(stream);
			BufferedReader buffer = new BufferedReader(inReader);
			String strLine = null;
			while ((strLine = buffer.readLine()) != null) {
				result += strLine;
			}
			inReader.close();
			conn.disconnect();
			Log.d(TAG, "http result : " + result);
			return result;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "getFromWebByHttpUrlCOnnection:" + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "getFromWebByHttpUrlCOnnection:" + e.getMessage());
			Log.e("Connection Error", "error url:" + url);
			e.printStackTrace();
			return null;
		}
	}

	public static String doPost(String strUrl, Map<String, String> params) {
		String result = "";
		try {

			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			// 设置是否从httpUrlConnection读入，默认情况下是true;
			conn.setDoInput(true);
			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true, 默认情况下是false;
			conn.setDoOutput(true);
			// 设定请求的方法为"POST"，默认是GET
			conn.setRequestMethod("POST");
			// 设置超时
			conn.setConnectTimeout(10*1000);
			conn.setReadTimeout(10*1000);
			// Post 请求不能使用缓存
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			// 设定传送的内容类型是可序列化的java对象
			// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，
			conn.connect();
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			byte[] data = getRequestData(params, CHARSET_UTF8).toString()
					.getBytes(); // 获得请求体
			out.write(data);
			out.flush();
			out.close(); // flush and close

			InputStream in = conn.getInputStream();
			InputStreamReader inStream = new InputStreamReader(in);
			BufferedReader buffer = new BufferedReader(inStream);
			String strLine = null;
			while ((strLine = buffer.readLine()) != null) {
				result += strLine;
			}
			Log.d(TAG, "http post result : " + result);
			return result;
		} catch (IOException ex) {
			Log.e(TAG, "PostFromWebByHttpURLConnection：" + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	public static StringBuffer getRequestData(Map<String, String> params,
			String encode) {
		StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
			System.out.println(stringBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static void downloadFile(String downUrl, File file) {
		boolean isSuccess = false;
		InputStream inputStream = null;
		try {
			URL url = new URL(downUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setConnectTimeout(10 * 1000);
			http.setReadTimeout(10 * 1000);
			http.setRequestMethod("GET");
			http.connect(); // 连接
			if (http.getResponseCode() == 200) {
				inputStream = http.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		saveDownloadFile(inputStream, file);
		// return inputStream;
	}

	private static void saveDownloadFile(InputStream is, File file) {
		if (is != null) {

			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 10];
			try {
				while (true) {
					int len;

					len = is.read(buffer);
					if (len == -1) {
						break;
					}
					arrayOutputStream.write(buffer, 0, len); // 写入
				}
				arrayOutputStream.flush();
				arrayOutputStream.close();
				is.close();
				byte[] data = arrayOutputStream.toByteArray();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(data);
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
