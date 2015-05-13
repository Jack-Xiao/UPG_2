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
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;

public class MaintenaceItemListAdapter extends BaseAdapter{
	
	private List<MaintenaceTaskEquipmentItem> list;
	private LayoutInflater mLayoutInflater;

	public MaintenaceItemListAdapter(Context context){
		list=new ArrayList<MaintenaceTaskEquipmentItem>();
		mLayoutInflater=LayoutInflater.from(context);
	}
	
	public void addData(List<MaintenaceTaskEquipmentItem> mList){
		if(list == null ){
			list=new ArrayList<MaintenaceTaskEquipmentItem>();
		}
		list.addAll(mList);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public MaintenaceTaskEquipmentItem getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.wx_task_item_list_item,null);
			holder= new ViewHolder();
			holder.stateIcon=(ImageView)convertView.findViewById(R.id.wx_stateIcon);
			holder.itemName=(TextView)convertView.findViewById(R.id.wx_itemName);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		MaintenaceTaskEquipmentItem item=getItem(position);
		try{
			//holder.itemName.setText(item.equipmentSpotcheckName);
			holder.itemName.setText(item.problemName);
			if(item.uploadState == 0){
				holder.stateIcon.setImageResource(R.drawable.maintenace_not_ok);
			}else{
				holder.stateIcon.setImageResource(R.drawable.maintenace_ok);
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
