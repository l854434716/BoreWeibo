package com.ultrapower.boreweibo.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.fragment.FragmentController;
import com.ultrapower.boreweibo.utils.ToastUtils;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener{

    private  RadioGroup rg_tab;

    private ImageView  iv_add;

    private FragmentController  fragmentController;


    private static  final int  WRITESTATUSREQUESTCODE=110;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fragmentController= FragmentController.getInstance(this,R.id.fl_content);

        setContentView(R.layout.activity_main);
        fragmentController.showFragments(0);

        initView();


    }

    private void initView() {

        rg_tab= (RadioGroup) findViewById(R.id.rg_tab);

        iv_add= (ImageView) findViewById(R.id.iv_add);

        rg_tab.setOnCheckedChangeListener(this);

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,WriteStatusActivity.class);

                startActivityForResult(intent,WRITESTATUSREQUESTCODE);

            }
        });

    }



    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {


        switch (checkedId){

            case R.id.rb_home:
                fragmentController.showFragments(0);

                break;
            case R.id.rb_meassage:
                fragmentController.showFragments(1);

                break;
            case R.id.rb_search:
                fragmentController.showFragments(2);

                break;
            case R.id.rb_user:
                fragmentController.showFragments(3);

                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {

         switch (v.getId()){

             case R.id.iv_add:
                 ToastUtils.showToast(this,"add", Toast.LENGTH_SHORT);
                 break;
             default:
                 break;
         }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragmentController=null;
        System.gc();
    }
}
