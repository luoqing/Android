package com.example.happyday.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.happyday.db.HappyDayDB;
import com.example.happyday.model.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by luoqing on 2015/7/1.
 */
public class Utility {

    // 必须首先处理了省份信息再处理后续的信息
    public  static boolean handleLocationsResponse(HappyDayDB happyDB, String response, int parent_id, int type){
        if (!TextUtils.isEmpty(response)){
            String[] allLocations = response.split(",");
            if (allLocations != null && allLocations.length > 0){
                for (String p:allLocations){
                    String [] array = p.split("\\|");
                    Location location = new Location();
                    location.setCode(array[0]);
                    location.setName(array[1]);
                    location.setParentId(parent_id);
                    location.setType(type);

                    happyDB.saveLocation(location);
                }

                return true;
            }

        }
        return false;

    }

    public static void handleWeatherResponse(Context context, String response){
        // 对获取到的json串进行分析
        try {
            Log.d("HappyDay", "Weather Info Analysis - " + response);

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
