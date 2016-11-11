package com.cqing.project00.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cqing.project00.R;
import com.cqing.project00.adapter.CollectionMovieAdapter;
import com.cqing.project00.data.PopMoviesContract;
import com.cqing.project00.fragment.PopularDetailFragment;

/**
 * Created by Cqing on 2016/10/31.
 */

public class CollectionActivity extends AppCompatActivity{

    private ListView lv_collecting_movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        initView();
    }

    public void initView(){
        lv_collecting_movie = (ListView) findViewById(R.id.lv_collecting_movie);
        Cursor c = getContentResolver().query(PopMoviesContract.PopMoviesEntry.CONTENT_URI, PopularDetailFragment.DETAIL_COLUMNS, PopMoviesContract.PopMoviesEntry.COLUMN_COLLECTION + "=0", null, null);
        lv_collecting_movie.setAdapter(new CollectionMovieAdapter(this, c, 0));
        lv_collecting_movie.setEmptyView(findViewById(R.id.ll_empty));
        lv_collecting_movie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Intent intent = new Intent(CollectionActivity.this, MovieDetailActivity.class)
                        .setData(PopMoviesContract.PopMoviesEntry.buildPopMoviesUri(cursor.getLong(PopularDetailFragment.COL_POPMOVIES_MOVIEID)));
                startActivity(intent);
            }
        });
    }

}
