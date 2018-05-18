package com.yufeng.ireader.reader.viewimpl;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.yufeng.ireader.reader.viewinterface.PageTurn;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 仿真翻页
 */

public class SimulationPageTurn extends PageTurn{
    private float touchX, touchY ;
    private boolean hasDirection = false;
    @Override
    public void turnNext() {

    }

    @Override
    public void turnPrevious() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP){
            touchX = event.getX();
            touchY = event.getY();
            hasDirection = false;
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            if (!hasDirection){
                if (event.getX() > touchX){
                    setPageTurnDirection(PageTurnDirection.DIRECTION_PREVIOUS);
                    hasDirection = true;
                }else if (event.getX() < touchX){
                    setPageTurnDirection(PageTurnDirection.DIRECTION_NEXT);
                    hasDirection = true;
                }
            }

            if (hasDirection){
                calcPoint(event.getX(), event.getY());
            }

        }else if (event.getAction() == MotionEvent.ACTION_DOWN){
            return true;
        }
        return false;
    }

    private void calcPoint(float touchX, float touchY){

    }

    @Override
    public boolean draw(Canvas canvas) {
        return true;
    }
}
