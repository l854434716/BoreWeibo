package com.ultrapower.boreweibo.activity;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;
import com.ultrapower.boreweibo.BaseActivity;
import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.adapter.StatusAdapter;
import com.ultrapower.boreweibo.api.MainLooperWarpperRequestListener;
import com.ultrapower.boreweibo.api.SimpleRequestListener;
import com.ultrapower.boreweibo.utils.ImageOptHelper;
import com.ultrapower.boreweibo.utils.TitleBuilder;
import com.ultrapower.boreweibo.widget.Pull2RefreshListView;
import com.ultrapower.boreweibo.widget.UnderlineIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {


    // 标题栏
    private View title;
    private ImageView titlebar_iv_left;
    private TextView titlebar_tv;
    // headerView - 用户信息
    private View user_info_head;
    private ImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_follows;
    private TextView tv_fans;
    private TextView tv_sign;
    // shadow_tab - 顶部悬浮的菜单栏
    private View shadow_user_info_tab;
    private RadioGroup shadow_rg_user_info;
    private UnderlineIndicatorView shadow_uliv_user_info;
    private View user_info_tab;
    private RadioGroup rg_user_info;
    private UnderlineIndicatorView uliv_user_info;
    // headerView - 添加至列表中作为header的菜单栏
    private ImageView iv_user_info_head;
    private Pull2RefreshListView plv_user_info;
    private View footView;
    // 用户相关信息
    private boolean isCurrentUser;
    private User user;
    private String userName;
    // 个人微博列表
    private List<Status> statuses = new ArrayList<Status>();
    private StatusAdapter statusAdapter;
    private long curPage = 1;
    // 背景图片最小高度
    private int minImageHeight = -1;
    // 背景图片最大高度
    private int maxImageHeight = -1;

    private int curScrollY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userName= getIntent().getStringExtra("userName");

        if(TextUtils.isEmpty(userName)){

            isCurrentUser=true;

            user=application.currentUser;
        }

        initView();

        loadData();
    }



    private void initView() {

        title = new TitleBuilder(this)
                .setTitleBgRes(R.drawable.userinfo_navigationbar_background)
                .setLeftImage(R.drawable.navigationbar_back_sel)
                .setLeftOnClickListener(this)
                .build();
        // 获取标题栏信息,需要时进行修改
        titlebar_iv_left = (ImageView) title.findViewById(R.id.titlebar_iv_left);
        titlebar_tv = (TextView) title.findViewById(R.id.titlebar_tv);

        initInfoHead();
        initTab();
        initListView();

    }



    private void initInfoHead() {

        iv_user_info_head = (ImageView) findViewById(R.id.iv_user_info_head);

        user_info_head = View.inflate(this, R.layout.user_info_head, null);
        iv_avatar = (ImageView) user_info_head.findViewById(R.id.iv_avatar);
        tv_name = (TextView) user_info_head.findViewById(R.id.tv_name);

        tv_follows = (TextView) user_info_head.findViewById(R.id.tv_follows);
        tv_fans = (TextView) user_info_head.findViewById(R.id.tv_fans);
        tv_sign = (TextView) user_info_head.findViewById(R.id.tv_sign);

    }



    // 初始化菜单栏
    private void initTab() {
        // 悬浮显示的菜单栏
        shadow_user_info_tab = findViewById(R.id.user_info_tab);
        shadow_rg_user_info = (RadioGroup) findViewById(R.id.rg_user_info);
        shadow_uliv_user_info = (UnderlineIndicatorView) findViewById(R.id.uliv_user_info);

        shadow_rg_user_info.setOnCheckedChangeListener(this);
        shadow_uliv_user_info.setCurrentItemWithoutAnim(1);

        // 添加到列表中的菜单栏
        user_info_tab = View.inflate(this, R.layout.user_info_tab, null);
        rg_user_info = (RadioGroup) user_info_tab.findViewById(R.id.rg_user_info);
        uliv_user_info = (UnderlineIndicatorView) user_info_tab.findViewById(R.id.uliv_user_info);

        rg_user_info.setOnCheckedChangeListener(this);
        uliv_user_info.setCurrentItemWithoutAnim(1);
    }

    @SuppressLint("NewApi")
    private void initListView() {

        plv_user_info = (Pull2RefreshListView) findViewById(R.id.plv_user_info);

        initLoadingLayout();

        footView = View.inflate(this, R.layout.footview_loading, null);

        final ListView lv = plv_user_info.getRefreshableView();

        statusAdapter = new StatusAdapter(this, statuses);

        plv_user_info.setAdapter(statusAdapter);

        lv.addHeaderView(user_info_head);
        lv.addHeaderView(user_info_tab);

        plv_user_info.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadStatuses(1);
            }
        });

        plv_user_info.setOnLastItemVisibleListener(
                new PullToRefreshBase.OnLastItemVisibleListener() {

                    @Override
                    public void onLastItemVisible() {
                        loadStatuses((int) (curPage + 1));
                    }
                });

        //只有初始位置下拉刷新的时候调用
        plv_user_info.setOnPlvScrollListener(new Pull2RefreshListView.OnPlvScrollListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {


                int scrollY = curScrollY = t;

                if(minImageHeight == -1) {
                    minImageHeight = iv_user_info_head.getHeight();
                }

                if(maxImageHeight == -1) {
                    Rect rect = iv_user_info_head.getDrawable().getBounds();
                    maxImageHeight = rect.bottom - rect.top;
                }

                if(minImageHeight - scrollY < maxImageHeight) {
                    iv_user_info_head.layout(0, 0, iv_user_info_head.getWidth(),
                            minImageHeight - scrollY);
                } else {
                    iv_user_info_head.layout(0,
                            -scrollY - (maxImageHeight - minImageHeight),
                            iv_user_info_head.getWidth(),
                            -scrollY - (maxImageHeight - minImageHeight) + iv_user_info_head.getHeight());
                }
            }
        });

        //
        iv_user_info_head.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(curScrollY == bottom - oldBottom) {

                        iv_user_info_head.layout(0, 0,
                                iv_user_info_head.getWidth(),
                                oldBottom);

                }
            }
        });

        // 一直调用
        plv_user_info.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                iv_user_info_head.layout(0,
                        user_info_head.getTop(),
                        iv_user_info_head.getWidth(),
                        user_info_head.getTop() + iv_user_info_head.getHeight());


                if (user_info_head.getBottom() < title.getBottom()) {
                    shadow_user_info_tab.setVisibility(View.VISIBLE);
                    title.setBackgroundResource(R.drawable.navigationbar_background);
                    titlebar_iv_left.setImageResource(R.drawable.navigationbar_back_sel);
                    titlebar_tv.setVisibility(View.VISIBLE);
                } else {
                    shadow_user_info_tab.setVisibility(View.GONE);
                    title.setBackgroundResource(R.drawable.userinfo_navigationbar_background);
                    titlebar_iv_left.setImageResource(R.drawable.userinfo_navigationbar_back_sel);
                    titlebar_tv.setVisibility(View.GONE);
                }
            }

        });

    }

    /*设置下拉刷新布局*/
    private void initLoadingLayout() {

        ILoadingLayout loadingLayout = plv_user_info.getLoadingLayoutProxy();
        loadingLayout.setPullLabel("");
        loadingLayout.setRefreshingLabel("");
        loadingLayout.setReleaseLabel("");
        loadingLayout.setLoadingDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));


    }


    private void loadData() {

        if(isCurrentUser) {
            // 如果是当前授权用户,直接设置信息
            setUserInfo();
        } else {
            // 如果是查看他人,调用获取用户信息接口
            loadUserInfo();
        }

        // 加载用户所属微博列表
        loadStatuses(1);

    }



    private void setUserInfo() {
        if(user == null) {
            return;
        }
        tv_name.setText(user.name);
        titlebar_tv.setText(user.name);
        imageLoader.displayImage(user.avatar_large, new ImageViewAware(iv_avatar),
                ImageOptHelper.getAvatarOptions());
        tv_follows.setText("关注 " + user.friends_count);
        tv_fans.setText("粉丝 " + user.followers_count);
        tv_sign.setText("简介:" + user.description);
    }


    private void loadUserInfo() {

        usersAPI.show(userName,new MainLooperWarpperRequestListener(new SimpleRequestListener(this){

            @Override
            public void onComplete(String response) {
                super.onComplete(response);

                // 获取用户信息并设置
                user = User.parse(response);
                setUserInfo();
            }
        }));
    }

    private void loadStatuses(final int requestPage) {


        statusesAPI.friendsTimeline(requestPage,new MainLooperWarpperRequestListener(new SimpleRequestListener(this){

            @Override
            public void onComplete(String response) {
                super.onComplete(response);

                showLog("status comments = " + response);

                if(requestPage == 1) {
                    statuses.clear();
                }

                addStatus(StatusList.parse(response));
            }

            @Override
            public void onAllDone() {
                super.onAllDone();

                plv_user_info.onRefreshComplete();
            }
        }));

    }

    private void addStatus(StatusList response) {

        for(Status status : response.statusList) {
            if(!statuses.contains(status)) {
                statuses.add(status);
            }
        }
        statusAdapter.notifyDataSetChanged();

        if(curPage < response.total_number) {
            addFootView(plv_user_info, footView);
        } else {
            removeFootView(plv_user_info, footView);
        }
    }

    private void removeFootView(Pull2RefreshListView plv_user_info, View footView) {

        ListView lv = plv_user_info.getRefreshableView();
        if(lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
    }

    private void addFootView(Pull2RefreshListView plv_user_info, View footView) {

        ListView lv = plv_user_info.getRefreshableView();
        if(lv.getFooterViewsCount() == 1) {
            lv.addFooterView(footView);
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        // 同步悬浮和列表中的标题栏状态
        syncRadioButton(group, checkedId);
    }

    private void syncRadioButton(RadioGroup group, int checkedId) {
        int index = group.indexOfChild(group.findViewById(checkedId));

        if(shadow_user_info_tab.getVisibility() == View.VISIBLE) {
            shadow_uliv_user_info.setmCurrentItem(index);

            ((RadioButton)rg_user_info.findViewById(checkedId)).setChecked(true);
            uliv_user_info.setCurrentItemWithoutAnim(index);
        } else {
            uliv_user_info.setmCurrentItem(index);

            ((RadioButton)shadow_rg_user_info.findViewById(checkedId)).setChecked(true);
            shadow_uliv_user_info.setCurrentItemWithoutAnim(index);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titlebar_iv_left:
                finish();
                break;
            default:
                break;
        }
    }
}
