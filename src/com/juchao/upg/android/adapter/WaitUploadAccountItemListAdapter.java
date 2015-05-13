package com.juchao.upg.android.adapter;

import java.util.ArrayList;
import java.util.List;

import com.juchao.upg.android.R;
import com.juchao.upg.android.adapter.WaitUploadInspectionItemListAdapter.ViewHolder;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juchao.upg.android.entity.AccountEquipment;

public class WaitUploadAccountItemListAdapter extends BaseAdapter {
	private Context mContext;
	private List<AccountEquipment> list;
	private LayoutInflater mLayoutInflater;

	public WaitUploadAccountItemListAdapter(Context context) {
		list = new ArrayList<AccountEquipment>();
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void addData(List<AccountEquipment> mList) {
		if (list == null) {
			list = new ArrayList<AccountEquipment>();
		}
		list.addAll(mList);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public AccountEquipment getItem(int position) {
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
					R.layout.pd_task_item_list_item, null);
			holder = new ViewHolder();
			holder.stateIcon = (ImageView) convertView
					.findViewById(R.id.stateIcon);
			holder.itemName = (TextView) convertView
					.findViewById(R.id.itemName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {
			AccountEquipment item = getItem(position);
			holder.itemName.setText(item.equipmentName);
			holder.stateIcon
					.setImageResource(R.drawable.inspection_wait_upload);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		ImageView stateIcon;
		TextView itemName;
	}

	public void setData(List<AccountEquipment> result) {
		
		
	}
}
