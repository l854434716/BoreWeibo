package com.ultrapower.boreweibo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.ultrapower.boreweibo.BaseActivity;
import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.contants.AccessTokenKeeper;

public class SplashActivity extends BaseActivity {

    private  static final int WHAT_INTENT2LOGIN=1;

    private  static  final int WHAT_INTENT2MAIN=2;

    private  static  final long SPLASH_DUR_TIME = 1000;

    private Oauth2AccessToken  token;

    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what){

                case WHAT_INTENT2LOGIN:
                    intent2Activity(LoginActivity.class);
                    // close current activity
                    finish();
                    break;
                case WHAT_INTENT2MAIN:
                    intent2Activity(MainActivity.class);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        token= AccessTokenKeeper.readAccessToken(this);

        if (token==null)
            return;

        if (token.isSessionValid()){
            handler.sendEmptyMessageDelayed(WHAT_INTENT2MAIN,SPLASH_DUR_TIME);
        }else{
            handler.sendEmptyMessageDelayed(WHAT_INTENT2LOGIN,SPLASH_DUR_TIME);
        }
    }


}
