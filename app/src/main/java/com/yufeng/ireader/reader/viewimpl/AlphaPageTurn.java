package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.viewinterface.PageTurn;

/**
 * Created by yufeng on 2018/5/3.
 * 淡入淡出的翻页方式
 */

public class AlphaPageTurn extends PageTurn{
    private Animator animator;
    private float curAlpha;
    private Paint bitmapPaint;

    public AlphaPageTurn() {
        bitmapPaint = new Paint();
    }

    @Override
    public void turnNext() {
        setPageTurnDirection(PageTurnDirection.DIRECTION_NEXT);
        startAnimation(1,0);
    }

    @Override
    public void turnPrevious() {
        setPageTurnDirection(PageTurnDirection.DIRECTION_PREVIOUS);
        startAnimation(0,1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return event.getAction() != MotionEvent.ACTION_UP;
    }

    @Override
    public boolean draw(Canvas canvas) {
        if (isAnimationEnd()){
            onPageTurnListener.onPageTurnAnimationEnd(canvas, getPageTurnDirection(), true);
            setAnimationEnd(false);
            return true;
        }
        canvas.save();
        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){

            bitmapPaint.setAlpha((int)((1-curAlpha)*255));
            PageManager.getInstance().drawCanvasBitmap(canvas, PageManager.getInstance().getNextCacheBitmap(), bitmapPaint);

            bitmapPaint.setAlpha((int) (curAlpha * 255));
            PageManager.getInstance().drawCanvasBitmap(canvas, PageManager.getInstance().getCurBitmap(), bitmapPaint);

        } else if(getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
            bitmapPaint.setAlpha((int) ((1- curAlpha) * 255));
            PageManager.getInstance().drawCanvasBitmap(canvas, PageManager.getInstance().getCurBitmap(), bitmapPaint);
            bitmapPaint.setAlpha((int) (curAlpha*255));
            PageManager.getInstance().drawCanvasBitmap(canvas, PageManager.getInstance().getPreCacheBitmap(), bitmapPaint);
        }
        canvas.restore();
        return false;
    }

    private void startAnimation(float startAlpha, float endAlpha){
        if (animator != null && animator.isRunning()){
            animator.cancel();
            animator = null;
        }
        animator = ObjectAnimator.ofFloat(this,"alpha",startAlpha, endAlpha);
        animator.setDuration(ANIMATION_DURATION);
        animator.addListener(animatorListener);
        animator.setInterpolator(interpolator);
        animator.start();
    }

    @SuppressWarnings("unused")
    public void setAlpha(float value){
        curAlpha = value;
        onPageTurnListener.onAnimationInvalidate();
    }
}
