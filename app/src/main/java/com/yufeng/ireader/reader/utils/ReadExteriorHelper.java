package com.yufeng.ireader.reader.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;

import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.FileHelper;
import com.yufeng.ireader.utils.PathHelper;

import java.io.IOException;
import java.lang.ref.WeakReference;
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

    public void drawReadBackground(Canvas canvas, Paint paint){
        if (readSetting == null){
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

    private void createBgBitmap(String filePath){
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bgBitmap = BitmapFactory.decodeFile(filePath, options);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initBgRectF(){
        bgRectF = new RectF(0, 0 , DisplayConstant.DISPLAY_WIDTH, DisplayConstant.DISPLAY_HEIGHT);
    }

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
