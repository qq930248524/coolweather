package com.example.coolweather.receive;

import com.example.coolweather.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WeatherReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(arg0, AutoUpdateService.class);
		//sarg0.startActivity(intent);
	}

}
