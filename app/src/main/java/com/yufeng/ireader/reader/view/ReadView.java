package com.yufeng.ireader.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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
    private static final int DEFAULT_STROKE_WIDTH = 2;

    private int lineSpaceExtra = 30;
    private int displayWidth;
    private int displayHeight;
    private int baseLineY = 0;
    private boolean isStop = false;

    private IReadSetting readSetting;

    private List<String> paragraphList;
    private List<Integer> baseLineYList = new ArrayList<>();
    private int curLineCut = -1;
    private List<TxtParagraph> txtParagraphList = new ArrayList<>();

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
        displayWidth = DisPlayUtil.getDisplayWidth(context);
        displayHeight = DisPlayUtil.getDisplayHeight(context);
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

    public void setReadSetting(IReadSetting readSetting){
        this.readSetting  = readSetting;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private double measureLineHeight(){
        if (contentPaint != null){
             Paint.FontMetrics fontMetrics = contentPaint.getFontMetrics();
             double height = fontMetrics.bottom - fontMetrics.top;
             baseLineY = (int)(height * 0.6);
             return height;
        }
        return 0;
    }


    public int getCurLineCut() {
        return curLineCut;
    }

    public void setCurLineCut(int curLineCut) {
        this.curLineCut = curLineCut;
    }

    public void setTxtParagraphList(List<TxtParagraph> list){
        this.txtParagraphList = list;
        for (int i = 0; i < list.size() ; i++){
            Log.e(TAG, "content="+list.get(i).toString() );
        }
        postInvalidate();
    }
}
