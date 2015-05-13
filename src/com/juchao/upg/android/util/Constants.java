package com.juchao.upg.android.util;

public class Constants {

	public static final Boolean LOGD_ENABLE = true;
	public static final Boolean LOG_SDCARD_ENABLE = false;
	public static final String LOG_FILE_NAME = "com.juchao.upg.android_log.txt";
	
	public static final String BASE_IP = "172.16.2.13";
	public static final String BASE_PORT = "8080";
	public static final String BASE_URL = "http://"+BASE_IP+":"+BASE_PORT+"/baoquan";
	
	public static final int REQUEST_SUCCESS = 0;
	public static final int REQUEST_FAIL = -1;
	
	
	public static final String KEY_SERVICE_ADDRESS = "key_service_address";  //服务器地址
	public static final String KEY_SERVICE_PORT = "key_service_port";  //服务器端口
	public static final String KEY_ONLY_REMIND_ONCE = "key_only_remind_once";  //仅提醒一次
	public static final String KEY_REMIND_INTERVAL = "key_remind_interval";  //时间间隔
	
	
	public static final String KEY_TOKEN = "key_token";  //登录token
	public static final String KEY_USER_ID = "key_user_id";  //登录用户ID
	public static final String KEY_USER_NAME = "key_user_name";  //登录的用户名
	public static final String KEY_MAC = "key_mac";  //
	public static final String KEY_LAST_UPDATE_DATA_TIME = "key_last_update_data_time";  //上次更新基础数据的时间
	public static final String KEY_LAST_PUSH_TIME = "key_last_push_time";
	public static final String KEY_LAST_MSG_ID = "key_last_msg_id";  //
	
	
	/**
	 * Maintenace  功能  
	 * 对应表  Maintenace_Item_Table_Name 字段:state.
	 */
	public static final int WX_NOT_COMMIT = 1;   //问题未维修
	public static final int WX_HAS_COMMIT = 2;   //问题已维修
	
	public static final int WX_HAS_COMMIT_TASK_STATE_SET = 2;  // 已维修下载,需要效果确认的任务
	
	//Task attribute state
	public static final int TASK_NOT_DOWNLOAD = 0; //未下载的任务
	public static final int TASK_HAS_DOWNLOAD = 1; //任务已下载,未曾维修
	public static final int TASK_HAS_MAINTENACE = 2; //已维修
	public static final int TASK_HAS_EFFECT = 3;  //已 效果确认
	public static final int TASK_HAS_FINISH = 4; //已完成的任务（已上传成功）
	
	//对 problem 表 添加uploadState 状态
	public static final int TASK_NOT_UPLOAD = 0;
	public static final int TASK_HAS_UPLOAD = 1;
	
	//对缩小版本添加前缀
	public static final String PHOTO_PREFIX = "T_";
	
	//使用后门 开户日志记录
	public static final String BEGIN_LOG="begin_record_log";
	
	public static final int TEXT_COUNT_LIMIT=255;
	
	public static final int DJ_POPUP = 1;
	public static final int WX_POPUP = 2;
	
}
