package com.juchao.upg.android.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.juchao.upg.android.R;
import com.juchao.upg.android.ui.StartActivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.view.Display;

public class ClientUtil {
	
	private static final int COMPRESSION=30;
	/**
	 * 获取屏幕的宽高
	 * @param mActivity
	 * @return
	 */
	public static int[] getWidthHeight(Activity mActivity){
		int[] w_h = new int[2]; 
		Display display = mActivity.getWindowManager().getDefaultDisplay();
		w_h[0] = display.getWidth();
		w_h[1] = display.getHeight();
		return w_h;
	}
	
	
	/**
     * 将dip转换为px
     * 
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px转换为dip
     * 
     * @param context
     * @param dipValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
    }
    
    public static String getSDPath(){
    	  File sdDir = null;
    	  boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); 
    	  //判断sd卡是否存在
    	  if (sdCardExist){
    		  sdDir = Environment.getExternalStorageDirectory();//获取跟目录
    		  return sdDir.toString();
    	  }
    	  return null;
    }
    
    public static String getRootDir(){
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	public static String getDownDir(){
		String path = getRootDir() + "/upg/apk";
		
		File file = new File(path);
		if(!file.exists()){//判断文件夹目录是否存在  
			file.mkdirs();//如果不存在则创建  
         }  
		return file.getAbsolutePath();
	}
	
	public static String getImageDir(){
		String path = getRootDir() + "/upg/image";
		
		File file = new File(path);
		if(!file.exists()){//判断文件夹目录是否存在  
			file.mkdirs();//如果不存在则创建  
         }  
		return file.getAbsolutePath();
	}
	
	/**
	 * 保存图片
	 * @param Bmp
	 * @param filePath
	 */
	public static void saveImage(Bitmap Bmp,String fileName){
		try {
	    	File path = new File(getImageDir() ,fileName);// 给新照的照片文件命名
//	    	path.createNewFile();
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(path));
			/* 采用压缩转档方法 */
			//Bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
			Bmp.compress(Bitmap.CompressFormat.PNG, 80, bos);
			/* 调用flush()方法，更新BufferStream */
			bos.flush();
			/* 结束OutputStream */
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public static Bitmap compressImage(Bitmap image) {  
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 80;  
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            options -= 10;//每次都减少10 
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  

    }  
	
	public static void saveImage1(Bitmap Bmp,String fileName){
		try {
	    	File path = new File(getImageDir() ,fileName);// 给新照的照片文件命名
//	    	path.createNewFile();
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(path));
			/* 采用压缩转档方法 */
			//Bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
			Bmp.compress(Bitmap.CompressFormat.PNG, 30, bos);
			/* 调用flush()方法，更新BufferStream */
			bos.flush();
			/* 结束OutputStream */
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Bitmap getThumbnailImage(String fileName ,int w ,int h){
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
	   // 返回为空
		String path = getImageDir() + File.separator +fileName;
		BitmapFactory.decodeFile(path , opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
		if (width > w || height > h) {
		// 缩放
		scaleWidth = ((float) width) / w;
		scaleHeight = ((float) height) / h;
		}
	    opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int)scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
		return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	}
	/**
	 * 
	 * @param usedTime 单位：秒
	 * @return
	 */
	public static String getUsedTime(int usedTime){
		int minute = 0;
		int second = 0;
		if(usedTime >= 60){
			minute = (int) usedTime / 60;
			second = (int)(usedTime % 60);
			return minute + "分" + second + "秒";
		}else{
			return "0分" + usedTime + "秒";
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getLocalMacAddress(Context context) {  
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info = wifi.getConnectionInfo();  
        return info.getMacAddress();  
    } 
	
	@SuppressWarnings("deprecation")
	public static void showNotification(Context context,String title, String summary) {
		NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification();
		n.flags |= Notification.FLAG_SHOW_LIGHTS;
      	n.flags |= Notification.FLAG_AUTO_CANCEL;
        n.defaults = Notification.DEFAULT_ALL;
		n.icon = R.drawable.messageremind;
		n.when = System.currentTimeMillis();
				// Simply open the parent activity
		Intent intent = new Intent(context,StartActivity.class);
//		broadcastIntent.putExtra(PushConstants.APP_KEY, APPKEY);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		
		// Change the name of the notification here
		n.setLatestEventInfo(context, title, summary, pi);
		mNotificationManager.notify(0, n);
	}
	
	public static String getTimeFormat(long time){
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
	
	public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 720f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
}
