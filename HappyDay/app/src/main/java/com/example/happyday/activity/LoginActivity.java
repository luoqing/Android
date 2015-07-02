package com.example.happyday.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.happyday.R;
import com.example.happyday.model.AccessTokenKeeper;
import com.example.happyday.model.Constants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.component.view.AttentionComponentView;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.widget.LoginButton;

/**
 * Created by luoqing on 2015/7/1.
 */
public class LoginActivity extends Activity {

    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;

    private LoginButton mLoginBtnDefault;

    private AttentionComponentView mAttentionView;

    private UsersAPI mUsersAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid())
        {
            // 求关注
            mAttentionView = (AttentionComponentView) findViewById(R.id.attentionView);
            // 设置你需要关注的uid
            String attentionUid ="1678426514";
            mAttentionView.setAttentionParam(AttentionComponentView.RequestParam.createRequestParam(
                    Constants.APP_KEY, mAccessToken.getToken(), attentionUid, "", new WeiboAuthListener() {
                        @Override
                        public void onWeiboException(WeiboException arg0) {
                        }

                        @Override
                        public void onComplete(Bundle arg0) {
                            Toast.makeText(LoginActivity.this, "auth acess_token:" + Oauth2AccessToken.parseAccessToken(arg0).getToken(), 0).show();
                        }

                        @Override
                        public void onCancel() {
                        }
                    }));

            startActivity(new Intent(LoginActivity.this, ChooseAreaActivity.class));
            // 并在此处显示某某已经登录， 获取用户名的信息
            mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);
            long uid = Long.parseLong(mAccessToken.getUid());
            mUsersAPI.show(uid, mListener);

        }
        else
        {
            // 使用微博授权进行相关登录
            mAuthInfo = new AuthInfo(this, Constants.APP_KEY,
                    Constants.REDIRECT_URL, Constants.SCOPE);
            mSsoHandler = new SsoHandler(LoginActivity.this, mAuthInfo);

            mLoginBtnDefault = (LoginButton) findViewById(R.id.login_button_default);

            mLoginBtnDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSsoHandler.authorizeWeb(new AuthListener());
                }
            });
            Toast.makeText(LoginActivity.this, "Please Login First", Toast.LENGTH_LONG)
                    .show();
        }


    }
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
//                LogUtil.i(TAG, response);
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {
                    Toast.makeText(LoginActivity.this,
                            "Login User: " + user.screen_name,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
//            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(LoginActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }

    };

    class AuthListener implements WeiboAuthListener {
        // 授权完成
        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(LoginActivity.this,
                        mAccessToken);
                Toast.makeText(LoginActivity.this, "success!!",
                        Toast.LENGTH_SHORT).show();
                // 如果成功，跳转到天气页面
                startActivity(new Intent(LoginActivity.this, ChooseAreaActivity.class));


            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                Toast.makeText(LoginActivity.this, "fail", Toast.LENGTH_LONG)
                        .show();
            }
        }

        // 取消授权
        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "cancel", Toast.LENGTH_LONG)
                    .show();
        }

        // 授权异常
        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }


}
