package com.example.coolWeather.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.coolWeather.db.CoolWeatherDB;
import com.example.coolWeather.model.StarCounty;
import com.example.coolWeather.widget.DragLayout;
import com.example.greattest.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewAdapter extends ArrayAdapter<StarCounty> {


	private ArrayList<StarCounty> dataList;
	private int	itemLayoutId;
	private HoldChild4ItemView holdChild4ItemView;
	private int index = -1;
	private ListView listView;
	private CoolWeatherDB db;
	
	public ListViewAdapter(Context context, ListView listView, int resource, List<StarCounty> objects, CoolWeatherDB db) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.dataList = (ArrayList<StarCounty>) objects;
		this.itemLayoutId = resource;
		this.listView = listView;
		this.db = db;
	}

	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(dataList != null){
			return dataList.size();
		}
		return 0;
	}

	@Override
	public StarCounty getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		StarCounty starCounty= this.dataList.get(position);
		View view;

		if(convertView != null){
			view = convertView;
			holdChild4ItemView = (HoldChild4ItemView) view.getTag();
		}else{
			view = LayoutInflater.from(getContext()).inflate(itemLayoutId, null);
			
			holdChild4ItemView = new HoldChild4ItemView();
			holdChild4ItemView.content = (TextView) view.findViewById(R.id.textContent);
			holdChild4ItemView.delete = (TextView) view.findViewById(R.id.textDelete);
			holdChild4ItemView.dragLayout = (DragLayout) view.findViewById(R.id.dragLayout);

			view.setTag(holdChild4ItemView);
		}
		holdChild4ItemView.content.setText(starCounty.weather.getResult().get(0).getDistrct());
		holdChild4ItemView.delete.setText("删除");
		holdChild4ItemView.delete.setVisibility(View.GONE);
		holdChild4ItemView.delete.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				db.removeStarCounty(dataList.get(position));
				dataList.remove(position);
				notifyDataSetChanged();
				Toast.makeText(getContext(), "删除成功！", Toast.LENGTH_SHORT).show();
			}
		});
		
		holdChild4ItemView.dragLayout.setOnslide(new DragLayout.slideListenerCallBack() {			
			@Override
			public void onSlided(Boolean flag) {
				// TODO Auto-generated method stub
				System.out.println("=================index = " + index);
//				if(flag){
//					if(index != position && index != -1){
//						if(listView.getChildAt(index) != null){
//							DragLayout itemLayout = (DragLayout) listView.getChildAt(index-listView.getFirstVisiblePosition()).findViewById(R.id.dragLayout);
//							itemLayout.revert();	
//						}
//					}
//					index = position;
//				}else{
//					if(index == position){
//						index = -1;
//					}
//				}
			}
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Toast.makeText(getContext(), "This is onClick!", Toast.LENGTH_LONG).show();
			}
		});
		return view;
	}

	private class HoldChild4ItemView{
		DragLayout dragLayout;
		TextView content;
		TextView delete;
	}
}
