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
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(
			SuperWeatherDB superWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			//先用，号分隔出单独的省份与代号
			String[] allProvinces=response.split(",");
			//如果数组有元素
			if(allProvinces!=null&&allProvinces.length>0){
				//遍历数组
				for(String p:allProvinces){
					//|号为转义符，所以要加\\
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
	 * 解析和处理服务器返回的市级数据
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
	 * 解析和处理服务器返回的县级数据
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
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
	 */
//	public static void handleWeatherResponse(Context context,String response){
//		try {
//			JSONObject jsonObject=new JSONObject(response);
//			//取出JSON中的weatherInfo(天气信息)元素
//			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
//			String cityName=weatherInfo.getString("city");
//			String weatherCode=weatherInfo.getString("cityid");
//			String publishTime=weatherInfo.getString("ptime");
//			String temp1=weatherInfo.getString("temp1");
//			String temp2=weatherInfo.getString("temp2");
//			String weatherCondition=weatherInfo.getString("weather");
//			System.out.println("加载的所有数据为:"+cityName+"\n"+weatherCode+"\n"+
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
			//获取当前城市
			JSONObject resultsObj=results.getJSONObject(0);
			String cityName=resultsObj.getString("currentCity");
//			System.out.println("当前点击的城市为"+cityName);
			//获取当前天气状况
			JSONArray weatherArr=resultsObj.getJSONArray("weather_data");
			JSONObject weatherArr0=weatherArr.getJSONObject(0);
			//获取当前温度
			String weatherCondition=weatherArr0.getString("weather");
			String temp=weatherArr0.getString("temperature");
			
			saveWeatherInfo(context, cityName, temp, weatherCondition);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中
	 */
//	private static void saveWeatherInfo(Context context, String cityName,
//			String weatherCode, String publishTime, String temp1, String temp2,
//			String weatherCondition) {
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
//		Editor edit=context.getSharedPreferences("weatherInfo", context.MODE_PRIVATE).edit();
//		//用于判断当前是否已经选择过城市
//		edit.putBoolean("city_selected", true);
//		edit.putString("city_name", cityName);
//		edit.putString("weather_code", weatherCondition);
//		edit.putString("publish_time", publishTime);
//		edit.putString("temp1", temp1);
//		edit.putString("temp2", temp2);
//		edit.putString("weather_condition", weatherCondition);
//		//new Date()为获取当前日期
//		edit.putString("current_date", sdf.format(new Date()));
//		edit.commit();
//	}
	private static void saveWeatherInfo(Context context, String cityName,String temp,
			String weatherCondition) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		Editor edit=context.getSharedPreferences("weatherInfo", context.MODE_PRIVATE).edit();
		//用于判断当前是否已经选择过城市
		edit.putBoolean("city_selected", true);
		edit.putString("city_name", cityName);
		edit.putString("temp", temp);
		edit.putString("weather_condition", weatherCondition);
		//new Date()为获取当前日期
		edit.putString("current_date", sdf.format(new Date()));
		edit.commit();
	}
}
