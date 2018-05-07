package com.yufeng.ireader.reader.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2018/5/7-0007.
 *
 */
public class CharCalculatorTest {
    @Test
    public void getFirstIndexNotBlank() throws Exception {
        int result = CharCalculator.getFirstIndexNotBlank("   你哈");
        System.out.print(result);
    }

}