package com.test.app.coolweather.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.test.app.coolweather.service.AutoUpdateService;

/**
 * Created by luoqing on 2015/6/23.
 */
public class AutoUpdateRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, AutoUpdateService.class));
    }
}
