package com.example.happyday.activity;

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
import android.widget.Toast;

import com.example.happyday.R;
import com.example.happyday.model.AccessTokenKeeper;
import com.example.happyday.model.Constants;
import com.example.happyday.util.HttpCallbackListener;
import com.example.happyday.util.HttpUtil;
import com.example.happyday.util.Utility;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;


/**
 * Created by luoqing on 2015/6/23.
 */
public class WeatherActivity extends Activity  implements IWeiboHandler.Response  {

    private TextView cityName;
    private TextView publishTime;
    private TextView weatherDesp;
    private TextView currentDate;
    private TextView temp1;
    private TextView temp2;

    private Button switchCity;
    private Button refreshWeather;
    private Button shareToWeibo;
    private IWeiboShareAPI mWeiboShareAPI = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        switchCity = (Button) findViewById(R.id.home);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        shareToWeibo = (Button) findViewById(R.id.btn_share);


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


        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
        mWeiboShareAPI.registerApp();	// ��Ӧ��ע�ᵽ΢���ͻ���

        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }

        shareToWeibo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMultiMessage(true, false, true, false, false, false);
            }
        });

        //  ��cache�л���server�в�ѯ�������Ϣ���Ȳ�cache�����cacheû�鵽���ٲ������
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // �ӵ�ǰӦ�û���΢�������з���󣬷��ص���ǰӦ��ʱ����Ҫ�ڴ˴����øú���
        // ������΢���ͻ��˷��ص����ݣ�ִ�гɹ������� true��������
        // {@link IWeiboHandler.Response#onResponse}��ʧ�ܷ��� false�������������ص�
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(this, "share Success", Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, "share Canceled", Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this,
                        "share Failed! " + "Error Message: " + baseResp.errMsg,
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getSharedText();
        return textObject;
    }

    private String getSharedText() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String city_name = sharedPref.getString("city_name", "");
        String weather_info = sharedPref.getString("weather_desp", "");
        String str_temp1 = sharedPref.getString("temp1", "");
        String str_temp2 = sharedPref.getString("temp2", "");
        String current_date = sharedPref.getString("current_date", "");

        String shared_text = current_date + " " + city_name + " " + weather_info + " " + str_temp2 + " ~ " + str_temp1;

        return shared_text;
    }

    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = com.sina.weibo.sdk.utils.Utility.generateGUID();
        mediaObject.title = "���� HappyDay";
        mediaObject.description = "ÿ�춼�к�����^_^";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String weatherCode =  sharedPref.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";

        mediaObject.actionUrl = address; // ��ȡ�������ص�����
        mediaObject.defaultText = "Webpage Ĭ���İ�";
        return mediaObject;
    }

    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
                                  boolean hasMusic, boolean hasVideo, boolean hasVoice) {

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//��ʼ��΢���ķ�����Ϣ
        if (hasText) {
            weiboMessage. textObject = getTextObj();
        }

        // �û����Է�������ý����Դ����ҳ�����֡���Ƶ�������е�һ�֣�
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        AuthInfo authInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }

        mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {
            @Override
            public void onWeiboException( WeiboException arg0 ) {
            }

            @Override
            public void onComplete( Bundle bundle ) {
                // TODO Auto-generated method stub
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
                Toast.makeText(getApplicationContext(), "onAuthorizeComplete token = " + newToken.getToken(), 0).show();
            }

            @Override
            public void onCancel() {
            }
        });

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
                        // ��̨����һֱ�ڻ�ȡ��������Ϣ
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // ��ʾ������Ϣ
                                showWeather();

                            }
                        });
                    }
                }

            }

            @Override
            public void onError(Exception e) {
                // ��ʾͬ��ʧ��������
                publishTime.setText("load failure..");


            }
        });

    }

    private void showWeather(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        cityName.setText(sharedPref.getString("city_name", "��������"));
        publishTime.setText(sharedPref.getString("current_date", "")
                + "  " + sharedPref.getString("publish_time", ""));
//        currentDate.setText(sharedPref.getString("current_date", ""));
        weatherDesp.setText(sharedPref.getString("weather_desp", ""));
        temp1.setText(sharedPref.getString("temp1", ""));
        temp2.setText(sharedPref.getString("temp2", ""));

        // startService(new Intent(WeatherActivity.this, AutoUpdateService.class));


    }

}
