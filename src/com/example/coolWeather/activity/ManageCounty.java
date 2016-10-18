package com.example.coolWeather.activity;

import java.util.ArrayList;
import java.util.Arrays;

import com.example.greattest.R;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ManageCounty extends RootActivity {
	
	private PullToRefreshListView refreshListView;
	private ArrayList<String> dataList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_county);
		initData();
		initView();
	}
	
	private void initData(){
		String[]strArray = {"aaa","bb", "cc", "dd", "ee", "ff", "gg", "hh", "ii", "jj", "kk"};
		dataList = new ArrayList<String>();
		dataList.addAll(Arrays.asList(strArray));
		
		
	}
	
	private void initView(){
		refreshListView = (PullToRefreshListView) findViewById(R.id.pulltorefreshListView);
		refreshListView.setMode(Mode.PULL_FROM_START);
		refreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {//监听器2是两个重写函数，上啦和下拉；监听器1只有一个
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				ILoadingLayout pullLayout = refreshView.getLoadingLayoutProxy(true, false);
				pullLayout.setPullLabel("xia la shua xin");
				pullLayout.setRefreshingLabel("zheng zai la");
				pullLayout.setReleaseLabel("shi fang shua xin");
				
			}
		});
		
		ListView listView = refreshListView.getRefreshableView();
		ArrayAdapter madapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(madapter);
	}
	
	private class MyAsyncTask extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
            try {  
                Thread.sleep(4000);  
            } catch (InterruptedException e) {  
            }  
			publishProgress();
			
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
	    /**  
	     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
	     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
	     */  
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			refreshListView.onRefreshComplete();
			super.onPostExecute(result);
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
	}
}
