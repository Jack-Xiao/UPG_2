package com.juchao.upg.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juchao.upg.android.R;

public class ImageTextView extends LinearLayout {

	

	private LinearLayout tabView;
	private ImageView tabIcon ;
	private TextView tabName;
	public ImageTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		tabView = (LinearLayout) inflate(context, R.layout.image_text_view, null);
		addView(tabView);
		tabIcon = (ImageView) tabView.findViewById(R.id.view_icon);
		tabName = (TextView) tabView.findViewById(R.id.view_name);
		
	}
	

	public ImageTextView(Context context) {
		this(context, null);
	}

	public void setText(String text){
		tabName.setText(text);
	}
	
	public void setIcon(int resId){
		tabIcon.setImageResource(resId);
	}

	
}
