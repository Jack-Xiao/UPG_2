package com.juchao.upg.android.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juchao.upg.android.R;
import com.juchao.upg.android.ui.fragment.InspectionFragment;
import com.juchao.upg.android.ui.fragment.MaintenaceFragment;
import com.juchao.upg.android.ui.fragment.QueryFragment;
import com.juchao.upg.android.util.Constants;
import com.juchao.upg.android.util.DefaultShared;
import com.juchao.upg.android.util.IntentUtil;
import com.juchao.upg.android.view.CustomDialog;
import com.juchao.upg.android.view.PopupWindowUtil;
import com.juchao.upg.android.view.TabView;

public class MainActivity extends FragmentActivity implements OnClickListener{

	private FragmentManager fragmentManager;  
    private TabView tab[] = new TabView[4];
    private String[] tabName = {"维修" ,"点检" , "盘点" ,"查询"};
    private int[] imgGrayRes = { R.drawable.tab_maintenance_gray ,R.drawable.tab_inspection_gray
    		,R.drawable.tab_inventory_gray ,R.drawable.tab_query_gray 
    };
    private int[] imgWhiteRes = { R.drawable.tab_maintenance_white ,R.drawable.tab_inspection_white
    		,R.drawable.tab_inventory_white ,R.drawable.tab_query_white 
    };
    
    
    private TextView leftTitle,userInfo;
    private ImageButton rightBtn;
    private RelativeLayout headerLayout;
    
    private List<Fragment> list;// 声明一个list集合存放Fragment(数据源)
    private int currentPage = 1;
    private ViewPager viewPager;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_main);
		
        fragmentManager = getSupportFragmentManager();
        /*添加ViewPager  实现滑动切换*/
		initView();
        
        headerLayout = (RelativeLayout) findViewById(R.id.header_layout); 
        leftTitle = (TextView) findViewById(R.id.leftTitle); 
        rightBtn = (ImageButton) findViewById(R.id.rightBtn);
        userInfo=(TextView)findViewById(R.id.userinfo);
        //--++ @20141030 
        //leftTitle.setText("点检");
        
        userInfo.setText(DefaultShared.getString(Constants.KEY_USER_NAME, ""));
        
        tab[0] = (TabView) findViewById(R.id.tab0); 
        tab[1] = (TabView) findViewById(R.id.tab1); 
        tab[2] = (TabView) findViewById(R.id.tab2); 
        tab[3] = (TabView) findViewById(R.id.tab3); 
        
        tab[0].setOnClickListener(this);
        tab[1].setOnClickListener(this);
        tab[2].setOnClickListener(this);
        tab[3].setOnClickListener(this);
        
        rightBtn.setOnClickListener(this);
        //onTabChange(1); 默认初始Tab
        //init select tab fragment.
        leftTitle.setText(tabName[currentPage].toString());
        onTabChange(currentPage);
        
		long lastUpdatetime = DefaultShared.getLong(Constants.KEY_LAST_UPDATE_DATA_TIME, 0);
        if(lastUpdatetime <= 0){
        	CustomDialog.Builder builder = new CustomDialog.Builder(this);  
	        builder.setMessage("请先更新基础数据");
	        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {  
	            	dialog.dismiss(); 
	            	IntentUtil.startActivity(MainActivity.this, UpdateDataActivity.class);
	            }
	        });
            builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {  
                public void onClick(DialogInterface dialog, int which) {  
                    dialog.dismiss();  
                }
            });
	        builder.create().show();
        }
	}
	
	private void initView() {
		viewPager=(ViewPager)findViewById(R.id.viewpager);
		list = new ArrayList<Fragment>();
    	Fragment fragmentMaintenace= (Fragment) new MaintenaceFragment();
         
        Fragment fragmentInspection = new InspectionFragment();  
         
    	Fragment fragmentAccount = new com.juchao.upg.android.ui.fragment.AccountFragment(); 
          
        Fragment fragmentQuery = new QueryFragment();
        
		list.add(fragmentMaintenace);
		list.add(fragmentInspection);
		list.add(fragmentAccount);
		list.add(fragmentQuery);
		
		FragmentPagerAdapter adapter = new FragmentPagerAdapter(fragmentManager) {
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return list.size();
			}
			
			@Override
			public Fragment getItem(int position) {
				
				return list.get(position);
			}
		};
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				onTabChange(arg0);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
				
			}
		});
	}


	private  void onTabChange(int index){
		for(int i = 0 ; i < tab.length ; i++){
			if(index == i){
				tab[i].setBackgroundColor(Color.parseColor("#0078FF"));
				tab[i].setTextAndColor(tabName[i] ,Color.parseColor("#FFFFFF"));
				tab[i].setIcon(imgWhiteRes[i]);
			}else{
				tab[i].setBackgroundColor(Color.parseColor("#00FFFFFF"));
				tab[i].setTextAndColor(tabName[i],Color.parseColor("#666666"));
				tab[i].setIcon(imgGrayRes[i]);
			}
		}
		leftTitle.setText(tabName[index]);
		currentPage = index;
		
		viewPager.setCurrentItem(index,false);
		
//		FragmentTransaction transaction = fragmentManager.beginTransaction();  
        Fragment fragment = FragmentFactory.getInstanceByIndex(index);  
//        transaction.replace(R.id.content, fragment);  
//        transaction.commit(); 
      
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.rightBtn :
			PopupWindowUtil.getInstance().showActionWindow(MainActivity.this, headerLayout);
		break;
		
		case R.id.tab0:
			 
			onTabChange(0);
			break;
		case R.id.tab1:
			 
			onTabChange(1);
			break;
		case R.id.tab2:
			 
			onTabChange(2);
			break;
		case R.id.tab3:
			
			onTabChange(3);
			break;
		}
	}


	@Override
	public void onBackPressed() {
		exitApp();
	}
	
	// 客户端退出
	private long startexit = 0;
	private int exitAppTimes = 0; 

	public void exitApp() {
		if (System.currentTimeMillis() - startexit > 3000) {
			exitAppTimes = 0;
		}
		++exitAppTimes;
		if (exitAppTimes == 1) {
			startexit = System.currentTimeMillis();
			Toast.makeText(MainActivity.this, "再按一次退出", 3000).show();
		} else if (exitAppTimes == 2) {
			IntentUtil.startActivity(this, LoginActivity.class);
			finish();
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		 try {
	            Object service = getSystemService("statusbar");
	            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
	            Method test = statusbarManager.getMethod("collapse");
	            test.invoke(service);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	}
	

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) { 
//			exitApp();
//		}
//		return super.onKeyDown(keyCode, event);
//	}
}
