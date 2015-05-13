package com.juchao.upg.android.view;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.db.QueryEquipmentAndSparePartDao;
import com.juchao.upg.android.entity.BaseEquipment;
import com.juchao.upg.android.ui.QueryEquipmentItemActivity;

/**
 * 创建一个弹出窗体，显示更详细的信息
 * @author xiao-jiang
 *
 */
public class PopupQueryEquipmentDetailUtil extends BasePopupWindow {

	private static final String TAG=PopupQueryEquipmentDetailUtil.class.getSimpleName();
	private PopupWindow popupWindow;

	private LinearLayout equipmentInfo,k3NO,pEquip,useStatus,storage,manufacturer,mfgNO,modelNO,supplier,purchaseDate,equipmentAmount,unitsid;
	
	private TextView equipmentInfo_title,k3NO_title,pEquip_title,useStatus_title,storage_title,manufacturer_title,mfgNO_title,modelNO_title,
					supplier_title,purchaseDate_title,equipmentAmount_title,unitsid_title;
	private TextView equipmentInfo_content,k3NO_content,pEquip_content,useStatus_content,storage_content,manufacturer_content,mfgNO_content,modelNO_content,
					supplier_content,purchaseDate_content,equipmentAmount_content,unitsid_content;
	
	private static PopupQueryEquipmentDetailUtil instance;
	
	public PopupQueryEquipmentDetailUtil(){
	}
	
	public synchronized static PopupQueryEquipmentDetailUtil getInstance(){
		if(instance == null){
			instance = new PopupQueryEquipmentDetailUtil();
		}
		return instance;
	}
	
	public boolean isShowing(){
		if(popupWindow !=null ){
			return popupWindow.isShowing();
		}
		return false;
	}
	
	public void dismiss(Activity activity){
		if(activity !=null && !activity.isFinishing() && popupWindow !=null && popupWindow.isShowing()){
			popupWindow.dismiss();
		}
	}
	
	public void showActionWindow(Activity mActivity, long equipmentId){
		QueryEquipmentAndSparePartDao dao=new QueryEquipmentAndSparePartDao(mActivity);
		BaseEquipment entity=dao.queryEquipment(equipmentId);
		dao.closeDB();
		
		View view=getView(mActivity,entity);
		int width=view.getWidth();
		int height=view.getHeight();
		
		popupWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		popupWindow.update();
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
		if(mActivity !=null && !mActivity.isFinishing())
			popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	private View getView(Activity mActivity, BaseEquipment entity) {
		View view=LayoutInflater.from(mActivity).inflate(R.layout.query_equipment_detail_layout, null);

		equipmentInfo = (LinearLayout)view.findViewById(R.id.equipmentInfo);
		k3NO=(LinearLayout)view.findViewById(R.id.k3NO);
		pEquip=(LinearLayout)view.findViewById(R.id.pEquip);
		useStatus=(LinearLayout)view.findViewById(R.id.useStatus);
		storage=(LinearLayout)view.findViewById(R.id.storage);
		manufacturer=(LinearLayout)view.findViewById(R.id.manufacturer);
		mfgNO=(LinearLayout)view.findViewById(R.id.mfgNO);
		modelNO=(LinearLayout)view.findViewById(R.id.modelNO);
		supplier=(LinearLayout)view.findViewById(R.id.supplier);
		purchaseDate=(LinearLayout)view.findViewById(R.id.purchaseDate);
		equipmentAmount=(LinearLayout)view.findViewById(R.id.equipmentAmount);
		unitsid=(LinearLayout)view.findViewById(R.id.unitsid);
		
		//title
		equipmentInfo_title=(TextView)equipmentInfo.findViewById(R.id.title);
		k3NO_title=(TextView)k3NO.findViewById(R.id.title);
		pEquip_title=(TextView)pEquip.findViewById(R.id.title);
		useStatus_title=(TextView)useStatus.findViewById(R.id.title);
		storage_title=(TextView)storage.findViewById(R.id.title);
		manufacturer_title=(TextView)manufacturer.findViewById(R.id.title);
		mfgNO_title=(TextView)mfgNO.findViewById(R.id.title);
		modelNO_title=(TextView)modelNO.findViewById(R.id.title);
		supplier_title=(TextView)supplier.findViewById(R.id.title);
		purchaseDate_title=(TextView)purchaseDate.findViewById(R.id.title);
		equipmentAmount_title=(TextView)equipmentAmount.findViewById(R.id.title);
		unitsid_title=(TextView)unitsid.findViewById(R.id.title);
		
		//content
		equipmentInfo_content=(TextView)equipmentInfo.findViewById(R.id.content);
		k3NO_content=(TextView)k3NO.findViewById(R.id.content);
		pEquip_content=(TextView)pEquip.findViewById(R.id.content);
		useStatus_content=(TextView)useStatus.findViewById(R.id.content);
		storage_content=(TextView)storage.findViewById(R.id.content);
		manufacturer_content=(TextView)manufacturer.findViewById(R.id.content);
		mfgNO_content=(TextView)mfgNO.findViewById(R.id.content);
		modelNO_content=(TextView)modelNO.findViewById(R.id.content);
		supplier_content=(TextView)supplier.findViewById(R.id.content);
		purchaseDate_content=(TextView)purchaseDate.findViewById(R.id.content);
		equipmentAmount_content=(TextView)equipmentAmount.findViewById(R.id.content);
		unitsid_content=(TextView)unitsid.findViewById(R.id.content);
		
		//set value
		equipmentInfo_title.setText(entity.equipmentName);
		equipmentInfo_content.setText(entity.managementOrgNum);
		
		k3NO_title.setText("k3编号：");//
		pEquip_title.setText("从属设备：");
		useStatus_title.setText("使用状况：");
		storage_title.setText("当前位置：");
		manufacturer_title.setText("生产厂家：");
		mfgNO_title.setText("出厂编号：");
		modelNO_title.setText("型    号：");
		supplier_title.setText("供应商：");
		purchaseDate_title.setText("购置日期：");
		equipmentAmount_title.setText("设备数量：");
		unitsid_title.setText("单位：");
		
		k3NO_content.setText(entity.k3NO);
		//pEquip_content.setText(entity.pEquipid+"");
		pEquip_content.setText(entity.pEquip+"");
		//useStatus_content.setText(entity.useStateid+"");
		useStatus_content.setText(entity.useState+"");
		
		storage_content.setText(entity.storage);
		manufacturer_content.setText(entity.manufacturer);
		mfgNO_content.setText(entity.mfgNO);
		modelNO_content.setText(entity.modelNO);
		supplier_content.setText(entity.supplier);
		purchaseDate_content.setText(entity.purchaseDate);
		equipmentAmount_content.setText(entity.equipmentAmount+"");
		//unitsid_content.setText(entity.unitsid+"");
		unitsid_content.setText(entity.units+"");
		
	return view;	
	}
}
