package com.juchao.upg.android.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.AccountTask;
import com.juchao.upg.android.task.DownloadAccountTask;
import com.juchao.upg.android.task.GetAccountTaskList;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.NetUtils;
import com.juchao.upg.android.util.ToastUtil;

public class PdTaskDownloadActivity extends BaseActivity implements
		OnClickListener, OnScrollListener {
	protected static final String TAG = PdTaskDownloadActivity.class
			.getSimpleName();

	private Context context;
	private ListView listView;
	private TextView headerLeft, headerTitle, noResult;
	private int curpage = 1;
	private int totalpage;
	private static final int PAGE_SIZE = 20;
	private AccountTaskAdapter adapter;
	private GetAccountTaskList task;
	public ProgressDialog progressDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.pd_task_download_act);

		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);
		headerLeft.setText("盘点");
		headerTitle.setText("盘点任务列表");

		noResult = (TextView) findViewById(R.id.noResult);
		listView = (ListView) findViewById(R.id.listView);

		listView.setOnScrollListener(this);
		mListFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.loadmore_bar, listView, false);
		mListFooterProgress = (ProgressBar) mListFooterView
				.findViewById(R.id.progress);
		mLoadingText = (TextView) mListFooterView
				.findViewById(R.id.loading_text);
		listView.addFooterView(mListFooterView, null, false);

		adapter = new AccountTaskAdapter(this);
		listView.setAdapter(adapter);
		headerLeft.setOnClickListener(this);
		loadData();
	}

	private void loadData() {
		task = new GetAccountTaskList(context,
				new GetAccountTaskList.TaskCallBack() {

					@Override
					public void preExecute() {
						noResult.setVisibility(View.GONE);
						if (curpage == 1) {
							setFooterViewVisibility(View.GONE);
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
													if (task != null) {
														task.cancel(true);
													}
												}
											});
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}

					@Override
					public void callBackResult(List<AccountTask> taskList) {
						if (curpage == 1) {
							if (!isFinishing() && null != progressDialog
									&& progressDialog.isShowing()) {
								try {
									progressDialog.dismiss();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						if (taskList != null && taskList.size() > 0) {
							adapter.addData(taskList);
						} else {
							if (curpage == 1) {
								noResult.setVisibility(View.VISIBLE);
							}
						}
						if (taskList == null || taskList.size() < PAGE_SIZE) {
							setFooterViewVisibility(View.GONE);
						} else {
							if (curpage == 1) {
								setFooterViewVisibility(View.VISIBLE);
							}
						}
					}
				});
		task.execute(PAGE_SIZE + "", curpage + "");
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int last = view.getLastVisiblePosition();
		if (null != adapter) {
			int adapterCnt = adapter.getCount() - 1;
			if (last > adapterCnt
					&& mListFooterView.getVisibility() == View.VISIBLE) {
				++curpage;
				loadData();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_left: // 返回
			finishActivity();
			break;
		}
	}

	class AccountTaskAdapter extends BaseAdapter {
		private List<AccountTask> list;
		private LayoutInflater mLayoutInflater;
		
		
		public AccountTaskAdapter(Context context) {
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
			AccountTask task=getItem(position);
			holder.taskNum.setText("任务" + (position + 1)+"、");
			holder.taskName.setText(task.name + "(共"+task.total + "台设备)");
			holder.startTime.setText("开始时间："+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(task.startTime));
			
			if (getItem(position).isDownloaded) {
				holder.taskBtn.setText("查看");
				holder.taskBtn.setBackgroundColor(Color.parseColor("#005FCB"));
			} else {
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
			TextView startTime;
		}

		private DownloadAccountTask downloadTask;
		private OnClickListener btnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				final int positon = (Integer) v.getTag();
				AccountTask task = getItem(positon);
				if (task.isDownloaded) {
					/**
					 * 查看任务
					 */
					IntentUtil.startActivity(PdTaskDownloadActivity.this,
							PdTaskListActivity.class);

				} else {
					/**
					 * 下载任务
					 */

					if (NetUtils.isNetworkConnected(context)) {
						downloadTask = new DownloadAccountTask(context,
								new DownloadAccountTask.TaskCallBack() {
									@Override
									public void preExecute() {
										if (!isFinishing()) {
											try {
												progressDialog = ProgressDialog
														.show(context, null,
																"加载中...", true,
																true);
												progressDialog
														.setCanceledOnTouchOutside(false);
												progressDialog
														.setOnCancelListener(new DialogInterface.OnCancelListener() {
															@Override
															public void onCancel(
																	DialogInterface dialog) {
																if (downloadTask != null)
																	downloadTask
																			.cancel(true);
															}
														});
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}

									@Override
									public void callBackResult(int code) {
										if (!isFinishing()
												&& null != progressDialog
												&& progressDialog.isShowing()) {
											try {
												progressDialog.dismiss();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}

										if (code == DownloadAccountTask.SUCCESS) {
											getItem(positon).isDownloaded = true;
											ToastUtil.showLongToast(context,
													"下载成功");
											notifyDataSetChanged();
										} else {
											ToastUtil.showLongToast(context,
													"下载失败");
										}
									}

								});
						downloadTask.execute(task.id);
					} else {
						ToastUtil.showLongToast(context, "请检查网络");
					}
				}
			}
		};
	}
}
