package com.example.coolWeather.db;

import java.util.ArrayList;
import java.util.List;

import com.example.coolWeather.model.City;
import com.example.coolWeather.model.County;
import com.example.coolWeather.model.Province;
import com.example.coolWeather.model.StarCounty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class CoolWeatherDB {
	//name
	public static final String DB_NAME = "cool_weather";
	//version
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	//获取实例
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if(coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	public void closs(){}
	
	//将Province实例存储到数据库
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	//读取Province信息
	public List<Province> loadProvinces(){
		List <Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);				
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}
	
	public void saveCity(City city) {
		ContentValues values = new ContentValues();
		values.put("city_name", city.getCityName());
		values.put("city_code", city.getCityCode());
		values.put("province_id", city.getProvinceId());
		db.insert("city", null, values);
	}
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}
	
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	public List<County> loadCounty(int cityId){
		List <County> list = new ArrayList<County>();
		Cursor	cursor = db.query("County", null, "city_id = ?", 
				new String[] {String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}
	
	public Boolean isStarCounty(String weatherCode){
		Boolean flag = false;
		Cursor cursor =	db.query("StarCounty", null, "weather_code = ?",
				new String[] {String.valueOf(weatherCode)}, null, null, null);
		flag = cursor.moveToFirst()?true:false;
		cursor.close();
		return flag;
	}
	
	public void saveStarCounty(StarCounty starCounty){
		if(!isStarCounty(starCounty.weather_code)){
			ContentValues values = new ContentValues();
			starCounty.weather.airCondition = "asdkfasdkf";
			values.put("weather_code", 	starCounty.weather_code);
			values.put("airCondition", 	starCounty.weather.airCondition);
			values.put("city", 			starCounty.weather.city);
			values.put("coldIndex", 	starCounty.weather.coldIndex);
			values.put("date", 			starCounty.weather.date);
			values.put("distrct", 		starCounty.weather.distrct);
			values.put("dressingIndex",	starCounty.weather.dressingIndex);
			values.put("exerciseIndex",	starCounty.weather.exerciseIndex);
			values.put("humidity", 		starCounty.weather.humidity);
			values.put("pollutionIndex",starCounty.weather.pollutionIndex);
			values.put("province", 		starCounty.weather.province);
			values.put("sunrise", 		starCounty.weather.sunrise);
			values.put("sunset", 		starCounty.weather.sunset);
			values.put("temperature", 	starCounty.weather.temperature);
			values.put("time", 			starCounty.weather.time);
			values.put("updateTime", 	starCounty.weather.updateTime);
			values.put("washIndex", 	starCounty.weather.washIndex);
			values.put("weather", 		starCounty.weather.weather);
			values.put("weak", 			starCounty.weather.weak);
			values.put("wind", 			starCounty.weather.wind);
			
			db.insert("StarCounty", null, values);
		}
	}
	public void removeStarCounty(StarCounty starCounty){
		if(isStarCounty(starCounty.weather_code)){
			db.delete("StarCounty", "weather_code = ?", new String[]{String.valueOf(starCounty.weather_code)});
		}
	}
	public ArrayList<StarCounty> loadStarCountyList(){
		ArrayList<StarCounty> list = new ArrayList<StarCounty>();
		Cursor cursor = db.query("StarCounty", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				StarCounty starCounty =  new StarCounty();
				
				starCounty.weather_code 		= (cursor.getString(cursor.getColumnIndex("weather_code")));
				starCounty.weather.airCondition = (cursor.getString(cursor.getColumnIndex("airCondition")));
				starCounty.weather.city 		= (cursor.getString(cursor.getColumnIndex("city")));
				starCounty.weather.coldIndex 	= (cursor.getString(cursor.getColumnIndex("coldIndex")));
				starCounty.weather.date 		= (cursor.getString(cursor.getColumnIndex("date")));
				starCounty.weather.distrct 		= (cursor.getString(cursor.getColumnIndex("distrct")));
				starCounty.weather.dressingIndex= (cursor.getString(cursor.getColumnIndex("dressingIndex")));
				starCounty.weather.exerciseIndex= (cursor.getString(cursor.getColumnIndex("exerciseIndex")));
				starCounty.weather.humidity 	= (cursor.getString(cursor.getColumnIndex("humidity")));
				starCounty.weather.pollutionIndex=(cursor.getString(cursor.getColumnIndex("pollutionIndex")));
				starCounty.weather.province 	= (cursor.getString(cursor.getColumnIndex("province")));
				starCounty.weather.sunrise 		= (cursor.getString(cursor.getColumnIndex("sunrise")));
				starCounty.weather.sunset 		= (cursor.getString(cursor.getColumnIndex("sunset")));
				starCounty.weather.temperature	= (cursor.getString(cursor.getColumnIndex("temperature")));
				starCounty.weather.time 		= (cursor.getString(cursor.getColumnIndex("time")));
				starCounty.weather.updateTime 	= (cursor.getString(cursor.getColumnIndex("updateTime")));
				starCounty.weather.washIndex 	= (cursor.getString(cursor.getColumnIndex("washIndex")));
				starCounty.weather.weather 		= (cursor.getString(cursor.getColumnIndex("weather")));
				starCounty.weather.weak 		= (cursor.getString(cursor.getColumnIndex("weak")));
				starCounty.weather.wind 		= (cursor.getString(cursor.getColumnIndex("wind")));
				
				list.add(starCounty);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

}
