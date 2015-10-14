package com.ultrapower.boreweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.ultrapower.boreweibo.BaseFragment;
import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.adapter.StatusAdapter;
import com.ultrapower.boreweibo.api.MainLooperWarpperRequestListener;
import com.ultrapower.boreweibo.utils.TitleBuilder;
import com.ultrapower.boreweibo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {


    private  final  static int WEIBONUM=20;
    private  View  view;

    private PullToRefreshListView lv_home;

    private View footView;

    private StatusAdapter adapter;
    private List<Status> statuses = new ArrayList<Status>();

    private  int curPage=1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        initView();
        loadData(1);
        return  view;

    }


    private  void initView(){

        view= View.inflate(mainActivity,R.layout.fragment_home,null);

        new TitleBuilder(view)
                .setTitleText("首页")
                .setLeftText("LEFT")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mainActivity, "left onclick", Toast.LENGTH_SHORT);
                    }
                });

        lv_home = (PullToRefreshListView) view.findViewById(R.id.lv_home);
        adapter = new StatusAdapter(mainActivity,statuses);
        lv_home.setAdapter(adapter);

        lv_home.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(1);
            }
        });

        lv_home.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                loadData(curPage + 1);
            }
        });

        footView = View.inflate(mainActivity, R.layout.footview_loading, null);
    }



    private void loadData(final int page) {


        statusesAPI.friendsTimeline(0,0,WEIBONUM,page,false,0,false,new MainLooperWarpperRequestListener(new RequestListener() {
            @Override
            public void onComplete(String s) {
                if(page==1){
                    statuses.clear();
                }

                curPage=page;


                StatusList statusList= StatusList.parse(s);

                addData(statusList);
                onAllDone();

             //   StatusTimeLineResponse timeLineResponse = new Gson().fromJson(response, StatusTimeLineResponse.class);
               /* StatusAdapter lvAdapter=new StatusAdapter(mainActivity, statusList.statusList);
                lv_home.setAdapter(lvAdapter);
                lvAdapter.notifyDataSetChanged();*/

            }

            @Override
            public void onWeiboException(WeiboException e) {
                onAllDone();

                ToastUtils.showToast(getActivity(),e.toString(),Toast.LENGTH_SHORT);

            }


            public void onAllDone() {

                lv_home.onRefreshComplete();
            }

        }));

    }


    private void addData(StatusList resBean) {
        if (resBean==null)
            return;
        if (resBean.statusList==null)
            return;
        for(Status status : resBean.statusList) {
            if(!statuses.contains(status)) {
                statuses.add(status);
            }
        }
        adapter.notifyDataSetChanged();

        if(curPage < resBean.total_number) {
            addFootView(lv_home, footView);
        } else {
            removeFootView(lv_home, footView);
        }
    }

    private void addFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if(lv.getFooterViewsCount() == 1) {
            lv.addFooterView(footView);
        }
    }

    private void removeFootView(PullToRefreshListView plv, View footView) {
        ListView lv = plv.getRefreshableView();
        if(lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
    }
}
