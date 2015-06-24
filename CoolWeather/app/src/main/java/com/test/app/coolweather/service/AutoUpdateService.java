package com.test.app.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.test.app.coolweather.BroadcastReceiver.AutoUpdateRecevier;
import com.test.app.coolweather.util.HttpCallbackListener;
import com.test.app.coolweather.util.HttpUtil;
import com.test.app.coolweather.util.Utility;

/**
 * Created by luoqing on 2015/6/23.
 */
public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();

        // 定时运行这段代码
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int eightHours = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + eightHours;

        Intent i = new Intent(this, AutoUpdateRecevier.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);

    }

    public void updateWeather(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = sharedPref.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (!TextUtils.isEmpty(response)) {
                    Utility.handleWeatherResponse(AutoUpdateService.this, response);
                }
            }

            @Override
            public void onError(Exception e) {
               e.printStackTrace();
            }
        });

    }


}
