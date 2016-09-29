package com.example.coolWeather.activity;

import com.example.coolWeather.fragment.CountyDetailFragment;
import com.example.coolWeather.fragment.CountyListFragment;
import com.example.greattest.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;

public class ManageCounty extends RootActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_county);
		showCoutyList();
	}

	public void showCoutyList(){
		Fragment showConty = new CountyListFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.id_main_layout, showConty);
		transaction.commit();
	}
	
	public void showGeography(String picCode, String picName){
		Fragment showGeoraphy = new CountyDetailFragment(picCode, picName);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.id_main_layout, showGeoraphy);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
