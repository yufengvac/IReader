package com.yufeng.ireader.reader.viewinterface;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by yfueng on 2018/4/25-0025.
 * 翻页回调
 */

public interface OnPageTurnListener {

    Bitmap getNextBitmap();
    Bitmap getCurrentBitmap();
    Bitmap getPreviousBitmap();
    void onAnimationInvalidate();
    void onPageTurnAnimationEnd(Canvas canvas, int pageTurnDirection);
}
