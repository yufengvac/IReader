package com.yufeng.ireader.reader.viewimpl;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.yufeng.ireader.reader.viewinterface.PageTurn;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 上下覆盖翻页
 */

public class TopBottomCoveragePageTurn extends PageTurn{
    @Override
    public void turnNext() {

    }

    @Override
    public void turnPrevious() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return event.getAction() != MotionEvent.ACTION_UP;
    }

    @Override
    public boolean draw(Canvas canvas) {
        return true;
    }
}
