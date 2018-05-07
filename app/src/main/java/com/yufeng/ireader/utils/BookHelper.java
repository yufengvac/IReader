package com.yufeng.ireader.utils;

import android.text.TextUtils;

import com.yufeng.ireader.ui.beans.Book;

import java.util.List;

/**
 * Created by yufeng on 2018/4/13-0013.
 *
 */

public class BookHelper {

    private static char[] CS_LINEENDCHARS = new char[] { '。', ':', ';', '」', '；', '：'};

    private static char[] CS_LEVEL = new char[] { '卷', '册', '部', '季', '集', '篇', '章', '回', '节', '弹' , '幕'};

    private static char[] CS_NUMBER1 = new char[] { '零', '一', '二', '两', '三', '四', '五', '六', '七', '八', '九', '十', '百', '千'};

    private static char[] CS_NUMBER2 = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };

    private static String[] CS_EXTERNAL = new String[] { "序言", "序幕", "绪论", "楔子", "书籍介绍", "序" };

    private static char CS_PREFIX = '第';

    public static boolean isChapterParagraph(String paragraph){
       return !TextUtils.isEmpty(paragraph) && paragraph.length() < 30 &&
               (paragraph.matches(".*第.{1,9}章.*") || paragraph.matches(".第.{1,9}回.")||
                       paragraph.matches(".第.{1,9}节.") || paragraph.matches(".第.{1,9}卷.*") ||
               paragraph.matches(".第一卷.*") || paragraph.matches(".第二卷.*") || paragraph.matches(".第三卷.*")||
               paragraph.matches(".第四卷.*") || paragraph.matches(".第五卷.*") || paragraph.matches(".第六卷.*"));
    }

    public static List<Book> getLocalBooksInDirectory(){
        return PathHelper.getBooksInDirectory();
    }

    public static boolean isChapter(String paragraph){
        return false;
    }
}
