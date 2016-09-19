package com.example.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
		new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.connect();
					InputStream in = connection.getInputStream();
					InputStreamReader inr = new InputStreamReader(in);
					BufferedReader reader = new BufferedReader(inr);
					String line;
					StringBuilder response = new StringBuilder();
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					System.out.println("data: " + response.toString());
					if(listener != null){
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(listener != null){
						listener.onError(e);
					}
				} finally {
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}