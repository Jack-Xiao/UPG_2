package com.juchao.upg.android.ui;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.WaitUploadMaintenaceItemListAdapter;
import com.juchao.upg.android.db.MaintenaceTaskDao;
import com.juchao.upg.android.entity.MaintenaceTaskEquipment;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;

public class WxTaskUploadItemListActivity extends BaseActivity implements
		OnClickListener {

	private ListView listView;
	private TextView headerLeft, headerTitle, noResult;
	private WaitUploadMaintenaceItemListAdapter adapter;
	protected static final String TAG = WxTaskUploadItemListActivity.class
			.getSimpleName();
	private long maintenaceTaskId = 0;
	private long problemId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dj_task_item_list_act);

		maintenaceTaskId = getIntent().getLongExtra("maintenaceTaskId", 0);
		problemId=getIntent().getLongExtra("problemId",0);
		
		Log.d(TAG, "inspectionTaskId > " + maintenaceTaskId);
		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);
		headerLeft.setText("任务查看");
		headerTitle.setText("待上传数据列表");

		noResult = (TextView) findViewById(R.id.noResult);
		listView = (ListView) findViewById(R.id.listView);

		adapter = new WaitUploadMaintenaceItemListAdapter(this);
		listView.setAdapter(adapter);
		headerLeft.setOnClickListener(this);
		

		loadData();
	}

	private void loadData() {
		new AsyncTask<String, Void, List<MaintenaceTaskEquipmentItem>>() {
			protected void onPreExecute() {
				noResult.setVisibility(View.GONE);
			}

			@Override
			protected List<MaintenaceTaskEquipmentItem> doInBackground(
					String... params) {
				MaintenaceTaskDao dao = new MaintenaceTaskDao(
						WxTaskUploadItemListActivity.this);
				// List<MaintenaceTaskEquipmentItem> itemList=
				// dao.queryMaintenaceItemList(maintenaceTaskId);
				/*List<MaintenaceTaskEquipmentItem> itemList = dao
						.queryMaintenaceItemProblemName(maintenaceTaskId);
				dao.closeDB();*/
				List<MaintenaceTaskEquipmentItem> itemList = dao
				.queryMaintenaceItemProblemName(problemId);
				return itemList;

			}
			
			protected void onPostExecute(List<MaintenaceTaskEquipmentItem> result) {
				if (result != null && result.size() > 0) {
					adapter.addData(result);  
					adapter.notifyDataSetChanged();
				} else {
					noResult.setVisibility(View.VISIBLE);
				}
			};

		}.execute();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_left:
			finishActivity();
			break;
		}

	}

}
