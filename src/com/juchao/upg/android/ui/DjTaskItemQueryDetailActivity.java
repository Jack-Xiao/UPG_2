package com.juchao.upg.android.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
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

import com.juchao.upg.android.R;
import com.juchao.upg.android.db.BaseDataDao;
import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.task.SaveInspectionItemTask;
import com.juchao.upg.android.task.UploadInspectionItemTask;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.FileUtil;
import com.juchao.upg.android.util.NetUtils;
import com.juchao.upg.android.util.ToastUtil;
import com.juchao.upg.android.view.CustomDialog;
import com.juchao.upg.android.view.PopupAddImageUtil;

public class DjTaskItemQueryDetailActivity extends BaseActivity implements
		OnClickListener {

	protected static final String TAG = DjTaskItemQueryDetailActivity.class
			.getSimpleName();

	protected MediaPlayer mediaPlayer = null;

	private RelativeLayout header_layout;
	private TextView headerLeft, headerTitle;
	private LinearLayout addContainer, addtionLayout, resultLayout;
	private Button  btnConfirm, btnCancel;

	private Context context;
	private LinearLayout codeLayout, planLayout, equipmentNum, equipmentName,
			itemIndex, itemName, way, expendTime,costPlanTime, standard,equNum;
	private TextView code_title, plan_title, equipmentNum_title,
			equipmentName_title, itemIndex_title, itemName_title, way_title,standard_title,costPlanTime_title,equNum_title;
	private TextView code_content, plan_content, equipmentNum_content,
			equipmentName_content, itemIndex_content, itemName_content,
			way_content, standard_content,costPlanTime_content,equNum_content;

	private long startTime;
	private long endTime;
	
	// 需要传入的
	private long inspectionTaskEquipmentId;
	private long equipmentId;
	private String managementOrgNum = ""; // 设备编号，需根据equipmentId查询 --- 设备条码
	private String mEquipmentName; // (待补)
	private String taskName; // (待补)
	private int mItemIndex = 1; // 项目序号
	// 图片名称
	private String imageNames = ""; // 多个图片，名称以"," 分隔
	private boolean isScaned;

	private long inspectionTaskId;
	private String curTempImage = "temp.png";
	private int style;
	private String scanCode;
	private InspectionTaskEquipmentItem introduction;
	private long inspectionItemId;
	private long spotcheckId;
	private String equNumber; //设备条码
	private StringBuilder photoPathTotal;

	private Map<Long, Long> inspectionItemIdMap = new HashMap<Long, Long>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dj_task_detail_query_act);
		context = this;
		photoPathTotal = new StringBuilder();
		startTime = System.currentTimeMillis();
		introduction=(InspectionTaskEquipmentItem)getIntent().getExtras().getSerializable("inspectionItem");
		inspectionItemId=introduction.id;
		spotcheckId=introduction.equipmentSpotcheckId;
//		inspectionTaskId = getIntent().getLongExtra("inspectionTaskId", 0L);
//		inspectionTaskEquipmentId = getIntent().getLongExtra(
//				"inspectionTaskEquipmentId", 0);
//		equipmentId = getIntent().getLongExtra("equipmentId", 0);
//		mEquipmentName = getIntent().getStringExtra("mEquipmentName");
//		taskName = getIntent().getStringExtra("taskName");
//		style=getIntent().getIntExtra("style", 0);
//		scanCode=getIntent().getStringExtra("scanCode");
		
		inspectionTaskId = getIntent().getLongExtra("inspectionTaskId", 0L);
		
		inspectionTaskEquipmentId = introduction.inspectionTaskEquipmentId;
		
		equipmentId=getIntent().getLongExtra("equipmentId", 0L);
		mEquipmentName = getIntent().getStringExtra("mEquipmentName");
		taskName = getIntent().getStringExtra("taskName");
		style=introduction.style;
		scanCode=getIntent().getStringExtra("scanCode");
		//inspectionItemId = getIntent().getLongExtra("inspectionItemId",0L);

		header_layout = (RelativeLayout) findViewById(R.id.header_layout);
		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);		

		initView();
		addtionLayout = (LinearLayout) findViewById(R.id.addtionLayout1);
		
		addContainer = (LinearLayout) findViewById(R.id.addContainer);
		resultLayout = (LinearLayout) findViewById(R.id.resultLayout);
		//addtionLayout.setVisibility(View.GONE); 
		resultLayout.setVisibility(View.GONE);

		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnConfirm.setText("OK");
		btnCancel.setText("NG");
		
		
		headerLeft.setText("点检列表");
		headerTitle.setText("点检任务详细");

		headerLeft.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		initInspectionInfo();
		
		queryItemIdList();
		
		//queryData(scanCode);
		queryData1();
		
	}

	private void queryData1() {
		new AsyncTask<Void, Void, InspectionTaskEquipmentItem>() {

			@Override
			protected InspectionTaskEquipmentItem doInBackground(Void... params) {
				BaseDataDao dao = new BaseDataDao(DjTaskItemQueryDetailActivity.this);
				InspectionTaskEquipmentItem item = dao
						.queryBaseEquipmentItem(spotcheckId);

				dao.closeDB();
				return item;
			}

			@Override
			protected void onPostExecute(InspectionTaskEquipmentItem result) {
				super.onPostExecute(result);
				if (result != null) {
					Set<Long> set = inspectionItemIdMap.keySet();
					Log.d(TAG, "queryData > equipmentSpotcheckId ："
							+ result.equipmentSpotcheckId);
//					if (set.contains(result.equipmentSpotcheckId)) { // 扫描成功
//						isScaned = true;
//						inspectionItem = result;
//						startTime = System.currentTimeMillis();
//						inspectionItem.id = inspectionItemIdMap
//								.get(result.equipmentSpotcheckId);
//						inspectionItem.startTime = ClientUtil
//								.getTimeFormat(startTime);
//						inspectionItem.inspectionTaskEquipmentId = inspectionTaskEquipmentId;
//						setupViewParams();
//					} else {
//						ToastUtil.showLongToast(context, "您点检的项目，不属于本设备");
//					}
					isScaned = true;
					inspectionItem = result;
					startTime = System.currentTimeMillis();
					inspectionItem.id = inspectionItemIdMap
							.get(result.equipmentSpotcheckId);
					inspectionItem.startTime = ClientUtil
							.getTimeFormat(startTime);
					inspectionItem.inspectionTaskEquipmentId = inspectionTaskEquipmentId;
					setupViewParams();
					
				} else {
					ToastUtil.showLongToast(context, "找不到相应的项目");
				}
			}
		}.execute();
		
	}

	private void initView() {
		codeLayout = (LinearLayout) findViewById(R.id.codeLayout);
		planLayout = (LinearLayout) findViewById(R.id.planLayout);
		equipmentNum = (LinearLayout) findViewById(R.id.equipmentNum);
		equipmentName = (LinearLayout) findViewById(R.id.equipmentName);
		itemIndex = (LinearLayout) findViewById(R.id.itemIndex);
		itemName = (LinearLayout) findViewById(R.id.itemName);
		way = (LinearLayout) findViewById(R.id.way);
		expendTime = (LinearLayout) findViewById(R.id.expendTime);
		standard = (LinearLayout) findViewById(R.id.standard);
		costPlanTime=(LinearLayout)findViewById(R.id.costPlanTime);
		equNum = (LinearLayout)findViewById(R.id.equNum);

		code_title = (TextView) codeLayout.findViewById(R.id.title);
		plan_title = (TextView) planLayout.findViewById(R.id.title);
		equipmentNum_title = (TextView) equipmentNum.findViewById(R.id.title);
		equipmentName_title = (TextView) equipmentName.findViewById(R.id.title);
		itemIndex_title = (TextView) itemIndex.findViewById(R.id.title);
		itemName_title = (TextView) itemName.findViewById(R.id.title);
		way_title = (TextView) way.findViewById(R.id.title);
 		standard_title = (TextView) standard.findViewById(R.id.title);
 		costPlanTime_title=(TextView)costPlanTime.findViewById(R.id.title);
 		equNum_title=(TextView)equNum.findViewById(R.id.title);
 		
		code_title.setText("条码：");
		plan_title.setText("计划：");
		equipmentNum_title.setText("设备条码：");
		equipmentName_title.setText("设备名称：");
		itemIndex_title.setText("项目序号：");
		itemName_title.setText("项目：");
		way_title.setText("方法：");
 		standard_title.setText("判断基准：");
 		costPlanTime_title.setText("预计耗时：");
 		equNum_title.setText("设备编号：");

		code_content = (TextView) codeLayout.findViewById(R.id.content);
		//code_content.setText("请扫描点检部位条码");
		code_content.setText(scanCode);
		plan_content = (TextView) planLayout.findViewById(R.id.content);
		equipmentNum_content = (TextView) equipmentNum
				.findViewById(R.id.content);
		equipmentName_content = (TextView) equipmentName
				.findViewById(R.id.content);
		itemIndex_content = (TextView) itemIndex.findViewById(R.id.content);
		itemName_content = (TextView) itemName.findViewById(R.id.content);
		way_content = (TextView) way.findViewById(R.id.content);
 		standard_content = (TextView) standard.findViewById(R.id.content);
 		costPlanTime_content=(TextView) costPlanTime.findViewById(R.id.content);
 		equNum_content = (TextView)equNum.findViewById(R.id.content);
	}

	public ProgressDialog progressDialog;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.header_left: // 返回
			finishActivity();
			break;
		}
	}

	private InspectionTaskEquipmentItem inspectionItem;

	private void queryData(final String scanCode) {
		new AsyncTask<Void, Void, InspectionTaskEquipmentItem>() {

			@Override
			protected InspectionTaskEquipmentItem doInBackground(Void... params) {
				BaseDataDao dao = new BaseDataDao(DjTaskItemQueryDetailActivity.this);
				InspectionTaskEquipmentItem item = dao
						.queryBaseEquipmentItem(scanCode);

				dao.closeDB();
				return item;
			}

			@Override
			protected void onPostExecute(InspectionTaskEquipmentItem result) {
				super.onPostExecute(result);
				if (result != null) {
					Set<Long> set = inspectionItemIdMap.keySet();
					Log.d(TAG, "queryData > equipmentSpotcheckId ："
							+ result.equipmentSpotcheckId);
//					if (set.contains(result.equipmentSpotcheckId)) { // 扫描成功
//						isScaned = true;
//						inspectionItem = result;
//						startTime = System.currentTimeMillis();
//						inspectionItem.id = inspectionItemIdMap
//								.get(result.equipmentSpotcheckId);
//						inspectionItem.startTime = ClientUtil
//								.getTimeFormat(startTime);
//						inspectionItem.inspectionTaskEquipmentId = inspectionTaskEquipmentId;
//						setupViewParams();
//					} else {
//						ToastUtil.showLongToast(context, "您点检的项目，不属于本设备");
//					}
					isScaned = true;
					inspectionItem = result;
					startTime = System.currentTimeMillis();
					inspectionItem.id = inspectionItemIdMap
							.get(result.equipmentSpotcheckId);
					inspectionItem.startTime = ClientUtil
							.getTimeFormat(startTime);
					inspectionItem.inspectionTaskEquipmentId = inspectionTaskEquipmentId;
					setupViewParams();
					
				} else {
					ToastUtil.showLongToast(context, "找不到相应的项目");
				}
			}
		}.execute();
	}

	private void queryItemIdList() {
		new AsyncTask<Void, Void, Map<Long, Long>>() {

			@Override
			protected Map<Long, Long> doInBackground(Void... params) {
				InspectionEquipmentDao dao = new InspectionEquipmentDao(
						DjTaskItemQueryDetailActivity.this);
				Map<Long, Long> itemIdToSid = dao
						.queryInspectionItemIdList(inspectionTaskEquipmentId,style);
				managementOrgNum = dao.queryEquipmentOrgNum(equipmentId);
				equNumber = dao.queryEquipmentNumber(equipmentId);
				dao.closeDB();
				return itemIdToSid;
			}

			@Override
			protected void onPostExecute(Map<Long, Long> result) {
				super.onPostExecute(result);
				if (result != null) {
					inspectionItemIdMap = result;
					Set<Long> set = inspectionItemIdMap.keySet();
					for (Long sid : set) {
						Log.d(TAG, "inspectionItemIdMap > sid : " + sid);
					}
				} else {
					ToastUtil.showLongToast(context, "该点检设备下，无点检项目");
				}
			}
		}.execute();
	}

	// 1410680700617
	private void setupViewParams() {
		plan_content.setText(taskName + "");
		equipmentNum_content.setText(managementOrgNum);
		equNum_content.setText(equNumber+"");
		equipmentName_content.setText(mEquipmentName + "");
		itemIndex_content.setText(mItemIndex + "");
		itemName_content
				.setText(inspectionItem.equipmentSpotcheckName == null ? ""
						: inspectionItem.equipmentSpotcheckName);
		way_content.setText(inspectionItem.checkMethod == null ? ""
				: inspectionItem.checkMethod);

 		standard_content.setText(inspectionItem.judgementStandard == null ? ""
				: inspectionItem.judgementStandard);

 		costPlanTime_content.setText(inspectionItem.costPlanTime +"");
//		headerAdd.setClickable(true);
//		addtionLayout.setVisibility(View.VISIBLE);
//		resultLayout.setVisibility(View.VISIBLE);
	}

	protected void onDestroy() {
		super.onDestroy();
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
		// mImageView.setOnClickListener(new onClickPhotosListener());
		// mImageView.setOnLongClickListener(new LongClickPhotosListener());
		
		photoPathTotal.append(fileName);
		photoPathTotal.append(",");
		
		Log.d("photoTotal", photoPathTotal.toString());
		addContainer.addView(mImageView);
		
//		int index = addContainer.getChildCount();
//		if (index > 1) {
//			addContainer.addView(mImageView, (index - 1));
//		} else {
//			addContainer.addView(mImageView, 0);
//		}
	}
	
	private OnClickListener inspectionItemInfoClick = new OnClickListener(){
		@Override
		public void onClick(View v) {
			//if(v.)
			if(v !=null){
				if(v.getParent().equals(addContainer)){
					String source=v.getTag().toString();
					Log.d("inspection image click ", source);
					Intent intent = new Intent(DjTaskItemQueryDetailActivity.this, SpaceImageDetailActivity.class);
					intent.putExtra("source",source);
					//intent.putExtra("images", (ArrayList<String>) datas);
					//intent.putExtra("position", position);
					int[] location = new int[2];
					v.getLocationOnScreen(location);
					intent.putExtra("locationX", location[0]);
					intent.putExtra("locationY", location[1]);
					
					intent.putExtra("sourceTotal", photoPathTotal.toString());
					intent.putExtra("width", v.getWidth());
					intent.putExtra("height", v.getHeight());
					startActivity(intent);
					overridePendingTransition(0, 0);
					
				}
			}
		}
	};
	
	private void initInspectionInfo() {
		new AsyncTask<Void, Void, List<String>>() {

			@Override
			protected  List<String> doInBackground(Void... params) {
				InspectionEquipmentDao dao = new InspectionEquipmentDao(
						DjTaskItemQueryDetailActivity.this);
//				Map<Long, Long> itemIdToSid = dao
//						.queryInspectionItemIdList(inspectionTaskEquipmentId,style);
				//String pictureIds=dao.queryInspectionItemPiecture(spotcheckId);
				//managementOrgNum = dao.queryEquipmentOrgNum(equipmentId);
				List<String> pictureIds=dao.queryInspectionItemPiecture(spotcheckId);
				//String[] pictures = pictureIds.split(",");
				List<String> list = new ArrayList<String>();
				//String regex="(.*/).*?.(jpg|png|bmp|gif|JPG|PNG|BMP|GIF)";
				//Pattern pattern=Pattern.compile(regex);
				
				for(int i =0;i<pictureIds.size(); i++){
					//String curPath=dao.queryPictureInfo(pictureIds.get(i));
					
//					Matcher matcher=pattern.matcher(curPath);
//					while(matcher.find()){
//						curPath = curPath.replace(matcher.group(1).toString(), "");
//					}
					
					list.add(pictureIds.get(i));
				}
				dao.closeDB();
				return list;
			}

			@Override
			protected void onPostExecute(List<String> args) {
				super.onPostExecute(args);
//				if (result != null) {
//					inspectionItemIdMap = result;
//					Set<Long> set = inspectionItemIdMap.keySet();
//					for (Long sid : set) {
//						Log.d(TAG, "inspectionItemIdMap > sid : " + sid);
//					}
//				} else {
//					ToastUtil.showLongToast(context, "该点检设备下，无点检项目");
//				}
				for(int i =0;i <args.size();i++){
					
					String curSource=FileUtil.getBaseImageDir() + File.separator + args.get(i)  +".jpg";;
					
					Bitmap bm = BitmapFactory.decodeFile(curSource);
					//String imageName = getPhotoFileName();

					// String tempImageName="t_" + imageName;

					//ClientUtil.saveImage(photo, imageName);
					// ClientUtil.saveImage1(photo, tempImageName);

					Bitmap bb = ThumbnailUtils
							.extractThumbnail(bm, 120, 120);
					createInspectionInfoImageView(bb, curSource);
				}
			}
		}.execute();
		
	}
}
