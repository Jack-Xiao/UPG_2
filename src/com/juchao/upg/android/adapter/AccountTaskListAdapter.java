package com.juchao.upg.android.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.AccountTask;
import com.juchao.upg.android.entity.AccountTaskList;
import com.juchao.upg.android.ui.DjReadyActivity;
import com.juchao.upg.android.util.IntentUtil;

public class AccountTaskListAdapter extends BaseAdapter{
	private List<AccountTask> list;
	private LayoutInflater mLayoutInflater;
	private Context context;
	private int from ;

	public AccountTaskListAdapter(Context context, int from) {
		this.context=context;
		this.from  = from;
		list = new ArrayList<AccountTask>();
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void addData(List<AccountTask> list) {
		if (this.list == null) {
			this.list = new ArrayList<AccountTask>();
		}
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	
	public void clearData(){
		if (this.list == null) {
			this.list = new ArrayList<AccountTask>();
		}
		this.list.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public AccountTask getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(
					R.layout.pd_task_download_item, null);
			holder.taskNum = (TextView) convertView
					.findViewById(R.id.taskNum);
			holder.taskName = (TextView) convertView
					.findViewById(R.id.taskName);
			holder.taskBtn = (Button) convertView
					.findViewById(R.id.taskBtn);
			holder.startTime=(TextView)convertView.findViewById(R.id.startTime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(from == com.juchao.upg.android.ui.PdTaskListActivity.FROM_QUERY){
			holder.taskBtn.setVisibility(View.GONE);
		}else{
			holder.taskBtn.setVisibility(View.VISIBLE);
			holder.taskBtn.setText("盘点");
			holder.taskBtn.setOnClickListener(clickListener);
			holder.taskBtn.setTag(position);
		}
		AccountTask task=getItem(position);
		holder.taskNum.setText("任务" + (position + 1)+"、");
		holder.taskName.setText(task.name + "(共"+task.total + "台设备)");
		holder.startTime.setText("开始时间："+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(task.startTime));
		return convertView;
	}
	
	OnClickListener clickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			int position = (Integer)v.getTag();
			AccountTask tempTask= getItem(position);
			Bundle bundle=new Bundle();
			bundle.putLong("taskId", tempTask.id);
			bundle.putString("taskName", tempTask.name);
			IntentUtil.startActivity((Activity)context, com.juchao.upg.android.ui.PdTaskListScanningActivity.class, bundle);
		}
	};

	class ViewHolder {
		TextView taskNum;
		TextView taskName;
		Button taskBtn;
		TextView startTime;
	}
}
