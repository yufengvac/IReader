package com.yufeng.ireader.utils;

import android.text.TextUtils;

/**
 * Created by yufeng on 2018/4/13-0013.
 *
 */

public class BookHelper {

    public static boolean isChapterParagraph(String paragraph){
       return (!TextUtils.isEmpty(paragraph) && paragraph.length() < 30 &&
               (paragraph.matches(".*第.{1,8}章.*") || paragraph.matches(".第.{1,9}回.")|| paragraph.matches(".第.{1,9}节.")));
    }
}
