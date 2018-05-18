package com.yufeng.ireader.utils;

import android.text.TextUtils;
import android.util.Log;

import com.yufeng.ireader.db.book.Book;
import com.yufeng.ireader.db.book.BookDatabase;
import com.yufeng.ireader.ui.home.callback.OnBookQueryListener;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yufeng on 2018/4/13-0013.
 *
 */

public class BookHelper {

    private static final String TAG = BookHelper.class.getSimpleName();
    public static boolean isChapterParagraph(String paragraph){
       return !TextUtils.isEmpty(paragraph) && paragraph.length() < 30 && judgeChapter(paragraph);
    }

    public static boolean judgeChapter(String paragraph){
        return paragraph.matches(".*第.{1,9}章.*\\n") || paragraph.matches(".第.{1,9}回.\\n")||
                paragraph.matches(".第.{1,9}节.\\n") || paragraph.matches(".第.{1,9}卷.*\\n") ||
                paragraph.matches(".第一卷.*\\n") || paragraph.matches(".第二卷.*\\n") || paragraph.matches(".第三卷.*\\n")||
                paragraph.matches(".第四卷.*\\n") || paragraph.matches(".第五卷.*\\n") || paragraph.matches(".第六卷.*\\n");
    }

    public static void getLocalBooksInDirectory(final OnBookQueryListener listener){
        try {
            BookDatabase.getInstance().getBookDao().getAllBookList()
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Book>>() {
                        @Override
                        public void accept(List<Book> bookList) throws Exception {
                            if (bookList != null && bookList.size() > 0){
                                listener.onBookQuery(bookList);
                            }else {
                                List<Book> bookList1 = PathHelper.getBooksInDirectory();
                                saveLocalBookToDB(bookList1);
                                listener.onBookQuery(bookList1);
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void updateLastReadTime(final String bookPath, final long lastReadTime){
        try {
            Single.create(new SingleOnSubscribe<Void>() {
                @Override
                public void subscribe(SingleEmitter<Void> singleEmitter) throws Exception {
                    long result = BookDatabase.getInstance().getBookDao().updateBookLastReadTime(bookPath, lastReadTime);
                    Log.i(TAG,"更新数据库成功->result="+result);
                }
            }).subscribeOn(Schedulers.io()).toFuture();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void saveLocalBookToDB(List<Book> bookList){
        for (final Book book : bookList){
            try {
                Single.create(new SingleOnSubscribe<Void>() {
                    @Override
                    public void subscribe(SingleEmitter<Void> singleEmitter) throws Exception {
                        long result = BookDatabase.getInstance().getBookDao().insertBook(book);
                        Log.i(TAG,"插入成功->"+result);
                    }
                }).subscribeOn(Schedulers.io()).toFuture();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新书籍介绍，该方法需要在子线程中调用
     * @param bookPath  书籍路径
     * @param bookDesc  书籍介绍
     * @return          true成功；false失败
     */
    public static boolean updateBookDesc(String bookPath, String bookDesc){
        if (isEmptyLine(bookDesc) || isEmptyLine(bookPath)){
            return false;
        }
        BookDatabase.getInstance().getBookDao().updateBookDesc(bookPath, bookDesc);
        return true;
    }

    public static boolean isEmptyLine(String txtParagraph){
        return TextUtils.isEmpty(txtParagraph.trim()) || txtParagraph.trim().equals("\n");
    }


    public static String tranFormFromTimeMillis(long timeMillis){
        String timeStr;
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - timeMillis <= 10 * 60 * 1000L){//10分钟
            timeStr = "刚刚";
        }else if (currentTimeMillis - timeMillis < 60 * 60 *1000L){//1小时
            timeStr =(int)(( currentTimeMillis - timeMillis )/1000f/60f) +"分钟前";
        }else if (currentTimeMillis - timeMillis < 24 * 60 * 60 * 1000L){//一天
            timeStr = (int)((currentTimeMillis - timeMillis)/ 1000f/60f/60f) +"小时前";
        }else if (currentTimeMillis - timeMillis < 30 * 24 * 60 * 60 * 1000L){//一月
            timeStr = (int)((currentTimeMillis - timeMillis)/ 1000f/60f/60f/24f) + "天前";
        }else if (currentTimeMillis - timeMillis < 12 * 30 * 24 * 60 * 60 * 1000L){//一年
            timeStr = (int)((currentTimeMillis - timeMillis)/ 1000f/60f/60f/24f / 30f) + "个月前";
        }else {
            timeStr = "一年前";
        }

        return timeStr;
    }

    public static String tranFormFromReadPercent(float readPercent){
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String percentStr = decimalFormat.format(readPercent * 100);
        if (!TextUtils.isEmpty(percentStr) && !percentStr.equals("0.0")){
            return percentStr+"%";
        }else {
            return "";
        }

    }

    public static String transFormFromByte(long bytes){
        String sizeStr;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        float kb = bytes/1024f;
        if (kb < 1024){
            sizeStr = decimalFormat.format(kb) + "KB";
        }else if (kb < 1024 *1024){
            sizeStr = decimalFormat.format(kb/1024f) + "MB";
        }else if (kb < 1024 * 1024 *1024){
            sizeStr = decimalFormat.format(kb/1024f/1024f) + "GB";
        }else {
            sizeStr = "未知";
        }

        return sizeStr;
    }

}
