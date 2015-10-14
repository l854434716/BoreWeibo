package com.ultrapower.boreweibo.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.ultrapower.boreweibo.utils.Logger;

/**
 * Created by Administrator on 2015/9/1.
 */
public class MainLooperWarpperRequestListener implements RequestListener{
    private  RequestListener requestListener=null;



    private  final  String  TAG="MainLooperWarpperRequestListener";
    private  Handler mainLooperHandler= new Handler(Looper.getMainLooper());


    public  MainLooperWarpperRequestListener(RequestListener listener){
        this.requestListener=listener;

    }

    @Override
    public void onComplete(  final  String response) {

        if (requestListener==null){
            Logger.show(TAG,"requestListener should not be null", Log.ERROR);
            return;
        }

        mainLooperHandler.post(new Runnable() {
            @Override
            public void run() {

                requestListener.onComplete(response);

            }
        });

    }

    @Override
    public void onWeiboException(final WeiboException e) {

        if (requestListener==null){
            Logger.show(TAG,"requestListener should not be null", Log.ERROR);
            return;
        }

        mainLooperHandler.post(new Runnable() {
            @Override
            public void run() {

                requestListener.onWeiboException(e);
            }
        });

    }



}
