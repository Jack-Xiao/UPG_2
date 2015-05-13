package com.juchao.upg.android.ui;

import android.support.v4.app.Fragment;

import com.juchao.upg.android.ui.fragment.Fragment3;
import com.juchao.upg.android.ui.fragment.InspectionFragment;
import com.juchao.upg.android.ui.fragment.MaintenaceFragment;
import com.juchao.upg.android.ui.fragment.QueryFragment;

public class FragmentFactory {

	public static Fragment getInstanceByIndex(int index) {  
        Fragment fragment = null;  
        switch (index) {  
            case 0:  
                //fragment = new Fragment1();
            	fragment= (Fragment) new MaintenaceFragment();
                break;  
            case 1:  
                fragment = new InspectionFragment();  
                break;  
            case 2:   
                //fragment = new Fragment3();
            	fragment = new com.juchao.upg.android.ui.fragment.AccountFragment(); 
                break;  
            case 3:  
                fragment = new QueryFragment();  
                break;  
           
        }  
        return fragment;  
    }  
}
