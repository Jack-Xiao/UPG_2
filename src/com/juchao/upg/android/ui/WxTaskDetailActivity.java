package com.juchao.upg.android.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juchao.upg.android.MyApp;
import com.juchao.upg.android.R;
import com.juchao.upg.android.db.BaseDataDao;
import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.db.MaintenaceEquipmentDao;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentProblem;
import com.juchao.upg.android.entity.ResEquipmentSparePart1;
import com.juchao.upg.android.task.SaveMaintenaceItemTask;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.FileUtil;
import com.juchao.upg.android.util.HadwareControll;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.ToastUtil;
import com.juchao.upg.android.util.scan.qr_codescan.MipcaActivityCapture;
import com.juchao.upg.android.view.PopupAddImageUtil;
import com.juchao.upg.android.view.PopupReplaceSparePartUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WxTaskDetailActivity extends BaseActivity implements
		OnClickListener {

	protected static final String TAG = WxTaskDetailActivity.class
			.getSimpleName();
	public Handler mHandler = new MainHandler();
	protected MediaPlayer mediaPlayer = null;

	private RelativeLayout header_layout;
	private TextView headerLeft, headerTitle;
	private LinearLayout addContainer, addtionLayout, resultLayout;
	private Button headerAdd, btnConfirm, btnCancel, btnBegin;

	private Context context;
	private LinearLayout codeLayout, planLayout, equipmentNum, equipmentName,
			itemIndex, itemName, way, expendTime, standard, maintenaceState,
			beginMaintenace,problemDesc,problemSuggestion,problemMeasure,sparepartlayout,imageInfoContainerlayout;
	private TextView code_title, plan_title, equipmentNum_title,
			equipmentName_title, itemIndex_title, itemName_title, way_title,
			expendTime_title, standard_title, maintenaceState_title,problemDesc_title,problemSuggestion_title,problemMeasure_title,sparepartlayout_title;
	private TextView code_content, plan_content, equipmentNum_content,
			equipmentName_content, itemIndex_content, itemName_content,
			way_content, expendTime_content, standard_content,
			maintenaceState_content,problemDesc_content,problemSuggestion_content,problemMeasure_content,sparepartlayout_content;
	private LinearLayout maintenaceTask;
	private TextView maintenaceTask_title;
	private TextView maintenaceTask_content;

	private long startTime;
	private long endTime;

	// 需要传入的
	private long maintenaceTaskEquipmentId;
	private long equipmentId;
	private String managementOrgNum = ""; // 设备编号，需根据equipmentId查询
	private String mEquipmentName; // (待补)
	private String taskName; // (待补)
	private int mItemIndex = 1; // 项目序号
	// 图片名称
	private String imageNames = ""; // 多个图片，名称以"," 分隔
	private boolean isScaned;

	// 维修问题id
	private long problemId;

	private Map<Long, Long> maintenaceItemIdMap = new HashMap<Long, Long>();

	// 设备二维码
	private String equipmentTDC;
	// 效果确认
	private int from;
	// 计划名称
	private String planName;

	public static WxTaskDetailActivity instance;
	private String curTempImage = "temp.png";
	public static final int UPDATEPROBLEM= 1;
	private MaintenaceTaskEquipmentProblem curProblem;
	private String sparepartContent;
	private StringBuilder photoTotal;


	@SuppressLint("ResourceAsColor")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wx_task_detail_act);

		instance = this;
		context = this;
		photoTotal = new StringBuilder();
		//HadwareControll.Open();
		HadwareControll.m_handler = mHandler;
		startTime = System.currentTimeMillis();
		maintenaceTaskEquipmentId = getIntent().getLongExtra(
				"maintenaceTaskEquipmentId", 0);
		equipmentId = getIntent().getLongExtra("equipmentId", 0);
		mEquipmentName = getIntent().getStringExtra("mEquipmentName");
		taskName = getIntent().getStringExtra("taskName");
		equipmentTDC = getIntent().getStringExtra("equipmentTDC");
		problemId = getIntent().getLongExtra("problemId", -1);
		from = getIntent().getIntExtra("from", -1);
		planName = getIntent().getStringExtra("planName");

		header_layout = (RelativeLayout) findViewById(R.id.header_layout);
		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);
		headerAdd = (Button) findViewById(R.id.header_add);

		addtionLayout = (LinearLayout) findViewById(R.id.wx_addtionLayout);
		addContainer = (LinearLayout) findViewById(R.id.wx_addContainer);
		resultLayout = (LinearLayout) findViewById(R.id.wx_resultLayout);

		beginMaintenace = (LinearLayout) findViewById(R.id.wx_beginmaintenace);

		initView();
		addtionLayout.setVisibility(View.GONE);
		resultLayout.setVisibility(View.GONE);

		btnBegin = (Button) findViewById(R.id.btnBegin); // 在此view 中
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		// maintenaceState_content.setTextColor(R.color.red);
		maintenaceState_content.setText("未维修");
		btnConfirm.setText("结束维修");
		btnCancel.setText("取消");

		if (from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace) {

			btnCancel.setVisibility(View.GONE);
			btnConfirm.setVisibility(View.VISIBLE);
			btnConfirm.setBackgroundResource(R.drawable.button_blue);
			btnConfirm.setText("效果确认");
			// btnCancel.setVisibility(View.GONE);
			headerTitle.setText("任务确认");
			btnBegin.setText("效果确认");
		} else {
			// holder.itemBtn.setVisibility(View.VISIBLE);

			btnConfirm.setVisibility(View.VISIBLE);
			btnCancel.setBackgroundResource(R.drawable.button_red);
			btnCancel.setVisibility(View.VISIBLE);
			headerLeft.setText("维修");
			headerTitle.setText("");
			btnBegin.setText("开始维修");
		}

		headerLeft.setOnClickListener(this);
		headerAdd.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnBegin.setOnClickListener(this);
		headerAdd.setClickable(false);
        initInspectionInfo();
		queryItemIdList();
		
		
		beginScanCode(this);

	}

	private void initView() {
		codeLayout = (LinearLayout) findViewById(R.id.wx_codeLayout);
		planLayout = (LinearLayout) findViewById(R.id.wx_planLayout);
		maintenaceTask = (LinearLayout) findViewById(R.id.wx_maintenacetaskname);
		equipmentNum = (LinearLayout) findViewById(R.id.wx_equipmentNum);
		equipmentName = (LinearLayout) findViewById(R.id.wx_equipmentName);
		maintenaceState = (LinearLayout) findViewById(R.id.wx_maintenacestate);
		problemDesc = (LinearLayout)findViewById(R.id.wx_problemDesc);
		problemSuggestion = (LinearLayout)findViewById(R.id.wx_problemSuggestion);
		problemMeasure = (LinearLayout)findViewById(R.id.wx_problemMeasure);
		sparepartlayout = (LinearLayout)findViewById(R.id.sparepartlayout);
        imageInfoContainerlayout =(LinearLayout)findViewById(R.id.wxImageInfoContainer);

        sparepartlayout.setVisibility(View.GONE);// 备品备件暂时不可见

		// itemIndex = (LinearLayout)findViewById(R.id.wx_itemIndex);
		// itemName = (LinearLayout)findViewById(R.id.wx_itemName);
		// way = (LinearLayout)findViewById(R.id.wx_way);
		// expendTime = (LinearLayout)findViewById(R.id.wx_expendTime);
		// standard = (LinearLayout)findViewById(R.id.wx_standard);

		code_title = (TextView) codeLayout.findViewById(R.id.title);
		plan_title = (TextView) planLayout.findViewById(R.id.title);
		maintenaceTask_title = (TextView) maintenaceTask
				.findViewById(R.id.title);
		equipmentNum_title = (TextView) equipmentNum.findViewById(R.id.title);
		equipmentName_title = (TextView) equipmentName.findViewById(R.id.title);
		maintenaceState_title = (TextView) maintenaceState
				.findViewById(R.id.title);
		problemDesc_title = (TextView)problemDesc.findViewById(R.id.title);
		problemSuggestion_title=(TextView)problemSuggestion.findViewById(R.id.title);
		problemMeasure_title=(TextView)problemMeasure.findViewById(R.id.title);
		sparepartlayout_title=(TextView)sparepartlayout.findViewById(R.id.title);

		// itemIndex_title = (TextView)itemIndex.findViewById(R.id.title);
		// itemName_title = (TextView)itemName.findViewById(R.id.title);
		// way_title = (TextView)way.findViewById(R.id.title);
		// expendTime_title = (TextView)expendTime.findViewById(R.id.title);
		// standard_title = (TextView)standard.findViewById(R.id.title);

		code_title.setText("设备条码：");
		plan_title.setText("计划：");
		maintenaceTask_title.setText("任务：");
		equipmentNum_title.setText("设备编号：");
		equipmentName_title.setText("设备名称：");
		maintenaceState_title.setText("状态：");
		problemDesc_title.setText("问题描述：");
		problemSuggestion_title.setText("处理意见：");
		problemMeasure_title.setText("处理对策：");
		sparepartlayout_title.setText("备品备件：");
		// itemIndex_title.setText("项目序号：");
		// itemName_title.setText("项目：");
		// way_title.setText("方法：");
		// expendTime_title.setText("维修耗时：");

		// standard_title.setText("判断基准：");
		//problemMeasure_title.
		
		code_content = (TextView) codeLayout.findViewById(R.id.content);
		
		code_content.setText("请扫描维修设备部位条码");
		plan_content = (TextView) planLayout.findViewById(R.id.content);
		maintenaceTask_content = (TextView) maintenaceTask
				.findViewById(R.id.content);
		equipmentNum_content = (TextView) equipmentNum
				.findViewById(R.id.content);
		equipmentName_content = (TextView) equipmentName
				.findViewById(R.id.content);
		maintenaceState_content = (TextView) maintenaceState
				.findViewById(R.id.content);
		
		problemDesc_content= (TextView)problemDesc.findViewById(R.id.content);
		problemSuggestion_content=(TextView)problemSuggestion.findViewById(R.id.content);
		problemMeasure_content=(TextView)problemMeasure.findViewById(R.id.content);
		sparepartlayout_content=(TextView)sparepartlayout.findViewById(R.id.content);;
		
		problemDesc_content.setMovementMethod(ScrollingMovementMethod.getInstance());
		problemSuggestion_content.setMovementMethod(ScrollingMovementMethod.getInstance());
		problemMeasure_content.setMovementMethod(ScrollingMovementMethod.getInstance());
		sparepartlayout_content.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		// itemIndex_content = (TextView)itemIndex.findViewById(R.id.content);
		// itemName_content = (TextView)itemName.findViewById(R.id.content);
		// way_content = (TextView)way.findViewById(R.id.content);
		// expendTime_content = (TextView)expendTime.findViewById(R.id.content);
		// standard_content = (TextView)standard.findViewById(R.id.content);

	}

	public ProgressDialog progressDialog;

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
		System.out.println("TAG : " + TAG + "   " + v.getId());
		switch (v.getId()) {

		case R.id.header_left:
			finishActivity();
			break;
		// inspectionItem = new InspectionTaskEquipmentItem();
		// inspectionItem.result = "NG";
		// Bundle bundle1 = new Bundle();
		// bundle1.putSerializable("inspectionItem", inspectionItem);
		// Intent intent1 = new Intent(DjTaskDetailActivity.this,
		// DjNGExplainActivity.class);
		// intent1.putExtras(bundle1);
		// intent1.putExtra("startTime", startTime);
		// startActivityForResult(intent1, 103);
		case R.id.header_add: // 附加
			/*
			 * if(maintenaceItem == null){ ToastUtil.showShortToast(context,
			 * "请先扫描维修项目"); return; }
			 */
			PopupAddImageUtil.getInstance().showActionWindow(
					WxTaskDetailActivity.this, header_layout,Constants.DJ_POPUP,
					new PopupAddImageUtil.SelItemClickListener(){
						@Override
						public void onCallback(int type) {
							if (type == 0) {
								doTakePhoto();// 用户点击了从照相机获取
							} else if(type == 1){
								doPickPhotoFromGallery();// 从相册中去获取
							} else if(type == 2){
								doSparePart(); //从备品备件中获取数据。
							}
						}
					});
			break;
		case R.id.btnConfirm:

			if (from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace) {
				if (maintenaceItem == null)
					return;

				maintenaceItem.imageNames = imageNames;

				maintenaceItem.id = problemId;
				
				maintenaceItem.equipmentId = equipmentId;
				maintenaceItem.equipmentName = mEquipmentName;

				maintenaceItem.equipmentTDC = equipmentTDC;
				maintenaceItem.taskName = taskName;
				maintenaceItem.equipmentNum = managementOrgNum;
				
				Bundle bundle = new Bundle();
				bundle.putSerializable("maintenaceItem", maintenaceItem);
				bundle.putInt("from", from);

				Intent intent = new Intent(WxTaskDetailActivity.this,
						WxTaskResultUploadActivity.class);
				intent.putExtras(bundle);
				intent.putExtra("startTime", startTime);
				startActivityForResult(intent, 103);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				// finishActivity();
				return;

			} else if (from == WxTaskEquipmentListActivity.From_Equipment_Maintenace) {
				if (maintenaceItem == null)
					return;
				maintenaceItem.imageNames = imageNames;
				maintenaceItem.result = "OK";
				endTime = System.currentTimeMillis();
				maintenaceItem.costTime = (int) ((endTime - startTime) / 1000);

				maintenaceItem.startTime = ClientUtil.getTimeFormat(endTime);
				maintenaceItem.endTime = ClientUtil.getTimeFormat(endTime);

				maintenaceItem.id = problemId;
				maintenaceItem.equipmentId = equipmentId;
				maintenaceItem.equipmentName = mEquipmentName;
				maintenaceItem.equipmentNum = managementOrgNum;

				maintenaceItem.equipmentTDC = equipmentTDC;
				maintenaceItem.taskName = taskName;
				maintenaceItem.sparepartContent = sparepartContent;
				
				Bundle bundle = new Bundle();
				bundle.putSerializable("maintenaceItem", maintenaceItem);
				bundle.putInt("from", from);

				Intent intent = new Intent(WxTaskDetailActivity.this,
						WxTaskResultUploadActivity.class);
				intent.putExtras(bundle);
				intent.putExtra("startTime", startTime);
				startActivityForResult(intent, 203);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				// finishActivity();
			}

			// 下面是不需要的
			/**
			 * CustomDialog.Builder builder = new CustomDialog.Builder(this);
			 * builder.setMessage("确定本项目维修结果为OK？"); // builder.setTitle("提示");
			 * builder.setPositiveButton("确定", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int which) { dialog.dismiss();
			 * //设置ok的操作事项 if(maintenaceItem == null) return;
			 * maintenaceItem.imageNames = imageNames; maintenaceItem.result =
			 * "OK"; endTime = System.currentTimeMillis();
			 * maintenaceItem.costTime = (int)((endTime - startTime) / 1000);
			 * maintenaceItem.endTime = ClientUtil.getTimeFormat(endTime);
			 * maintenaceItem.id=problemId;
			 * if(NetUtils.isNetworkConnected(context)){ //上传维修项目...
			 * UploadMaintenaceItemTask task = new
			 * UploadMaintenaceItemTask(context ,maintenaceItem ,-1, new
			 * UploadMaintenaceItemTask.TaskCallBack(){
			 * 
			 * @Override public void preExecute() { if(!isFinishing()){ try {
			 *           progressDialog = ProgressDialog.show(context, null,
			 *           "上传中...",true,true);
			 *           progressDialog.setCanceledOnTouchOutside(false); }
			 *           catch (Exception e) { e.printStackTrace(); } } }
			 * @Override public void callBackResult(Integer[] result) { if(null
			 *           != progressDialog && progressDialog.isShowing()){ try {
			 *           progressDialog.dismiss(); } catch (Exception e) {
			 *           e.printStackTrace(); } } if(result[0] == 0 || result[0]
			 *           == 8){ Log.d(TAG, "上传返回结果为： " +result[0]); if(result[0]
			 *           == 0){ ToastUtil.showLongToast(context, "提交成功"); }else{
			 *           ToastUtil.showLongToast(context, "重复提交"); }
			 *           if(result[1] > 0){ //本设备还有未完成的项目 resetView(); }else{
			 *           //本设备的所有项目已经维修完成。 finishActivity(); sendBroadcast(new
			 *           Intent(WxTaskEquipmentListActivity.REFRASH_ACTION)); }
			 *           }else{ ToastUtil.showLongToast(context, "提交失败");
			 *           //保存本地下次上传 saveMaintenaceItemTask(); } } });
			 *           task.execute();
			 * 
			 *           }else{ saveMaintenaceItemTask(); } } });
			 * 
			 *           builder.setNegativeButton("取消", new
			 *           android.content.DialogInterface.OnClickListener() {
			 *           public void onClick(DialogInterface dialog, int which)
			 *           { dialog.dismiss(); } });
			 * 
			 *           builder.create().show();
			 */
			break;
		case R.id.btnCancel: // NG
			finishActivity();

			/*
			 * if(from ==
			 * WxTaskEquipmentListActivity.From_Equipment_Maintenace){ // do
			 * something.... }
			 * 
			 * if(maintenaceItem == null) return;
			 * maintenaceItem.imageNames=imageNames; maintenaceItem.result="NG";
			 * maintenaceItem.id=problemId; Bundle bundle = new Bundle();
			 * bundle.putSerializable("maintenaceItem", maintenaceItem);
			 * bundle.putInt("from", from);
			 * 
			 * Intent intent = new Intent(WxTaskDetailActivity.this,
			 * WxNGExplainActivity.class); intent.putExtras(bundle);
			 * intent.putExtra("startTime", startTime);
			 * startActivityForResult(intent, 103);
			 * overridePendingTransition(R.anim.push_left_in,
			 * R.anim.push_left_out);
			 */
			break;
		case R.id.btnBegin:

			if (from == WxTaskEquipmentListActivity.From_Equipment_Maintenace) {
				beginMaintenace.setVisibility(View.GONE);
				resultLayout.setVisibility(View.VISIBLE);
				maintenaceState_content.setText("维修中...");
				maintenaceState_content.setTextColor(R.color.fuchsia);
			} else if (from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace) {
				btnConfirm.performClick();
			}
		}
	}

	protected void doSparePart() {
		new AsyncTask<Void, Void, List<ResEquipmentSparePart1>>() {
			@Override
			protected List<ResEquipmentSparePart1> doInBackground(Void... params) {
				BaseDataDao dao = new BaseDataDao(WxTaskDetailActivity.this);
				List<ResEquipmentSparePart1> list =  dao.queryBaseEquipmentSparPart(equipmentId);
				//dao.closeDB();
				return list;
			}
			
			protected void onPostExecute(final List<ResEquipmentSparePart1> result) {
				if(result == null || result.size() <= 0){
					ToastUtil.showShortToast(WxTaskDetailActivity.this, "该设备无备品备件.");
					return;
				}
				PopupReplaceSparePartUtil.getInstance().showActionWindow(WxTaskDetailActivity.this, result,
						new PopupReplaceSparePartUtil.SelItemClickListener(){
							@Override
							public void onCallback(String curSparepartContent) {
								sparepartContent = curSparepartContent;
								BaseDataDao dao = new BaseDataDao(WxTaskDetailActivity.this);
								String temp = dao.updateSparepartcontent(equipmentId,curSparepartContent);
								sparepartlayout_content.setText(temp);
							}
				});
			};
		}.execute();
		
	}

	private void queryItemIdList() {
		new AsyncTask<Void, Void, Map<Long, Long>>() {

			@Override
			protected Map<Long, Long> doInBackground(Void... params) {
				MaintenaceEquipmentDao dao = new MaintenaceEquipmentDao(
						WxTaskDetailActivity.this);
				Map<Long, Long> itemIdToSid = dao
						.queryMaintenaceItemIdList(maintenaceTaskEquipmentId);
				managementOrgNum=dao.queryEquipmentNumber(equipmentId);
				//managementOrgNum = dao.queryEquipmentOrgNum(equipmentId);
				//managementOrgNum = dao.qu
				//strProblemDesc = dao.queryProblemDesc(problemId);
				curProblem = dao.queryCurProblem(problemId);
				
				dao.closeDB();
				return itemIdToSid;
			}

			protected void onPostExecute(Map<Long, Long> result) {
				super.onPostExecute(result);
				if (result != null) {
					maintenaceItemIdMap = result;
					Set<Long> set = maintenaceItemIdMap.keySet();
					for (Long sid : set) {
						Log.d(TAG, "maintenaceItemIdMap > sid : " + sid);
					}
				} else {
					// ToastUtil.showLongToast(context, "该维修设备下， 无维修项目");
				}
				proHandler.sendEmptyMessage(UPDATEPROBLEM);
			}
		}.execute();
	}

	private void saveMaintenaceItemTask() {
		SaveMaintenaceItemTask task = new SaveMaintenaceItemTask(context,
				maintenaceItem, new SaveMaintenaceItemTask.TaskCallBack() {
					@Override
					public void callBackResult(Integer[] result) {
						if (result[0] > 0) {
							ToastUtil.showShortToast(context, "保存本地成功");
						} else {
							ToastUtil.showShortToast(context, "保存本地失败");
						}
						// test: @20141105 暂时取消项目检查
						/**
						 * if(result[1] > 0){ //本设备还有未完成的项目 resetView(); }else{
						 */
						// 本设备的所有项目已经点检完成。
						finishActivity();
						sendBroadcast(new Intent(
								WxTaskEquipmentListActivity.REFRASH_ACTION));
						// }
					}
				}, from);
		task.execute();
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

	protected void onDestroy() {
		if(HadwareControll.barcodeControll !=null){
			HadwareControll.Close();
			HadwareControll.m_handler = null;
		}
		super.onDestroy();
	}

	private class MainHandler extends Handler {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HadwareControll.BARCODE_READ: {
				if (isScaned) {
					break;
				}
				String result = msg.obj + "";
				// String result = "SC-212-001";
				// test: @20141127
				getScanResult(result);
				break;
			}
			default:
				break;
			}
		}
	};
	
	private Handler proHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATEPROBLEM:
				problemDesc_content.setText(curProblem.problemDesc);
				problemSuggestion_content.setText(curProblem.problemSuggestion);
				problemMeasure_content.setText(curProblem.problemMeasure);
				problemMeasure_title.setText("处理对策："+curProblem.faultSchemeType);
			}
		}
	};

	private MaintenaceTaskEquipmentItem maintenaceItem;

	private void queryData(final String scanCode) {
		new AsyncTask<Void, Void, MaintenaceTaskEquipmentItem>() {

			@Override
			protected MaintenaceTaskEquipmentItem doInBackground(Void... params) {
				BaseDataDao dao = new BaseDataDao(WxTaskDetailActivity.this);
				MaintenaceTaskEquipmentItem item = dao
						.queryMaintenaceBaseEquipmentItem(scanCode);

				dao.closeDB();
				return item;
			}

			@Override
			protected void onPostExecute(MaintenaceTaskEquipmentItem result) {
				super.onPostExecute(result);
				// test: @20141104
				if (result != null || result == null) {
					Set<Long> set = maintenaceItemIdMap.keySet();

					// test: @20141105 暂时取消此设定
					// if(set.contains(result.equipmentSpotcheckId)){ //扫描成功
					isScaned = true;
					maintenaceItem = result;
					startTime = System.currentTimeMillis();
					// @20141120
					maintenaceItem.id = problemId;// maintenaceItemIdMap.get(result.equipmentSpotcheckId);
					maintenaceItem.startTime = ClientUtil
							.getTimeFormat(startTime);
					maintenaceItem.equipmentId = maintenaceTaskEquipmentId;
					maintenaceItem.equipmentNum = managementOrgNum;
					setupViewParams();
					// test: @20141105 暂时取消项目检测功能
					/*
					 * }else{ ToastUtil.showLongToast(context, "您维修的项目，不属于本设备");
					 */// }
				} else {
					// ToastUtil.showLongToast(context, "找不到相应的项目");
				}

			}
		}.execute();
	}

	private void setupViewParams() {
		// 计划-----此处有问题
		// 二维码 扫描匹配后:

		plan_content.setText(planName);
		maintenaceTask_content.setText(taskName + "");
		equipmentNum_content.setText(managementOrgNum);
		equipmentName_content.setText(mEquipmentName + "");
		if (from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace) {
			maintenaceState_content.setText("待确认");
		}

		// itemIndex_content.setText(mItemIndex + "");
		// itemName_content.setText(maintenaceItem.equipmentSpotcheckName ==
		// null ? "" : maintenaceItem.equipmentSpotcheckName);
		// way_content.setText(maintenaceItem.checkMethod == null ? "" :
		// maintenaceItem.checkMethod);
		// endTime = System.currentTimeMillis();
		// userTime = (int)((endTime - startTime) / 1000);
		// inspectionItem.startTime = ClientUtil.getTimeFormat(startTime);
		// inspectionItem.endTime = ClientUtil.getTimeFormat(endTime);
		// inspectionItem.costTime = userTime;
		// expendTime_content.setText(ClientUtil.getUsedTime(userTime));
		// expendTime_content.setText("");
		// standard_content.setText(maintenaceItem.judgementStandard == null ?
		// "" : maintenaceItem.judgementStandard);
		headerAdd.setVisibility(View.VISIBLE);
		headerAdd.setClickable(true);
		addtionLayout.setVisibility(View.VISIBLE);
		beginMaintenace.setVisibility(View.VISIBLE);

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

	// -----------------------添加照片-------------------------
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;

	/**
	 * 调用相机 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
//			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			startActivityForResult(intent, CAMERA_WITH_DATA);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			String output = ClientUtil.getImageDir() + "/" + curTempImage;
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(output)));
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 1);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "调用相机异常！", Toast.LENGTH_LONG).show();
		}
	}

	// 请求Gallery程序
	protected void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "获取相册失败", Toast.LENGTH_LONG).show();
		}
	}

	// 封装请求Gallery的intent
	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 400);
		intent.putExtra("return-data", true);
		return intent;
	}

	// 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
				if (resultCode != RESULT_OK){
					return;}
				switch (requestCode) {
					case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
						
						if (resultCode == RESULT_OK) {
							Bitmap photo = data.getParcelableExtra("data");
							if (photo != null) {
								// 下面就是显示照片了
								// 缓存用户选择的图片
								String imageName = getPhotoFileName();
								ClientUtil.saveImage(photo, imageName);
								Bitmap bb = ThumbnailUtils
										.extractThumbnail(photo, 120, 120);
								createImageView(bb, imageName);
								imageNames += TextUtils.isEmpty(imageNames) ? imageName
										: "," + imageName;
							}
						}
						break;
					}
					case CAMERA_WITH_DATA: // 照相机程序返回的
						if(resultCode == RESULT_OK){
						//Bitmap bm = (Bitmap) data.getExtras().get("data");
							
								// 照片缩略图
//								Bitmap bb = ThumbnailUtils.extractThumbnail(bm, 120, 120);
//								// 想图像显示在ImageView视图上
							//ClientUtil.saveImage(bm, imageName);
							if(imageNames !=null && imageNames.contains(",")){
								String [] contais=imageNames.split(",");
								if(contais.length == 9){
									ToastUtil.showLongToast(context, "已到图片上传上限数");
									return;
								}
							}
								String imageName = getPhotoFileName();
								String orgTempImage = ClientUtil.getImageDir() + "/"
										+ curTempImage;
								
								String fullImagePath = ClientUtil.getImageDir() + "/"+ imageName;
								Bitmap bm = ClientUtil.getimage(orgTempImage);
								BufferedOutputStream fos;
								try {
									fos = new BufferedOutputStream(new FileOutputStream(
											new File(fullImagePath)));
									bm.compress(CompressFormat.JPEG, 70, fos);
									fos.flush();
									fos.close();
									createImageView(bm, imageName);
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								imageNames += TextUtils.isEmpty(imageNames) ? imageName : ","
										+ imageName;
						}
						break;
					case SCANNIN_GREQUEST_CODE:
						if(resultCode == RESULT_OK){
							Bundle bundle = data.getExtras();
							scanningCode=bundle.getString("result");
							
							getScanResult(scanningCode);
						}
						break;
				}
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		long maintenaceItemId = 0;
		if (maintenaceItem != null) {
			// test: 添加此处测试
			if (maintenaceItem.id != null) {
				maintenaceItemId = maintenaceItem.id;
			} else {
				maintenaceItemId = System.currentTimeMillis();
			}
		}
		return maintenaceItemId + "_" + dateFormat.format(date) + ".jpg";
	}

	private void createImageView(Bitmap bt, String fileNam) {
		float scale = this.getResources().getDisplayMetrics().density;
		int mWidthHeight = (int) (80 * scale + 0.5f);
		int marginRigth = (int) (10 * scale + 0.5f);
		Log.d("ZMHDCasualTakeActivity", "mWidthHeight : " + mWidthHeight);

		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				mWidthHeight, mWidthHeight);
		mLayoutParams.setMargins(0, 0, marginRigth, 0);

		ImageView mImageView = new ImageView(this);
		mImageView.setLayoutParams(mLayoutParams);
		mImageView.setImageBitmap(bt);
		mImageView.setScaleType(ScaleType.FIT_XY);
		mImageView.setTag(fileNam);
		// mImageView.setOnClickListener(new onClickPhotosListener());
		// mImageView.setOnLongClickListener(new LongClickPhotosListener());

		int index = addContainer.getChildCount();
		if (index > 1) {
			addContainer.addView(mImageView, (index - 1));
		} else {
			addContainer.addView(mImageView, 0);
		}
	}

	private void resetView() {
		mItemIndex++;
		isScaned = false;
		maintenaceItem = null;
		imageNames = "";

		code_content.setText("请扫描维修部位条码");
		plan_content.setText("");
		equipmentNum_content.setText("");
		equipmentName_content.setText("");
		// itemIndex_content.setText("");
		// itemName_content.setText("");
		// way_content.setText("");
		// expendTime_content.setText("");
		// standard_content.setText("");

		headerAdd.setClickable(false);
		addtionLayout.setVisibility(View.GONE);
		resultLayout.setVisibility(View.GONE);
		addContainer.removeAllViews();
	}

    private void initInspectionInfo() {
        new AsyncTask<Void, Void, String []>() {

            @Override
            protected  String[] doInBackground(Void... params) {
                InspectionEquipmentDao dao = new InspectionEquipmentDao(
                        WxTaskDetailActivity.this);

//                List<String> pictureIds= dao.queryInspectionItemPiecture(spotcheckId);
//                List<String> list = new ArrayList<String>();
                  String [] list = null;
                  String pictureId = dao.queryMaintenacePic(problemId);
                  if(String.valueOf(pictureId) !="null" && pictureId !=""){
                      list=pictureId.split(",");
                  }
                return list;
            }

            @Override
            protected void onPostExecute(String[] args) {
                super.onPostExecute(args);

                if(args == null) return;
                for(String picId : args){

                    String curSource= FileUtil.getMaintenacePicDir() + File.separator + picId +".jpg";

                    Bitmap bm = BitmapFactory.decodeFile(curSource);
                    if( bm == null ) continue;
                    Bitmap bb = ThumbnailUtils
                            .extractThumbnail(bm, 120, 120);
                    createInspectionInfoImageView(bb, curSource);
                }
            }
        }.execute();
    }

    private void createInspectionInfoImageView(Bitmap bt, String fileName) {
        float scale = this.getResources().getDisplayMetrics().density;
        int mWidthHeight = (int) (80 * scale + 0.5f);
        int marginRigth = (int) (10 * scale + 0.5f);
        Log.d("ZMHDCasualTakeActivity", "mWidthHeight : " + mWidthHeight);

        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                mWidthHeight, mWidthHeight);
        mLayoutParams.setMargins(0, 0, marginRigth, 0);

        ImageView mImageView = new ImageView(this);
        mImageView.setLayoutParams(mLayoutParams);
        mImageView.setImageBitmap(bt);
        mImageView.setScaleType(ScaleType.FIT_XY);
        mImageView.setTag(fileName);
        
        mImageView.setOnClickListener(inspectionItemInfoClick);
		
        photoTotal.append(fileName);
		photoTotal.append(",");

        int index = imageInfoContainerlayout.getChildCount();
        if (index > 1) {
            imageInfoContainerlayout.addView(mImageView, (index - 1));
        } else {
            imageInfoContainerlayout.addView(mImageView, 0);
        }
    }

    private OnClickListener inspectionItemInfoClick = new OnClickListener(){
        @Override
        public void onClick(View v) {
            //if(v.)
            if(v !=null){
                if(v.getParent().equals(imageInfoContainerlayout)){
                    String source=v.getTag().toString();
                    Log.d("inspection image click ", source);
                    Intent intent = new Intent(WxTaskDetailActivity.this, SpaceImageDetailActivity.class);
                    intent.putExtra("source",source);
                    intent.putExtra("sourceTotal", photoTotal.toString());

                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    intent.putExtra("locationX", location[0]);
                    intent.putExtra("locationY", location[1]);

                    intent.putExtra("width", v.getWidth());
                    intent.putExtra("height", v.getHeight());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        }
    };
    

    
    public void getScanResult(String result){
    	if (!result.equals(equipmentTDC)) {
			ToastUtil.showLongToast(context, "你扫描的设备，不是本次所需维修设备");
			return;
		}
		result = equipmentTDC;
		code_content.setText(result);
		queryData(result);
		// test: 暂时取消声音 @20141108
		if(!MyApp.IsIData){
			play_sound();
		}
    }
}
