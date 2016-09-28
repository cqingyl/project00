package com.cqing.project00.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cqing.project00.R;
import com.cqing.project00.fragment.PopularDetailFragment;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PopularDetailFragment())
                    .commit();
        }
    }
}
