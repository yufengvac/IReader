package com.yufeng.ireader.reader.viewinterface;

import android.view.View;

/**
 * Created by yufeng on 2018/4/27.
 * 阅读器基本菜单选项接口
 */

public interface OnReadMenuClickListener {
    void onCategoryClick(View view);
    void onBrightnessClick(View view);
    void onListenClick(View view);
    void onSettingClick(View view);
}
