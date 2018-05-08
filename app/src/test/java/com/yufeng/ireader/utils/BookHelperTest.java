package com.yufeng.ireader.utils;

import org.junit.Test;

/**
 * Created by yufeng on 2018/5/7-0007.
 *
 */
public class BookHelperTest {
    @Test
    public void isChapterParagraph() throws Exception {
       boolean result =  BookHelper.judgeChapter("第一卷 第一章 青云\n");
       System.out.print(result?"是标题":"不是标题");
    }

    @Test
    public void trim() throws Exception{
        String result = BookHelper.trim(null);
        System.out.println("结果是："+result+",长度是："+result.length());
    }

}