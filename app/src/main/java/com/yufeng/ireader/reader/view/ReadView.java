package com.yufeng.ireader.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yufeng.ireader.utils.DisPlayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/15.
 *
 */

public class ReadView extends View{

    private static Paint contentPaint;

    private static final int DEFAULT_TEXT_SIZE = 18;
    private static final String DEFAULT_TEXT_COLOR = "#000000";
    private static final int DEFAULT_STROKE_WIDTH = 2;

    private int lineSpaceExtra = 30;
    private int displayWidth;
    private int displayHeight;
    private int baseLineY = 0;
    private boolean isStop = false;

    private List<String> paragraphList;
    private List<Integer> baseLineYList = new ArrayList<>();

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

    public static Paint getContentPaint(){
        return contentPaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        double height = measureLineHeight();
        if (paragraphList != null){
            for (int i = 0; i < paragraphList.size(); i++){
                canvas.drawText(paragraphList.get(i),0, (int)(i *(height + lineSpaceExtra) + height * 0.6) ,contentPaint);
            }
        }

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


    private int getMaxLine(){
        double lines = displayHeight / (measureLineHeight() + lineSpaceExtra);
        return (int) lines;
    }



    public void setParagraphList(List<String> list){
        int curLine = 0;
        int maxLines = getMaxLine();
        if (paragraphList == null){
            paragraphList = new ArrayList<>();
        }else {
            paragraphList.clear();
        }

        for (int i = 0; i < list.size() && curLine < maxLines; i ++){
            String desStr = list.get(i).trim();
            float width = contentPaint.measureText(desStr);
            if (width <= displayWidth){
                curLine ++;
                paragraphList.add(desStr);
            }else {
                int line =(int) Math.ceil((double) (width / displayWidth));
                curLine +=line;
//                paragraphList.add(desStr);
                getString(line,desStr);
            }
        }

        postInvalidate();
        requestLayout();
    }

    public void getString(int line, String desStr){
        char[] desStrs = desStr.toCharArray();
        int firstCount = desStr.length() / line;
        int tryCount = firstCount;
        int start = 0;
        boolean isAdd ;
        int totalTryCount = 0;
        do {
            isAdd = check(desStrs,start,tryCount);
            if (isAdd){
                totalTryCount +=tryCount;
                start = totalTryCount + 1;
                tryCount = firstCount;
            }else {
                tryCount ++;
            }

        }while (start + firstCount < desStrs.length);

        if (desStrs.length - start >0){
            paragraphList.add(new String(desStrs,start,desStrs.length - start ) );
        }

//            if (!check(desStrs,start,tryCount)){
//                tryCount ++;
//                check(desStrs,start, tryCount);
//            }else {
//                start = tryCount + 1;
//                if (start + firstCount >= desStrs.length){
//                    check(desStrs,start, firstCount);
//                }else {
//                    paragraphList.add(new String(desStrs,start,desStrs.length - start ) );
//                }
//            }


    }

    private boolean check(char[] chars,int start, int count){
        if (contentPaint.measureText(chars,start,count) > displayWidth){
            Log.e("ReadView","h->"+new String(chars,start,count));
            paragraphList.add(new String(chars,start,count));
            return true;
        }
        return false;
    }
}
