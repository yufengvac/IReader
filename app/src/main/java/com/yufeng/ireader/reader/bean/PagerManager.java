package com.yufeng.ireader.reader.bean;

import android.graphics.Bitmap;
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
import com.yufeng.ireader.utils.PathHelper;

import java.io.IOException;
import java.util.List;

/**
 * Created by yufeng on 2018/4/18-0018.
 *
 */

public class PagerManager {
    private static final String TAG = PagerManager.class.getSimpleName();
    private SparseArray<Pager> pagerSparseArray ;
    private ReadRandomAccessFile readRandomAccessFile;
    private IReadSetting readSetting;
    private Bitmap nextCacheBitmap;
    private Bitmap preCacheBitmap;
    private int lastCanDrawLine = -1;
    private TxtParagraph curPageTxtParagraph;

    private PagerManager(){
        if (pagerSparseArray == null){
            pagerSparseArray = new SparseArray<>();
        }
        try {
            nextCacheBitmap = Bitmap.createBitmap(DisplayConstant.DISPLAY_WIDTH, DisplayConstant.DISPLAY_HEIGHT, Bitmap.Config.ARGB_4444);
        }catch (Exception e){
            e.printStackTrace();
            nextCacheBitmap = null;
        }

        try {
            preCacheBitmap = Bitmap.createBitmap(DisplayConstant.DISPLAY_WIDTH, DisplayConstant.DISPLAY_HEIGHT, Bitmap.Config.ARGB_4444);
        }catch (Exception e){
            e.printStackTrace();
            preCacheBitmap = null;
        }

        curPageTxtParagraph = null;
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
        initReadRandomAccessFile(path);
        this.readSetting = readSetting;
        pagerSparseArray.put(0,Pager.createPager(curPageTxtParagraph, lastCanDrawLine,this.readSetting, readRandomAccessFile));
    }

    public void drawPager(Canvas canvas, Paint paint){
        if (pagerSparseArray == null){
            return;
        }
        if (pagerSparseArray.size() > 0){
            Pager curPage = pagerSparseArray.get(0);

            int code = curPage.drawTxtParagraph(canvas,paint);
            setLastCanDrawLineAndTxtParagraph(curPage, code);
        }
    }

    public void prepareNextBitmap(Paint paint){
        if (pagerSparseArray == null){
            return;
        }
        Pager nextPage = Pager.createPager(curPageTxtParagraph, lastCanDrawLine, readSetting, readRandomAccessFile);
        pagerSparseArray.put(1, nextPage);

        if (nextCacheBitmap != null){

            Canvas cacheCanvas = new Canvas(nextCacheBitmap);
            cacheCanvas.drawColor(Color.parseColor("#B3AFA7"));

            int code = nextPage.drawTxtParagraph(cacheCanvas, paint);
            setLastCanDrawLineAndTxtParagraph(nextPage,code);
            PathHelper.saveBitmapToSDCard(nextCacheBitmap, System.currentTimeMillis()+"_1");
        }else {
            Log.e(TAG,"cacheBitmap == null");
        }
    }

    private void setLastCanDrawLineAndTxtParagraph(Pager pager,int code){
        lastCanDrawLine = code;
        List<TxtParagraph> curPagerTxtParagraphList = pager.getTxtParagraphList();
        if (lastCanDrawLine == -1){
            curPageTxtParagraph = null;
        }else {
            curPageTxtParagraph = curPagerTxtParagraphList.get(curPagerTxtParagraphList.size()-1);
        }
    }

    public void turnNextPage(Canvas canvas, Paint paint){
        canvas.drawBitmap(nextCacheBitmap,0,0,paint);
        PathHelper.saveBitmapToSDCard(nextCacheBitmap, System.currentTimeMillis()+"_2");

    }

    public void turnPrePage(Canvas canvas, Paint paint){
        canvas.drawBitmap(preCacheBitmap,0,0, paint);
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

    public void onDestroy(){
        try {

            curPageTxtParagraph = null;
            lastCanDrawLine = -1;

            if (nextCacheBitmap != null && !nextCacheBitmap.isRecycled()){
                nextCacheBitmap.recycle();
                nextCacheBitmap = null;
            }

            if (preCacheBitmap != null && !preCacheBitmap.isRecycled()){
                preCacheBitmap.recycle();
                preCacheBitmap = null;
            }

            if (readRandomAccessFile != null){
                readRandomAccessFile.setCurPosition(0);
                readRandomAccessFile.seek(0);
            }

            pagerSparseArray.clear();
            pagerSparseArray = null;
            System.gc();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (readRandomAccessFile != null){
                    readRandomAccessFile.close();
                    readRandomAccessFile = null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
