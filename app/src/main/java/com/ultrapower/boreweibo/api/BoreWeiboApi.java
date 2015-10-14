package com.ultrapower.boreweibo.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.AbsOpenAPI;
import com.ultrapower.boreweibo.contants.AccessTokenKeeper;
import com.ultrapower.boreweibo.contants.WeiboConstants;

/**
 * Created by Administrator on 2015/9/1.
 */
public class BoreWeiboApi extends AbsOpenAPI{

    private Handler mainLooperHandler= new Handler(Looper.getMainLooper());// 主线程执行

    /**
     * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
     *
     * @param context
     * @param appKey
     * @param accessToken
     */
    public BoreWeiboApi(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
    }

        /**
         * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
         *
         * @param context
         * @param accessToken
         */
     public BoreWeiboApi(Context context, Oauth2AccessToken accessToken) {
            super(context, WeiboConstants.APP_KEY, accessToken);
     }

    /**
     * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
     *
     * @param context

     */
    public BoreWeiboApi(Context context) {
        super(context, WeiboConstants.APP_KEY, AccessTokenKeeper.readAccessToken(context));
    }


    public void requestInMainLooper(String url, WeiboParameters params,String httpMethod, final RequestListener listener) {


          requestAsync(url, params, httpMethod, new RequestListener() {
              @Override
              public void onComplete(final String response) {
                  mainLooperHandler.post(new Runnable() {
                      @Override
                      public void run() {

                          listener.onComplete(response);

                      }
                  });
              }

              @Override
              public void onWeiboException(final WeiboException e) {

                  mainLooperHandler.post(new Runnable() {
                      @Override
                      public void run() {
                          listener.onWeiboException(e);
                      }
                  });

              }
          });


    }



}
