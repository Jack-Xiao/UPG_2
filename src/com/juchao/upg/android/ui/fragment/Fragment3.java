package com.juchao.upg.android.ui.fragment;

import com.juchao.upg.android.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Fragment3 extends BaseFragment {

	private static final String TAG = Fragment3.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.main_fragment_1, null);
		
		return contentView;
	}

}
