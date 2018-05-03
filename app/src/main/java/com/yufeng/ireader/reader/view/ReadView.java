package com.yufeng.ireader.reader.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.utils.PageTurnFactory;
import com.yufeng.ireader.reader.utils.ReadExteriorConstants;
import com.yufeng.ireader.reader.utils.ReadExteriorHelper;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.reader.viewinterface.OnMenuListener;
import com.yufeng.ireader.reader.viewinterface.OnPageTurnListener;
import com.yufeng.ireader.reader.viewinterface.PageTurn;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.ReadPreferHelper;

/**
 * Created by yufeng on 2018/4/15.
 *
 */

public class ReadView extends View implements OnPageTurnListener{
    private static final String TAG = ReadView.class.getSimpleName();

    private Paint contentPaint;


    private static final String DEFAULT_TEXT_COLOR = "#000000";
    private static final String DEFAULT_BG_COLOR = "#B3AFA7";
    private static final int DEFAULT_STROKE_WIDTH = 2;
    private Context context;
    private PageTurn pageTurn;
    private OnMenuListener onMenuListener;
    private boolean isForceCalc = false;//是否需要重新进行当前页的排版


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
        contentPaint.setTextSize(DisPlayUtil.sp2px(context, ReadPreferHelper.getInstance().getFontTextSize()));
        contentPaint.setStyle(Paint.Style.FILL);
        contentPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);

        Typeface typeface = getFontFace();
        if (typeface != null){
            contentPaint.setTypeface(typeface);
        }else {
            contentPaint.setTypeface(Typeface.DEFAULT);
        }

    }

    private Typeface getFontFace(){
        int typeface = ReadPreferHelper.getInstance().getTypeface();
        Typeface fontTypeface = null;
        if (typeface == ReadExteriorConstants.ReadTypeFace.TYPEFACE_ITALIC){
            fontTypeface = Typeface.createFromAsset(context.getAssets(),"font/italic.ttf");
        }else if (typeface == ReadExteriorConstants.ReadTypeFace.TYPEFACE_XU){
            fontTypeface = Typeface.createFromAsset(context.getAssets(), "font/xujinglei.ttf");
        }
        return fontTypeface;
    }


    public Paint getContentPaint(){
        return contentPaint;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);

        if (pageTurn.getPageTurnDirection() == PageTurn.PageTurnDirection.DIRECTION_NEXT){
            if (pageTurn.onDraw(canvas)){
                pageTurn.resetPageTurnDirection();
            }
        }else if (pageTurn.getPageTurnDirection() == PageTurn.PageTurnDirection.DIRECTION_PREVIOUS){
            if (pageTurn.onDraw(canvas)){
                pageTurn.resetPageTurnDirection();
            }
        }else {
            if (!pageTurn.onTouchEvent){
                drawCurrentContent(canvas);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pageTurn.onTouchEvent(event)){
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            performClick();
            float touchX = event.getX();
            if (touchX > DisplayConstant.DISPLAY_WIDTH * (2.0 / 3)){
                pageTurn.setPageTurnDirection(PageTurn.PageTurnDirection.DIRECTION_NEXT);
                pageTurn.turnNext();
            }else if (touchX < DisplayConstant.DISPLAY_WIDTH *( 1.0 / 3)){
                pageTurn.setPageTurnDirection(PageTurn.PageTurnDirection.DIRECTION_PREVIOUS);
                pageTurn.turnPrevious();
            }else {
                if (onMenuListener != null){
                    onMenuListener.onClickMenu();
                }
            }
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
        PageManager.getInstance().drawPager(canvas, contentPaint, isForceCalc);
    }


    private void turnNextPage(Canvas canvas){
        PageManager.getInstance().turnNextPage(canvas,contentPaint);
    }

    private void turnPrePage(Canvas canvas){
        PageManager.getInstance().turnPrePage(canvas, contentPaint, context);
    }


    public void prepare(Activity activity, IReadSetting readSetting, String path){
        PageManager.getInstance().initPagers(readSetting, path);
        PageManager.getInstance().setReadView(this);


        ReadExteriorHelper.init(activity, readSetting);
        ReadExteriorHelper.getInstance().resetContentPaint();
//        ReadExteriorHelper.getInstance().setFullScreen(activity,true);
//        ReadExteriorHelper.hideNavigation(activity.getWindow().getDecorView());


        pageTurn = PageTurnFactory.createPageTurn(readSetting);
        pageTurn.setOnPageTurnListener(this);
        pageTurn.setPaint(contentPaint);
        pageTurn.setContext(getContext());
    }

    public void refreshReadView(boolean isForceCalc){
        this.isForceCalc = isForceCalc;
        invalidate();
    }

    public void recreatePageTurn(IReadSetting readSetting){
        pageTurn = PageTurnFactory.createPageTurn(readSetting);
        pageTurn.setOnPageTurnListener(this);
        pageTurn.setPaint(contentPaint);
        pageTurn.setContext(getContext());
    }

    public void setOnMenuListener(OnMenuListener listener){
        onMenuListener = listener;
    }

    @Override
    public Bitmap getNextBitmap() {
        return PageManager.getInstance().getNextCacheBitmap();
    }

    @Override
    public Bitmap getPreviousBitmap() {
        return PageManager.getInstance().getPreCacheBitmap();
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return PageManager.getInstance().getCurBitmap();
    }

    @Override
    public void onAnimationInvalidate() {
       invalidate();
    }

    @Override
    public void onPageTurnAnimationEnd(Canvas canvas, int pageTurnDirection, boolean isPageTurn) {
        if (!isPageTurn){
            return;
        }
        if (pageTurnDirection == PageTurn.PageTurnDirection.DIRECTION_NEXT){
            PageManager.getInstance().drawCanvasBitmap(canvas, getNextBitmap(),contentPaint);
            turnNextPage(canvas);
        }else if (pageTurnDirection == PageTurn.PageTurnDirection.DIRECTION_PREVIOUS){
            PageManager.getInstance().drawCanvasBitmap(canvas, getPreviousBitmap(),contentPaint);
            turnPrePage(canvas);
        }
    }

    public void saveHistory(){
        PageManager.getInstance().saveReadHistory();
    }

    public void onDestroy(){
        PageManager.getInstance().onDestroy();
        ReadExteriorHelper.getInstance().destroy();
    }

}
