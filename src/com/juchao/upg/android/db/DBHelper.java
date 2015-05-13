package com.juchao.upg.android.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DBName = "upg.db"; // 数据库名称
	private static int DBVersion = 1; // 数据库版本号

	public static final String Inspection_Task_Table_Name = "InspectionTaskTable"; // 表名
	public static final String Inspection_Equipment_Table_Name = "InspectionEquipmentTable"; // 表名
	public static final String Inspection_Item_Table_Name = "InspectionItemTable"; // 表名

	public static final String Base_Equipment_Table_Name = "BaseEquipmentTable"; // 表名:设备数据
	public static final String Base_SparePart_Table_Name = "BaseSparePartTable"; // 表名:备件数据
	public static final String Base_Equipment_Spotcheck_Table_Name = "BaseEquipmentSpotcheckTable"; // 表名:设备点检项目数据
	public static final String Base_Common_Problem_Table_Name = "BaseCommonProblemTable"; // 表名:常见问题数据
	public static final String Base_Equipment_SparePart_Table_Name = "BaseEquipmentSparePartTable"; // 表名：设备备件表（对应关系）
	public static final String Base_Category_Table_Name = "BaseCategoryTable"; // 表名：通常分类

	// @20141024 Xiao-Jiang
	public static final String Maintenace_Task_Table_Name = "MaintenaceTaskTable";//
	public static final String Maintenace_Equipment_Table_Name = "MaintenaceEquipmentTable";
	public static final String Maintenace_Item_Table_Name = "MaintenaceEquipmentProblemTable";

	// 盘点
	public static final String Account_Task_Table_Name = "AccountTaskTable";
	public static final String Account_Equipment_Table_Name = "AccountEquipmentTable";
	
	//图片管理 table
	public static final String Base_Image_Table_Name="BaseImageTable";
	//操作需知
	public static final String Base_Operation_Notice="BaseOperationNoticeTable";
	//点检项目图片
	public static final String Base_Equip_Check_Picture="EquipCheckpictureTable";
	//基础设备点检、维修、使用说明
	public static final String Base_Equip_Attachment="BaseEquipAttachmentTable";
	

	/**
	 * public static final String
	 * Base_Maintenace_Equipment_Table_Name="BaseMaintenaceEquipmentTable";
	 * public static final String
	 * Base_Maintenace_SparePart_Table_Name="BaseMaintenaceSparePartTable";
	 * public static final String
	 * Base_Maintenace_Equipment_Spotcheck_Table_Name=
	 * "BaseMaintenaceEquipmentSpotcheckTable"; public static final String
	 * Base_Maintenace_Common_Problem_Table_Name
	 * ="BaseMaintenaceCommonProblemTable";
	 */

	public DBHelper(Context context) {
		super(context, DBName, null, DBVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {

			// --------------------点检数据表--------------------
			String createInspectionTaskTable = "CREATE TABLE "
					+ Inspection_Task_Table_Name
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT, taskName NTEXT,startTime NTEXT, endTime NTEXT, isDownloaded NTEXT, state INTEGER ,userId NTEXT,style NTEXT,"
					+ " type NTEXT)";
			db.execSQL(createInspectionTaskTable);

			String createEquipmentTable = "CREATE TABLE "
					+ Inspection_Equipment_Table_Name
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT, inspectionTaskId NTEXT,equipmentId NTEXT,equipmentName NTEXT , type NTEXT"
					+ ", inspectionState INTEGER, timeList INTEGER,userId NTEXT,style NTEXT)";
			db.execSQL(createEquipmentTable);

			String createEquipmentItemTable = "CREATE TABLE "
					+ Inspection_Item_Table_Name
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT, inspectionTaskEquipmentId NTEXT,equipmentSpotcheckId NTEXT, equipmentSpotcheckName NTEXT,"
					+ " type NTEXT, result NTEXT, faultDescribe NTEXT , state INTEGER , imageNames NTEXT , costTime NTEXT ,startTime NTEXT , endTime NTEXT, userId NTEXT,inspectionTaskId NTEXT,style NTEXT)";
			db.execSQL(createEquipmentItemTable);
			
			// -------------------维修数据表--------------------
			String createMaintenaceTaskTable = "CREATE TABLE "
					+ Maintenace_Task_Table_Name
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT, taskName NTEXT,startTime NTEXT, endTime NTEXT, isDownloaded NTEXT, state INTEGER , userId NTEXT"
					+ ", taskState NTEXT,planId NTEXT,planName NTEXT)";
			db.execSQL(createMaintenaceTaskTable);

			String createMaintenaceEquipmentTable = "CREATE TABLE "
					+ Maintenace_Equipment_Table_Name
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT, maintenaceTaskId NTEXT,equipmentId NTEXT,equipmentName NTEXT,type NTEXT"
					+ ", maintenaceState INTEGER, timeList INTEGER,userId NTEXT)";
			db.execSQL(createMaintenaceEquipmentTable);

			String createMaintenaceEquipmentItemTable = "CREATE TABLE "
					+ Maintenace_Item_Table_Name
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT,maintenaceTaskId NTEXT, problemName NTEXT, maintenaceTaskEquipmentId NTEXT,"
					+ " type NTEXT, result NTEXT, problemDescribe NTEXT , state INTEGER , imageNames NTEXT , costTime NTEXT ,startTime NTEXT , endTime NTEXT"
					+ ",uploadState INTEGER,sparePartIds NTEXT,userId NTEXT,proDesc NTEXT,proSuggestion NTEXT,proMeasure NTEXT,proPicId NTEXT,faultSchemeType NTEXT)";
			db.execSQL(createMaintenaceEquipmentItemTable);
			
			// ----------------- 基础数据 ------------------
			
			String createBaseEquipmentTable = "CREATE TABLE "
					+ Base_Equipment_Table_Name
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT, k3NO NTEXT, equipmentName NTEXT ,  pEquipid NTEXT , dutyOrgid NTEXT "
					+ ",dutyUserid NTEXT ,modelNO NTEXT ,manufacturer NTEXT ,mfgNO NTEXT , supplier NTEXT ,purchaseDate NTEXT , useStateid NTEXT "
					+ ", equipmentTypeid NTEXT , storage NTEXT , remarks NTEXT , maintainNotice NTEXT ,managementOrgNum NTEXT , "
					+ "equipmentAmount INTEGER , unitsid NTEXT , updateTime NTEXT ,delMark INTEGER,equNum NTEXT)";
			db.execSQL(createBaseEquipmentTable);

			String createBaseSparePartTable = "CREATE TABLE "
					+ Base_SparePart_Table_Name
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT, name NTEXT, modelNo NTEXT, replaceModelNo NTEXT,makeCompany NTEXT,"
					+ "provider NTEXT,maxStore INTEGER,minStore INTEGER,currentStore INTEGER,goodsDate INTEGER,storeAddr NTEXT,updateTime NTEXT,"
					+ "delMark INTEGER,description NTEXT)";
			db.execSQL(createBaseSparePartTable);

			String createBaseEquipmentSpotcheckTable = "CREATE TABLE "
					+ Base_Equipment_Spotcheck_Table_Name
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT , name NTEXT, checkGroupmark NTEXT, projectTypeid NTEXT, projectNO NTEXT,"
					+ "checkType  INTEGER, iluLv INTEGER,checkMethod NTEXT, judgementStandard NTEXT, checkedEquipId NTEXT, updateTime NTEXT, delMark INTEGER,costPlanTime NTEXT)";
			db.execSQL(createBaseEquipmentSpotcheckTable);

			String createBaseCommonProblemTable = "CREATE TABLE "
					+ Base_Common_Problem_Table_Name
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT , equipmentId NTEXT , equipmentSpotcheckId NTEXT , faultDescribe NTEXT , createTime NTEXT , updateTime NTEXT)";
			db.execSQL(createBaseCommonProblemTable);

			// 设备与备品备件关系表
			String createBaseEquipmentSparePartTable = "CREATE TABLE "
					+ Base_Equipment_SparePart_Table_Name
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, id NTEXT, equid NTEXT, spapartid NTEXT)";
			db.execSQL(createBaseEquipmentSparePartTable);

			// 通常分类
			String createBaseCategoryTable = "CREATE TABLE "
					+ Base_Category_Table_Name
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT, name NTEXT,parentid NTEXT)";
			db.execSQL(createBaseCategoryTable);

			// 盘点
			String createAccountTaskTable = "CREATE TABLE "
					+ Account_Task_Table_Name
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT,name NTEXT,status NTEXT,startTime NTEXT,endTime NTEXT,isDownloaded NTEXT,range NTEXT,total NTEXT,userId NTEXT,taskStatus NTEXT)";
			db.execSQL(createAccountTaskTable);
			
			String createAccountEquipmentTable = "CREATE TABLE "
					+ Account_Equipment_Table_Name
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT,accountId NTEXT,equipmentId NTEXT,"
					+ "assetCode NTEXT,status NTEXT,scanCodeTime NTEXT,userId NTEXT,uploadStatus NTEXT,equNum NTEXT)";
			db.execSQL(createAccountEquipmentTable);
			
			//断点续传表
			String createDownlog="CREATE TABLE IF NOT EXISTS filedownlog (id integer primary key autoincrement, " +
					"downpath varchar(100), threadid INTEGER, downlength INTEGER)";
			db.execSQL(createDownlog);
			
			//下载图片
			String createImageTable="CREATE TABLE IF NOT EXISTS " + Base_Image_Table_Name +
					"(_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT,url NTEXT)";
			db.execSQL(createImageTable);
			
			//操作需知
			String createOperationNotice="CREATE TABLE IF NOT EXISTS " + Base_Operation_Notice +
					"(_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT,equipmentId NTEXT, type int,notice NTEXT)";
			db.execSQL(createOperationNotice);
			
			//点检项目图片
			String createEquipCheckPicture = "CREATE TABLE IF NOT EXISTS " + Base_Equip_Check_Picture +
					"(_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT,pictureId NTEXT,checksubjectId NTEXT)";
			db.execSQL(createEquipCheckPicture);
			
			//设备使用说明
			String createBaseEquipAttachment = "CREATE TABLE IF NOT EXISTS " + Base_Equip_Attachment +
					"(_id INTEGER PRIMARY KEY AUTOINCREMENT,id NTEXT,fileName NTEXT,equipId NTEXT,fileId NTEXT)";
			db.execSQL(createBaseEquipAttachment);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + Inspection_Task_Table_Name);
			db.execSQL("DROP TABLE IF EXISTS "
					+ Inspection_Equipment_Table_Name);
			db.execSQL("DROP TABLE IF EXISTS " + Inspection_Item_Table_Name);

			// 维修
			db.execSQL("DROP TABLE IF EXISTS " + Maintenace_Task_Table_Name);
			db.execSQL("DROP TABLE IF EXISTS "
					+ Maintenace_Equipment_Table_Name);
			db.execSQL("DROP TABLE IF EXISTS " + Maintenace_Item_Table_Name);

			db.execSQL("DROP TABLE IF EXISTS " + Base_Equipment_Table_Name);
			db.execSQL("DROP TABLE IF EXISTS " + Base_SparePart_Table_Name);
			db.execSQL("DROP TABLE IF EXISTS "
					+ Base_Equipment_Spotcheck_Table_Name);
			db.execSQL("DROP TABLE IF EXISTS " + Base_Common_Problem_Table_Name);
			db.execSQL("DROP TABLE IF EXISTS "
					+ Base_Equipment_SparePart_Table_Name); // @20141208
			
			db.execSQL("DROP TABLE IF EXISTS "
					+ Account_Task_Table_Name);
			
			db.execSQL("DROP TABLE IF EXISTS "
					+ Account_Equipment_Table_Name);
			
			db.execSQL("DROP TABLE IF FXISTS "
					+ Base_Image_Table_Name);
			
			db.execSQL("DROP TABLE IF EXISTS "
					+ Base_Equip_Attachment);
			onCreate(db);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
