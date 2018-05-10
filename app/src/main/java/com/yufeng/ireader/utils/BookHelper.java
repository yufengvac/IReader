package com.yufeng.ireader.utils;

import android.text.TextUtils;
import android.util.Log;

import com.yufeng.ireader.db.book.Book;
import com.yufeng.ireader.db.book.BookDatabase;
import com.yufeng.ireader.ui.home.callback.OnBookQueryListener;

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

}
