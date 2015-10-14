package com.ultrapower.boreweibo.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ultrapower.boreweibo.BaseActivity;
import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.utils.DisplayUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/13.
 */
public class ImageBrowserAdapter extends PagerAdapter {

    private BaseActivity context;

    private List<PicUrls> picUrls= new ArrayList<>();

    private ImageLoader imageLoader;

    private ArrayList<View> picViews;


    public  ImageBrowserAdapter(BaseActivity context , List<String> imgUrs){

        this.context=context;


        for (String _imgUrl: imgUrs){
            PicUrls picUrls=new PicUrls();
            picUrls.setThumbnail_pic(_imgUrl);
            this.picUrls.add(picUrls);
        }
        imageLoader= ImageLoader.getInstance();

        initImgs();

    }

    private void initImgs() {
        picViews = new ArrayList<View>();

        for(int i=0; i<picUrls.size(); i++) {
            // 填充显示图片的页面布局
            View view = View.inflate(context, R.layout.item_image_browser, null);
            picViews.add(view);
        }
    }

    @Override
    public int getCount() {
        if(picUrls.size() > 1) {
            return Integer.MAX_VALUE;
        }
        return picUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public View  instantiateItem(ViewGroup container, int position) {
        int index = position % picUrls.size();
        View view = picViews.get(index);
        final ImageView iv_image_browser = (ImageView) view.findViewById(R.id.iv_image_browser);
        PicUrls picUrl = picUrls.get(index);

        String url = picUrl.isShowOriImag() ? picUrl.getOriginal_pic() : picUrl.getBmiddle_pic();

        if (url==null){

            url= picUrl.getThumbnail_pic();
        }


        imageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                if (loadedImage!=null&&loadedImage.getWidth()!=0){

                    float scale= loadedImage.getHeight()/loadedImage.getWidth();

                    int screenWidthPixels = DisplayUtils.getScreenWidthPixels(context);
                    int screenHeightPixels = DisplayUtils.getScreenHeightPixels(context);
                    int height = (int) (screenWidthPixels * scale);

                    if (height<screenHeightPixels){

                        height= screenHeightPixels;
                    }

                    ViewGroup.LayoutParams params = iv_image_browser.getLayoutParams();
                    params.height = height;
                    params.width = screenWidthPixels;

                    iv_image_browser.setImageBitmap(loadedImage);

                }


            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        container.addView(view);
        return  view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public PicUrls getPic(int position) {
        return picUrls.get(position%picUrls.size());
    }

    public Bitmap getBitmap(int position) {
        Bitmap bitmap = null;
        View view = picViews.get(position % picViews.size());
        ImageView iv_image_browser = (ImageView) view.findViewById(R.id.iv_image_browser);
        Drawable drawable = iv_image_browser.getDrawable();
        if(drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            bitmap = bd.getBitmap();
        }

        return bitmap;
    }


    static  public  class PicUrls implements Serializable{


        // 中等质量图片url前缀
        private static final String BMIDDLE_URL = "http://ww3.sinaimg.cn/bmiddle/";
        // 原质量图片url前缀
        private static final String ORIGINAL_URL = "http://ww3.sinaimg.cn/large/";

        private String thumbnail_pic;
        private String bmiddle_pic;
        private String original_pic;

        private boolean showOriImag;

        /**
         * 从缩略图url中截取末尾的图片id,用于和拼接成其他质量图片url
         */
        public String getImageId() {
            int indexOf = thumbnail_pic.lastIndexOf("/") + 1;
            return thumbnail_pic.substring(indexOf);
        }

        public String getThumbnail_pic() {
            return thumbnail_pic;
        }

        public void setThumbnail_pic(String thumbnail_pic) {
            this.thumbnail_pic = thumbnail_pic;
        }

        public String getBmiddle_pic() {
            return TextUtils.isEmpty(bmiddle_pic) ? BMIDDLE_URL + getImageId() : bmiddle_pic;
        }

        public void setBmiddle_pic(String bmiddle_pic) {
            this.bmiddle_pic = bmiddle_pic;
        }

        public String getOriginal_pic() {
            return TextUtils.isEmpty(original_pic) ? ORIGINAL_URL + getImageId() : original_pic;
        }

        public void setOriginal_pic(String original_pic) {
            this.original_pic = original_pic;
        }

        public boolean isShowOriImag() {
            return showOriImag;
        }

        public void setShowOriImag(boolean showOriImag) {
            this.showOriImag = showOriImag;
        }
    }
}
