package com.cqing.project00.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cqing.project00.R;
import com.cqing.project00.adapter.MovieDetailAdapter;

/**
 * Created by Cqing on 2016/11/25.
 */
public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tv_trailer;
    public LinearLayout ll_trailer;
    public MovieDetailAdapter.OnItemClickListener clickListener;
    public VideoViewHolder(View itemView ,MovieDetailAdapter.OnItemClickListener clickListener) {
        super(itemView);
        this.clickListener = clickListener;
        tv_trailer = (TextView) itemView.findViewById(R.id.tv_trailer);
        ll_trailer = (LinearLayout) itemView.findViewById(R.id.ll_trailer);
        ll_trailer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (clickListener != null) {
            clickListener.onClick(itemView, getAdapterPosition());
        }
    }
}
