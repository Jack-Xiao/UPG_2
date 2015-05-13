package com.juchao.upg.android.net.download;

import java.io.File;

import android.os.Handler;
import android.os.Message;

import com.juchao.upg.android.MyApp;


/**
 * 
 * UI控件画面的重绘(更新)是由主线程负责处理的，如果在子线程中更新UI控件的值，更新后的值不会重绘到屏幕上
 * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
 * 
 */
public class DownloadTask implements Runnable {

	private String path;
	private File saveDir;
	private FileDownloader loader;
	private Handler mHandler;
	
	public DownloadTask(String path, File saveDir , Handler mHandler) {
		this.path = path;
		this.saveDir = saveDir;
		this.mHandler = mHandler;
	}

	/**
	 * 退出下载
	 */
	public void exit() {
		if (loader != null)
			loader.exit();
	}

	DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
		@Override
		public void onDownloadSize(int size) {
			Message msg = new Message();
			//
//			msg.what = ThemeListAdapter.PROCESSING;
			msg.getData().putInt("size", size);
			mHandler.sendMessage(msg);
		}
	};
	
	@Override
	public void run() {
		try {
			// 实例化一个文件下载器
			loader = new FileDownloader(MyApp.application.getApplicationContext(), path,
					saveDir, 3);
			// 设置进度条最大值
			loader.download(downloadProgressListener);
		} catch (Exception e) {
			e.printStackTrace();
			//
//			mHandler.sendMessage(mHandler.obtainMessage(ThemeListAdapter.FAILURE)); // 发送一条空消息对象
		}
	}

}
