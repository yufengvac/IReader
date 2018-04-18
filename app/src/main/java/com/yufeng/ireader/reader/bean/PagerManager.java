package com.yufeng.ireader.reader.bean;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;

import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisplayConstant;

import java.io.IOException;

/**
 * Created by yufeng on 2018/4/18-0018.
 *
 */

public class PagerManager {
    private static final String TAG = PagerManager.class.getSimpleName();
    private SparseArray<Pager> pagerSparseArray ;
    private ReadRandomAccessFile readRandomAccessFile;
    private IReadSetting readSetting;

    private PagerManager(){
        if (pagerSparseArray == null){
            pagerSparseArray = new SparseArray<>();
        }
    }

    private static class PagerManagerHolder {
        private static PagerManager INSTANCE  = null;
        private static PagerManager getINSTANCE(){
            if (INSTANCE == null){
                INSTANCE = new PagerManager();
            }
            return INSTANCE;
        }
    }

    public static PagerManager getInstance(){
        return PagerManagerHolder.getINSTANCE();
    }


    public void initPagers(IReadSetting readSetting, String path){
        if (pagerSparseArray == null){
            pagerSparseArray = new SparseArray<>();
        }
        if (pagerSparseArray.get(0) == null){
            initReadRandomAccessFile(path);
            this.readSetting = readSetting;
            pagerSparseArray.put(0,Pager.createNextPager(this.readSetting, readRandomAccessFile));

        }
    }

    public void drawPagerOne(Canvas canvas, Paint paint){
        if (pagerSparseArray == null){
            return;
        }
        if (pagerSparseArray.size() > 0){
            Pager curPage = pagerSparseArray.get(0);
            curPage.drawTxtParagraph(canvas,paint);
        }
    }

    public void drawPagerTwo(Canvas canvas, Paint paint){
        if (pagerSparseArray == null){
            return;
        }
        Pager nextPage = Pager.createNextPager(readSetting, readRandomAccessFile);
        pagerSparseArray.put(1, nextPage);
        canvas.save();
        canvas.translate(DisplayConstant.DISPLAY_WIDTH, 0);
        nextPage.drawTxtParagraph(canvas, paint);
        RectF rectF = new RectF(0,0,DisplayConstant.DISPLAY_WIDTH,DisplayConstant.DISPLAY_HEIGHT);
        canvas.drawRect(rectF,paint);
        canvas.restore();
    }

    private void initReadRandomAccessFile(String path) {
        try {
            if (readRandomAccessFile == null) {
                readRandomAccessFile = new ReadRandomAccessFile(path, "r");
                int code = CodeUtil.regCode(path);
                Log.i(TAG, "字符编码是：" + CodeUtil.getEncodingByCode(code));
                readRandomAccessFile.setCode(code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
