package com.yufeng.ireader.db.book;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yufeng on 2018/4/14.
 *
 */
@Entity(tableName = "book")
public class Book {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "book_path")
    private String path = "";

    @NonNull
    @ColumnInfo(name = "book_name")
    private String bookName = "";

    @ColumnInfo(name = "book_desc")
    private String bookDesc;

    @ColumnInfo(name = "last_modify_time")
    private long lastModifyTime;

    @ColumnInfo(name = "size")
    private long size;

    @ColumnInfo(name = "last_read_time")
    private long lastReadTime;

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

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public static Book createBook(String bookName, String bookDesc, String path, long lastModifyTime, long size, long lastReadTime){
        if (TextUtils.isEmpty(bookName)||TextUtils.isEmpty(path)){
            return null;
        }
        Book book = new Book();
        book.setBookName(bookName);
        book.setBookDesc(TextUtils.isEmpty(bookDesc)? "" : bookDesc);
        book.setPath(path);
        book.setLastModifyTime(lastModifyTime);
        book.setSize(size);
        book.setLastReadTime(lastReadTime);
        return book;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookName='" + bookName + '\'' +
                "bookDesc='" + bookDesc + '\'' +
                ", path='" + path + '\'' +
                ", lastModifyTime='" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(new Date(lastModifyTime)) + '\'' +
                ", size='" + (size/1024f/1024f +"M") +
                ", lastReadTime='" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(new Date(lastReadTime)) + '\'' +
                '}';
    }
}
