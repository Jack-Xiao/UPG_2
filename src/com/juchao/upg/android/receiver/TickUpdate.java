package com.juchao.upg.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.juchao.upg.android.entity.ResMessage;
import com.juchao.upg.android.entity.ResMessage.Message;
import com.juchao.upg.android.net.Data;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.Log;
import com.juchao.upg.android.util.NetUtils;


public class TickUpdate extends BroadcastReceiver{
	private static final String TAG = TickUpdate.class.getSimpleName();
	private static TickUpdate tickUpdate;
	private static long PERIOD = 1000 * 60 * 60 ; // 1小时
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		Long lastCheckTime = DefaultShared.getLong(Constants.KEY_LAST_PUSH_TIME, 0L);
		PERIOD =  DefaultShared.getLong(Constants.KEY_REMIND_INTERVAL, 0L);
		PERIOD=1000*60;
		Long currentTime = System.currentTimeMillis();
		Long diff = currentTime - lastCheckTime;
		if(PERIOD < 1000 * 60){ //间隔小于1分钟
			PERIOD = 1000 * 60 * 60 ; //默认1小时
		}
		if (diff >= PERIOD && NetUtils.isNetworkConnected(context)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						Log.d(TAG, "auto update appTypes and checkNewVersion");
						String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
						if(TextUtils.isEmpty(token)){
							return;
						}
						ResMessage result = NetAccessor.findMsg(token);
						if(DefaultShared.getBoolean(Constants.BEGIN_LOG, false)){
							Log.fileLog(TAG,"begin notification: "+ new Data() +"");
						}
						if(result != null && result.code == 0 ){
							if(DefaultShared.getBoolean(Constants.BEGIN_LOG, false)){
								Log.d(TAG, "the notification result isn't null and result.code is 0 : "+ new Data() +"");		
							}
							DefaultShared.putLong(Constants.KEY_LAST_PUSH_TIME, System.currentTimeMillis());
							if(result.data != null && result.data.size() > 0){
								if(DefaultShared.getBoolean(Constants.BEGIN_LOG, false)){
									Log.fileLog(TAG, "the notification result's data isn't null and result's data size > 0 : "+ new Data() +"");
								}
								boolean onlyOnce = DefaultShared.getBoolean(Constants.KEY_ONLY_REMIND_ONCE, false);
								if(onlyOnce){
									long lastMsgId =  DefaultShared.getLong(Constants.KEY_LAST_MSG_ID, 0);
									if(lastMsgId != result.data.get(0).id){
										DefaultShared.putLong(Constants.KEY_LAST_MSG_ID, result.data.get(0).id);
										ClientUtil.showNotification(context, result.data.get(0).msgType, result.data.get(0).message);
//										for(Message message : result.data){
//											//ClientUtil.showNotification(context, result.data.get(0).msgType, result.data.get(0).message);
//											ClientUtil.showNotification(context, message.msgType, message.message);
//										}
//										
									}
								}else{
									DefaultShared.putLong(Constants.KEY_LAST_MSG_ID, result.data.get(0).id);
									//ClientUtil.showNotification(context, result.data.get(0).msgType, result.data.get(0).message);
									for(Message message : result.data){
										ClientUtil.showNotification(context, message.msgType, message.message);
										Thread.sleep(1500);
									}
								}
							}
						}
						
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
			}).start();
		}
	}
	
		
	 public static void register( Context context ){
	    synchronized( TickUpdate.class ){
	      if( tickUpdate == null ){
	        tickUpdate = new TickUpdate();
	        IntentFilter filter = new IntentFilter( Intent.ACTION_TIME_TICK );
	        filter.addAction( Intent.ACTION_DATE_CHANGED );
	        filter.addAction( Intent.ACTION_TIME_CHANGED );
	        filter.addAction( Intent.ACTION_TIMEZONE_CHANGED );
	        context.registerReceiver( tickUpdate, filter );
	        Log.v(TAG, "register TickUpdate receiver");
	      }
	    }
	  }
		  
	  public static void unregister( Context context ){
	    synchronized( TickUpdate.class){
	      if( tickUpdate != null ){
	        context.unregisterReceiver( tickUpdate );
	        tickUpdate = null;
	        Log.v(TAG, "unRegister TickUpdate receiver");
	      }
	    }
	  }
}
