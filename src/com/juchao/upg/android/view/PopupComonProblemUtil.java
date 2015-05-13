package com.juchao.upg.android.view;




import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.entity.BaseCommonProblem;
import com.juchao.upg.android.util.ClientUtil;


/**
 * PopupWindowUtil
 * @author xuxd
 *
 */
public class PopupComonProblemUtil extends BasePopupWindow{
	
	private static final String TAG = PopupComonProblemUtil.class.getSimpleName();
	private PopupWindow popupWindow;
	
	private static PopupComonProblemUtil instance;
	
	
	public PopupComonProblemUtil(){
		
	}
	public synchronized static PopupComonProblemUtil getInstance(){
		if(instance == null){
			instance = new PopupComonProblemUtil();
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
		public void onCallback(String commonProble);
	}
	public void showActionWindow(Activity mActivity  ,List<BaseCommonProblem> list ,SelItemClickListener listener) {
		this.listener = listener;
		View view = getView(mActivity ,list);
		
		int width = view.getWidth();
		int height = view.getHeight();
		
//		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT,true);
		int[] w_h = ClientUtil.getWidthHeight(mActivity);
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);  
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
		popupWindow.update();  
		popupWindow.setTouchable(true);  
		popupWindow.setFocusable(true);  
		
//		int[] location = new int[2];  
//        v.getLocationOnScreen(location);
//        Log.d(TAG, "x : "+ location[0] + "  , y : " + location[1] + " ,, height :  " +v.getHeight());
		if(mActivity != null && !mActivity.isFinishing())
			popupWindow.showAtLocation(view, Gravity.CENTER, 0 , 0);
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			try{
				CheckBox checkBox= (CheckBox)view.findViewById(R.id.checkBox);
				if(checkBox.isChecked()){
					checkBox.setChecked(false);
					((BaseCommonProblem)parent.getAdapter().getItem(position)).isChecked = false;
				}else{
					checkBox.setChecked(true);
					((BaseCommonProblem)parent.getAdapter().getItem(position)).isChecked = true;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	
	private View getView(final Activity mActivity ,List<BaseCommonProblem> list) {
		View view = LayoutInflater.from(mActivity).inflate(R.layout.dj_common_proble_popup_layout, null);
		final ListView listView = (ListView)view.findViewById(R.id.listView);
		final InspectionItemListAdapter adapter = new InspectionItemListAdapter(mActivity ,list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
		
		Button setting = (Button)view.findViewById(R.id.btnConfirm);
		setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String commonProble = "";
				List<BaseCommonProblem> list = adapter.getList();
				if(listener != null && list != null){
					for(int i = 0 ; i <list.size() ;i++ ){
						if(list.get(i).isChecked == true){
							commonProble += list.get(i).faultDescribe;
						}
					}
					listener.onCallback(commonProble);
				}
				dismiss(mActivity);
			}
		});
		
		Button exit = (Button)view.findViewById(R.id.btnCancel);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss(mActivity);
			}
		});
		return view; 
	}
	
	class InspectionItemListAdapter extends BaseAdapter {

		private List<BaseCommonProblem> list;
		private LayoutInflater mLayoutInflater;
		
		public InspectionItemListAdapter(Context context ,List<BaseCommonProblem> list) {
			this.list = list;
			mLayoutInflater = LayoutInflater.from(context);
		}
		
		public List<BaseCommonProblem> getList(){
			return list;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public BaseCommonProblem getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if(convertView == null ){
				convertView = mLayoutInflater.inflate(R.layout.dj_common_proble_item, null);
				holder = new ViewHolder();
				holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
				holder.itemName = (TextView)convertView.findViewById(R.id.itemName);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			try{
				BaseCommonProblem item = getItem(position);
				if(item.isChecked){
					holder.checkBox.setChecked(true);
				}else{
					holder.checkBox.setChecked(false);
				}
				holder.itemName.setText(item.faultDescribe);
				holder.checkBox.setTag(position);
				holder.checkBox.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CheckBox cb = (CheckBox)v;
						int pos = (Integer)cb.getTag();
						if(cb.isChecked()){
							getItem(pos).isChecked = true;
						}else{
							getItem(pos).isChecked = false;
						}
						
					}
				});
			}catch(Exception e){
				e.printStackTrace();
			}
			return convertView;
		}

		class ViewHolder{
			CheckBox checkBox;
			TextView itemName;
		}
	}
	
	
}
