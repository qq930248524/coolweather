package com.example.coolWeather.fragment;

import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.List;

import com.example.coolWeather.activity.SelectCounty;
import com.example.coolWeather.db.CoolWeatherDB;
import com.example.coolWeather.model.StarCounty;
import com.example.coolWeather.util.HttpCallbackListener;
import com.example.coolWeather.util.HttpUtil;
import com.example.coolWeather.util.Utility;
import com.example.greattest.R;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CountyDetailFragment extends Fragment implements OnClickListener{
	private View myself;	
	private Boolean STAR_FLAG;
	private ArrayList<String> name;
	private CoolWeatherDB coolWeatherDb;
	
	//view
	private Button btnCollect;
	private TextView textGeoraphy;
	
	private static final int PROVINCE 	= 0;
	private static final int CITY 		= 1;
	private static final int COUNTY 	= 2;
	private static final int WEATHER_CODE= 3;
	
	
	/***********************************
	 * county详细信息碎片的构造函数
	 * @param name 装有城市详细名字的list
	 * 				list0=provinceName	
	 * 				list1=cityName
	 * 				list2=contyName
	 * 				list3=weatherCode
	 ***********************************/
	public CountyDetailFragment(ArrayList<String> name) {
		// TODO Auto-generated constructor stub
		this.name = name;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		coolWeatherDb = CoolWeatherDB.getInstance(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		myself = inflater.inflate(R.layout.fragment_area_show, null);
		initView();
		initData();
		return myself;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		saveData();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	void initView(){
		STAR_FLAG = coolWeatherDb.isStarCounty(name.get(WEATHER_CODE));
		STAR_FLAG = coolWeatherDb.isLocalCounty(name.get(COUNTY));
		textGeoraphy = (TextView) myself.findViewById(R.id.city_name);
		textGeoraphy.setText(name.get(COUNTY));
		myself.findViewById(R.id.btn_back).setOnClickListener(this);
		btnCollect = (Button) myself.findViewById(R.id.btn_collect);
		btnCollect.setOnClickListener(this);
		flushStar();
	}
	
	void initData(){
		String address = "http://poster.weather.com.cn/p_files/base/" + name.get(WEATHER_CODE) + ".jpg";
		HttpUtil.sendHttpRequestBitmap(address, new HttpCallbackListener() {			
			@Override
			public void onFinish(final Object response) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						flushPic((Bitmap)response);
					}
				});
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub				
			}
		});
	}
	private void saveData(){
		StarCounty starCounty = new StarCounty();
		starCounty.weather_code 	= name.get(WEATHER_CODE);
		starCounty.weather.getResult().get(0).setProvince(name.get(PROVINCE));
		starCounty.weather.getResult().get(0).setCity(name.get(CITY));
		starCounty.weather.getResult().get(0).setDistrct(name.get(COUNTY));
		
		if(STAR_FLAG){
			coolWeatherDb.saveStarCounty(starCounty);
		}else{
			coolWeatherDb.removeStarCounty(starCounty);
		}
	}
	
	public void flushPic(Bitmap bitmap){
		ImageView image =  (ImageView) myself.findViewById(R.id.imageView);
		image.setImageBitmap(bitmap);
		
	}
	private void flushStar(){
		if(STAR_FLAG){
			btnCollect.setBackgroundResource(R.drawable.star_bright);
		} else {
			btnCollect.setBackgroundResource(R.drawable.star_dark);			
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_back:
			saveData();
			((SelectCounty) getActivity()).showCoutyList();
			break;
		case R.id.btn_collect:
			if(STAR_FLAG){
				STAR_FLAG = false;
			}else{
				STAR_FLAG = true;
			}
			flushStar();
			break;
		}
	}
}
