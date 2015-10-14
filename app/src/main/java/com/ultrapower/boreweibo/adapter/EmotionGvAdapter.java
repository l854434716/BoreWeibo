package com.ultrapower.boreweibo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.utils.EmotionUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/9/27.
 */
public class EmotionGvAdapter extends BaseAdapter{

    private  Context context;

    private   List<String> emotionNames;

    private  int  itemWidth;


    public EmotionGvAdapter(Context context, List<String> emotionNames, int gv_itme_width) {

        this.context= context;

        this.emotionNames= emotionNames;

        this.itemWidth= gv_itme_width;


    }

    @Override
    public int getCount() {
        if (emotionNames!=null){

            return emotionNames.size()+1;
        }
        return 0;
    }

    @Override
    public String  getItem(int position) {
        return emotionNames.get(position);
    }

    @Override
    public long  getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView  imageView = new ImageView(context);
        int _ivpadding= itemWidth/8;
        imageView.setPadding(_ivpadding,_ivpadding,_ivpadding,_ivpadding);
        AbsListView.LayoutParams layoutParams= new AbsListView.LayoutParams(itemWidth,itemWidth);
        imageView.setLayoutParams(layoutParams);
        if(position == getCount() - 1) {
            imageView.setImageResource(R.drawable.emotion_delete_icon);
        } else {
            String emotionName = emotionNames.get(position);
            imageView.setImageResource(EmotionUtils.getImgByName(emotionName));
        }

        return  imageView;
    }
}
