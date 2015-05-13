package com.juchao.upg.android.net;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.juchao.upg.android.entity.BaseEquipmentAttachment;
import com.juchao.upg.android.entity.BaseImage;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.FileUtil;

public class HttpUtils {
	// 网络连接部分
	public synchronized static String doPost(String strUrl,
			Map<String, String> params) {

		return CustomHttpURLConnection.doPost(strUrl, params);
	}

	public synchronized static String doGet(String strUrl,
			Map<String, String> params) {

		return CustomHttpURLConnection.doGet(strUrl, params);
	}

	public static String getBaseUrl() {
		String baseUrl = null;
		String ipAddr = DefaultShared.getString(Constants.KEY_SERVICE_ADDRESS,
				"");
		String port = DefaultShared.getString(Constants.KEY_SERVICE_PORT, "");

		if (TextUtils.isEmpty(ipAddr) || TextUtils.isEmpty(port)) {
			baseUrl = Constants.BASE_URL;
		} else {
			baseUrl = "http://" + ipAddr + ":" + port + "/baoquan";
		}
		return baseUrl;
	}

	public static String getBaseIp() {
		String ipAddr = DefaultShared.getString(Constants.KEY_SERVICE_ADDRESS,
				"");
		if (TextUtils.isEmpty(ipAddr)) {
			return Constants.BASE_IP;
		} else {
			return ipAddr;
		}

	}

	public static String getBasePort() {
		String port = DefaultShared.getString(Constants.KEY_SERVICE_PORT, "");
		if (TextUtils.isEmpty(port)) {
			return Constants.BASE_PORT;
		} else {
			return port;
		}
	}

	public static void downloadImage(String url, Map<String, String> params,
			BaseImage image) {
		String token = params.get("token");

		try {
			File file;
			if (image.id == null || image.id.trim() == "0") {
				String name = url.substring(url.lastIndexOf("/") + 1,
						url.length());
				file = new File(
						com.juchao.upg.android.util.FileUtil.getBaseImageDir(),
						name);
			} else {
//				String name = url.substring(url.lastIndexOf("id=") + 1,
//						url.length());
				//String curUrl = image.url;
				String name = image.id + ".jpg" ;
				
				//String name=curUrl.substring(curUrl.lastIndexOf("/") + 1, curUrl.length());
				//image.url.substring(image.url.last, end)
				//String name=url.substring(curUrl.lastIndexOf("/") + 1, curUrl.length());
				
				file = new File(
						com.juchao.upg.android.util.FileUtil.getBaseImageDir(),
						name);
			}

			if (file != null && file.exists()) {
				file.delete();
			}
			 CustomHttpURLConnection.downloadFile(url,file);

//			if (is != null) {
//				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//				byte[] buffer = new byte[1024 *10];
//				while (true) {
//					int len = is.read(buffer);
//					if (len == -1) {
//						break;
//					}
//					arrayOutputStream.write(buffer, 0, len); // 写入
//				}
//				arrayOutputStream.flush();
//				arrayOutputStream.close();
//				is.close();
//				
//				byte[] data = arrayOutputStream.toByteArray();
//				//Bitmap bitmap=BitmapFactory.decodeByteArray(data, 0, data.length);
//				
//				//BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
//				
//				//bitmap=ClientUtil.compressImage(bitmap);
//				//bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//				FileOutputStream fileOutputStream = new FileOutputStream(file);
//				fileOutputStream.write(data);
//				// 记得关闭输入流
//				//bos.flush();
//				//bos.close();
//				fileOutputStream.close();
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void downloadAttachment(String url,
			BaseEquipmentAttachment att) {
		
		try{
			File file;
			file = new File(FileUtil.getBaseEquipmentAttachmentDir(),att.fileName);
			if(file !=null && file.exists())
				file.delete();
			
			CustomHttpURLConnection.downloadFile(url,file);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
