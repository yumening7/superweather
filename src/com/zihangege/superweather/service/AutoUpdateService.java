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
		//��ȡ��ʱ��������
		AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
		//8Сʱ�ĺ�����
		int anHour=8*60*60*1000;
		//���ô���ʱ��
		long triggerAtTime=SystemClock.elapsedRealtime()+anHour;
		Intent i=new Intent(this,AutoUpdateReceiver.class);
		//��ͼ����
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent, 0);
		//���ö�ʱ������,����ʱ��,��ͼ����
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	/*
	 * ����������Ϣ
	 */
	private void updateWeather(){
		SharedPreferences preferences=getSharedPreferences("weatherInfo", MODE_PRIVATE);
		//ȡ��ѡ����ĳ���
		String countyName=preferences.getString("city_name", "");
		//��װ���ʵ�ַ
		String address="http://api.map.baidu.com/telematics/v3/weather?location=" +countyName+
				"&output=json&ak=j5DLEawQCFdqFma5bhDkBExGsMsmcan1";
		//��������
		HttpUtil.senHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onSuccess(String response) {
				//�����ص�����,�����ڱ���
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}
			
			@Override
			public void onFailed(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
