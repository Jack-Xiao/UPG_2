package com.juchao.upg.android.net.upgrade;

import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.juchao.upg.android.entity.ResAppUpdate;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.ui.CheckAppUpdateActivity;
import com.juchao.upg.android.util.ToastUtil;


/**
 * 版本升级工具
 * 
 * 
 */
public class UpgradeUtils
{

	private static final String tag = UpgradeUtils.class.getSimpleName();
	private static ProgressDialog progressDialog;

	/**
	 * 检测新版本
	 * 
	 * @param ctx
	 */
	public static void checkNewVersion(final Activity ctx)
	{
		checkNewVersion(ctx, false);
	}

	/**
	 * 检测新版本
	 * 
	 * @param ctx
	 * @param isTip
	 *            是否显示  "正在检查更新，请稍候..."对话框
	 */
	public static void checkNewVersion(final Activity ctx, final boolean isTip){

		new Thread(){
			@Override
			public void run(){
				try{
					if (isTip){
						ctx.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								try {
									progressDialog = ProgressDialog.show(ctx, null, "正在检查更新，请稍候... 版本："
												+ ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),0).versionCode +"",
											true, true);
								} catch (NameNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
					
					PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),
							0);
					int mLocalVersonCode = info.versionCode;
					
					//获取检查更新的信息
					final ResAppUpdate mInfo = NetAccessor.getAppUpdateInfo(ctx);

					
					
					Log.d(tag, " close progressDialog");
					if (isTip){
						ctx.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if (!ctx.isFinishing() && progressDialog != null
										&& progressDialog.isShowing()){
									progressDialog.dismiss();
								}
							}
						});
					}

					
					// 当前版本小于服务器版本时才提示用户更新
					if (mInfo != null ){
						int mServiceVersonCode = mInfo.versionCode;
						if(mServiceVersonCode > mLocalVersonCode ){
							mInfo.localVersionCode = mLocalVersonCode;
							ctx.runOnUiThread(new Runnable(){
								@Override
								public void run(){
									
									Intent intent = new Intent(ctx , CheckAppUpdateActivity.class);
									intent.putExtra("ResAppUpdate", mInfo);
									ctx.startActivity(intent);
								}
							});
						}else if (isTip){
							ctx.runOnUiThread(new Runnable(){
								@Override
								public void run(){
									showNoUpdateTip(ctx);
								}
							});
						}
						
					} else if (isTip){
						ctx.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								ToastUtil.showShortToast(ctx, "获取数据失败");
							}
						});
						
					}
				} catch (Exception e){
					
					Log.d(tag, " " + e.getMessage());
				}
			}

			/**
			 * 将版本信息持久化
			 * 
			 * @param info
			 */
//			private void saveNewVersionInfo(VersionInfo info){
//				try{
//					ObjectOutputStream out = new ObjectOutputStream(ctx.openFileOutput(
//							"VersionInfo", Context.MODE_PRIVATE));
//					out.writeObject(info);
//					out.close();
//				} catch (Exception e){
//					e.printStackTrace();
//				}
//			}
		}.start();

	}

	

	private static void showNoUpdateTip(final Activity activity)
	{
		new AlertDialog.Builder(activity).setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("升级提醒").setMessage("您的版本已是最新版本！").setPositiveButton("确定", null).show();

	}

	


}
