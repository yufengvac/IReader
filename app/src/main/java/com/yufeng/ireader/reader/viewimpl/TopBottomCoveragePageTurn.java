package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.viewinterface.PageTurn;
import com.yufeng.ireader.utils.DisplayConstant;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 上下覆盖翻页
 */

public class TopBottomCoveragePageTurn extends PageTurn{
    private Animator animator;
    private float curY;
    private boolean hasEnsureDirection = false;
    private float touchY = 0;
    @Override
    public void turnNext() {
        startAnimation(0, -DisplayConstant.DISPLAY_HEIGHT);
    }

    @Override
    public void turnPrevious() {
        startAnimation(0, DisplayConstant.DISPLAY_HEIGHT);
    }
    @SuppressWarnings("unused")
    private void setShiftY(float curY){
        this.curY = curY;
        if (onPageTurnListener != null){
            onPageTurnListener.onAnimationInvalidate();
        }
    }
    private void startAnimation(float startY, float endY){
        if (animator != null && animator.isRunning()){
            animator.cancel();
            animator = null;
        }
        animator = ObjectAnimator.ofFloat(this, "shiftY", startY, endY);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(interpolator);
        animator.addListener(animatorListener);
        animator.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animator != null && animator.isRunning()){
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            touchY = event.getY();
            hasEnsureDirection = false;
            onTouchEvent = true;
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            onTouchEvent = true;
            if (!hasEnsureDirection){
                if (event.getY() - touchY > 0){
                    setPageTurnDirection(PageTurnDirection.DIRECTION_PREVIOUS);
                    hasEnsureDirection = true;
                }else if (event.getY() - touchY < 0 ){
                    setPageTurnDirection(PageTurnDirection.DIRECTION_NEXT);
                    hasEnsureDirection = true;
                }
            }
            if (hasEnsureDirection){
                setShiftY(event.getY());
            }
        }else if (event.getAction() == MotionEvent.ACTION_UP){
            hasEnsureDirection = false;
            onTouchEvent = false;
            float startY = event.getY();
            if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
                startAnimation(touchY - startY, -DisplayConstant.DISPLAY_HEIGHT);
            }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
                startAnimation(startY - touchY, DisplayConstant.DISPLAY_HEIGHT);
            }else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean draw(Canvas canvas) {
        if (isAnimationEnd()){//动画结束
            onPageTurnListener.onPageTurnAnimationEnd(canvas, getPageTurnDirection(), true);
            setAnimationEnd(false);
            return true;
        }

        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
            PageManager.getInstance().drawCanvasBitmap(canvas, PageManager.getInstance().getNextCacheBitmap(), null);
        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
            PageManager.getInstance().drawCanvasBitmap(canvas, PageManager.getInstance().getCurBitmap(), null);
        }

        canvas.save();
        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){

            canvas.translate(0, -curY);
            PageManager.getInstance().drawCanvasBitmap(canvas, PageManager.getInstance().getCurBitmap(), null);

        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){

            canvas.translate(0, curY);
            PageManager.getInstance().drawCanvasBitmap(canvas, PageManager.getInstance().getPreCacheBitmap(), null);

        }

        canvas.restore();
        return true;
    }
}
