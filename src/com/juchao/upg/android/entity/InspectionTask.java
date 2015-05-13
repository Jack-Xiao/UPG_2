package com.juchao.upg.android.entity;

public class InspectionTask {

	public int _id;
	public long id;
	public String taskName;
	public String startTime;
	public String endTime;
	
	public int index; //任务序号  不保存数据库
	public boolean isDownloaded;  //是否已经下载对应的点检任务
	public int state; //0 ：未完成 ， 1:已完成
	
	//public int type; //0：定常点检任务  1：自定义点检任务  2：特种设备点检任务
	//public int style; //0：定常点检任务  1：自定义点检任务  2：特种设备点检任务
	public int status; //0：定常点检任务  1：自定义点检任务  2：特种设备点检任务
	
	public int waitUploadNum; //待上传数
	
	// 存放点检类型值  1、年度;2、 季度;3、 月度;4、周常;5、 日常;6、维修;7、自定义
	public int intervalType;
	
}
