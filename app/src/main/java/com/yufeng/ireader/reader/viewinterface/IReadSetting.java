package com.yufeng.ireader.reader.viewinterface;

import android.graphics.Paint;

/**
 * Created by Administrator on 2018/4/16-0016.
 *
 */

public interface IReadSetting {
    int getPaddingLeft();
    int getPaddingTop();
    int getPaddingRight();
    int getPaddingBottom();

    /**行间距*/
    int getLineSpaceExtra();

    /**每个字之间的间距*/
    int getHorizontalExtra();

    /**首行缩进空格的数量*/
    int getIndentCount();

    void setContentPaint(Paint paint);

    Paint getContentPaint();

    String getContentPaintTextColor();

    int getCanvasBgOptions();

    String getCanvasBgColor();

    int getPageTurnType();

    boolean isDayMode();
}
