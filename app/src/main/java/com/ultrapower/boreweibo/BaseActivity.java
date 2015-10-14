package com.ultrapower.boreweibo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.ultrapower.boreweibo.contants.AccessTokenKeeper;
import com.ultrapower.boreweibo.contants.CommonConstants;
import com.ultrapower.boreweibo.contants.WeiboConstants;
import com.ultrapower.boreweibo.utils.Logger;
import com.ultrapower.boreweibo.utils.ToastUtils;

/**
 * Created by Administrator on 2015/8/20.
 */
public class BaseActivity  extends Activity {

    protected  String TAG;

    protected  BaseApplication  application;

    protected  SharedPreferences  sp;


    protected ImageLoader imageLoader;


    protected CommentsAPI  commentsAPI;


    protected StatusesAPI  statusesAPI;

    protected UsersAPI usersAPI;

    protected Oauth2AccessToken mAccessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        application= (BaseApplication) getApplication();

        sp=getSharedPreferences(CommonConstants.SP_NAME, MODE_PRIVATE);

        imageLoader = ImageLoader.getInstance();
        mAccessToken=AccessTokenKeeper.readAccessToken(this);

        commentsAPI=new CommentsAPI(this, WeiboConstants.APP_KEY, mAccessToken);

        statusesAPI= new StatusesAPI(this,WeiboConstants.APP_KEY, mAccessToken);

        usersAPI= new UsersAPI(this, WeiboConstants.APP_KEY,mAccessToken);
    }

    protected  void intent2Activity(Class<? extends  Activity> tarActivityClass){

        if(tarActivityClass==null)
            return;
        Intent intent= new Intent(this,tarActivityClass);

        startActivity(intent);
    }

    protected  void  showToast(String msg){
        ToastUtils.showToast(this, msg, Toast.LENGTH_SHORT);
    }

    protected  void  showLog(String msg){

        Logger.show(TAG, msg);
    }
}
