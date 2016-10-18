package com.example.coolWeather.activity;

import java.util.ArrayList;

import com.example.coolWeather.fragment.CountyDetailFragment;
import com.example.coolWeather.fragment.CountyListFragment;
import com.example.greattest.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;

public class SelectCounty extends RootActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_selecte_county);
		
		//切换碎片为地区选择碎片
		showCoutyList();
	}
	

	/*****************************
	 * 切换fragment为显示城市列表的fragment
	 *****************************/
	public void showCoutyList(){
		Fragment showConty = new CountyListFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.id_main_layout, showConty);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	/***********************************
	 * 切换fragment为显示城市信息的碎片
	 * @param name 装有城市详细名字的list
	 * 				list0=provinceName	
	 * 				list1=cityName
	 * 				list2=contyName
	 * 				list3=weatherCode
	 ***********************************/
	public void showGeography(ArrayList<String> name){
		Fragment showGeoraphy = new CountyDetailFragment(name);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.id_main_layout, showGeoraphy);		
		transaction.commit();
	}
}
