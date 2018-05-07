package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;

import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.viewinterface.PageTurn;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.ReadPreferHelper;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 上下覆盖翻页
 */

public class TopBottomCoveragePageTurn extends PageTurn{
    private Animator animator;
    private float curY;
    private boolean hasEnsureDirection = false;
    private float touchStartY = 0;
    private GradientDrawable[] shadowDrawable = new GradientDrawable[2];
    private static final int[][] SHADOW_COLOR = {{0x50454545, 0x00454545,}, {0xb0151515, 0x00151515}};
    private boolean isDayMode;
    private int shadowWidth;
    @Override
    public void turnNext() {
        startAnimation(0, -DisplayConstant.DISPLAY_HEIGHT, ANIMATION_DURATION);
    }

    @Override
    public void turnPrevious() {
        startAnimation(-DisplayConstant.DISPLAY_HEIGHT,0, ANIMATION_DURATION);
    }

    private void setShiftY(float curY){
        this.curY = curY;
        if (onPageTurnListener != null){
            onPageTurnListener.onAnimationInvalidate();
        }
    }
    private void startAnimation(float startY, float endY, int duration){
        if (animator != null && animator.isRunning()){
            animator.cancel();
            animator = null;
        }
        shadowWidth = 30;
        isDayMode = ReadPreferHelper.getInstance().isDayMode();
        animator = ObjectAnimator.ofFloat(this, "shiftY", startY, endY);
        animator.setDuration(duration);
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
            touchStartY = event.getY();
            hasEnsureDirection = false;
            onTouchEvent = true;
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            onTouchEvent = true;
            if (!hasEnsureDirection){
                if (event.getY() - touchStartY > 0){
                    setPageTurnDirection(PageTurnDirection.DIRECTION_PREVIOUS);
                    hasEnsureDirection = true;
                }else if (event.getY() - touchStartY < 0 ){
                    setPageTurnDirection(PageTurnDirection.DIRECTION_NEXT);
                    hasEnsureDirection = true;
                }
            }
            if (hasEnsureDirection){
                if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
                    if (event.getY() - touchStartY<=0){
                        setShiftY(event.getY()-touchStartY);
                    }
                }else {
                    setShiftY(-DisplayConstant.DISPLAY_HEIGHT + event.getY() - touchStartY);
                }
            }
        }else if (event.getAction() == MotionEvent.ACTION_UP){
            hasEnsureDirection = false;
            onTouchEvent = false;
            float touchEndY = event.getY();
            int duration =(int)(ANIMATION_DURATION * 1.0 / DisplayConstant.DISPLAY_HEIGHT *(DisplayConstant.DISPLAY_HEIGHT - Math.abs(touchEndY - touchStartY)));
            if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
                startAnimation(touchEndY - touchStartY, -DisplayConstant.DISPLAY_HEIGHT, duration);
            }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
                startAnimation(-DisplayConstant.DISPLAY_HEIGHT + touchEndY - touchStartY,0, duration);
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

            canvas.translate(0,  curY + DisplayConstant.DISPLAY_HEIGHT);
            Drawable drawable = getShadow(isDayMode);
            drawable.setBounds(0, 0, DisplayConstant.DISPLAY_WIDTH, (int) Math.min(shadowWidth, -curY));
            drawable.draw(canvas);

            canvas.translate(0, -DisplayConstant.DISPLAY_HEIGHT);
            PageManager.getInstance().drawCanvasBitmap(canvas, PageManager.getInstance().getCurBitmap(), null);

        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){

            canvas.translate(0, curY + DisplayConstant.DISPLAY_HEIGHT);

            Drawable drawable = getShadow(isDayMode);
            drawable.setBounds(0, 0, DisplayConstant.DISPLAY_WIDTH, (int) Math.min(shadowWidth, -curY));
            drawable.draw(canvas);

            canvas.translate(0, - DisplayConstant.DISPLAY_HEIGHT);
            PageManager.getInstance().drawCanvasBitmap(canvas, PageManager.getInstance().getPreCacheBitmap(), null);
        }

        canvas.restore();
        return false;
    }

    private Drawable getShadow(boolean dayMode) {
        int index = dayMode ? 0 : 1;
        if (shadowDrawable[index] == null) {
            shadowDrawable[index] = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, SHADOW_COLOR[index]);
            shadowDrawable[index].setGradientType(GradientDrawable.LINEAR_GRADIENT);
        }
        return shadowDrawable[index];
    }
}
