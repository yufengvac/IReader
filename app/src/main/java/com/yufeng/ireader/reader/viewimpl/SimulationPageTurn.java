package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import android.view.MotionEvent;

import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.viewinterface.PageTurn;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.ReadPreferHelper;

import javax.xml.datatype.Duration;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 仿真翻页
 */

public class SimulationPageTurn extends PageTurn{
    private float touchX, touchY ;
    private float startX, startY;
    private boolean hasDirection = false;
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
    private Ratio mRatio;// 定义当前折叠边长
    private float mDegree;

    /**
     * 枚举类定义长边短边
     */
    private enum Ratio {
        LONG, SHORT
    }

    private Region mRegionShortSize;// 短边的有效区域
    private Region mRegionCurrent;// 当前页区域，其实就是控件的大小


    private Region mRegionNext;// 当前页区域，其实就是控件的大小
    private Region mRegionFold;// 当前页区域，其实就是控件的大小
    private Region mRegionSemicircle;// 两月半圆区域

    private RectF foldRectf;
    private RectF nextRectf;
    private RectF currentRectf;

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

        /*
		 * 实例化区域对象
		 */
        mRegionShortSize = new Region();
        mRegionCurrent = new Region();
        mRegionSemicircle = new Region();

        mValueAdded = viewHeight * VALUE_ADDED;
        mBuffArea = viewHeight * BUFF_AREA;

        currentRectf = new RectF(0, 0 , viewWidth, viewHeight);
        initPaint();
        computeShortSizeRegion();
    }

    private void initPaint(){
        contentPaint = new Paint();
        contentPaint.setAntiAlias(true);
        contentPaint.setColor(Color.parseColor("#000000"));
        contentPaint.setTextSize(5f);
        contentPaint.setStyle(Paint.Style.STROKE);
        contentPaint.setStrokeWidth(4f);
    }
    @Override
    public void turnNext() {
        startAnimation(viewWidth, 0, ANIMATION_DURATION);
    }

    @Override
    public void turnPrevious() {

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
        touchX -= 10;
        touchY = startY + ((touchX - startX) * (viewHeight - startY)) / (viewWidth - startX);
        calcPoint1(touchX, touchY);
        onPageTurnListener.onAnimationInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            touchX = event.getX();
            touchY = event.getY();
            hasDirection = false;
            onTouchEvent = true;
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
            }

            if (hasDirection){
                if (!mRegionShortSize.contains((int)event.getX(), (int)event.getY())){
                    float touchY = (float)(Math.sqrt((Math.pow(viewWidth, 2) - Math.pow(event.getX(), 2))) - viewHeight);
                    touchY = Math.abs(touchY) + mValueAdded;
                    float area = viewHeight - mBuffArea;
                    if (touchY >= area){
                        touchY = area;
                    }
                    calcPoint1(event.getX(), touchY);
                }else {
                    calcPoint1(event.getX(), event.getY());
                }
            }

        }else if (event.getAction() == MotionEvent.ACTION_UP){
            onTouchEvent = false;
            startX = event.getX();
            startY = event.getY();
            if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
                startAnimation(event.getX(),0, ANIMATION_DURATION);
            }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){

            }else if (event.getX() == touchX){
                return false;
            }

        }
        return true;
    }

    private void calcPoint1(float touchX, float touchY){
        mPath.reset();
        mPathFoldAndNext.reset();

        viewHeight = DisplayConstant.DISPLAY_HEIGHT_SIMULATION;

        float mk = viewWidth - touchX;
        float ml = viewHeight - touchY;

        float temp = (float)(Math.pow(ml, 2) + Math.pow(mk, 2));

        float sizeShort = temp / (2f * mk);
        float sizeLong = temp / (2f * ml);

        mPath.moveTo(touchX, touchY);
        mPathFoldAndNext.moveTo(touchX, touchY);

        if (sizeShort < sizeLong){
            mRatio = Ratio.SHORT;
            float sin = (mk - sizeShort) / sizeShort;
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

            mPath.lineTo(topX1, 0);
            mPath.lineTo(topX2, 0);
            mPath.lineTo(btmX2, viewHeight);
            mPath.close();

            mPathFoldAndNext.lineTo(topX1, 0);
            mPathFoldAndNext.lineTo(viewWidth, 0);
            mPathFoldAndNext.lineTo(viewWidth, viewHeight);
            mPathFoldAndNext.lineTo(btmX2, viewHeight);
            mPathFoldAndNext.close();
        }else {

            float leftY = viewHeight - sizeLong;
            float btmX = viewWidth - sizeShort;

            mPath.lineTo(viewWidth, leftY);
            mPath.lineTo(btmX, viewHeight);
            mPath.close();

            mPathFoldAndNext.lineTo(viewWidth, leftY);
            mPathFoldAndNext.lineTo(viewWidth, viewHeight);
            mPathFoldAndNext.lineTo(btmX, viewHeight);
            mPathFoldAndNext.close();
        }

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

    private void calcPoint(float touchX, float touchY){

        mPath.reset();
        mPathFoldAndNext.reset();
        mPathTrap.reset();
        mPathSemicircleBtm.reset();
        mPathSemicircleLeft.reset();

        //计算sizeLong
        float mK = viewWidth - touchX;
        float mL = viewHeight - touchY;
        float temp = (float)(Math.pow(mL, 2) + Math.pow(mK, 2));

        float sizeShort = temp / (2f * mK);
        float sizeLong = temp / (2f * mL);

        if (sizeLong > viewHeight){//右侧滑到最顶端，右侧不显示曲线
            calcPointWithoutRight(touchX, touchY,mK, mL, sizeShort, sizeLong);
        }else {
            calcPointWithRight(touchX, touchY, mK, mL, sizeShort, sizeLong);
        }

        //根据Path生成的折叠区域
        mRegionFold = computeRegion(mPath);

        //替补区域
        Region regionTrap = computeRegion(mPathTrap);

        //令折叠区域与替补区域相加
        mRegionFold.op(regionTrap, Region.Op.UNION);

        //从相加后的区域中剔除月半圆的区域或得最终折叠区域
        mRegionFold.op(mRegionSemicircle, Region.Op.DIFFERENCE);

        //计算下一页区域
        mRegionNext = computeRegion(mPathFoldAndNext);
        mRegionNext.op(mRegionFold, Region.Op.DIFFERENCE);


        this.touchX = touchX;
        this.touchY = touchY;
        onPageTurnListener.onAnimationInvalidate();
    }

    private void calcPointWithoutRight(float touchX, float touchY,float mk,float mL, float sizeShort, float sizeLong){

        float an = sizeLong - viewHeight;

        float largerTrianShortSize  = an / ( sizeLong - (viewHeight - touchY)) * (viewWidth - touchX);
        float smallTrianShortSize = an / sizeLong * sizeShort;

        float topX1 = viewWidth - viewWidth - largerTrianShortSize;
        float topX2 = viewWidth - smallTrianShortSize;
        float btmX2 = viewWidth - sizeShort;

        //计算曲线起点
        float startXBtm = btmX2 - CURVATURE * sizeShort;
        float startYBtm = viewHeight;

        //计算曲线终点
        float endXBtm = touchX + (1 - CURVATURE) * (mk - sizeShort);
        float endYBtm = touchY + (1 - CURVATURE) * mL;

        //计算曲线控制点
        float controlXBtm = btmX2;
        float controlYBtm = viewHeight;

        //计算曲线顶点
        float bezierPeakXBtm = 0.25f * startXBtm + 0.5f * controlXBtm + 0.25f * endXBtm;
        float bezierPeakYBtm = 0.25f * startYBtm + 0.5f * controlYBtm + 0.25f * endYBtm;

        //生成带曲线的四边形路径
        mPath.moveTo(startXBtm, startYBtm);
        mPath.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
        mPath.lineTo(touchX, touchY);
        mPath.lineTo(topX1, 0);
        mPath.lineTo(topX2, 0);

        //替补区域Path
        mPathTrap.moveTo(startXBtm, startYBtm);
        mPathTrap.lineTo(topX2, 0);
        mPathTrap.lineTo(bezierPeakXBtm, bezierPeakYBtm);
        mPathTrap.close();

        //底部月半圆path
        mPathSemicircleBtm.moveTo(startXBtm, startYBtm);
        mPathSemicircleBtm.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
        mPathSemicircleBtm.close();

        //生成包含折叠和下一页的路径
        mPathFoldAndNext.moveTo(startXBtm, startYBtm);
        mPathFoldAndNext.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
        mPathFoldAndNext.lineTo(touchX, touchY);
        mPathFoldAndNext.lineTo(topX1, 0);
        mPathFoldAndNext.lineTo(viewWidth, 0);
        mPathFoldAndNext.lineTo(viewWidth, viewHeight);
        mPathFoldAndNext.close();

        // 计算月半圆区域
        mRegionSemicircle = computeRegion(mPathSemicircleBtm);

    }

    private void calcPointWithRight(float touchX, float touchY, float mk, float mL, float sizeShort, float sizeLong) {
            /*
             * 计算参数
			 */
        float leftY = viewHeight - sizeLong;
        float btmX = viewWidth - sizeShort;

        // 计算曲线起点
        float startXBtm = btmX - CURVATURE * sizeShort;
        float startYBtm = viewHeight;
        float startXLeft = viewWidth;
        float startYLeft = leftY - CURVATURE * sizeLong;

        // 计算曲线终点
        float endXBtm = touchX + (1 - CURVATURE) * (mk - sizeShort);
        float endYBtm = touchY + (1 - CURVATURE) * mL;
        float endXLeft = touchX + (1 - CURVATURE) * mk;
        float endYLeft = touchY - (1 - CURVATURE) * (sizeLong - mL);

        // 计算曲线控制点
        float controlXBtm = btmX;
        float controlYBtm = viewHeight;
        float controlXLeft = viewWidth;
        float controlYLeft = leftY;

        // 计算曲线顶点
        float bezierPeakXBtm = 0.25F * startXBtm + 0.5F * controlXBtm + 0.25F * endXBtm;
        float bezierPeakYBtm = 0.25F * startYBtm + 0.5F * controlYBtm + 0.25F * endYBtm;
        float bezierPeakXLeft = 0.25F * startXLeft + 0.5F * controlXLeft + 0.25F * endXLeft;
        float bezierPeakYLeft = 0.25F * startYLeft + 0.5F * controlYLeft + 0.25F * endYLeft;

			/*
             * 限制右侧曲线起点
			 */
        if (startYLeft <= 0) {
            startYLeft = 0;
        }

			/*
             * 限制底部左侧曲线起点
			 */
        if (startXBtm <= 0) {
            startXBtm = 0;
        }

			/*
			 * 根据底部左侧限制点重新计算贝塞尔曲线顶点坐标
			 */
        float partOfShortLength = CURVATURE * sizeShort;
        if (btmX >= -mValueAdded && btmX <= partOfShortLength - mValueAdded) {
            float f = btmX / partOfShortLength;
            float t = 0.5F * f;

            float bezierPeakTemp = 1 - t;
            float bezierPeakTemp1 = bezierPeakTemp * bezierPeakTemp;
            float bezierPeakTemp2 = 2 * t * bezierPeakTemp;
            float bezierPeakTemp3 = t * t;

            bezierPeakXBtm = bezierPeakTemp1 * startXBtm + bezierPeakTemp2 * controlXBtm + bezierPeakTemp3 * endXBtm;
            bezierPeakYBtm = bezierPeakTemp1 * startYBtm + bezierPeakTemp2 * controlYBtm + bezierPeakTemp3 * endYBtm;
        }

			/*
			 * 根据右侧限制点重新计算贝塞尔曲线顶点坐标
			 */
        float partOfLongLength = CURVATURE * sizeLong;
        if (leftY >= -mValueAdded && leftY <= partOfLongLength - mValueAdded) {
            float f = leftY / partOfLongLength;
            float t = 0.5F * f;

            float bezierPeakTemp = 1 - t;
            float bezierPeakTemp1 = bezierPeakTemp * bezierPeakTemp;
            float bezierPeakTemp2 = 2 * t * bezierPeakTemp;
            float bezierPeakTemp3 = t * t;

            bezierPeakXLeft = bezierPeakTemp1 * startXLeft + bezierPeakTemp2 * controlXLeft + bezierPeakTemp3 * endXLeft;
            bezierPeakYLeft = bezierPeakTemp1 * startYLeft + bezierPeakTemp2 * controlYLeft + bezierPeakTemp3 * endYLeft;
        }

			/*
			 * 替补区域Path
			 */
        mPathTrap.moveTo(startXBtm, startYBtm);
        mPathTrap.lineTo(startXLeft, startYLeft);
        mPathTrap.lineTo(bezierPeakXLeft, bezierPeakYLeft);
        mPathTrap.lineTo(bezierPeakXBtm, bezierPeakYBtm);
        mPathTrap.close();

			/*
			 * 生成带曲线的三角形路径
			 */
        mPath.moveTo(startXBtm, startYBtm);
        mPath.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
        mPath.lineTo(touchX, touchY);
        mPath.lineTo(endXLeft, endYLeft);
        mPath.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);

			/*
			 * 生成底部月半圆的Path
			 */
        mPathSemicircleBtm.moveTo(startXBtm, startYBtm);
        mPathSemicircleBtm.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
        mPathSemicircleBtm.close();

			/*
			 * 生成右侧月半圆的Path
			 */
        mPathSemicircleLeft.moveTo(endXLeft, endYLeft);
        mPathSemicircleLeft.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);
        mPathSemicircleLeft.close();

			/*
			 * 生成包含折叠和下一页的路径
			 */
        mPathFoldAndNext.moveTo(startXBtm, startYBtm);
        mPathFoldAndNext.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
        mPathFoldAndNext.lineTo(touchX, touchY);
        mPathFoldAndNext.lineTo(endXLeft, endYLeft);
        mPathFoldAndNext.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);
        mPathFoldAndNext.lineTo(viewWidth, viewHeight);
        mPathFoldAndNext.close();

			/*
			 * 计算底部和右侧两月半圆区域
			 */
        Region regionSemicircleBtm = computeRegion(mPathSemicircleBtm);
        Region regionSemicircleLeft = computeRegion(mPathSemicircleLeft);

        // 合并两月半圆区域
        mRegionSemicircle.op(regionSemicircleBtm, regionSemicircleLeft, Region.Op.UNION);
    }

    /**
     * 通过路径计算区域
     *
     * @param path
     *            路径对象
     * @return 路径的Region
     */
    private Region computeRegion(Path path) {
        Region region = new Region();
        RectF f = new RectF();
        path.computeBounds(f, true);
        region.setPath(path, new Region((int) f.left, (int) f.top, (int) f.right, (int) f.bottom));
        return region;
    }

    private RectF computeRectF(Path path){
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        return rectF;
    }


    @Override
    public boolean draw(Canvas canvas) {
        if (isAnimationEnd()){//动画结束
            onPageTurnListener.onPageTurnAnimationEnd(canvas, getPageTurnDirection(), true);
            setAnimationEnd(false);
            return true;
        }

//        canvas.save();
//        PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
//        canvas.drawPath(mPath, contentPaint);
//        canvas.restore();

//        foldRectf = computeRectF(mPath);
//        nextRectf = computeRectF(mPathFoldAndNext);

        canvas.save();
        canvas.clipRect(currentRectf);
        canvas.clipPath(mPathFoldAndNext, Region.Op.DIFFERENCE);
        PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
        canvas.restore();
//
        canvas.save();
        canvas.clipPath(mPath);
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
        PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
        canvas.restore();
//
        canvas.save();
        canvas.clipPath(mPathFoldAndNext);
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getNextBitmap(), null);
        canvas.restore();


        return false;
    }
}
