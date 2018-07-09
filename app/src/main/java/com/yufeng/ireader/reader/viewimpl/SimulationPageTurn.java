package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;

import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.viewinterface.PageTurn;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.ReadPreferHelper;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 仿真翻页
 */

public class SimulationPageTurn extends PageTurn{
    private float touchX, touchY ;
    private float startX, startY;
    private boolean hasDirection = false;
    private boolean isPageTurn = true;
    private float mValueAdded;// 精度附减值
    private float mBuffArea;

    private int viewHeight;
    private int viewWidth;

    private static final float CURVATURE = 1 / 4F;// 曲度值
    private static final float VALUE_ADDED = 1 / 400F;// 精度附加值占比
    private static final float BUFF_AREA = 1 / 50F;// 底部缓冲区域占比

    private Path mPath ;
    private Path mPathTrap ;
    private Path mPathSemicircleBtm ;
    private Path mPathFoldAndNext ;
    private Path mPathSemicircleLeft;

    private Path mPathShadowDiagonally;//斜对角阴影path
    private float hypotenuseDegree;//折线边的斜率，折角的左边斜线
    private Path mPathHypotenuse;//折线边
    private float x1,y1,x2,y2;
    private Ratio mRatio;// 定义当前折叠边长
    private float mDegree;

    private Shadow shadow;
    private boolean isDayMode = true;
    private boolean isVerticalPage = false;//是否是垂直翻页，不是折角翻页
    private int verticalEndX;

    /**
     * 枚举类定义长边短边
     */
    private enum Ratio {
        LONG, SHORT
    }

    private Region mRegionShortSize;// 短边的有效区域


    private RectF mRectFSemicircle;

    private Paint contentPaint;
    private Animator animator;

    public SimulationPageTurn(){
        viewWidth = DisplayConstant.DISPLAY_WIDTH;
        viewHeight = DisplayConstant.DISPLAY_HEIGHT_SIMULATION;

        mPath = new Path();
        mPathTrap = new Path();
        mPathSemicircleBtm = new Path();
        mPathFoldAndNext = new Path();
        mPathSemicircleLeft = new Path();
        mPathShadowDiagonally = new Path();
        mPathHypotenuse = new Path();

        /*
		 * 实例化区域对象
		 */
        mRegionShortSize = new Region();

        mValueAdded = viewHeight * VALUE_ADDED;
        mBuffArea = viewHeight * BUFF_AREA;


        mRectFSemicircle = new RectF();


        initPaint();
        computeShortSizeRegion();
    }

    private void initPaint(){
        contentPaint = new Paint();
        contentPaint.setAntiAlias(true);
        contentPaint.setColor(Color.parseColor("#A4A19E"));
        contentPaint.setTextSize(2f);
        contentPaint.setStyle(Paint.Style.STROKE);
        contentPaint.setStrokeWidth(2f);
    }
    @Override
    public void turnNext() {
        startAnimation(viewWidth-200, -viewWidth, ANIMATION_DURATION);
    }

    @Override
    public void turnPrevious() {
        startAnimation(-viewWidth, viewWidth, ANIMATION_DURATION);
    }

    private void startAnimation(float startX, float endX, int duration){
        if (animator != null && animator.isRunning()){
            animator.cancel();
            animator = null;
        }
        animator = ObjectAnimator.ofFloat(this,"shiftX",startX, endX);
        animator.setDuration(duration);
        animator.setInterpolator(interpolator);
        animator.addListener(animatorListener);
        animator.start();
    }

    @SuppressWarnings("unused")
    private void setShiftX(float x){
        touchX = x;
        if (isPageTurn){
            touchY = startY + ((x - startX) * (viewHeight - startY)) / (-viewWidth - startX);
        }else {
            touchY = startY + ((x - startX) * (viewHeight - startY)) / (viewWidth - startX);
        }
        if (isVerticalPage){
            calcPoint(x, touchY);
        }else {
            calcPoint1(x, touchY, true);
        }
        onPageTurnListener.onAnimationInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            touchX = event.getX();
            touchY = event.getY();
            hasDirection = false;
            onTouchEvent = true;
            isPageTurn = true;
            isDayMode = ReadPreferHelper.getInstance().isDayMode();
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            onTouchEvent = true;
            if (!hasDirection){
                if (event.getX() > touchX){
                    setPageTurnDirection(PageTurnDirection.DIRECTION_PREVIOUS);
                    hasDirection = true;
                }else if (event.getX() < touchX){
                    setPageTurnDirection(PageTurnDirection.DIRECTION_NEXT);
                    hasDirection = true;
                }
                if (hasDirection){
                    isVerticalPage = (event.getY() < viewHeight -  viewWidth * 0.25f);
                }
            }

            if (hasDirection){
                if (PageManager.getInstance().isFirstPage() && getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
                    return false;
                }else {
                    if (isVerticalPage){
                        calcPoint(event.getX(), event.getY());
                    }else {
                        calcPoint1(event.getX(), event.getY(), false);
                    }
                }

            }

        }else if (event.getAction() == MotionEvent.ACTION_UP){
            onTouchEvent = false;
            startX = event.getX();
            startY = event.getY();
            if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
                if (startX < viewWidth * 0.75){ //如果滑动到左侧小于屏幕3/4处认为是翻开下一页
                    isPageTurn = true;
                    startAnimation(event.getX(),-viewWidth, ANIMATION_DURATION);
                }else {
                    isPageTurn = false;
                    startAnimation(event.getX(),viewWidth, ANIMATION_DURATION);
                }
            }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
                if (!PageManager.getInstance().isFirstPage()){
                    startAnimation(event.getX(), viewWidth , ANIMATION_DURATION);
                }else {
                    PageManager.getInstance().turnPrePage(context);
                }
            }else if (event.getX() == touchX){
                isVerticalPage  = true;
                startX = viewWidth - 200;
                startY = viewHeight;
                return false;
            }

        }
        return true;
    }

    private void calcPoint(float touchX, float touchY){
        mPath.reset();
        mPathFoldAndNext.reset();

        float endX = 0.6f * touchX + 0.4f * viewWidth;

        mPath.moveTo(touchX, viewHeight);
        mPath.lineTo(touchX, 0);
        mPath.lineTo(endX, 0);
        mPath.lineTo(endX, viewHeight);
        mPath.close();

        mPathFoldAndNext.moveTo(touchX, viewHeight);
        mPathFoldAndNext.lineTo(touchX, 0);
        mPathFoldAndNext.lineTo(viewWidth, 0);
        mPathFoldAndNext.lineTo(viewWidth, viewHeight);
        mPathFoldAndNext.close();

        verticalEndX = (int) endX;
        this.touchX = touchX;
        this.touchY = touchY;
        onPageTurnListener.onAnimationInvalidate();
    }

    private void calcPoint1(float touchX, float touchY, boolean canScrollBottom){
        mPath.reset();
        mPathFoldAndNext.reset();
        mPathTrap.reset();
        mPathSemicircleBtm.reset();
        mPathSemicircleLeft.reset();
        mPathShadowDiagonally.reset();

        viewHeight = DisplayConstant.DISPLAY_HEIGHT_SIMULATION;

        if (!mRegionShortSize.contains((int)touchX, (int)touchY) && !isVerticalPage){
            touchY = (float)(Math.sqrt((Math.pow(viewWidth, 2) - Math.pow(touchX, 2))) - viewHeight);
            touchY = Math.abs(touchY) + mValueAdded;
            float area = viewHeight - mBuffArea;
            if (touchY >= area && !canScrollBottom){
                touchY = area;
            }
        }

        float mk = viewWidth - touchX;
        float ml = viewHeight - touchY;

        float temp = (float)(Math.pow(ml, 2) + Math.pow(mk, 2));

        float sizeShort = temp / (2f * mk);
        float sizeLong = temp / (2f * ml);

        float tempAM = mk - sizeShort;


        if (sizeShort < sizeLong){
            mRatio = Ratio.SHORT;
            float sin = tempAM / sizeShort;
            mDegree = (float)(Math.asin(sin) / Math.PI * 180);
        }else {
            mRatio = Ratio.LONG;
            float cos = mk / sizeLong;
            mDegree = (float) (Math.acos(cos)/ Math.PI * 180);
        }

        if (sizeLong > viewHeight){

            float an = sizeLong - viewHeight;
            float largerTriangleShortSize = an / (sizeLong - (viewHeight - touchY)) * (viewWidth - touchX);
            float smallTriangleShortSize = an / sizeLong * sizeShort;

            float topX1 = viewWidth - largerTriangleShortSize;
            float topX2 = viewWidth - smallTriangleShortSize;
            float btmX2 = viewWidth - sizeShort;

            //计算曲线起点
            float startXBtm = btmX2 - CURVATURE * sizeShort;
            float startYBtm = viewHeight;

            //计算曲线终点
            float endXBtm = touchX + (1 - CURVATURE) * tempAM;
            float endYBtm = touchY + (1 - CURVATURE) * ml;

            //计算曲线控制点
            float controlXBtm = btmX2 * 1.0f;
            float controlYBtm = viewHeight;

            //计算曲线顶点
            float bezierPeakXBtm = 0.25f * startXBtm + 0.5f * controlXBtm + 0.25f * endXBtm;
            float bezierPeakYBtm = 0.25f * startYBtm + 0.5f * controlYBtm + 0.25f * endYBtm;

            mPath.moveTo(startXBtm, startYBtm);
            mPath.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
            mPath.lineTo(touchX, touchY);
            mPath.lineTo(topX1, 0);
            mPath.lineTo(topX2, 0);


            mPathTrap.moveTo(startXBtm, startYBtm);
            mPathTrap.lineTo(topX2, 0);
            mPathTrap.lineTo(bezierPeakXBtm, bezierPeakYBtm);
            mPathTrap.close();

            mPathSemicircleBtm.moveTo(startXBtm, startYBtm);
            mPathSemicircleBtm.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
            mPathSemicircleBtm.close();

            mPathFoldAndNext.moveTo(startXBtm, startYBtm);
            mPathFoldAndNext.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
            mPathFoldAndNext.lineTo(touchX, touchY);
            mPathFoldAndNext.lineTo(topX1, 0);
            mPathFoldAndNext.lineTo(viewWidth, 0);
            mPathFoldAndNext.lineTo(viewWidth, viewHeight);
            mPathFoldAndNext.close();

            x1 = bezierPeakXBtm;
            y1 = bezierPeakYBtm;
            x2 = topX2;
            y2 = 0;

            hypotenuseDegree = (touchX - controlXBtm)/(touchY - controlYBtm);

            mPathSemicircleBtm.computeBounds(mRectFSemicircle, false);
        }else {

            float leftY = viewHeight - sizeLong;
            float btmX = viewWidth - sizeShort;

            //计算曲线起点
            float startXBtm = btmX - CURVATURE * sizeShort;
            float startYBtm = viewHeight;
            float startXLeft = viewWidth;
            float startYLeft = leftY - CURVATURE * sizeLong;

            //计算曲线终点
            float endXBtm = touchX + (1 - CURVATURE) * tempAM;
            float endYBtm = touchY + (1 - CURVATURE) * ml;
            float endXLeft = touchX + (1 - CURVATURE) * mk;
            float endYLeft = touchY - (1 - CURVATURE) * (sizeLong - ml);

            //计算曲线控制点
            float controlXBtm = btmX * 1.0f;
            float controlYBtm = viewHeight;
            float controlXLeft = viewWidth;
            float controlYLeft = leftY * 1.0f;

            //计算曲线顶点
            float bezierPeakXBtm = 0.25f * startXBtm + 0.5f * controlXBtm + 0.25f * endXBtm;
            float bezierPeakYBtm = 0.25f * startYBtm + 0.5f * controlYBtm + 0.25f * endYBtm;
            float bezierPeakXLeft = 0.25f * startXLeft + 0.5f * controlXLeft + 0.25f * endXLeft;
            float bezierPeakYLeft = 0.25f * startYLeft + 0.5f * controlYLeft + 0.25f * endYLeft;

            if (startYLeft <= 0 ){
                startYLeft = 0;
            }

            if (startXBtm <= 0){
                startXBtm = 0;
            }

            float partOfShortLength = CURVATURE * sizeShort;
            if (btmX >= -mValueAdded && btmX <= partOfShortLength - mValueAdded){
                float f = btmX / partOfShortLength;
                float t = 0.5f * f;

                float bezierPeakTemp = 1 - t;
                float bezierPeakTemp1 = bezierPeakTemp * bezierPeakTemp;
                float bezierPeakTemp2 = 2 * t * bezierPeakTemp;
                float bezierPeakTemp3 = t * t;

                bezierPeakXBtm = bezierPeakTemp1 * startXBtm + bezierPeakTemp2 * controlXBtm + bezierPeakTemp3 * endXBtm;
                bezierPeakYBtm = bezierPeakTemp1 * startYBtm + bezierPeakTemp2 * controlYBtm + bezierPeakTemp3 * endYBtm;
            }

            float partOfLongLength = CURVATURE * sizeLong;
            if (leftY >= -mValueAdded && leftY <= partOfLongLength - mValueAdded){
                float f = leftY / partOfLongLength;
                float t = 0.5f * f;

                float bezierPeakTemp = 1- t;
                float bezierPeakTemp1 = bezierPeakTemp * bezierPeakTemp;
                float bezierPeakTemp2 = 2 * t * bezierPeakTemp;
                float bezierPeakTemp3 = t * t;

                bezierPeakXLeft = bezierPeakTemp1 * startXLeft + bezierPeakTemp2 * controlXLeft + bezierPeakTemp3 * endXLeft;
                bezierPeakYLeft = bezierPeakTemp1 * startYLeft + bezierPeakTemp2 * controlYLeft + bezierPeakTemp3 * endYLeft;
            }

            mPathTrap.moveTo(startXBtm, startYBtm);
            mPathTrap.lineTo(startXLeft, startYLeft);
            mPathTrap.lineTo(bezierPeakXLeft, bezierPeakYLeft);
            mPathTrap.lineTo(bezierPeakXBtm, bezierPeakYBtm);
            mPathTrap.close();

            mPathSemicircleBtm.moveTo(startXBtm, startYBtm);
            mPathSemicircleBtm.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
            mPathSemicircleBtm.close();

            mPathSemicircleLeft.moveTo(endXLeft, endYLeft);
            mPathSemicircleLeft.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);
            mPathSemicircleLeft.close();

            mPath.moveTo(startXBtm, startYBtm);
            mPath.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
            mPath.lineTo(touchX, touchY);
            mPath.lineTo(endXLeft, endYLeft);
            mPath.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);


            mPathFoldAndNext.moveTo(startXBtm, startYBtm);
            mPathFoldAndNext.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
            mPathFoldAndNext.lineTo(touchX, touchY);
            mPathFoldAndNext.lineTo(endXLeft, endYLeft);
            mPathFoldAndNext.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);
            mPathFoldAndNext.lineTo(viewWidth, viewHeight);
            mPathFoldAndNext.close();

            mPathShadowDiagonally.moveTo(bezierPeakXBtm, bezierPeakYBtm);
            mPathShadowDiagonally.moveTo(bezierPeakXLeft, bezierPeakYLeft);

            x1 = bezierPeakXBtm;
            y1 = bezierPeakYBtm;
            x2 = bezierPeakXLeft;
            y2 = bezierPeakYLeft;

            mPathShadowDiagonally.moveTo(x1,y1);
            mPathShadowDiagonally.lineTo(x2,y2);
            mPathShadowDiagonally.lineTo(viewWidth,leftY);
            mPathShadowDiagonally.lineTo(btmX,viewHeight);
            mPathShadowDiagonally.close();

            hypotenuseDegree = (touchX - controlXBtm)/(touchY - controlYBtm);

        }

//        mPath.computeBounds(mRectFFold, false);
//        mPathFoldAndNext.computeBounds(mRectFNextAndFold, false);
//        mPathTrap.computeBounds(mRectFTrap, false);


        this.touchX = touchX;
        this.touchY = touchY;
        onPageTurnListener.onAnimationInvalidate();
    }

    /**
     * 计算短边的有效区域
     */
    private void computeShortSizeRegion() {
        // 短边圆形路径对象
        Path pathShortSize = new Path();

        // 用来装载Path边界值的RectF对象
        RectF rectShortSize = new RectF();

        // 添加圆形到Path
        pathShortSize.addCircle(0, viewHeight, viewWidth, Path.Direction.CCW);

        // 计算边界
        pathShortSize.computeBounds(rectShortSize, true);

        // 将Path转化为Region
        mRegionShortSize.setPath(pathShortSize, new Region((int) rectShortSize.left, (int) rectShortSize.top, (int) rectShortSize.right, (int) rectShortSize.bottom));
    }


    @Override
    public boolean draw(Canvas canvas) {
        if (isAnimationEnd()){//动画结束
            onPageTurnListener.onPageTurnAnimationEnd(canvas, getPageTurnDirection(), isPageTurn);
            setAnimationEnd(false);
            return true;
        }

        if (isVerticalPage){

            canvas.save();
            canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
                PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
            }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
                PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getPreviousBitmap(), null);
            }
            drawVerticalShadow(canvas);
            canvas.restore();

            canvas.save();
            canvas.clipPath(mPath);
            canvas.translate(touchX, 0);
            canvas.scale(-1,1);
            canvas.translate(-viewWidth, 0);
            if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
                PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
            }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
                PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getPreviousBitmap(), null);
            }
            canvas.restore();

            canvas.save();
            canvas.clipPath(mPath);
            canvas.clipPath(mPathFoldAndNext, Region.Op.XOR);
            if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
                PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getNextBitmap(), null);
            }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
                PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
            }
            canvas.restore();
            drawVerticalShadowInRight(canvas);
            return false;
        }

        canvas.save();
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        canvas.clipPath(mPathSemicircleBtm, Region.Op.UNION);
        canvas.clipPath(mPathSemicircleLeft, Region.Op.UNION);

        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getPreviousBitmap(), null);
        }
        drawHypotenuseShadow(canvas);
        canvas.restore();

        canvas.save();
        canvas.clipPath(mPath);
        canvas.clipPath(mPathTrap, Region.Op.UNION);
        canvas.clipPath(mPathSemicircleBtm, Region.Op.DIFFERENCE);
        canvas.clipPath(mPathSemicircleLeft, Region.Op.DIFFERENCE);
        PageManager.getInstance().drawCanvasBg(canvas,null);
        canvas.translate(touchX, touchY);
        if (mRatio == Ratio.SHORT){
            canvas.rotate(90 - mDegree);
            canvas.translate(0, - viewHeight);
            canvas.scale(-1, 1);
            canvas.translate(-viewWidth, 0);
        }else {
            canvas.rotate(-(90-mDegree));
            canvas.translate(-viewWidth, 0);
            canvas.scale(1,-1);
            canvas.translate(0, -viewHeight);
        }

        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getPreviousBitmap(), null);
        }
//        canvas.drawColor(Color.parseColor("#ff0000"));

        canvas.restore();

        canvas.save();
        canvas.clipPath(mPath);
        canvas.clipPath(mPathTrap, Region.Op.UNION);
        canvas.clipPath(mPathSemicircleBtm, Region.Op.DIFFERENCE);
        canvas.clipPath(mPathSemicircleLeft, Region.Op.DIFFERENCE);

        canvas.clipPath(mPathFoldAndNext, Region.Op.REVERSE_DIFFERENCE);
        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getNextBitmap(), null);
        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
        }

//        canvas.drawColor(Color.parseColor("#ff0000"));
        canvas.restore();

        canvas.save();
//        canvas.drawPath(mPath , contentPaint);
//        canvas.drawLine(x1, y1, x2, y2, contentPaint);

        canvas.restore();

        drawDiagonallyShadow(canvas);

        return false;
    }

    /**
     * 绘画翻页的直角两条线的上部阴影
     * @param canvas 画步
     */
    private void drawHypotenuseShadow(Canvas canvas){
        int right = (int)(viewHeight * 1f);
        Shadow shadow = getShadow(isDayMode ? 0 : 1);
        hypotenuseDegree = -hypotenuseDegree;
        float rotate = (float) (Math.atan(hypotenuseDegree) / Math.PI * 180);

        canvas.translate(touchX, touchY);
        canvas.rotate(rotate);
        canvas.translate(-18, -18);

        int edgeShadowMaxLen = 18;
        int maxLen = edgeShadowMaxLen + Math.max(viewWidth, viewHeight);
        mPathHypotenuse.reset();
        mPathHypotenuse.lineTo(0, 0);
        mPathHypotenuse.lineTo(maxLen, 0);
        mPathHypotenuse.lineTo(maxLen, edgeShadowMaxLen);
        mPathHypotenuse.lineTo(edgeShadowMaxLen, edgeShadowMaxLen);
        mPathHypotenuse.close();
        if (-hypotenuseDegree > 0) {
            if (rotate < -5) {
                canvas.save();
                canvas.clipPath(mPathHypotenuse);
                shadow.edgeShadow.setBounds(0, 0, right, edgeShadowMaxLen);
                shadow.edgeShadow.draw(canvas);
                canvas.restore();
            }

            canvas.rotate(90);
            canvas.scale(1, -1);
            canvas.clipPath(mPathHypotenuse);
            shadow.edgeShadow.setBounds(0, 0, edgeShadowMaxLen + viewHeight, edgeShadowMaxLen);
            shadow.edgeShadow.draw(canvas);
        } else {
            if (rotate > -85) {
                canvas.save();
                canvas.rotate(90);
                canvas.scale(1, -1);
                canvas.clipPath(mPathHypotenuse);
                shadow.edgeShadow.setBounds(0, 0, right, edgeShadowMaxLen);
                shadow.edgeShadow.draw(canvas);
                canvas.restore();
            }

            canvas.clipPath(mPathHypotenuse);
            shadow.edgeShadow.setBounds(0, 0, edgeShadowMaxLen + viewHeight, edgeShadowMaxLen);
            shadow.edgeShadow.draw(canvas);

        }
    }

    /**
     * 绘画翻页的折线两边的阴影
     * @param canvas 画步
     */
    private void drawDiagonallyShadow(Canvas canvas) {

        int shadowWidth;
        int shadowHeight;

        canvas.save();
        canvas.clipPath(mPathFoldAndNext);

        float hypotenuseDegree = (x1 - x2) / (y1 - y2);
        hypotenuseDegree = -hypotenuseDegree;

        float degree = (float)(Math.atan(hypotenuseDegree) / Math.PI * 180);
        shadowWidth = 90;

        float conditionA = y2 - y1;
        float conditionB = x1 - x2;
        float conditionC = x2 * y1 - x1 * y2;

        float calcStartX = 0;
        float calcStartY = conditionC / (-conditionB);
        float calcEndX =  conditionC / (-conditionA);
        float calcEndY = 0;

        shadowHeight = (int) Math.hypot(calcStartX - calcEndX, calcStartY - calcEndY);

        canvas.translate(calcEndX, calcEndY);
        canvas.rotate(degree);


        Shadow shadow = getShadow(isDayMode ? 0 : 1);
        shadow.edgeFoldShadow.setBounds(-shadowWidth, 0, shadowWidth, shadowHeight);
        shadow.edgeFoldShadow.draw(canvas);

        canvas.restore();
    }

    /**
     * 绘画垂直翻页的左边直线的阴影
     * @param canvas 画步
     */
    private void drawVerticalShadow(Canvas canvas){
        Shadow shadow = getShadow(isDayMode ? 0 : 1);

        int edgeShadowMaxLen = 18;
        canvas.translate(touchX - edgeShadowMaxLen, 0);

        shadow.verticalShadow.setBounds(0, 0 , edgeShadowMaxLen, viewHeight);
        shadow.verticalShadow.draw(canvas);

    }


    /**
     * 绘画垂直翻页的右边的阴影
     * @param canvas 画步
     */
    private void drawVerticalShadowInRight(Canvas canvas) {

        canvas.save();
        int shadowWidth = 90;
        int shadowHeight = viewHeight;
        if (verticalEndX - touchX < shadowWidth){
            shadowWidth = (int)(verticalEndX - touchX);
        }
        canvas.translate(verticalEndX, 0);
        Shadow shadow = getShadow(isDayMode ? 0 : 1);
        shadow.edgeFoldShadow.setBounds(-shadowWidth, 0, shadowWidth, shadowHeight);
        shadow.edgeFoldShadow.draw(canvas);

        canvas.restore();
    }


    private Shadow getShadow(int mode) {
        if (shadow == null) {
            shadow = new Shadow(mode);
        }
        return shadow;
    }

    private static class Shadow {

        // 边上的阴影
        private static final int[][] EDGE_SHADOW = {{0x00454545, 0x80454545}, {0x00151515, 0x80151515}};
        // 折边直线上的阴影
        private static final int[][] EDGEFOLD_SHADOW_COLORS = {{0x00454545, 0x80454545, 0x00454545}, {0x00151515, 0x80151515, 0x00151515}};
        //三角区
        private static final int[][] CORNER_SHADOW_COLORS = {{0x80454545, 0x00454545}, {0x80151515, 0x00151515}};

        private GradientDrawable verticalShadow;
        private GradientDrawable edgeShadow;//边上的阴影
        private GradientDrawable edgeFoldShadow;//折边阴影
        private GradientDrawable cornerShadow;//三角区阴影

        private Shadow(int mode) {

            verticalShadow = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, EDGE_SHADOW[mode]);
            verticalShadow.setGradientType(GradientDrawable.LINEAR_GRADIENT);

            edgeShadow = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, EDGE_SHADOW[mode]);
            edgeShadow.setGradientType(GradientDrawable.LINEAR_GRADIENT);

            edgeFoldShadow = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, EDGEFOLD_SHADOW_COLORS[mode]);
            edgeFoldShadow.setGradientType(GradientDrawable.LINEAR_GRADIENT);

            cornerShadow = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, CORNER_SHADOW_COLORS[mode]);
            cornerShadow.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        }
    }
}
