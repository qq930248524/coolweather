package com.example.coolWeather.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.coolWeather.adapter.MyPagerAdapter;
import com.example.coolWeather.db.CoolWeatherDB;
import com.example.coolWeather.fragment.WeatherFragment;
import com.example.coolWeather.model.StarCounty;
import com.example.greattest.R;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowWeather extends RootActivity implements OnClickListener{
	private ProgressDialog progressDialog;
	
	private CoolWeatherDB coolWeatherDb;
	
	private ViewPager viewPaper ;
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
		viewPaper = (ViewPager) findViewById(R.id.viewpager);
		viewPaper.setAdapter(adapter);
		//viewPaper.setOnPageChangeListener((OnPageChangeListener) this);
		
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
	
	private void startRefresh(StarCounty starCounty){
		if(!progressDialog.isShowing()){
			progressDialog.show();
		}
		
	}
	private void endRefreshList(){
		if(progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
	
}
