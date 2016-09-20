package com.cqing.project00.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cqing.project00.R;
import com.cqing.project00.bean.PopularMovies;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter and sync
 */

/**
 * Created by Cqing on 2016/9/13.
 */

public class PopularMoviesAdapter extends BaseAdapter {

    private Context mContext;
    private List<PopularMovies> mData;

    public PopularMoviesAdapter(Context mContext, List<PopularMovies> mData){
        this.mContext = mContext;
        this.mData = mData;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public PopularMovies getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_popular_movies, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_popular_movies);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 加载图片
      // holder.iv.setImageResource(mData.get(position).getImgId());
        PopularMovies movies = getItem(position);
        String url = movies.getImgUrl();
        Picasso.with(mContext).load(url).resize(200,200).into(holder.iv);

        return convertView;
    }

    public class ViewHolder {
        public  ImageView iv;
    }


}
