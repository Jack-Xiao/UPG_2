package com.juchao.upg.android.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.ResAppUpdate;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.ToastUtil;

public class CheckAppUpdateActivity extends BaseActivity implements OnClickListener {

    
    private static final String TAG = CheckAppUpdateActivity.class.getSimpleName();
	private TextView headerLeft , headerTitle ,secondTitle;
    private Button btnConfirm,btnCancel;
    private TextView updateNote ;
    private ResAppUpdate mResAppUpdate;
    
    private File file;
    private DownloadTask downloadTask;//下载线程
    private RelativeLayout progressLayout;
    private ProgressBar  progressBar;
    private TextView progressNum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.check_app_update_act);
       
		
		mResAppUpdate = (ResAppUpdate)getIntent().getSerializableExtra("ResAppUpdate");
		
		if(mResAppUpdate == null){
			finishActivity();
		}
		initView();
		
		
	}
	
	
	
	
	private void initView() {
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		secondTitle = (TextView)findViewById(R.id.second_title);
		updateNote = (TextView)findViewById(R.id.updateNote);
		
		headerLeft.setText("配置");
		headerTitle.setText("检查更新");
		secondTitle.setText("新版本V" + mResAppUpdate.versionName + "介绍：");
		updateNote.setText(mResAppUpdate.log);
		
		progressLayout = (RelativeLayout)findViewById(R.id.progressLayout);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		progressNum = (TextView)findViewById(R.id.progressNum);
		progressLayout.setVisibility(View.GONE);
		
		btnConfirm = (Button)findViewById(R.id.btnConfirm);
		btnConfirm.setText("立即更新");
		btnCancel = (Button)findViewById(R.id.btnCancel);
	
		
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}



	private void initApkFile() {
		try {
			
			String mDirPath = ClientUtil.getDownDir();
			
			String apkName = "new_upg.apk";
			file = new File(mDirPath , apkName);
			if(file.exists()){
				file.delete();
				Log.i(TAG, "file is deleted");
			}
			file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.header_left:  //返回
				finishActivity();
				break;
			case R.id.btnConfirm:  //确认
				btnConfirm.setClickable(false);
				downloadTask = new DownloadTask();
				downloadTask.execute(mResAppUpdate.url);
				
				break;
			case R.id.btnCancel:  //取消
				if(downloadTask != null && !downloadTask.isCancelled()){
					downloadTask.cancel(true);
				}
				finishActivity();
				break;
		}
	}
	
	
	/**
	 * 下载进程
	 */
	private class DownloadTask extends AsyncTask<String, Integer, Boolean> {

		int downLength = 0;
		int total = 0;
		int progress = 0;
		int lastProgress = 0;
		@Override
		protected void onPreExecute() {
			progressBar.setProgress(0);
			progressNum.setText("0%");
			btnConfirm.setClickable(false);
			progressLayout.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean isSccuess = false;
			
			String downUrl = params[0].toString();
			
			try {
				Log.d(TAG, "start download apk");
				URL url = new URL(downUrl);
				HttpURLConnection http = (HttpURLConnection) url.openConnection();
				http.setConnectTimeout(5 * 1000); // 设置连接超时
				http.setReadTimeout(5 * 1000);
				http.setRequestMethod("GET"); // 设置请求方法，这里是“GET”
				http.connect();  //连接  
				if (http.getResponseCode() == 200) { //返回的响应码200,是成功. 
					total = http.getContentLength();
					initApkFile();
					InputStream inputStream = http.getInputStream();   
		            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(); //缓存   
		            byte[] buffer = new byte[1024 * 10];
		            while (true) {   
	                    int len = inputStream.read(buffer);   
	                    publishProgress(len);   
	                    if (len == -1) {   
	                        break;  //读取完  
	                    }
	                   arrayOutputStream.write(buffer, 0, len);  //写入  
	                }
		            arrayOutputStream.close();   
	                inputStream.close();   
	  
	                byte[] data = arrayOutputStream.toByteArray();   
	                FileOutputStream fileOutputStream = new FileOutputStream(file);   
	                fileOutputStream.write(data); 
	                //记得关闭输入流   
	                fileOutputStream.close();
	                isSccuess = true;
				}
				
				Log.d(TAG, "start download success");
			} catch (Exception e) {
				isSccuess = false;
				downLength = -1;
			}
			
			return isSccuess;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			downLength += values[0];
			if(total != 0){
				progress = (int)(((float)downLength / total) * 100);
				Log.d(TAG, "onProgressUpdate valuesp[0] : " +values[0] + " , progress : " +progress + " ,total : "+ total);
				
				 //防止界面刷新得很频繁
				if((progress >= lastProgress + 1) || progress == 100){ 
					lastProgress = progress;
					progressBar.setProgress(progress);
					progressNum.setText(progress+"%");
				}
			}
			
		}

		@Override
		public void onPostExecute(Boolean isSccuess) {//安装apk
			if(isSccuess){
				try{
					Intent apkintent = new Intent(Intent.ACTION_VIEW); 
					apkintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    Uri puri = Uri.fromFile(file);
				    apkintent.setDataAndType(puri, "application/vnd.android.package-archive");  
				    startActivity(apkintent);
				}catch(Exception e){
					e.printStackTrace();
					ToastUtil.showShortToast(CheckAppUpdateActivity.this, "下载失败");
				}
			}else{
				ToastUtil.showShortToast(CheckAppUpdateActivity.this, "下载失败");
				btnConfirm.setClickable(true);
			}
		}
	}
	
	
	
}
