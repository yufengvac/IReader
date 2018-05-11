package com.yufeng.ireader.ui.home.other;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yufeng.ireader.utils.DisPlayUtil;

/**
 * Created by yufeng on 2018/5/11-0011.
 * RecyclerView分割线
 */

public class RecyclerViewDivider extends RecyclerView.ItemDecoration {

    public static final int LINEAR_LAYOUT = 0;
    public static final int GRID_LAYOUT = 1;

    private int mOrientation;
    private Context mContext;

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public RecyclerViewDivider(Context context) {
        mContext = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int topMargin;
        int position = parent.getChildAdapterPosition(view);
        if (mOrientation == LINEAR_LAYOUT) {

            if (position == 0) {
                topMargin = DisPlayUtil.dp2px(mContext, 10);
            } else {
                topMargin = 0;
            }
            outRect.set(0, topMargin, 0, 0);
        } else if (mOrientation == GRID_LAYOUT) {
            outRect.set(0, 0, 0, 0);
        }
    }
}
