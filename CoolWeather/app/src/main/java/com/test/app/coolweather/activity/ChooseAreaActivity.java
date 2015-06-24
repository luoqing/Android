package com.test.app.coolweather.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.app.coolweather.R;
import com.test.app.coolweather.db.CoolWeatherDB;
import com.test.app.coolweather.model.City;
import com.test.app.coolweather.model.Country;
import com.test.app.coolweather.model.Province;
import com.test.app.coolweather.util.HttpCallbackListener;
import com.test.app.coolweather.util.HttpUtil;
import com.test.app.coolweather.util.Utility;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by luoqing on 2015/6/17.
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;

    private ListView listView;
    private TextView titleText;

    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;

    private List<String> datalist = new ArrayList<String>();
    private int currentLevel;

    private Province selectedProvince;
    private City selectedCity;
    private Country selectedCountry;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;

    private ProgressDialog progressDialog;

    private  boolean is_from_weather_activity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);

        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this); // Ϊʲô��Ҫ��һ�еĴ���

        is_from_weather_activity = getIntent().getBooleanExtra("from_weather_activity", false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    // ��ѯ������Ϣ
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    // ��ѯ�������Ϣ
                    queryCountries();
                }
                else if (currentLevel == LEVEL_COUNTRY){
                    // ��ת��������Ϣ
                    selectedCountry = countryList.get(position);
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("countryCode", selectedCountry.getCountryCode());
                    startActivity(intent);
                }
            }
        });
        // ��ѯʡ����Ϣ
      queryProvinces();
    }


    /**
     * ��ȡȫ��ʡ�ݵ���Ϣ���Ȳ�db���ٲ��������
     */
    private void queryProvinces(){
        provinceList = coolWeatherDB.loadProvinces();

        if (provinceList.size() > 0){
            datalist.clear();
            for (Province province:provinceList){
                datalist.add(province.getProvinceName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("China");
            currentLevel = LEVEL_PROVINCE;
        }
        else{
            // �ӷ������ϲ�ѯ�����ҽ���Щ���ݽ������
            queryFromServer(null, "province");
        }
    }

    /**
     * ��ȡselectedProvince�ĳ��е������Ϣ
     */
    private void queryCities(){
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());

        if (cityList.size() > 0){
            datalist.clear();
            for (City city:cityList){
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;

        }else{
            queryFromServer(selectedProvince.getProvinceCode(), "city");

        }

    }

    /**
     * ��ȡselectedCity�ĳ���������Ϣ
     */
    private void queryCountries() {
        countryList = coolWeatherDB.loadCounties(selectedCity.getId());

        if (countryList.size() > 0){
            datalist.clear();
            for (Country country : countryList){
                datalist.add(country.getCountryName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTRY;

        }else{
            queryFromServer(selectedCity.getCityCode(), "country");
        }
    }

    // �ӷ������ϻ�ȡ�������
    private void queryFromServer(final String code, final String type){
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                closeProgressDialog();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(coolWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
                } else if ("country".equals(type)) {
                    result = Utility.handleCountriesResponse(coolWeatherDB, response, selectedCity.getId());
                }

                // ��̨�����ڳ��Ի�ȡ����صĳ�����Ϣ
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("country".equals(type)) {
                                queryCountries();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "load failure", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });

    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("loading...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    public void onBackPressed(){
        if (currentLevel == LEVEL_COUNTRY) {
            queryCities();
        }else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        }
        else{
            if (is_from_weather_activity){
                startActivity(new Intent(ChooseAreaActivity.this, WeatherActivity.class));
            }
            finish();
        }
    }


}
