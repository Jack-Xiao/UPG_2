package com.juchao.upg.android.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.juchao.upg.android.entity.AccountEquipment;
import com.juchao.upg.android.entity.BaseEquipmentAttachment;
import com.juchao.upg.android.entity.BaseImage;
import com.juchao.upg.android.entity.BaseResult;
import com.juchao.upg.android.entity.InspectionTaskEquipmentItem;
import com.juchao.upg.android.entity.MaintenaceTaskEquipmentItem;
import com.juchao.upg.android.entity.ResAccountTask;
import com.juchao.upg.android.entity.ResAccountTaskEquipment;
import com.juchao.upg.android.entity.ResAppUpdate;
import com.juchao.upg.android.entity.ResInspectionTask;
import com.juchao.upg.android.entity.ResInspectionTaskEquipment;
import com.juchao.upg.android.entity.ResLogin;
import com.juchao.upg.android.entity.ResMaintenaceTask;
import com.juchao.upg.android.entity.ResMaintenaceTaskEquipment;
import com.juchao.upg.android.entity.ResMessage;
import com.juchao.upg.android.entity.ResQuerySparePart;
import com.juchao.upg.android.util.ClientUtil;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.FileUtil;
import com.juchao.upg.android.util.JsonMapperUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NetAccessor {
	private static final String TAG = NetAccessor.class.getSimpleName();
	private static String THEME_LIST_URL;

	/**
	 * 登陆
	 * 
	 * @param mContext
	 * @param token
	 * @return
	 */
	public static ResLogin login(Context mContext, String userName,
			String userPwd, String mac) {

		ResLogin mResLogin = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("username", userName);
			params.put("password", userPwd);
			params.put("mac", mac);
			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl()
					+ "/mobileapp/login.action", params);

			if (!TextUtils.isEmpty(jsonString)) {
				mResLogin = JsonMapperUtils
						.toObject(jsonString, ResLogin.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResLogin;
	}

	/**
	 * 获取点检列表
	 * 
	 * @param mContext
	 * @param token
	 * @return
	 */
	public static ResInspectionTask getInspectionTaskList(Context mContext,
			String token) {

		ResInspectionTask mResInspectionTask = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);
			params.put("status", "3");

			// String jsonString = Data.task_list;
			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl()
					+ "/mobileapp/inspectionTask/list.action", params);

			if (!TextUtils.isEmpty(jsonString)) {

				mResInspectionTask = JsonMapperUtils.toObject(jsonString,
						ResInspectionTask.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResInspectionTask;
	}

	/**
	 * 下载点检任务
	 * 
	 * @param mContext
	 * @param token
	 * @param taskId
	 * @return
	 */
	public static ResInspectionTaskEquipment downloadInspectionTask(
			Context mContext, String token, long taskId,int style) {

		ResInspectionTaskEquipment mResInspectionTaskEquipment = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", taskId + "");
			params.put("token", token);
			params.put("status", style + "");

			// String jsonString = null;
			// if(taskId == 1){
			// jsonString = Data.down_task_1;
			// }else if(taskId == 2){
			// jsonString = Data.down_task_2;
			// }else if(taskId == 3){
			// jsonString = Data.down_task_3;
			// }
			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl()
					+ "/mobileapp/inspectionTask/load.action", params);

			if (!TextUtils.isEmpty(jsonString)) {
				mResInspectionTaskEquipment = JsonMapperUtils.toObject(
						jsonString, ResInspectionTaskEquipment.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResInspectionTaskEquipment;
	}

	/**
	 * 数据更新接口
	 * 
	 * @param mContext
	 * @param token
	 * @param type
	 *            数据结果的类名
	 * @param lastTime
	 *            增量更新时间
	 * @return
	 */
	public static <T> T updateData(Context mContext, String token, String type,
			String lastTime, Class<T> clazz) {

		T t = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);
			params.put("type", type);
			params.put("lastTime", lastTime);

			// String jsonString = null;
			// if(type.equals("EquipmentSpotcheck")){
			// jsonString = Data.BASE_EquipmentSpotcheck;
			// }else if(type.equals("Equipment")){
			// jsonString = Data.BASE_Equipment;
			// }else{
			// jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl() +
			// "/mobileapp/updateData.action", params);
			// }

			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl()
					+ "/mobileapp/updateData.action", params);

			if (!TextUtils.isEmpty(jsonString)) {
				t = JsonMapperUtils.toObject(jsonString, clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 应用更新检查接口
	 */
	public static ResAppUpdate getAppUpdateInfo(Context mContext) {

		ResAppUpdate mResAppUpdate = null;
		try {
			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl()
					+ "/mobileapp/getVersion.action", null);
			// String jsonString =
			// "{\"versionCode\":2,\"versionName\":\"1.2.1\",\"code\":0,\"log\":\"1、更新\\n2、修改\",\"msg\":\"操作成功\",\"url\":\"http://count.liqucn.com/d.php?id=79846&ArticleOS=Android&content_url=http://www.liqucn.com/rj/79846.shtml&down_url=kpa.moc.ncuqil_2.5_ncrennacsregnif.diordna.egnahckniht.ibom/nauqna/2102/daolpu/moc.ncuqil.elif//:ptth&Pink=0&from_type=web\"}";
			if (!TextUtils.isEmpty(jsonString)) {
				mResAppUpdate = JsonMapperUtils.toObject(jsonString,
						ResAppUpdate.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResAppUpdate;
	}

	/**
	 * 提交点检项目
	 * 
	 * @param mContext
	 * @param token
	 * @param type
	 *            数据结果的类名
	 * @param lastTime
	 *            增量更新时间
	 * @return
	 */
	public static int commitInspectionItemData(Context mContext, String token,
			InspectionTaskEquipmentItem inspectionItem) {

		// 上传图片
		try {
			if (inspectionItem != null
					&& !TextUtils.isEmpty(inspectionItem.imageNames)) {
				inspectionItem.imageIds = "";
				String[] mImageNames = inspectionItem.imageNames.split(",");
				for (int i = 0; i < mImageNames.length; i++) {
					File imageFile = new File(ClientUtil.getImageDir()
							+ File.separator + mImageNames[i]);
					System.out.println("image file: "+ imageFile);
					String responeStr = HttpPostFileUtils.postContentAndPic(
							mContext, HttpUtils.getBaseUrl()
									+ "/mobileapp/uploadImage.action", token,
							imageFile);

					Log.d(TAG, "upload image  responeStr : " + responeStr);

					if (!TextUtils.isEmpty(responeStr)) {
						JSONObject jsonObj = new JSONObject(responeStr);
						if (jsonObj != null && jsonObj.optInt("code") == 0) {
							String imageId = jsonObj.optString("id");

							if (i == mImageNames.length - 1) {
								inspectionItem.imageIds += imageId;
							} else {
								inspectionItem.imageIds += imageId + ",";
							}

						} else {
							return -1;
						}
					} else {
						return -1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		try {
			Log.d("begin submit Result:", new Date()+"");
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);
			params.put("id", inspectionItem.id == null ? "" : inspectionItem.id
					+ "");
			params.put("result", inspectionItem.result);
			params.put("costTime", inspectionItem.costTime + "");
			params.put("faultDescribe",
					inspectionItem.faultDescribe == null ? ""
							: inspectionItem.faultDescribe);
			params.put("imageIds", inspectionItem.imageIds == null ? ""
					: inspectionItem.imageIds);
			params.put("startTime", inspectionItem.startTime);
			params.put("endTime", inspectionItem.endTime);
			params.put("status", inspectionItem.style+"");

			String jsonString = HttpUtils.doPost(HttpUtils.getBaseUrl()
					+ "/mobileapp/inspectionTask/submitResult.action", params);

			Log.d("end submit Result:", new Date()+"");
			if (!TextUtils.isEmpty(jsonString)) {
				BaseResult result = JsonMapperUtils.toObject(jsonString,
						BaseResult.class);
				if (result != null) {
					return result.code;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static ResMessage findMsg(String token) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);

			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl()
					+ "/mobileapp/message/findMsg.action", params);

			if (!TextUtils.isEmpty(jsonString)) {
				ResMessage result = JsonMapperUtils.toObject(jsonString,
						ResMessage.class);
				return result;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ResMaintenaceTaskEquipment downloadMaintenaceTask(
			Context mContext, String token, long taskId) {
		// TODO Auto-generated method stub
		ResMaintenaceTaskEquipment mResMaintenaceTaskEquipment = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", taskId + "");
			params.put("token", token);

			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl()
					+ "/mobileapp/maintenaceTask/load.action", params);
			if (!TextUtils.isEmpty(jsonString)) {
				mResMaintenaceTaskEquipment = JsonMapperUtils.toObject(
						jsonString, ResMaintenaceTaskEquipment.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mResMaintenaceTaskEquipment;
	}

	public static ResMaintenaceTask getMaintenaceTaskList(Context mContext,
			String token) {
		ResMaintenaceTask mResMaintenaceTask = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);

			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl()
					+ "/mobileapp/maintenaceTask/list.action", params);
			// Toast.makeText(this, text, duration)
			if (!TextUtils.isEmpty(jsonString)) {
				mResMaintenaceTask = JsonMapperUtils.toObject(jsonString,
						ResMaintenaceTask.class);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResMaintenaceTask;
	}

	public static int commitMaintenaceItemData(Context mContext, String token,
			MaintenaceTaskEquipmentItem maintenaceItem) {

		// 上传图片
		try {
			if (maintenaceItem != null
					&& !TextUtils.isEmpty(maintenaceItem.imageNames)) {
				maintenaceItem.imageIds = "";
				String[] mImageNames = maintenaceItem.imageNames.split(",");
				for (int i = 0; i < mImageNames.length; i++) {
					File imageFile = new File(ClientUtil.getImageDir()
							+ File.separator + mImageNames[i]);
					String responeStr = HttpPostFileUtils.postContentAndPic(
							mContext, HttpUtils.getBaseUrl()
									+ "/mobileapp/uploadImage.action", token,
							imageFile);
					Log.d(TAG, "upload image responeStr : " + responeStr);
					if (!TextUtils.isEmpty(responeStr)) {
						JSONObject jsonObj = new JSONObject(responeStr);
						if (jsonObj != null && jsonObj.optInt("code") == 0) {
							String imageId = jsonObj.optString("id");
							if (i == mImageNames.length - 1) {
								maintenaceItem.imageIds += imageId;
							} else {
								maintenaceItem.imageIds += imageId + ",";
							}
						} else {
							return -1;
						}
					} else {
						return -1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		try {

			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);
			params.put("id", maintenaceItem.id == null ? "" : maintenaceItem.id
					+ "");
			// params.put("result", maintenaceItem.result);
			// params.put("costTime", maintenaceItem.costTime + "");
			params.put("startTime", maintenaceItem.startTime);
			params.put("endTime", maintenaceItem.endTime);
			params.put("describe", maintenaceItem.faultDescribe == null ? ""
					: maintenaceItem.faultDescribe);
			params.put("imageIds", maintenaceItem.imageIds == null ? ""
					: maintenaceItem.imageIds);
			// params.put("sparePartIds", maintenaceItem.terminalNumber);
			params.put("sparePartIds",
					maintenaceItem.terminalNumber == null ? ""
							: maintenaceItem.terminalNumber);
			String jsonString = "";
			if (maintenaceItem.state == 3) {
				jsonString = HttpUtils.doPost(HttpUtils.getBaseUrl()
						+ "/mobileapp/maintenaceTask" + "/submitResult.action",
						params);
			} else if (maintenaceItem.state == 4) {
				jsonString = HttpUtils.doPost(
						HttpUtils.getBaseUrl() + "/mobileapp/maintenaceTask"
								+ "/submitConfirm.action", params);
			}

			if (!TextUtils.isEmpty(jsonString)) {
				BaseResult result = JsonMapperUtils.toObject(jsonString,
						BaseResult.class);
				if (result != null) {
					return result.code;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static ResMaintenaceTask getMaintenaceEffectConfirmTaskList(
			Context context, String token) {
		ResMaintenaceTask mResMaintenaceTask = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);

			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl()
					+ "/mobileapp/maintenaceTask/listConfirm.action", params);

			if (!TextUtils.isEmpty(jsonString)) {
				mResMaintenaceTask = JsonMapperUtils.toObject(jsonString,
						ResMaintenaceTask.class);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResMaintenaceTask;
	}

	/**
	 * 效果确认任务下载接口需要修改：
	 * 
	 * @param mContext
	 * @param token
	 * @param taskId
	 * @return
	 */
	public static ResMaintenaceTaskEquipment downloadMaintenaceEffectConfirmTask(
			Context mContext, String token, long taskId) {
		ResMaintenaceTaskEquipment mResMaintenaceTaskEquipment = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", taskId + "");
			params.put("token", token);
			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl()
					+ "/mobileapp/maintenaceTask/loadConfirm.action", params);
			if (!TextUtils.isEmpty(jsonString)) {
				mResMaintenaceTaskEquipment = JsonMapperUtils.toObject(
						jsonString, ResMaintenaceTaskEquipment.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResMaintenaceTaskEquipment;
	}

	public static int confirmMaintenaceItemData(Context mContext, String token,
			MaintenaceTaskEquipmentItem maintenaceItem) {
		// 上传图片
		try {
			if (maintenaceItem != null
					&& !TextUtils.isEmpty(maintenaceItem.imageNames)) {
				maintenaceItem.imageIds = "";
				String[] mImageNames = maintenaceItem.imageNames.split(",");
				for (int i = 0; i < mImageNames.length; i++) {
					File imageFile = new File(ClientUtil.getImageDir()
							+ File.separator + mImageNames[i]);
					String responeStr = HttpPostFileUtils.postContentAndPic(
							mContext, HttpUtils.getBaseUrl()
									+ "/mobileapp/uploadImage.action", token,
							imageFile);
					Log.d(TAG, "upload image responeStr : " + responeStr);
					if (!TextUtils.isEmpty(responeStr)) {
						JSONObject jsonObj = new JSONObject(responeStr);
						if (jsonObj != null && jsonObj.optInt("code") == 0) {
							String imageId = jsonObj.optString("id");
							if (i == mImageNames.length - 1) {
								maintenaceItem.imageIds += imageId;
							} else {
								maintenaceItem.imageIds += imageId + ",";
							}
						} else {
							return -1;
						}
					} else {
						return -1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);
			params.put("id", maintenaceItem.id == null ? "" : maintenaceItem.id
					+ "");
			// params.put("result", maintenaceItem.result);
			// params.put("costTime", maintenaceItem.costTime + "");
			params.put("startTime", maintenaceItem.startTime);
			params.put("endTime", maintenaceItem.endTime);
			params.put("describe", maintenaceItem.faultDescribe == null ? ""
					: maintenaceItem.faultDescribe);
			params.put("imageIds", maintenaceItem.imageIds == null ? ""
					: maintenaceItem.imageIds);
			params.put("sparePartIds",
					maintenaceItem.terminalNumber == null ? ""
							: maintenaceItem.terminalNumber);

			String jsonString = HttpUtils.doPost(HttpUtils.getBaseUrl()
					+ "/mobileapp/maintenaceTask" + "/submitConfirm.action",
					params);
			if (!TextUtils.isEmpty(jsonString)) {
				BaseResult result = JsonMapperUtils.toObject(jsonString,
						BaseResult.class);
				if (result != null) {
					return result.code;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public static ResQuerySparePart querySparePart(Context mContext,String token,String equipmentId,String keyword){
		ResQuerySparePart result=new ResQuerySparePart();
		try{
			Map<String,String> params=new HashMap<String,String>();
			params.put("token", token);
			params.put("equid", equipmentId);
			params.put("keyword", keyword);
			
			String jsonString=HttpUtils.doPost(HttpUtils.getBaseUrl()
					+"/mobileapp/sparePart/search.action", params);
			if(!TextUtils.isEmpty(jsonString)){
				result=JsonMapperUtils.toObject(jsonString, ResQuerySparePart.class);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取盘点任务列表
	 * @param mContext
	 * @param token
	 * @return
	 */
	public static ResAccountTask getAccountTaskList(Context mContext,
			String token) {
		ResAccountTask mResAccountTask=null;
		try{
			Map<String,String> params=new HashMap<String,String>();
			params.put("token", token);
			String jsonString=HttpUtils.doGet(HttpUtils.getBaseUrl() + "/mobileapp/account/list.action",
					params);
			if(!TextUtils.isEmpty(jsonString)){
				mResAccountTask=JsonMapperUtils.toObject(jsonString,ResAccountTask.class);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return mResAccountTask;
	}

	public static ResAccountTaskEquipment downloadAccountTask(Context mContext,
			String token, long taskId) {
		ResAccountTaskEquipment entity = null;
		try{
			Map<String,String> params=new HashMap<String,String>();
			params.put("id", taskId+"");
			params.put("token", token);
			
			String jsonString = HttpUtils.doGet(HttpUtils.getBaseUrl() + "/mobileapp/account/load.action",
					params);
			if(!TextUtils.isEmpty(jsonString)){
				entity=JsonMapperUtils.toObject(jsonString, ResAccountTaskEquipment.class);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return entity;
	}

	public static int commitAccountItemData(Context mContext, String token,
			AccountEquipment item) {
				
		int result = -1;
		try {
			BaseResult baseResult=new BaseResult();
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);
			params.put("id", item.accountId + "");
	
			JSONObject json=new JSONObject();
			json.put("equipmentId", item.equipmentId);
			json.put("status", item.status);
			json.put("scanCodeTime", item.scanCodeTime);
			
			String resultJson=json.toString();
			params.put("equAccount", resultJson);
			
			String jsonString=HttpUtils.doPost(HttpUtils.getBaseUrl() +
					"/mobileapp/account/submitResult.action", params);
			if(!TextUtils.isEmpty(jsonString)){
				baseResult=JsonMapperUtils.toObject(jsonString, BaseResult.class);
				if(baseResult !=null){
					return  baseResult.code;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			result = -1;
		}
		return result;
	}
	
//	public static ResBaseImage downloadImages(Context context,String token){
//		ResBaseImage images = null;
//		try{
//			Map<String,String> params = new HashMap<String,String>();
//			params.put("token", token);
//			String jsonString=HttpUtils.doGet(HttpUtils.getBaseUrl()+"/mobileapp/BaseImage.action",
//					params);
//			if(!TextUtils.isEmpty(jsonString)){
//				images = JsonMapperUtils.toObject(jsonString, ResBaseImage.class);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return images;
//	}

	public static void DownloadBaseImage(Context mContext, String token,BaseImage image) {
		try{
			Map<String,String> params=new HashMap<String,String>();
			String url=image.url;
			String id=image.id;
			
			params.put("token", token);
			String curUrl="";
			
			if(image.id ==null || image.id.trim() == "0"){
				curUrl = HttpUtils.getBaseUrl() +"/" +url;
			}else{
				//curUrl = HttpUtils.getBaseUrl() + "/fileuploadCom/showPic.action?id=" +id;
				//curUrl = HttpUtils.getBaseUrl() + "fileuploadCom/readFile.action?id=" +id;
				curUrl= HttpUtils.getBaseUrl()  + "/mobileapp/downloadImage.action?id=" +id +"&token="+token; 
			}
				HttpUtils.downloadImage(curUrl,params,image);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void DownloadBaseEquipmentAttachment(Context mContext,
			String token, BaseEquipmentAttachment att) {
		try{
			String curUrl = HttpUtils.getBaseUrl() + "/mobileapp/downloadAttachment.action?id="+att.fileId+"&token="+token;
			
			HttpUtils.downloadAttachment(curUrl,att);
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}
    public static void DownloadPic(Context mContext,String token,String picId){

        try{
            File file;
            file = new File(FileUtil.getMaintenacePicDir(), picId+".jpg");
            if(file.exists()){
                return;
            }
            String url =HttpUtils.getBaseUrl() + "/mobileapp/downloadAttachment.action?id="+picId+"&token="+token;
            CustomHttpURLConnection.downloadFile(url,file);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}