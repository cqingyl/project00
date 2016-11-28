package com.cqing.project00.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Cqing on 2016/11/18.
 */

public class UnScrollListView extends ListView {

    public UnScrollListView(Context context) {
    super(context);
}

    public UnScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UnScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}