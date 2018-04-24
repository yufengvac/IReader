package com.yufeng.ireader.reader.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.yufeng.ireader.reader.db.ReadTxtParagraph;
import com.yufeng.ireader.reader.db.ReadTxtParagraphDatabase;
import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadExteriorHelper;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisplayConstant;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yufeng on 2018/4/18-0018.
 *
 */

public class PageManager {
    private static final String TAG = PageManager.class.getSimpleName();
    private SparseArray<Page> pagerSparseArray;
    private ReadRandomAccessFile readRandomAccessFile;
    private IReadSetting readSetting;
    private Bitmap nextCacheBitmap;
    private Bitmap preCacheBitmap;
    private int lastCanDrawLine = -1;
    private TxtParagraph curPageTxtParagraph;
    private ReadTxtParagraphDatabase readBookHistoryDb;

    private static class PageType {
        private static final int PAGE_CURRENT = 0;
        private static final int PAGE_NEXT = 1;
        private static final int PAGE_PREVIOUS = 2;
    }

    private PageManager() {
        if (pagerSparseArray == null) {
            pagerSparseArray = new SparseArray<>();
        }

        try {
            nextCacheBitmap = Bitmap.createBitmap(DisplayConstant.DISPLAY_WIDTH, DisplayConstant.DISPLAY_HEIGHT, Bitmap.Config.ARGB_4444);
        } catch (Exception e) {
            e.printStackTrace();
            nextCacheBitmap = null;
        }

        try {
            preCacheBitmap = Bitmap.createBitmap(DisplayConstant.DISPLAY_WIDTH, DisplayConstant.DISPLAY_HEIGHT, Bitmap.Config.ARGB_4444);
        } catch (Exception e) {
            e.printStackTrace();
            preCacheBitmap = null;
        }

        curPageTxtParagraph = null;
        readBookHistoryDb = ReadTxtParagraphDatabase.getInstance();
    }

    private static class PagerManagerHolder {
        private static PageManager INSTANCE = null;

        private static PageManager getINSTANCE() {
            if (INSTANCE == null) {
                INSTANCE = new PageManager();
            }
            return INSTANCE;
        }

        private static void destroy() {
            INSTANCE = null;
        }
    }

    public static PageManager getInstance() {
        return PagerManagerHolder.getINSTANCE();
    }


    public void initPagers(IReadSetting readSetting, String path) {
        if (pagerSparseArray == null) {
            pagerSparseArray = new SparseArray<>();
        }
        initReadRandomAccessFile(path);
        this.readSetting = readSetting;

        Page currentPage = Page.createNextPager(curPageTxtParagraph, lastCanDrawLine, this.readSetting, readRandomAccessFile);
        pagerSparseArray.put(PageType.PAGE_CURRENT, currentPage);

    }

    public void drawPager(Canvas canvas, Paint paint) {
        if (pagerSparseArray == null) {
            return;
        }
        if (pagerSparseArray.size() > 0) {
            Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);

            int code = curPage.drawTxtParagraph(canvas, paint);
            setLastCanDrawLineAndTxtParagraph(curPage, code);
        }
    }

    public void prepareNextBitmap() {
        if (pagerSparseArray == null) {
            return;
        }
        Page nextPage = Page.createNextPager(curPageTxtParagraph, lastCanDrawLine, readSetting, readRandomAccessFile);
        pagerSparseArray.put(PageType.PAGE_NEXT, nextPage);
    }

    private void preparePreBitmap() {
        if (pagerSparseArray == null) {
            return;
        }
        Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);

        TxtParagraph firstTxtParagraph = curPage.getFirstTxtParagraph();
        firstTxtParagraph = TxtParagraph.copyTxtParagraph(firstTxtParagraph);

        Page prePage = Page.createPrePager(firstTxtParagraph, firstTxtParagraph.getFirstCanDrawLine(), readSetting, readRandomAccessFile);

        pagerSparseArray.put(PageType.PAGE_PREVIOUS, prePage);
    }

    private void setLastCanDrawLineAndTxtParagraph(Page pager, int code) {
        lastCanDrawLine = code;
        curPageTxtParagraph = pager.getLastTxtParagraph();

        if (curPageTxtParagraph != null){
            if (lastCanDrawLine == -1) {//可以被完全绘制完成
                curPageTxtParagraph.setCanDrawCompleted(true);
            } else if (lastCanDrawLine == -2) {
                curPageTxtParagraph = null;
            } else {//不可以被完全绘制完成
                curPageTxtParagraph.setCanDrawCompleted(false);
            }
        }
        curPageTxtParagraph = TxtParagraph.copyTxtParagraph(curPageTxtParagraph);

    }

    public void turnNextPage(Canvas canvas, Paint paint) {
        if (nextCacheBitmap != null) {

            Page nextPage = pagerSparseArray.get(PageType.PAGE_NEXT);
            Canvas cacheCanvas = new Canvas(nextCacheBitmap);
            drawCanvasBg(cacheCanvas, paint);

            int code = nextPage.drawTxtParagraph(cacheCanvas, paint);
            setLastCanDrawLineAndTxtParagraph(nextPage, code);

            canvas.drawBitmap(nextCacheBitmap, 0, 0, paint);


            Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);
            pagerSparseArray.put(PageType.PAGE_PREVIOUS, curPage);
            pagerSparseArray.put(PageType.PAGE_CURRENT, nextPage);

            prepareNextBitmap();

        } else {
            Log.e(TAG, "cacheBitmap == null");
        }
    }

    public void turnPrePage(Canvas canvas, Paint paint, Context context) {
        if (preCacheBitmap != null) {
            Page prePage = pagerSparseArray.get(PageType.PAGE_PREVIOUS);

            if (prePage != null) {
                TxtParagraph txtParagraph = prePage.getLastTxtParagraph();
                if (txtParagraph != null){

                    Canvas cacheCanvas = new Canvas(preCacheBitmap);
                    drawCanvasBg(cacheCanvas, paint);

                    int code = prePage.drawTxtParagraph(cacheCanvas, paint);
                    setLastCanDrawLineAndTxtParagraph(prePage, code);

                    canvas.drawBitmap(preCacheBitmap, 0, 0, paint);

                    Page nextPage = pagerSparseArray.get(PageType.PAGE_CURRENT);

                    pagerSparseArray.put(PageType.PAGE_NEXT, nextPage);
                    pagerSparseArray.put(PageType.PAGE_CURRENT, prePage);

                    preparePreBitmap();
                }
            } else {
                Canvas cacheCanvas = new Canvas(preCacheBitmap);
                drawCanvasBg(cacheCanvas, paint);
                Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);
                int code = curPage.drawTxtParagraph(cacheCanvas, paint);
                setLastCanDrawLineAndTxtParagraph(curPage, code);
                canvas.drawBitmap(preCacheBitmap, 0, 0, paint);

                Toast.makeText(context, "已经是第一章了~", Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.e(TAG, "cacheBitmap == null");
        }
    }

    public void drawCanvasBg(Canvas canvas, Paint paint){
        ReadExteriorHelper.getInstance().drawReadBackground(canvas, paint);
    }

    private void initReadRandomAccessFile(String path) {
        try {
            if (readRandomAccessFile == null) {
                readRandomAccessFile = new ReadRandomAccessFile(path, "r");
                int code = CodeUtil.regCode(path);
                Log.i(TAG, "字符编码是：" + CodeUtil.getEncodingByCode(code));
                readRandomAccessFile.setCode(code);
                readRandomAccessFile.setSize(readRandomAccessFile.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveReadHistory(){
        Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);
        if (curPage != null){
            final TxtParagraph firstTxtParagraph = curPage.getFirstTxtParagraph();
            final String bookName = readRandomAccessFile.getRealPath();
            if (firstTxtParagraph != null){
//                try {
//                    Single.create(new SingleOnSubscribe<Void>() {
//                        @Override
//                        public void subscribe(SingleEmitter<Void> e) throws Exception {
//
//                            float percent = firstTxtParagraph.getSeekEnd() *1.0f / readRandomAccessFile.getSize();
//
//                            ReadTxtParagraph readTxtParagraph = ReadTxtParagraph.createReadTxtParagraph(bookName,readRandomAccessFile.getRealPath(),
//                                    readRandomAccessFile.getSize(),percent,firstTxtParagraph);
//                            long result = readBookHistoryDb.getReadTxtParagraphDao().insertReadBookHistory(readTxtParagraph);
//                            Log.e(TAG,"result="+result);
//                        }
//                    }).subscribeOn(Schedulers.io()).toFuture().get();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

            }
        }
    }

    public void onDestroy() {
        try {

            curPageTxtParagraph = null;
            lastCanDrawLine = -1;

            if (nextCacheBitmap != null && !nextCacheBitmap.isRecycled()) {
                nextCacheBitmap.recycle();
                nextCacheBitmap = null;
            }

            if (preCacheBitmap != null && !preCacheBitmap.isRecycled()) {
                preCacheBitmap.recycle();
                preCacheBitmap = null;
            }

            if (readRandomAccessFile != null) {
                readRandomAccessFile.setCurPosition(0);
                readRandomAccessFile.seek(0);
            }

            pagerSparseArray.clear();
            pagerSparseArray = null;
            System.gc();

            PagerManagerHolder.destroy();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (readRandomAccessFile != null) {
                    readRandomAccessFile.close();
                    readRandomAccessFile = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
