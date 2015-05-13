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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.db.MaintenaceEquipmentDao;
import com.juchao.upg.android.db.MaintenaceTaskDao;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.entity.MaintenaceTaskList;
import com.juchao.upg.android.ui.WxTaskEquipmentListActivity;
import com.juchao.upg.android.util.IntentUtil;

public class MaintenaceTaskListAdapter extends BaseAdapter {
	
	private List<MaintenaceTaskList> list;
	private LayoutInflater mLayoutInflater;
	public static final int TYPE_1=1;
	public static final int TYPE_2=2;
	private Context context;
	private String tag=null;
	
	private int from;
	
	public MaintenaceTaskListAdapter(Context context, int from){
		this.context=context;
		this.from=from;
		list= new ArrayList<MaintenaceTaskList>();
		mLayoutInflater=LayoutInflater.from(context);
		
	}
	public MaintenaceTaskListAdapter(
			Context context,
			int from2, String tag) {
		this.context=context;
		this.from=from2;
		this.tag=tag;
		list= new ArrayList<MaintenaceTaskList>();
		mLayoutInflater=LayoutInflater.from(context);
	}
	public void addData(List<MaintenaceTaskList> mList){
		if(list == null){
			list= new ArrayList<MaintenaceTaskList>();
		}
		list.addAll(mList);
		notifyDataSetChanged();
	}
	public void clearData(){
		if(list !=null){
			list.clear();
		}
		notifyDataSetChanged();
	}
	
	public int getCount(){
		return list.size();
	}
	public MaintenaceTaskList getItem(int position){
		return list.get(position);
	}
	public long getItemId(int position){
		return position;
	}
	
	@Override 
	public View getView(int position,View convertView,ViewGroup parent){
		ViewHolder holder = null;
		
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.wx_task_list_item2,null);
			holder= new ViewHolder();
			holder.type_title_layout=(RelativeLayout)convertView.findViewById(R.id.wx_type_title_layout);
			holder.type_content_layout=(RelativeLayout)convertView.findViewById(R.id.wx_type_content_layout);
			
			holder.taskIndex=(TextView)convertView.findViewById(R.id.wx_taskIndex);
			holder.taskName=(TextView)convertView.findViewById(R.id.wx_taskName);
			
			holder.itemName=(TextView)convertView.findViewById(R.id.wx_itemName);
			holder.itemBtn = (Button)convertView.findViewById(R.id.wx_itemBtn);
			holder.itemArrow = (TextView)convertView.findViewById(R.id.wx_itemArrow);
			//holder.itemArrow=(TextView)convertView.findViewById(android.R.id.itemArrow);
			convertView.setTag(holder);
		}else{
				holder=(ViewHolder)convertView.getTag();
			}
		MaintenaceTaskList tempTask=getItem(position);
		if(tempTask.type ==TYPE_1){
			holder.type_title_layout.setVisibility(View.VISIBLE);
			holder.type_content_layout.setVisibility(View.GONE);
			
			holder.taskIndex.setText(("任务" + tempTask.taskIndex));
			holder.taskName.setText(tempTask.name + "(" + tempTask.deviceNum + "台设备. 共 " + tempTask.itemNum + "项");
		}else{
			holder.type_title_layout.setVisibility(View.GONE);
			holder.type_content_layout.setVisibility(View.VISIBLE);
			
			holder.itemName.setText(tempTask.equipmentName);
			if(from ==  com.juchao.upg.android.ui.WxTaskEquipmentListActivity.From_Equipment_Maintenace||
					from ==  com.juchao.upg.android.ui.WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace){
				//holder.itemArrow.setVisibility(View.GONE);
				holder.itemArrow.setVisibility(View.VISIBLE);
				holder.itemBtn.setVisibility(View.VISIBLE);
				holder.itemArrow.setTag(position);
				holder.itemBtn.setTag(position);
				holder.itemBtn.setBackgroundColor(Color.parseColor("#005FCB"));
				holder.itemBtn.setOnClickListener(clickListener);
				if(tag !=null){
					holder.itemBtn.setText("确认");
				}
			}else if(from ==WxTaskEquipmentListActivity.From_Query_Maintenace ||
					from == WxTaskEquipmentListActivity.From_Task_Query){
				holder.itemArrow.setVisibility(View.VISIBLE);
				holder.itemBtn.setVisibility(View.GONE);
			}
			else{
				
			}
		}
		return convertView;
	}
	
	OnClickListener clickListener = new OnClickListener(){
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			MaintenaceTaskList tempTask=getItem(position);
			
			if(from == WxTaskEquipmentListActivity.From_Query_Maintenace)
				return;
			Bundle bundle = new Bundle();
			bundle.putLong("maintenaceTaskEquipmentId",tempTask.equipmentId );
			bundle.putLong("maintenaceTaskId",tempTask.maintenaceTaskId);
			bundle.putString("maintenaceTaskEquipmentName", tempTask.equipmentName);
			bundle.putBoolean("GetTheProblem", true);
			bundle.putString("taskName", tempTask.taskName);
			bundle.putInt("from", from);
			IntentUtil.startActivity((Activity)context, 
					com.juchao.upg.android.ui.WxEquipmentProblListActivity.class, bundle);
			
			
			//int position = (Integer)v.getTag();
			 
				//test: 20141105 暂时取消此设定，等待后台接口完成
				/**if(unMaintenaceItemNum == 0 ) {
					com.juchao.upg.android.util.ToastUtil.showLongToast(context,"没有未维修的项目");
					return;
				}*/
			/*Bundle bundle=new Bundle();
			bundle.putLong("maintenaceTaskEquipmentId",tempTaskEntity.id);
			bundle.putLong("equipmentId",tempTaskEntity.equipmentId);
			bundle.putString("mEquipmentName", tempTaskEntity.name);
			bundle.putString("taskName", tempTaskEntity.taskName);
			//IntentUtil.startActivity(context,WxReadyActivity.class,bundle);*/
			/*Bundle bundle = new Bundle();
			bundle.putLong("problemId", entity.id);
			bundle.putLong("equipmentId", entity.equipmentId);
			bundle.putString("taskName", maintenaceName);
			bundle.putString("mEquipmentName", equipmentName);
			bundle.putString("" , "" );
			//IntentUtil.startActivity(this, com.juchao.upg.android.ui.WxReadyActivity.class, bundle);
			IntentUtil.startActivity((Activity)context, com.juchao.upg.android.ui.WxReadyActivity.class, bundle);
	
		//@Override
		/**public void onClick(View v) {
			int position = (Integer)v.getTag();
			try{
				MaintenaceTaskList tempTaskEntity=getItem(position);
				com.juchao.upg.android.db.MaintenaceEquipmentDao dao=new com.juchao.upg.android.db.MaintenaceEquipmentDao(context);
				int unMaintenaceItemNum = dao.queryItemSizeById(tempTaskEntity.id, 1);
				dao.closeDB();
				
				//test: 20141105 暂时取消此设定，等待后台接口完成
				/**if(unMaintenaceItemNum == 0 ) {
					com.juchao.upg.android.util.ToastUtil.showLongToast(context,"没有未维修的项目");
					return;
				}*/
			/**	Bundle bundle=new Bundle();
				bundle.putLong("maintenaceTaskEquipmentId",tempTaskEntity.id);
				bundle.putLong("equipmentId",tempTaskEntity.equipmentId);
				bundle.putString("mEquipmentName", tempTaskEntity.name);
				bundle.putString("taskName", tempTaskEntity.taskName);
				//IntentUtil.startActivity(context,WxReadyActivity.class,bundle);
				com.juchao.upg.android.util.IntentUtil.startActivity((Activity)context, com.juchao.upg.android.ui.WxReadyActivity.class, bundle);
			}catch(Exception e){
				e.printStackTrace();
			}*/
		
			
		}
	};

	
	class ViewHolder{
		RelativeLayout type_title_layout , type_content_layout;
		TextView taskIndex , taskName;
		
		TextView itemName , itemArrow;
		Button itemBtn;
	}
}
    