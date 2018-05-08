package com.yufeng.ireader.utils;

import android.text.TextUtils;

import com.yufeng.ireader.ui.beans.Book;

import java.util.List;

/**
 * Created by yufeng on 2018/4/13-0013.
 *
 */

public class BookHelper {

    public static boolean isChapterParagraph(String paragraph){
       return !TextUtils.isEmpty(paragraph) && paragraph.length() < 30 && judgeChapter(paragraph);
    }

    public static boolean judgeChapter(String paragraph){
        return paragraph.matches(".*第.{1,9}章.*\\n") || paragraph.matches(".第.{1,9}回.\\n")||
                paragraph.matches(".第.{1,9}节.\\n") || paragraph.matches(".第.{1,9}卷.*\\n") ||
                paragraph.matches(".第一卷.*\\n") || paragraph.matches(".第二卷.*\\n") || paragraph.matches(".第三卷.*\\n")||
                paragraph.matches(".第四卷.*\\n") || paragraph.matches(".第五卷.*\\n") || paragraph.matches(".第六卷.*\\n");
    }

    public static List<Book> getLocalBooksInDirectory(){
        return PathHelper.getBooksInDirectory();
    }

}
