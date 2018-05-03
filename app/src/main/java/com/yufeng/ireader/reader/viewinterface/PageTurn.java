package com.yufeng.ireader.reader.viewinterface;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

/**
 * Created by yufeng on 2018/4/25-0025.
 *
 */

public abstract class PageTurn {

    protected OnPageTurnListener onPageTurnListener;

    private boolean isAnimationEnd = false;
    protected Paint contentPaint ;
    private int pageTurnDirection = -1;
    protected Context context;
    public boolean onTouchEvent = false;

    public static class PageTurnType{
        public static final int LEFT_RIGHT_COVERAGE = 0;
        public static final int LEFT_RIGHT_TRANSLATION = 1;
        public static final int SIMULATION = 2;
        public static final int TOP_BOTTOM_COVERAGE = 3;
        public static final int NONE = 4;
        public static final int ALPHA = 5;
    }

    public static class PageTurnDirection{
        public static final int DIRECTION_NEXT = 0;
        public static final int DIRECTION_PREVIOUS = 1;
        public static final int DIRECTION_NULL = -1;
    }

    protected static final int ANIMATION_DURATION = 300;
    protected static final int CRITICAL_VALUE = 25;
    protected LinearInterpolator interpolator = new LinearInterpolator();

    protected Animator.AnimatorListener animatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            animation.removeAllListeners();
            setAnimationEnd(true);
        }

        @Override
        public void onAnimationStart(Animator animation) {
            setAnimationEnd(false);
        }
    };

    public abstract void turnNext();
    public abstract void turnPrevious();
    public abstract boolean draw(Canvas canvas);
    public abstract boolean onTouchEvent(MotionEvent event);

    protected boolean isAnimationEnd(){
        return isAnimationEnd;
    }

    protected void setAnimationEnd(boolean animationEnd) {
        isAnimationEnd = animationEnd;
    }

    public final boolean onDraw(Canvas canvas){
        return draw(canvas);
    }


    public void resetPageTurnDirection(){
        pageTurnDirection = -1;
    }

    public int getPageTurnDirection() {
        return pageTurnDirection;
    }

    public void setPageTurnDirection(int pageTurnDirection) {
        this.pageTurnDirection = pageTurnDirection;
    }

    public void setOnPageTurnListener(OnPageTurnListener onPageTurnListener) {
        this.onPageTurnListener = onPageTurnListener;
    }

    public void setPaint(Paint paint){
        this.contentPaint = paint;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
