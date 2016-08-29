package com.zihangege.superweather.activity;

import com.zihangege.superweather.R;
import com.zihangege.superweather.model.City;
import com.zihangege.superweather.service.AutoUpdateService;
import com.zihangege.superweather.util.HttpCallbackListener;
import com.zihangege.superweather.util.HttpUtil;
import com.zihangege.superweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {
	private LinearLayout ll_weatherInfoLayout;
	/*
	 * 用于显示城市名
	 */
	private TextView tv_cityName;
	/*
	 * 用于显示发布时间
	 */
	private TextView tv_publishTime;
	/*
	 * 用于显示当前日期
	 */
	private TextView tv_currentDate;
	/*
	 * 用于显示天气状况
	 */
	private TextView tv_weatherCondition;
//	/*
//	 * 用于显示最低温度
//	 */
//	private TextView tv_temp1;
//	/*
//	 * 用于显示最高温度
//	 */
//	private TextView tv_temp2;
	/*
	 * 用于显示温度
	 */
	private TextView tv_temp;
//	/*
//	 * 用于切换城市
//	 */
//	private Button btn_switch;
//	/*
//	 * 用于刷新天气
//	 */
//	private Button btn_refresh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		init();
	}
	/*
	 * 初始化控件
	 */
	private void init(){
		ll_weatherInfoLayout=(LinearLayout) findViewById(R.id.ll_weatherInfo);
		tv_cityName=(TextView) findViewById(R.id.tv_cityName);
		tv_publishTime=(TextView) findViewById(R.id.tv_publish);
		tv_currentDate=(TextView) findViewById(R.id.tv_currentDate);
		tv_weatherCondition=(TextView) findViewById(R.id.tv_weatherCondition);
		tv_temp=(TextView) findViewById(R.id.tv_temp);
		//切换城市按钮监听
		findViewById(R.id.btn_switchCity).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(WeatherActivity.this,ChooseAreaActivity.class);
				//做个标记，代表这个意图是从WeatherActivity传递过去的
				intent.putExtra("from_weather_activity", true);
				startActivity(intent);
				finish();
			}
		});
		//刷新按钮监听
		findViewById(R.id.btn_refresh).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tv_publishTime.setText("同步中...");
				tv_publishTime.setVisibility(View.VISIBLE);
				ll_weatherInfoLayout.setVisibility(View.INVISIBLE);
				SharedPreferences preferences=getSharedPreferences("weatherInfo", MODE_PRIVATE);
				String countyName=preferences.getString("city_name", "");
				queayWeatherInfo(countyName);
			}
		});
		
//		String countyCode=getIntent().getStringExtra("county_code");
		String countyName=getIntent().getStringExtra("county_name");
//		if(!TextUtils.isEmpty(countyCode)){
//			//有县级代号时区查询对应的天气
//			tv_publishTime.setText("同步中...");
//			//让所有天气信息不可见
//			ll_weatherInfoLayout.setVisibility(View.INVISIBLE);
//			//让城市名字不可见
//			tv_cityName.setVisibility(View.INVISIBLE);
////			//通过县名请求获取对应的天气代号
//			queryWeatherCode(countyCode);
////			queayWeatherInfo(countyName);
//		}
		if(!TextUtils.isEmpty(countyName)){
			//有县级Name时,查询对应的天气
			tv_publishTime.setText("同步中...");
			//让所有天气信息不可见
			ll_weatherInfoLayout.setVisibility(View.INVISIBLE);
			//让城市名字不可见
			tv_cityName.setVisibility(View.INVISIBLE);
//			//通过县名请求获取对应的天气信息
			queayWeatherInfo(countyName);
		}else{
			//如果没有接收到县名,表示之前已设置过并获取数据保存到了本地,则直接显示本地的数据
			showWeather();
		}
	}
	/*
	 * 查询县级代号对应的天气代号
	 */
//	private void queryWeatherCode(String countyCode) {
//		//用县级代号来确定访问地址
//		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
//		//获取天气代号
//		queryFromServer(address,"countyCode");
//	}
	/*
	 * 查询天气代号对应的天气信息
	 */
//	private void queayWeatherInfo(String weatherCode){
//		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
//		//获取天气信息
//		queryFromServer(address, "weatherCode");
//	}
	/*
	 * 通过县名查询对应的天气信息
	 */
	private void queayWeatherInfo(String countyName){
		String address="http://api.map.baidu.com/telematics/v3/weather?location=" +countyName+
				"&output=json&ak=j5DLEawQCFdqFma5bhDkBExGsMsmcan1";
//		String address="http://api.map.baidu.com/telematics/v3/weather?location=" +countyName+
//				"&output=JSON&ak=NCUfQngufTazjdBu6PX24hVlGh6xbaSv";
		queryFromServer(address);
	}
//	private void queryFromServer(String address, final String type) {
//		HttpUtil.senHttpRequest(address, new HttpCallbackListener() {
//			
//			@Override
//			public void onSuccess(String response) {
//				//判断是用县级代号请求天气代号,还是用天气代号请求天气信息
//				if("countyCode".equals(type)){
//					if(!TextUtils.isEmpty(response)){
//						String[] array=response.split("\\|");
//						if(array!=null&&array.length==2){
//							String weatherCode=array[1];
//							System.out.println("当前县对应的天气代号 : "+weatherCode);
//							queayWeatherInfo(weatherCode);
//						}
//					}
//				}else if("weatherCode".equals(type)){
//					//解析并保存获取到的天气信息
//					Utility.handleWeatherResponse(WeatherActivity.this, response);
//					//返回Ui主线程
//					runOnUiThread(new Runnable() {
//						
//						@Override
//						public void run() {
//							showWeather();
//						}
//					});
//				}
//			}
//			
//			@Override
//			public void onFailed(Exception e) {
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						tv_publishTime.setText("同步失败");
//					}
//				});
//			}
//		});
//	}
	private void queryFromServer(String address) {
		HttpUtil.senHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onSuccess(String response) {
				System.out.println("Success");
				Utility.handleWeatherResponse(WeatherActivity.this, response);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						showWeather();
					}
				});
			}
			
			@Override
			public void onFailed(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tv_publishTime.setText("同步失败");
					}
				});
			}
		});
	}
	
	/*
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
//	private void showWeather(){
//		SharedPreferences preferences=getSharedPreferences("weatherInfo", MODE_PRIVATE);
//		tv_cityName.setText(preferences.getString("city_name", ""));
//		String publish=preferences.getString("publish_time", "");
//		tv_publishTime.setText("今天"+publish+"发布");
//		tv_currentDate.setText(preferences.getString("current_date", ""));
//		tv_weatherCondition.setText(preferences.getString("weather_condition", ""));
//		tv_temp1.setText(preferences.getString("temp1", ""));
//		tv_temp2.setText(preferences.getString("temp2", ""));
//		//让城市信息和当前天气信息可见
//		tv_cityName.setVisibility(View.VISIBLE);
//		ll_weatherInfoLayout.setVisibility(View.VISIBLE);
//	}
	private void showWeather(){
		SharedPreferences preferences=getSharedPreferences("weatherInfo", MODE_PRIVATE);
		tv_cityName.setText(preferences.getString("city_name", ""));
		tv_currentDate.setText(preferences.getString("current_date", ""));
		tv_weatherCondition.setText(preferences.getString("weather_condition", ""));
		tv_temp.setText(preferences.getString("temp", ""));
		//让城市信息和当前天气信息可见
		tv_cityName.setVisibility(View.VISIBLE);
		ll_weatherInfoLayout.setVisibility(View.VISIBLE);
		tv_publishTime.setVisibility(View.INVISIBLE);
		//启动服务
		Intent intent=new Intent(this,AutoUpdateService.class);
		startService(intent);
	}
}
