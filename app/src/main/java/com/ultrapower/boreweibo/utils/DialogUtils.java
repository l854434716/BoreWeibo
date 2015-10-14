package com.ultrapower.boreweibo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.ultrapower.boreweibo.BaseActivity;

import java.util.List;

/**
 * Created by Administrator on 2015/9/25.
 */
public class DialogUtils {


    public static void showImagePickDialog(final BaseActivity activity) {

        String title= "选择获取图片的方式";

        String[] items= new String[]{"拍照","从手机中选择"};

        showListDialog(activity, title, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                switch (which){

                    case 0:
                        ImageUtils.openCameraImage(activity);
                        break;
                    case 1:
                        ImageUtils.openLocalImage(activity);
                        break;
                }
            }
        });

    }


    /**
     * 列表型dialog
     *
     * @param context
     * @param title
     *            标题名称,内容为空时即不设置标题
     * @param items
     *            所有item选项的名称
     * @param onClickListener
     *            确定按钮监听
     * @return
     */
    public  static AlertDialog  showListDialog(Context context, String title,
                String[] items,  DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder  builder= new AlertDialog.Builder(context);

        if (!TextUtils.isEmpty(title)){

            builder.setTitle(title);
        }

        AlertDialog  dialog= builder.setItems(items,onClickListener).show();

        return  dialog;
    }


    /**
     * 列表型dialog
     *
     * @param context
     * @param title
     *            标题名称,内容为空时即不设置标题
     * @param items
     *            所有item选项的名称
     * @param onClickListener
     *            确定按钮监听
     * @return
     */
    public static AlertDialog showListDialog(Context context, String title,
                                             List<String> items, DialogInterface.OnClickListener onClickListener) {
        return showListDialog(context, title,
                items.toArray(new String[items.size()]), onClickListener);
    }
}
