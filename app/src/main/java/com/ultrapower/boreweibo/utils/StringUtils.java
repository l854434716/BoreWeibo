package com.ultrapower.boreweibo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ultrapower.boreweibo.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/9/15.
 */
public class StringUtils {


    public static SpannableString  getWeiboContent(final Context context, final TextView textView, String source){

        String regexAt = "@[\u4e00-\u9fa5\\w]+";
        String regexTopic = "#[\u4e00-\u9fa5\\w]+#";
        String regexEmoji = "\\[[\u4e00-\u9fa5\\w]+\\]";

        String regex="("+regexAt+")|("+regexTopic+")|("+regexEmoji+")";
        if (source==null)
            source="null";
        SpannableString spannableString= new SpannableString(source);

        Pattern pattern= Pattern.compile(regex);

        Matcher matcher= pattern.matcher(spannableString);

        if (matcher.find())
        {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            matcher.reset();
        }


        while (matcher.find()){

            final String atStr= matcher.group(1);
            final String topicStr= matcher.group(2);
            String emojiStr= matcher.group(3);

            //对@文字进行处理
            if (atStr!=null){
                int  _start= matcher.start(1);

                BoreClickableSpan boreCilckableSpan= new BoreClickableSpan(context){

                    @Override
                    public void onClick(View widget) {
                        ToastUtils.showToast(context, "用户: " + atStr, Toast.LENGTH_SHORT);
                    }
                };

                spannableString.setSpan(boreCilckableSpan,_start,_start+atStr.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


            //对主题文字进行处理
            if (topicStr!=null){
                int _start = matcher.start(2);

                BoreClickableSpan clickableSpan = new BoreClickableSpan(context) {

                    @Override
                    public void onClick(View widget) {
                        ToastUtils.showToast(context, "话题: " + topicStr, Toast.LENGTH_SHORT);
                    }
                };

                spannableString.setSpan(clickableSpan,_start,_start+topicStr.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            }


            //对表情文字进行处理
            if (emojiStr!=null){
                int start = matcher.start(3);

                int imgRes = EmotionUtils.getImgByName(emojiStr);
                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),imgRes);

                if (bitmap!=null){
                    int _size = (int) textView.getTextSize();

                    bitmap= Bitmap.createScaledBitmap(bitmap,_size,_size,true);

                    ImageSpan  imageSpan= new ImageSpan(context,bitmap);

                    spannableString.setSpan(imageSpan,start,start+emojiStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                }




            }
        }
        return  spannableString;
    }


    static class BoreClickableSpan extends ClickableSpan{
        private Context  context;


        public BoreClickableSpan(Context context){

            this.context= context;
        }


        @Override
        public void onClick(View widget) {

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.txt_at_blue));
            ds.setUnderlineText(false);
        }
    }
}
