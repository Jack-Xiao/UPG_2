package com.juchao.upg.android.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public class HttpPostFileUtils {

	private static final String TAG = HttpPostFileUtils.class.getSimpleName();

	/**
	 * <pre>
	 * 发送带参数的POST的HTTP请求
	 * </pre>
	 * 
	 * @param reqUrl
	 *            HTTP请求URL
	 * @param parameters
	 *            参数映射表
	 * @return HTTP响应的字符串
	 */
	public static synchronized String postContentAndPic(Context context,
			String reqUrl, String token, File imageFile) throws Exception {

		String recvEncoding = "UTF-8";
		String method = "POST";
		final String BOUNDARY = "---------------------------"
				+ System.currentTimeMillis();// 分割符
		final String PREFIX = "--"; // 前缀
		final String LINEND = "\r\n"; // 换行符
		final String MULTIPART_FROM_DATA = "multipart/form-data";// 数据类型
		final String CHARSET = "UTF-8";// 字符编码
		HttpURLConnection url_con = null;
		String responseContent = null;
		byte[] data = null;
		try {
			
			Log.d(TAG, "Image file path :  " + imageFile.getAbsolutePath());
			
			reqUrl += "?token=" + token; 
			Log.d(TAG, "reqUrl :  " + reqUrl);
			URL url = new URL(reqUrl);
			if (url.getProtocol().toLowerCase().equals("https")) {

				HttpsURLConnection https = (HttpsURLConnection) url
						.openConnection();
				// https.setHostnameVerifier(DO_NOT_VERIFY);
				url_con = https;
			} else {
				url_con = (HttpURLConnection) url.openConnection();
			}
			url_con.setConnectTimeout(10000);// （单位：毫秒）jdk
			// 1.5换成这个,连接超时
			url_con.setReadTimeout(10000);// （单位：毫秒）jdk 1.5换成这个,读操作超时

			if (Build.VERSION.SDK_INT < 14) {
				// url_con.setDoOutput(true);
			}
			url_con.setRequestMethod(method);
			url_con.setDoInput(true);
			url_con.setDoOutput(true);
			url_con.setUseCaches(false);
			url_con.setInstanceFollowRedirects(false);
			url_con.setRequestProperty("Connection", "Keep-Alive");
			url_con.setRequestProperty("Charset", "UTF-8");
			url_con.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);
			// Log.i("tweet","url = " +reqUrl + ",responsecode ="
			// +url_con.getResponseCode());
			// 内容
			if (method.equals("POST")) {
//				StringBuffer params = new StringBuffer();
//				for (Iterator iter = parameters.entrySet().iterator(); iter
//						.hasNext();) {
//					Entry element = (Entry) iter.next();
//					params.append(PREFIX);
//					params.append(BOUNDARY);
//					params.append(LINEND);
//					params.append("Content-Disposition: form-data; name=\""
//							+ element.getKey() + "\"" + LINEND);
//					// params.append("Content-Type: text/plain; charset=" +
//					// CHARSET + LINEND);
//					// params.append("Content-Transfer-Encoding: 8bit" +
//					// LINEND);
//					params.append(LINEND);
//					params.append(element.getValue());
//					params.append(LINEND);
//				}

				// String[] imgPath= imgPaths.split(";");
				if (imageFile != null) {
//					Log.d(TAG, "upload image params > " + params.toString());
//					url_con.getOutputStream().write(
//							params.toString().getBytes(), 0,
//							params.toString().getBytes().length);

					// 图片
//					for (int i = 0; i < photoFiles.length; i++) {
						StringBuffer param = new StringBuffer();
						// File imgFile = new File(imgPath[i]);
						param.append(PREFIX);
						param.append(BOUNDARY);
						param.append(LINEND);
						/*
						 * if (isRenRen) {
						 * params.append("Content-Disposition: form-data; name=\""
						 * + "upload" + "\"; filename=\"" +
						 * imgFile.getName().toString() + "\"" + LINEND); } else
						 * {
						 */
						param.append("Content-Disposition: form-data; name=\""
								+ "image\"; filename=\""
								+imageFile.getName().toString() + "\""
								+ LINEND);
						// }
						// params.append("Content-Type: application/octet-stream; charset="
						// + CHARSET + LINEND);
						param.append("Content-Type: image/*");
						/*
						 * param.append(
						 * "Content-Type: application/octet-stream; charset=" +
						 * CHARSET);
						 */
						param.append(LINEND);
						param.append(LINEND);
						Log.i("tweet", "url =" + reqUrl);

						url_con.getOutputStream().write(
								param.toString().getBytes(), 0,
								param.toString().getBytes().length);
						// data =
						// Bitmap2Bytes(BitmapUtils.getLoacalBitmap(imgPath));
						InputStream is = new FileInputStream(imageFile);
						try {
							ByteArrayOutputStream outStream = new ByteArrayOutputStream();

							int bytesAvailable;
							while ((bytesAvailable = is.available()) > 0) {
								int bufferSize = Math.min(bytesAvailable,
										4096 * 1024);
								byte[] buffer = new byte[bufferSize];
								int bytesRead = is.read(buffer, 0, bufferSize);
								outStream.write(buffer, 0, bytesRead);
								outStream.flush();
								outStream.close();
								// Log.i("tweet", "size =" + bytesAvailable
								// + "bytesRead =" + bytesRead + "buffer"
								// + buffer);
								url_con.getOutputStream().write(buffer, 0,
										bytesRead);
								url_con.getOutputStream().write(
										LINEND.getBytes());

							}
						} catch (IOException e) {
							throw new Exception(e);
						} finally {
							if (null != is) {
								try {
									is.close();
								} catch (IOException e) {
									throw new Exception(e);
								}
							}
						}
//					}

					// 请求结束标志
					byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND)
							.getBytes();
					url_con.getOutputStream().write(end_data);

					url_con.getOutputStream().flush();
					url_con.getOutputStream().close();
				} 

			}

			InputStream in = url_con.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in,
					recvEncoding));
			String tempLine = rd.readLine();
			StringBuffer tempStr = new StringBuffer();
			String crlf = System.getProperty("line.separator");
			while (tempLine != null) {
				tempStr.append(tempLine);
				tempStr.append(crlf);
				tempLine = rd.readLine();
			}
			responseContent = tempStr.toString();
			rd.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			// logger.error("网络故障", e);
		} finally {
			if (url_con != null) {
				url_con.disconnect();
			}
		}
		// Log.i("tweet", "responseContent = " + responseContent);
		return responseContent;
	}

}
