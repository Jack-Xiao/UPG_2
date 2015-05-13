package com.juchao.upg.android;


import com.juchao.upg.android.ui.MainActivity;
import com.juchao.upg.android.ui.StartActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
	static final String action_boot="android.intent.action.BOOT_COMPLETED"; 
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getAction().equals(action_boot)){  
            Intent ootStartIntent=new Intent(context,StartActivity.class);  
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
            context.startActivity(ootStartIntent);  
        } 
	}
}
