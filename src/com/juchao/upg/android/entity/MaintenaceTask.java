package com.juchao.upg.android.entity;

public class MaintenaceTask {
	
	public int _id;  
	public long id; //任务 id ?
	public String taskName;
	public String startTime;
	public String endTime;
	
	public int index; //任务序号  不保存数据库
	public boolean isDownloaded;
	public int state;//0:未完成   1:已维修,未确认    2:确认维修  
	
	public int waitUploadNum; //待上传数
	//任务userId， 添加userId 是因 任务Id 不在唯一，同一个任务的不同问题可能会分配给不同的人 @20141127
	public long userId;
	
	/**
	 * 设备数量
	 */
	
	public int equipmentNum;
	/**
	 * 等上传问题数量
	 */
	public int problemNum;
	 
	public long problemId; //问题id
	
	//计划Id
	public MaintenacePlan plan; 
}  
