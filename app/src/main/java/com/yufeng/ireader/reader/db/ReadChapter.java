package com.yufeng.ireader.reader.db;

/**
 * Created by yufeng on 2018/5/7-0007.
 *
 */

public class ReadChapter {
    private String chapterName;
    private long curPosition;
    private float percent;

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

    @Override
    public String toString() {
        return "ReadChapter{" +
                "chapterName='" + chapterName + '\'' +
                ", curPosition=" + curPosition +
                ", percent=" + percent +
                '}';
    }
}
