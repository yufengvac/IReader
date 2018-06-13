package com.yufeng.ireader.reader.viewimpl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.MotionEvent;

import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.viewinterface.PageTurn;
import com.yufeng.ireader.utils.DisplayConstant;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 仿真翻页
 */

public class SimulationPageTurn extends PageTurn{
    private float touchX, touchY ;
    private boolean hasDirection = false;
    private float mValueAdded;// 精度附减值

    private int viewHeight;
    private int viewWidth;

    private static final float CURVATURE = 1 / 4F;// 曲度值
    private static final float VALUE_ADDED = 1 / 400F;// 精度附加值占比

    private Path mPath ;
    private Path mPathTrap ;
    private Path mPathSemicircleBtm ;
    private Path mPathFoldAndNext ;
    private Path mPathSemicircleLeft;

    private Region mRegionShortSize;// 短边的有效区域
    private Region mRegionCurrent;// 当前页区域，其实就是控件的大小


    private Region mRegionNext;// 当前页区域，其实就是控件的大小
    private Region mRegionFold;// 当前页区域，其实就是控件的大小
    private Region mRegionSemicircle;// 两月半圆区域

    private Paint contentPaint;

    public SimulationPageTurn(){
        viewWidth = DisplayConstant.DISPLAY_WIDTH;
        viewHeight = DisplayConstant.DISPLAY_HEIGHT;

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

        initPaint();
    }

    private void initPaint(){
        contentPaint = new Paint();
        contentPaint.setAntiAlias(true);
        contentPaint.setColor(Color.parseColor("#000000"));
        contentPaint.setTextSize(16f);
        contentPaint.setStyle(Paint.Style.FILL);
        contentPaint.setStrokeWidth(10f);
    }
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
                calcPoint1(event.getX(), event.getY());
            }

        }else if (event.getAction() == MotionEvent.ACTION_DOWN){
            return true;
        }
        return false;
    }

    private void calcPoint1(float touchX, float touchY){
        mPath.reset();
        float mk = viewWidth - touchX;
        float ml = viewHeight - touchY;

        float temp = (float)(Math.pow(ml, 2) + Math.pow(mk, 2));

        float sizeShort = temp / (2f * mk);
        float sizeLong = temp / (2f * ml);

        mPath.moveTo(touchX, touchY);
        mPath.lineTo(viewWidth, viewHeight- sizeLong);
        mPath.lineTo(viewWidth- sizeShort, viewHeight);
        mPath.close();

        this.touchX = touchX;
        this.touchY = touchY;
        onPageTurnListener.onAnimationInvalidate();
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
//        if (getPageTurnDirection() == PageTurnDirection.DIRECTION_NEXT){
//            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getNextBitmap(), null);
//        }else if (getPageTurnDirection() == PageTurnDirection.DIRECTION_PREVIOUS){
//            PageManager.getInstance().drawCanvasBitmap(canvas, onPageTurnListener.getCurrentBitmap(), null);
//        }
        canvas.drawPath(mPath, contentPaint);
//        canvas.restore();

        return false;
    }
}
