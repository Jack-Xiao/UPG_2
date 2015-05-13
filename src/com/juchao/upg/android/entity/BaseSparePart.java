package com.juchao.upg.android.entity;


/**
 * 基础数据--备品备件
 * @author xuxd
 *
 */
public class BaseSparePart {

	/**
	 * 备件ID
	 */
	public Long id;
	
	/**
	 * 名称
	 */
	public String name;
	
	/**
	 * 备件型号
	 */
	public String modelNo;

	
	/**
	 * 备件代替型号
	 */
	public String replaceModelNo;

	/**
	 * 制作厂商
	 */
	public String makeCompany;
	
	/**
	 * 供应商
	 */
	public String provider;

	/**
	 * 最大库存
	 */
	public int maxStore;

	/**
	 * 最小库存
	 */
	public String minStore;

	/**
	 * 当前库存
	 */
	public int currentStore;

	
	
	/**
	 * 供货提前期
	 */
	public int goodsDate;

	/**
	 * 存放地址
	 */
	public String storeAddr;

	/**
	 * 数据修改时间
	 */
	public String updateTime;

	/**
	 * 标记删除 0：未删除; 1：已删除
	 */
	public int delMark;
	
	/**
	 * 备注
	 */
	public String description;

}
