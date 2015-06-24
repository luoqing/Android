package com.test.app.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.test.app.coolweather.db.CoolWeatherDB;
import com.test.app.coolweather.model.City;
import com.test.app.coolweather.model.Country;
import com.test.app.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by luoqing on 2015/6/17.
 * 用于解析请求接口所得到的数据
 */
public class Utility {

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0){
                for (String p:allProvinces){
                    String [] array =p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);

                    // 将解析出来的数据存储到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;

    }

    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0){
                for (String p:allCities){
                    String [] array =p.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);

                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }

        return false;

    }

    public static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCountries = response.split(",");
            if (allCountries != null && allCountries.length > 0){
                for (String p:allCountries){
                    String [] array =p.split("\\|");
                    Country country = new Country();
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    country.setCityId(cityId);
                    coolWeatherDB.saveCountry(country);
                }
                return true;
            }
        }

        return false;
    }

    public static void handleWeatherResponse(Context context, String response){
        // 对获取到的json串进行分析
        try {
            Log.d("WEATHER_TEST", "Weather Info Analysis - " + response );

            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weather_desp = weatherInfo.getString("weather");
            String publish_time = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weather_desp, publish_time);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // 由于天气信息随着时间而更新，所以不保存db，但是为了防止每次都要去请求服务器，保存在本地的cache中。
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weather_desp, String publish_time){

        SimpleDateFormat current_date = new SimpleDateFormat("yyyy-M-d", Locale.CHINA);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weather_desp);
        editor.putString("publish_time", publish_time);
        editor.putString("current_date", current_date.format(new Date()));
        editor.putString("weather_code", weatherCode);
        editor.commit();
    }



}
