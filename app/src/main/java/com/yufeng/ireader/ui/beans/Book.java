package com.yufeng.ireader.ui.beans;

import android.text.TextUtils;

/**
 * Created by yufeng on 2018/4/14.
 *
 */

public class Book {
    private String bookName;
    private String bookDesc;
    private String path;

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static Book createBook(String bookName,String bookDesc, String path){
        if (TextUtils.isEmpty(bookName)||TextUtils.isEmpty(path)){
            return null;
        }
        Book book = new Book();
        book.setBookName(bookName);
        book.setBookDesc(TextUtils.isEmpty(bookDesc)? "" : bookDesc);
        book.setPath(path);
        return book;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookName='" + bookName + '\'' +
                "bookDesc='" + bookDesc + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
