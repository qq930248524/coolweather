package com.example.coolWeather.fragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.coolWeather.activity.SelectCounty;
import com.example.coolWeather.db.CoolWeatherDB;
import com.example.coolWeather.model.City;
import com.example.coolWeather.model.County;
import com.example.coolWeather.model.Province;
import com.example.coolWeather.model.StarCounty;
import com.example.coolWeather.util.HttpCallbackListener;
import com.example.coolWeather.util.HttpUtil;
import com.example.coolWeather.util.Utility;
import com.example.greattest.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CountyListFragment extends Fragment {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	public static final int LEVEL_WEATHER = 3;
	
	private ProgressDialog progressDialog;
	private TextView titileText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private static CoolWeatherDB coolWeatherDB;
	
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;
	private City selectedCity;
	private County selectedCounty;
	
	private int currentLevel;
	private View myself;
	private String picCode;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		coolWeatherDB = CoolWeatherDB.getInstance(getActivity());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		myself = inflater.inflate(R.layout.fragment_area_choose, null);
		initView();
		
		//默认添加本地定位County到StarCounty列表
		addLocalCounty();
		//显示查询省
		queryProvince();
		return myself;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	

	public void initView(){
		currentLevel = LEVEL_PROVINCE;
		titileText = (TextView) myself.findViewById(R.id.title_text);
		
		
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dataList);
		listView = (ListView) myself.findViewById(R.id.listView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel == LEVEL_PROVINCE){
					currentLevel = LEVEL_CITY;
					selectedProvince = provinceList.get(index);
					queryCity();
				}else if (currentLevel == LEVEL_CITY){
					currentLevel = LEVEL_COUNTY;
					selectedCity = cityList.get(index);
					queryCounty();
				}else if(currentLevel == LEVEL_COUNTY){
					currentLevel = LEVEL_WEATHER;
					selectedCounty = countyList.get(index);
					
					//切换碎片到显示地址
					queryCode();

				}
			}
		});
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("正在加载。。。");
		progressDialog.setCanceledOnTouchOutside(false);
	}

	/*************
	 * 添加定位城市到数据库，主要过程为：	
	 * 		1、获取公网IP
	 * 		2、根据公网IP获取城市名字
	 * 		3、将得到的county添加数据库
	 */
	public static void addLocalCounty(){
		
		try {
			String address = "https://ipip.yy.com/get_ip_info.php";
			URL url = new URL(address);
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {				
				@Override
				public void onFinish(Object response) {
					// TODO Auto-generated method stub
					try {
						String[] result = response.toString().split("=");
						JSONObject jsonObject;
						jsonObject = new JSONObject(result[1]);
						String city 	= jsonObject.getString("city");
						String province	= jsonObject.getString("province");

						StarCounty starCounty = new StarCounty();
						starCounty.weather.getResult().get(0).setProvince(province);
						starCounty.weather.getResult().get(0).setCity(province);
						starCounty.weather.getResult().get(0).setDistrct(city);
						coolWeatherDB.saveStarCounty(starCounty);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					

				}
				
				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * 1/getIp
		 * 2/getCounty
		 * 3/saveDb
		 */
	}
	
	/****************************
	 * 查询省份信息列表
	 *  
	 *********************/
	private void queryProvince(){
		provinceList = coolWeatherDB.loadProvinces();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province p : provinceList){
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titileText.setText("中国");
		} else {
			queryFromServer(null);
		}
	}
	
	/************************ 
	 * 查询城市信息列表
	 * 
	 *********************/
	private void queryCity(){
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size() > 0){
			dataList.clear();
			for(City p : cityList){
				dataList.add(p.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titileText.setText(selectedProvince.getProvinceName());
		} else {
			queryFromServer(selectedProvince.getProvinceCode());
		}
	}
	/**********************
	 * 查询区县信息列表
	 * 
	 */
	private void queryCounty(){
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		if(countyList.size() > 0){
			dataList.clear();
			for(County p : countyList){
				dataList.add(p.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titileText.setText(selectedCity.getCityName());
		} else {
			queryFromServer(selectedCity.getCityCode());
		}
	}
	private void queryCode(){
		queryFromServer(selectedCounty.getCountyCode());
	}
	
	/***************** 
	 * 根据地区编号，从服务器获取数据
	 * @param code 地区编号
	 */
	private void queryFromServer(String code){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		//showProgressDialog();
		progressDialog.show();
		HttpUtil.sendHttpRequest(address, httpCallback);
	};
	
	/************************************************************
	 * Http回调函数，通过不同的level，对response最不同的处理
	 *************************************************************/
	private HttpCallbackListener httpCallback = new HttpCallbackListener() {
		
		@Override
		public void onFinish(Object response) {
			boolean result = false;
			// TODO Auto-generated method stub
			switch(currentLevel){
				case LEVEL_PROVINCE:
					result = Utility.handleProvinceResponse(coolWeatherDB, (String)response);
					break;
				case LEVEL_CITY:
					result = Utility.handleCityResponse(coolWeatherDB, (String)response, selectedProvince.getId());
					break;
				case LEVEL_COUNTY:
					result = Utility.handleCountyResponse(coolWeatherDB, (String)response, selectedCity.getId());
					break;
				case LEVEL_WEATHER:
					progressDialog.dismiss();
					String[] array = ((String) response).split("\\|");
					picCode = array[1];
					result = true;
				default:
					break;
			}
			if(result){
				getActivity().runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//closeProgressDialog();
						progressDialog.dismiss();
						switch(currentLevel){
							case LEVEL_PROVINCE:
								currentLevel = LEVEL_PROVINCE;
								queryProvince();
								break;
							case LEVEL_CITY:
								currentLevel = LEVEL_CITY;
								queryCity();
								break;
							case LEVEL_COUNTY:
								currentLevel = LEVEL_COUNTY;
								queryCounty();
								break;			
							case LEVEL_WEATHER:
								if(!TextUtils.isEmpty(picCode)){
									ArrayList<String> name = new ArrayList<String>();
									name.add(selectedProvince.getProvinceName());
									name.add(selectedCity.getCityName());
									name.add(selectedCounty.getCountyName());
									name.add(picCode);
									((SelectCounty) getActivity()).showGeography(name);
								}
								break;
							default:
								break;
					}
					}
				});
			}
			
		}
		
		@Override
		public void onError(Exception e) {
			// TODO Auto-generated method stub
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					//closeProgressDialog();
					progressDialog.dismiss();
					Toast.makeText(getActivity(), "加载失败！", Toast.LENGTH_SHORT).show();
				}
			});
		}
	};

	/******************
	 * 返回按键相应函数
	 */
	public void onBackPressed(){
		System.out.println("================onBackPressed: currentLevel = " + currentLevel);
		switch(currentLevel){
		case LEVEL_PROVINCE:
			//todo switch intent
			break;
		case LEVEL_CITY:
			currentLevel = LEVEL_PROVINCE;
			queryProvince();
			break;
		case LEVEL_COUNTY:
			currentLevel = LEVEL_CITY;
			queryCity();
			break;
		default:
			break;
		}
	}
	

}
