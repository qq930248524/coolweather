package com.example.coolWeather.gson;

import java.util.List;


public class Travel {
	Boolean ret;
	Integer errcode;
	String errmsg;
	Integer ver;
	Data data;

	class Data {
		List<Books> books;
		String count;
	}

	class Books {
		String bookUrl;
		String title;
		String headImage;
		String userName;
		String userHeadImg;
		String startTime;
		Integer routeDays;
		String bookImgNum;
		Integer viewCount;
		Integer likeCount;
		Integer commentCount;
		String text;
		Boolean elite;
	}

}
