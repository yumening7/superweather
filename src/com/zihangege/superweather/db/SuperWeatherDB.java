package com.zihangege.superweather.db;

import java.util.ArrayList;
import java.util.List;

import com.zihangege.superweather.model.City;
import com.zihangege.superweather.model.County;
import com.zihangege.superweather.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SuperWeatherDB {
	/*
	 * ���ݿ���
	 */
	public static final String DB_NAME="super_weather";
	
	/*
	 * ���ݿ�汾
	 */
	public static final int VERSION=1;
	
	private static SuperWeatherDB superWeatherDB;
	
	private SQLiteDatabase db;
	
	/*
	 * ���췽��˽�л�
	 */
	
	private SuperWeatherDB(Context context){
		WeatherSQLiteOpenHelper dbHelper=new WeatherSQLiteOpenHelper
				(context, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	
	/*
	 * ��ȡSuperWeatherDBʵ��
	 */
	public synchronized static SuperWeatherDB getInstance(Context context){
		if(superWeatherDB==null){
			superWeatherDB=new SuperWeatherDB(context);
		}
		return superWeatherDB;
	}
	
	/*
	 * ��Provinceʵ���洢�����ݿ�
	 */
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	/*
	 * �����ݿ��ȡȫ������ʡ����Ϣ
	 */
	public List<Province> loadProvinces(){
		List<Province> list=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		//����ƶ�����һ������
		if(cursor.moveToFirst()){
			do{
				//����Province����
				Province province=new Province();
				//��ȡid
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				//��ȡname
				province.setProvinceName(cursor.getString(
						cursor.getColumnIndex("province_name")));
				//��ȡcode
				province.setProvinceCode(cursor.getString(
						cursor.getColumnIndex("province_code")));
				//���ӵ�list����
				list.add(province);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	/*
	 * ��Cityʵ�����浽���ݿ�
	 */
	public void saveCity(City city){
		ContentValues values=new ContentValues();
		values.put("city_name", city.getCityName());
		values.put("city_code", city.getCityCode());
		values.put("province_id", city.getProvinceId());
		db.insert("City", null, values);
	}
	
	/*
	 * �����ݿ��ȡĳʡ�����еĳ�����Ϣ
	 */
	public List<City> loadCities(int provinceId){
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id=?",
				new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	/*
	 * ��countyʵ���浽���ݿ�
	 */
	public void saveCounty(County county){
		if(county!=null){
			ContentValues values=new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	
	/*
	 * �����ݿ��ȡĳ���������е�����Ϣ
	 */
	public List<County> loadCounties(int cityId){
		List<County> list=new ArrayList<County>();
		Cursor cursor=db.query("County", null, "city_id=?",
				new String[] {String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county=new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(
						cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(
						cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			}while(cursor.moveToNext());
		}
		return list;
	}
}