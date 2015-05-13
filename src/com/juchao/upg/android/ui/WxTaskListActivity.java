package com.juchao.upg.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.MaintenaceTask;
import com.juchao.upg.android.task.DownloadMaintenaceTask;
import com.juchao.upg.android.task.GetMaintenaceTaskList;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.NetUtil;
import com.juchao.upg.android.util.ToastUtil;

public class WxTaskListActivity extends BaseActivity implements OnClickListener,OnScrollListener{
	protected static final String TAG=WxTaskListActivity.class.getSimpleName();
	
	private ListView listView;
	private TextView headerLeft, headerTitle, noResult;
	private int curPage=1;
	private int totalPage;
	private static final int PAGE_SIZE = 20;
	
	public ProgressDialog progressDialog;
	private MaintenaceTaskAdapter adapter;
	   
	private Context context;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		context= this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wx_task_download_act);
		
		headerLeft=(TextView)findViewById(R.id.header_left);
		headerTitle=(TextView)findViewById(R.id.header_title);
		headerLeft.setText("维修");
		headerTitle.setText("任务列表");
		noResult=(TextView)findViewById(R.id.wx_noResult);
		listView=(ListView)findViewById(R.id.wx_listview);
		
		listView.setOnScrollListener(this);
		mListFooterView =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.loadmore_bar,listView,false);
		mListFooterProgress=  (ProgressBar) mListFooterView.findViewById(R.id.progress);
		mLoadingText=(TextView)mListFooterView.findViewById(R.id.loading_text);
		listView.addFooterView(mListFooterView, null, false);
		
		adapter= new MaintenaceTaskAdapter(this);
		listView.setAdapter(adapter);
		
		headerLeft.setOnClickListener(this);
		curPage=1;
		loadData();
		
	}
	
	private GetMaintenaceTaskList task;
	
	private void loadData(){
		task = new GetMaintenaceTaskList(WxTaskListActivity.this,
				new GetMaintenaceTaskList.TaskCallBack(){

					@Override
					public void preExecute() {
						noResult.setVisibility(View.GONE);
						if(curPage == 1){
							setFooterViewVisibility(View.GONE);
							if(!isFinishing()){
								try {
									progressDialog = ProgressDialog.show(context, null, "加载中...",true,true);
									progressDialog.setCanceledOnTouchOutside(false);
									progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
										@Override
										public void onCancel(DialogInterface dialog) {
											if(task != null)
												task.cancel(true);
										}
									});
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					@Override
					public void callBackResult(List<MaintenaceTask> taskList) {
						if(curPage == 1){
							if(!isFinishing() && null != progressDialog && progressDialog.isShowing()){
								try {
									progressDialog.dismiss();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						if(taskList != null && taskList.size() > 0){
							Log.d(TAG, "taskList size : " +taskList.size());
							adapter.addData(taskList);
							
							adapter.notifyDataSetChanged();
						}else{
							Log.d(TAG, "taskList size : 0");
							if(curPage == 1){
								noResult.setVisibility(View.VISIBLE);
							}
						}
						
						if(taskList == null || taskList.size() < PAGE_SIZE){
							setFooterViewVisibility(View.GONE);
						}else{
							if(curPage == 1){
								setFooterViewVisibility(View.VISIBLE);
							}
						}
						
					}
				});
				task.execute(PAGE_SIZE+"" , curPage+"");
	}
	@Override
	public void onClick(View v) {
		 switch(v.getId()){
		 case R.id.header_left://返回
			 finishActivity();
			 break;
		 }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int last=view.getLastVisiblePosition();
		if(null !=adapter){
			int adapterCnt=adapter.getCount() -1;
			if(last >= adapterCnt && mListFooterView.getVisibility() == View.VISIBLE){
				++curPage;
				loadData();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		 
		  
	}
	
	class MaintenaceTaskAdapter extends BaseAdapter{
		private List<MaintenaceTask> list;
		private LayoutInflater mLayoutInflater;
		
		public MaintenaceTaskAdapter(Context context){
			list=new ArrayList<MaintenaceTask>();
			mLayoutInflater=LayoutInflater.from(context);
		}
		public void addData(List<MaintenaceTask> list){
			if(this.list == null){
				this.list= new ArrayList<MaintenaceTask>();
			}
			this.list.addAll(list);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public MaintenaceTask getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				holder= new ViewHolder();
				convertView=mLayoutInflater.inflate(R.layout.wx_task_download_item, null);
				holder.taskNum=(TextView)convertView.findViewById(R.id.wx_taskNum);
				holder.taskName=(TextView)convertView.findViewById(R.id.wx_taskName);
				holder.taskBtn=(Button)convertView.findViewById(R.id.wx_taskBtn);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder)convertView.getTag();
				Log.d(TAG,"holder: "+ holder );
			}
			holder.taskNum.setText("任务" + (position + 1));
			System.out.println(position + "");
			holder.taskName.setText(getItem(position).taskName);
			
			if(getItem(position).isDownloaded){
				holder.taskBtn.setText("查看");
				holder.taskBtn.setBackgroundColor(Color.parseColor("#005FCB"));
			}else{
				holder.taskBtn.setText("下载");
				holder.taskBtn.setBackgroundColor(Color.parseColor("#2AAE3A"));
			}
			holder.taskBtn.setTag(position);
			holder.taskBtn.setOnClickListener(btnClickListener);
			return convertView;
		}
		
		class ViewHolder {
			TextView taskNum;
			TextView taskName;
			Button taskBtn;
		}
	private DownloadMaintenaceTask downTask;
	private OnClickListener btnClickListener= new OnClickListener(){

		@Override
		public void onClick(View v) {
			final int position=(Integer)v.getTag();
			MaintenaceTask task=getItem(position);
			if(task.isDownloaded){
				/**
				 * 查看任务
				 */
				IntentUtil.startActivity(WxTaskListActivity.this, WxTaskEquipmentListActivity.class);
			}else{
				/**
				 * 下载任务
				 */
				if(NetUtil.isNetworkConnected(context)){
					downTask=new DownloadMaintenaceTask(context,new DownloadMaintenaceTask.TaskCallBack() {
						
						@Override
						public void preExecute() {
							if(!isFinishing()){
								try{
									progressDialog = ProgressDialog.show(context, null, "加载中...",true,true);
									progressDialog.setCanceledOnTouchOutside(false);
									progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
										
										@Override
										public void onCancel(DialogInterface dialog){
											if(downTask !=null)
												downTask.cancel(true);
										}
									});
									
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}
						
						@Override
						public void callBackResult(int code) {
							if(!isFinishing() && null !=progressDialog && progressDialog.isShowing()){
								try{
									progressDialog.dismiss();
								}catch(Exception e){
									e.printStackTrace();
								}
							}
							if(code == DownloadMaintenaceTask.SUCCESS){
								getItem(position).isDownloaded = true;
								ToastUtil.showLongToast(context, "下载成功");
								notifyDataSetChanged();
							}else{
								ToastUtil.showLongToast(context, "下载失败");
							}
						}
					});
					downTask.execute(task.id);
				}else{
					ToastUtil.showLongToast(context, "请检查网络");
				}
			}
		}
		
	};
	}

}
