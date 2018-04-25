package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.MotionEvent;

import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.viewinterface.PageTurn;
import com.yufeng.ireader.utils.DisplayConstant;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 左右覆盖翻页
 */

public class LeftRightCoveragePageTurn extends PageTurn{
    private static final String TAG = LeftRightCoveragePageTurn.class.getSimpleName();
    private Animator animator;
    private float translateX;
    private float touchX = 0;
    private boolean hasEnsureDirection = false;

    private GradientDrawable[] shadowDrawable = new GradientDrawable[2];
    private static final int[][] SHADOW_COLOR = {{0x50454545, 0x00454545,}, {0xb0151515, 0x00151515}};
    private int shadowWidth;

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
        shadowWidth = 30;
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
        if (animator != null && animator.isRunning()){
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            touchX = event.getX();
            hasEnsureDirection = false;
            onTouchEvent = true;
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            onTouchEvent = true;
            if (!hasEnsureDirection){
                if (event.getX() > touchX){ //方向是向右，即翻向上一页
                    setPageTurnDirection(PageTurnDirection.DIRECTION_PREVIOUS);
                    hasEnsureDirection = true;
                }else if (event.getX() < touchX){ //方向是像左，即翻向下一页
                    setPageTurnDirection(PageTurnDirection.DIRECTION_NEXT);
                    hasEnsureDirection = true;
                }
            }
            if (hasEnsureDirection){
                if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
                    setShiftX(event.getX() - touchX);
                }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
                    setShiftX(-DisplayConstant.DISPLAY_WIDTH + event.getX() - touchX);
                }

            }

        }else if (event.getAction() == MotionEvent.ACTION_UP){
            onTouchEvent = false;
            hasEnsureDirection = false;
            if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
                startAnimation(event.getX() - touchX, -DisplayConstant.DISPLAY_WIDTH);
            }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
                startAnimation(-DisplayConstant.DISPLAY_WIDTH + event.getX() - touchX,0);
            }else if (event.getX() == touchX){
                return false;
            }

        }
        return true;
    }

    @Override
    public boolean draw(Canvas canvas) {
        if (isAnimationEnd()){//动画结束
            onPageTurnListener.onPageTurnAnimationEnd(canvas, getPageTurnDirection());
            setAnimationEnd(false);
            return true;
        }
        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getNextBitmap(), null);
        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
        }

        canvas.save();
        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
            canvas.translate(translateX + DisplayConstant.DISPLAY_WIDTH,0);
            //绘制阴影
            Drawable drawable = getShadow(true);
            drawable.setBounds(0, 0, (int) Math.min(shadowWidth, -translateX), DisplayConstant.DISPLAY_HEIGHT);
            drawable.draw(canvas);

            canvas.translate(- DisplayConstant.DISPLAY_WIDTH, 0);
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);

        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
            canvas.translate(translateX + DisplayConstant.DISPLAY_WIDTH,0);
            //绘制阴影
            Drawable drawable = getShadow(true);
            drawable.setBounds(0, 0, (int) Math.min(shadowWidth, -translateX), DisplayConstant.DISPLAY_HEIGHT);
            drawable.draw(canvas);

            canvas.translate(- DisplayConstant.DISPLAY_WIDTH, 0);
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getPreviousBitmap(), null);
        }


        canvas.restore();
        return false;
    }

    private Drawable getShadow(boolean dayMode) {
        int index = dayMode ? 0 : 1;
        if (shadowDrawable[index] == null) {
            shadowDrawable[index] = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, SHADOW_COLOR[index]);
            shadowDrawable[index].setGradientType(GradientDrawable.LINEAR_GRADIENT);
        }
        return shadowDrawable[index];
    }
}
