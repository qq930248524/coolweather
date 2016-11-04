package com.example.coolWeather.model;
import java.util.ArrayList;
import java.util.List;
public class StarWeather {
	public StarWeather() {
		// TODO Auto-generated constructor stub
		result = new ArrayList<Result>();
		result.add(new Result());
	}
	private String msg;

	private List<Result> result ;

	private String retCode;

	public void setMsg(String msg){
		this.msg = msg;
	}
	public String getMsg(){
		return this.msg;
	}
	public void setResult(List<Result> result){
		this.result = result;
	}
	public List<Result> getResult(){
		return this.result;
	}
	public void setRetCode(String retCode){
		this.retCode = retCode;
	}
	public String getRetCode(){
		return this.retCode;
	}

}