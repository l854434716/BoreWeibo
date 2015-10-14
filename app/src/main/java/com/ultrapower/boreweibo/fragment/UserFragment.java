package com.ultrapower.boreweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.openapi.models.UserItem;
import com.ultrapower.boreweibo.BaseApplication;
import com.ultrapower.boreweibo.BaseFragment;
import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.activity.UserInfoActivity;
import com.ultrapower.boreweibo.adapter.UserItemAdapter;
import com.ultrapower.boreweibo.api.MainLooperWarpperRequestListener;
import com.ultrapower.boreweibo.api.SimpleRequestListener;
import com.ultrapower.boreweibo.contants.AccessTokenKeeper;
import com.ultrapower.boreweibo.contants.WeiboConstants;
import com.ultrapower.boreweibo.utils.ImageOptHelper;
import com.ultrapower.boreweibo.utils.TitleBuilder;
import com.ultrapower.boreweibo.widget.WrapHeightListView;

import java.util.ArrayList;
import java.util.List;


public class UserFragment extends BaseFragment {

    private LinearLayout ll_userinfo;

    private ImageView iv_avatar;
    private TextView tv_subhead;
    private TextView tv_caption;

    private TextView tv_status_count;
    private TextView tv_follow_count;
    private TextView tv_fans_count;

    private WrapHeightListView lv_user_items;

    private User user;
    private View view;

    private UserItemAdapter adapter;
    private List<UserItem> userItems;

    private UsersAPI usersAPI;
    private Oauth2AccessToken mAccessToken;
    private ImageLoader imageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= View.inflate(mainActivity, R.layout.fragment_user,null);
        mAccessToken = AccessTokenKeeper.readAccessToken(mainActivity);
        imageLoader = ImageLoader.getInstance();

        usersAPI= new UsersAPI(mainActivity, WeiboConstants.APP_KEY,mAccessToken);
        initView();

        setItem();


        return  view;

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden){
            usersAPI.show(Long.parseLong(mAccessToken.getUid()),new MainLooperWarpperRequestListener(new SimpleRequestListener(mainActivity){


                @Override
                public void onComplete(String s) {
                    super.onComplete(s);

                    BaseApplication application= (BaseApplication) mainActivity.getApplication();

                    application.currentUser= user=User.parse(s);
                    setUserInfo();

                }
            }));


        }
    }


    private void initView() {

        new TitleBuilder(view)
                .setTitleText("我").build();

        ll_userinfo = (LinearLayout) view.findViewById(R.id.ll_userinfo);

        ll_userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserFragment.this.intent2Activity(UserInfoActivity.class);
            }
        });

        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tv_subhead = (TextView) view.findViewById(R.id.tv_subhead);
        tv_caption = (TextView) view.findViewById(R.id.tv_caption);
        // 互动信息栏: 微博数、关注数、粉丝数
        tv_status_count = (TextView) view.findViewById(R.id.tv_status_count);
        tv_follow_count = (TextView) view.findViewById(R.id.tv_follow_count);
        tv_fans_count = (TextView) view.findViewById(R.id.tv_fans_count);
        // 设置栏列表
        lv_user_items = (WrapHeightListView) view.findViewById(R.id.lv_user_items);
        userItems = new ArrayList<UserItem>();
        adapter = new UserItemAdapter(mainActivity, userItems);
        lv_user_items.setAdapter(adapter);

    }

    private void setUserInfo() {

        tv_subhead.setText(user.name);
        tv_caption.setText("简介:" + user.description);
        //imageLoader.displayImage(user.getAvatar_large(), iv_avatar);
        imageLoader.displayImage(user.avatar_large, iv_avatar, ImageOptHelper.getAvatarOptions());
        tv_status_count.setText("" + user.statuses_count);
        tv_follow_count.setText("" + user.followers_count);
        tv_fans_count.setText("" + user.friends_count);

    }

    private void setItem() {

        userItems.add(new UserItem(false, R.drawable.push_icon_app_small_1, "新的朋友", ""));
        userItems.add(new UserItem(false, R.drawable.push_icon_app_small_2, "微博等级", "Lv13"));
        userItems.add(new UserItem(false, R.drawable.push_icon_app_small_3, "编辑资料", ""));
        userItems.add(new UserItem(true, R.drawable.push_icon_app_small_4, "我的相册", "(18)"));
        userItems.add(new UserItem(false, R.drawable.push_icon_app_small_5, "我的点评", ""));
        userItems.add(new UserItem(false, R.drawable.push_icon_app_small_4, "我的赞", "(32)"));
        userItems.add(new UserItem(true, R.drawable.push_icon_app_small_3, "微博支付", ""));
        userItems.add(new UserItem(false, R.drawable.push_icon_app_small_2, "微博运动", "步数、卡路里、跑步轨迹"));
        userItems.add(new UserItem(true, R.drawable.push_icon_app_small_1, "更多", "收藏、名片"));
        adapter.notifyDataSetChanged();

    }



}
