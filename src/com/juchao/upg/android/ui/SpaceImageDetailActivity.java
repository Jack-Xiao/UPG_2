package com.juchao.upg.android.ui;

import java.util.ArrayList;

import com.juchao.upg.android.R;

import android.R.integer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import com.juchao.upg.android.view.SmoothImageView;

public class SpaceImageDetailActivity extends BaseActivity{

	private ArrayList<String> mDatas;
	private int mPosition;
	private int mLocationX;
	private int mLocationY;
	private int mWidth;
	private int mHeight;
	private String source;
	private String sourceTotal;
	private LinearLayout pointLinearLayout;
	private int currentIndex ;
	private ViewPager viewPager;
	private String[] size;
	private SmoothImageView [] imageViews;
	private ImageView[] pointViews;
	
	
	//private ImageView imageView;
	com.juchao.upg.android.view.SmoothImageView imageView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_photo_view_layout);
				
		pointLinearLayout = (LinearLayout)findViewById(R.id.pointLayout);
		viewPager = (ViewPager)findViewById(R.id.viewPager);
												  
		sourceTotal = getIntent().getStringExtra("sourceTotal");
		
		if(sourceTotal.length() >0){
			sourceTotal = sourceTotal.substring(0, sourceTotal.length() - 1)
					.toString();
			size = sourceTotal.split(",");

			imageViews = new SmoothImageView[size.length];
			pointViews = new ImageView[size.length];

			source = getIntent().getStringExtra("source");

			for (int i = 0; i < size.length; i++) {
				if (size[i].equals(source)) {
					currentIndex = i;
					break;
				}
			}

			for (int i = 0; i < size.length; i++) {

				initPointView(size[i]); // 初始化所有的点
				// 初始化所有的ImageView到数组中保存
				SmoothImageView imageView = new SmoothImageView(this);
				Drawable drawable = Drawable.createFromPath(size[i]);

				// iData95 版本为 16
				imageView.setImageDrawable(drawable);
				imageViews[i] = imageView;
			}

			viewPager.setAdapter(new MyAdapter());
			viewPager.setCurrentItem(currentIndex, false);
			viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		}
		
//		mDatas = (ArrayList<String>) getIntent().getSerializableExtra("images");
//		mPosition = getIntent().getIntExtra("position", 0);
//		mLocationX = getIntent().getIntExtra("locationX", 0);
//		mLocationY = getIntent().getIntExtra("locationY", 0);
//		mWidth = getIntent().getIntExtra("width", 0);
//		mHeight = getIntent().getIntExtra("height", 0);
//		
//
//		imageView = new com.juchao.upg.android.view.SmoothImageView(this);
//		//imageView = new ImageView(this);
//		//imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
//		//imageView.transformIn();
//		
//		
//		imageView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		
//		
//		//imageView.setScaleType(ScaleType.FIT_CENTER);
//		imageView.setScaleType(ScaleType.MATRIX);
//		
//		imageView.setImageDrawable(Drawable.createFromPath(source));
//		setContentView(imageView);
		
		
		
		//ImageLoader.getInstance().displayImage(mDatas.get(mPosition), imageView);
		
//		imageView.setImageResource(R.drawable.temp);
		// ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f,
		// 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
		// 0.5f);
		// scaleAnimation.setDuration(300);
		// scaleAnimation.setInterpolator(new AccelerateInterpolator());
		// imageView.startAnimation(scaleAnimation);

	}
	public class MyAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return size.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			
			return arg0 == arg1;
		}
		
		
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(imageViews[position]);
			return imageViews[position];
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			
			container.removeView(imageViews[position]);
		}
	}
	

	private void initPointView(String source) {
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
		ImageView pointView = new ImageView(this);
		
		pointView.setImageResource(R.drawable.page);
		
		mLayoutParams.leftMargin = 10;		
		
		pointView.setScaleType(ScaleType.MATRIX);
			
		int index = pointLinearLayout.getChildCount();
		pointViews[index] = pointView;
		
		if(index == currentIndex ) pointView.setImageResource(R.drawable.page_now);
		
		pointLinearLayout.addView(pointView,mLayoutParams);
	}
	

	@Override
	public void onBackPressed() {
//		imageView.setOnTransformListener(new com.juchao.upg.android.view.SmoothImageView.TransformListener() {
//			@Override
//			public void onTransformComplete(int mode) {
//				if (mode == 2) {
//					finish();
//				}
//			}
//		});
//		imageView.transformOut();
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			overridePendingTransition(0, 0);
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			setPointViewBackground(arg0);
		}
	}
	
	private void setPointViewBackground(int index){
		for(int i = 0; i <size.length; i++){
			if(i == index ){
				pointViews[i].setImageResource(R.drawable.page_now);
			}else{
				pointViews[i].setImageResource(R.drawable.page);
			}
		}
	}
}