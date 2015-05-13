package com.juchao.upg.android.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.juchao.upg.android.MyApp;
import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.AccountEquipmentListAdapter;
import com.juchao.upg.android.db.AccountTaskDao;
import com.juchao.upg.android.entity.AccountEquipment;
import com.juchao.upg.android.task.UploadAccountEquipmentTask;
import com.juchao.upg.android.util.HadwareControll;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.NetUtils;
import com.juchao.upg.android.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import static com.juchao.upg.android.util.FloatBottonUtil.SHAPENORMAL;
import static com.juchao.upg.android.util.FloatBottonUtil.SHAPEPRESSED;

public class PdTaskListScanningActivity extends BaseActivity implements
		OnClickListener {
	private static final String TAG = PdTaskListScanningActivity.class
			.getSimpleName();
	private TextView hasScan, notScan;
	private Button btnFinishScan;
	private Context context;
	// private TextView itemName, itemScan;
	private long taskId;
	public String taskName;
	private TextView headerLeft, headerTitle, noResult;
	private ListView listView;
	private AccountEquipmentListAdapter adapter;
	private int hasScanCount, notScanCount;
	protected MediaPlayer mediaPlayer = null;
	private static final int MEG = 101;
	private AccountTaskDao dao;
	private MainHandler hHandler = new MainHandler();
	public ProgressDialog progressDialog;
	public static PdTaskListScanningActivity instance;
	public static final int BTN_FINISH_TAG = 101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pd_task_list_2);
		taskId = getIntent().getLongExtra("taskId", 0L);
		taskName = getIntent().getStringExtra("taskName");

		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);

		context = this;
		instance = this;
		hasScan = (TextView) findViewById(R.id.hasScan);
		notScan = (TextView) findViewById(R.id.notScan);
		// itemName = (TextView) findViewById(R.id.assertName);
		// itemScan = (TextView) findViewById(R.id.assertCode);
		btnFinishScan = (Button) findViewById(R.id.btnFinishScaned);
		btnFinishScan.setTag(BTN_FINISH_TAG);

		/*
		 * itemName.setText("项目"); itemScan.setText("条码");
		 */

		hasScan.setText("已采集  台设备");
		notScan.setText("未采集(  )");

		headerLeft.setText("盘点");
		headerTitle.setText(taskName);
		noResult = (TextView) findViewById(R.id.noResult);
		listView = (ListView) findViewById(R.id.listView);

		listView.setOnItemClickListener(null);
		adapter = new AccountEquipmentListAdapter(this);
		listView.setAdapter(adapter);
		headerLeft.setOnClickListener(this);
		btnFinishScan.setOnClickListener(this);

		// HadwareControll.Open();
		HadwareControll.m_handler = hHandler;

		loadData();

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

	private void loadData() {
		new AsyncTask<String, Void, List<AccountEquipment>>() {
			@Override
			protected void onPreExecute() {
				noResult.setVisibility(View.GONE);
			}

			@Override
			protected List<AccountEquipment> doInBackground(String... params) {
				dao = new AccountTaskDao(PdTaskListScanningActivity.this);
				List<AccountEquipment> list = dao.queryTaskEquipment(taskId, 1);
				List<AccountEquipment> list1 = dao
						.queryTaskEquipment(taskId, 2);
				hasScanCount = list.size();
				notScanCount = list1.size();
				mHandler.sendEmptyMessage(MEG);

				return list;
			}

			@Override
			protected void onPostExecute(List<AccountEquipment> result) {
				if (result != null && result.size() > 0) {
					adapter.addData(result);
				} else {
					// noResult.setVisibility(View.VISIBLE);
				}
			}
		}.execute();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressLint("SimpleDateFormat")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MEG:
				hasScan.setText("已采集(" + hasScanCount + ")台设备");
				notScan.setText("未采集(" + notScanCount + ")");
				break;
			}
		}
	};

	@SuppressLint("HandlerLeak")
	private class MainHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HadwareControll.BARCODE_READ: {
				String result = msg.obj + "";
				getScanResult(result);
				break;
			}
			}
		}
	};

	private void handleScan(String result) {
		List<AccountEquipment> list;

		list = dao.handleScan(result, taskId);
		if (list.size() > 0) {
			AccountEquipment entity = list.get(0);
			adapter.addData(list); // 添加
			mHandler.sendEmptyMessage(MEG);
			if (notScanCount > 0) {
				notScanCount -= 1;
				hasScanCount += 1;
			}
			if (NetUtils.isNetworkConnected(context)) {
				UploadAccountEquipmentTask task = new UploadAccountEquipmentTask(
						taskId, context, entity,
						new UploadAccountEquipmentTask.TaskCallBack() {
							@Override
							public void preExecute() {
								if (!isFinishing()) {
									try {
										progressDialog = ProgressDialog.show(
												context, null, "上传中...", true,
												true);
										progressDialog
												.setCanceledOnTouchOutside(false);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

							@Override
							public void callBackResult(Integer[] result) {
								if (null != progressDialog
										&& progressDialog.isShowing()) {
									try {
										progressDialog.dismiss();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								if (result[0] == 0 || result[0] == 8) {
									if (result[0] == 0) {
										ToastUtil
												.showLongToast(context, "提交成功");
									} else {
										ToastUtil.showLongToast(context,
												"该盘点任务已结束！请重新下载任务");
									}
								} else {
									ToastUtil.showLongToast(context, "提交失败");
								}
							}
						});
				task.execute();
			} else {
				ToastUtil.showShortToast(context, "请连接网络");
			}
		} else {
			if (dao.queryScan(result, taskId)) {
				ToastUtil.showLongToast(context, "已采集.");
			} else {
				ToastUtil.showLongToast(context, "您采集的条码, 不属于本次任务未采集条码.");
			}
		}
	}

	protected void onDestroy() {
		if (HadwareControll.IsIScan) {
			HadwareControll.Close();
			HadwareControll.m_handler = null;
		}

		dao.closeDB();
		super.onDestroy();
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10
				|| keyCode == KeyEvent.KEYCODE_F11) {
			HadwareControll.scan_start();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10
				|| keyCode == KeyEvent.KEYCODE_F11) {
			HadwareControll.scan_stop();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "当前按下值为:" + v.getId());

		switch (v.getId()) {
		case R.id.header_left: // 返回
			finishActivity();
			break;
		case R.id.btnFinishScaned:
			if ((Integer) v.getTag() == BTN_FINISH_TAG) {
				Bundle bundle = new Bundle();
				bundle.putLong("taskId", taskId);
				bundle.putString("taskName", taskName);
				IntentUtil.startActivityFromMain(
						PdTaskListScanningActivity.this,
						PdFinishAccountTaskActivity.class, bundle);
			}
			break;
		}
	}

	private void getScanResult(String result) {
		if (MyApp.IsIData)
			play_sound();

		// 处理。。。
		handleScan(result);
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