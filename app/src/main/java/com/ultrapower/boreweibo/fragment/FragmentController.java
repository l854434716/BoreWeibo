package com.ultrapower.boreweibo.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/22.
 */
public class FragmentController {



    private  int containerId;

    private FragmentManager fm;

    private List<Fragment> fragments;

    private  FragmentController(FragmentActivity fragmentActivity,int containerId){


        this.fm= fragmentActivity.getSupportFragmentManager();

        this.containerId=containerId;


        initFragments();
    }

    /*初始化所有fragment*/
    private void initFragments() {
        fragments=new ArrayList<Fragment>();

        fragments.add(new HomeFragment());
        fragments.add(new MessageFragment());
        fragments.add(new SearchFragment());
        fragments.add(new UserFragment());

        FragmentTransaction fragmentTransaction= fm.beginTransaction();

        for (Fragment fragment: fragments){
            if(fragment!=null)
            fragmentTransaction.add(containerId,fragment);
        }

        fragmentTransaction.commit();
    }


    public  static FragmentController  getInstance(FragmentActivity fragmentActivity,int containerId){

        return  new FragmentController(fragmentActivity,containerId);

    }



    public  void  hideAllFragments(){

        FragmentTransaction fragmentTransaction= fm.beginTransaction();

        for (Fragment fragment: fragments){
            if(fragment!=null)
            fragmentTransaction.hide(fragment);
        }

        fragmentTransaction.commitAllowingStateLoss();
    }


    public   void  showFragments(int  position){

        hideAllFragments();



        if (position<0||fragments.size()<=position)
            return;

        Fragment fragment= fragments.get(position);

        if (fragment==null||fragment.isVisible())
            return;

        FragmentTransaction fragmentTransaction= fm.beginTransaction();
        fragmentTransaction.show(fragment);
        fragmentTransaction.commitAllowingStateLoss();


    }

    
    public Fragment getFragment(int position) {
        return fragments.get(position);
    }

}
