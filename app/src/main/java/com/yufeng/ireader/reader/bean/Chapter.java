package com.yufeng.ireader.reader.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by yufeng on 2018/4/13-0013.
 *
 */

public class Chapter {
    private String chapterName;//章节名，实际上是章节的那一个段落
    private String totalContent;//整个章节的内容
    private int chapterIndex;//章节名在整个小说里面的位置index
    private int type;//章节类型，普通章节值为0、开头的简介或者引言等值为1
    private List<String> paragraphList;//段落列表集合

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

    public String getTotalContent() {
        return totalContent;
    }

    public void setTotalContent(String totalContent) {
        if (!TextUtils.isEmpty(totalContent)){
            this.totalContent = totalContent;
        }else {
            this.totalContent = "";
        }
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getParagraphList() {
        return paragraphList;
    }

    public void setParagraphList(List<String> paragraphList) {
        this.paragraphList = paragraphList;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "chapterName='" + chapterName + '\'' +
                ", totalContent='" + (totalContent !=null && totalContent.length()>30?totalContent.substring(0,30):(totalContent!=null?totalContent:"")) + '\'' +
                ", chapterIndex='" + chapterIndex + '\'' +
                ", type ='" + type + '\'' +
                ", paragraphList.size ='" + paragraphList.size() + '\'' +
                '}';
    }
}
