package com.cqing.project00.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.cqing.project00.R;
import com.cqing.project00.fragment.PopularMoviesFragment;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Cqing on 2016/10/27.
 */

public class PopMoviesAdapter extends CursorAdapter {
    public PopMoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.item_popular_movies, null);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String url = cursor.getString(PopularMoviesFragment.COL_POPMOVIES_COLUMN_POSTER_PATH);
        Picasso.with(context)
                .load(url)
                .error(R.mipmap.ic_launcher)
                .into(viewHolder.iv);
    }

    public class ViewHolder {
        @BindView(R.id.iv_popular_movies) ImageView iv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
