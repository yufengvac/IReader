package com.yufeng.ireader.db.readchapter;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by yufeng on 2018/5/7-0007.
 *
 */
@Entity(tableName = "chapter")
public class ReadChapter {
    @NonNull
    @ColumnInfo(name = "book_path")
    private String bookPath = "";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "chapter_name")
    private String chapterName = "";

    @ColumnInfo(name = "cur_position")
    private long curPosition;

    @ColumnInfo(name = "percent")
    private float percent;

    @ColumnInfo(name = "type")
    private int type;//章节类型，普通章节值为0、开头的简介或者引言等值为1

    public static class Type{
        public static final int NORMAL = 0x0;
        public static final int INTRODUCE = 0x1;
    }


    @NonNull
    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(@NonNull String bookPath) {
        this.bookPath = bookPath;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public long getCurPosition() {
        return curPosition;
    }

    public void setCurPosition(long curPosition) {
        this.curPosition = curPosition;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "ReadChapter{" +
                "bookPath='" + bookPath + '\'' +
                "chapterName='" + chapterName + '\'' +
                ", curPosition=" + curPosition +
                ", percent=" + percent +
                ", type=" + type +
                '}';
    }
}
