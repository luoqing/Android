package com.test.app.coolweather.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by luoqing on 2015/6/17.
 * ����������ؽӿ�---��������������黹���ҵı������֣����úúü�ǿ��
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
       // ��̨һֱ�������������
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();

                    // �����������ز���
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    // ��ȡ����ķ��ؽ�����Է��ؽ��������ص�ת��
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
