package com.example.coolWeather.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.coolWeather.adapter.MyPagerAdapter;
import com.example.coolWeather.db.CoolWeatherDB;
import com.example.coolWeather.fragment.WeatherFragment;
import com.example.coolWeather.model.StarCounty;
import com.example.coolWeather.util.HttpCallbackListener;
import com.example.coolWeather.util.HttpUtil;
import com.example.coolWeather.util.Utility;
import com.example.greattest.R;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowWeather extends RootActivity implements OnClickListener{
	private static final String TextView = null;

	private ProgressDialog progressDialog;

	private CoolWeatherDB coolWeatherDb;

	private ViewPager viewPager ;
	private MyPagerAdapter adapter;
	private ArrayList<View> viewList;
	private ArrayList<StarCounty> starList;	
	private ArrayList<ImageView> pointList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		initData();
		initView();
	}


	private void initData(){
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在加载。。。");
		progressDialog.setCanceledOnTouchOutside(false);

		coolWeatherDb = CoolWeatherDB.getInstance(this);
		starList = coolWeatherDb.loadStarCountyList();		
		viewList = inflatViewList(starList);
		adapter = new MyPagerAdapter(viewList);
	}

	private void initView(){
		findViewById(R.id.btn_fun).setOnClickListener(this);
		findViewById(R.id.btn_share).setOnClickListener(this);

		//初始化viewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				StarCounty tempStar = starList.get(arg0);
				if(tempStar.weather == null){
					startRefresh(tempStar);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});


		//初始化点点的线性布局
		inflatPointList();

		setPointBright(0);		
	}

	private void setPointBright(int position){
		for(int i=0; i<starList.size(); i++){
			pointList.get(i).setEnabled(true);
		}
		pointList.get(position).setEnabled(false);
	}

	private void inflatPointList(){
		LinearLayout pointLayout = (LinearLayout) findViewById(R.id.id_point_layout);
		pointList = new ArrayList<ImageView>();
		for(int i=0; i < starList.size(); i++){
			ImageView pointView = (ImageView)LayoutInflater.from(this).inflate(R.layout.point, null);
			pointView.setEnabled(true);
			pointList.add(pointView);
			pointLayout.addView(pointView);
		}
	}
	private ArrayList<View> inflatViewList(ArrayList<StarCounty> starList){
		ArrayList<View> viewList = new ArrayList<View>();		
		for(StarCounty starCounty: starList){
			View view = getLayoutInflater().inflate(R.layout.activity_show_weather, null);
			((TextView) view.findViewById(R.id.text_publish)).setText(starCounty.publish_time);
			((TextView) view.findViewById(R.id.text_date)).setText(starCounty.get_time);
			((TextView) view.findViewById(R.id.view_temp1)).setText(starCounty.temp_low);
			((TextView) view.findViewById(R.id.view_temp2)).setText(starCounty.temp_height);
			((TextView) view.findViewById(R.id.text_weather)).setText(starCounty.weather);			
			((TextView) view.findViewById(R.id.text_name)).setText(starCounty.county_name);			
			viewList.add(view);
		}			
		return viewList;
	}

	private List<Fragment> initFragmentList(){
		List<Fragment> fragmentList = new ArrayList<Fragment>();
		List<StarCounty> starCountyList = new ArrayList<StarCounty>();
		//从数据库中获取starCountyList
		//如果实体为空，则刷新实体数据
		starCountyList = coolWeatherDb.loadStarCountyList();
		List<StarCounty> refreshList = new ArrayList<StarCounty>();
		for(StarCounty s:starCountyList){
			fragmentList.add(new WeatherFragment(s));
		}

		return fragmentList;
	}

	private void startRefresh(final StarCounty starCounty){
		if(!progressDialog.isShowing()){
			progressDialog.show();
		}

		String address = "http://api.k780.com:88/?app=weather.today&weaid="+ 
				starCounty.weather_code +
				"&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";				
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {			
			@Override
			public void onFinish(Object response) {
				// TODO Auto-generated method stub

				Utility.handleWeatherResponse(coolWeatherDb, starCounty, (String)response);

				//在adapter的des中总是异常，暂时还没解决
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						View view = viewPager.findViewWithTag(viewPager.getCurrentItem());
						updateView(view, starCounty);						
					}
				});

				endRefreshList();

			}			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				endRefreshList();
				System.out.println("error at onError!\n");
			}
		});
	}
	public void updateView(View view, StarCounty starCounty){
		((android.widget.TextView)view.findViewById(R.id.view_temp1)).setText(starCounty.temp_low);
		((android.widget.TextView)view.findViewById(R.id.view_temp2)).setText(starCounty.temp_height);
		((android.widget.TextView)view.findViewById(R.id.text_weather)).setText(starCounty.weather);
		((android.widget.TextView)view.findViewById(R.id.text_publish)).setText(starCounty.publish_time);
		((android.widget.TextView)view.findViewById(R.id.text_date)).setText(starCounty.get_time);
		((android.widget.TextView)view.findViewById(R.id.text_name)).setText(starCounty.county_name);		
	}

	private void endRefreshList(){
		if(progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_fun:
			getSlidingMenu().showMenu();
			break;
		case R.id.btn_share:
			break;
		default:
			break;
		}
	}

}
