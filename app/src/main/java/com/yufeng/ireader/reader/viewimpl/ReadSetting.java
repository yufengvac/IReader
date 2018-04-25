package com.yufeng.ireader.reader.viewimpl;

import android.graphics.Paint;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;

/**
 * Created by Administrator on 2018/4/16-0016.
 *
 */

public class ReadSetting implements IReadSetting {

    /**画内容的 画笔 区分画标题的画笔*/
    private Paint contentPaint;

    @Override
    public int getPaddingLeft() {
        return 45;
    }

    @Override
    public int getPaddingTop() {
        return 90;
    }

    @Override
    public int getPaddingRight() {
        return 45;
    }

    @Override
    public int getPaddingBottom() {
        return 90;
    }

    @Override
    public int getLineSpaceExtra() {
        return 30;
    }

    @Override
    public int getHorizontalExtra() {
        return 6;
    }

    @Override
    public int getIndentCount() {
        return 2;
    }

    @Override
    public void setContentPaint(Paint paint){
        this.contentPaint = paint;
    }

    @Override
    public Paint getContentPaint(){
        return contentPaint;
    }

    @Override
    public int getCanvasBgOptions() {
        return 2;
    }

    @Override
    public String getCanvasBgColor() {
        return "#B3AFA7";
    }

    @Override
    public int getPageTurnType() {
        return 1;
    }
}
