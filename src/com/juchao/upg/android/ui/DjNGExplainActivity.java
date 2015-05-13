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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.juchao.upg.android.R;
import com.juchao.upg.android.db.BaseDataDao;
import com.juchao.upg.android.entity.BaseCommonProblem;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.task.SaveInspectionItemTask;
import com.juchao.upg.android.task.UploadInspectionItemTask;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.NetUtils;
import com.juchao.upg.android.util.ToastUtil;
import com.juchao.upg.android.view.PopupComonProblemUtil;

/**
 * NG说明
 * @author xuxd
 *
 */
public class DjNGExplainActivity extends BaseActivity implements OnClickListener {

    private TextView headerLeft , headerTitle;
    private Button btnConfirm ,btnCancel;
    
    private EditText etProblemDescription;
    private Button commonProblemsBtn;
    private LinearLayout addContainer;
    private InspectionTaskEquipmentItem inspectionItem;
    private String[] mImageNames; 
    public ProgressDialog progressDialog;
    private Context context;
    private int style;
    
    private long inspectionTaskId;
    private long startTime ,endTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.dj_ng_explain_act);
		context = this;
		inspectionItem = (InspectionTaskEquipmentItem)getIntent().getSerializableExtra("inspectionItem");
		inspectionTaskId=getIntent().getLongExtra("inspectionTaskId", inspectionTaskId);
		startTime = getIntent().getLongExtra("startTime", 0L);
		style=getIntent().getIntExtra("style", 0);
		
		if(inspectionItem == null){
			return;
		}
		if(!TextUtils.isEmpty(inspectionItem.imageNames)){
			mImageNames = inspectionItem.imageNames.split(",");
		}
		
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		headerLeft.setText("任务详情");
		headerTitle.setText("NG说明");
		
		addContainer  = (LinearLayout)findViewById(R.id.addContainer);
		addContainer.removeAllViews();
		if(mImageNames != null && mImageNames.length > 0){
			for(int i = 0 ; i < mImageNames.length ; i++){
				Bitmap bt = ClientUtil.getThumbnailImage(mImageNames[i] , 80 ,80);
				createImageView(bt , mImageNames[i]);
			}
		}
		
		etProblemDescription = (EditText)findViewById(R.id.etProblemDescription);
		commonProblemsBtn = (Button)findViewById(R.id.commonProblemsBtn);
		
		btnConfirm = (Button)findViewById(R.id.btnConfirm);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		btnConfirm.setText("确认");
		btnCancel.setText("取消");
		commonProblemsBtn.setOnClickListener(this);
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

	private void createImageView(Bitmap bt,String fileName){
		float scale = this.getResources().getDisplayMetrics().density; 
		int mWidthHeight = (int)(80 * scale + 0.5f); 
		int marginRigth = (int)(10 * scale + 0.5f); 
		Log.d("ZMHDCasualTakeActivity", "mWidthHeight : "+mWidthHeight);
		
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(mWidthHeight, mWidthHeight);
		mLayoutParams.setMargins(0, 0, marginRigth, 0); 
		
		ImageView mImageView = new ImageView(this);
		mImageView.setLayoutParams(mLayoutParams);
		mImageView.setImageBitmap(bt);
		mImageView.setScaleType(ScaleType.FIT_XY);
		mImageView.setTag(fileName);
//		mImageView.setOnClickListener(new onClickPhotosListener());
//		mImageView.setOnLongClickListener(new LongClickPhotosListener());
		
		int index = addContainer.getChildCount();
		if(index > 1){
			addContainer.addView(mImageView,(index - 1));
		}else{
			addContainer.addView(mImageView,0);
		}
	}
	
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.header_left:  //返回
				finishActivity();
				break;
			case R.id.commonProblemsBtn : //常见问题
				
				new AsyncTask<Void, Void, List<BaseCommonProblem>>() {
					@Override
					protected List<BaseCommonProblem> doInBackground(Void... params) {
						BaseDataDao dao = new BaseDataDao(DjNGExplainActivity.this);
						List<BaseCommonProblem> list =  dao.queryBaseCommonProblemList();
						//dao.closeDB();
						return list;
					}
					
					protected void onPostExecute(final List<BaseCommonProblem> result) {
						if(result == null || result.size() <= 0){
							ToastUtil.showShortToast(DjNGExplainActivity.this, "找不到常见问题，您可以尝试更新基础数据。");
							return;
						}
						PopupComonProblemUtil.getInstance().showActionWindow(DjNGExplainActivity.this, result,
								new PopupComonProblemUtil.SelItemClickListener(){
									@Override
									public void onCallback(String commonProble) {
										etProblemDescription.setText(commonProble);
										etProblemDescription.setSelection(etProblemDescription.getText().length());
									}
						});
					};
					
				}.execute();
				
				
				break;
			case R.id.btnConfirm : //OK
				inspectionItem.faultDescribe = etProblemDescription.getText().toString() + "";
				endTime = System.currentTimeMillis();
                inspectionItem.costTime = (int)((endTime - startTime) / 1000);
                inspectionItem.endTime = ClientUtil.getTimeFormat(endTime);
                inspectionItem.style=style;
                
				 if(NetUtils.isNetworkConnected(context)){
	                	//上传点检项目...
	                	UploadInspectionItemTask task = new UploadInspectionItemTask(inspectionTaskId,context ,inspectionItem  ,
	                			new UploadInspectionItemTask.TaskCallBack(){
									@Override
									public void preExecute() {
										if(!isFinishing()){
											try {
												progressDialog = ProgressDialog.show(context, null, "上传中...",true,true);
												progressDialog.setCanceledOnTouchOutside(false);
												com.juchao.upg.android.util.Log.fileLog("", "NGresult  begin upload ......  ");
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
									@Override
									public void callBackResult(Integer[] result) {
										if(null != progressDialog && progressDialog.isShowing()){
											try {
												progressDialog.dismiss();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
										if(result[0] == 0 || result[0] == 8){
											if(result[0] == 0){
												ToastUtil.showLongToast(context, "提交成功");
											}else{
												ToastUtil.showLongToast(context, "重复提交");
											}
											if(result[1] > 0){  //本设备还有未完成的项目
												Intent intent = new Intent();
												intent.putExtra("flag", 1);
												setResult(RESULT_OK, intent);
												finishActivity();
											}else{
												//本设备的所有项目已经点检完成。
												Intent intent = new Intent();
												intent.putExtra("flag", 2);
												setResult(RESULT_OK, intent);
												finishActivity();
											}
										}else if(result[0] ==9){
											ToastUtil.showLongToast(context, "点检分类与设备状态不符");
											finishActivity();
										}
										else{
											saveInspectionItemTask();
											ToastUtil.showLongToast(context, "提交失败");
										}
									}
	                	});
	                	task.execute();
	                	
	                }else{
	                	saveInspectionItemTask();
	                }
				break;
			case R.id.btnCancel : //取消
				finishActivity();
				break;
		}
		
	}
	
	private void saveInspectionItemTask(){
		SaveInspectionItemTask task = new SaveInspectionItemTask(inspectionTaskId,context ,inspectionItem ,
    			new SaveInspectionItemTask.TaskCallBack() {
					@Override
					public void callBackResult(Integer[] result) {
						if(result[0] > 0){
							ToastUtil.showShortToast(context, "保存成功");
						}
						if(result[1] > 0){  //本设备还有未完成的项目
							Intent intent = new Intent();
							intent.putExtra("flag", 1);
							setResult(RESULT_OK, intent);
							finishActivity();
						}else{
							Intent intent = new Intent();
							intent.putExtra("flag", 2);
							setResult(RESULT_OK, intent);
							//本设备的所有项目已经点检完成。
							finishActivity();
						}
					}
				});
    	task.execute();
	}
}
