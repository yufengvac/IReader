package com.yufeng.ireader.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by yufeng on 2018/5/7-0007.
 *
 */
public class BookHelperTest {
    @Test
    public void isChapterParagraph() throws Exception {
       boolean result =  BookHelper.isChapterParagraph("第一章");
       System.out.print(result?"是标题":"不是标题");
    }

}