package com.juchao.upg.android.entity;

import java.util.Date;

public class AccountEquipment {

	//盘点设备id
	public long id;
	//盘点任务id
	public long accountId;
	//设备资料id
	public long equipmentId;
	//资产条码
	public String assetCode;
	//状态  0:未采集  1:已采集
	public int status;
	//扫描条码时间
	public Date scanCodeTime;
	
	public String equipmentName;
	//设备编号
	public String equNum;
}
