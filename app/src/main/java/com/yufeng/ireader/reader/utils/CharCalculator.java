package com.yufeng.ireader.reader.utils;

import android.graphics.Paint;

import com.yufeng.ireader.reader.viewinterface.IReadSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/16-0016.
 * 计算每个char字符的偏移量
 */

public class CharCalculator {
    private static final int CHINESE_START = '\u4e00'; //表示汉字起点
    private static final int CHINESE_END  = '\u9fa5'; //表示汉字终点
    public static final int RETURN_CHAR   = '\r';     //表示回车
    public static final int NEW_LINE_CHAR = '\n';     //表示换行，视作文段结束标识
    public static final int ILLEGAL_CHAR  = '\ufeff'; //表示非法字符
    public static final int BLANK_CHAR    = ' ';      //表示空格

    public static void calcCharOffset(String paragrah, int displayWidth, IReadSetting readSetting){

        //行首的首个字符索引
        List<Integer> headIndexList = new ArrayList<>();

        char[] blankChar = new char[]{' '};

        char[] paraChars = paragrah.toCharArray();

        int indentCount = readSetting.getIndentCount();
        Paint contentPaint = readSetting.getContentPaint();

        //初始x偏移量
        float initialOffsetX = readSetting.getPaddingLeft() +
                (contentPaint.measureText(blankChar, 0, blankChar.length) + readSetting.getHorizontalExtra())*indentCount;

        int firstIndexNotBlank = getFirstIndexNotBlank(paragrah);
        if (firstIndexNotBlank != -1){ //说明不是空行
            headIndexList.add(0);
        }

        float[] offsetX = new float[paragrah.length()];

        float baseChineseCharWidth = contentPaint.measureText("我");
        for (int i = 0; i < paraChars.length; i++){

            char desChar = paraChars[i];

            switch (desChar){
                case NEW_LINE_CHAR:
                    break;
            }

            if (desChar != NEW_LINE_CHAR){
                float prevOffsetX = 0f;
                if (i > 0){
                    if (i <= firstIndexNotBlank){
                        prevOffsetX = 0;
                    }else {
                        prevOffsetX = offsetX[i-1] + getOneCharWidth(baseChineseCharWidth, contentPaint, paraChars[i-1]) + readSetting.getHorizontalExtra();
                    }
                }
                if (i < firstIndexNotBlank){
                    offsetX[i] = 0f;
                }else if (i == firstIndexNotBlank){
                    offsetX[i] = prevOffsetX + initialOffsetX ;
                }else {
                    offsetX[i] = prevOffsetX;
                }

                float remainingWidth = displayWidth - offsetX[i] - readSetting.getPaddingRight();

                if (remainingWidth < 0){ //需要换行了

                }
            }
        }

    }

    /**
     * 要判断这里是汉字还是其他字符，汉字直接返回baseChineseCharWidth
     * 其它字符先测量之后再返回
     * @param baseChineseCharWidth 一个汉字的宽度
     */
    private static float getOneCharWidth(float baseChineseCharWidth, Paint contentPaint, char desChar){
        if (desChar >= CHINESE_START && desChar <= CHINESE_END){
            return baseChineseCharWidth;
        }else {
            return contentPaint.measureText(String.valueOf(desChar));
        }
    }


    /**
     * 得到字符串中第一次出现非空字符串的位置
     * @param str 要检测的字符串
     * @return    位置 -1说明是空行
     */
    public static int getFirstIndexNotBlank(String str){
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++){
            if (!(chars[i] == '　' || chars[i] == ' ')){
                return i ;
            }
        }
        return -1;
    }
}
