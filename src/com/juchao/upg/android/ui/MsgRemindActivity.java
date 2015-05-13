package com.juchao.upg.android.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.ToastUtil;

public class MsgRemindActivity extends BaseActivity implements OnClickListener {

    
    private TextView headerLeft , headerTitle ,secondTitle;
    private Button btnConfirm,btnCancel;
    private EditText intervalEt;
    private CheckBox cbOnlyOne , cbInterval ;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.msg_remind_act);
       
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		secondTitle = (TextView)findViewById(R.id.second_title);
		
		headerLeft.setText("配置");
		headerTitle.setText("消息提醒");
		secondTitle.setText("新消息提醒方式：");
		
		
		btnConfirm = (Button)findViewById(R.id.btnConfirm);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		
		boolean onlyRemindOnce = DefaultShared.getBoolean(Constants.KEY_ONLY_REMIND_ONCE, false);
		cbOnlyOne = (CheckBox)findViewById(R.id.cbOnlyOne);
		cbOnlyOne.setChecked(onlyRemindOnce);
		
		
		cbInterval = (CheckBox)findViewById(R.id.cbInterval);
		intervalEt = (EditText)findViewById(R.id.etInterval);
		long remindInterval = DefaultShared.getLong(Constants.KEY_REMIND_INTERVAL, 0);
		if(remindInterval >= 60 * 1000){
			cbInterval.setChecked(true);
			intervalEt.setText((remindInterval / (60 * 1000) ) + "");
		}


		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		headerLeft.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.header_left:  //返回
				finishActivity();
				break;
			case R.id.btnConfirm:  //确认
				
				
				String interval = intervalEt.getText().toString().trim();
				if(cbInterval.isChecked() && TextUtils.isEmpty(interval)){
					ToastUtil.showShortToast(MsgRemindActivity.this, "请填写提醒的时间间隔");
					return ;
				}
				
				
				if(cbInterval.isChecked()){
					try{
						if(interval.equals("001")){
							DefaultShared.putBoolean(Constants.BEGIN_LOG, true);
						}
						int intervalInt = Integer.parseInt(interval);
						long intervalMs =  intervalInt * 60 * 1000; //转为毫秒
						DefaultShared.putLong(Constants.KEY_REMIND_INTERVAL, intervalMs);
					}catch(Exception e){
						ToastUtil.showShortToast(MsgRemindActivity.this, "您输入时间间隔的格式有误");
						break;
					}
					
				}else{
					DefaultShared.putLong(Constants.KEY_REMIND_INTERVAL, 0);
				}
				
				if(cbOnlyOne.isChecked()){
					DefaultShared.putBoolean(Constants.BEGIN_LOG, false);
					
					DefaultShared.putBoolean(Constants.KEY_ONLY_REMIND_ONCE, true);
				}else{
					DefaultShared.putBoolean(Constants.KEY_ONLY_REMIND_ONCE, false);
				}
				
				ToastUtil.showShortToast(MsgRemindActivity.this, "设置成功");
				finishActivity();
				break;
			case R.id.btnCancel:  //取消
				
				finishActivity();
				
				break;
		}
	}
}
