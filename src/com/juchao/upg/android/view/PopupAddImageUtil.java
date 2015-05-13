package com.juchao.upg.android.view;




import android.app.Activity;
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
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.Constants;


/**
 * PopupWindowUtil
 * @author xuxd
 *
 */
public class PopupAddImageUtil extends BasePopupWindow{
	
	private static final String TAG = PopupAddImageUtil.class.getSimpleName();
	private PopupWindow popupWindow;
	private int type;
	private static PopupAddImageUtil instance;
	
	
	public PopupAddImageUtil(){
		
	}
	public synchronized static PopupAddImageUtil getInstance(){
		if(instance == null){
			instance = new PopupAddImageUtil();
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

	private SelItemClickListener listener;
	public interface SelItemClickListener{
		public void onCallback(int type);
	}
	public void showActionWindow(Activity mActivity , View v ,int wxPopup, SelItemClickListener listener) {
		this.listener = listener;
		type = wxPopup;
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
		
		View view = null;
		if(type == Constants.WX_POPUP){
			 view = LayoutInflater.from(mActivity).inflate(R.layout.wx_add_image_popup_layout, null);
		}else if(type == Constants.DJ_POPUP){
			view = LayoutInflater.from(mActivity).inflate(R.layout.dj_add_image_popup_layout, null);
		}
		
		
		TextView setting = (TextView)view.findViewById(R.id.item1);
		setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null){
					listener.onCallback(0);
				}
				dismiss(mActivity);
			}
		});
		
		TextView exit = (TextView)view.findViewById(R.id.item2);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null){
					listener.onCallback(1);
				}
				dismiss(mActivity);
			}
		});
		
		TextView sparePart = (TextView)view.findViewById(R.id.item3);
		if(sparePart !=null){
			sparePart.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if(listener !=null){
						listener.onCallback(2);
					}
					dismiss(mActivity);
				}
			});
		}
		return view; 
		
	}
	
	
}
