package com.yufeng.ireader.reader.bean;

import java.util.List;

/**
 * Created by yufeng on 2018/4/15.
 * 段落
 */

public class Paragraph {
    private List<String> charList;
    private List<Integer> offestList;

    public List<String> getCharList() {
        return charList;
    }

    public void setCharList(List<String> charList) {
        this.charList = charList;
    }

    public List<Integer> getOffestList() {
        return offestList;
    }

    public void setOffestList(List<Integer> offestList) {
        this.offestList = offestList;
    }

    public static void createParagraph(){

    }
}
