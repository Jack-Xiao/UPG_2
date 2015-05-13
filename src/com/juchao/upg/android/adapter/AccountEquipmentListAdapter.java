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
import com.juchao.upg.android.entity.AccountEquipment;

public class AccountEquipmentListAdapter extends BaseAdapter{
	private Context context;
	private List<AccountEquipment> list;
	private LayoutInflater mLayoutInflater;
	
	public AccountEquipmentListAdapter(Context context){
		this.context=context;
		this.list=new ArrayList<AccountEquipment>();
		mLayoutInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		
		return list.size();
	}

	@Override
	public AccountEquipment getItem(int position) {
		
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder=null;
		if(convertView == null){
			holder=new ViewHolder();
			convertView=mLayoutInflater.inflate(R.layout.pd_task_equipment_list_item, null);
			holder.stateIcon=(ImageView)convertView.findViewById(R.id.stateIcon);
			holder.itemName=(TextView)convertView.findViewById(R.id.itemName1);
			holder.itemScan=(TextView)convertView.findViewById(R.id.itemscan1);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		
		AccountEquipment item=getItem(position);
		holder.itemName.setText(item.equipmentName);
		holder.itemScan.setText(item.equNum);
		if( item.status == 0){
			holder.stateIcon.setImageResource(R.drawable.inspection_not_ok);
		}else{
			holder.stateIcon.setImageResource(R.drawable.inspection_ok);
		}
		return convertView;
	}

	public void addData(List<AccountEquipment> mList) {
		if(list ==null){
			list=new ArrayList<AccountEquipment>();
		}
		list.addAll(mList);
		notifyDataSetChanged();
	}
	
	class ViewHolder{
		ImageView stateIcon;
		TextView itemName,itemScan;
	}
}
