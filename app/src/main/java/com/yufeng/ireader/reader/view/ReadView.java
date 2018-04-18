package com.yufeng.ireader.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yufeng.ireader.reader.bean.PagerManager;
import com.yufeng.ireader.reader.bean.TxtParagraph;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisPlayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/15.
 *
 */

public class ReadView extends View{
    private static final String TAG = ReadView.class.getSimpleName();

    private Paint contentPaint;

    private static final int DEFAULT_TEXT_SIZE = 18;
    private static final String DEFAULT_TEXT_COLOR = "#000000";
    private static final String DEFAULT_BG_COLOR = "#B3AFA7";
    private static final int DEFAULT_STROKE_WIDTH = 2;


    public ReadView(Context context) {
       this(context, null);
    }

    public ReadView(Context context, @Nullable AttributeSet attrs) {
       this(context, attrs, 0);
    }

    public ReadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        initDefaultContentPaint(context);
    }

    private void initDefaultContentPaint(Context context){
        contentPaint = new Paint();
        contentPaint.setAntiAlias(true);
        contentPaint.setColor(Color.parseColor(DEFAULT_TEXT_COLOR));
        contentPaint.setTextSize(DisPlayUtil.sp2px(context, DEFAULT_TEXT_SIZE));
        contentPaint.setStyle(Paint.Style.FILL);
        contentPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
    }

    public Paint getContentPaint(){
        return contentPaint;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);

        drawCurrentContent(canvas);
    }

    private void drawBg(Canvas canvas){
        canvas.drawColor(Color.parseColor(DEFAULT_BG_COLOR));
    }


    private void drawCurrentContent(Canvas canvas){

    }


    public void prepare(IReadSetting readSetting, String path){
        PagerManager.getInstance().initPagers(readSetting, path);
    }

}
