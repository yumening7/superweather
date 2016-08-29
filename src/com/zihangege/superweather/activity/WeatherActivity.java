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
	 * ������ʾ������
	 */
	private TextView tv_cityName;
	/*
	 * ������ʾ����ʱ��
	 */
	private TextView tv_publishTime;
	/*
	 * ������ʾ��ǰ����
	 */
	private TextView tv_currentDate;
	/*
	 * ������ʾ����״��
	 */
	private TextView tv_weatherCondition;
//	/*
//	 * ������ʾ����¶�
//	 */
//	private TextView tv_temp1;
//	/*
//	 * ������ʾ����¶�
//	 */
//	private TextView tv_temp2;
	/*
	 * ������ʾ�¶�
	 */
	private TextView tv_temp;
//	/*
//	 * �����л�����
//	 */
//	private Button btn_switch;
//	/*
//	 * ����ˢ������
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
	 * ��ʼ���ؼ�
	 */
	private void init(){
		ll_weatherInfoLayout=(LinearLayout) findViewById(R.id.ll_weatherInfo);
		tv_cityName=(TextView) findViewById(R.id.tv_cityName);
		tv_publishTime=(TextView) findViewById(R.id.tv_publish);
		tv_currentDate=(TextView) findViewById(R.id.tv_currentDate);
		tv_weatherCondition=(TextView) findViewById(R.id.tv_weatherCondition);
		tv_temp=(TextView) findViewById(R.id.tv_temp);
		//�л����а�ť����
		findViewById(R.id.btn_switchCity).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(WeatherActivity.this,ChooseAreaActivity.class);
				//������ǣ����������ͼ�Ǵ�WeatherActivity���ݹ�ȥ��
				intent.putExtra("from_weather_activity", true);
				startActivity(intent);
				finish();
			}
		});
		//ˢ�°�ť����
		findViewById(R.id.btn_refresh).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tv_publishTime.setText("ͬ����...");
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
//			//���ؼ�����ʱ����ѯ��Ӧ������
//			tv_publishTime.setText("ͬ����...");
//			//������������Ϣ���ɼ�
//			ll_weatherInfoLayout.setVisibility(View.INVISIBLE);
//			//�ó������ֲ��ɼ�
//			tv_cityName.setVisibility(View.INVISIBLE);
////			//ͨ�����������ȡ��Ӧ����������
//			queryWeatherCode(countyCode);
////			queayWeatherInfo(countyName);
//		}
		if(!TextUtils.isEmpty(countyName)){
			//���ؼ�Nameʱ,��ѯ��Ӧ������
			tv_publishTime.setText("ͬ����...");
			//������������Ϣ���ɼ�
			ll_weatherInfoLayout.setVisibility(View.INVISIBLE);
			//�ó������ֲ��ɼ�
			tv_cityName.setVisibility(View.INVISIBLE);
//			//ͨ�����������ȡ��Ӧ��������Ϣ
			queayWeatherInfo(countyName);
		}else{
			//���û�н��յ�����,��ʾ֮ǰ�����ù�����ȡ���ݱ��浽�˱���,��ֱ����ʾ���ص�����
			showWeather();
		}
	}
	/*
	 * ��ѯ�ؼ����Ŷ�Ӧ����������
	 */
//	private void queryWeatherCode(String countyCode) {
//		//���ؼ�������ȷ�����ʵ�ַ
//		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
//		//��ȡ��������
//		queryFromServer(address,"countyCode");
//	}
	/*
	 * ��ѯ�������Ŷ�Ӧ��������Ϣ
	 */
//	private void queayWeatherInfo(String weatherCode){
//		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
//		//��ȡ������Ϣ
//		queryFromServer(address, "weatherCode");
//	}
	/*
	 * ͨ��������ѯ��Ӧ��������Ϣ
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
//				//�ж������ؼ�����������������,������������������������Ϣ
//				if("countyCode".equals(type)){
//					if(!TextUtils.isEmpty(response)){
//						String[] array=response.split("\\|");
//						if(array!=null&&array.length==2){
//							String weatherCode=array[1];
//							System.out.println("��ǰ�ض�Ӧ���������� : "+weatherCode);
//							queayWeatherInfo(weatherCode);
//						}
//					}
//				}else if("weatherCode".equals(type)){
//					//�����������ȡ����������Ϣ
//					Utility.handleWeatherResponse(WeatherActivity.this, response);
//					//����Ui���߳�
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
//						tv_publishTime.setText("ͬ��ʧ��");
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
						tv_publishTime.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	
	/*
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
//	private void showWeather(){
//		SharedPreferences preferences=getSharedPreferences("weatherInfo", MODE_PRIVATE);
//		tv_cityName.setText(preferences.getString("city_name", ""));
//		String publish=preferences.getString("publish_time", "");
//		tv_publishTime.setText("����"+publish+"����");
//		tv_currentDate.setText(preferences.getString("current_date", ""));
//		tv_weatherCondition.setText(preferences.getString("weather_condition", ""));
//		tv_temp1.setText(preferences.getString("temp1", ""));
//		tv_temp2.setText(preferences.getString("temp2", ""));
//		//�ó�����Ϣ�͵�ǰ������Ϣ�ɼ�
//		tv_cityName.setVisibility(View.VISIBLE);
//		ll_weatherInfoLayout.setVisibility(View.VISIBLE);
//	}
	private void showWeather(){
		SharedPreferences preferences=getSharedPreferences("weatherInfo", MODE_PRIVATE);
		tv_cityName.setText(preferences.getString("city_name", ""));
		tv_currentDate.setText(preferences.getString("current_date", ""));
		tv_weatherCondition.setText(preferences.getString("weather_condition", ""));
		tv_temp.setText(preferences.getString("temp", ""));
		//�ó�����Ϣ�͵�ǰ������Ϣ�ɼ�
		tv_cityName.setVisibility(View.VISIBLE);
		ll_weatherInfoLayout.setVisibility(View.VISIBLE);
		tv_publishTime.setVisibility(View.INVISIBLE);
		//��������
		Intent intent=new Intent(this,AutoUpdateService.class);
		startService(intent);
	}
}
