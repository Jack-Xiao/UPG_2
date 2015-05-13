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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.MaintenaceTask;
import com.juchao.upg.android.task.GetMaintenaceWaitUploadTaskList;
import com.juchao.upg.android.task.UploadMultMaintenaceItemTask;
import com.juchao.upg.android.ui.DjTaskDownloadActivity.InspectionTaskAdapter.ViewHolder;
import com.juchao.upg.android.ui.WxTaskListActivity.MaintenaceTaskAdapter;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.NetUtil;
import com.juchao.upg.android.util.ToastUtil;

public class WxTaskUploadActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnScrollListener {
	protected static final String TAG = WxTaskUploadActivity.class
			.getSimpleName();

	private ListView listView;
	private TextView headerLeft, headerTitle, noResult;
	private int curpage = 1;
	private static final int PAGE_SIZE = 50;
	public ProgressDialog progressDialog;
	private MaintenaceTaskAdapter adapter;
	private Button btnUpload;
	private Context context;
	private List<MaintenaceTask> taskList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wx_task_upload_act);

		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);

		headerLeft.setText("结果上传");
		headerTitle.setText("待上传数据列表");

		noResult = (TextView) findViewById(R.id.wx_upload_noResult);
		listView = (ListView) findViewById(R.id.wx_upload_listview);
		btnUpload = (Button) findViewById(R.id.wx_upload_btnUpload);

		btnUpload.setBackgroundColor(Color.parseColor("#005FCB"));
		btnUpload.setOnClickListener(this);
		listView.setOnScrollListener(this);
		listView.setOnItemClickListener(this);

		mListFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.loadmore_bar, listView, false);
		mListFooterProgress = (ProgressBar) mListFooterView
				.findViewById(R.id.progress);
		mLoadingText = (TextView) mListFooterView
				.findViewById(R.id.loading_text);
		listView.addFooterView(mListFooterView, null, false);

		adapter = new MaintenaceTaskAdapter(this);
		listView.setAdapter(adapter);

		headerLeft.setOnClickListener(this);
		curpage = 1;
		loadData(DEFAULT);
	}

	private GetMaintenaceWaitUploadTaskList task;
	private static final int REFRESH = 1;
	private static final int DEFAULT = 0;

	private void loadData(final int type) { 
		task = new GetMaintenaceWaitUploadTaskList(WxTaskUploadActivity.this,
				new GetMaintenaceWaitUploadTaskList.TaskCallBack(){
			@Override
			public void preExecute(){
				noResult.setVisibility(View.GONE);
				if(curpage == 1){
					setFooterViewVisibility(View.GONE);
					if(!isFinishing()){
							try{
								progressDialog = ProgressDialog.show(context,null, "加载中...",true,true);
								progressDialog.setCanceledOnTouchOutside(false);
								progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
									@Override
									public void onCancel(DialogInterface dialog) {
										if(task != null)
											task.cancel(true);
									}
								});
							}catch(Exception e){
								e.printStackTrace();
						}
					}
				}
			}
			@Override
			public void callBackResult(List<MaintenaceTask> list){
				if(curpage == 1){
					if(!isFinishing () && null !=progressDialog && progressDialog.isShowing()){
						try{
							progressDialog.dismiss();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
				if(list !=null && list.size() > 0 ){
					taskList = list;
					Log.d(TAG, "taskList size: " + list.size());
					if(type == DEFAULT){
						adapter.addData(list);
					}else{
						adapter.setData(list);
					}
					adapter.notifyDataSetChanged();
				}else{
					Log.d(TAG, "taskList size : 0");
					if(type == REFRESH){
						adapter.setData(new ArrayList<MaintenaceTask>());
						adapter.notifyDataSetChanged();
					}
					if(curpage == 1){
						noResult.setVisibility(View.VISIBLE);
					}
				}
				if(list == null || list.size() <PAGE_SIZE){
					setFooterViewVisibility(View.GONE);
				}else{
					if(curpage == 1){
						setFooterViewVisibility(View.VISIBLE);
					}
				}
			}
		});
		task.execute(PAGE_SIZE + "", curpage +"");
	}

	private UploadMultMaintenaceItemTask uploadTask;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		 
			MaintenaceTask mMaintenaceTask = (MaintenaceTask) parent
					.getAdapter().getItem(position);
			
			Bundle bundle = new Bundle();
			
			bundle.putLong("maintenaceTaskId", mMaintenaceTask.id);
			bundle.putLong("problemId", mMaintenaceTask.problemId);
			//bundle.putString("inspectionTaskEquipmentName", tempTaskEntity.name);
			IntentUtil.startActivity(WxTaskUploadActivity.this,
					WxTaskUploadItemListActivity.class, bundle);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.header_left:
			finishActivity();
			break;
		case R.id.wx_upload_btnUpload:
			if (taskList == null || taskList.size() <= 0) {
				ToastUtil.showShortToast(context, "没有需要上传的任务");
				break;
			}
			if (!NetUtil.isNetworkConnected(context)) {
				ToastUtil.showShortToast(context, "请检查网络");
				break;
			}
			uploadTask = new UploadMultMaintenaceItemTask(context, taskList,
					new UploadMultMaintenaceItemTask.TaskCallBack() {
						@Override
						public void preExecute() {
							if (!isFinishing()) {
								try {
									progressDialog = ProgressDialog
											.show(context, null, "加载中...",
													true, true);
									progressDialog
											.setCanceledOnTouchOutside(false);
									progressDialog
											.setOnCancelListener(new DialogInterface.OnCancelListener() {
												@Override
												public void onCancel(
														DialogInterface dialog) {
													if (task != null)
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
							if (!isFinishing() && null != progressDialog
									&& progressDialog.isShowing()) {
								try {
									progressDialog.dismiss();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							if (result[0] > 0 && result[1] == 0) {
								ToastUtil.showLongToast(context, "上传成功");
							//} //else if (result[0] > 0 && result[1] > 0) {
								//ToastUtil.showShortToast(context, "部分项目上传成功");
							}
							else {
								ToastUtil.showLongToast(context, "上传失败");
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
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int last = view.getLastVisiblePosition();
		if (null != adapter) {
			int adapterCnt = adapter.getCount() - 1;
			if (last >= adapterCnt
					&& mListFooterView.getVisibility() == View.VISIBLE) {
				++curpage;
				loadData(DEFAULT);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	class MaintenaceTaskAdapter extends BaseAdapter {
		private List<MaintenaceTask> list;
		private LayoutInflater mLayoutInflater;

		public MaintenaceTaskAdapter(Context context) {
			list = new ArrayList<MaintenaceTask>();
			mLayoutInflater = LayoutInflater.from(context);
		}

		public void addData(List<MaintenaceTask> list) {
			if (this.list == null) {
				this.list = new ArrayList<MaintenaceTask>();
			}
			this.list.addAll(list);
		}
		
		public void setData(List<MaintenaceTask> list){
			this.list = list;
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
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(
						R.layout.wx_task_upload_item, null);
				holder.taskNum = (TextView) convertView
						.findViewById(R.id.wx_upload_taskNum);
				holder.taskName = (TextView) convertView
						.findViewById(R.id.wx_upload_taskName);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			int index = position + 1;
			holder.taskNum.setText("任务" + index + "、");
			holder.taskName.setText(getItem(position).taskName + "、 ("
					+ getItem(position).equipmentNum + "台设备，待上传"+ getItem(position).problemNum +"项)");
			return convertView;
		}

		class ViewHolder {
			TextView taskNum;
			TextView taskName;
		}

	}
}
