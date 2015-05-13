package com.juchao.upg.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.BaseEquipment;
import com.juchao.upg.android.util.Log;

public class QueryEquipmentTaskAdapter extends BaseAdapter{
	private List<BaseEquipment> list;
	private LayoutInflater mLayoutInflater;
	private Context context;
	private LinearLayout dependenceEquipment,useStatus,curLocation;

	
	public QueryEquipmentTaskAdapter(Context context){
		this.context=context;
		mLayoutInflater=LayoutInflater.from(context);
		list=new ArrayList<BaseEquipment>();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	@Override
	public BaseEquipment getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		
		return position;
	}
	public void addData(List<BaseEquipment> list){
		if(this.list==null){
			this.list=new ArrayList<BaseEquipment>();
		}
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void setData(List<BaseEquipment> list){
		if(this.list==null){
			this.list=new ArrayList<BaseEquipment>();
		}
		if(list == null){
			list=new ArrayList<BaseEquipment>();
		}
		this.list=list;
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 ViewHolder holder;
		 if(convertView == null){
			 holder = new ViewHolder();
			 convertView = mLayoutInflater.inflate(com.juchao.upg.android.R.layout.query_equipment_item1, null);
			 dependenceEquipment=(LinearLayout)convertView.findViewById(R.id.dependenceEquipemnt);
			 useStatus=(LinearLayout)convertView.findViewById(R.id.useState);
			 curLocation=(LinearLayout)convertView.findViewById(R.id.curLocation);			 
			 
			 holder.managementOrgNum=(TextView)convertView.findViewById(R.id.equipmentId);
			 holder.equipmentName=(TextView)convertView.findViewById(R.id.equipmentName);
			 initView(holder);
			 
			 convertView.setTag(holder);
		 }else{
			 holder=(ViewHolder)convertView.getTag();
		 }
		 setInitView(holder,position);
		return convertView;
	}
	
	private void setInitView(ViewHolder holder,int position) {
		BaseEquipment entity=list.get(position);
		holder.equipmentName.setText(entity.equipmentName);
		//
		TextPaint tp=holder.equipmentName.getPaint();
		tp.setFakeBoldText(true);
		
		holder.managementOrgNum.setText(entity.managementOrgNum);
		holder.dependenceEquipment_content.setText(entity.pEquip +"");
		holder.useStatus_content.setText(entity.useState + "");
		holder.curLocation_content.setText(entity.storage);
	}

	class ViewHolder{
		TextView dependenceEquipment_title,useStatus_title,curLocation_title,managementOrgNum;
		TextView dependenceEquipment_content,useStatus_content,curLocation_content,equipmentName;
		
	}
	private void initView(ViewHolder holder){
		
		holder.dependenceEquipment_title=(TextView)dependenceEquipment.findViewById(R.id.title);
		holder.useStatus_title=(TextView)useStatus.findViewById(R.id.title);
		holder.curLocation_title=(TextView)curLocation.findViewById(R.id.title);
		
		holder.dependenceEquipment_title.setText("从属设备：");
		holder.useStatus_title.setText("使用状况：");
		holder.curLocation_title.setText("当前位置：");
		
		
		holder.dependenceEquipment_content=(TextView)dependenceEquipment.findViewById(R.id.content);
		holder.useStatus_content=(TextView)useStatus.findViewById(R.id.content);
		holder.curLocation_content=(TextView)curLocation.findViewById(R.id.content);
	}

	public void clearData() {
		this.list.clear();
	}
	
}
