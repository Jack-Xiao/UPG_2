package com.juchao.upg.android.task;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.juchao.upg.android.db.BaseDataDao;
import com.juchao.upg.android.entity.BaseCategory;
import com.juchao.upg.android.entity.BaseCommonProblem;
import com.juchao.upg.android.entity.BaseEquipment;
import com.juchao.upg.android.entity.BaseEquipmentAttachment;
import com.juchao.upg.android.entity.BaseEquipmentSpotcheck;
import com.juchao.upg.android.entity.BaseImage;
import com.juchao.upg.android.entity.BaseOperationNotice;
import com.juchao.upg.android.entity.BaseSparePart;
import com.juchao.upg.android.entity.EquipmentSparePart;
import com.juchao.upg.android.entity.ResBaseCategory;
import com.juchao.upg.android.entity.ResBaseCommonProblem;
import com.juchao.upg.android.entity.ResBaseEquipment;
import com.juchao.upg.android.entity.ResBaseEquipmentAttachment;
import com.juchao.upg.android.entity.ResBaseEquipmentSpotcheck;
import com.juchao.upg.android.entity.ResBaseImage;
import com.juchao.upg.android.entity.ResBaseOperationNotice;
import com.juchao.upg.android.entity.ResBaseSparePart;
import com.juchao.upg.android.entity.ResEquipCheckPicture;
import com.juchao.upg.android.entity.ResEquipmentSparePart;
import com.juchao.upg.android.net.NetAccessor;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;

/**
 * GetInspectionDownloadListTask
 * 
 */
public class UpdateDataTask extends AsyncTask<String, String, Integer> {
	private static final String TAG = UpdateDataTask.class.getSimpleName();
	private Context mContext;
	private ProgressDialog mDialog;
	private TaskCallBack mCallBack;
	private int resultCode;
	
	public UpdateDataTask(Context context) {
		this(context, null);
	}
	
	public UpdateDataTask(Context context, TaskCallBack callback) {
		this.mContext = context;
		this.mCallBack = callback;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		resultCode = Constants.REQUEST_SUCCESS;
//		mDialog = ProgressDialog.show(mContext, null, "加载中...", true, true);
		
		this.mCallBack.preUpdate();
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
		String token = DefaultShared.getString(Constants.KEY_TOKEN, "");
		
		//下载设备数据																	   Equipment
		ResBaseEquipment mResBaseEquipment = NetAccessor.updateData(mContext, token , "Equipment" ,"" ,ResBaseEquipment.class);
		if(mResBaseEquipment != null && mResBaseEquipment.code == 0 && 
				mResBaseEquipment.data != null && mResBaseEquipment.data.size() > 0){
			publishProgress("下载设备数据..");
			BaseDataDao mDao = new BaseDataDao(mContext);
			boolean isDelSuccess = false;
			isDelSuccess = mDao.deleteBaseEquipment();
			Log.d(TAG, "delete table Equipment  ,  isDelSuccess : " + isDelSuccess);
			
			long insertNum = 0;
			for(BaseEquipment entity : mResBaseEquipment.data){
				insertNum = mDao.insertBaseEquipment(entity);
			}
			Log.d(TAG, "delete table Equipment  ,  insertNum : " + insertNum + " , data.size() : " + mResBaseEquipment.data.size());
			mDao.closeDB();
			
			if(insertNum <= 0){
				resultCode = Constants.REQUEST_FAIL;
				return resultCode;
			}
		}else{
			publishProgress("下载设备数据失败..");
			resultCode = Constants.REQUEST_FAIL;
			return resultCode;
		}
		//publichProgress();
		//下载备件
		ResBaseSparePart mResBaseSparePart = NetAccessor.updateData(mContext, token , "SparePart" ,"" ,ResBaseSparePart.class);
		if(mResBaseSparePart != null && mResBaseSparePart.code == 0 && 
				mResBaseSparePart.data != null && mResBaseSparePart.data.size() > 0){
			publishProgress("下载备件数据..");
			BaseDataDao mDao = new BaseDataDao(mContext);
			boolean isDelSuccess = false;
			isDelSuccess = mDao.deleteBaseSparePart();
			Log.d(TAG, "delete table SparePart  ,  isDelSuccess : " + isDelSuccess);
			
			long insertNum = 0;
			for(BaseSparePart entity : mResBaseSparePart.data){
				insertNum = mDao.insertBaseSparePart(entity);
			}
			Log.d(TAG, "delete table SparePart  ,  insertNum : " + insertNum + " , data.size() : " + mResBaseSparePart.data.size());
			mDao.closeDB();
			
			if(insertNum <= 0){
				resultCode = Constants.REQUEST_FAIL;
				return resultCode;
			}
		}else{
//			resultCode = Constants.REQUEST_FAIL;
//			return resultCode;
		}
		//下载备品备件关联表    relation
		ResEquipmentSparePart mResEquipmentSparePart=NetAccessor.updateData(mContext, token, "EquipmentSparePart", "",ResEquipmentSparePart.class);
		if(mResEquipmentSparePart !=null && mResEquipmentSparePart.code == 0 
				&& mResEquipmentSparePart.data !=null && mResEquipmentSparePart.data.size() > 0){
			BaseDataDao mDao = new BaseDataDao(mContext);
			boolean isDelSuccess = false;
			isDelSuccess = mDao.deleteEquipmentSparePartRelation();
			Log.d(TAG, "delete table equipment spare part relation , isDelSuccess : " + isDelSuccess);		
			long insertNum = 0 ;
			for(EquipmentSparePart entity : mResEquipmentSparePart.data){
				insertNum=mDao.insertEquipmentSparePartRelation(entity);
			}
			mDao.closeDB();
		}
		
		//下载通用分类表
		ResBaseCategory mResBaseCategory =NetAccessor.updateData(mContext, token, "Category", "", ResBaseCategory.class);
		if(mResBaseCategory !=null && mResBaseCategory.code ==0
				&& mResBaseCategory.data !=null && mResBaseCategory.data.size() > 0){
			publishProgress("下载分类数据..");
			BaseDataDao mDao = new BaseDataDao(mContext);
			boolean isDelSuccess = false;
			isDelSuccess = mDao.deleteBaseCategory();
			Log.d(TAG, "delete table equipment spare part relation , isDelSuccess : " + isDelSuccess);		
			long insertNum = 0 ;
			for(BaseCategory entity : mResBaseCategory.data){
				insertNum=mDao.insertBaseCategory(entity);
			}
			mDao.closeDB();
		}
		
		//下载设备项目
		ResBaseEquipmentSpotcheck mResBaseEquipmentSpotcheck = NetAccessor.updateData(mContext, token , "EquipmentSpotcheck" ,"" ,ResBaseEquipmentSpotcheck.class);
		if(mResBaseEquipmentSpotcheck != null && mResBaseEquipmentSpotcheck.code == 0 && 
				mResBaseEquipmentSpotcheck.data != null && mResBaseEquipmentSpotcheck.data.size() > 0){
			publishProgress("下载项目数据..");
			BaseDataDao mDao = new BaseDataDao(mContext);
			boolean isDelSuccess = false;
			isDelSuccess = mDao.deleteBaseEquipmentSpotcheck();
			Log.d(TAG, "delete table EquipmentSpotcheck  ,  isDelSuccess : " + isDelSuccess);
			
			long insertNum = 0;
			for(BaseEquipmentSpotcheck entity : mResBaseEquipmentSpotcheck.data){
				insertNum = mDao.insertBaseEquipmentSpotcheck(entity);
			}
			Log.d(TAG, "delete table EquipmentSpotcheck  ,  insertNum : " + insertNum + " , data.size() : " + mResBaseEquipmentSpotcheck.data.size());
			mDao.closeDB();
			
			if(insertNum <= 0){
				resultCode = Constants.REQUEST_FAIL;
				return resultCode;
			}
		}else{
			resultCode = Constants.REQUEST_FAIL;
			return resultCode;
		}
		
		//下载项目图片资料
		ResEquipCheckPicture mResEquipCheckPicture=NetAccessor.updateData(mContext, token, "EquipCheckpicture", "", ResEquipCheckPicture.class);
		if(mResEquipCheckPicture != null && mResEquipCheckPicture.code == 0 && 
				mResEquipCheckPicture.data != null && mResEquipCheckPicture.data.size() > 0){
			publishProgress("下载项目图片数据..");
			BaseDataDao mDao = new BaseDataDao(mContext);
			boolean isDelSuccess = false;
			isDelSuccess = mDao.deleteEquipCheckPicture();
			Log.d(TAG, "delete table EquipCheckPicture  ,  isDelSuccess : " + isDelSuccess);
			
			long insertNum = 0;
			for(com.juchao.upg.android.entity.EquipCheckPicture entity : mResEquipCheckPicture.data){
				insertNum = mDao.insertBaseEquipCheckPicture(entity);
				mDao.insertBaseImageId(entity); //插入基础图片
			}
			Log.d(TAG, "delete table EquipCheckPicture  ,  insertNum : " + insertNum + " , data.size() : " + mResEquipCheckPicture.data.size());
			mDao.closeDB();
			
		}else{
			
		}
		
		//下载常见问题
		ResBaseCommonProblem mResBaseCommonProblem = NetAccessor.updateData(mContext, token , "CommonProblem" ,"" ,ResBaseCommonProblem.class);
		if(mResBaseCommonProblem != null && mResBaseCommonProblem.code == 0 && 
				mResBaseCommonProblem.data != null && mResBaseCommonProblem.data.size() > 0){
			publishProgress("下载常见问题数据..");
			BaseDataDao mDao = new BaseDataDao(mContext);
			boolean isDelSuccess = false;
			isDelSuccess = mDao.deleteBaseCommonProblem();
			Log.d(TAG, "delete table CommonProblem  ,  isDelSuccess : " + isDelSuccess);
			
			long insertNum = 0;
			for(BaseCommonProblem entity : mResBaseCommonProblem.data){
				insertNum = mDao.insertBaseCommonProblem(entity);
			}
			Log.d(TAG, "delete table CommonProblem  ,  insertNum : " + insertNum + " , data.size() : " + mResBaseCommonProblem.data.size());
			mDao.closeDB();
			
			if(insertNum <= 0){
				resultCode = Constants.REQUEST_FAIL;
				return resultCode;
			}
		}else{
			//常见问题 可能为空 @20141118
			/*resultCode = Constants.REQUEST_FAIL;
			return resultCode;*/
		}
		
		//基础数据图片
		ResBaseImage mResBaseImage = NetAccessor.updateData(mContext, token, "BaseImage", "",ResBaseImage.class);
		if(mResBaseImage !=null && mResBaseImage.code == 0 &&
				mResBaseImage.data !=null && mResBaseImage.data.size() > 0){
			publishProgress("下载图片数据..");
			BaseDataDao mDao=new BaseDataDao(mContext);
			boolean isDelSuccess = false;
			isDelSuccess = mDao.deleteBaseImage();
			Log.d(TAG, "delete table downloadImage , isDelSuccess : " + isDelSuccess);
			long insertNum = 0;
			for(BaseImage image : mResBaseImage.data){
				insertNum = mDao.insertBaseImage(image);
			}
			List<BaseImage> list=mDao.queryBaseImage();
			for(BaseImage image :list){
				
				NetAccessor.DownloadBaseImage(mContext,token,image);
			} 
			mDao.closeDB();
			Log.d(TAG, "delete table CommonProblem  ,  insertNum : " + insertNum + " , data.size() : " + mResBaseCommonProblem.data.size());
			
		}else{
			//图片为空
		}
		
		//下载操作需知
		ResBaseOperationNotice mResBaseOperationNotice = NetAccessor.updateData(mContext, token, "BaseOperationNotice", "", ResBaseOperationNotice.class);
		if(mResBaseOperationNotice !=null && mResBaseOperationNotice.code == 0 && mResBaseOperationNotice.data !=null
				 && mResBaseOperationNotice.data.size() > 0){
			publishProgress("下载操作需知数据..");
			BaseDataDao mDao=new BaseDataDao(mContext);
			boolean isDelSuccess = false;
			isDelSuccess = mDao.deleteBaseOperationNotice();
			long insertNum = 0;
			for(BaseOperationNotice notice : mResBaseOperationNotice.data){
				insertNum = mDao.insertBaseOperationNotice(notice);
			}
			mDao.closeDB();
		}
		//附件下载
		ResBaseEquipmentAttachment mResBaseEquipmentAttachment = NetAccessor.updateData(mContext, token, "BaseEquipmentAttachment", "", ResBaseEquipmentAttachment.class);
		if(mResBaseEquipmentAttachment !=null && mResBaseEquipmentAttachment.code == 0 && mResBaseEquipmentAttachment.data !=null
				&& mResBaseEquipmentAttachment.data.size() > 0){
			publishProgress("下载设备附件数据..");
			BaseDataDao mDao = new BaseDataDao(mContext);
			mDao.deleteBaseEquipmentAttachment();
			for(BaseEquipmentAttachment attachment : mResBaseEquipmentAttachment.data){
				mDao.insertBaseEquipmentAttachment(attachment);
			}
			List<BaseEquipmentAttachment> list = mDao.queryBaseEquipmentAttachment();
			for(BaseEquipmentAttachment att : list){
				NetAccessor.DownloadBaseEquipmentAttachment(mContext,token,att);
			}
			mDao.closeDB();
		}	
		
		//更新基础数据时，检测维修任务设备名称
		BaseDataDao mDao = new BaseDataDao(mContext);
		mDao.checkEmptyMaintenaceEquipmentInfo();
		//更新基础数据时， 检测点检任务设备及项目名称
		mDao.checkEmptyInspectionEquipmenInfo();
		//
		mDao.checkEmptyInspectionProjectInfo();
		mDao.closeDB();
		
		return resultCode;
	}
	
	
//    protected void onProgressUpdate(Progress... values) {
//    }
	
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		try {
			if (!((Activity)mContext).isFinishing() && mDialog != null && mDialog.isShowing()) {
				mDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(mCallBack != null) {
			if(result == Constants.REQUEST_SUCCESS){
				mCallBack.callBackResult(0 , "更新成功");
			}else{
				mCallBack.callBackResult(1 ,"更新失败");
			}
		}
	}
	
	@Override
	public void onProgressUpdate(String... values){
		super.onProgressUpdate(values);
		mCallBack.progressUpdate(values[0]);
	}
	
	public interface TaskCallBack {
		public void preUpdate();
		public void callBackResult(int code ,String resultMsg);
		public void progressUpdate(String values);
	}
}
