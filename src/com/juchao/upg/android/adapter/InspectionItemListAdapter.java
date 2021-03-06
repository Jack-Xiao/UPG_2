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
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;

public class InspectionItemListAdapter extends BaseAdapter {

	private List<InspectionTaskEquipmentItem> list;
	private LayoutInflater mLayoutInflater;
	
	public InspectionItemListAdapter(Context context) {
		list = new ArrayList<InspectionTaskEquipmentItem>();
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	public void addData(List<InspectionTaskEquipmentItem> mList){
		if(list == null){
			list = new ArrayList<InspectionTaskEquipmentItem>();
		}
		list.addAll(mList);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public InspectionTaskEquipmentItem getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void clearData(){
		if(list != null){
			list.clear();
		}
		notifyDataSetChanged();
	}

	
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		ViewHolder holder = null;
		if(convertView == null ){
			convertView = mLayoutInflater.inflate(R.layout.dj_task_item_list_item, null);
			holder = new ViewHolder();
			holder.stateIcon = (ImageView)convertView.findViewById(R.id.stateIcon);
			holder.itemName = (TextView)convertView.findViewById(R.id.itemName);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		InspectionTaskEquipmentItem item = getItem(position);
		try{
			holder.itemName.setText(item.equipmentSpotcheckName);
			if(item.state == 1){
				holder.stateIcon.setImageResource(R.drawable.inspection_not_ok);
			}else{
				holder.stateIcon.setImageResource(R.drawable.inspection_ok);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return convertView;
	}

	
	class ViewHolder{
		ImageView stateIcon;
		TextView itemName;
	}
	
	
}
