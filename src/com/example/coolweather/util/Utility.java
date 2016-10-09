package com.example.coolWeather.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.coolWeather.db.CoolWeatherDB;
import com.example.coolWeather.model.City;
import com.example.coolWeather.model.County;
import com.example.coolWeather.model.Province;
import com.example.coolWeather.model.StarCounty;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;


public class Utility {
	public static void handleWeatherResponse(CoolWeatherDB coolWeatherDB, StarCounty starCounty, String response){
		try {
			System.out.println("========response:" + response);
			JSONObject weatherInfo 	= new JSONObject(response).getJSONObject("result");
			starCounty.county_name 	= weatherInfo.getString("citynm");
			starCounty.weather_code 	= weatherInfo.getString("cityid");
			starCounty.temp_low		= weatherInfo.getString("temp_low");
			starCounty.temp_height	= weatherInfo.getString("temp_high");
			starCounty.weather		= weatherInfo.getString("weather");
			starCounty.publish_time	= weatherInfo.getString("days");
			long currentTime = System.currentTimeMillis();
			SimpleDateFormat format = new SimpleDateFormat("yy年MM月dd日--HH时mm分ss秒");
			Date date = new Date(currentTime);
			starCounty.get_time 		= format.format(date);
			
			//更新数据库中StarCounty的元素
			coolWeatherDB.removeStarCounty(starCounty);
			coolWeatherDB.saveStarCounty(starCounty);
			
			//更新数组中需要更新的元素
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//detail province
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response){
		if(!TextUtils.isEmpty(response)) {
			String [] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0){
				for(String p : allProvinces){
					String[] provinces = p.split("\\|"); 
					Province province = new Province();
					province.setProvinceName(provinces[1]);
					province.setProvinceCode(provinces[0]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	//detail city data
	public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String [] allCitys = response.split(",");
			if( allCitys != null && allCitys.length > 0){
				for(String p : allCitys){
					String [] array = p.split("\\|");
					City city = new City();
					city.setCityName(array[1]);
					city.setCityCode(array[0]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}

		}
		return false;
	}
	
	//detail county data
	public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB, String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] counties = response.split(",");
			if( counties != null && counties.length > 0){
				for(String p : counties){
					String[] array = p.split("\\|");
					County county = new County();
					county.setCountyName(array[1]);
					county.setCountyCode(array[0]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	

}
