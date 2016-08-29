package com.zihangege.superweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.AvoidXfermode.Mode;
import android.text.TextUtils;

import com.zihangege.superweather.db.SuperWeatherDB;
import com.zihangege.superweather.model.City;
import com.zihangege.superweather.model.County;
import com.zihangege.superweather.model.Province;

public class Utility {
	
	/*
	 * �����ʹ�����������ص�ʡ������
	 */
	public synchronized static boolean handleProvincesResponse(
			SuperWeatherDB superWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			//���ã��ŷָ���������ʡ�������
			String[] allProvinces=response.split(",");
			//���������Ԫ��
			if(allProvinces!=null&&allProvinces.length>0){
				//��������
				for(String p:allProvinces){
					//|��Ϊת���������Ҫ��\\
					String[] array=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					superWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * �����ʹ�����������ص��м�����
	 */
	public static boolean handleCitiesResponse(
			SuperWeatherDB superWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities=response.split(",");
			if(allCities!=null&&allCities.length>0){
				for(String c:allCities){
					String[] array=c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					superWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * �����ʹ�����������ص��ؼ�����
	 */
	public static boolean handleCountiesResponse(
			SuperWeatherDB superWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounteis=response.split(",");
			if(allCounteis!=null&&allCounteis.length>0){
				for(String c:allCounteis){
					String[] array=c.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					superWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * �������������ص�JSON���ݣ����������������ݴ洢������
	 */
//	public static void handleWeatherResponse(Context context,String response){
//		try {
//			JSONObject jsonObject=new JSONObject(response);
//			//ȡ��JSON�е�weatherInfo(������Ϣ)Ԫ��
//			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
//			String cityName=weatherInfo.getString("city");
//			String weatherCode=weatherInfo.getString("cityid");
//			String publishTime=weatherInfo.getString("ptime");
//			String temp1=weatherInfo.getString("temp1");
//			String temp2=weatherInfo.getString("temp2");
//			String weatherCondition=weatherInfo.getString("weather");
//			System.out.println("���ص���������Ϊ:"+cityName+"\n"+weatherCode+"\n"+
//					publishTime+"\n"+temp1+"\n"+temp2+"\n"+weatherCondition);
//			saveWeatherInfo(context,cityName,weatherCode,publishTime,temp1,temp2,weatherCondition);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject=new JSONObject(response);
//			System.out.println(jsonObject.toString());
			JSONArray results=jsonObject.getJSONArray("results");
			//��ȡ��ǰ����
			JSONObject resultsObj=results.getJSONObject(0);
			String cityName=resultsObj.getString("currentCity");
//			System.out.println("��ǰ����ĳ���Ϊ"+cityName);
			//��ȡ��ǰ����״��
			JSONArray weatherArr=resultsObj.getJSONArray("weather_data");
			JSONObject weatherArr0=weatherArr.getJSONObject(0);
			//��ȡ��ǰ�¶�
			String weatherCondition=weatherArr0.getString("weather");
			String temp=weatherArr0.getString("temperature");
			
			saveWeatherInfo(context, cityName, temp, weatherCondition);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * �����������ص�����������Ϣ�洢��SharedPreferences�ļ���
	 */
//	private static void saveWeatherInfo(Context context, String cityName,
//			String weatherCode, String publishTime, String temp1, String temp2,
//			String weatherCondition) {
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
//		Editor edit=context.getSharedPreferences("weatherInfo", context.MODE_PRIVATE).edit();
//		//�����жϵ�ǰ�Ƿ��Ѿ�ѡ�������
//		edit.putBoolean("city_selected", true);
//		edit.putString("city_name", cityName);
//		edit.putString("weather_code", weatherCondition);
//		edit.putString("publish_time", publishTime);
//		edit.putString("temp1", temp1);
//		edit.putString("temp2", temp2);
//		edit.putString("weather_condition", weatherCondition);
//		//new Date()Ϊ��ȡ��ǰ����
//		edit.putString("current_date", sdf.format(new Date()));
//		edit.commit();
//	}
	private static void saveWeatherInfo(Context context, String cityName,String temp,
			String weatherCondition) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		Editor edit=context.getSharedPreferences("weatherInfo", context.MODE_PRIVATE).edit();
		//�����жϵ�ǰ�Ƿ��Ѿ�ѡ�������
		edit.putBoolean("city_selected", true);
		edit.putString("city_name", cityName);
		edit.putString("temp", temp);
		edit.putString("weather_condition", weatherCondition);
		//new Date()Ϊ��ȡ��ǰ����
		edit.putString("current_date", sdf.format(new Date()));
		edit.commit();
	}
}
