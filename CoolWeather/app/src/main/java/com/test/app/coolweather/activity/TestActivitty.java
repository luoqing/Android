package com.test.app.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.test.app.coolweather.R;
import com.test.app.coolweather.model.AccessTokenKeeper;
import com.test.app.coolweather.model.Constants;

/**
 * Created by luoqing on 2015/6/26.
 */
public class TestActivitty extends Activity {

    private AuthInfo mAuthInfo;

    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        mAuthInfo = new AuthInfo(this, Constants.APP_KEY,
                Constants.REDIRECT_URL, Constants.SCOPE);

        mSsoHandler = new SsoHandler(TestActivitty.this, mAuthInfo);

        btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorizeWeb(new AuthListener());
            }
        });

    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
     * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
     * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
     * SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        // 授权完成
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {

                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(TestActivitty.this,
                        mAccessToken);
                Toast.makeText(TestActivitty.this, "success!!",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TestActivitty.this, ChooseAreaActivity.class));


//                // 根据uid 获取用户的昵称,因为uid是在回调方法从服务器传递过来的数据中。
//                long uid = Long.parseLong(mAccessToken.getUid());
//                mUsersAPI.show(uid, mListener);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                Toast.makeText(TestActivitty.this, "fail", Toast.LENGTH_LONG)
                        .show();
            }
        }

        // 取消授权
        @Override
        public void onCancel() {
            Toast.makeText(TestActivitty.this, "cancel", Toast.LENGTH_LONG)
                    .show();
        }

        // 授权异常
        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(TestActivitty.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }


}
