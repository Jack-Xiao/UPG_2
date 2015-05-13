package com.juchao.upg.android.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.juchao.upg.android.R;
import com.juchao.upg.android.db.InspectionTaskDao;
import com.juchao.upg.android.entity.ResLogin;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.NetUtils;
import com.juchao.upg.android.util.ToastUtil;
import com.juchao.upg.android.view.MyAlertDialog;

public class LoginActivity extends BaseActivity implements OnClickListener{
	private Button btnLogin;
	 private EditText userNameEt, userPwdEt;
	 private Context context;
	 public ProgressDialog progressDialog;
	 private TextView serviceAddress;
	 private TextView tvVisionName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_act);
		
		context = this;
		tvVisionName = (TextView)findViewById(R.id.tvVisionName);
		userNameEt = (EditText)findViewById(R.id.loginName);
		userPwdEt = (EditText)findViewById(R.id.loginPwd);
		serviceAddress = (TextView)findViewById(R.id.serviceAddress);
		
		btnLogin = (Button)findViewById(R.id.btnLogin);
		
		serviceAddress.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		tvVisionName.setText("v" + getVersionName());
	}
	
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.serviceAddress:
			IntentUtil.startActivity(LoginActivity.this, ServiceAddressActivity.class);
			//服务器地址设置
			break;
		
		case R.id.btnLogin:  //登陆
			
			
//			DefaultShared.putString(Constants.KEY_TOKEN, "NZ5gIy2aYuo=");
//			IntentUtil.startActivity(LoginActivity.this, MainActivity.class);
//			finishActivity();
			
			String userName = userNameEt.getText().toString().trim();
			String userPwd = userPwdEt.getText().toString().trim();
			if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)){
				ToastUtil.showShortToast(LoginActivity.this, "用户名或密码不能为空");
				break ;
			}
			if(!NetUtils.isNetworkConnected(context)){
				Toast.makeText(context, "请检查网络", Toast.LENGTH_LONG).show();
				return;
			}
			doLogin(userName , userPwd);
			break;
		}
		
	}
	
	private void doLogin(final String userName , final String userPwd){
		new AsyncTask<String, Void, ResLogin>() {
			protected void onPreExecute() {
				if(!isFinishing()){
					try {
						progressDialog = ProgressDialog.show(context, null, "加载中...",true,true);
						progressDialog.setCanceledOnTouchOutside(false);
						progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								cancel(true);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			@Override
			protected ResLogin doInBackground(String... params) {
				String mac = ClientUtil.getLocalMacAddress(context);
				ResLogin mResLogin = NetAccessor.login(context, userName, userPwd, mac);
				
				if(mResLogin != null && mResLogin.code == Constants.REQUEST_SUCCESS){ //登录成功
					DefaultShared.putString(Constants.KEY_TOKEN, mResLogin.token);
					if(mResLogin.user != null){
						DefaultShared.putLong(Constants.KEY_USER_ID, mResLogin.user.userId);
						DefaultShared.putString(Constants.KEY_USER_NAME, userName);
					}
				}
				return mResLogin;
			}
			
			protected void onPostExecute(ResLogin result) {
				if(null != progressDialog && progressDialog.isShowing()){
					try {
						progressDialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if(result != null && result.code == Constants.REQUEST_SUCCESS){
					IntentUtil.startActivity(LoginActivity.this, MainActivity.class);
					finishActivity();
				}else if(result != null){
					ToastUtil.showShortToast(LoginActivity.this, result.msg);
				}else{
					ToastUtil.showShortToast(LoginActivity.this, "登录失败");
				}
			};
		}.execute();
	}
	
	
	private String getVersionName() {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo;
		String version = "";
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		LayoutInflater inflater = LayoutInflater.from(this);
		final View exitView = inflater.inflate(R.layout.loginurl, null);
		final EditText editText = (EditText)exitView.findViewById(R.id.edtInput);
		MyAlertDialog.Builder alter = new MyAlertDialog.Builder(this);
		alter.setView(exitView);
		alter.setTitle("请输入退出密码");
		alter.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if("00000000".equals(editText.getText().toString())){
					finish();
				}else{
					ToastUtil.showLongToast(context, "密码输入错误");
				}
			}
		});
		alter.setNegativeButton("取消", null);
		alter.show();
	}
}