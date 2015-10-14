package com.ultrapower.boreweibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.ultrapower.boreweibo.R;

/**
 * Created by Administrator on 2015/10/9.
 */
public class UnderlineIndicatorView extends LinearLayout {

    private int mCurrentPosition;

    public UnderlineIndicatorView(Context context) {
        super(context,null);
    }

    public UnderlineIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(HORIZONTAL);

        int count = 4;
        for (int i = 0; i < count; i++) {
            View view = new View(context);
            LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
            params.weight = 1;
            view.setLayoutParams(params);
            view.setBackgroundResource(R.color.transparent);
            addView(view);
        }
    }


    public  void  setCurrentItemWithoutAnim(int  position){

        View oldChlid= getChildAt(mCurrentPosition);

        View newChild= getChildAt(position);

        oldChlid.setBackgroundResource(R.color.transparent);
        newChild.setBackgroundResource(R.color.orange);

        mCurrentPosition=position;

        invalidate();

    }

    public  void  setmCurrentItem(int  position){

       final  View oldChlid= getChildAt(mCurrentPosition);
        final  View newChild= getChildAt(position);

        TranslateAnimation translateAnimation= new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, position - mCurrentPosition,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);

        translateAnimation.setDuration(200);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                oldChlid.setBackgroundResource(R.color.transparent);
                newChild.setBackgroundResource(R.color.orange);
            }
        });


        oldChlid.setAnimation(translateAnimation);

        mCurrentPosition = position;
        invalidate();



    }



}
