package com.test.app.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.test.app.coolweather.R;
import com.test.app.coolweather.service.AutoUpdateService;
import com.test.app.coolweather.util.HttpCallbackListener;
import com.test.app.coolweather.util.HttpUtil;
import com.test.app.coolweather.util.Utility;



/**
 * Created by luoqing on 2015/6/23.
 */
public class WeatherActivity extends Activity {

    private TextView cityName;
    private TextView publishTime;
    private TextView weatherDesp;
    private TextView currentDate;
    private TextView temp1;
    private TextView temp2;

    private Button switchCity;
    private Button refreshWeather;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        switchCity = (Button) findViewById(R.id.home);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);

        switchCity.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
            }
        });

        refreshWeather.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String weatherCode = sharedPref.getString("weather_code", "");
                queryWeatherInfoByWeatherCode(weatherCode);
            }
        });

        //  从cache中或者server中查询到相关信息，先查cache，如果cache没查到，再查服务器
        cityName = (TextView) findViewById(R.id.name);
        publishTime = (TextView) findViewById(R.id.publish_time);
        currentDate = (TextView) findViewById(R.id.current_date);
        weatherDesp = (TextView) findViewById(R.id.weather_desp);
        temp1 = (TextView) findViewById(R.id.temp1);
        temp2 = (TextView) findViewById(R.id.temp2);
        String countryCode = getIntent().getStringExtra("countryCode");
        if (!TextUtils.isEmpty(countryCode)){
            queryWeatherInfoByContryCode(countryCode);
        }
        else
        {
            showWeather();
        }

    }

    private void queryWeatherInfoByContryCode(String countryCode){
        String address = "http://www.weather.com.cn/data/list3/city" + countryCode + ".xml";
        queryFromServer(address, "countryCode");

    }

    private void queryWeatherInfoByWeatherCode(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    private void queryFromServer(final String address, final String type){
        Log.d("WEATHER_TEST", "get address - " + address);
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countryCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] arr = response.split("\\|");
                        if (arr != null && arr.length == 2) {
                            String weatherCode = arr[1];
                            queryWeatherInfoByWeatherCode(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        Utility.handleWeatherResponse(WeatherActivity.this, response);
                        // 后台服务一直在获取天气的信息
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 显示天气信息
                                showWeather();

                            }
                        });
                    }
                }

            }

            @Override
            public void onError(Exception e) {
                // 显示同步失败有问题
                publishTime.setText("load failure..");


            }
        });

    }

    private void showWeather(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        cityName.setText(sharedPref.getString("city_name", "城市天气"));
        publishTime.setText(sharedPref.getString("publish_time", ""));
        currentDate.setText(sharedPref.getString("current_date", ""));
        weatherDesp.setText(sharedPref.getString("weather_desp", ""));
        temp1.setText(sharedPref.getString("temp1", ""));
        temp2.setText(sharedPref.getString("temp2", ""));

        startService(new Intent(WeatherActivity.this, AutoUpdateService.class));


    }

}
