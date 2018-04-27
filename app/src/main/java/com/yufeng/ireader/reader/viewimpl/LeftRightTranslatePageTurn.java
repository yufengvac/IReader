package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.viewinterface.PageTurn;
import com.yufeng.ireader.utils.DisplayConstant;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 左右平移翻页
 */

public class LeftRightTranslatePageTurn extends PageTurn{
    private Animator animator;
    private float translateX ;

    @SuppressWarnings("unused")
    public void setShiftX(float x){
        translateX = x;
        onPageTurnListener.onAnimationInvalidate();
    }

    private void startAnimation(float startX, float endX){
        if (animator != null && animator.isRunning()){
            animator.cancel();
            animator = null;
        }
        animator = ObjectAnimator.ofFloat(this,"shiftX",startX, endX);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(interpolator);
        animator.addListener(animatorListener);
        animator.start();
    }

    @Override
    public void turnNext() {
        startAnimation(0, -DisplayConstant.DISPLAY_WIDTH);
    }

    @Override
    public void turnPrevious() {
        startAnimation(-DisplayConstant.DISPLAY_WIDTH,0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean draw(Canvas canvas) {
        if (isAnimationEnd()){//动画结束
            onPageTurnListener.onPageTurnAnimationEnd(canvas, getPageTurnDirection(), true);
            return true;
        }
        canvas.save();
        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
            canvas.translate(translateX,0);
            PageManager.getInstance().drawCanvasBitmap(canvas,onPageTurnListener.getCurrentBitmap(), null);

            canvas.translate(DisplayConstant.DISPLAY_WIDTH, 0);
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getNextBitmap(), null);

        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
            canvas.translate(translateX, 0);
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getPreviousBitmap(), null);

            canvas.translate(DisplayConstant.DISPLAY_WIDTH,0);
            PageManager.getInstance().drawCanvasBitmap(canvas,onPageTurnListener.getCurrentBitmap(),null);
        }


        canvas.restore();
        return false;
    }
}
