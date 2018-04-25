package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.yufeng.ireader.reader.bean.Page;
import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.viewinterface.PageTurn;
import com.yufeng.ireader.utils.DisplayConstant;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 左右平移翻页
 */

public class LeftRightTranslatePageTurn extends PageTurn{
    private Animator animator;
    private float translateX;

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
    public boolean draw(Canvas canvas) {
        if (isAnimationEnd()){//动画结束
            onPageTurnListener.onPageTurnAnimationEnd(canvas, getPageTurnDirection());
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
