package com.ultrapower.boreweibo.api;

import android.content.Context;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.ultrapower.boreweibo.utils.ToastUtils;

/**
 * Created by Administrator on 2015/9/21.
 */
public class SimpleRequestListener implements RequestListener{

    private Context context;

    public  SimpleRequestListener(Context context){

        this.context= context;

    }

    @Override
    public void onComplete(String s) {
        onAllDone();

    }

    @Override
    public void onWeiboException(WeiboException e) {
        onAllDone();
        ToastUtils.showToast(context,e.getMessage(), Toast.LENGTH_LONG);

    }

    public  void onAllDone(){

    }


}
