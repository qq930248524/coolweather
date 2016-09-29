package com.example.coolWeather.fragment;

import com.example.coolWeather.model.StarCounty;
import com.example.greattest.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WeatherFragment extends Fragment {
	private StarCounty starCounty;
	private View myself;
	
	public WeatherFragment(StarCounty starCounty) {
		// TODO Auto-generated constructor stub
		this.starCounty = starCounty;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		myself = inflater.inflate(R.layout.fragment_weather_show, null);
		return myself;
	}
}
