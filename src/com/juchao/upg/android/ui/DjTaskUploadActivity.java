package com.juchao.upg.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.InspectionTask;
import com.juchao.upg.android.task.GetWaitUploadTaskList;
import com.juchao.upg.android.task.UploadMultInspectionItemTask;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.NetUtils;
import com.juchao.upg.android.util.ToastUtil;

public class DjTaskUploadActivity extends BaseActivity implements OnClickListener, OnItemClickListener, ListView.OnScrollListener{
	protected static final String TAG = DjTaskUploadActivity.class.getSimpleName();
    
    private ListView listView;
    private TextView headerLeft , headerTitle ,noResult;
    private int curpage = 1;
	private static final int PAGE_SIZE = 50;

	public ProgressDialog progressDialog;
    private InspectionTaskAdapter adapter;
    private Button btnUpload;
    private Context context;
    private List<InspectionTask> taskList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.dj_task_upload_act);
       
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		headerLeft.setText("点检");
		headerTitle.setText("待上传数据列表");
		
		noResult= (TextView)findViewById(R.id.noResult);
		listView = (ListView)findViewById(R.id.listView);
		btnUpload = (Button)findViewById(R.id.btnUpload);
		
		btnUpload.setBackgroundColor(Color.parseColor("#005FCB"));
		btnUpload.setOnClickListener(this);
		listView.setOnScrollListener(this);
		listView.setOnItemClickListener(this);
		mListFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.loadmore_bar, listView,false);
		mListFooterProgress = (ProgressBar) mListFooterView.findViewById(R.id.progress);
		mLoadingText = (TextView) mListFooterView.findViewById(R.id.loading_text);
		listView.addFooterView(mListFooterView, null, false);
		
		adapter = new InspectionTaskAdapter(this);
		listView.setAdapter(adapter);
		
		headerLeft.setOnClickListener(this);
		curpage = 1;
		loadData(DEFAULT);
	}
	private GetWaitUploadTaskList task;
	private static final int REFRESH = 1;
	private static final int DEFAULT = 0;
	private void loadData(final int type) {
		
		task = new GetWaitUploadTaskList(DjTaskUploadActivity.this,
				new GetWaitUploadTaskList.TaskCallBack() {
			
			@Override
			public void preExecute() {
				noResult.setVisibility(View.GONE);
				if(curpage == 1){
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
			public void callBackResult(List<InspectionTask> list) {
				
				if(curpage == 1){
					if(!isFinishing() && null != progressDialog && progressDialog.isShowing()){
						try {
							progressDialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				if(list != null && list.size() > 0){
					taskList = list;
					Log.d(TAG, "taskList size : " +list.size());
					if(type == DEFAULT){
						adapter.addData(list);
					}else{
						adapter.setData(list);
					}
					adapter.notifyDataSetChanged();
				}else{
					Log.d(TAG, "taskList size : 0");
					
					if(type == REFRESH){
						adapter.setData(new ArrayList<InspectionTask>());
						adapter.notifyDataSetChanged();
					}
					if(curpage == 1){
						noResult.setVisibility(View.VISIBLE);
					}
				}
				
				if(list == null || list.size() < PAGE_SIZE){
					setFooterViewVisibility(View.GONE);
				}else{
					if(curpage == 1){
						setFooterViewVisibility(View.VISIBLE);
					}
				}
			}
		});
		task.execute(PAGE_SIZE+"" , curpage+"");
	}

	private UploadMultInspectionItemTask uploadTask;
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.header_left:  //返回
			finishActivity();
			break;
		case R.id.btnUpload: //上传
			if(taskList == null || taskList.size() <= 0){
				ToastUtil.showShortToast(context, "没有需要上传的任务");
				break;
			}
			if(!NetUtils.isNetworkConnected(context)){
				ToastUtil.showShortToast(context, "请检查网络");
				break;
			}
			uploadTask = new UploadMultInspectionItemTask(context ,taskList , 
					new UploadMultInspectionItemTask.TaskCallBack(){
						@Override
						public void preExecute() {
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

						@Override
						public void callBackResult(Integer[] result) {
							if(!isFinishing() && null != progressDialog && progressDialog.isShowing()){
								try {
									progressDialog.dismiss();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
							if(result[2] == 1){
								ToastUtil.showLongToast(context, "点检分类与设备状态不符, 请重新点检!");
								Dialog d=new Dialog(context);
								
								d.setTitle("");
								d.show();
								
							}else{
								 if(result[0] > 0 && result[1] ==0){
									 ToastUtil.showLongToast(context, "上传成功");
								 }else if(result[0] > 0 && result[1] > 0){
									 ToastUtil.showLongToast(context, "部分项目上传成功");
								 }else{
									 ToastUtil.showLongToast(context, "上传失败");
								 }
							}
							
							curpage = 1;
							loadData(REFRESH);
						}
			});
			uploadTask.execute();
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try{
			InspectionTask mInspectionTask = (InspectionTask)parent.getAdapter().getItem(position);
			Bundle  bundle = new Bundle();
			bundle.putLong("inspectionTaskId", mInspectionTask.id);
			bundle.putInt("style", mInspectionTask.status);
			IntentUtil.startActivity(DjTaskUploadActivity.this, DjTaskUploadItemListActivity.class, bundle);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	class InspectionTaskAdapter extends BaseAdapter {
		private List<InspectionTask> list;
		private LayoutInflater mLayoutInflater;

		public InspectionTaskAdapter(Context context) {
			list = new ArrayList<InspectionTask>();
			mLayoutInflater = LayoutInflater.from(context);
		}

		public void addData(List<InspectionTask> list){
			if(this.list ==null){
				this.list = new ArrayList<InspectionTask>();
			}
			
			this.list.addAll(list);
		}
		
		public void setData(List<InspectionTask> list){
			this.list = list;
		}
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public InspectionTask getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.dj_task_upload_item, null);
				holder.taskNum = (TextView) convertView.findViewById(R.id.taskNum);
				holder.taskName = (TextView) convertView.findViewById(R.id.taskName);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.taskNum.setText("任务" +(position + 1));
			holder.taskName.setText(getItem(position).taskName + "(待上传" +getItem(position).waitUploadNum+ "项)");
			return convertView;
		}
		class ViewHolder {
			TextView taskNum;
			TextView taskName;
		}
	}

	
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int last = view.getLastVisiblePosition();
		
		if(null != adapter){
			int adapterCnt = adapter.getCount()-1;
			if(last >= adapterCnt && mListFooterView.getVisibility()==View.VISIBLE){
				++curpage;
				loadData(DEFAULT);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
}
