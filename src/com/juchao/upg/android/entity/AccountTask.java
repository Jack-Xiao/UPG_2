package com.juchao.upg.android.entity;

import java.util.Date;

public class AccountTask {

	//盘点任务id
	public long id;
	//盘点任务名称
	public String name;
	//状态
	public int status;
	
	public Date startTime;
	
	public Date endTime;
	//盘点范围
	public int range;
	//盘点数量
	public int total;
	//是否下载
	public boolean isDownloaded;
	
	//待下载数
	public int waitUploadNum;
}
