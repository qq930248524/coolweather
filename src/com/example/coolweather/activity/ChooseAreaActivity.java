package com.example.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.coolweather.R;
import com.example.coolweather.db.CoolWeatherDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	public static final int LEVEL_WEATHER = 3;
	
	private ProgressDialog progressDialog;
	private TextView titileText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;
	private City selectedCity;
	private County selectedCounty;
	
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
		initView();
		queryProvince();
	}
	
	public void initView(){
		currentLevel = LEVEL_PROVINCE;
		titileText = (TextView) findViewById(R.id.title_text);
		coolWeatherDB = CoolWeatherDB.getInstance(ChooseAreaActivity.this);
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel == LEVEL_PROVINCE){
					currentLevel = LEVEL_CITY;
					selectedProvince = provinceList.get(index);
					queryCity();
				}else if (currentLevel == LEVEL_CITY){
					currentLevel = LEVEL_COUNTY;
					selectedCity = cityList.get(index);
					queryCounty();
				}else if(currentLevel == LEVEL_COUNTY){
					currentLevel = LEVEL_WEATHER;
					showWeather();
				}
			}
		});
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在加载。。。");
		progressDialog.setCanceledOnTouchOutside(false);
	}
	
	private void showWeather(){
		//Intent intent = new Intent(ChooseAreaActivity.this, Weather.class);
		//intent.putExtra("code", selectedCounty.getCountyCode());
	}
	
	private void queryProvince(){
		provinceList = coolWeatherDB.loadProvinces();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province p : provinceList){
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titileText.setText("中国");
		} else {
			queryFromServer(null);
		}
	}
	
	private void queryCity(){
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size() > 0){
			dataList.clear();
			for(City p : cityList){
				dataList.add(p.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titileText.setText(selectedProvince.getProvinceName());
		} else {
			queryFromServer(selectedProvince.getProvinceCode());
		}
	}
	private void queryCounty(){
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		if(countyList.size() > 0){
			dataList.clear();
			for(County p : countyList){
				dataList.add(p.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titileText.setText(selectedCity.getCityName());
		} else {
			queryFromServer(selectedCity.getCityCode());
		}
	}
	
	private void queryFromServer(String code){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		//showProgressDialog();
		progressDialog.show();
		HttpUtil.sendHttpRequest(address, httpCallback);
	};
	
	private HttpCallbackListener httpCallback = new HttpCallbackListener() {
		
		@Override
		public void onFinish(String response) {
			boolean result = false;
			// TODO Auto-generated method stub
			switch(currentLevel){
				case LEVEL_PROVINCE:
					result = Utility.handleProvinceResponse(coolWeatherDB, response);
					break;
				case LEVEL_CITY:
					result = Utility.handleCityResponse(coolWeatherDB, response, selectedProvince.getId());
					break;
				case LEVEL_COUNTY:
					result = Utility.handleCountyResponse(coolWeatherDB, response, selectedCity.getId());
					break;
				default:
					break;
			}
			if(result){
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//closeProgressDialog();
						progressDialog.dismiss();
						switch(currentLevel){
							case LEVEL_PROVINCE:
								currentLevel = LEVEL_PROVINCE;
								queryProvince();
								break;
							case LEVEL_CITY:
								currentLevel = LEVEL_CITY;
								queryCity();
								break;
							case LEVEL_COUNTY:
								currentLevel = LEVEL_COUNTY;
								queryCounty();
								break;
							default:
								break;
					}
					}
				});
			}
			
		}
		
		@Override
		public void onError(Exception e) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				public void run() {
					//closeProgressDialog();
					progressDialog.dismiss();
					Toast.makeText(ChooseAreaActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
				}
			});
		}
	};

	public void onBackPressed(){
		System.out.println("================onBackPressed: currentLevel = " + currentLevel);
		switch(currentLevel){
		case LEVEL_PROVINCE:
			finish();
			break;
		case LEVEL_CITY:
			currentLevel = LEVEL_PROVINCE;
			queryProvince();
			break;
		case LEVEL_COUNTY:
			currentLevel = LEVEL_CITY;
			queryCity();
			break;
		default:
			finish();
			break;
		}
	}
}
