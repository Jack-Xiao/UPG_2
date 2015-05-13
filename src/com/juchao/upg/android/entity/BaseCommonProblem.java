package com.juchao.upg.android.entity;


/**
 * 基础数据--常见问题
 *
 */
public class BaseCommonProblem {

	
	public Long id;//问题id
	public Long equipmentId;//设备id
	public Long equipmentSpotcheckId;//点检项目id
	public String faultDescribe;//问题内容
	public String createTime;//创建时间
	public String updateTime;//更新时间

	public boolean isChecked;
}