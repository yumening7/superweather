package com.zihangege.superweather.service;

import com.zihangege.superweather.receiver.AutoUpdateReceiver;
import com.zihangege.superweather.util.HttpCallbackListener;
import com.zihangege.superweather.util.HttpUtil;
import com.zihangege.superweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		//获取定时任务助手
		AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
		//8小时的毫秒数
		int anHour=8*60*60*1000;
		//设置触发时间
		long triggerAtTime=SystemClock.elapsedRealtime()+anHour;
		Intent i=new Intent(this,AutoUpdateReceiver.class);
		//意图处理
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent, 0);
		//设置定时器类型,触发时间,意图处理
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	/*
	 * 更新天气信息
	 */
	private void updateWeather(){
		SharedPreferences preferences=getSharedPreferences("weatherInfo", MODE_PRIVATE);
		//取出选择过的城市
		String countyName=preferences.getString("city_name", "");
		//组装访问地址
		String address="http://api.map.baidu.com/telematics/v3/weather?location=" +countyName+
				"&output=json&ak=j5DLEawQCFdqFma5bhDkBExGsMsmcan1";
		//发送请求
		HttpUtil.senHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onSuccess(String response) {
				//处理返回的数据,保存在本地
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}
			
			@Override
			public void onFailed(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
