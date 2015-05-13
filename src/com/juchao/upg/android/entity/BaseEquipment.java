package com.juchao.upg.android.entity;


/**
 * 基础数据--设备
 * @author xuxd
 *
 */
public class BaseEquipment {

	
	
	/**
	 * 设备Id
	 */
	public Long id;
	/**
	 * K3编号
	 */
	public String k3NO;
	/**
	 * 设备名称
	 */
	public String equipmentName;
	/**
	 * 父级设备
	 */
	public Long pEquipid;
	/**
	 * 责任部门
	 */
	public Long dutyOrgid;
	
	
	/**
	 * 责任人
	 */
	public Long dutyUserid;
	/**
	 * 设备型号
	 */
	public String modelNO;
	/**
	 * 制造厂商
	 */
	public String manufacturer;
	/**
	 * 出厂编号
	 */
	public String mfgNO;
	/**
	 * 供应商
	 */
	public String supplier;
	
	
	
	/**
	 * 购置日期
	 */
	public String purchaseDate;
	/**
	 * 使用状况
	 */
	public Long useStateid;
	/**
	 * 设备类型
	 */
	public Long equipmentTypeid;
	/**
	 * 当前位置(存放地点)
	 */
	public String storage;
	/**
	 * 备注
	 * length="1000"
	 */
	public String remarks;
	/**
	 * 维修点检操作须知
	 * length="4000"
	 */
	public String maintainNotice;
	
	/**
	 * 编号
	 */
	public String managementOrgNum;
	/**
	 * 设备数量
	 */
	public int equipmentAmount;
	/**
	 * 单位
	 */
	public Long unitsid;
	
	/**
	 * 数据更新时间
	 */
	public String updateTime;
	/**
	 * 删除标识
	 * 0:正常,1标记删除
	 */
	public int delMark;

	//单位名称
	public String units;
	//设备类型
	public String equipmentType;
	//使用状况
	public String useState;
	//低级设备名称
	public String pEquip;
	//设备条码
	public String equNum;
}
