package com.example.coolWeather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

	public static final String CREATE_PROVINCE = "create table Province ("
			+ "id integer primary key autoincrement,"
			+ "province_name text,"
			+ "province_code text)";
	public static final String CREATE_CITY = "create table City ("
			+ "id integer primary key autoincrement,"
			+ "city_name text,"
			+ "city_code text,"
			+ "province_id integer)";
	public static final String CREATE_COUNTY = "create table County ("
			+ "id integer primary key autoincrement,"
			+ "county_name text,"
			+ "county_code text,"
			+ "city_id integer)";
	public static final String CREATE_STAR_COUNTY = "create table StarCounty("
			+ "id integer primary key autoincrement,"
			+ "weather_code text,"
			+ "airCondition text,"
			+ "city text,"
			+ "coldIndex text,"
			+ "date text,"
			+ "distrct text,"
			+ "dressingIndex text,"
			+ "exerciseIndex text,"
			+ "humidity text,"
			+ "pollutionIndex text,"
			+ "province text,"
			+ "sunrise text,"
			+ "sunset text,"
			+ "temperature text,"
			+ "time text,"
			+ "updateTime text,"
			+ "washIndex text,"
			+ "weather text,"
			+ "weak text,"
			+ "wind text)";

	
	public CoolWeatherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
		db.execSQL(CREATE_STAR_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists Province");
		db.execSQL("drop table if exists City");
		db.execSQL("drop table if exists County");
		onCreate(db);

	}

}
