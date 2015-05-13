package com.juchao.upg.android.ui;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.db.InspectionEquipmentDao;
import com.juchao.upg.android.util.FileUtil;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.util.ToastUtil;

public class DjReadyActivity extends BaseActivity implements OnClickListener {
	public static final String TAG="DjReadyActivity";
    
    private TextView headerLeft , headerTitle ,secondTitle1 , text1;
    private CheckBox cbReady;
    private Button startInspectionBtn;
    
    private long inspectionTaskEquipmentId;
    private long equipmentId ;
    private String mEquipmentName; 
	private String taskName; 
	private long inspectionTaskId;
	private int style;
	private int type;
	
    public Context context;
    private WindowManager vm;
    private boolean isFirst = true;
    private StringBuilder photoBuilder;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.dj_ready_act);
        vm=this.getWindowManager();
        
        photoBuilder = new StringBuilder();
		inspectionTaskId=getIntent().getLongExtra("inspectionTaskId", 0L);
		inspectionTaskEquipmentId = getIntent().getLongExtra("inspectionTaskEquipmentId", 0);
		equipmentId = getIntent().getLongExtra("equipmentId", 0);
		mEquipmentName = getIntent().getStringExtra("mEquipmentName");
		taskName = getIntent().getStringExtra("taskName"); 
		style=getIntent().getIntExtra("style", 0);
		type=getIntent().getIntExtra("type",0);
		
		headerLeft = (TextView)findViewById(R.id.header_left);
		headerTitle = (TextView)findViewById(R.id.header_title);
		secondTitle1 = (TextView)findViewById(R.id.secondTitle1);
		text1 = (TextView)findViewById(R.id.text1);
		cbReady = (CheckBox)findViewById(R.id.cbReady);
		startInspectionBtn = (Button)findViewById(R.id.startInspectionBtn);
		
		headerLeft.setText("点检列表");
		headerTitle.setText("准备工作确认");
		secondTitle1.setText("点检操作须知：");
		
		headerLeft.setOnClickListener(this);
		startInspectionBtn.setOnClickListener(this);
		
		loadData();
		
		text1.setOnClickListener(textListener);
		//text1.setMovementMethod(LinkMovementMethod.getInstance());
		//setSpanClickable(null);
		//InitNotice();
	}
	

	private void InitNotice() {
		Spannable s= (Spannable) text1.getText();
		
		ImageSpan [] imageSpans=s.getSpans(0, s.length(), ImageSpan.class);
		for(final ImageSpan span: imageSpans){
			final String image_src=span.getSource();
			final int start = s.getSpanStart(span);
			final int end = s.getSpanEnd(span);
			
			ClickableSpan[] click_spans=s.getSpans(start, end, ClickableSpan.class);
			
			if(click_spans.length != 0){
				
				//remove all click spans
				for(ClickableSpan c_span : click_spans){
					s.removeSpan(c_span);
				}
			}
			
			//spanEnd = end;
			ClickableSpan click_span = new ClickableSpan(){

				@Override
				public void onClick(View widget) {
					/**
					 * do some thing.
					 */
					//ToastUtil.showLongToast(context, "click");
					int[]location=new int[2];
						//v.getLocationOnScreen(location);
						ShowMaxPicture(image_src,location[0],location[1],0,0);
					
				}
			};
			s.setSpan(click_span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
	}


	private void loadData() {
		//查数据库 ，返回操作须知
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				InspectionEquipmentDao dao = new InspectionEquipmentDao(DjReadyActivity.this);
				String operationNotice = dao.queryEquipmentOperationNotice(equipmentId,type);
				
				dao.closeDB();
				return operationNotice;
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if(!TextUtils.isEmpty(result)){
					//result=result.replace("<img src=">", "<img src='http://www.baidu.com/img/baidu_sylogo1.gif'/></p>");
					//Html.fromHtml(result, imageGetter, tagHandler);
					//Pattern pattern=Pattern.compile("<img src=(.*?)////.*?\.gif");
//					MatchCollection mth=null;
//					Matcher matcher=pattern.matcher(result);
					//String regex="<img src=\"(.*?)\\\\.*?\\.gif";
					//String regex="<img .*?src=\"(.*)/[^/]+\\.(jpg|png|bmp|jpeg|gif|PNG|JPG|BMP|GIF)";
					
					String regex="<img .*?src=\"(([^\"]+id=)([^\"]+))";
					
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher=pattern.matcher(result);
					while(matcher.find()){
						result=result.replace(matcher.group(1).toString(),FileUtil.getBaseImageDir()+File.separator +matcher.group(3).toString()+".jpg");
					}
					text1.setText(Html.fromHtml(result,locImgGetter,null));
			        text1.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动
				}
			}
		}.execute();
	}
	
	public void setSpanClickable(final View v){
		Spannable s= (Spannable) text1.getText();
		
		
		text1.setMovementMethod(LinkMovementMethod.getInstance());
		
		ImageSpan [] imageSpans=s.getSpans(0, s.length(), ImageSpan.class);
		for(final ImageSpan span: imageSpans){
			final String image_src=span.getSource();
			final int start = s.getSpanStart(span);
			final int end = s.getSpanEnd(span);
			
			ClickableSpan[] click_spans=s.getSpans(start, end, ClickableSpan.class);
			
			if(click_spans.length != 0){
				
				//remove all click spans
				for(ClickableSpan c_span : click_spans){
					s.removeSpan(c_span);
				}
			}
			
			//spanEnd = end;
			ClickableSpan click_span = new ClickableSpan(){

				@Override
				public void onClick(View widget) {
					/**
					 * do some thing.
					 */
					//ToastUtil.showLongToast(context, "click");
					int[]location=new int[2];
					if(v!=null){
						//v.getLocationOnScreen(location);
						ShowMaxPicture(image_src,location[0],location[1],v.getWidth(),v.getHeight());
					}
				}
			};

			s.setSpan(click_span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	protected void ShowMaxPicture(String source, int start, int end, int width, int height) {
		
		Log.d("inspection image click ", source);
		Intent intent = new Intent(DjReadyActivity.this, SpaceImageDetailActivity.class);
		intent.putExtra("source",source);
		intent.putExtra("sourceTotal", photoBuilder.toString());
		//intent.putExtra("images", (ArrayList<String>) datas);
		//intent.putExtra("position", position);
		//int[] location = new int[2];
		//v.getLocationOnScreen(location);
//		intent.putExtra("locationX", start);
//		intent.putExtra("locationY", end);

//		intent.putExtra("width", width);
//		intent.putExtra("height", height);
		
		startActivity(intent);
		//overridePendingTransition(0, 0);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private OnClickListener textListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			setSpanClickable(v);
//			// I dont know why the first is does not work,so we should run it again.
//			if(isFirst){ 
//				setSpanClickable(v);
//				isFirst=false;
//			}
//			InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(text1.getWindowToken(), 0);
//			Spanned s=(Spanned) text1.getText();
//			ImageSpan [] imageSpans=s.getSpans(0, s.length(), ImageSpan.class);
//			int selectionStart = text1.getSelectionStart();
//			for(ImageSpan span:imageSpans){
//				int start = s.getSpanStart(span);
//				int end= s.getSpanEnd(span);
//				if(selectionStart >= start && selectionStart < end){
//					
//				}
//			}
//			imm.showSoftInput(text1, 0);
		}
	};
	
	
	ImageGetter urlImgGetter = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			URL url;
			try{
				url= new URL(source);
				URLConnection conn=url.openConnection();
				conn.connect();
				InputStream is=conn.getInputStream();
				
				drawable = Drawable.createFromStream(is, "");
				is.close();
			}catch(Exception e){
				e.printStackTrace();
				Log.d(TAG, e.getMessage());
				return null;
			}
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			return drawable;
		}
	};
	ImageGetter locImgGetter = new Html.ImageGetter() {
		
		@Override
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			try{
				drawable = Drawable.createFromPath(source);
				if(drawable !=null){
					photoBuilder.append(source);
					photoBuilder.append(",");
					//BitmapDrawable d
					//BitmapDrawable d=new BitmapDrawable();
					//DisplayMetrics metrics=new DisplayMetrics();
					
					
					//Bitmap bm = BitmapFactory.decodeFile(source);
					int macWidth=vm.getDefaultDisplay().getWidth();
					
					int intrWid = drawable.getIntrinsicWidth() > macWidth/2  ? drawable.getIntrinsicWidth() : macWidth/2;
					
					int width = macWidth < intrWid ? macWidth : intrWid;
					
					int height= drawable.getIntrinsicHeight() * width/ drawable.getIntrinsicWidth();
					
					
					//int height = text1.getHeight() < bm.getHeight() ? text1.getHeight() : bm.getHeight();
					//Bitmap d=
							
					//text1.getWidth();
					//
//		           CharSequence t = text1.getText();  
//		           text1.setText(t);  
				   drawable.setBounds(0, 0, width, height);
				}
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			return drawable;
		}
	};
	

	private class MySpan extends ClickableSpan implements OnClickListener{

		public MySpan(Editable output) {
			
		}

		@Override
		public void onClick(View widget) {
			
			
		}
	}
	
	 TagHandler locTagHandler=new Html.TagHandler() {
		
		@Override
		public void handleTag(boolean opening, String tag, Editable output,
				XMLReader xmlReader) {
			//output.setSpan(new MySpan(output), 0, output.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	};
	
	TagHandler urlTagHandler = new Html.TagHandler() {
		
		@Override
		public void handleTag(boolean opening, String tag, Editable output,
				XMLReader xmlReader) {
			
		}
	};


	@Override

	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.header_left:  //返回
				finishActivity();
				break;
				
				
			case R.id.startInspectionBtn : //开始点检
				if(!cbReady.isChecked()){
					ToastUtil.showShortToast(context, "请勾选已经准备好");
					break;
				}
				try{
					
					Bundle bundle = new Bundle();
					bundle.putLong("inspectionTaskId", inspectionTaskId);
					bundle.putLong("inspectionTaskEquipmentId", inspectionTaskEquipmentId);
					bundle.putLong("equipmentId", equipmentId);
					bundle.putString("mEquipmentName", mEquipmentName);
					bundle.putString("taskName", taskName);
					bundle.putInt("style",style);
					//IntentUtil.startActivity((Activity)context, DjTaskDetailActivity.class, bundle);
					IntentUtil.startActivity(DjReadyActivity.this,DjTaskItemListOperationActivity.class, bundle);
					
					finishActivity();
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
		}
	}
}
