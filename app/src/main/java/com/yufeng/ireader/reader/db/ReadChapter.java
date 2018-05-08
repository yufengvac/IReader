package com.yufeng.ireader.reader.db;

/**
 * Created by yufeng on 2018/5/7-0007.
 *
 */

public class ReadChapter {
    private String chapterName;
    private long curPosition;
    private float percent;
    private int type;//章节类型，普通章节值为0、开头的简介或者引言等值为1

    public static class Type{
        public static final int NORMAL = 0x0;
        public static final int INTRODUCE = 0x1;
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
                "chapterName='" + chapterName + '\'' +
                ", curPosition=" + curPosition +
                ", percent=" + percent +
                ", type=" + type +
                '}';
    }
}
