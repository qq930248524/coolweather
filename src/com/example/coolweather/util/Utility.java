package com.example.coolweather.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.coolweather.db.CoolWeatherDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;


public class Utility {
	public static void handleWeatherResponse(Context context, String response){
		try {
			System.out.println("========response:" + response);
			JSONObject weatherInfo = new JSONObject(response).getJSONObject("result");
			String cityName = weatherInfo.getString("citynm");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp_low");
			String temp2 = weatherInfo.getString("temp_high");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("days");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void saveWeatherInfo(Context context, 
			String cityName, 
			String weatherCode, 
			String temp1, 
			String temp2, 
			String weatherDesp, 
			String publishTime){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date(System.currentTimeMillis())));
		editor.commit();
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
