package com.example.coolWeather.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.coolWeather.db.CoolWeatherDB;
import com.example.coolWeather.fragment.WeatherFragment;
import com.example.coolWeather.model.StarCounty;
import com.example.coolWeather.util.HttpUtil;
import com.example.greattest.R;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShowWeather extends RootActivity implements OnClickListener{
	private ProgressDialog progressDialog;
	
	private CoolWeatherDB coolWeatherDb;
	private Button btnFunction;
	private Button btnShare;
	private TextView textTitle;
	
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	private ViewPager viewPaper ;
	private FragmentPagerAdapter adapter;
	
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
		fragmentList = initFragmentList();
	}
	private void initView(){
		btnFunction = (Button) findViewById(R.id.btn_fun);
		btnShare = (Button) findViewById(R.id.btn_share);
		textTitle = (TextView) findViewById(R.id.text_county);
		btnFunction.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		
		viewPaper = (ViewPager) findViewById(R.id.id_viewpager);
		adapter = new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public int getCount()
			{
				return fragmentList.size();
			}

			@Override
			public Fragment getItem(int arg0)
			{
				return fragmentList.get(arg0);
			}
		};
		viewPaper.setAdapter(adapter);
		//viewPaper.setOnPageChangeListener((OnPageChangeListener) this);
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
