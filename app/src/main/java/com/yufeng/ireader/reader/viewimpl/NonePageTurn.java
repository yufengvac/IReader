package com.yufeng.ireader.reader.viewimpl;

import android.graphics.Canvas;

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
    public boolean draw(Canvas canvas) {
        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
            PageManager.getInstance().turnNextPage(canvas, contentPaint);
        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
            PageManager.getInstance().turnPrePage(canvas, contentPaint, context);
        }

        return true;
    }
}
