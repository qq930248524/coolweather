package com.example.coolWeather.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.example.coolWeather.adapter.MyPagerAdapter;
import com.example.coolWeather.adapter.WeatherListViewAdapter;
import com.example.coolWeather.db.CoolWeatherDB;
import com.example.coolWeather.fragment.WeatherFragment;
import com.example.coolWeather.model.Future;
import com.example.coolWeather.model.Result;
import com.example.coolWeather.model.StarCounty;
import com.example.coolWeather.model.StarWeather;
import com.example.coolWeather.util.HttpCallbackListener;
import com.example.coolWeather.util.HttpUtil;
import com.example.coolWeather.util.Utility;
import com.example.greattest.R;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowWeather extends RootActivity implements OnClickListener{
	private static final String TextView = null;

	private ProgressDialog progressDialog;

	private CoolWeatherDB coolWeatherDb;
	private int position;

	private ViewPager viewPager ;
	private MyPagerAdapter viewPaperAdapter;

	private ArrayList<View> viewList;
	private ArrayList<StarCounty> starList;	
	private ArrayList<ImageView> pointList;

	private int posation = 0;//页面位置
	private ArrayList<Future> futureList; //页面listview的10天数据
	private WeatherListViewAdapter listViewAdapter;	//页面listView的适配器

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_new);
		//初始化shareSDK，用户微信分享
		//ShareSDK.initSDK(getApplicationContext(), "17c8e1454cf40");
		
		if(initData() == false){ //false数据库没有数据，需要切换到城市选择界面
			Intent intent = new Intent(ShowWeather.this, SelectCounty.class);
			startActivity(intent);
			this.finish();
		}else{
			initView();
		}
	}


	private Boolean initData(){
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在加载。。。");
		progressDialog.setCanceledOnTouchOutside(false);

		//从数据库加载starList
		coolWeatherDb = CoolWeatherDB.getInstance(this);
		starList = coolWeatherDb.loadStarCountyList();
		if(starList.size() == 0) return false;
		
		//使用加载到的starList实例化本地对象futureList和adapter
		//所有viewPaper中的ListView，公用一个futureList和adapter，以便实现数据刷新
		futureList = new ArrayList<Future>();
		futureList.addAll(starList.get(0).weather.getResult().get(0).getFuture());
		listViewAdapter = new WeatherListViewAdapter(this, futureList, coolWeatherDb);

		//获取viewPaper中的view，以list形式返回。包括view的实现
		viewList = inflatViewList(starList);
		viewPaperAdapter = new MyPagerAdapter(viewList);			
		return true;
	}

	private void initView(){
		findViewById(R.id.btn_fun).setOnClickListener(clickListener);

		//初始化viewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(viewPaperAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {					

			//切换新页面时被调用
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				posation = arg0;
				
				if(starList.get(arg0).weather.getResult().get(0).getWeather() == null){
					startRefresh(starList.get(arg0));
				}else{
					updateView(viewList.get(arg0), starList.get(arg0));
				}

				setPointBright(arg0);	//切换点点导航
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

		//初始化首次显示viewPager的首页
		StarCounty firstStar = starList.get(0);
		if(starList.get(0).weather.getResult().get(0).getWeather() == null){
			startRefresh(starList.get(0));
		}else{
			updateView(viewList.get(0), starList.get(0));
		}
		//初始化点点的线性布局
		inflatPointList();
		setPointBright(0);		
	}

	OnClickListener clickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_share:
				Toast.makeText(getApplicationContext(), "The function don't manage!", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_refresh:
				StarCounty starCounty = starList.get(viewPager.getCurrentItem());
				startRefresh(starCounty);
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
			p.setMargins(10, 0, 10, 10);
			pointView.setEnabled(true);
			pointList.add(pointView);
			pointLayout.addView(pointView, p);
		}
	}
	private ArrayList<View> inflatViewList(ArrayList<StarCounty> starList){
		ArrayList<View> viewList = new ArrayList<View>();		
		for( final StarCounty starCounty: starList){
			View view = getLayoutInflater().inflate(R.layout.viewpager_weather, null);
			//初始化listView
			final PullToRefreshListView pullListView = (PullToRefreshListView) view.findViewById(R.id.listView_weatherPullToRefresh);
			pullListView.setMode(Mode.PULL_FROM_START);
			pullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
				@Override
				public void onRefresh(PullToRefreshBase<ListView> refreshView) {
					// TODO Auto-generated method stub
					ILoadingLayout loadLayout = refreshView.getLoadingLayoutProxy(true, false);
					loadLayout.setPullLabel("下拉刷新");
					loadLayout.setRefreshingLabel("正在刷新");
					loadLayout.setReleaseLabel("刷新完毕");
					new MyAsyncTask(starCounty, pullListView).execute();
				}
			});
			futureList.clear();
			futureList.addAll(starList.get(0).weather.getResult().get(0).getFuture());
			WeatherListViewAdapter tempAdapter = new WeatherListViewAdapter(this, futureList, coolWeatherDb);

			//为listview设置适配器和动画
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.listview_item_rotate);
			LayoutAnimationController controller = new LayoutAnimationController(animation);
			pullListView.getRefreshableView().setLayoutAnimation(controller);
			pullListView.getRefreshableView().setAdapter(listViewAdapter);		

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


	/*******************************************
	 * 刷新数据
	 * 1、从后台拿数据
	 * 2、回调中通知适配器刷新
	 * @param starCounty
	 *************************************************/
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

				//gson 解析数据到starCounty
				Utility.handleWeatherResponse(coolWeatherDb, starCounty, (String)response);

				//在adapter的des中总是异常，暂时还没解决
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						viewPaperAdapter.notifyDataSetChanged();
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

	public void updateView(View view, final StarCounty starCounty){
		Result result = starCounty.weather.getResult().get(0);
		ImageView image = (ImageView) view.findViewById(R.id.imageView_weather);
		if(result.getWeather().indexOf("晴") > -1){
			image.setImageResource(R.drawable.tianqi_sun);
		}else if(result.getWeather().indexOf("阴") > -1){
			image.setImageResource(R.drawable.tianqi_sun_cloud);
		}else if(result.getWeather().indexOf("云") > -1){
			image.setImageResource(R.drawable.tianqi_cloud);
		}else if(result.getWeather().indexOf("雪") > -1){
			image.setImageResource(R.drawable.tianqi_snow);
		}else if(result.getWeather().indexOf("雾") > -1){
			image.setImageResource(R.drawable.tianqi_fog);
		}else if(result.getWeather().indexOf("雨") > -1){
			image.setImageResource(R.drawable.tianqi_rain);
		}

		//weather wendu date descText
		TextView city = (android.widget.TextView) view.findViewById(R.id.city);
		TextView wendu = (android.widget.TextView) view.findViewById(R.id.wendu);
		TextView wind = (android.widget.TextView) view.findViewById(R.id.wind);
		TextView desc = (android.widget.TextView) view.findViewById(R.id.desc);
		TextView weather = (android.widget.TextView) view.findViewById(R.id.weather);
		city.setText(result.getDistrct());
		wendu.setText(result.getTemperature());
		wind.setText(result.getWind());
		weather.setText(result.getWeather());
		String allIndex = "空气污染指数：" + result.getAirCondition(); 
		desc.setText(allIndex);

		futureList.clear();
		futureList.addAll(starCounty.weather.getResult().get(0).getFuture());
		listViewAdapter.notifyDataSetChanged();
	}

	private class MyAsyncTask extends AsyncTask<Void, Void, String>{
		private StarCounty starCounty;
		private PullToRefreshListView pullRefresh;
		public MyAsyncTask(StarCounty starCounty, PullToRefreshListView pullToRefresh) {
			// TODO Auto-generated constructor stub
			this.starCounty = starCounty;
			this.pullRefresh = pullToRefresh;
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String address = null;
			try {
				address = "http://apicloud.mob.com/v1/weather/query?key=17cbd753c5b5f"
						+"&city=" + URLEncoder.encode(starCounty.weather.getResult().get(0).getDistrct(), "utf-8")
						+"&province=" + URLEncoder.encode(starCounty.weather.getResult().get(0).getCity(), "utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String response = null;
			response = HttpUtil.sendHttprequestNature(address);
			starCounty.weather = new Gson().fromJson(response, StarWeather.class);

			//更新数据库中StarCounty的元素
			coolWeatherDb.removeStarCounty(starCounty);
			coolWeatherDb.saveStarCounty(starCounty);
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			updateView(viewList.get(posation), starCounty);
			pullRefresh.onRefreshComplete();
			super.onPostExecute(result);
		}

	}

	private void endRefreshList(){
		if(progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}


}
