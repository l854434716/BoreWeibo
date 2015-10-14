package com.ultrapower.boreweibo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.ultrapower.boreweibo.activity.MainActivity;
import com.ultrapower.boreweibo.contants.AccessTokenKeeper;
import com.ultrapower.boreweibo.contants.WeiboConstants;


public class BaseFragment extends Fragment {


    protected  MainActivity   mainActivity;

    protected StatusesAPI statusesAPI;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity= (MainActivity) getActivity();

        statusesAPI= new StatusesAPI(mainActivity, WeiboConstants.APP_KEY, AccessTokenKeeper.readAccessToken(mainActivity));
    }


    protected  void  intent2Activity(Class<? extends Activity> cls){
        Intent intent= new Intent(mainActivity,cls);

        startActivity(intent);
    }


}
