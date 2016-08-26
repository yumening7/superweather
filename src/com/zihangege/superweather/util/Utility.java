package com.zihangege.superweather.util;

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
}
