package com.yufeng.ireader.reader.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.PathHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/18-0018.
 *
 */

public class PageManager {
    private static final String TAG = PageManager.class.getSimpleName();
    private SparseArray<Page> pagerSparseArray ;
    private List<Page> pageList;
    private ReadRandomAccessFile readRandomAccessFile;
    private IReadSetting readSetting;
    private Bitmap nextCacheBitmap;
    private Bitmap preCacheBitmap;
    private int lastCanDrawLine = -1;
    private TxtParagraph curPageTxtParagraph;
    private int curPageIndex = -1;

    public static class PageType{
        private static final int PAGE_CURRENT = 0;
        private static final int PAGE_NEXT = 1;
    }

    private PageManager(){
        if (pagerSparseArray == null){
            pagerSparseArray = new SparseArray<>();
        }
        if (pageList == null){
            pageList = new ArrayList<>();
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
        private static PageManager INSTANCE  = null;
        private static PageManager getINSTANCE(){
            if (INSTANCE == null){
                INSTANCE = new PageManager();
            }
            return INSTANCE;
        }
        private static void destroy(){
            INSTANCE = null;
        }
    }

    public static PageManager getInstance(){
        return PagerManagerHolder.getINSTANCE();
    }
    public static void destroy(){
        PagerManagerHolder.destroy();
    }


    public void initPagers(IReadSetting readSetting, String path){
        if (pagerSparseArray == null){
            pagerSparseArray = new SparseArray<>();
        }
        initReadRandomAccessFile(path);
        this.readSetting = readSetting;

        Page currentPage = Page.createPager(curPageTxtParagraph, lastCanDrawLine,this.readSetting, readRandomAccessFile);
        pagerSparseArray.put(PageType.PAGE_CURRENT, currentPage);

        pageList.add(currentPage);
        curPageIndex = pageList.size() - 1;
    }

    public void drawPager(Canvas canvas, Paint paint){
        if (pagerSparseArray == null){
            return;
        }
        if (pagerSparseArray.size() > 0){
            Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);

           int code = curPage.drawTxtParagraph(canvas,paint);
           setLastCanDrawLineAndTxtParagraph(curPage, code);
        }
    }

    public void prepareNextBitmap(){
        if (pagerSparseArray == null){
            return;
        }
        Page nextPage = Page.createPager(curPageTxtParagraph, lastCanDrawLine, readSetting, readRandomAccessFile);
        pagerSparseArray.put(PageType.PAGE_NEXT, nextPage);
    }

    public void preparePreBitmap(){
    }

    private void setLastCanDrawLineAndTxtParagraph(Page pager, int code){
        lastCanDrawLine = code;
        List<TxtParagraph> curPagerTxtParagraphList = pager.getTxtParagraphList();
        if (lastCanDrawLine == -1){
            curPageTxtParagraph = null;
        }else {
            curPageTxtParagraph = curPagerTxtParagraphList.get(curPagerTxtParagraphList.size()-1);
        }

    }

    public void turnNextPage(Canvas canvas, Paint paint){
        if (nextCacheBitmap != null){
            if (curPageIndex < pageList.size()-1){

                curPageIndex++;

                Page nextPage = pageList.get(curPageIndex);
                Canvas cacheCanvas = new Canvas(nextCacheBitmap);
                cacheCanvas.drawColor(Color.parseColor("#B3AFA7"));

                nextPage.drawTxtParagraph(cacheCanvas, paint);

                canvas.drawBitmap(nextCacheBitmap,0,0,paint);


            }else if (curPageIndex == pageList.size() - 1){

                Page nextPage = pagerSparseArray.get(PageType.PAGE_NEXT);
                Canvas cacheCanvas = new Canvas(nextCacheBitmap);
                cacheCanvas.drawColor(Color.parseColor("#B3AFA7"));

                int code = nextPage.drawTxtParagraph(cacheCanvas, paint);
                setLastCanDrawLineAndTxtParagraph(nextPage,code);

                canvas.drawBitmap(nextCacheBitmap,0,0,paint);

                pageList.add(nextPage);
                curPageIndex = pageList.size() - 1;

                prepareNextBitmap();
            }
        }else {
            Log.e(TAG,"cacheBitmap == null");
        }
    }

    public void turnPrePage(Canvas canvas, Paint paint, Context context){
        if (preCacheBitmap != null){
            if (curPageIndex > 0){
                curPageIndex --;
                Page prePage = pageList.get(curPageIndex);
                Canvas cacheCanvas = new Canvas(preCacheBitmap);
                cacheCanvas.drawColor(Color.parseColor("#B3AFA7"));

                prePage.drawTxtParagraph(cacheCanvas, paint);
                canvas.drawBitmap(preCacheBitmap,0,0,paint);

            }else {
                Toast.makeText(context,"已经是第一章了~",Toast.LENGTH_LONG).show();
                curPageIndex = 0;
            }
        }else {
            Log.e(TAG,"cacheBitmap == null");
        }
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

            destroy();

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
