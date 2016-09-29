package com.example.coolWeather.util;

public interface HttpCallbackListener {
	void onFinish(Object response);
	void onError(Exception e);
}
