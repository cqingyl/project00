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
public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tv_review_author;
    public TextView tv_review_content;
    public LinearLayout ll_review;
    MovieDetailAdapter.OnItemClickListener clickListener;
    public ReviewViewHolder( View itemView , MovieDetailAdapter.OnItemClickListener clickListener) {
        super(itemView);
        this.clickListener = clickListener;
        tv_review_author = (TextView) itemView.findViewById(R.id.tv_review_author);
        tv_review_content = (TextView) itemView.findViewById(R.id.tv_review_content);
        ll_review = (LinearLayout) itemView.findViewById(R.id.ll_review);
        ll_review.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (clickListener != null) {
            clickListener.onClick(itemView, getAdapterPosition());
        }

    }
}
