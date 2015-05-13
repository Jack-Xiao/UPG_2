package com.juchao.upg.android.ui;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.db.BaseDataDao;
import com.juchao.upg.android.entity.BaseCommonProblem;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.task.SaveMaintenaceItemTask;
import com.juchao.upg.android.task.UploadInspectionItemTask;
import com.juchao.upg.android.task.UploadMaintenaceItemTask;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.NetUtils;
import com.juchao.upg.android.util.ToastUtil;
import com.juchao.upg.android.view.PopupComonProblemUtil;

/**
 * NG 说明
 * 
 * @author xiao-jiang
 * 
 */
public class WxNGExplainActivity extends BaseActivity implements
		OnClickListener {

	private TextView headerLeft, headerTitle;
	private Button btnConfirm, btnCancel;

	private EditText etProblemDescription;
	private LinearLayout addContainer;
	private MaintenaceTaskEquipmentItem maintenaceItem;
	private String[] mImageNames;
	public ProgressDialog progressDialog;
	private Context context;
	private int from;
	private static final String TAG = WxNGExplainActivity.class.getSimpleName();

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

		headerLeft = (TextView) findViewById(R.id.header_left);
		headerTitle = (TextView) findViewById(R.id.header_title);
		
		if (from == WxTaskEquipmentListActivity.From_EffectConfirm_Maintenace) {
			headerTitle.setText("效果确认");
			headerTitle.setText("");
		} else if (from == WxTaskEquipmentListActivity.From_Equipment_Maintenace) {
			headerLeft.setText("");
			headerTitle.setText("");
		}

		addContainer = (LinearLayout) findViewById(R.id.wx_addContainer);
		addContainer.removeAllViews();
		if (mImageNames != null && mImageNames.length > 0) {
			for (int i = 0; i < mImageNames.length; i++) {
				Bitmap bt = ClientUtil
						.getThumbnailImage(mImageNames[i], 80, 80);
				createImageView(bt, mImageNames[i]);
			}
		}

		etProblemDescription = (EditText) findViewById(R.id.wx_etProblemDescription);

		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnConfirm.setText("确认");
		btnCancel.setText("取消");

		
		headerLeft.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		etProblemDescription.addTextChangedListener(watcher);
	}
	
	private TextWatcher watcher = new TextWatcher(){
		int num = Constants.TEXT_COUNT_LIMIT;
		private int selectionStart;
		private int selectionEnd;
		private CharSequence temp;
		  
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			temp = s;
		}

		@Override
		public void afterTextChanged(Editable s) {
			selectionStart = etProblemDescription.getSelectionStart();
			selectionEnd = etProblemDescription.getSelectionEnd();
			if(temp.length() > num){
			    s.delete(selectionStart - 1, selectionEnd);
                int tempSelection = selectionStart;
                etProblemDescription.setText(s);
                etProblemDescription.setSelection(tempSelection);//设置光标在最后
			}
		}
	};
	
	

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

		int index = addContainer.getChildCount();
		if (index > 1) {
			addContainer.addView(mImageView, (index - 1));
		} else {
			addContainer.addView(mImageView, 0);
		}

	}

	@Override
	public void onClick(View v) {
		System.out.println(TAG + "TAG  current click ID:" + v.getId());
		System.out.println(TAG + ": commonProblemsBtn: "
				+ R.id.commonProblemsBtn);
		switch (v.getId()) {
		case R.id.header_left: // 返回
			finishActivity();
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
									saveMaintenaceItemTask();
									ToastUtil.showLongToast(context, "提交失败");
								}
							}
						});
				task.execute();

			} else {
				saveMaintenaceItemTask();
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

	private void saveMaintenaceItemTask() {
		SaveMaintenaceItemTask task = new SaveMaintenaceItemTask(context,
				maintenaceItem, new SaveMaintenaceItemTask.TaskCallBack() {
					@Override
					public void callBackResult(Integer[] result) {
						if (result[0] > 0) {
							ToastUtil.showShortToast(context, "保存成功");
						}
						if (result[1] > 0) { // 本设备还有未完成的项目
							Intent intent = new Intent();
							intent.putExtra("flag", 1);
							setResult(RESULT_OK, intent);
							// finishActivity();
						} else {
							Intent intent = new Intent();
							intent.putExtra("flag", 2);
							setResult(RESULT_OK, intent);
							// 本设备的所有项目已经点检完成。
							// finishActivity();
						}
					}
				}, from);
		task.execute();
		// sendBroadcast(new Intent(DjTaskListActivity.REFRASH_ACTION));
	}
}
