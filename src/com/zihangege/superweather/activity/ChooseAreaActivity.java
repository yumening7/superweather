package com.zihangege.superweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zihangege.superweather.R;
import com.zihangege.superweather.db.SuperWeatherDB;
import com.zihangege.superweather.model.City;
import com.zihangege.superweather.model.County;
import com.zihangege.superweather.model.Province;
import com.zihangege.superweather.util.HttpCallbackListener;
import com.zihangege.superweather.util.HttpUtil;
import com.zihangege.superweather.util.Utility;

public class ChooseAreaActivity extends Activity {
	
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private SuperWeatherDB superWeatherDB;
	//Adapter的数据
	private List<String> dataList=new ArrayList<String>();
	
	/*
	 * 省列表
	 */
	private List<Province> provinceList;
	/*
	 * 市列表
	 */
	private List<City> cityList;
	/*
	 * 县列表
	 */
	private List<County> countyList;
	/*
	 * 选中的省
	 */
	private Province selectedProvince;
	/*
	 * 选中的市
	 */
	private City selectedCity;
	/*
	 * 当前显示的等级
	 */
	private int currentLevel;
	
	private long currentTime;
	private long lastTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences preferences=getSharedPreferences("weatherInfo", MODE_PRIVATE);
		//判断是否已经选择过城市，并且不是从WeatherActivity界面跳转过来的，如果是则直接跳入天气界面
		if(preferences.getBoolean("city_selected", false)
				&&!getIntent().getBooleanExtra("from_weather_activity", false)){
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView) findViewById(R.id.list_view);
		titleText=(TextView) findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		//获取一个数据库助手实例
		superWeatherDB=SuperWeatherDB.getInstance(this);
		//设置ListView的监听
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(currentLevel==LEVEL_PROVINCE){
					//用当前点击的子项下标去获取对应下标的省份
					selectedProvince=provinceList.get(position);
					//查询市
					queryCities();
				}else if(currentLevel==LEVEL_CITY){
					//用当前点击的子项下标去获取对应下标的城市
					selectedCity=cityList.get(position);
					//查询县
					queryCounties();
				}else if(currentLevel==LEVEL_COUNTY){
					//获取当前点击的县的代号,然后传入给WeatherActivity
//					String countyCode=countyList.get(position).getCountyCode();
//					System.out.println("当前县代号 ：  "+countyCode);
//					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
//					intent.putExtra("county_code", countyCode);
					//获取当前点击的县的Name,然后传入给WeatherActivity
					String countyName=countyList.get(position).getCountyName();
					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_name", countyName);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvinces();
	}
	
	//查询所有省
	private void queryProvinces() {
		provinceList=superWeatherDB.loadProvinces();
		if(provinceList.size()>0){
			//清空Adapter中的数据
			dataList.clear();
			//遍历provinceList集合
			for(Province province:provinceList){
				//用省份的名字给dataList赋值
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	//查询选中的省下的所有市
	private void queryCities(){
		//优先从数据库中获取
		cityList=superWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	//查询选中的市下的所有县
	private void queryCounties(){
		countyList=superWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	//从服务器加载
	private void queryFromServer(String code, final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.senHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onSuccess(String response) {
				boolean result=false;
				if("province".equals(type)){
					result=Utility.handleProvincesResponse(superWeatherDB, response);
				}else if("city".equals(type)){
					result=Utility.handleCitiesResponse(superWeatherDB, response, 
							selectedProvince.getId());
				}else if("county".equals(type)){
					result=Utility.handleCountiesResponse(superWeatherDB, response,
							selectedCity.getId());
				}
				if(result){
					//通过runOnUiThread()方法回到主线程处理逻辑,也可以用Handle
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
							closeProgressDialog();
						}
					});
				}
			}
			
			@Override
			public void onFailed(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", 0).show();
					}
				});
			}
		});
	}
	
	private void showProgressDialog() {
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			//在对话框外面点击，对话框消失；防止4.0版本对话框崩溃
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	/*
	 * 捕获返回键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
	 */
	@Override
	public void onBackPressed() {
		//该方法默认退出Activity，重写时要去掉
//		super.onBackPressed();			
		if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel==LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
}
