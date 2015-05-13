package com.juchao.upg.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.InspectionTaskList;
import com.juchao.upg.android.ui.DjReadyActivity;
import com.juchao.upg.android.ui.DjTaskListActivity;
import com.juchao.upg.android.util.IntentUtil;

public class InspectionTaskListAdapter extends BaseAdapter {

	private List<InspectionTaskList> list;
	private LayoutInflater mLayoutInflater;
	public static final int TYPE_1 = 1;  
	public static final int TYPE_2 = 2;
	private Context context;
	
	private int from;
	public InspectionTaskListAdapter(Context context , int from) {
		this.context = context;
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
	
	public void clearData(){
		if(list != null){
			list.clear();
		}
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

	 // 每个convert view都会调用此方法，获得当前所需要的view样式  
//    @Override  
//    public int getItemViewType(int position) {  
//        if (getItem(position).type == 1)  
//            return TYPE_1;  
//        else 
//            return TYPE_2;  
//    }  
//    
//    @Override  
//    public int getViewTypeCount() {  
//        return 2;  
//    }  
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		ViewHolder holder = null;
//		int type = getItemViewType(position);
		
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.dj_task_list_item2, null);
			holder = new ViewHolder();
			holder.type_title_layout = (RelativeLayout)convertView.findViewById(R.id.type_title_layout);
			holder.type_content_layout = (RelativeLayout)convertView.findViewById(R.id.type_content_layout);
			
			holder.taskIndex = (TextView)convertView.findViewById(R.id.taskIndex);
			holder.taskName = (TextView)convertView.findViewById(R.id.taskName);
			
			holder.itemName = (TextView)convertView.findViewById(R.id.itemName);
			holder.itemBtn = (Button)convertView.findViewById(R.id.itemBtn);
			holder.itemArrow = (TextView)convertView.findViewById(R.id.itemArrow);
			holder.imgTaskStyle=(ImageView)convertView.findViewById(R.id.imgTaskStyle);
			
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		InspectionTaskList tempTask = getItem(position);
		if(tempTask.type == TYPE_1){
			holder.type_title_layout.setVisibility(View.VISIBLE);
			holder.type_content_layout.setVisibility(View.GONE);
			
			holder.taskIndex.setText("任务" + tempTask.taskIndex);
			
			if(tempTask.style == 0) {
				holder.imgTaskStyle.setBackgroundResource(R.drawable.steady_task);
			}else if(tempTask.style == 1){
				holder.imgTaskStyle.setBackgroundResource(R.drawable.custom_task);
			}else if(tempTask.style == 2){
				holder.imgTaskStyle.setBackgroundResource(R.drawable.special_task);
			}
			holder.taskName.setText(tempTask.name + 
					"("+tempTask.deviceNum+"台设备，共"+tempTask.itemNum+"项)");
		}else{
			holder.type_title_layout.setVisibility(View.GONE);
			holder.type_content_layout.setVisibility(View.VISIBLE);
			
			holder.itemName.setText(tempTask.name);
			if(from == DjTaskListActivity.From_Equipment_Inspection){  //设备点检
				holder.itemArrow.setVisibility(View.GONE);
				holder.itemBtn.setVisibility(View.VISIBLE);
				holder.itemBtn.setTag(position);
				holder.itemBtn.setBackgroundColor(Color.parseColor("#005FCB"));
				holder.itemBtn.setOnClickListener(clickListener);
			}else{
				holder.itemArrow.setVisibility(View.VISIBLE);
				holder.itemBtn.setVisibility(View.GONE);
			}
		}
		
		return convertView;
	}

	//设备点检
	OnClickListener clickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			int position = (Integer)v.getTag();
			
			try{
				
				InspectionTaskList tempTaskEntity = getItem(position);
				//InspectionEquipmentDao dao = new InspectionEquipmentDao(context);
/*				int unInspectionItemNum = dao.queryItemSizeById(tempTaskEntity.id , 1);
				dao.closeDB();
				if(unInspectionItemNum == 0){
					ToastUtil.showLongToast(context, "没有未点检的项目");
					return;
				}*/
				long taskId=tempTaskEntity.taskId;
				Bundle bundle = new Bundle();
				bundle.putLong("inspectionTaskEquipmentId", tempTaskEntity.id);
				bundle.putLong("equipmentId", tempTaskEntity.equipmentId);
				bundle.putString("mEquipmentName", tempTaskEntity.name);
				bundle.putString("taskName", tempTaskEntity.taskName);
				bundle.putLong("inspectionTaskId", taskId);
				bundle.putInt("style", tempTaskEntity.style);
				bundle.putInt("type", tempTaskEntity.intervalType);
				IntentUtil.startActivity((Activity)context, DjReadyActivity.class, bundle);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	class ViewHolder{
		RelativeLayout type_title_layout , type_content_layout;
		TextView taskIndex , taskName;
		
		TextView itemName , itemArrow;
		Button itemBtn;
		ImageView imgTaskStyle;
	}
	
}
