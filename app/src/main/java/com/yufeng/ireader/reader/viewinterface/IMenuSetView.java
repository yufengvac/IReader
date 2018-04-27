package com.yufeng.ireader.reader.viewinterface;

import android.view.View;

/**
 * Created by yufeng on 2018/4/27-0027.
 *
 */

public interface IMenuSetView {

    void setContentView(int layoutRes);
    View findViewById(int viewId);
    void show();
    void hide();
}
