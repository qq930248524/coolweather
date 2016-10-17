package com.example.coolWeather.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;

import com.example.coolWeather.adapter.MyPagerAdapter;
import com.example.coolWeather.db.CoolWeatherDB;
import com.example.coolWeather.fragment.WeatherFragment;
import com.example.coolWeather.model.StarCounty;
import com.example.coolWeather.model.StarWeather.ResultBean;
import com.example.coolWeather.util.HttpCallbackListener;
import com.example.coolWeather.util.HttpUtil;
import com.example.coolWeather.util.Utility;
import com.example.greattest.R;

import android.R.color;
import android.R.menu;
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
import android.widget.LinearLayout.LayoutParams;
import cn.sharesdk.framework.ShareSDK;
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
		//初始化shareSDK，用户微信分享
		ShareSDK.initSDK(getApplicationContext(), "17c8e1454cf40");
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
		findViewById(R.id.btn_fun).setOnClickListener(clickListener);
		findViewById(R.id.btn_share).setOnClickListener(clickListener);

		//初始化viewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				StarCounty tempStar = starList.get(arg0);
				if(tempStar.weather.getResult().get(0).getWeather() == null){
					startRefresh(tempStar);
				}
				setPointBright(arg0);
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
	
	OnClickListener clickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_fun:
				getSlidingMenu().showMenu();
				break;
			case R.id.btn_share:
				break;
			}
		}
	};

	private void setPointBright(int position){
		for(int i=0; i<starList.size(); i++){
			pointList.get(i).setEnabled(false);
		}
		pointList.get(position).setEnabled(true);
	}

	private void inflatPointList(){
		LinearLayout pointLayout = (LinearLayout) findViewById(R.id.id_point_layout);
		pointList = new ArrayList<ImageView>();
		for(int i=0; i < starList.size(); i++){
			ImageView pointView = (ImageView)getLayoutInflater().inflate(R.layout.point, null);
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			p.setMargins(0, 0, 0, 10);
			pointView.setEnabled(true);
			pointList.add(pointView);
			pointLayout.addView(pointView, p);
		}
	}
	private ArrayList<View> inflatViewList(ArrayList<StarCounty> starList){
		ArrayList<View> viewList = new ArrayList<View>();		
		for(StarCounty starCounty: starList){
			View view = getLayoutInflater().inflate(R.layout.activity_show_weather, null);
			ResultBean result = starCounty.weather.getResult().get(0);
			((TextView) view.findViewById(R.id.text_publish)).	setText(result.getUpdateTime());
			((TextView) view.findViewById(R.id.text_date)).		setText(result.getTime());
			((TextView) view.findViewById(R.id.view_temp1)).	setText(result.getTemperature());
			((TextView) view.findViewById(R.id.view_temp2)).	setText(result.getTemperature());
			((TextView) view.findViewById(R.id.text_weather)).	setText(result.getWeather());			
			((TextView) view.findViewById(R.id.text_name)).		setText(result.getDistrct());			
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

		String address = null;
		try {
			address = "http://apicloud.mob.com/v1/weather/query?key=17cbd753c5b5f"
					+"&city=" + URLEncoder.encode(starCounty.weather.getResult().get(0).getDistrct(), "utf-8")
					+"&province=" + URLEncoder.encode(starCounty.weather.getResult().get(0).getCity(), "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
		ResultBean result = starCounty.weather.getResult().get(0);
		((android.widget.TextView)view.findViewById(R.id.view_temp1)).	setText(result.getTemperature());
		((android.widget.TextView)view.findViewById(R.id.view_temp2)).	setText(result.getTemperature());
		((android.widget.TextView)view.findViewById(R.id.text_weather)).setText(result.getWeather());
		((android.widget.TextView)view.findViewById(R.id.text_publish)).setText(result.getUpdateTime());
		((android.widget.TextView)view.findViewById(R.id.text_date)).	setText(result.getTime());
		((android.widget.TextView)view.findViewById(R.id.text_name)).	setText(result.getDistrct());		
	}

	private void endRefreshList(){
		if(progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}


}
