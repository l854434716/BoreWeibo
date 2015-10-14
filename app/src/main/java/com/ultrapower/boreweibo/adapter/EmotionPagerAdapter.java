package com.ultrapower.boreweibo.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

/**
 * Created by Administrator on 2015/9/25.
 */
public class EmotionPagerAdapter  extends PagerAdapter {

    private  List<GridView> gridViews;

    public EmotionPagerAdapter(List<GridView> gridViews) {

        this.gridViews= gridViews;

    }

    @Override
    public int getCount() {
        if (gridViews!=null)
            return gridViews.size();
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        if (container instanceof ViewPager){

            ((ViewPager) container).removeView(gridViews.get(position));

        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (container instanceof ViewPager){

            ((ViewPager) container).addView(gridViews.get(position));
        }
        return  gridViews.get(position);

    }
}
