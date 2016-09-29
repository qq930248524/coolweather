package com.example.coolWeather.service;

import com.example.coolWeather.receive.WeatherReceiver;
import com.example.coolWeather.util.HttpCallbackListener;
import com.example.coolWeather.util.HttpUtil;
import com.example.coolWeather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		}).start();
		
		Intent i = new Intent(this, WeatherReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);		
		long triggerAtMillis = SystemClock.elapsedRealtime() + 8*24*60 * 1000;
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		manager.set(TRIM_MEMORY_RUNNING_CRITICAL, triggerAtMillis, pi);		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void updateWeather(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = preferences.getString("weather_code", "");
		String address = "http://api.k780.com:88/?app=weather.today&weaid="+ weatherCode +"&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";				
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {			
			@Override
			public void onFinish(Object response) {
				// TODO Auto-generated method stub
				Utility.handleWeatherResponse(AutoUpdateService.this, (String)response);
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}

