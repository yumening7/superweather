package com.zihangege.superweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	public static void senHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				InputStream is=null;
				InputStreamReader isr=null;
				BufferedReader br=null;
				try{
					URL url=new URL(address);
					connection=(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					is=connection.getInputStream();
					isr=new InputStreamReader(is);
					br=new BufferedReader(isr);
					StringBuilder sb=new StringBuilder();
					String line;
					while((line=br.readLine())!=null){
						sb.append(line);
					}
					if(listener!=null){
						listener.onSuccess(sb.toString());
					}
				}catch(Exception e){
					if(listener!=null){
						listener.onFailed(e);
					}
				}finally{
					try{
						if(connection!=null){
							connection.disconnect();
						}
						if(is!=null){
							is.close();
						}
						if(isr!=null){
							isr.close();
						}
						if(br!=null){
							br.close();
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
