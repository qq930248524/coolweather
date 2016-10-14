package com.example.coolWeather.activity;

import com.example.greattest.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import cn.sharesdk.framework.ShareSDK;

public class RootActivity extends SlidingFragmentActivity implements OnClickListener{
	private Button btnWeather;
	private Button btnCity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//setContentView(R.layout.activity_county);
		setBehindContentView(R.layout.activity_left_menu);
		initLeftMenu();
		initView();
	}
	
	private void initView(){
		btnWeather = (Button) findViewById(R.id.btn1);
		btnWeather.setOnClickListener(this);
		btnCity = (Button) findViewById(R.id.btn2);
		btnCity.setOnClickListener(this);
	}
	

	private void initLeftMenu(){

		SlidingMenu menu = getSlidingMenu();
		menu.setMode(SlidingMenu.LEFT);
		// 设置触摸屏幕的模式
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		// 设置滑动菜单视图的宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//		menu.setBehindWidth()
		// 设置渐入渐出效果的值
		menu.setFadeDegree(0.35f);		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn1:
			Intent intent1 = new Intent(this, ShowWeather.class);
			startActivity(intent1);
			finish();
			break;
		case R.id.btn2:
			Intent intent2 = new Intent(this, ManageCounty.class);
			startActivity(intent2);
			finish();
			break;
		}	
	}


}
