package com.ultrapower.boreweibo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/8/20.
 */
public class ToastUtils {

    private  static Toast toast=null;

    public static void showToast(Context context,CharSequence text, int duration){

        if (context==null)
            return ;

        if (text==null)
            text="null toast information";


        if (toast==null){
            toast= Toast.makeText(context,text,duration);
        }else {

            toast.setText(text);
            toast.setDuration(duration);
        }

        toast.show();
    }
}
