package com.example.coolWeather.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

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
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		
		View view = viewList.get(position);
		view.setTag(position);
		container.addView(view);
		return view;
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}
	//销毁position位置的界面
	@Override
	public void destroyItem(ViewGroup view, int position, Object arg2) {
		view.removeView(viewList.get(position));       
	}

	@Override
	//判断是否由对象生成界面
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}

}
