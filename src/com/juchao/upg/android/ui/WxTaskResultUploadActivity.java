package com.juchao.upg.android.ui;

import java.util.List;

import com.juchao.upg.android.R;
import com.juchao.upg.android.db.BaseDataDao;
import com.juchao.upg.android.entity.BaseCommonProblem;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.task.SaveMaintenaceItemTask;
import com.juchao.upg.android.task.UploadMaintenaceItemTask;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.NetUtils;
import com.juchao.upg.android.util.ToastUtil;
import com.juchao.upg.android.view.PopupComonProblemUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class WxTaskResultUploadActivity extends BaseActivity implements
		OnClickListener {

	private TextView headerLeft, headerTitle;
	private Button btnConfirm, btnCancel;

	private EditText etProblemDescription;
	//private LinearLayout addContainer;
	private MaintenaceTaskEquipmentItem maintenaceItem;
	private String[] mImageNames;
	public ProgressDialog progressDialog;
	private Context context;
	private int from;
	private static final String TAG = WxNGExplainActivity.class.getSimpleName();
	
	private LinearLayout codeLayout ,planLayout , equipmentNum , equipmentName ,itemIndex , itemName ,way ,expendTime ,standard,maintenaceState ;
   	private TextView code_title, plan_title,equipmentNum_title ,equipmentName_title,itemIndex_title,itemName_title,way_title,expendTime_title,standard_title,maintenaceState_title;
   	private TextView code_content ,plan_content,equipmentNum_content , equipmentName_content , itemIndex_content ,itemName_content ,way_content , expendTime_content , standard_content,maintenaceState_content;
   	private LinearLayout maintenaceTask;
   	private TextView maintenaceTask_title;
   	private TextView maintenaceTask_content;
	
	private long startTime, endTime;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wx_ng_explain_act);
		context = this;
		maintenaceItem = (MaintenaceTaskEquipmentItem) getIntent()
				.getSerializableExtra("maintenaceItem");
		startTime = getIntent().getLongExtra("startTime", 0L);
		from = getIntent().getIntExtra("from", -1);
		if (maintenaceItem == null) {
			return;
		}
		if (!TextUtils.isEmpty(maintenaceItem.imageNames)) {
			mImageNames = maintenaceItem.imageNames.split(",");
		}

		initView();
		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);
		headerLeft.setText("维修");

		if (from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace) {
			headerTitle.setText("效果确认");
		} else if(from == WxTaskEquipmentListActivity.From_Equipment_Maintenace) {
			headerTitle.setText("");
		}

	/*	addContainer = (LinearLayout) findViewById(R.id.wx_addContainer);
		addContainer.removeAllViews();
		if (mImageNames != null && mImageNames.length > 0) {
			for (int i = 0; i < mImageNames.length; i++) {
				Bitmap bt = ClientUtil
						.getThumbnailImage(mImageNames[i], 80, 80);
				createImageView(bt, mImageNames[i]);
			}
		}*/

		etProblemDescription = (EditText) findViewById(R.id.wx_etProblemDescription);

		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnConfirm.setText("确认");
		btnCancel.setText("取消");
		
		headerLeft.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

	}
	
	private void initView(){
		codeLayout  = (LinearLayout)findViewById(R.id.wx_codeLayout);
		planLayout = (LinearLayout)findViewById(R.id.wx_planLayout);
		maintenaceTask=(LinearLayout)findViewById(R.id.wx_maintenacetaskname);
		equipmentNum = (LinearLayout)findViewById(R.id.wx_equipmentNum);
		equipmentName = (LinearLayout)findViewById(R.id.wx_equipmentName);
		maintenaceState= (LinearLayout)findViewById(R.id.wx_maintenacestate);
		
		code_title = (TextView)codeLayout.findViewById(R.id.title);
		plan_title = (TextView)planLayout.findViewById(R.id.title);
		maintenaceTask_title=(TextView)maintenaceTask.findViewById(R.id.title);
		equipmentNum_title = (TextView)equipmentNum.findViewById(R.id.title);
		equipmentName_title = (TextView)equipmentName.findViewById(R.id.title);
		maintenaceState_title=(TextView)maintenaceState.findViewById(R.id.title);
		
		code_content = (TextView)codeLayout.findViewById(R.id.content);
		plan_content= (TextView)planLayout.findViewById(R.id.content);
		maintenaceTask_content=(TextView)maintenaceTask.findViewById(R.id.content);
		equipmentNum_content = (TextView)equipmentNum.findViewById(R.id.content);
		equipmentName_content = (TextView)equipmentName.findViewById(R.id.content);
		maintenaceState_content=(TextView)maintenaceState.findViewById(R.id.content);
		
//		itemIndex_title = (TextView)itemIndex.findViewById(R.id.title);
//		itemName_title = (TextView)itemName.findViewById(R.id.title);
//		way_title = (TextView)way.findViewById(R.id.title);
//		expendTime_title = (TextView)expendTime.findViewById(R.id.title);
//		standard_title = (TextView)standard.findViewById(R.id.title);
		
		code_title.setText("设备条码：");
		plan_title.setText("计划：");
		maintenaceTask_title.setText("任务：");
		equipmentNum_title.setText("设备编号：");
		equipmentName_title.setText("设备名称：");
		maintenaceState_title.setText("状态: ");
		
		
		code_content.setText(maintenaceItem.equipmentTDC +"");
		maintenaceTask_content.setText(maintenaceItem.taskName + "");
		equipmentNum_content.setText(maintenaceItem.equipmentNum + "");
		equipmentName_content.setText(maintenaceItem.equipmentName + "");
		if(from == WxTaskEquipmentListActivity.From_Equipment_Maintenace){
			maintenaceState_content.setText("已维修");
		}else if(from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace){
			maintenaceState_content.setText("已确认");
		}
	}

	private void createImageView(Bitmap bt, String fileName) {
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
		// mImageView.setOnClickListener(new onClickPhotosListener());
		// mImageView.setOnLongClickListener(new LongClickPhotosListener());

		/*int index = addContainer.getChildCount();
		if (index > 1) {
			addContainer.addView(mImageView, (index - 1));
		} else {
			addContainer.addView(mImageView, 0);
		}*/

	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.header_left: // 返回
			finishActivity();
			break;
		case R.id.commonProblemsBtn: // 常见问题

			new AsyncTask<Void, Void, List<BaseCommonProblem>>() {
				@Override
				protected List<BaseCommonProblem> doInBackground(Void... params) {
					BaseDataDao dao = new BaseDataDao(
							WxTaskResultUploadActivity.this);
					List<BaseCommonProblem> list = dao
							.queryBaseCommonProblemList();
					dao.closeDB();
					return list;
				}

				protected void onPostExecute(
						final List<BaseCommonProblem> result) {
					if (result == null || result.size() <= 0) {
						ToastUtil.showShortToast(
								WxTaskResultUploadActivity.this,
								"找不到常见问题，您可以尝试更新基础数据。");
						return;
					}
					PopupComonProblemUtil.getInstance().showActionWindow(
							WxTaskResultUploadActivity.this, result,
							new PopupComonProblemUtil.SelItemClickListener() {
								@Override
								public void onCallback(String commonProble) {
									etProblemDescription.setText(commonProble);
									etProblemDescription
											.setSelection(etProblemDescription
													.getText().length());
								}
							});
				};

			}.execute();

			break;
			
		case R.id.btnConfirm: // OK
			maintenaceItem.faultDescribe = etProblemDescription.getText()
					.toString() + "";
			endTime = System.currentTimeMillis();
			maintenaceItem.costTime = (int) ((endTime - startTime) / 1000);
			maintenaceItem.endTime = ClientUtil.getTimeFormat(endTime);

			if (NetUtils.isNetworkConnected(context)) {
				// 上传维修项目...
				UploadMaintenaceItemTask task = new UploadMaintenaceItemTask(
						context, maintenaceItem, from,
						new UploadMaintenaceItemTask.TaskCallBack() {
							@Override
							public void preExecute() {
								if (!isFinishing()) {
									try {
										progressDialog = ProgressDialog.show(
												context, null, "加载中...", true,
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
										
										finishActivity();
									} else {
										ToastUtil
												.showLongToast(context, "重复提交");
									}
									if (result[1] > 0) { // 本设备还有未完成的项目
										Intent intent = new Intent();
										intent.putExtra("flag", 1);
										setResult(RESULT_OK, intent);
										finishActivity();
									} else {
										// 本设备的所有项目已经维修完成。
										Intent intent = new Intent();
										intent.putExtra("flag", 2);
										setResult(RESULT_OK, intent);
										finishActivity();
									}
								} else {
									saveMaintenaceItemTask(from);
									ToastUtil.showLongToast(context, "提交失败");
								}
							}
						});
				task.execute();

			} else {
				saveMaintenaceItemTask(from);
			}
			finishActivity();
			WxTaskDetailActivity.instance.finish();
			WxEquipmentProblListActivity.instance.finish();
			sendBroadcast(new Intent(WxTaskEquipmentListActivity.REFRASH_ACTION));
			break;
		case R.id.btnCancel: // 取消
			finishActivity();
			break;
		}

	}

	private void saveMaintenaceItemTask(int from) {
		SaveMaintenaceItemTask task = new SaveMaintenaceItemTask(context,
				maintenaceItem, new SaveMaintenaceItemTask.TaskCallBack(){
					@Override
					public void callBackResult(Integer[] result) {
						if (result[0] > 0) {
							ToastUtil.showShortToast(context, "保存成功");
						}
						if (result[1] > 0) { // 本设备还有未维修的项目
							Intent intent = new Intent();
							intent.putExtra("flag", 1);
							setResult(RESULT_OK, intent);
							//finishActivity();
						} else {
							Intent intent = new Intent();
							intent.putExtra("flag", 2);
							setResult(RESULT_OK, intent);
							// 本设备所有问题已维修完成
							//finishActivity();
						}
					}
				},from);
		task.execute();
	}

}
