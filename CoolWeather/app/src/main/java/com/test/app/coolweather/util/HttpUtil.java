package com.test.app.coolweather.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by luoqing on 2015/6/17.
 * 用于请求相关接口---至于网络请求这块还是我的薄弱部分，还得好好加强下
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
       // 后台一直尝试请求服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();

                    // 设置请求的相关参数
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    // 获取请求的返回结果，对返回结果进行相关的转化
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String line;
                    while((line = reader.readLine()) != null){
                        response.append(line);
                    }

                    if (listener != null) {
                        listener.onFinish(response.toString());
                    }

                } catch (Exception e) {
                    if (listener != null){
                        listener.onError(e);
                        e.printStackTrace();
                    }

                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }

        }).start();

    }

}
