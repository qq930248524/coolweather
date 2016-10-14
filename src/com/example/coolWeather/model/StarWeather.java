package com.example.coolWeather.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StarWeather{
	public String msg;
	public Weather result;
	public String retCode;


	public static class Weather {
		public String airCondition;	//空气污染指数
		public String city; 		//城市
		public String coldIndex; 	//感冒指数
		public String date;			//日期
		public String distrct; 		//县
		public String dressingIndex;//穿衣建议
		public String exerciseIndex;//锻炼建议
		public List<Future> future;
		public String humidity;		//湿度
		public String pollutionIndex;//污染指数
		public String province;		//省
		public String sunrise;		//日出时间
		public String sunset;		//日落时间
		public String temperature;	//温度
		public String time;			//时间
		public String updateTime;	//更新时间 
		public String washIndex;	//洗衣指数
		public String weather;		//天气
		public String weak;			//星期
		public String wind;			//风力	
	}
	
	public static class Future {
		public String date;	//天气日期 
		public String dayTime;	//白天天气
		public String night;	//夜晚天气
		public String temperature;	//温度
		public String weak;		//星期
		public String wind;		//风力
	}



}
