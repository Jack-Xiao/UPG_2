package com.juchao.upg.android.adapter;

import java.util.ArrayList;
import java.util.List;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.entity.InspectionTaskList;
import com.juchao.upg.android.ui.DjTaskListActivity;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class QueryEquipmentAdapter extends BaseAdapter {

	private List<InspectionTaskList> list;
	private LayoutInflater mLayoutInflater;
	public static final int TYPE_1 = 0;  
	public static final int TYPE_2 = 1;
	
	private int from;
	public QueryEquipmentAdapter(Context context , int from) {
		this.from = from;
		list = new ArrayList<InspectionTaskList>();
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	public void addData(List<InspectionTaskList> mList){
		if(list == null){
			list = new ArrayList<InspectionTaskList>();
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
	public InspectionTaskList getItem(int position) {
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
		if(convertView == null || !( convertView.getTag() instanceof ViewHolder)){
			convertView = mLayoutInflater.inflate(R.layout.dj_task_list_title_item, null);
			holder = new ViewHolder();
			holder.taskIndex = (TextView)convertView.findViewById(R.id.taskIndex);
			holder.taskName = (TextView)convertView.findViewById(R.id.taskName);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		
		
		return convertView;
	}

	//设备点检
	OnClickListener clickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			long id = (Long)v.getTag();
			
			
		}
	};
	
	class ViewHolder{
		TextView taskIndex , taskName;
	}
	
	class ViewHolder2{
		TextView itemName , itemArrow;
		Button itemBtn;
	}
}
