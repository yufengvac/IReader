package com.yufeng.ireader.reader.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.utils.ReadExteriorHelper;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.DisplayConstant;

/**
 * Created by yufeng on 2018/4/15.
 *
 */

public class ReadView extends View{
    private static final String TAG = ReadView.class.getSimpleName();

    private Paint contentPaint;

    private static final int DEFAULT_TEXT_SIZE = 22;
    private static final String DEFAULT_TEXT_COLOR = "#000000";
    private static final String DEFAULT_BG_COLOR = "#B3AFA7";
    private static final int DEFAULT_STROKE_WIDTH = 2;
    private boolean isTurnNext = false;
    private boolean isTurnPre = false;
    private Context context;


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
        this.context = context;
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

        if (isTurnNext){
            turnNextPage(canvas);
            isTurnNext = false;
        }else if (isTurnPre){
            turnPrePage(canvas);
            isTurnPre = false;
        }else {
            drawCurrentContent(canvas);
            prepareNextContent();
            preparePreContent();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            float touchX = event.getX();
            if (touchX > DisplayConstant.DISPLAY_WIDTH * (2.0 / 3)){
                isTurnNext = true;
                invalidate();
            }else if (touchX < DisplayConstant.DISPLAY_WIDTH *( 1.0 / 3)){
                isTurnPre = true;
                invalidate();
            }else {
                Log.e(TAG,"showMainMenu");
            }
        }else if (event.getAction() == MotionEvent.ACTION_UP){
            performClick();
        }
        return super.onTouchEvent(event);
    }

    private void drawBg(Canvas canvas){
        PageManager.getInstance().drawCanvasBg(canvas, contentPaint);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void drawCurrentContent(Canvas canvas){
        PageManager.getInstance().drawPager(canvas, contentPaint);
    }

    private void prepareNextContent(){
        PageManager.getInstance().prepareNextBitmap();
    }

    private void preparePreContent(){
//        PageManager.getInstance().preparePreBitmap();
    }

    private void turnNextPage(Canvas canvas){
        PageManager.getInstance().turnNextPage(canvas,contentPaint);
    }

    private void turnPrePage(Canvas canvas){
        PageManager.getInstance().turnPrePage(canvas, contentPaint, context);
    }


    public void prepare(IReadSetting readSetting, String path){
        PageManager.getInstance().initPagers(readSetting, path);
    }

    public void refresh(){
        invalidate();
    }

    public void onDestroy(){
        PageManager.getInstance().onDestroy();
        ReadExteriorHelper.getInstance().destroy();
    }

}
