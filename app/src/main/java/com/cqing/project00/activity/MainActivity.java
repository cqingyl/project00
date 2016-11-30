package com.cqing.project00.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.cqing.project00.R;
import com.cqing.project00.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_1) TextView btn_1;
    @BindView(R.id.btn_2) TextView btn_2;
    @BindView(R.id.btn_3) TextView btn_3;
    @BindView(R.id.btn_4) TextView btn_4;
    @BindView(R.id.btn_5) TextView btn_5;
    @BindView(R.id.btn_6) TextView btn_6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
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
