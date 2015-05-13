package com.juchao.upg.android.view;




import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.ui.ConfigActivity;
import com.juchao.upg.android.ui.LoginActivity;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.IntentUtil;


/**
 * PopupWindowUtil
 * @author xuxd
 *
 */
public class PopupWindowUtil extends BasePopupWindow{
	
	private static final String TAG = PopupWindowUtil.class.getSimpleName();
	private PopupWindow popupWindow;
	
	private static PopupWindowUtil instance;
	
	
	public PopupWindowUtil(){
		
	}
	public synchronized static PopupWindowUtil getInstance(){
		if(instance == null){
			instance = new PopupWindowUtil();
		}
		return instance;
	}
	
	public boolean isShowing(){
		if(popupWindow != null)
			return popupWindow.isShowing();
		return false;
	}
	
	public  void dismiss(Activity activity){
		if(activity != null && !activity.isFinishing() && 
				popupWindow != null && popupWindow.isShowing()){
			popupWindow.dismiss();
		}
	}

	public void showActionWindow(Activity mActivity , View v) {
		View view = getView(mActivity);
		
		int width = view.getWidth();
		int height = view.getHeight();
		
//		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT,true);
		popupWindow = new PopupWindow(view, ClientUtil.dip2px(mActivity, 160),LayoutParams.WRAP_CONTENT,true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);  
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
		popupWindow.update();  
		popupWindow.setTouchable(true);  
		popupWindow.setFocusable(true);  
		
		int[] location = new int[2];  
        v.getLocationOnScreen(location);
        Log.d(TAG, "x : "+ location[0] + "  , y : " + location[1] + " ,, height :  " +v.getHeight());
		if(mActivity != null && !mActivity.isFinishing())
			popupWindow.showAtLocation(view, Gravity.RIGHT|Gravity.TOP, 0 , location[1] + v.getHeight());
		
	}
	
	private View getView(final Activity mActivity ) {
		View view = LayoutInflater.from(mActivity).inflate(R.layout.main_popup_layout, null);
		
		TextView setting = (TextView)view.findViewById(R.id.item1);
		setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				IntentUtil.startActivityFromMain(mActivity, ConfigActivity.class);
				
				dismiss(mActivity);
			}
		});
		
		TextView exit = (TextView)view.findViewById(R.id.item2);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss(mActivity);
				
				CustomDialog.Builder builder = new CustomDialog.Builder(mActivity);  
		        builder.setMessage("确定要退出吗？");  
		        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
		            public void onClick(DialogInterface dialog, int which) {  
		                dialog.dismiss();  
		                DefaultShared.putString(Constants.KEY_TOKEN, "");
		                IntentUtil.startActivity(mActivity, LoginActivity.class);
						mActivity.finish();
		            }
		        });  
		        builder.setNegativeButton("取消",  
		                new android.content.DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int which) {  
		                        dialog.dismiss();  
		                    }  
		                });  
		        builder.create().show();	
			}
		});
		return view; 
	}
	
	
}
