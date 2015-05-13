package com.juchao.upg.android.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.juchao.upg.android.R;

public class AboutActivity extends BaseActivity implements OnClickListener {

    
    private TextView headerLeft , headerTitle ,secondTitle1 , secondTitle2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.about_act);
       
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		secondTitle1 = (TextView)findViewById(R.id.secondTitle1);
		secondTitle2 = (TextView)findViewById(R.id.secondTitle2);
		
		headerLeft.setText("配置");
		headerTitle.setText("关于");
		secondTitle1.setText("关于设备保全管理系统");
		secondTitle2.setText("关于广州聚超软件科技有限公司");
		
		headerLeft.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.header_left:  //返回
				finishActivity();
				break;
		}
	}
}
