package com.juchao.upg.android.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.FileUtil;
import com.juchao.upg.android.util.NetUtils;
import com.juchao.upg.android.util.ToastUtil;
import com.juchao.upg.android.view.CustomDialog;
import com.juchao.upg.android.view.PopupAddImageUtil;

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

public class DjTaskDetailActivity extends BaseActivity implements
		OnClickListener {

	protected static final String TAG = DjTaskDetailActivity.class
			.getSimpleName();
	//public Handler mHandler = new MainHandler();
	protected MediaPlayer mediaPlayer = null;

	private RelativeLayout header_layout;
	private TextView headerLeft, headerTitle;
	private LinearLayout addContainer, addtionLayout, resultLayout,inspectionItemInfoLayout;
	private Button headerAdd, btnConfirm, btnCancel;

	private Context context;
	private LinearLayout codeLayout, planLayout, equipmentNum, equipmentName,
			itemIndex, itemName, way, expendTime, standard,costPlanTime,equNum;
	private TextView code_title, plan_title, equipmentNum_title,
			equipmentName_title, itemIndex_title, itemName_title, way_title,
			expendTime_title, standard_title,costPlanTime_title,equNum_title;
	private TextView code_content, plan_content, equipmentNum_content,
			equipmentName_content, itemIndex_content, itemName_content,
			way_content, expendTime_content, standard_content,costPlanTime_content,equNum_content;

	private long startTime;
	private long endTime;

	// 需要传入的
	private long inspectionTaskEquipmentId;
	private long equipmentId;
	private String managementOrgNum = ""; // 设备编号，需根据equipmentId查询
	private String mEquipmentName; // (待补)
	private String taskName; // (待补)
	private int mItemIndex = 1; // 项目序号
	// 图片名称
	private String imageNames = ""; // 多个图片，名称以"," 分隔
	private boolean isScaned;

	private long inspectionTaskId;
	private String curTempImage = "temp.png";
	private int style;

	private Map<Long, Long> inspectionItemIdMap = new HashMap<Long, Long>();
	private String scanCode;
	private long inspectionItemId;
	private long spotcheckId;
	protected String equNumber; //设备编号
	private StringBuilder photoPathTotal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dj_task_detail_act);
		context = this;
//		HadwareControll.Open();  取消此设定
//		HadwareControll.m_handler = mHandler;
		photoPathTotal = new StringBuilder();
		startTime = System.currentTimeMillis();
		inspectionTaskId = getIntent().getLongExtra("inspectionTaskId", 0L);
		inspectionTaskEquipmentId = getIntent().getLongExtra(
				"inspectionTaskEquipmentId", 0);
		equipmentId = getIntent().getLongExtra("equipmentId", 0);
		mEquipmentName = getIntent().getStringExtra("mEquipmentName");
		taskName = getIntent().getStringExtra("taskName");
		style=getIntent().getIntExtra("style", 0);
		scanCode=getIntent().getStringExtra("scanCode");
		inspectionItemId = getIntent().getLongExtra("inspectionItemId",0L);
		spotcheckId=getIntent().getLongExtra("spotcheckId", 0L);

		header_layout = (RelativeLayout) findViewById(R.id.header_layout);
		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);
		headerAdd = (Button) findViewById(R.id.header_add);

		initView();
		addtionLayout = (LinearLayout) findViewById(R.id.addtionLayout);
		addContainer = (LinearLayout) findViewById(R.id.addContainer);
		inspectionItemInfoLayout=(LinearLayout) findViewById(R.id.imageInfoContainer);
		
		resultLayout = (LinearLayout) findViewById(R.id.resultLayout);
		
		addtionLayout.setVisibility(View.GONE);
		resultLayout.setVisibility(View.GONE);

		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnConfirm.setText("OK");
		btnCancel.setText("NG");

		headerLeft.setText("点检列表");
		headerTitle.setText("点检任务详细");

		headerLeft.setOnClickListener(this);
		headerAdd.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		headerAdd.setClickable(false);
		
		inspectionItemInfoLayout.setOnClickListener(inspectionItemInfoClick);
		initInspectionInfo();
		//queryItemIdList();
		queryItemIdList1();
	
		queryData();
	}

	private void queryItemIdList1() {
		 
			InspectionEquipmentDao dao = new InspectionEquipmentDao(
					DjTaskDetailActivity.this);
			Map<Long, Long> itemIdToSid = dao
					.queryInspectionItemIdList(inspectionTaskEquipmentId,style);
			managementOrgNum = dao.queryEquipmentOrgNum(equipmentId);
			//dao.closeDB();
			inspectionItemIdMap=itemIdToSid;
	}

	private void initInspectionInfo() {
		new AsyncTask<Void, Void, List<String>>() {

			@Override
			protected  List<String> doInBackground(Void... params) {
				InspectionEquipmentDao dao = new InspectionEquipmentDao(
						DjTaskDetailActivity.this);
//				Map<Long, Long> itemIdToSid = dao
//						.queryInspectionItemIdList(inspectionTaskEquipmentId,style);
				//String pictureIds=dao.queryInspectionItemPiecture(spotcheckId);
				List<String> pictureIds= dao.queryInspectionItemPiecture(spotcheckId);
				//managementOrgNum = dao.queryEquipmentOrgNum(equipmentId);
				//String[] pictures = pictureIds.split(",");

				List<String> list = new ArrayList<String>();
				//String regex="(.*/).*?.(jpg|png|bmp|gif|PNG|JPG|BMP|GIF)";
				//Pattern pattern=Pattern.compile(regex);

				for(int i =0;i<pictureIds.size(); i++){
//					String curPath=dao.queryPictureInfo(pictureIds.get(i));
//
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
				com.juchao.upg.android.util.Log.fileLog("","load pictures");
				for(int i =0;i <args.size();i++){

					String curSource=FileUtil.getBaseImageDir() + File.separator + args.get(i) +".jpg";

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
		costPlanTime=(LinearLayout) findViewById(R.id.costPlanTime);
		equNum = (LinearLayout) findViewById(R.id.equNum);
		
		code_title = (TextView) codeLayout.findViewById(R.id.title);
		plan_title = (TextView) planLayout.findViewById(R.id.title);
		equipmentNum_title = (TextView) equipmentNum.findViewById(R.id.title);
		equipmentName_title = (TextView) equipmentName.findViewById(R.id.title);
		itemIndex_title = (TextView) itemIndex.findViewById(R.id.title);
		itemName_title = (TextView) itemName.findViewById(R.id.title);
		way_title = (TextView) way.findViewById(R.id.title);
		expendTime_title = (TextView) expendTime.findViewById(R.id.title);
		standard_title = (TextView) standard.findViewById(R.id.title);
		costPlanTime_title=(TextView)costPlanTime.findViewById(R.id.title);
		equNum_title= (TextView) equNum.findViewById(R.id.title);

		code_title.setText("条码：");
		plan_title.setText("计划：");
		equipmentNum_title.setText("设备条码：");
		equipmentName_title.setText("设备名称：");
		itemIndex_title.setText("项目序号：");
		itemName_title.setText("项目：");
		way_title.setText("方法：");
		expendTime_title.setText("点检耗时：");
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
		expendTime_content = (TextView) expendTime.findViewById(R.id.content);
		standard_content = (TextView) standard.findViewById(R.id.content);
		costPlanTime_content=(TextView)costPlanTime.findViewById(R.id.content);
		equNum_content = (TextView)equNum.findViewById(R.id.content);
	}

	public ProgressDialog progressDialog;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.header_left: // 返回
			finishActivity();
			break;

		case R.id.header_add: // 附加
			// inspectionItem = new InspectionTaskEquipmentItem();
			// inspectionItem.result = "NG";
			// Bundle bundle1 = new Bundle();
			// bundle1.putSerializable("inspectionItem", inspectionItem);
			// Intent intent1 = new Intent(DjTaskDetailActivity.this,
			// DjNGExplainActivity.class);
			// intent1.putExtras(bundle1);
			// intent1.putExtra("startTime", startTime);
			// startActivityForResult(intent1, 103);

			if (inspectionItem == null) {
				ToastUtil.showShortToast(context, "请先扫描点检项目");
				return;
			}
			PopupAddImageUtil.getInstance().showActionWindow(
					DjTaskDetailActivity.this, header_layout,Constants.DJ_POPUP,
					new PopupAddImageUtil.SelItemClickListener() {
						@Override
						public void onCallback(int type) {
							if (type == 0) {
								doTakePhoto();// 用户点击了从照相机获取
							} else {
								doPickPhotoFromGallery();// 从相册中去获取
							}
						}
					});
			break;
		case R.id.btnConfirm: // OK
			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setMessage("确定本项目点检结果为OK？");
			// builder.setTitle("提示");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 设置ok的操作事项
							if (inspectionItem == null)
								return;
							inspectionItem.style=style;
							inspectionItem.imageNames = imageNames;
							inspectionItem.result = "OK";
							endTime = System.currentTimeMillis();
							inspectionItem.costTime = (int) ((endTime - startTime) / 1000);
							inspectionItem.endTime = ClientUtil
									.getTimeFormat(endTime);
							if (NetUtils.isNetworkConnected(context)) {
								// 上传点检项目...
								UploadInspectionItemTask task = new UploadInspectionItemTask(
										inspectionTaskId,
										context,
										inspectionItem,
										new UploadInspectionItemTask.TaskCallBack() {
											@Override
											public void preExecute() {
												if (!isFinishing()) {
													try {
														progressDialog = ProgressDialog
																.show(context,
																		null,
																		"上传中...",
																		true,
																		true);
														progressDialog
																.setCanceledOnTouchOutside(false);
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
											}

											@Override
											public void callBackResult(
													Integer[] result) {
												if (null != progressDialog
														&& progressDialog
																.isShowing()) {
													try {
														progressDialog
																.dismiss();
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
												if (result[0] == 0
														|| result[0] == 8) {
													if (result[0] == 0) {
														ToastUtil
																.showLongToast(
																		context,
																		"提交成功");
													} else {
														ToastUtil
																.showLongToast(
																		context,
																		"重复提交");
													}
													if (result[1] > 0) { // 本设备还有未完成的项目
														resetView();
													} else {
														// 本设备的所有项目已经点检完成。
														DjTaskItemListOperationActivity.instance.finishActivity();
														
														finishActivity();
														sendBroadcast(new Intent(
																DjTaskListActivity.REFRASH_ACTION));
													}
												}else if(result[0] == 9){
													ToastUtil
													.showLongToast(
															context,
															"点检分类与设备状态不符");
													finishActivity();
												}
												else {
													ToastUtil.showLongToast(
															context, "提交失败");
													// 保存本地下次上传
													saveInspectionItemTask(inspectionTaskId);
												}
											}
										});
								task.execute();

							} else {
								saveInspectionItemTask(inspectionTaskId);
							}
						}
					});

			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();
			break;
		case R.id.btnCancel: // NG

			if (inspectionItem == null)
				return;
			inspectionItem.imageNames = imageNames;
			inspectionItem.result = "NG";
			Bundle bundle = new Bundle();
			bundle.putSerializable("inspectionItem", inspectionItem);
			Intent intent = new Intent(DjTaskDetailActivity.this,
					DjNGExplainActivity.class);
			intent.putExtras(bundle);
			intent.putExtra("startTime", startTime);
			intent.putExtra("inspectionTaskId", inspectionTaskId);
			intent.putExtra("style", style);
			startActivityForResult(intent, 103);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		}
	}

	private void saveInspectionItemTask(long inspectionTaskId) {
		SaveInspectionItemTask task = new SaveInspectionItemTask(
				inspectionTaskId, context, inspectionItem,
				new SaveInspectionItemTask.TaskCallBack() {
					@Override
					public void callBackResult(Integer[] result) {
						if (result[0] > 0) {
							ToastUtil.showShortToast(context, "保存本地成功");
						} else {
							ToastUtil.showShortToast(context, "保存本地失败");
						}
						if (result[1] > 0) { // 本设备还有未完成的项目
							resetView();
						} else {
							// 本设备的所有项目已经点检完成。
							finishActivity();
							DjTaskItemListOperationActivity.instance.finishActivity();
							sendBroadcast(new Intent(
									DjTaskListActivity.REFRASH_ACTION));
						}
					}
				});
		task.execute();
	}

	private void resetView() {
		mItemIndex++;
		isScaned = false;
		inspectionItem = null;
		imageNames = "";

		code_content.setText("请扫描点检部位条码");
		plan_content.setText("");
		equipmentNum_content.setText("");
		equNum_content.setText("");
		equipmentName_content.setText("");
		itemIndex_content.setText("");
		itemName_content.setText("");
		way_content.setText("");
		expendTime_content.setText("");
		standard_content.setText("");

		headerAdd.setClickable(false);
		addtionLayout.setVisibility(View.GONE);
		resultLayout.setVisibility(View.GONE);
		addContainer.removeAllViews();
		sendBroadcast(new Intent(DjTaskItemListOperationActivity.REFRESH_ACTION));
		finishActivity();
	}

//	private class MainHandler extends Handler {
//
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case HadwareControll.BARCODE_READ: {
//				if (isScaned) {
//					break;
//				}
//				String result = msg.obj + "";
//				// String result = "SC-212-001";
//				code_content.setText(result);
//				queryData(result);
//				play_sound();
//				break;
//			}
//			default:
//				break;
//			}
//		}
//	};

	private InspectionTaskEquipmentItem inspectionItem;
	

	private void queryData() {
		new AsyncTask<Void, Void, InspectionTaskEquipmentItem>() {

			@Override
			protected InspectionTaskEquipmentItem doInBackground(Void... params) {
				
//				InspectionEquipmentDao dao = new InspectionEquipmentDao(
//						DjTaskDetailActivity.this);
//				Map<Long, Long> itemIdToSid = dao
//						.queryInspectionItemIdList(inspectionTaskEquipmentId,style);
//				managementOrgNum = dao.queryEquipmentOrgNum(equipmentId);
//
//				inspectionItemIdMap = itemIdToSid;
//				
//				Set<Long> set = inspectionItemIdMap.keySet();
//				for (Long sid : set) {
//					Log.d(TAG, "inspectionItemIdMap > sid : " + sid);
//				}
//				dao.closeDB();

				BaseDataDao dao1 = new BaseDataDao(DjTaskDetailActivity.this);
				InspectionTaskEquipmentItem item = dao1
						.queryBaseEquipmentItem(spotcheckId);

				dao1.closeDB();
				return item;
			}

			@Override
			protected void onPostExecute(InspectionTaskEquipmentItem result) {
				super.onPostExecute(result);
				if (result != null) {
//					Set<Long> set = inspectionItemIdMap.keySet();
//					Log.d(TAG, "queryData > equipmentSpotcheckId ："
//							+ result.equipmentSpotcheckId);
					//if (set.contains(result.equipmentSpotcheckId)) { // 扫描成功
						isScaned = true;
						inspectionItem = result;
						startTime = System.currentTimeMillis();
						inspectionItem.id = inspectionItemIdMap
								.get(result.equipmentSpotcheckId);
						inspectionItem.startTime = ClientUtil
								.getTimeFormat(startTime);
						inspectionItem.inspectionTaskEquipmentId = inspectionTaskEquipmentId;
						setupViewParams();
//					} else {
//						ToastUtil.showLongToast(context, "您点检的项目，不属于本设备");
//					}
//				} else {
//					ToastUtil.showLongToast(context, "找不到相应的项目");
				}
			}
		}.execute();
	}

	private void queryItemIdList() {
		new AsyncTask<Void, Void, Map<Long, Long>>() {

			@Override
			protected Map<Long, Long> doInBackground(Void... params) {
				InspectionEquipmentDao dao = new InspectionEquipmentDao(
						DjTaskDetailActivity.this);
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
		// endTime = System.currentTimeMillis();
		// userTime = (int)((endTime - startTime) / 1000);
		// inspectionItem.startTime = ClientUtil.getTimeFormat(startTime);
		// inspectionItem.endTime = ClientUtil.getTimeFormat(endTime);
		// inspectionItem.costTime = userTime;
		// expendTime_content.setText(ClientUtil.getUsedTime(userTime));
		expendTime_content.setText("");
		costPlanTime_content.setText(inspectionItem.costPlanTime+"");
		
		standard_content.setText(inspectionItem.judgementStandard == null ? ""
				: inspectionItem.judgementStandard);
		
		headerAdd.setClickable(true);
		addtionLayout.setVisibility(View.VISIBLE);
		resultLayout.setVisibility(View.VISIBLE);
	}

//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10
//				|| keyCode == KeyEvent.KEYCODE_F11) {
//			HadwareControll.scan_start();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10
//				|| keyCode == KeyEvent.KEYCODE_F11) {
//			HadwareControll.scan_stop();
//			return true;
//		}
//		return super.onKeyUp(keyCode, event);
//	}

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

//	protected void onDestroy() {
//		HadwareControll.Close();
//		HadwareControll.m_handler = null;
//		super.onDestroy();
//	}

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
	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
			if (resultCode == RESULT_OK) {
				Bitmap photo = data.getParcelableExtra("data");
				if (photo != null) {
					// 下面就是显示照片了
					// 缓存用户选择的图片

					String imageName = getPhotoFileName();

					// String tempImageName="t_" + imageName;

					ClientUtil.saveImage(photo, imageName);
					// ClientUtil.saveImage1(photo, tempImageName);

					Bitmap bb = ThumbnailUtils
							.extractThumbnail(photo, 120, 120);
					createImageView(bb, imageName);

					imageNames += TextUtils.isEmpty(imageNames) ? imageName
							: "," + imageName;
				}
			}
			break;
		}
		case CAMERA_WITH_DATA: {// 照相机程序返回的
			// Bitmap bm = (Bitmap) data.getExtras().get("data");
			if (resultCode == RESULT_OK) {
				if (imageNames != null && imageNames.contains(",")) {
					String[] contais = imageNames.split(",");
					if (contais.length == 9) {
						ToastUtil.showLongToast(context, "已到图片上传上限数");
						return;
					}
				}
				// 照片缩略图
				// Bitmap bb = ThumbnailUtils.extractThumbnail(bm, 120, 120);
				// // 想图像显示在ImageView视图上
				// String imageName = getPhotoFileName();
				//
				// ClientUtil.saveImage(bm, imageName);
				//
				// //String tempImageName="t_" + imageName;
				// //ClientUtil.saveImage1(bm, tempImageName);
				//
				// createImageView(bb,imageName);
				// String fileUri=data.getExtras()(MediaStore.EXTRA_OUTPUT);
				// int imageViewWidth=
				// Uri u=
				// long size=bm.getByteCount();
				//
				// long hight=bm.getHeight();
				// long width=bm.getWidth();
				//
				//
				// Bitmap bv=ClientUtil.compressImage(bm);
				//
				// String image1=getPhotoFileName();
				// String org="o_" + image1;
				// ClientUtil.saveImage(bv, org);
				//
				// Bitmap b1=ThumbnailUtils.extractThumbnail(bm, 320,480);
				// image1 = "t_" + image1;
				// ClientUtil.saveImage(bm, image1);

				// createImageView(b1,image1);
				String orgTempImage = ClientUtil.getImageDir() + "/"
						+ curTempImage;
				String imageName = getPhotoFileName();
				String fullImagePath = ClientUtil.getImageDir() + "/"
						+ imageName;

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
		}

		case 103:
			if (data != null) {
				int flag = data.getIntExtra("flag", 0);
				sendBroadcast(new Intent(DjTaskItemListOperationActivity.REFRESH_ACTION));
				if (flag == 1) {
					resetView();
				} else if (flag == 2){
					finishActivity();
					sendBroadcast(new Intent(DjTaskListActivity.REFRASH_ACTION));
					DjTaskItemListOperationActivity.instance.finishActivity();
				}
				break;
			}
		}
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		long inspectionItemId = 0;
		if (inspectionItem != null) {
			inspectionItemId = inspectionItem.id;
		}
		return inspectionItemId + "_" + dateFormat.format(date) + ".jpg";
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
		int index = inspectionItemInfoLayout.getChildCount();
		if (index > 1) {
			inspectionItemInfoLayout.addView(mImageView, (index - 1));
		} else {
			inspectionItemInfoLayout.addView(mImageView, 0);
		}
	}
	
	private OnClickListener inspectionItemInfoClick = new OnClickListener(){
		@Override
		public void onClick(View v) {
			//if(v.)
			if(v !=null){
				if(v.getParent().equals(inspectionItemInfoLayout)){
					String source=v.getTag().toString();
					Log.d("inspection image click ", source);
					Intent intent = new Intent(DjTaskDetailActivity.this, SpaceImageDetailActivity.class);
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
}