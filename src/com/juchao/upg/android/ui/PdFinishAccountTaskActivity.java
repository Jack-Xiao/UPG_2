package com.juchao.upg.android.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.AccountEquipmentListAdapter;
import com.juchao.upg.android.db.AccountTaskDao;
import com.juchao.upg.android.entity.AccountEquipment;
import com.juchao.upg.android.view.CustomDialog;

public class PdFinishAccountTaskActivity extends BaseActivity implements OnClickListener{
	public static final String TAG=PdFinishAccountTaskActivity.class.getSimpleName();
	protected static final int MEG = 0;
	public long taskId=0L;
	public String taskName;
	private TextView headerLeft, headerTitle, noResult, notScan;
	private ListView listView;
	private AccountTaskDao dao;
	private AccountEquipmentListAdapter adapter;
	private int notScanCount;
	private Button btnConfirm,btnCancel;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pd_task_account_finish);
		taskId = getIntent().getLongExtra("taskId", 0L);
		taskName = getIntent().getStringExtra("taskName");
		
		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);
		notScan = (TextView) findViewById(R.id.notScan);
		
		headerLeft.setText("已采集");
		headerTitle.setText(taskName + " - 未采集");
		
		listView=(ListView)findViewById(R.id.listView);
		noResult=(TextView)findViewById(R.id.noResult);
		adapter= new AccountEquipmentListAdapter(PdFinishAccountTaskActivity.this);
		btnConfirm=(Button)findViewById(R.id.btnConfirm);
		btnCancel=(Button)findViewById(R.id.btnCancel);
		
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		headerLeft.setOnClickListener(this);
		
		listView.setAdapter(adapter);
		loadData();
	}
	
	private void loadData() {
		new AsyncTask<String, Void, List<AccountEquipment>>() {
			@Override
			protected void onPreExecute() {
				noResult.setVisibility(View.GONE);
			}

			@Override
			protected List<AccountEquipment> doInBackground(String... params) {
				AccountTaskDao dao = new AccountTaskDao(PdFinishAccountTaskActivity.this);
				List<AccountEquipment> list = dao.queryTaskEquipment(taskId, 2);
				notScanCount=list.size();
				dao.closeDB();
				return list;
			}

			@Override
			protected void onPostExecute(List<AccountEquipment> result) {
				if (result != null && result.size() > 0) {
					mHandler.sendEmptyMessage(MEG);
					adapter.addData(result);
				} else {
					mHandler.sendEmptyMessage(MEG);
					noResult.setVisibility(View.GONE);
				}
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.header_left:
			finishActivity();
			break;
		case R.id.btnCancel:
			finishActivity();
			break;
		case R.id.btnConfirm:
			if(listView.getAdapter().getCount() > 0){
	        	CustomDialog.Builder builder = new CustomDialog.Builder(this);  
		        builder.setMessage("本任务有未采集设备，请确认是否完成盘点");
		        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
		            public void onClick(DialogInterface dialog, int which) {  
		            	dialog.dismiss(); 
		            	CloseAccountTask();
		            }
		        });
	            builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {  
	                    dialog.dismiss();  
	                }
	            });
		        builder.create().show();
			}else{
				CloseAccountTask();
			}
			break;
		}
	}

	protected void CloseAccountTask() {
		AccountTaskDao dao=new AccountTaskDao(PdFinishAccountTaskActivity.this);
    	dao.closeAccountTask(taskId);
		finishActivity();
		sendBroadcast(new Intent(PdTaskListActivity.REFRASH_ACTION));
		PdTaskListScanningActivity.instance.finish();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressLint("SimpleDateFormat")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MEG:
				notScan.setText("未采集(" + notScanCount + ")台设备");
				break;
			}
		}
	};
}