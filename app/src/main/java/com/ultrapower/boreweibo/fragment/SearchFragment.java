package com.ultrapower.boreweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ultrapower.boreweibo.BaseFragment;
import com.ultrapower.boreweibo.R;

public class SearchFragment extends BaseFragment {

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= View.inflate(mainActivity, R.layout.fragment_search,null);

        return  view;

    }


}
