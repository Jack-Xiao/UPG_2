package com.juchao.upg.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.MaintenaceTaskEquipment;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;

public class WaitUploadMaintenaceItemListAdapter extends BaseAdapter {
	
	private List<MaintenaceTaskEquipmentItem> list;
	private LayoutInflater mLayoutInflater;
	
	public WaitUploadMaintenaceItemListAdapter(Context context) {
		list = new ArrayList<MaintenaceTaskEquipmentItem>();
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public MaintenaceTaskEquipmentItem getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 ViewHolder holder = null;
		 if(convertView == null){
			 convertView = mLayoutInflater.inflate(R.layout.wx_task_item_list_item, null);
			 holder = new ViewHolder();
			 holder.stateIcon=(ImageView)convertView.findViewById(R.id.wx_stateIcon);
			 holder.itemName=(TextView) convertView.findViewById(R.id.wx_itemName);
			 convertView.setTag(holder);
		 }else{
			 holder=(ViewHolder)convertView.getTag();
		 }
		 
		 try{
			 MaintenaceTaskEquipmentItem item=getItem(position);
			 holder.itemName.setText(item.problemName);
			 holder.stateIcon.setImageResource(R.drawable.maintenace_wait_upload);
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 return convertView;
	}

	public void addData(List<MaintenaceTaskEquipmentItem> result) {
		list.addAll(result);		
	}
	class ViewHolder{
		ImageView stateIcon;
		TextView itemName;
	}

}
