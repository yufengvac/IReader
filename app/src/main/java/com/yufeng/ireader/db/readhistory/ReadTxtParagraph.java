package com.yufeng.ireader.db.readhistory;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yufeng.ireader.reader.bean.TxtParagraph;

/**
 * Created by yufeng on 2018/4/24-0024.
 *
 */
@Entity(tableName = "book_read_history")
public class ReadTxtParagraph {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "book_path")
    private String bookPath = "";

    @ColumnInfo(name = "book_name")
    private String bookName;

    @ColumnInfo(name = "size")
    private long size;

    @ColumnInfo(name = "read_percent")
    private float readPercent;

    @ColumnInfo(name = "last_read_time")
    private long lastReadTime;

    @ColumnInfo(name = "txt_paragraph")
    private String txtParagraph;

    @ColumnInfo(name = "first_can_draw_line")
    private int firstCanDrawLine;

    @ColumnInfo(name = "last_can_draw_line")
    private int lastCanDrawLine;

    @ColumnInfo(name = "seek_start")
    private long seekStart;

    @ColumnInfo(name = "seek_end")
    private long seekEnd;

    @ColumnInfo(name = "is_can_be_draw_completed")
    private boolean isCanbeDrawCompleted;

    @ColumnInfo(name = "offset_x")
    private String offsetX;

    @ColumnInfo(name = "offset_y")
    private String offsetY;

    @ColumnInfo(name = "head_index")
    private String headIndex;

    @NonNull
    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(@NonNull String bookPath) {
        this.bookPath = bookPath;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public float getReadPercent() {
        return readPercent;
    }

    public void setReadPercent(float readPercent) {
        this.readPercent = readPercent;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public String getTxtParagraph() {
        return txtParagraph;
    }

    public void setTxtParagraph(String txtParagraph) {
        this.txtParagraph = txtParagraph;
    }

    public int getFirstCanDrawLine() {
        return firstCanDrawLine;
    }

    public void setFirstCanDrawLine(int firstCanDrawLine) {
        this.firstCanDrawLine = firstCanDrawLine;
    }

    public int getLastCanDrawLine() {
        return lastCanDrawLine;
    }

    public void setLastCanDrawLine(int lastCanDrawLine) {
        this.lastCanDrawLine = lastCanDrawLine;
    }

    public long getSeekStart() {
        return seekStart;
    }

    public void setSeekStart(long seekStart) {
        this.seekStart = seekStart;
    }

    public long getSeekEnd() {
        return seekEnd;
    }

    public void setSeekEnd(long seekEnd) {
        this.seekEnd = seekEnd;
    }

    public boolean isCanbeDrawCompleted() {
        return isCanbeDrawCompleted;
    }

    public void setCanbeDrawCompleted(boolean canbeDrawCompleted) {
        isCanbeDrawCompleted = canbeDrawCompleted;
    }

    public String getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(String offsetX) {
        this.offsetX = offsetX;
    }

    public String getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(String offsetY) {
        this.offsetY = offsetY;
    }

    public String getHeadIndex() {
        return headIndex;
    }

    public void setHeadIndex(String headIndex) {
        this.headIndex = headIndex;
    }

    public static ReadTxtParagraph createReadTxtParagraph(String bookName, String bookPath, long size, float readPercent, TxtParagraph txtParagraph){
        ReadTxtParagraph readTxtParagraph = new ReadTxtParagraph();
        readTxtParagraph.setBookName(bookName);
        readTxtParagraph.setBookPath(bookPath);
        readTxtParagraph.setSize(size);
        readTxtParagraph.setReadPercent(readPercent);
        readTxtParagraph.setLastReadTime(System.currentTimeMillis());
        readTxtParagraph.setTxtParagraph(txtParagraph.getParagraph());
        readTxtParagraph.setFirstCanDrawLine(txtParagraph.getFirstCanDrawLine());
        readTxtParagraph.setLastCanDrawLine(txtParagraph.getLastCanDrawLine());
        readTxtParagraph.setSeekStart(txtParagraph.getSeekStart());
        readTxtParagraph.setSeekEnd(txtParagraph.getSeekEnd());
        readTxtParagraph.setCanbeDrawCompleted(txtParagraph.isCanDrawCompleted());
        readTxtParagraph.setOffsetX(TxtParagraph.arrayToString(txtParagraph.getOffsetX()));
        readTxtParagraph.setOffsetY(TxtParagraph.arrayToString(txtParagraph.getOffsetY()));
        readTxtParagraph.setHeadIndex(TxtParagraph.listToString(txtParagraph.getHeadIndexList()));
        return readTxtParagraph;
    }

    public static TxtParagraph backToTxtParagraph(ReadTxtParagraph readTxtParagraph){
        long seekStart = readTxtParagraph.getSeekStart();
        long seekEnd = readTxtParagraph.getSeekEnd();
        String paragraph = readTxtParagraph.getTxtParagraph();
        TxtParagraph txtParagraph = new TxtParagraph(paragraph, seekStart, seekEnd);
        txtParagraph.setFirstCanDrawLine(readTxtParagraph.getFirstCanDrawLine());
        txtParagraph.setLastCanDrawLine(readTxtParagraph.getLastCanDrawLine());
        txtParagraph.setOffsetX(TxtParagraph.stringToArray(readTxtParagraph.getOffsetX()));
        txtParagraph.setOffsetY(TxtParagraph.stringToArray(readTxtParagraph.getOffsetY()));
        txtParagraph.setHeadIndexList(TxtParagraph.stringToList(readTxtParagraph.getHeadIndex()));
        txtParagraph.setCanDrawCompleted(txtParagraph.getLastCanDrawLine() + 1 == txtParagraph.getHeadIndexList().size());
        return txtParagraph;
    }

    @Override
    public String toString() {
        return "ReadTxtParagraph{" +
                "bookPath='" + bookPath + '\'' +
                ", bookName='" + bookName + '\'' +
                ", size=" + size +
                ", readPercent=" + readPercent +
                ", lastReadTime=" + lastReadTime +
                ", txtParagraph='" + txtParagraph + '\'' +
                ", firstCanDrawLine=" + firstCanDrawLine +
                ", lastCanDrawLine=" + lastCanDrawLine +
                ", seekStart=" + seekStart +
                ", seekEnd=" + seekEnd +
                ", isCanbeDrawCompleted=" + isCanbeDrawCompleted +
                ", offsetX='" + (TextUtils.isEmpty(offsetX)?"":offsetX) + '\'' +
                ", offsetY='" + (TextUtils.isEmpty(offsetY)?"":offsetY) + '\'' +
                ", headIndex='" + (TextUtils.isEmpty(headIndex)?"":headIndex) + '\'' +
                '}';
    }
}
