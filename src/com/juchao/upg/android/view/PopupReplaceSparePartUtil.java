package com.juchao.upg.android.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.juchao.upg.android.R;
import com.juchao.upg.android.db.BaseDataDao;
import com.juchao.upg.android.entity.EquipmentSparePart;
import com.juchao.upg.android.entity.ResEquipmentSparePart1;
import com.juchao.upg.android.net.HttpUtils;
import com.juchao.upg.android.ui.LoginActivity;
import com.juchao.upg.android.util.ClientUtil;

/**
 * PopupWindowUtil
 * 
 * @author xuxd
 * 
 */
public class PopupReplaceSparePartUtil extends BasePopupWindow{

	private static final String TAG = PopupReplaceSparePartUtil.class
			.getSimpleName();
	private PopupWindow popupWindow;

	private static PopupReplaceSparePartUtil instance;
	private Activity mActivity;
	private int curInputValue = 0;
	private String sparePartTotal = "";

	public PopupReplaceSparePartUtil() {

	}

	public synchronized static PopupReplaceSparePartUtil getInstance() {
		if (instance == null) {
			instance = new PopupReplaceSparePartUtil();
		}
		return instance;
	}

	public boolean isShowing() {
		if (popupWindow != null)
			return popupWindow.isShowing();
		return false;
	}

	public void dismiss(Activity activity) {
		if (activity != null && !activity.isFinishing() && popupWindow != null
				&& popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	private SelItemClickListener listener;

	public interface SelItemClickListener {
		public void onCallback(String sparePart);
	}

	public void showActionWindow(Activity mActivity,
			List<ResEquipmentSparePart1> list, SelItemClickListener listener) {
		this.listener = listener;
		View view = getView(mActivity, list);

		int width = view.getWidth();
		int height = view.getHeight();

		// popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT,true);
		int[] w_h = ClientUtil.getWidthHeight(mActivity);
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		popupWindow.update();
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);

		// int[] location = new int[2];
		// v.getLocationOnScreen(location);
		// Log.d(TAG, "x : "+ location[0] + "  , y : " + location[1] +
		// " ,, height :  " +v.getHeight());
		if (mActivity != null && !mActivity.isFinishing())
			popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			try {
				ResEquipmentSparePart1 entity = (ResEquipmentSparePart1) parent
						.getAdapter().getItem(position);
				setSparePartNum();
				if (curInputValue != 0) {
					entity.number = curInputValue;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private View getView(final Activity mActivity,
			List<ResEquipmentSparePart1> list) {
		this.mActivity = mActivity;
		View view = LayoutInflater.from(mActivity).inflate(
				R.layout.dj_common_proble_popup_layout, null);
		final ListView listView = (ListView) view.findViewById(R.id.listView);
		final PopupSparepartAdapter adapter = new PopupSparepartAdapter(
				mActivity, list);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);

		Button confirm = (Button) view.findViewById(R.id.btnConfirm);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ResEquipmentSparePart1 entity = new ResEquipmentSparePart1();
				List<ResEquipmentSparePart1> list = adapter.getList();
				if (list != null && list.size() > 0) {
					{
						for (int i = 0; i < list.size(); i++) {
							if (((ResEquipmentSparePart1) list.get(i)).number != 0) {
								entity = list.get(i);
								String tempSparepart = entity.spapartid + "="
										+ entity.number + "";

								sparePartTotal += tempSparepart + ",";
							}
						}
						if(sparePartTotal.length() >0){
							sparePartTotal = sparePartTotal.substring(0, sparePartTotal.length() -1);
						}
						listener.onCallback(sparePartTotal);
					}
				}
				dismiss(mActivity);
			}
		});

		Button exit = (Button) view.findViewById(R.id.btnCancel);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss(mActivity);
			}
		});
		return view;
	}

	private void setSparePartNum() {
		curInputValue = 0;
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		final View textEntryView = inflater.inflate(
				R.layout.wx_sparepart_item_num, null);
		final EditText edtInput = (EditText) textEntryView
				.findViewById(R.id.edtInput);

		final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setCancelable(false);
		builder.setTitle("请输入数量");
		builder.setView(textEntryView);

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@SuppressLint("CommitPrefEdits")
			public void onClick(DialogInterface dialog, int whichButton) {
				curInputValue = Integer.parseInt(edtInput.getText().toString());
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	class PopupSparepartAdapter extends BaseAdapter {
		private List<ResEquipmentSparePart1> list;
		private LayoutInflater mLayoutInflater;
		private BaseDataDao dao;

		public PopupSparepartAdapter(Context context,
				List<ResEquipmentSparePart1> list) {
			this.list = list;
			mLayoutInflater = LayoutInflater.from(context);
			BaseDataDao dao = new BaseDataDao(mActivity);
			this.dao = dao;
		}

		public List<ResEquipmentSparePart1> getList() {
			return list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public ResEquipmentSparePart1 getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.wx_sparepart_selection_item, null);
				holder = new ViewHolder();
				holder.sparePartName = (TextView) convertView
						.findViewById(R.id.sparepartName);
				holder.sparePartNum = (TextView) convertView
						.findViewById(R.id.sparepartNum);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			try {
				ResEquipmentSparePart1 item = getItem(position);
				holder.sparePartName.setText(item.name);
				holder.sparePartNum.setText(item.number);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

		class ViewHolder {
			TextView sparePartName;
			TextView sparePartNum;
		}
	}
}
