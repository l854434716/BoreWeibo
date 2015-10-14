package com.ultrapower.boreweibo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.Status;
import com.ultrapower.boreweibo.BaseActivity;
import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.adapter.EmotionGvAdapter;
import com.ultrapower.boreweibo.adapter.EmotionPagerAdapter;
import com.ultrapower.boreweibo.adapter.WriteStatusGridImgsAdapter;
import com.ultrapower.boreweibo.api.MainLooperWarpperRequestListener;
import com.ultrapower.boreweibo.api.SimpleRequestListener;
import com.ultrapower.boreweibo.utils.DialogUtils;
import com.ultrapower.boreweibo.utils.DisplayUtils;
import com.ultrapower.boreweibo.utils.EmotionUtils;
import com.ultrapower.boreweibo.utils.ImageUtils;
import com.ultrapower.boreweibo.utils.StringUtils;
import com.ultrapower.boreweibo.utils.TitleBuilder;
import com.ultrapower.boreweibo.widget.WrapHeightGridView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WriteStatusActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener {

    // 输入框
    private EditText et_write_status;
    // 添加的九宫格图片
    private WrapHeightGridView gv_write_status;
    // 转发微博内容
    private View include_retweeted_status_card;
    private ImageView iv_rstatus_img;;
    private TextView tv_rstatus_username;;
    private TextView tv_rstatus_content;;
    // 底部添加栏
    private ImageView iv_image;
    private ImageView iv_at;
    private ImageView iv_topic;
    private ImageView iv_emoji;
    private ImageView iv_add;
    // 表情选择面板
    private LinearLayout ll_emotion_dashboard;
    private ViewPager vp_emotion_dashboard;
    // 进度框
    private ProgressDialog progressDialog;

    private WriteStatusGridImgsAdapter statusImgsAdapter;
    private ArrayList<Uri> imgUris = new ArrayList<Uri>();
    private EmotionPagerAdapter emotionPagerGvAdapter;

    // 引用的微博
    private Status retweeted_status;
    // 显示在页面中,实际需要转发内容的微博
    private Status cardStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_status);

        retweeted_status= (Status) getIntent().getSerializableExtra("status");

        initView();
        initEmotion();
    }

    private void initView() {
        new TitleBuilder(this)
                .setTitleText("发微博")
                .setLeftText("取消")
                .setLeftOnClickListener(this)
                .setRightText("发送")
                .setRightOnClickListener(this)
                .build();
        // 输入框
        et_write_status = (EditText) findViewById(R.id.et_write_status);
        //添加的九宫格图片
        gv_write_status= (WrapHeightGridView) findViewById(R.id.gv_write_status);
        //转发微博内容
        include_retweeted_status_card= findViewById(R.id.include_retweeted_status_card);
        iv_rstatus_img = (ImageView) findViewById(R.id.iv_rstatus_img);
        tv_rstatus_username = (TextView) findViewById(R.id.tv_rstatus_username);
        tv_rstatus_content = (TextView) findViewById(R.id.tv_rstatus_content);

        // 底部添加栏
        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_at = (ImageView) findViewById(R.id.iv_at);
        iv_topic = (ImageView) findViewById(R.id.iv_topic);
        iv_emoji = (ImageView) findViewById(R.id.iv_emoji);
        iv_add = (ImageView) findViewById(R.id.iv_add);


        // 表情选择面板
        ll_emotion_dashboard = (LinearLayout) findViewById(R.id.ll_emotion_dashboard);
        vp_emotion_dashboard = (ViewPager) findViewById(R.id.vp_emotion_dashboard);

        //进度条

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("微博发送中...");

        statusImgsAdapter= new WriteStatusGridImgsAdapter(this, imgUris, gv_write_status);
        gv_write_status.setAdapter(statusImgsAdapter);
        gv_write_status.setOnItemClickListener(this);

        iv_image.setOnClickListener(this);
        iv_at.setOnClickListener(this);
        iv_topic.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);
        iv_add.setOnClickListener(this);


        initRetweetedStatus();
        initEmotion();

    }

    private void initEmotion() {
        // 获取屏幕宽度
        int gvWidth= DisplayUtils.getScreenWidthPixels(this);
        // 表情边距
        int space_gv= DisplayUtils.dp2px(this,8);
        // GridView中item的宽度
        int gv_itme_width= (gvWidth- space_gv*8)/7;
        // GridView中item的高度
        int gv_hight= gv_itme_width*3+space_gv*4;

        List<GridView> gvs= new ArrayList<>();

        List<String> emotionNames= new ArrayList<>();

        for (String emojiName: EmotionUtils.emojiMap.keySet()){

            emotionNames.add(emojiName);

            if (emotionNames.size()==20){
                GridView gridView= createEmotionGridView(emotionNames,gvWidth,space_gv,gv_itme_width,gv_hight);

                gvs.add(gridView);

                // 添加完一组表情,重新创建一个表情名字集合
                emotionNames = new ArrayList<String>();
            }
        }

        // 检查最后是否有不足20个表情的剩余情况
        if (emotionNames.size() > 0) {
            GridView gridView= createEmotionGridView(emotionNames,gvWidth,space_gv,gv_itme_width,gv_hight);
            gvs.add(gridView);
        }

        emotionPagerGvAdapter= new EmotionPagerAdapter(gvs);

        vp_emotion_dashboard.setAdapter(emotionPagerGvAdapter);

        LinearLayout.LayoutParams params=  new LinearLayout.LayoutParams(gvWidth,gv_hight);

        vp_emotion_dashboard.setLayoutParams(params);

    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int gv_itme_width, int gv_hight) {
        GridView gridView= new GridView(this);

        gridView.setBackgroundResource(R.color.bg_gray);
        gridView.setSelector(R.color.transparent);
        gridView.setNumColumns(7);
        gridView.setPadding(padding,padding,padding,padding);
        gridView.setHorizontalSpacing(padding);
        gridView.setVerticalSpacing(padding);
        ViewGroup.LayoutParams layoutParams= new ViewGroup.LayoutParams(gvWidth,gv_hight);

        /*AbsListView.LayoutParams layoutParams= new AbsListView.LayoutParams(gvWidth,gv_hight);*/
        gridView.setLayoutParams(layoutParams);

        EmotionGvAdapter  emotionGvAdapter= new EmotionGvAdapter(this,emotionNames,gv_itme_width);

        gridView.setAdapter(emotionGvAdapter);

        gridView.setOnItemClickListener(this);
        return gridView;
    }

    /**
     * 初始化引用微博内容
     */
    private void initRetweetedStatus() {
        // 转发微博特殊处理
        if (retweeted_status!=null){
            // 转发的微博是否包含转发内容
            Status rrstatus=retweeted_status.retweeted_status;
            if(rrstatus!=null){
                String content = "//@" + retweeted_status.user.name
                        + ":" + retweeted_status.text;

                et_write_status.setText(StringUtils.getWeiboContent(this,et_write_status,content));

                // 如果引用的为转发微博,则使用它转发的内容
                cardStatus = rrstatus;

            }else {

                et_write_status.setText("转发微博");
                // 如果引用的为原创微博,则使用它自己的内容
                cardStatus = retweeted_status;
            }

            // 设置转发图片内容
            String imgUrl = cardStatus.thumbnail_pic;
            if(TextUtils.isEmpty(imgUrl)) {
                iv_rstatus_img.setVisibility(View.GONE);
            } else {
                iv_rstatus_img.setVisibility(View.VISIBLE);
                imageLoader.displayImage(cardStatus.thumbnail_pic, iv_rstatus_img);
            }

            // 设置转发文字内容
            tv_rstatus_username.setText("@" + cardStatus.user.name);
            tv_rstatus_content.setText(cardStatus.text);


            // 转发微博时,不能添加图片
            iv_image.setVisibility(View.GONE);
            include_retweeted_status_card.setVisibility(View.VISIBLE);



        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titlebar_tv_left:
                finish();
                break;
            case R.id.titlebar_tv_right:
                sendStatus();
                break;
            case R.id.iv_image:
                DialogUtils.showImagePickDialog(this);
                break;
            case R.id.iv_at:
                break;
            case R.id.iv_topic:
                break;
            case R.id.iv_emoji:
                if(ll_emotion_dashboard.getVisibility() == View.VISIBLE) {
                    // 显示表情面板时点击,将按钮图片设为笑脸按钮,同时隐藏面板
                    iv_emoji.setImageResource(R.drawable.btn_insert_emotion);
                    ll_emotion_dashboard.setVisibility(View.GONE);
                } else {
                    // 未显示表情面板时点击,将按钮图片设为键盘,同时显示面板
                    iv_emoji.setImageResource(R.drawable.btn_insert_keyboard);
                    ll_emotion_dashboard.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_add:
                break;
        }

    }


    /**
     * 发送微博
     */
    private void sendStatus() {

        String comment= et_write_status.getText().toString();

        if(TextUtils.isEmpty(comment)){

            showToast("微博内容不能为空");
            return;
        }

        String imgFilePath = null;
        InputStream inputStream=null;
        if (imgUris.size() > 0) {
            // 微博API中只支持上传一张图片
            Uri uri = imgUris.get(0);
            imgFilePath = ImageUtils.getImageAbsolutePath(this, uri);
            try {
                inputStream= this.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }
        }

        // 转发微博的id
         long   retweetedStatsId = cardStatus == null ? -1 : Long.parseLong(cardStatus.id);
        // 上传微博api接口
        progressDialog.show();

        /*Bitmap bitmap=ImageUtils.safeDecodeBimtapFile(imgFilePath, null);*/


        Bitmap bitmap=ImageUtils.decodeBitmapFromInputStream(inputStream,null);


        statusesAPI.upload(comment, bitmap, new MainLooperWarpperRequestListener(new SimpleRequestListener(this) {

            @Override
            public void onAllDone() {
                super.onAllDone();
                if (progressDialog != null)
                    progressDialog.dismiss();
            }

            @Override
            public void onComplete(String s) {
                super.onComplete(s);

                setResult(RESULT_OK);

                showToast("微博发送成功");

                WriteStatusActivity.this.finish();


            }
        }));


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Object itemAdapter = parent.getAdapter();

        if (itemAdapter instanceof WriteStatusGridImgsAdapter) {
            // 点击的是添加的图片
            if (position == statusImgsAdapter.getCount() - 1) {
                // 如果点击了最后一个加号图标,则显示选择图片对话框
                DialogUtils.showImagePickDialog(this);
            }
        } else if (itemAdapter instanceof EmotionGvAdapter) {
            // 点击的是表情
            EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter) itemAdapter;

            if (position == emotionGvAdapter.getCount() - 1) {
                // 如果点击了最后一个回退按钮,则调用删除键事件
                et_write_status.dispatchKeyEvent(new KeyEvent(
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                // 如果点击了表情,则添加到输入框中
                String emotionName = emotionGvAdapter.getItem(position);

                // 获取当前光标位置,在指定位置上添加表情图片文本
                int curPosition = et_write_status.getSelectionStart();
                StringBuilder sb = new StringBuilder(et_write_status.getText().toString());
                sb.insert(curPosition, emotionName);

                // 特殊文字处理,将表情等转换一下
                et_write_status.setText(StringUtils.getWeiboContent(
                        this, et_write_status, sb.toString()));

                // 将光标设置到新增完表情的右侧
                et_write_status.setSelection(curPosition + emotionName.length());
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case ImageUtils.GET_IMAGE_BY_CAMERA:

                if (resultCode==RESULT_CANCELED){
                    // 如果拍照取消,将之前新增的图片地址删除
                    ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);

                }else{

                    // 拍照后将图片添加到页面上
                    // crop
                    ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);
                }
                break;

            case ImageUtils.CROP_IMAGE:
                if(resultCode != RESULT_CANCELED) {
                    imgUris.add(ImageUtils.cropImageUri);
                    updateImgs();
                }
                break;
            case ImageUtils.GET_IMAGE_FROM_PHONE:
                if(resultCode != RESULT_CANCELED) {
                    // 本地相册选择完后将图片添加到页面上
                    imgUris.add(data.getData());
                    updateImgs();
                }
                break;
            default:
                break;
        }


    }

    private void updateImgs() {
        if (imgUris.size()>0){
        // 如果有图片则显示GridView,同时更新内容
            gv_write_status.setVisibility(View.VISIBLE);

            statusImgsAdapter.notifyDataSetChanged();
        }else{
        // 无图则不显示GridView
            gv_write_status.setVisibility(View.GONE);
        }
    }
}
