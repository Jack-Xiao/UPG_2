package com.juchao.upg.android.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.juchao.upg.android.R;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

public class StartActivity extends BaseActivity {
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.start_act);

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = null;
				String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
				Log.d("StartActivity", "begin UPG    " + token);
				if (TextUtils.isEmpty(token)) {
					intent = new Intent(StartActivity.this, LoginActivity.class);
				//}
				} else {
					intent = new Intent(StartActivity.this, MainActivity.class);
				}
				startActivity(intent);
				finishActivity();
			}
		}, 50);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
	}
	
	
	
}
