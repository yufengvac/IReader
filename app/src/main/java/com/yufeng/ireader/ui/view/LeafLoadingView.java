package com.yufeng.ireader.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import com.yufeng.ireader.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yufeng on 2018/5/9-0009.
 *
 */

public class LeafLoadingView extends View{
    private static final String TAG = "LeafLoadingView";

    // 淡白色
    private static final int WHITE_COLOR = 0xfffde399;
    // 橙色
    private static final int ORANGE_COLOR = 0xffffa800;
    // 中等振幅大小的默认值
    private static final int MIDDLE_AMPLITUDE = 13;
    // 不同类型之间的振幅差距的默认值
    private static final int AMPLITUDE_DISPARITY = 5;
    // 中等振幅大小
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    // 振幅差
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;

    // 总进度
    private static final int TOTAL_PROGRESS = 100;
    // 叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_PERIOD = 3000;
    // 叶子旋转一周需要的时间
    private static final long LEAF_ROTATE_PERIOD = 2000;
    // 叶子飘动一个周期所花的时间
    private long mLeafFloatPeriod = LEAF_FLOAT_PERIOD;
    // 叶子旋转一周需要的时间
    private long mLeafRotatePeriod = LEAF_ROTATE_PERIOD;
    // 用于控制随机增加的时间不抱团
    private int mAddTime;

    // 用于控制绘制的进度条距离左／上／下的距离(dp)
    private static final int LEFT_MARGIN = 9;
    // 用于控制绘制的进度条距离右的距离(dp)
    private static final int RIGHT_MARGIN = 25;
    //单位(px)
    private int mLeftMargin, mRightMargin;

    private Bitmap mLeafBitmap;
    private int mLeafWidth, mLeafHeight;

    private Bitmap mOuterBitmap;
    private Rect mOuterSrcRect, mOuterDestRect;
    private int mOuterWidth, mOuterHeight;

    private Bitmap mFanBitmap;
    private int mFanWidth, mFanHeight;
    private int mFanRotateAngle;
    private int mFanRotateSpeed = 3;

    private int mTotalWidth, mTotalHeight;

    private Paint mBitmapPaint, mWhitePaint, mOrangePaint;
    private RectF mWhiteRectF, mOrangeRectF, mArcRectF;
    // 当前进度
    private int mProgress;
    // 所绘制的进度条部分的宽度
    private int mProgressWidth;
    // 当前所在的绘制的进度条的位置
    private int mCurrentProgressPosition;
    // 弧形的半径
    private int mArcRadius;

    // arc的右上角的x坐标，也是矩形x坐标的起始点
    private int mArcRightLocation;
    // 用于产生叶子信息
    private LeafFactory mLeafFactory;
    // 产生出的叶子信息
    private List<Leaf> mLeafInfos;
    private Resources mResources;


    public LeafLoadingView(Context context) {
        this(context, null);
    }

    public LeafLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mResources = context.getResources();
        mLeftMargin = dip2Px(LEFT_MARGIN);
        mRightMargin = dip2Px(RIGHT_MARGIN);

        initBitmap();
        initPaint();
        mLeafFactory = new LeafFactory();
        mLeafInfos = mLeafFactory.generateLeafs();
    }

    private void initBitmap() {
        mLeafBitmap = BitmapFactory.decodeResource(mResources, R.drawable.leaf);
        mLeafWidth = mLeafBitmap.getWidth();
        mLeafHeight = mLeafBitmap.getHeight();

        mOuterBitmap = BitmapFactory.decodeResource(mResources, R.drawable.leaf_kuang);
        mOuterWidth = mOuterBitmap.getWidth();
        mOuterHeight = mOuterBitmap.getHeight();

        mFanBitmap = BitmapFactory.decodeResource(mResources, R.drawable.fan);
        mFanWidth = mFanBitmap.getWidth();
        mFanHeight = mFanBitmap.getHeight();
    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(WHITE_COLOR);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(ORANGE_COLOR);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = dip2Px(302);
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = dip2Px(61);
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        mProgressWidth = mTotalWidth - mLeftMargin - mRightMargin;
        mArcRadius = (mTotalHeight - 2 * mLeftMargin) / 2;

        mOuterSrcRect = new Rect(0, 0, mOuterWidth, mOuterHeight);
        mOuterDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);

        mWhiteRectF = new RectF(mLeftMargin + mCurrentProgressPosition, mLeftMargin, mTotalWidth - mRightMargin, mTotalHeight - mLeftMargin);
        mOrangeRectF = new RectF(mLeftMargin + mArcRadius, mLeftMargin, mCurrentProgressPosition, mTotalHeight - mLeftMargin);

        mArcRectF = new RectF(mLeftMargin, mLeftMargin, mLeftMargin + 2 * mArcRadius, mTotalHeight - mLeftMargin);
        mArcRightLocation = mLeftMargin + mArcRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制进度条和叶子
        drawProgressAndLeafs(canvas);

        //绘制边框
        canvas.drawBitmap(mOuterBitmap, mOuterSrcRect, mOuterDestRect, mBitmapPaint);

        //绘制风扇
        drawFan(canvas);

        //不断刷新重绘
        postInvalidateDelayed(100);
    }

    private void drawFan(Canvas canvas) {
        Matrix matrix = new Matrix();
        canvas.save();
        matrix.postTranslate(247.0f / 302.0f * mTotalWidth, (mOuterHeight - mFanHeight) / 2);
        mFanRotateAngle = (mFanRotateAngle - mFanRotateSpeed) % 360;
        matrix.postRotate(mFanRotateAngle, 247.0f / 302.0f * mTotalWidth + mFanWidth / 2, mOuterHeight / 2);
        canvas.drawBitmap(mFanBitmap, matrix, mBitmapPaint);
        canvas.restore();
    }

    private void drawProgressAndLeafs(Canvas canvas) {
        if (mProgress >= TOTAL_PROGRESS) {
            mProgress = 0;
        }

        // mProgressWidth为进度条的宽度，根据当前进度算出进度条的位置
        mCurrentProgressPosition = mProgressWidth * mProgress / TOTAL_PROGRESS;
        //当前进度条位置在弧形内
        if (mCurrentProgressPosition < mArcRadius) {
            // 1.绘制White ARC
            // 2.绘制White RECT
            // 3.绘制Orange ARC

            // 1.绘制White ARC
            canvas.drawArc(mArcRectF, 90, 180, false, mWhitePaint);

            // 2.绘制White RECT
            mWhiteRectF.left = mArcRightLocation;
            canvas.drawRect(mWhiteRectF, mWhitePaint);

            // 绘制叶子
            drawLeafs(canvas);

            // 3.绘制Orange ARC
            // 单边角度
            int angle = (int) Math.toDegrees(Math.acos((mArcRadius - mCurrentProgressPosition) / (float) mArcRadius));
            // 起始的位置
            int startAngle = 180 - angle;
            // 扫过的角度
            int sweepAngle = 2 * angle;
            canvas.drawArc(mArcRectF, startAngle, sweepAngle, false, mOrangePaint);
        } else {
            // 1.绘制White RECT
            // 2.绘制Orange ARC
            // 3.绘制Orange RECT
            // 按这个层级进行绘制能让叶子感觉是融入棕色进度条中

            // 1.绘制White RECT
            mWhiteRectF.left = mCurrentProgressPosition;
            canvas.drawRect(mWhiteRectF, mWhitePaint);
            // 绘制叶子
            drawLeafs(canvas);
            // 2.绘制Orange ARC
            canvas.drawArc(mArcRectF, 90, 180, false, mOrangePaint);
            // 3.绘制Orange RECT
            mOrangeRectF.left = mArcRightLocation;
            mOrangeRectF.right = mCurrentProgressPosition;
            canvas.drawRect(mOrangeRectF, mOrangePaint);
        }
    }

    /**
     * 绘制叶子
     *
     * @param canvas
     */
    private void drawLeafs(Canvas canvas) {
        mLeafRotatePeriod = mLeafRotatePeriod <= 0 ? LEAF_ROTATE_PERIOD : mLeafRotatePeriod;
        long currentTime = System.currentTimeMillis();
        for (Leaf leaf : mLeafInfos) {
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                // 绘制叶子－－根据叶子的类型和当前时间得出叶子的（x，y）
                getLeafLocation(leaf, currentTime);
                // 根据时间计算旋转角度
                canvas.save();
                // 通过Matrix控制叶子旋转
                Matrix matrix = new Matrix();
                float transX = mLeftMargin + leaf.x;
                float transY = mLeftMargin + leaf.y;
                matrix.postTranslate(transX, transY);
                // 通过时间关联旋转角度，则可以直接通过修改 LEAF_ROTATE_PERIOD 调节叶子旋转快慢
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotatePeriod) / (float) mLeafRotatePeriod;
                int angle = (int) (rotateFraction * 360);
                // 根据叶子旋转方向确定叶子旋转角度
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : -angle + leaf.rotateAngle;
                matrix.postRotate(rotate, transX + mLeafWidth / 2, transY + mLeafHeight / 2);
                canvas.drawBitmap(mLeafBitmap, matrix, mBitmapPaint);
                canvas.restore();
            }
        }
    }

    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;
        mLeafFloatPeriod = mLeafRotatePeriod <= 0 ? LEAF_FLOAT_PERIOD : mLeafFloatPeriod;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatPeriod) {
            leaf.startTime = System.currentTimeMillis() + new Random().nextInt((int) mLeafFloatPeriod);
        }

        float fraction = (float) intervalTime / mLeafFloatPeriod;
        leaf.x = mProgressWidth * (1 - fraction);
        leaf.y = getLocationY(leaf);
    }

    // 通过叶子信息获取当前叶子的Y值
    private int getLocationY(Leaf leaf) {
        // y = A(wx+Q)+h
        float w = (float) (2 * Math.PI / mProgressWidth);
        float a = mMiddleAmplitude;
        switch (leaf.type) {
            case LITTLE:
                // 小振幅 ＝ 中等振幅 － 振幅差
                a = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case MIDDLE:
                break;
            case BIG:
                // 小振幅 ＝ 中等振幅 + 振幅差
                a = mMiddleAmplitude + mAmplitudeDisparity;
                break;
            default:
                break;
        }
        return (int) (a * Math.sin(w * leaf.x)) + mArcRadius * 2 / 3;
    }

    private enum StartType {
        LITTLE, MIDDLE, BIG
    }

    /**
     * 叶子对象，用来记录叶子的主要信息
     */
    private class Leaf {
        // 在绘制部分的位置
        float x, y;
        // 控制叶子飘动的振幅
        StartType type;
        // 旋转角度
        int rotateAngle;
        // 旋转方向--0代表顺时针,1代表逆时针
        int rotateDirection;
        // 起始时间(ms)
        long startTime;

        @Override
        public String toString() {
            return "Leaf{" +
                    "x=" + x +
                    ", y=" + y +
                    ", type=" + type +
                    ", rotateDirection=" + rotateDirection +
                    ", rotateAngle=" + rotateAngle +
                    '}';
        }
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 8;
        Random random = new Random();

        // 生成一个叶子，属性随机
        public Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            int randomType = random.nextInt(3);
            //随机振幅类型
            leaf.type = randomType == 0 ? StartType.MIDDLE : (randomType == 1 ? StartType.LITTLE : StartType.BIG);
            //随机起始的旋转角度
            leaf.rotateAngle = random.nextInt(360);
            //随机旋转方向(顺时针或逆时针)
            leaf.rotateDirection = random.nextInt(2);
            // 为了产生交错的感觉，让开始的时间有一定的随机性
            mLeafFloatPeriod = mLeafFloatPeriod <= 0 ? LEAF_FLOAT_PERIOD : mLeafFloatPeriod;
            mAddTime += random.nextInt((int) (mLeafFloatPeriod / 4));
            Log.i(TAG, "generateLeaf: mAddTime = " + mAddTime);
            leaf.startTime = System.currentTimeMillis() + mAddTime;
            return leaf;
        }

        // 根据最大叶子数产生叶子
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        // 根据传入的叶子数量产生叶子
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new ArrayList<>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }
    }

    private int dip2Px(int dip) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return (int) (dip * displayMetrics.density + 0.5f);
    }

    //-------------------------------对外开放的接口-------------------------------

    /**
     * 设置中等振幅大小
     *
     * @param amplitude
     */
    public void setMiddleAmplitude(int amplitude) {
        this.mMiddleAmplitude = amplitude;
    }

    /**
     * 设置振幅差
     *
     * @param disparity
     */
    public void setAmplitudeDisparity(int disparity) {
        this.mAmplitudeDisparity = disparity;
    }

    /**
     * 获取中等振幅大小
     *
     * @return
     */
    public int getMiddleAmplitude() {
        return mMiddleAmplitude;
    }

    /**
     * 获取振幅差
     *
     * @return
     */
    public int getAmplitudeDisparity() {
        return mAmplitudeDisparity;
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    /**
     * 设置叶子飘动的周期
     *
     * @param period
     */
    public void setLeafFloatPeriod(long period) {
        this.mLeafFloatPeriod = period;
    }

    /**
     * 设置叶子旋转周期
     *
     * @param period
     */
    public void setLeafRotatePeriod(long period) {
        this.mLeafRotatePeriod = period;
    }

    /**
     * 获取叶子飘动的周期
     *
     * @return
     */
    public long getLeafFloatPeriod() {
        mLeafFloatPeriod = mLeafFloatPeriod == 0 ? LEAF_FLOAT_PERIOD : mLeafFloatPeriod;
        return mLeafFloatPeriod;
    }

    /**
     * 获取叶子旋转周期
     *
     * @return
     */
    public long getLeafRotatePeriod() {
        mLeafRotatePeriod = mLeafRotatePeriod == 0 ? LEAF_ROTATE_PERIOD : mLeafRotatePeriod;
        return mLeafRotatePeriod;
    }

    public void onDestroy(){
        if (mLeafBitmap != null && !mLeafBitmap.isRecycled()){
            mLeafBitmap.recycle();
            mLeafBitmap = null;
        }

        if (mOuterBitmap != null && !mOuterBitmap.isRecycled()){
            mOuterBitmap.recycle();
            mOuterBitmap = null;
        }

        if (mFanBitmap != null && !mFanBitmap.isRecycled()){
            mFanBitmap.recycle();
            mFanBitmap = null;
        }
    }
}
