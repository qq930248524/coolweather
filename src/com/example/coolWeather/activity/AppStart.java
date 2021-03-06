package com.example.coolWeather.activity;

import com.example.coolWeather.db.CoolWeatherDB;
import com.example.greattest.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class AppStart extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = LayoutInflater.from(this).inflate(R.layout.start_layout, null);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view);
		
		//初始化数据库，第一次创建数据库需要时间，在其他activity中初始化第一次会崩溃
		CoolWeatherDB.getInstance(AppStart.this);
		
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		view.setAnimation(aa);
		aa.setDuration(2000);
		aa.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AppStart.this, ShowWeather.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
