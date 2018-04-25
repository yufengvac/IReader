package com.yufeng.ireader.reader.viewimpl;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.viewinterface.PageTurn;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 无动画翻页
 */

public class NonePageTurn extends PageTurn {

    @Override
    public void turnNext() {
        onPageTurnListener.onAnimationInvalidate();
    }

    @Override
    public void turnPrevious() {
        onPageTurnListener.onAnimationInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean draw(Canvas canvas) {
        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getNextBitmap(), null);
            PageManager.getInstance().turnNextPage(canvas, contentPaint);
        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getPreviousBitmap(), null);
            PageManager.getInstance().turnPrePage(canvas, contentPaint, context);
        }

        return true;
    }
}
