package com.juchao.upg.android.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.ResInspectionTask;
import com.juchao.upg.android.task.UpdateDataTask;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.ToastUtil;

public class UpdateDataActivity extends BaseActivity implements OnClickListener {

    
    private TextView headerLeft , headerTitle ;
    private Button btnConfirm,btnCancel;
    
    private ProgressBar progressBar;
    private TextView text1 , text2 ,progressNum;
    public ProgressDialog progressDialog;
    private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.update_data_act);
       
		context = this;
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		
		headerLeft.setText("配置");
		headerTitle.setText("更新本地数据");
		
		btnConfirm = (Button)findViewById(R.id.btnConfirm);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		
//		progressBar = (ProgressBar)findViewById(R.id.progressBar);
//		progressNum = (TextView)findViewById(R.id.progressNum);
		text1 = (TextView)findViewById(R.id.text1);
//		text2 = (TextView)findViewById(R.id.text2);
		
		headerLeft.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
	}

	private UpdateDataTask task ;
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.header_left:  //返回
				finishActivity();
				break;
			case R.id.btnConfirm:  //确认
				btnConfirm.setClickable(false);
				
				task = new UpdateDataTask(UpdateDataActivity.this, new UpdateDataTask.TaskCallBack(){

					@Override
					public void preUpdate() {
						if(!isFinishing()){
							try {
								progressDialog = ProgressDialog.show(context, null, "开始下载设备基础数据中...",true,true);
								progressDialog.setCanceledOnTouchOutside(false);
								progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
									@Override
									public void onCancel(DialogInterface dialog) {
										task.cancel(true);
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					@Override
					public void callBackResult(int code ,String resultMsg) {
						if(isFinishing()){
							return;
						}
						if(null != progressDialog && progressDialog.isShowing()){
							try {
								progressDialog.dismiss();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						if(code == 0){
							DefaultShared.putLong(Constants.KEY_LAST_UPDATE_DATA_TIME, System.currentTimeMillis());
							finishActivity();
						}else{
							btnConfirm.setClickable(true);
						}
						ToastUtil.showShortToast(context, resultMsg);
					}
					
					@Override
					public void progressUpdate(String value){
						progressDialog.setMessage(value);
					}
					
				});
				
				task.execute();
			
				break;
			case R.id.btnCancel:  //取消
				
				finishActivity();
				
				break;
		}
		
	}
	
	
}
