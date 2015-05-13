package com.juchao.upg.android.util;

import android.os.Environment;

import java.io.File;

public class FileUtil {

	
	public static String getRootDir(){
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	public static String getDownDir(){
		String path = getRootDir() + "/903dt/video";
		
		File file = new File(path);
		if(!file.exists()){//判断文件夹目录是否存在  
			file.mkdirs();//如果不存在则创建  
         }  
		return file.getAbsolutePath();
	}
	
	public static String getImageDir(){
		String path = getRootDir() + "/903dt/cache";
		
		File file = new File(path);
		if(!file.exists()){//判断文件夹目录是否存在  
			file.mkdirs();//如果不存在则创建  
         }  
		return file.getAbsolutePath(); 
	}
	
	public static void delVoiceFile(String fileName){
		try{
			File file = new File(getDownDir()+ File.separator + fileName);
			file.deleteOnExit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void delVoiceDir(){
		File file = new File(getDownDir());
		if(file.isDirectory()){//判断文件夹目录是否存在  
			file.delete();
         }  
	}
	
	public static String getExplainDir(){
		String path= getRootDir() + "/upg/explain"  ;
		File file=new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	
	public static String getBaseImageDir(){
		String path = getRootDir() +"/upg/baseImage" ;
		File file=new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	
	public static boolean existExplainFile(long equipmentId,int type){
		
		File file = new File(getExplainDir() + File.separator + equipmentId + "_" +type + ".doc");
		File file1= new File(getExplainDir() + File.separator + equipmentId + "_" +type + ".pdf");
		
		if(!file.exists() && !file1.exists()){
			return false;
		}
		return true;
	}
	
	public static String getBaseEquipmentAttachmentDir(){
		String path = getRootDir()+"/upg/attachment";
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	public static String getMaintenacePicDir(){
        String path = getRootDir() +"/upg/maintenacePic";
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }
}
