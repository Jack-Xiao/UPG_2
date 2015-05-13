package com.juchao.upg.android.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.juchao.upg.android.MyApp;
import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.InspectionItemListAdapter;
import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.util.FloatBottonUtil;
import com.juchao.upg.android.util.HadwareControll;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.ToastUtil;
import static com.juchao.upg.android.util.FloatBottonUtil.SHAPENORMAL;
import static com.juchao.upg.android.util.FloatBottonUtil.SHAPEPRESSED;

public class DjTaskItemListOperationActivity extends BaseActivity implements
		OnClickListener {
	private ListView listView;
	private TextView headerLeft, headerTitle, noResult;

	private InspectionItemListAdapter adapter;

	protected static final String TAG = "DjTaskListActivity";
	private long inspectionTaskEquipmentId = 0;
	private String inspectionTaskEquipmentName = "";
	private long inspectionTaskId = 0L;
	private int style;
	private TextView finishCount, total;
	private static final int UPDATE = 1;
	private int inFinishCount;
	private int inTotal;
	public static final String REFRESH_ACTION = "dj_task_item_operation_refresh";
	private String taskName;
	protected MediaPlayer mediaPlayer = null;
	public Handler mScanHandler = new MainHandler();
	private Context context;
	private long equipmentId;
	private Map<String, Long> scanCodeIdMap = new HashMap<String, Long>();
	private Map<String, Long> scanCodeWholeIdMap = new HashMap<String, Long>();
	public static DjTaskItemListOperationActivity instance;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dj_task_item_list_operation_act);

		inspectionTaskEquipmentId = getIntent().getLongExtra(
				"inspectionTaskEquipmentId", 0);
		inspectionTaskId = getIntent().getLongExtra("inspectionTaskId", 0L);
		inspectionTaskEquipmentName = getIntent().getStringExtra(
				"mEquipmentName");
		style = getIntent().getIntExtra("style", 0);
		taskName = getIntent().getStringExtra("taskName");
		equipmentId = getIntent().getLongExtra("equipmentId", 0L);

		context = this;
		instance = this;
		HadwareControll.m_handler = mScanHandler;

		// HadwareControll.Open();

		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);
		headerLeft.setText("开始点检");

		headerTitle.setText(taskName + "-" + inspectionTaskEquipmentName
				+ "--------------------------------");

		// leftBracket=(TextView)findViewById(R.id.leftBracket);
		finishCount = (TextView) findViewById(R.id.finishCount);
		total = (TextView) findViewById(R.id.total);

		total.setText("0/0)");

		noResult = (TextView) findViewById(R.id.noResult);
		listView = (ListView) findViewById(R.id.listView);

		listView.setOnItemClickListener(mOnItemClick);
		adapter = new InspectionItemListAdapter(this);
		listView.setAdapter(adapter);

		headerLeft.setOnClickListener(this);

		// listView.setOnItemLongClickListener(mOnItemLongClick);

		loadData();

		IntentFilter filter = new IntentFilter();
		filter.addAction(REFRESH_ACTION);
		registerReceiver(receiver, filter);

		initFloatBottonAndScanning();
	}
	
	private View fab;
	private int screenWidth, screenHeight;
	private float x1, y1, x2, y2;
	private int lastX, lastY;
	private int left, right, top, bottom;

	private void initFloatBottonAndScanning() {
		fab = findViewById(R.id.fab);
		fab.getBackground().setAlpha(SHAPENORMAL);
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		screenHeight = getResources().getDisplayMetrics().heightPixels;
		fab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				judegScan(true);
			}
		});

		fab.setOnTouchListener(imgListener);

	}

	private View.OnTouchListener imgListener = new View.OnTouchListener() {
		private float x, y; // 原来控件的位置
		private int mx, my; // 控件被拖曳的x,y轴距离长度
		private float startX, endX;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				fab.getBackground().setAlpha(SHAPEPRESSED);
				lastX = (int) event.getRawX();
				lastY = (int) event.getRawY();
				x1 = event.getRawX();
				y1 = event.getRawY();
			 
				break;
			case MotionEvent.ACTION_MOVE:
				fab.getBackground().setAlpha(SHAPEPRESSED);
				int dx = (int) event.getRawX() - lastX;
				int dy = (int) event.getRawY() - lastY;

				left = v.getLeft() + dx;
				top = v.getTop() + dy;
				right = v.getRight() + dx;
				bottom = v.getBottom() + dy;
				if (left < 0) {
					left = 0;
					right = left + v.getWidth();
				}
				if (right > screenWidth) {
					right = screenWidth;
					left = right - v.getWidth();
				}
				if (top < 0) {
					top = 0;
					bottom = top + v.getHeight();
				}
				if (bottom > screenHeight) {
					bottom = screenHeight;
					top = bottom - v.getHeight();
				}

				v.layout(left, top, right, bottom);
				lastX = (int) event.getRawX();
				lastY = (int) event.getRawY();

				break;
			case MotionEvent.ACTION_UP:
				fab.getBackground().setAlpha(SHAPENORMAL);
				x2 = event.getRawX();
				y2 = event.getRawY();
				double distance = Math.sqrt(Math.abs(x1 - x2)
						* Math.abs(x1 - x2) + Math.abs(y1 - y2)
						* Math.abs(y1 - y2));
				if (distance < 15) {

				} else {
					return true;
				}
			}
			return false;
		}
	};

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action != null && action.equals(REFRESH_ACTION)) {
				refrash();
			}
		}
	};

	private void refrash() {
		com.juchao.upg.android.util.Log.fileLog("",
				"DjTaskItemListOperation--- refrash");

		adapter.clearData();
		loadData();
	}

	private void loadData() {
		new AsyncTask<String, Void, List<InspectionTaskEquipmentItem>>() {
			protected void onPreExecute() {
				noResult.setVisibility(View.GONE);
			};

			@Override
			protected List<InspectionTaskEquipmentItem> doInBackground(
					String... params) {

				InspectionEquipmentDao dao = new InspectionEquipmentDao(
						DjTaskItemListOperationActivity.this);
				List<InspectionTaskEquipmentItem> itemList = dao
						.queryInspectionItemList(inspectionTaskId,
								inspectionTaskEquipmentId, 0, style);

				List<InspectionTaskEquipmentItem> itemList1 = dao
						.queryInspectionItemList(inspectionTaskId,
								inspectionTaskEquipmentId, 4, style);

				int finishScan = 0;
				int wholeScan = 0;
				if (itemList != null) {
					scanCodeWholeIdMap = dao.queryScanCodeIdsMap(itemList);
					wholeScan = scanCodeWholeIdMap.size();
				}
				Log.d("DjTaskItemActivity ", "------------------"
						+ scanCodeWholeIdMap.size());
				if (itemList1 != null) {
					finishScan = itemList1.size();
					scanCodeIdMap = dao.queryScanCodeIdsMap(itemList1);
				}

				dao.closeDB();
				sendMessage(finishScan, wholeScan);

				return itemList;
			}

			private void sendMessage(int inFinishCount, int total) {
				com.juchao.upg.android.util.Log.fileLog("",
						"DjTaskItemListOperation--- sendMessage");
				Message msg = new Message();
				msg.what = UPDATE;
				msg.arg1 = inFinishCount;
				msg.arg2 = total;
				mHandler.sendMessage(msg);
			}

			protected void onPostExecute(
					List<InspectionTaskEquipmentItem> result) {
				if (result != null && result.size() > 0) {
					Log.d("current operation list size",
							"size:  " + result.size());
					Log.d("current operation adapter size",
							"size:  " + adapter.getCount());
					adapter.clearData();
					adapter.addData(result);
				} else {
					noResult.setVisibility(View.VISIBLE);
				}
				// mHandler.sendEmptyMessage(UPDATE);
			};
		}.execute();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.header_left: // 返回
			finishActivity();
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE:
				finishCount.setText(msg.arg1 + "");
				total.setText("/" + msg.arg2 + ")");
				break;
			}
		}
	};

	private OnItemClickListener mOnItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			InspectionTaskEquipmentItem item = (InspectionTaskEquipmentItem) parent
					.getAdapter().getItem(position);

			InspectionEquipmentDao dao = new InspectionEquipmentDao(
					DjTaskItemListOperationActivity.this);

			Bundle bundle = new Bundle();

			String scanCode = dao
					.GetBaseEquipmentSpotcheckProjectNo(item.equipmentSpotcheckId);
			dao.closeDB();

			// bundle.putLong("inspectionTaskId", inspectionTaskId);
			// bundle.putLong("inspectionTaskEquipmentId",
			// inspectionTaskEquipmentId);
			// bundle.putLong("equipmentId", equipmentId);
			// bundle.putString("mEquipmentName", mEquipmentName);
			// bundle.putString("taskName", taskName);
			// bundle.putInt("style",style);

			bundle.putSerializable("inspectionItem", item);
			bundle.putLong("inspectionTaskId", inspectionTaskId);
			bundle.putString("mEquipmentName", inspectionTaskEquipmentName);
			bundle.putString("scanCode", scanCode);
			bundle.putString("taskName", taskName);
			bundle.putInt("style", style);
			bundle.putLong("equipmentId", equipmentId);
			bundle.putLong("equipmentSpotcheckId", item.equipmentSpotcheckId);

			IntentUtil.startActivityFromMain1(
					DjTaskItemListOperationActivity.this,
					DjTaskItemQueryDetailActivity.class, bundle);

		}
	};

	@SuppressLint("HandlerLeak")
	private class MainHandler extends Handler {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HadwareControll.BARCODE_READ: {
				play_sound();

				String result = msg.obj + "";
				getScanResult(result);
			}
			default:
				break;
			}
		}

	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10
				|| keyCode == KeyEvent.KEYCODE_F11) {
			HadwareControll.scan_start();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void SetpNext(String result) {
		Bundle bundle = new Bundle();
		bundle.putLong("inspectionTaskId", inspectionTaskId);
		bundle.putLong("inspectionTaskEquipmentId", inspectionTaskEquipmentId);
		bundle.putLong("equipmentId", equipmentId);
		bundle.putString("taskName", taskName);
		bundle.putString("mEquipmentName", inspectionTaskEquipmentName);
		bundle.putInt("style", style);
		bundle.putString("scanCode", result.toString());
		// bundle.putLong("inspectionItemId",
		// scanCodeWholeIdMap.get(result.toString()));
		bundle.putLong("spotcheckId", scanCodeWholeIdMap.get(result.toString()));
		// inspectionTaskId = getIntent().getLongExtra("inspectionTaskId", 0L);
		// inspectionTaskEquipmentId = getIntent().getLongExtra(
		// "inspectionTaskEquipmentId", 0);

		// equipmentId = getIntent().getLongExtra("equipmentId", 0);
		// mEquipmentName = getIntent().getStringExtra("mEquipmentName");
		// taskName = getIntent().getStringExtra("taskName");
		// style=getIntent().getIntExtra("style", 0);

		IntentUtil.startActivityFromMain(DjTaskItemListOperationActivity.this,
				DjTaskDetailActivity.class, bundle);

	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10
				|| keyCode == KeyEvent.KEYCODE_F11) {
			HadwareControll.scan_stop();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void play_sound() {
		try {
			if (mediaPlayer == null)
				mediaPlayer = MediaPlayer.create(this, R.raw.beep);
			mediaPlayer.stop();
			mediaPlayer.prepare();
			mediaPlayer.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onDestroy() {

		HadwareControll.Close();
		HadwareControll.m_handler = null;
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private void getScanResult(String result) {

		if (!scanCodeWholeIdMap.keySet().contains(result)) {
			// ToastUtil.showLongToast(context, "您点检的项目，不属于本设备");
			ToastUtil.showLongToast(context, "您点检的项目，不属于本次任务项目");
			return;
		}
		SetpNext(result);
	}
	

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
			case SCANNIN_GREQUEST_CODE:
				if (resultCode == RESULT_OK) {
					Bundle bundle = data.getExtras();
					scanningCode = bundle.getString("result");
					getScanResult(scanningCode);
				}
				break;
		}
	}
}
