/**
 * 2009-12-5
 */
package com.juchao.upg.android.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.juchao.upg.android.util.Log;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;



/**
 * <p>Title: NetIOUtils.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Eshore</p>
 * <p><a href="NetIOUtils.java.html"><i>View Source</i></a></p>
 * @version 1.0
 */
public class NetIOUtils {

	private static final String LOG_TAG = "NetIOUtils";
	public static Uri PREFERRED_APN_URI = Uri
	.parse("content://telephony/carriers/preferapn");
	public static boolean isNetworkAvailable(Activity mActivity) {  
	    Context context = mActivity.getApplicationContext(); 
	    ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    if (connectivity == null) {     
	      return false; 
	    } else {   
	        NetworkInfo[] info = connectivity.getAllNetworkInfo();     
	        if (info != null) {         
	            for (int i = 0; i < info.length; i++) {            
	                if (info[i].getState() == NetworkInfo.State.CONNECTED) {               
	                    return true;  
	                }         
	            }      
	        }  
	    }    
	    return false; 
	}	
	/**
	 * 获取是否已连接WIFI
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWiFiConnected(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		String mSSID = wifiManager.getConnectionInfo().getSSID();
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null) {
				if (info.getTypeName().equals("WIFI") && info.isConnected()) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isNetworkOk(Context context1) {  
	    Context context = context1; 
	    ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    if (connectivity == null) {     
	      return false; 
	    } else {   
	        NetworkInfo[] info = connectivity.getAllNetworkInfo();     
	        if (info != null) {         
	            for (int i = 0; i < info.length; i++) {            
	                if (info[i].getState() == NetworkInfo.State.CONNECTED) {               
	                    return true;  
	                }         
	            }      
	        }  
	    }    
	    return false; 
	}	
	/**
	 * 
	 * @param url
	 * @return 
	 * String
	 */
//	public static String getUrlResponse(String url) {
//		String result = null;
//		HttpGet get = new HttpGet(url);
//		// Log.d(LOG_TAG, "url: " + url);
//		// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
//		HttpParams params = new BasicHttpParams();
//		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
//		HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
//		HttpConnectionParams.setSoTimeout(params, 60 * 1000);
//		HttpConnectionParams.setSocketBufferSize(params, 8192);
//		// 设置重定向，缺省为 true
//		HttpClientParams.setRedirecting(params, true);
//		// 设置 user agent
//		//HttpProtocolParams.setUserAgent(params, userAgent);
//		/**
//		* 创建一个 HttpClient 实例
//		* 注意 HttpClient httpClient = new HttpClient(); 是Commons HttpClient
//		* 中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient
//		*/
//		HttpClient client = new DefaultHttpClient(params);
//		//HttpClient client = new DefaultHttpClient();
//		try {			
//			HttpResponse response = client.execute(get);
//			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
//				 // 错误处理
//				get.abort();   
//				//return String.valueOf(response.getStatusLine().getStatusCode());
//				return Constants.SC_BAD_REQUEST;
//			}
//			HttpEntity entity = response.getEntity();
//			return convertStreamToString(entity.getContent());
//		} catch (SocketTimeoutException e) {
//			result = Constants.SO_TIME_OUT;
//			Log.e(LOG_TAG, e.getMessage(), e);
//		} catch (Exception e) {
//			Log.e(LOG_TAG, e.getMessage(), e);
//		} finally {
//			// 释放连接
//			client.getConnectionManager().shutdown();
//		}
//		return result;
//	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is),
				8 * 1024);
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			sb.delete(0, sb.length());
		//	sb.append(Constants.NET_IO_ERROR);
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}
		}

		return sb.toString();
	}
//	public static Bitmap getBitmapFromUrl(Context context, String urlPath) {
//		try{
//			return BaseAccessor.getBitmapFromUrl(context, urlPath);
//		}catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
//		 
//	}
	public static Bitmap getBitmapFromUrl1(URL url) {
		Bitmap bitmap = null;
		InputStream in = null;
		OutputStream out = null;

		try {
			in = new BufferedInputStream(url.openStream(), 4 * 1024);

			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, 4 * 1024);
			copy(in, out);
			out.flush();

			final byte[] data = dataStream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			//Log.e(LOG_TAG, "bitmap returning something");
			return bitmap;
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			closeStream(in);
			closeStream(out);
		}
		//Log.e(LOG_TAG, "bitmap returning null");
		return null;
	}

	public static Drawable getDrawableFromUrl(URL url) {
		try {
			InputStream is = url.openStream();
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		return null;
	}
	 public static Drawable getDrawableFromUrl(String strurl) {
	        try {
	        	URL url = new URL(strurl);
	            InputStream is = url.openStream();
	            Drawable d = Drawable.createFromStream(is, "src");
	            return d;
	        } catch (MalformedURLException e) {
//	            e.printStackTrace();
	        } catch (IOException e) {
//	            e.printStackTrace();
	        }
	        return null;
	    }
	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[4 * 1024];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}
		}
	}
}
