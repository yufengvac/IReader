package com.yufeng.ireader.reader.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.yufeng.ireader.db.book.BookDatabase;
import com.yufeng.ireader.db.readhistory.ReadTxtParagraph;
import com.yufeng.ireader.db.readhistory.ReadTxtParagraphDatabase;
import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadExteriorHelper;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.view.ReadView;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.PathHelper;

import java.io.IOException;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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
    private Bitmap curBitmap;
    private ReadTxtParagraphDatabase readBookHistoryDb;
    private ReadView readView;

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

//        Single.create(new SingleOnSubscribe<Void>() {
//            @Override
//            public void subscribe(SingleEmitter<Void> e) throws Exception {
//                readBookHistoryDb.getReadTxtParagraphDao().deleteReadBookHistory();
//            }
//        }).subscribeOn(Schedulers.io()).toFuture();


        Single<List<ReadTxtParagraph>> single = readBookHistoryDb.getReadTxtParagraphDao().getAllReadBookHistory(path);
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ReadTxtParagraph>>() {
                    @Override
                    public void accept(List<ReadTxtParagraph> readTxtParagraphList) throws Exception {
                        initPageFromHistory(readTxtParagraphList);
                    }
                });
//        initPageFromHistory(null);

    }

    private void initPageFromHistory(List<ReadTxtParagraph> readTxtParagraphList){
        TxtParagraph firstTxtParagraph = null;
        try {
            if (readTxtParagraphList !=null && readTxtParagraphList.size() > 0){
                firstTxtParagraph = ReadTxtParagraph.backToTxtParagraph(readTxtParagraphList.get(0));
            }
            if (firstTxtParagraph != null){
                Log.e(TAG,"历史记录:"+firstTxtParagraph.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Page currentPage = Page.createCurPagerAgain(firstTxtParagraph, firstTxtParagraph != null ? firstTxtParagraph.getLastCanDrawLine():-1,
                this.readSetting, readRandomAccessFile, -1);

        pagerSparseArray.put(PageType.PAGE_CURRENT, currentPage);
        if (readView != null){
            readView.postInvalidate();
        }
    }

    public void drawPager(final Canvas canvas, final Paint paint, boolean isForceCalc, long fixedSeekStart) {
        if (pagerSparseArray == null) {
            return;
        }
        if (pagerSparseArray.size() > 0) {
            Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);

            if (curPage != null){

                if (isForceCalc){
                    TxtParagraph firstTxtParagraph = curPage.getFirstTxtParagraph();
                    curPage = Page.createCurPagerAgain(firstTxtParagraph , firstTxtParagraph != null ? firstTxtParagraph.getLastCanDrawLine():-1,
                            this.readSetting, readRandomAccessFile, fixedSeekStart);
                    pagerSparseArray.put(PageType.PAGE_CURRENT, curPage);
                }
                drawCanvasBg(canvas, paint);
                curPage.drawTxtParagraph(canvas, paint);

            }
        }

        Single.create(new SingleOnSubscribe<Void>() {
            @Override
            public void subscribe(SingleEmitter<Void> singleEmitter) throws Exception {
                Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);

                if (curBitmap != null && !curBitmap.isRecycled()){
                    curBitmap.recycle();
                    curBitmap = null;
                }
                try {
                    curBitmap = Bitmap.createBitmap(DisplayConstant.DISPLAY_WIDTH, DisplayConstant.DISPLAY_HEIGHT, Bitmap.Config.ARGB_4444);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (curBitmap != null && curPage != null){
                    Canvas cacheCanvas = new Canvas(curBitmap);
                    drawCanvasBg(cacheCanvas, paint);
                    curPage.drawTxtParagraph(cacheCanvas, paint);
                }

                prepareNextBitmap();
                preparePreBitmap();

            }
        }).subscribeOn(Schedulers.io()).toFuture();
    }

    private void prepareNextBitmap() {
        if (pagerSparseArray == null) {
            return;
        }
        synchronized (this){
            Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);

            if (curPage != null){
                TxtParagraph lastTxtParagraph = curPage.getLastTxtParagraph();
                lastTxtParagraph = TxtParagraph.copyTxtParagraph(lastTxtParagraph);

                Page nextPage = Page.createNextPager(lastTxtParagraph, lastTxtParagraph.getLastCanDrawLine(), readSetting, readRandomAccessFile);
                pagerSparseArray.put(PageType.PAGE_NEXT, nextPage);

                if (nextCacheBitmap != null){

                    Canvas cacheCanvas = new Canvas(nextCacheBitmap);
                    drawCanvasBg(cacheCanvas, readSetting.getContentPaint());
                    nextPage.drawTxtParagraph(cacheCanvas, readSetting.getContentPaint());
                }
            }
        }

    }

    private void preparePreBitmap() {
        if (pagerSparseArray == null) {
            return;
        }
        synchronized (this){
            Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);

            TxtParagraph firstTxtParagraph = curPage.getFirstTxtParagraph();
            firstTxtParagraph = TxtParagraph.copyTxtParagraph(firstTxtParagraph);

            Page prePage = Page.createPrePager(firstTxtParagraph, firstTxtParagraph.getFirstCanDrawLine(), readSetting, readRandomAccessFile);

            pagerSparseArray.put(PageType.PAGE_PREVIOUS, prePage);

            if (prePage != null && preCacheBitmap != null){
                Canvas cacheCanvas = new Canvas(preCacheBitmap);
                drawCanvasBg(cacheCanvas, readSetting.getContentPaint());
                prePage.drawTxtParagraph(cacheCanvas, readSetting.getContentPaint());
            }
        }

    }


    public void turnNextPage(final Canvas canvas, final Paint paint) {
        if (nextCacheBitmap != null) {

            Single.create(new SingleOnSubscribe<Void>() {
                @Override
                public void subscribe(SingleEmitter<Void> e) throws Exception {
                    Page nextPage = pagerSparseArray.get(PageType.PAGE_NEXT);

                    curBitmap = nextCacheBitmap.copy(Bitmap.Config.ARGB_4444, true);

                    Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);
                    pagerSparseArray.put(PageType.PAGE_PREVIOUS, curPage);
                    pagerSparseArray.put(PageType.PAGE_CURRENT, nextPage);

                    prepareNextBitmap();
                    preparePreBitmap();

                }
            }).subscribeOn(Schedulers.io()).toFuture();

        } else {
            Log.e(TAG, "cacheBitmap == null");
        }
    }

    public void turnPrePage(final Canvas canvas, Paint paint, Context context) {
        if (preCacheBitmap != null) {
            final Page prePage = pagerSparseArray.get(PageType.PAGE_PREVIOUS);

            if (prePage != null) {
                TxtParagraph txtParagraph = prePage.getLastTxtParagraph();
                if (txtParagraph != null){


                    Single.create(new SingleOnSubscribe<Void>() {
                        @Override
                        public void subscribe(SingleEmitter<Void> e) throws Exception {
                            curBitmap = preCacheBitmap.copy(Bitmap.Config.ARGB_4444,true);

                            Page nextPage = pagerSparseArray.get(PageType.PAGE_CURRENT);

                            pagerSparseArray.put(PageType.PAGE_NEXT, nextPage);
                            pagerSparseArray.put(PageType.PAGE_CURRENT, prePage);

                            preparePreBitmap();
                            prepareNextBitmap();
                        }
                    }).subscribeOn(Schedulers.io()).toFuture();

                }
            } else {
                Canvas cacheCanvas = new Canvas(preCacheBitmap);
                drawCanvasBg(cacheCanvas, paint);
                Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);
                curPage.drawTxtParagraph(cacheCanvas, paint);
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

    public void drawCanvasBitmap(Canvas canvas, Bitmap bitmap, Paint paint){
        canvas.drawBitmap(bitmap,0 ,0 , paint);
    }

    public Bitmap getNextCacheBitmap(){
        return nextCacheBitmap;
    }

    public Bitmap getPreCacheBitmap(){
        return preCacheBitmap;
    }

    public Bitmap getCurBitmap(){
        return curBitmap;
    }

    public long getCurPosition(){
        return pagerSparseArray.get(PageType.PAGE_CURRENT).getFirstTxtParagraph().getSeekStart();
    }

    public float getCurPercent(){
        return getCurPosition() * 1.0f / readRandomAccessFile.getSize();
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

    /**
     * 保存当前page的第一个TxtParagraph作为历史记录
     */
    public void saveReadHistory(){
        Page curPage = pagerSparseArray.get(PageType.PAGE_CURRENT);
        if (curPage != null){
            final TxtParagraph firstTxtParagraph = curPage.getFirstTxtParagraph();
            final String bookName = PathHelper.getBookNameByPath(readRandomAccessFile.getRealPath());
            if (firstTxtParagraph != null){
                try {
                    Single.create(new SingleOnSubscribe<Void>() {
                        @Override
                        public void subscribe(SingleEmitter<Void> e) throws Exception {
                            float percent = firstTxtParagraph.getSeekEnd() *1.0f / readRandomAccessFile.getSize();
                            Log.i(TAG,"保存历史记录:"+firstTxtParagraph.toString());
                            ReadTxtParagraph readTxtParagraph = ReadTxtParagraph.createReadTxtParagraph(bookName,readRandomAccessFile.getRealPath(),
                                    readRandomAccessFile.getSize(),percent,firstTxtParagraph);
                            long result = readBookHistoryDb.getReadTxtParagraphDao().insertReadBookHistory(readTxtParagraph);
                            Log.e(TAG,"保存第"+result+"次阅读历史记录");

                            long result1 = BookDatabase.getInstance().getBookDao().updateBookReadPercent(readRandomAccessFile.getRealPath(), percent);
                            Log.e(TAG,"更新书籍阅读进度percent="+percent);
                        }
                    }).subscribeOn(Schedulers.io()).toFuture();

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        final long deleteLastTime = System.currentTimeMillis() - 3 * 24 * 3600 * 1000;
        Single.create(new SingleOnSubscribe<Void>() {
            @Override
            public void subscribe(SingleEmitter<Void> singleEmitter) throws Exception {
                int result = readBookHistoryDb.getReadTxtParagraphDao().deleteReadBookHistory(readRandomAccessFile.getRealPath(), deleteLastTime);
                Log.e(TAG,"删除三天前的阅读记录"+result+"条");
            }
        }).subscribeOn(Schedulers.io()).toFuture();
    }

    public void setReadView(ReadView readView) {
        this.readView = readView;
    }

    public void onDestroy() {
        try {


            if (nextCacheBitmap != null && !nextCacheBitmap.isRecycled()) {
                nextCacheBitmap.recycle();
                nextCacheBitmap = null;
            }

            if (preCacheBitmap != null && !preCacheBitmap.isRecycled()) {
                preCacheBitmap.recycle();
                preCacheBitmap = null;
            }

            if (curBitmap != null && !curBitmap.isRecycled()){
                curBitmap.recycle();
                curBitmap = null;
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
