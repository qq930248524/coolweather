package com.example.coolweather.activity;

import com.example.coolweather.R;
import com.example.coolweather.service.AutoUpdateService;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.style.UpdateAppearance;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	
	private Button btnSwitchCity;
	private Button btnRefreshWeather;
	private LinearLayout layoutWeatherInfo;
	private TextView textCityName;
	private TextView textPublishTime;
	private TextView textCurrentData;
	private TextView textWeatherDesp;
	private TextView textTemp1;
	private TextView textTemp2;
	public HttpCallbackListener weatherCodeListener;
	public HttpCallbackListener weatherInfoListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		initView();
		functionRun();
	}
	
	private void initView(){
		btnSwitchCity = (Button) findViewById(R.id.btn_switchCity);
		btnSwitchCity.setOnClickListener(this);
		btnRefreshWeather = (Button) findViewById(R.id.btn_refreshWeather);
		btnRefreshWeather.setOnClickListener(this);
		
		layoutWeatherInfo = (LinearLayout) findViewById(R.id.weather_info_layout);
		textCityName = (TextView) findViewById(R.id.city_name);
		textPublishTime = (TextView) findViewById(R.id.text_publish);
		textCurrentData	 = (TextView) findViewById(R.id.text_date);
		textWeatherDesp = (TextView) findViewById(R.id.text_weather);
		textTemp1 = (TextView) findViewById(R.id.view_temp1);
		textTemp2 = (TextView) findViewById(R.id.view_temp2);
		//Http处理天气代号回调函数
		weatherCodeListener = new HttpCallbackListener() {		
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub	
				if(!TextUtils.isEmpty(response)){
					String[] array = response.split("\\|");
					if(array != null && array.length == 2){
						String weatherCode = array[1];
						String address = "http://api.k780.com:88/?app=weather.today&weaid="+ weatherCode +"&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";				
						HttpUtil.sendHttpRequest(address, weatherInfoListener);
					}
				}
			}
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub				
			}
		};
		
		//Http处理天气信息回调函数
		weatherInfoListener = new HttpCallbackListener() {			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub	
				Utility.handleWeatherResponse(WeatherActivity.this, response);
				runOnUiThread(new Runnable(){
					public void run(){
						showWeather();
					}
				});
			}			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub				
			}
		};
	}
	
	private void functionRun(){
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			// 有县级代号时就去查询天气
			queryWeather(countyCode);
		} else {
			// 没有县级代号时就直接显示本地天气
			showWeather();
		}
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}
	
	private void queryWeather(String countyCode){
		textPublishTime.setText("同步中...");
		layoutWeatherInfo.setVisibility(View.INVISIBLE);
		textCityName.setVisibility(View.INVISIBLE);
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		HttpUtil.sendHttpRequest(address, weatherCodeListener);
	}
	
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		textCityName.setText(prefs.getString("city_name", ""));
		textTemp1.setText(prefs.getString("temp1", ""));
		textTemp2.setText(prefs.getString("temp2", ""));
		textWeatherDesp.setText(prefs.getString("weather_desp", ""));
		textPublishTime.setText("发布时间" + prefs.getString("publish_time", ""));
		textCurrentData.setText(prefs.getString("current_date", ""));
		
		layoutWeatherInfo.setVisibility(View.VISIBLE);
		textCityName.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_refreshWeather:
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
				String countyCode = preferences.getString("countyCode", "");
				if(!TextUtils.isEmpty(countyCode)){
					queryWeather(countyCode);
				}
				break;
			case R.id.btn_switchCity:
				Intent intent = new Intent(this, ChooseAreaActivity.class);
				intent.putExtra("is_weather_return", true);
				startActivity(intent);
				finish();
				break;
		}
	}
	
}
