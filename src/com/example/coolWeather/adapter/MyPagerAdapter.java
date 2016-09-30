package com.example.coolWeather.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MyPagerAdapter extends PagerAdapter {
	public ArrayList<View> viewList;


	public MyPagerAdapter(ArrayList<View> viewList) {
		// TODO Auto-generated constructor stub
		this.viewList = (ArrayList<View>) viewList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(viewList != null){
			return viewList.size();
		}
		return 0;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub
		((ViewPager)container).addView(viewList.get(position), 0);
		return viewList.get(position);
	}

	//销毁position位置的界面
	@Override
	public void destroyItem(View view, int position, Object arg2) {
		((ViewPager) view).removeView(viewList.get(position));       
	}

	@Override
	//判断是否由对象生成界面
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}

}
