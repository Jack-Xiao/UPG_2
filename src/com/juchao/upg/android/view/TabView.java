package com.juchao.upg.android.view;

import java.lang.reflect.Method;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juchao.upg.android.MyApp;
import com.juchao.upg.android.R;

public class TabView extends LinearLayout {

	

	private LinearLayout tabView;
	private ImageView tabIcon ;
	private TextView tabName;
	public TabView(Context context, AttributeSet attrs) { 
		super(context, attrs);
		tabView = (LinearLayout) inflate(context, R.layout.tab_view, null);
		addView(tabView);
		tabIcon = (ImageView) tabView.findViewById(R.id.tab_icon);
		tabName = (TextView) tabView.findViewById(R.id.tab_name);
	}
	

	public TabView(Context context) {
		this(context, null);
	} 
  
	public void setTextAndColor(String text , int color){
		tabName.setText(text);
		tabName.setTextColor(color);
	}  
	
	public void setIcon(int resId){
		tabIcon.setImageResource(resId);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
        try {
        		
            Object service = MyApp.application.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method test = statusbarManager.getMethod("collapse");
            test.invoke(service);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
}
