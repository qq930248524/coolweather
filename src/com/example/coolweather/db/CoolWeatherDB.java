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
			values.put("weather_code", starCounty.weather_code);
			values.put("county_code", starCounty.county_code);
			values.put("county_name", starCounty.county_name);
			values.put("days", starCounty.days);
			values.put("temp_heigth", starCounty.temp_height);
			values.put("temp_low", starCounty.temp_low);
			values.put("weather", starCounty.weather);
			db.insert("StarCounty", null, values);
		}
	}
	public void removeStarCounty(StarCounty starCounty){
		if(isStarCounty(starCounty.weather_code)){
			db.delete("StarCounty", "weather_code = ?", new String[]{String.valueOf(starCounty.weather_code)});
		}
	}
	public List<StarCounty> loadStarCountyList(){
		List<StarCounty> list = new ArrayList<StarCounty>();
		Cursor cursor = db.query("StarCounty", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				StarCounty starCounty =  new StarCounty();
				
				starCounty.setCounty_code	(cursor.getString(cursor.getColumnIndex("county_code")));
				starCounty.setCounty_name	(cursor.getString(cursor.getColumnIndex("county_name")));
				starCounty.setDays			(cursor.getString(cursor.getColumnIndex("days")));
				starCounty.setTemp_height	(cursor.getString(cursor.getColumnIndex("temp_height")));
				starCounty.setTemp_low		(cursor.getString(cursor.getColumnIndex("temp_low")));
				starCounty.setWeather		(cursor.getString(cursor.getColumnIndex("weather")));
				starCounty.setWeather_code	(cursor.getString(cursor.getColumnIndex("weather_code")));
				
				list.add(starCounty);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

}
