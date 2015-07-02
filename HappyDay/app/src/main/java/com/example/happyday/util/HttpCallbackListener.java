package com.example.happyday.util;

/**
 * Created by luoqing on 2015/6/17.
 * 回掉服务返回的结果
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
