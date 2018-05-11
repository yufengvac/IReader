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

    }

    @Test
    public void tranlateFromTimeMillis() throws Exception{
        String result = BookHelper.tranFormFromTimeMillis(System.currentTimeMillis() - 5 * 60 *1000);
        System.out.println(result);

        String result1 = BookHelper.tranFormFromTimeMillis(System.currentTimeMillis() - 15 * 60 *1000);
        System.out.println(result1);

        String result2 = BookHelper.tranFormFromTimeMillis(System.currentTimeMillis() - 60 * 60 *1000);
        System.out.println(result2);

        String result3 = BookHelper.tranFormFromTimeMillis(System.currentTimeMillis() - 4 * 60 * 60 *1000);
        System.out.println(result3);

        String result4 = BookHelper.tranFormFromTimeMillis(System.currentTimeMillis() - 2*24 *60* 60 *1000);
        System.out.println(result4);

        String result5 = BookHelper.tranFormFromTimeMillis(System.currentTimeMillis() - 6*24 * 60 *1000);
        System.out.println(result5);
    }

    @Test
    public void transFormFromByte() throws Exception{
        String result = BookHelper.transFormFromByte(500 * 1024);
        System.out.println(result);
    }
}