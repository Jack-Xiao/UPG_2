package com.juchao.upg.android.crash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {
	
	private static final String TAG=CrashHandler.class.getSimpleName();
	//系统默认的 UncaughtException 处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	//CrashHandler实例
	private static CrashHandler INSTANCE=new CrashHandler();
	//程序的Context 对象
	private Context mContext;
	//用来存储设备信息和异常信息
	private Map<String,String> infos=new HashMap<String,String>();
	//用于格式化日期，作为日志文件名的一部分
	private DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
	
	/** 保证只有一个CrashHandler实例 */
	
	private CrashHandler(){
	}
	//获取CrashHandler实例，单例模式
	public static CrashHandler getInstance(){
		return INSTANCE;
	}
	
	/**
	 * 初始化
	 * @param context
	 */
	public void init(Context context){
		mContext=context;
		//获取系统默认的UncaughtException处理器
		mDefaultHandler=Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	/**
	 * 当UncaughtException 发生时会转入该函数来处理
	 */
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(!handleException(ex) && mDefaultHandler !=null){
			//如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		}else{
			try{
				Thread.sleep(3000);
			}catch(InterruptedException e){
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
			}
		}
		
	}
	private boolean handleException(Throwable ex) {
		if(ex == null){
			return false;
		}
		//使用Toast来显示异常信息
		new Thread(){
			@Override
			public void run(){
				Looper.prepare();
				Toast.makeText(mContext, "发生异常：\n " +
						"程序将退出，错误代码 -101 ", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		//收集设备参数信息
		collectDeviceInfo(mContext);
		saveCrashInfo2File(ex);
		return false;
	}
	public void collectDeviceInfo(Context ctx){
		try{
			PackageManager pm=ctx.getPackageManager();
			PackageInfo pi=pm.getPackageInfo(ctx.getPackageName(),PackageManager.GET_ACTIVITIES);
			if(pi !=null){
				String versionName=pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
				
			}
			
		}catch(NameNotFoundException e){
			Log.e(TAG, "an error occured when collect package info",e);
		}
		Field[] fields=Build.class.getDeclaredFields();
		for(Field field:fields){
			try{
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : "+field.get(null));
			}catch(Exception e){
				Log.e(TAG, "an error occured when collect crash info");
			}
		}
	}
	/**
	 * 保存错误信息到文件中
	 * @param ex
	 * @return 返回文件名称，便于将文件传送到服务器
	 */
	
	private String saveCrashInfo2File(Throwable ex){
		StringBuffer sb=new StringBuffer();
		for(Map.Entry<String, String> entry : infos.entrySet()){
			String key=entry.getKey();
			String value=entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		
		Writer writer=new StringWriter();
		PrintWriter printWriter=new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause=ex.getCause();
		while(cause !=null){
			cause.printStackTrace(printWriter);
			cause=cause.getCause();
		}
		printWriter.close();
		String result=writer.toString();
		sb.append(result);
		try{
			long timestamp= System.currentTimeMillis();
			String time=formatter.format(new Date());
			String fileName="crash-" + time + "-" + timestamp +".log";
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/upg/crash/";
				File dir=new File(path);
				if(!dir.exists()){
					dir.mkdirs();
				}
				FileOutputStream fos=new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		}catch(Exception e){
			Log.e(TAG, "an error occured while writing file...",e);
		}
		return null;
	}
	

}
