package com.juchao.upg.android.entity;


/**
 * 基础数据--设备项目
 *
 */
public class BaseEquipmentSpotcheck {

	/**
	 * 项目ID
	 */
	public Long id;
	
	/**
	 * 点检项目名称
	 */
	public String name;
	/**
	 * 点检项目分组标识
	 */
	public String checkGroupmark;
	
	
	/**
	 * 项目类型
	 */
	public Long projectTypeid;
	/**
	 * 项目编号
	 */
	public String projectNO;
	/**
	 * 点检类型
	 */
	public int checkType;
	/**
	 * ILU级别要求
	 */
	public int iluLv;
	/**
	 * 检查方法
	 * length="1000"
	 */
	public String checkMethod;
	/**
	 * 判断标准
	 * length="1000"
	 */
	public String judgementStandard;
	/**
	 * 点检设备
	 */
	public Long checkedEquipId;
	
	
	
	/**
	 * 数据操作时间
	 */
	public String updateTime;
	/**
	 * 删除标识
	 * 0:正常,1标记删除
	 */
	public int delMark;

	public long costPlanTime;

}
