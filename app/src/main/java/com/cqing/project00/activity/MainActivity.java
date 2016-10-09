package com.cqing.project00.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cqing.project00.R;
import com.cqing.project00.utils.ToastUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_1 :
                ToastUtil.show(MainActivity.this, getString(R.string.btn1_toast));
                startActivity(new Intent(this, PopularMoviesActivity.class));
                break;
            case R.id.btn_2 :
                ToastUtil.show(MainActivity.this,getString(R.string.btn2_toast));
                break;
            case R.id.btn_3 :
                ToastUtil.show(MainActivity.this,getString(R.string.btn3_toast));
                break;
            case R.id.btn_4 :
                ToastUtil.show(MainActivity.this,getString(R.string.btn4_toast));
                break;
            case R.id.btn_5 :
                ToastUtil.show(MainActivity.this,getString(R.string.btn5_toast));
                break;
            case R.id.btn_6 :
                ToastUtil.show(MainActivity.this,getString(R.string.btn6_toast));
                break;
        }
    }
}
