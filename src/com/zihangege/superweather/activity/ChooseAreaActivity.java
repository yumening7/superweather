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
	//Adapter������
	private List<String> dataList=new ArrayList<String>();
	
	/*
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	/*
	 * ���б�
	 */
	private List<City> cityList;
	/*
	 * ���б�
	 */
	private List<County> countyList;
	/*
	 * ѡ�е�ʡ
	 */
	private Province selectedProvince;
	/*
	 * ѡ�е���
	 */
	private City selectedCity;
	/*
	 * ��ǰ��ʾ�ĵȼ�
	 */
	private int currentLevel;
	
	private long currentTime;
	private long lastTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences preferences=getSharedPreferences("weatherInfo", MODE_PRIVATE);
		//�ж��Ƿ��Ѿ�ѡ������У����Ҳ��Ǵ�WeatherActivity������ת�����ģ��������ֱ��������������
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
		//��ȡһ�����ݿ�����ʵ��
		superWeatherDB=SuperWeatherDB.getInstance(this);
		//����ListView�ļ���
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(currentLevel==LEVEL_PROVINCE){
					//�õ�ǰ����������±�ȥ��ȡ��Ӧ�±��ʡ��
					selectedProvince=provinceList.get(position);
					//��ѯ��
					queryCities();
				}else if(currentLevel==LEVEL_CITY){
					//�õ�ǰ����������±�ȥ��ȡ��Ӧ�±�ĳ���
					selectedCity=cityList.get(position);
					//��ѯ��
					queryCounties();
				}else if(currentLevel==LEVEL_COUNTY){
					//��ȡ��ǰ������صĴ���,Ȼ�����WeatherActivity
//					String countyCode=countyList.get(position).getCountyCode();
//					System.out.println("��ǰ�ش��� ��  "+countyCode);
//					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
//					intent.putExtra("county_code", countyCode);
					//��ȡ��ǰ������ص�Name,Ȼ�����WeatherActivity
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
	
	//��ѯ����ʡ
	private void queryProvinces() {
		provinceList=superWeatherDB.loadProvinces();
		if(provinceList.size()>0){
			//���Adapter�е�����
			dataList.clear();
			//����provinceList����
			for(Province province:provinceList){
				//��ʡ�ݵ����ָ�dataList��ֵ
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	//��ѯѡ�е�ʡ�µ�������
	private void queryCities(){
		//���ȴ����ݿ��л�ȡ
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
	
	//��ѯѡ�е����µ�������
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
	
	//�ӷ���������
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
					//ͨ��runOnUiThread()�����ص����̴߳����߼�,Ҳ������Handle
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", 0).show();
					}
				});
			}
		});
	}
	
	private void showProgressDialog() {
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			//�ڶԻ������������Ի�����ʧ����ֹ4.0�汾�Ի������
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
	 * ���񷵻ؼ������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�
	 */
	@Override
	public void onBackPressed() {
		//�÷���Ĭ���˳�Activity����дʱҪȥ��
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
