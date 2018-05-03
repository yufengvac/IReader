package com.yufeng.ireader.reader.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.FileHelper;
import com.yufeng.ireader.utils.PathHelper;
import com.yufeng.ireader.utils.ReadPreferHelper;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by yufeng on 2018/4/24-0024.
 * 阅读器外观工具类
 */

public class ReadExteriorHelper {
    private WeakReference<Activity> weakReference;
    private IReadSetting readSetting;
    private Bitmap bgBitmap = null;
    private RectF bgRectF = null;

    private ReadExteriorHelper(Activity activity, IReadSetting readSetting){
        weakReference = new WeakReference<>(activity);
        this.readSetting = readSetting;
        copyReadBgToSDFromAssets();
        initBgRectF();
    }

    private static class ReadExteriorHolder{
        private static ReadExteriorHelper instance = null;
        private static void init(Activity activity, IReadSetting readSetting){
            if (instance == null){
                instance = new ReadExteriorHelper(activity, readSetting);
            }
        }
        private static ReadExteriorHelper getInstance(){
            if (instance == null){
                throw new NullPointerException("init(activity) must be called firstly to ensure instance is not null");
            }
            return instance;
        }
        private static void destroy(){
            instance = null;
        }
    }
    public static void init(Activity activity, IReadSetting readSetting){
        ReadExteriorHolder.init(activity, readSetting);
    }
    public static ReadExteriorHelper getInstance(){
        return ReadExteriorHolder.getInstance();
    }


    /**
     * 把默认在assets里面的阅读器背景图片拷贝到sd卡中去
     */
    private void copyReadBgToSDFromAssets(){
        if (weakReference.get() == null){
            return;
        }
        Context context = weakReference.get();
        List<String> readBgPathList = PathHelper.getDefaultReadBgInAssets();
        for (int i = 0; i < readBgPathList.size(); i++){
            FileHelper.copyToSDFromAssets(context, readBgPathList.get(i), PathHelper.getCoverPath());
        }
    }

    /**
     * 设置canvas的背景颜色或者图片
     * @param canvas 需要设置背景颜色或者图片的画步
     * @param paint  画笔
     */
    public void drawReadBackground(Canvas canvas, Paint paint){
        if (readSetting == null){
            return;
        }

        //如果是夜间模式，在这里只限定背景颜色，其实也可以自定义背景图片，暂时先不扩展
        if (!readSetting.isDayMode()){
            canvas.drawColor(Color.parseColor(readSetting.getCanvasBgColor()));
            return;
        }

        int readBgOption = readSetting.getCanvasBgOptions();
        String bgPath = PathHelper.getReadBgPathByOption(readBgOption);
        if (TextUtils.isEmpty(bgPath)){ //背景路径为空，只能设置该canvas的颜色资源
            canvas.drawColor(Color.parseColor(readSetting.getCanvasBgColor()));
        }else {
            if (bgBitmap == null){
                createBgBitmap(bgPath);
            }
            canvas.drawBitmap(bgBitmap,null,bgRectF,paint);
        }
    }

    /**
     * 根据文件路径生成一个canvas背景的bitmap对象
     * @param filePath 文件路径
     */
    private void createBgBitmap(String filePath){
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bgBitmap = BitmapFactory.decodeFile(filePath, options);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 初始化背景绘画区域
     */
    private void initBgRectF(){
        bgRectF = new RectF(0, 0 , DisplayConstant.DISPLAY_WIDTH, DisplayConstant.DISPLAY_HEIGHT);
    }

    /**
     * 改变日夜间模式
     */
    public void changeDayNightMode(){
        ReadPreferHelper.getInstance().setIsDayMode(!readSetting.isDayMode());
        resetContentPaint();
    }

    /**
     * 获取画步背景颜色值
     * @return 背景颜色字符串值
     */
    public String getBackgroundColor(){
        if (readSetting.isDayMode()){//日间模式
            return "#B3AFA7";
        }else {//夜间模式
            return "#121212";
        }
    }

    /**
     * 获取内容字体颜色值
     * @return 字体颜色字符串值
     */
    public String getContentPaintTextColor(){
        if (readSetting.isDayMode()){//日间模式
            return "#333333";
        }else {//夜间模式
            return "#4D4D4D";
        }
    }

    /**
     * 更换字体
     * @param context  context
     * @param typeface 字体
     */
    public void changeTypeface(Context context, int typeface){
        ReadPreferHelper.getInstance().setTypeface(typeface);
        Typeface fontTypeface = null;
        if (typeface == ReadExteriorConstants.ReadTypeFace.TYPEFACE_ITALIC){
            fontTypeface = Typeface.createFromAsset(context.getAssets(),"font/italic.ttf");
        }else if (typeface == ReadExteriorConstants.ReadTypeFace.TYPEFACE_XU){
            fontTypeface = Typeface.createFromAsset(context.getAssets(), "font/xujinglei.ttf");
        }
        if (fontTypeface != null){
            readSetting.getContentPaint().setTypeface(fontTypeface);
        }else {
            readSetting.getContentPaint().setTypeface(Typeface.DEFAULT);
        }
    }

    public void changeTextSize(Context context, boolean isMinus, boolean setDefault){
        int curTextSize = DisPlayUtil.px2sp(context, readSetting.getContentPaint().getTextSize());
        if (setDefault){
            if (curTextSize == ReadExteriorConstants.DEFAULT_TEXT_SIZE){
                return;
            }
            readSetting.getContentPaint().setTextSize(DisPlayUtil.sp2px(context, ReadExteriorConstants.DEFAULT_TEXT_SIZE));
            ReadPreferHelper.getInstance().setFontTextSize(ReadExteriorConstants.DEFAULT_TEXT_SIZE);
            return;
        }

        if (isMinus && curTextSize > ReadExteriorConstants.MIN_TEXT_SIZE){

            readSetting.getContentPaint().setTextSize(DisPlayUtil.sp2px(context, curTextSize - 1));
            ReadPreferHelper.getInstance().setFontTextSize(curTextSize -1);

        }else if (!isMinus && curTextSize < ReadExteriorConstants.MAX_TEXT_SIZE){

            readSetting.getContentPaint().setTextSize(DisPlayUtil.sp2px(context, curTextSize + 1));
            ReadPreferHelper.getInstance().setFontTextSize(curTextSize + 1);

        }

    }


    /**
     * 重置画笔颜色
     */
    public void resetContentPaint(){
        readSetting.getContentPaint().setColor(Color.parseColor(getContentPaintTextColor()));
    }

    public void setFullScreen(Activity activity, boolean full) {
        Window window = activity.getWindow();

        if (full) {
            WindowManager.LayoutParams params = window.getAttributes();
            boolean isFullScreen = ((params.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (!isFullScreen) {
                params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                window.setAttributes(params);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams params = window.getAttributes();
            boolean isFullScreen = ((params.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (isFullScreen) {
                params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                window.setAttributes(params);
            }
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static void hideNavigation(View view) {
        try {
            Class<?> classView = View.class;
            Method method = classView.getMethod("setSystemUiVisibility", int.class);
            Field flagField = classView.getField("SYSTEM_UI_FLAG_HIDE_NAVIGATION");
            method.invoke(view, flagField.get(null));
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回收资源
     */
    public void destroy(){
        if (bgBitmap != null && !bgBitmap.isRecycled()){
            bgBitmap.recycle();
            bgBitmap = null;
        }
        bgRectF = null;
        weakReference.clear();
        ReadExteriorHolder.destroy();
    }
}
