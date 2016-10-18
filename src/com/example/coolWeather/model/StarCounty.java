package com.example.coolWeather.model;

public class StarCounty {
	public String weather_code;
	public StarWeather weather;
	
	public StarCounty(){
		weather = new StarWeather();
	}
}
