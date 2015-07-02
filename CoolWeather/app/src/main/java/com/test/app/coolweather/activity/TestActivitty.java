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

    /** ��װ�� "access_token"��"expires_in"��"refresh_token"�����ṩ�����ǵĹ����� */
    private Oauth2AccessToken mAccessToken;
    /** ע�⣺SsoHandler ���� SDK ֧�� SSO ʱ��Ч */
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
     * �� SSO ��Ȩ Activity �˳�ʱ���ú��������á�
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // SSO ��Ȩ�ص�
        // ��Ҫ������ SSO ��½�� Activity ������д onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * ΢����֤��Ȩ�ص��ࡣ 1. SSO ��Ȩʱ����Ҫ�� {@link #onActivityResult} �е���
     * {@link SsoHandler#authorizeCallBack} �� �ûص��Żᱻִ�С� 2. �� SSO
     * ��Ȩʱ������Ȩ�����󣬸ûص��ͻᱻִ�С� ����Ȩ�ɹ����뱣��� access_token��expires_in��uid ����Ϣ��
     * SharedPreferences �С�
     */
    class AuthListener implements WeiboAuthListener {
        // ��Ȩ���
        @Override
        public void onComplete(Bundle values) {
            // �� Bundle �н��� Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {

                // ���� Token �� SharedPreferences
                AccessTokenKeeper.writeAccessToken(TestActivitty.this,
                        mAccessToken);
                Toast.makeText(TestActivitty.this, "success!!",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TestActivitty.this, ChooseAreaActivity.class));


//                // ����uid ��ȡ�û����ǳ�,��Ϊuid���ڻص������ӷ��������ݹ����������С�
//                long uid = Long.parseLong(mAccessToken.getUid());
//                mUsersAPI.show(uid, mListener);
            } else {
                // ���¼�������������յ� Code��
                // 1. ����δ��ƽ̨��ע���Ӧ�ó���İ�����ǩ��ʱ��
                // 2. ����ע���Ӧ�ó��������ǩ������ȷʱ��
                // 3. ������ƽ̨��ע��İ�����ǩ��������ǰ���Ե�Ӧ�õİ�����ǩ����ƥ��ʱ��
                Toast.makeText(TestActivitty.this, "fail", Toast.LENGTH_LONG)
                        .show();
            }
        }

        // ȡ����Ȩ
        @Override
        public void onCancel() {
            Toast.makeText(TestActivitty.this, "cancel", Toast.LENGTH_LONG)
                    .show();
        }

        // ��Ȩ�쳣
        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(TestActivitty.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }


}
