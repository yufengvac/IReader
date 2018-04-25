package com.yufeng.ireader.reader.viewimpl;

import android.graphics.Canvas;

import com.yufeng.ireader.reader.viewinterface.PageTurn;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 仿真翻页
 */

public class SimulationPageTurn extends PageTurn{
    @Override
    public void turnNext() {

    }

    @Override
    public void turnPrevious() {

    }

    @Override
    public boolean draw(Canvas canvas) {
        return false;
    }
}
