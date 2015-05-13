package com.juchao.upg.android.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.net.HttpUtils;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.ToastUtil;

public class ServiceAddressActivity extends BaseActivity implements OnClickListener{

	private Button btnConfirm,btnCancel;
    private EditText serviceAdrressEt, servicePortEt;
    private TextView port;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.service_address_act);
       
		btnConfirm = (Button)findViewById(R.id.btnConfirm);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		serviceAdrressEt = (EditText)findViewById(R.id.et_service_address);
		servicePortEt = (EditText)findViewById(R.id.et_service_port);
		port  = (TextView)findViewById(R.id.port);
		port.setText("端口：");
		
		serviceAdrressEt.setText(HttpUtils.getBaseIp());
		servicePortEt.setText(HttpUtils.getBasePort());
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.btnConfirm:  //确认
			String serviceAdrress = serviceAdrressEt.getText().toString().trim();
			String servicePort = servicePortEt.getText().toString().trim();
			if(TextUtils.isEmpty(serviceAdrress) || TextUtils.isEmpty(servicePort)){
				ToastUtil.showShortToast(ServiceAddressActivity.this, "服务器地址或端口不能为空");
				return ;
			}
			
			DefaultShared.putString(Constants.KEY_SERVICE_ADDRESS, serviceAdrress);
			DefaultShared.putString(Constants.KEY_SERVICE_PORT, servicePort);
			ToastUtil.showShortToast(ServiceAddressActivity.this, "保存成功");
			finishActivity();
			break;
		case R.id.btnCancel:  //取消
			
			finishActivity();
			
			break;
		}
		
	}
}
