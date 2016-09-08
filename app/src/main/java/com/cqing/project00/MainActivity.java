package com.cqing.project00;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

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
                Toast.makeText(MainActivity.this,"This button will launch POPULAR MOVIES!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_2 :
                Toast.makeText(MainActivity.this,"This button will launch STOCK HAWK!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_3 :
                Toast.makeText(MainActivity.this,"This button will BUILD IT BIGGER!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_4 :
                Toast.makeText(MainActivity.this,"This button will launch MAKE YOUR APP MATERIAL!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_5 :
                Toast.makeText(MainActivity.this,"This button will launch GO UBIQUITOUS!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_6 :
                Toast.makeText(MainActivity.this,"This button will launch my capstone app!",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
