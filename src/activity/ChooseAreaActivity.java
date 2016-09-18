package activity;

import java.util.ArrayList;
import java.util.List;

import com.example.coolweather.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import db.CoolWeatherDB;
import model.City;
import model.County;
import model.Province;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titileText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;
	private City selectedCity;
	private County selectedCounty;
	
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
		initView();
		queryProvince();
	}
	
	public void initView(){
		titileText = (TextView) findViewById(R.id.title_text);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(index);
					queryCity();
				}else if (currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounty();
				}
			}
		});
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在加载。。。");
		progressDialog.setCanceledOnTouchOutside(false);
	}
	
	private void queryProvince(){
		currentLevel = LEVEL_PROVINCE;
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
	
	private void queryCity(){}
	private void queryCounty(){}
	
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
	
	private HttpCallbackListener httpCallback = new HttpCallbackListener() {
		
		@Override
		public void onFinish(String response) {
			boolean result = false;
			// TODO Auto-generated method stub
			switch(currentLevel){
				case LEVEL_PROVINCE:
					result = Utility.handleProvinceResponse(coolWeatherDB, response);
					break;
				case LEVEL_CITY:
					result = Utility.handleCityResponse(coolWeatherDB, response, selectedProvince.getId());
					break;
				case LEVEL_COUNTY:
					result = Utility.handleCountyResponse(coolWeatherDB, response, selectedCity.getId());
					break;
				default:
					break;
			}
			if(result){
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//closeProgressDialog();
						progressDialog.dismiss();
						switch(currentLevel){
							case LEVEL_PROVINCE:
								queryProvince();
								break;
							case LEVEL_CITY:
								queryCity();
								break;
							case LEVEL_COUNTY:
								queryCounty();
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
			runOnUiThread(new Runnable() {
				public void run() {
					//closeProgressDialog();
					progressDialog.dismiss();
					Toast.makeText(ChooseAreaActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
				}
			});
		}
	};

	public void onBackPressed(){
		switch(currentLevel){
		case LEVEL_PROVINCE:
			queryProvince();
			break;
		case LEVEL_CITY:
			queryCity();
			break;
		case LEVEL_COUNTY:
			queryCounty();
			break;
		default:
			finish();
			break;
		}
	}
}
