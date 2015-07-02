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
            // ���ע
            mAttentionView = (AttentionComponentView) findViewById(R.id.attentionView);
            // ��������Ҫ��ע��uid
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
            // ���ڴ˴���ʾĳĳ�Ѿ���¼�� ��ȡ�û�������Ϣ
            mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);
            long uid = Long.parseLong(mAccessToken.getUid());
            mUsersAPI.show(uid, mListener);

        }
        else
        {
            // ʹ��΢����Ȩ������ص�¼
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
     * ΢�� OpenAPI �ص��ӿڡ�
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
//                LogUtil.i(TAG, response);
                // ���� User#parse ��JSON��������User����
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
        // ��Ȩ���
        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(LoginActivity.this,
                        mAccessToken);
                Toast.makeText(LoginActivity.this, "success!!",
                        Toast.LENGTH_SHORT).show();
                // ����ɹ�����ת������ҳ��
                startActivity(new Intent(LoginActivity.this, ChooseAreaActivity.class));


            } else {
                // ���¼�������������յ� Code��
                // 1. ����δ��ƽ̨��ע���Ӧ�ó���İ�����ǩ��ʱ��
                // 2. ����ע���Ӧ�ó��������ǩ������ȷʱ��
                // 3. ������ƽ̨��ע��İ�����ǩ��������ǰ���Ե�Ӧ�õİ�����ǩ����ƥ��ʱ��
                Toast.makeText(LoginActivity.this, "fail", Toast.LENGTH_LONG)
                        .show();
            }
        }

        // ȡ����Ȩ
        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "cancel", Toast.LENGTH_LONG)
                    .show();
        }

        // ��Ȩ�쳣
        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }


}
