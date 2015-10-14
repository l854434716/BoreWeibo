package com.ultrapower.boreweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ultrapower.boreweibo.BaseFragment;
import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.utils.TitleBuilder;
import com.ultrapower.boreweibo.utils.ToastUtils;


public class MessageFragment extends BaseFragment {


    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= View.inflate(mainActivity, R.layout.fragment_message,null);

        new TitleBuilder(view)
                .setTitleText("Message")
                .setRightImage(R.drawable.ic_launcher)
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mainActivity, "right click~", Toast.LENGTH_SHORT);
                    }
                });

        return  view;

    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }*/



}
