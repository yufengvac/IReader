package com.yufeng.ireader.reader.utils;

import android.text.TextUtils;

import com.yufeng.ireader.reader.db.ReadChapter;
import com.yufeng.ireader.reader.viewinterface.OnChapterSplitListener;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by yufeng on 2018/5/8-0008.
 *
 */

public class ChapterUtil {

    private static final char EMPTY_CHAR_1 = ' ';
    private static final char EMPTY_CHAR_2 = '　';

    private static char[] CS_LINEENDCHARS = new char[] { '。', ':', ';', '」', '；', '：'};

    private static char[] CS_LEVEL = new char[] { '卷', '册', '部', '季', '集', '篇', '章', '回', '节', '弹' , '幕'};

    private static char[] CS_NUMBER1 = new char[] { '零', '一', '二', '两', '三', '四', '五', '六', '七', '八', '九', '十', '百', '千'};

    private static char[] CS_NUMBER2 = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };

    private static String[] CS_EXTERNAL = new String[] { "序言", "序幕", "绪论", "楔子", "书籍介绍", "序" , "内容简介", "内容介绍", "书籍简介","前文"};

    private static char CS_PREFIX = '第';


    private static long seekStart;
    private static String paragraph;
    private static long totalLength;
    private static List<ReadChapter> readChapterList;
    private static OnChapterSplitListener onChapterSplitListener;

    public static void prepareStartSplitChapter(long curSeek, String para, long totalSize, List<ReadChapter> readChapterLists, OnChapterSplitListener listener){
        seekStart = curSeek;
        paragraph = para;
        totalLength = totalSize;
        readChapterList = readChapterLists;
        onChapterSplitListener = listener;
    }

    public static void reset(){
        seekStart = 0;
        paragraph = "";
        totalLength = 0;
        readChapterList = null;
        onChapterSplitListener = null;
    }

    public static void startSplitChapter(){
        String para = trim(paragraph);
        if (TextUtils.isEmpty(para)){
            return;
        }
        splitIntroduction();
        splitChapter(para);
    }


    public static void splitChapter(String paragraph){
        if (paragraph.length() >= 35){//多于35个字符长度一律不视作标题
            return;
        }
        int prefixIndex = paragraph.indexOf(CS_PREFIX);    //找到“第”的位置
        if (prefixIndex != -1){
            if (prefixIndex <= paragraph.length() - 2){//预留2个字符
                splitPrefixSecondChapter(paragraph, prefixIndex);//有“第”字，解析下个字符
            }
        }else {
            splitNotPrefixChapter(paragraph);//没有“第”字，解析章节
        }
    }

    /**
     * 解析“第”字之后的字符，判断是不是后面紧接着CS_NUMBER1或者CS_NUMBER2等字符
     * @param paragraph  解析的字符串
     * @param index      “第”字的位置
     */
    private static void splitPrefixSecondChapter(String paragraph, int index){
        int curIndex = -1;
        for (int i = index +1 ; i < paragraph.length(); i++){
            char ch = paragraph.charAt(i);
            if (ch == EMPTY_CHAR_1 || ch == EMPTY_CHAR_2){
                curIndex = i;
                continue;
            }
            if (isContainInCharArray(ch, CS_NUMBER1) || isContainInCharArray(ch, CS_NUMBER2)){
                curIndex = i;
                continue;
            }
            break;
        }
        if (curIndex != -1){
            if (curIndex <paragraph.length() - 2){
                splitPrefixThirdChapter(paragraph, curIndex);
            }
        }

    }

    /**
     * 解析数字之后的字符，判断是不是后面紧接着CS_LEVEL字符
     * @param paragraph  解析的字符串
     * @param index      数字之后的字符
     */
    private static void splitPrefixThirdChapter(String paragraph, int index){
        int curIndex = -1;
        for (int i = index + 1 ; i < paragraph.length(); i++){
            char ch = paragraph.charAt(i);
            if (ch == EMPTY_CHAR_1 || ch == EMPTY_CHAR_2){
                curIndex = i;
                continue;
            }
            if (isContainInCharArray(ch, CS_LEVEL)){
                curIndex = i;
                continue;
            }
            break;
        }
        if (curIndex != -1){
            if (paragraph.length() - curIndex <= 20){ //章节标题最多20个字
                createNormalChapter(paragraph);
            }
        }
    }

    private static void splitNotPrefixChapter(String paragraph){

    }

    /**
     * 检查某个char字符是不是在chars数组里面
     * @param ch     需要检测的字符
     * @param chars  比对的字符数组
     * @return       true 包含； false不包含
     */
    private static boolean isContainInCharArray(char ch, char[] chars){
        if (chars == null){
            return false;
        }
        for (char ch1 : chars){
            if (ch1 == ch){
                return true;
            }
        }
        return false;
    }

    public static void splitIntroduction(){
        if (seekStart*1.0f / totalLength <= 0.1f && readChapterList.size() == 0){//先判断序言部分
            for (String external: CS_EXTERNAL){
                if (paragraph.contains(external)){
                    createIntroduction(external);
                }
            }
        }
    }


    private static void createIntroduction(String introduction){
        ReadChapter readChapter = new ReadChapter();
        readChapter.setChapterName(introduction);
        readChapter.setType(ReadChapter.Type.INTRODUCE);
        readChapter.setCurPosition(0);
        readChapter.setPercent(0f);
        readChapterList.add(readChapter);
        if (onChapterSplitListener != null){
            onChapterSplitListener.onSplitting(0f);
        }
    }

    private static void createNormalChapter(String chapterContent){
        ReadChapter readChapter = new ReadChapter();

        int lastIndex = chapterContent.lastIndexOf("\n");
        if (lastIndex != 0){
            chapterContent = chapterContent.substring(0, lastIndex);
        }
        readChapter.setChapterName(chapterContent);
        readChapter.setType(ReadChapter.Type.NORMAL);
        readChapter.setCurPosition(seekStart);
        float percent = seekStart * 1.0f / totalLength;
        readChapter.setPercent(percent);
        readChapterList.add(readChapter);
        if (onChapterSplitListener != null){
            onChapterSplitListener.onSplitting(Float.valueOf(new DecimalFormat("0.00").format(percent)));
        }
    }

    /**
     * 去除字符串头部和尾部的空格
     * @param str  需要去除头尾空格的字符串
     * @return     去除头尾空格后的字符串
     */
    public static String trim(String str){
        if (str == null){
            return "";
        }
        char emptyChar = ' ';
        char emptyChar1 = '　';
        int startIndex;
        int endIndex;

        for (startIndex = 0; startIndex < str.length(); startIndex++){
            if (str.charAt(startIndex) != emptyChar && str.charAt(startIndex) != emptyChar1){
                break;
            }
        }

        if (startIndex >= str.length() -1){
            return "";
        }

        for (endIndex = str.length() -1; endIndex >= startIndex; endIndex --){
            if (str.charAt(endIndex) != emptyChar && str.charAt(endIndex) != emptyChar1){
                break;
            }
        }
        if (endIndex < startIndex){
            return "";
        }
        if (startIndex != 0 || endIndex != str.length()-1){
            return str.substring(startIndex, endIndex + 1);
        }
        return str;
    }
}
