package com.example.happyday.util;

/**
 * Created by luoqing on 2015/6/17.
 * �ص����񷵻صĽ��
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
