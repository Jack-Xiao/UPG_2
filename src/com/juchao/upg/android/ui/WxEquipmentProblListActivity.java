package com.juchao.upg.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.juchao.upg.android.MyApp;
import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.MaintenaceItemListAdapter;
import com.juchao.upg.android.db.MaintenaceEquipmentDao;
import com.juchao.upg.android.db.MaintenaceTaskDao;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.entity.MaintenaceTaskList;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.scan.qr_codescan.MipcaActivityCapture;

public class WxEquipmentProblListActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private ListView listView;
	private TextView headerLeft, headerTitle, noResult;
	private MaintenaceItemListAdapter adapter;
	protected static final String TAG = "WxTaskItemListActivity";
	private long maintenaceTaskEquipmentId;
	private String maintenaceTaskEquipmentName = "";
	private int from;
	private Long currProblemcorresMaintenaceTaskId;
	private boolean GetTheProblem = true;
	private long problemId;
	private String maintenaceTaskName;
	public static WxEquipmentProblListActivity instance;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.wx_header_left: // 返回
			finishActivity();
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wx_task_item_list_act);

		instance=this;
		maintenaceTaskEquipmentId = getIntent().getLongExtra(
				"maintenaceTaskEquipmentId", -1);
		Log.d(TAG, "maintenaceTaskEquipmentId > " + maintenaceTaskEquipmentId);
		maintenaceTaskEquipmentName = getIntent().getStringExtra(
				"maintenaceTaskEquipmentName");
		from = getIntent().getIntExtra("from", 1);
		currProblemcorresMaintenaceTaskId = getIntent().getLongExtra(
				"maintenaceTaskId", -1);// maintenaceTaskId
		// maintenaceTaskId
		GetTheProblem = getIntent().getBooleanExtra("GetTheProblem", true);
		maintenaceTaskName = getIntent().getStringExtra("taskName");

		headerLeft = (TextView) findViewById(R.id.wx_header_left);
		headerTitle = (TextView) findViewById(R.id.wx_header_title);
		headerLeft.setText("维修");
		headerTitle.setText(maintenaceTaskEquipmentName);

		noResult = (TextView) findViewById(R.id.wx_noResult);
		listView = (ListView) findViewById(R.id.wx_listView);

		adapter = new MaintenaceItemListAdapter(this);
		listView.setAdapter(adapter);
		headerLeft.setOnClickListener(this);
		// +++ 20141107 listview check funation enable should set this one;
		if (from == WxTaskEquipmentListActivity.From_Equipment_Maintenace
				|| from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace) {
			listView.setOnItemClickListener(this);
		}

		loadData();
	}

	private void loadData() {
		new AsyncTask<String, Void, List<MaintenaceTaskEquipmentItem>>() {
			protected void onPreExecute() {
				noResult.setVisibility(View.GONE);
			};

			@Override
			protected List<MaintenaceTaskEquipmentItem> doInBackground(
					String... params) {
				MaintenaceEquipmentDao dao = new MaintenaceEquipmentDao(
						WxEquipmentProblListActivity.this);
				List<MaintenaceTaskEquipmentItem> itemList = null;
				if (GetTheProblem) {
					MaintenaceTaskEquipmentItem item = dao.getTheProblem(
							maintenaceTaskEquipmentId,
							currProblemcorresMaintenaceTaskId);
					if (item != null) {
						problemId = item.id;
						itemList = new ArrayList<MaintenaceTaskEquipmentItem>();
						itemList.add(item);
					}
				} else {
					itemList = dao.queryMaintenaceItemList(
							maintenaceTaskEquipmentId, 0);
				}

				dao.closeDB();
				return itemList;
			}

			protected void onPostExecute(
					List<MaintenaceTaskEquipmentItem> result) {
				if (result != null && result.size() > 0) {
					adapter.addData(result);
				} else {
					noResult.setVisibility(View.VISIBLE);
				}
			};
		}.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		String mess="";
		if (from == WxTaskEquipmentListActivity.From_Query_Maintenace)
			return;

		// int position = (Integer)v.getTag();

		try {
			// tempTaskEntity=getItem(position);
			MaintenaceTaskEquipmentItem entity = (MaintenaceTaskEquipmentItem) parent
					.getAdapter().getItem(position);

			if (entity.uploadState != Constants.TASK_NOT_UPLOAD) {
				if (from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace) {
					mess="已确认";
				} else if (from == WxTaskEquipmentListActivity.From_Equipment_Maintenace) {
					mess="已维修";
				} else {
				}
				Toast.makeText(WxEquipmentProblListActivity.this, mess,
						Toast.LENGTH_SHORT).show();
				//@test:20141127
				return;
			}
			/**
			 * String maintenaceName = new MaintenaceTaskDao(this).
			 * queryMaintenaceTaskByid
			 * (currProblemcorresMaintenaceTaskId).taskName; String equipmentName
			 * =new MaintenaceEquipmentDao(this).
			 * queryEquipmentItemName(entity.maintenaceTaskEquipmentId); //test:
			 * 20141105 暂时取消此设定，等待后台接口完成 /**if(unMaintenaceItemNum == 0 ) {
			 * com.juchao
			 * .upg.android.util.ToastUtil.showLongToast(context,"没有未维修的项目");
			 * return; }
			 */

			MaintenaceTaskDao dao=new MaintenaceTaskDao(this);
			Bundle bundle = new Bundle();
			String taskName = dao.queryMaintenaceTaskByid(currProblemcorresMaintenaceTaskId).taskName;
			String planName=dao.queryPlanName(currProblemcorresMaintenaceTaskId);
			dao.closeDB();
			// String equipmentTDC=new
			// MaintenaceEquipmentDao(this).queryEquipmentTDC(maintenaceTaskEquipmentId);
			 MaintenaceEquipmentDao dao1=new MaintenaceEquipmentDao(this);
			String equipmentTDC = dao1.queryEquipmentTDC(maintenaceTaskEquipmentId);
			dao1.closeDB();
			bundle.putLong("problemId", entity.id);
			bundle.putLong("equipmentId", maintenaceTaskEquipmentId);
			bundle.putLong("maintenaceTaskId",
					currProblemcorresMaintenaceTaskId);
			bundle.putString("mEquipmentName", maintenaceTaskEquipmentName);
			bundle.putInt("from", from);
			bundle.putString("planName", planName);

			bundle.putString("taskName", taskName);
			bundle.putString("equipmentTDC", equipmentTDC);

			if (from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace) {
				IntentUtil.startActivity(this,
						com.juchao.upg.android.ui.WxTaskDetailActivity.class,
						bundle); 
			} else if (from == WxTaskEquipmentListActivity.From_Equipment_Maintenace) {
				IntentUtil.startActivity(this,
						com.juchao.upg.android.ui.WxTaskDetailActivity.class,
						bundle);
			} else {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
