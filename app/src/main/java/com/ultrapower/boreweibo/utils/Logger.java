package com.ultrapower.boreweibo.utils;

import android.util.Log;

import com.ultrapower.boreweibo.contants.CommonConstants;

/**
 * Created by Administrator on 2015/8/20.
 */
public class Logger {
    public static void show(String tag, String msg) {

        if (!CommonConstants.isShowLog) {
            return;
        }
        show(tag, msg, Log.INFO);

    }


    public  static  void show(String  TAG,String msg, int level){

        if(!CommonConstants.isShowLog)
            return;

        switch (level) {
            case Log.VERBOSE:
                Log.v(TAG, msg);
                break;
            case Log.DEBUG:
                Log.d(TAG, msg);
                break;
            case Log.INFO:
                Log.i(TAG, msg);
                break;
            case Log.WARN:
                Log.w(TAG, msg);
                break;
            case Log.ERROR:
                Log.e(TAG, msg);
                break;
            default:
                Log.i(TAG, msg);
                break;
        }
    }
}
