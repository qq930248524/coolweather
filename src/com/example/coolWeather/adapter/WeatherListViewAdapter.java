package com.example.coolWeather.adapter;

import java.util.ArrayList;

import com.example.coolWeather.db.CoolWeatherDB;
import com.example.coolWeather.model.Future;
import com.example.coolWeather.model.StarCounty;
import com.example.greattest.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherListViewAdapter extends ArrayAdapter {

	private static ArrayList<Future> futureList;
	private Context context;
	private CoolWeatherDB db;
	
	public WeatherListViewAdapter(Context context, ArrayList futureList, CoolWeatherDB db) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.weather_list_item, futureList);
		this.futureList = futureList;
		this.context = context;
		this.db = db;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.v("adapter", "================futureList.size = " + futureList.size());
		return futureList.size();
	}
	
	@Override
	public StarCounty getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Future futureOne = futureList.get(position);
		ViewHolder viewHolder = null;
		View view = null;
		if(convertView == null){
			view = LayoutInflater.from(context).inflate(R.layout.weather_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.image_weather = (ImageView) view.findViewById(R.id.imageView_weather);
			viewHolder.date = (TextView) view.findViewById(R.id.textView_date);
			viewHolder.day = (TextView) view.findViewById(R.id.textView_day);
			viewHolder.night = (TextView) view.findViewById(R.id.textView_night);
			viewHolder.temperature = (TextView) view.findViewById(R.id.textView_temperature);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if(futureOne.getDayTime() == null)return view;
		
		if(futureOne.getDayTime().indexOf("晴") > -1){
			viewHolder.image_weather.setImageResource(R.drawable.tianqi_sun);
		}else if(futureOne.getDayTime().indexOf("阴") > -1){
			viewHolder.image_weather.setImageResource(R.drawable.tianqi_sun_cloud);
		}else if(futureOne.getDayTime().indexOf("云") > -1){
			viewHolder.image_weather.setImageResource(R.drawable.tianqi_cloud);
		}else if(futureOne.getDayTime().indexOf("雪") > -1){
			viewHolder.image_weather.setImageResource(R.drawable.tianqi_snow);
		}else if(futureOne.getDayTime().indexOf("雾") > -1){
			viewHolder.image_weather.setImageResource(R.drawable.tianqi_fog);
		}else if(futureOne.getDayTime().indexOf("雨") > -1){
			viewHolder.image_weather.setImageResource(R.drawable.tianqi_rain);
		}
		
		viewHolder.date.setText(futureOne.getDate().split("-")[2] + "日 " + futureOne.getWeek());
		viewHolder.day.setText("白天：" + futureOne.getDayTime());
		viewHolder.night.setText("夜间：" + futureOne.getNight());
		viewHolder.temperature.setText(futureOne.getTemperature());
		
		return view;		
	}
	class ViewHolder{
		ImageView image_weather;
		TextView date;
		TextView day;
		TextView night;
		TextView temperature;
	}

}
