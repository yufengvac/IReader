package com.yufeng.ireader.reader.utils;

import android.graphics.Paint;

import com.yufeng.ireader.reader.bean.TxtParagraph;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/16-0016.
 * 计算每个char字符的偏移量
 */

public class CharCalculator {
    private static final char MAX_OFFSET = 3;//行首不符合规范的时候，往前往后寻找符合规范字符的最大范围
    private static final char CHINESE_START = '\u4e00'; //表示汉字起点
    private static final char CHINESE_END  = '\u9fa5'; //表示汉字终点
    public static final char RETURN_CHAR   = '\r';     //表示回车
    public static final char NEW_LINE_CHAR = '\n';     //表示换行，视作文段结束标识
    public static final char ILLEGAL_CHAR  = '\ufeff'; //表示非法字符
    public static final char BLANK_CHAR    = ' ';      //表示空格
    public static final char BLANK_CHINESE_CHAR    = '　';//表示全角空格

    public static void calcCharOffsetX(String paragrah, int displayWidth, IReadSetting readSetting, TxtParagraph txtParagraph){

        //行首的首个字符索引

        List<Integer>  headIndexList = new ArrayList<>();

        char[] blankChar = new char[]{'　'};

        float[] offsetXArr = new float[paragrah.length()];

        int indentCount = readSetting.getIndentCount();
        Paint contentPaint = readSetting.getContentPaint();

        //初始x偏移量
        float initialOffsetX = readSetting.getPaddingLeft() +
                (contentPaint.measureText(blankChar, 0, blankChar.length) + readSetting.getHorizontalExtra())*indentCount;

        int firstIndexNotBlank = getFirstIndexNotBlank(paragrah);
        if (firstIndexNotBlank >=0 && firstIndexNotBlank < offsetXArr.length){ //说明不是空行
            headIndexList.add(firstIndexNotBlank);
            offsetXArr[firstIndexNotBlank] = initialOffsetX; //设置第一个非空字符的偏移量
        }else {
            headIndexList.add(0);
        }


        float baseChineseCharWidth = contentPaint.measureText("我");
        float totalOffsetX = initialOffsetX;
        for (int i = 0; i < offsetXArr.length; i++){

            char desChar = paragrah.charAt(i);

            if (desChar == NEW_LINE_CHAR){//表示结束
                handleHeadIndexList(headIndexList, paragrah);
                txtParagraph.setHeadIndexList(headIndexList);
                txtParagraph.setOffsetX(offsetXArr);
                return;
            }


            float desCharWidth = getOneCharWidth(baseChineseCharWidth, contentPaint, desChar);

            float remainingWidth = displayWidth - totalOffsetX - desCharWidth  - readSetting.getPaddingRight();

            if (remainingWidth < 0) { //需要换行了
                int index = handleReturnByChar(paragrah, i , headIndexList, remainingWidth, baseChineseCharWidth, contentPaint, readSetting, offsetXArr, displayWidth);
                if (index == -1){ //说明index这个字符可以规范的放在下一行，那么重新计算一下这一行的偏移量（把多出来的空间均分给每个字符）
                    int curLineStartIndex = headIndexList.get(headIndexList.size()-1);
                    int curLineEndIndex = i - 1;
                    int curLineCount = curLineEndIndex - curLineStartIndex + 1;
//                    float oneLineRemainingAvgWidth = ( desCharWidth - totalOffsetX - readSetting.getPaddingRight() +readSetting.getHorizontalExtra() )/curLineCount;
                    float oneLineRemainingAvgWidth = (  displayWidth - totalOffsetX - readSetting.getPaddingRight() )/curLineCount;
                    int step = 0;
                    for (int j = curLineStartIndex ; j < curLineEndIndex ; j++){
                        offsetXArr[j] += oneLineRemainingAvgWidth ;
                        step++;
                    }
                }else {
                    i = index +1;
                }
                if (i >= offsetXArr.length){
                    break;
                }

                //新换一行肯定没有缩进
                totalOffsetX = readSetting.getPaddingLeft();
                offsetXArr[i] = totalOffsetX;

                char newLineChar = paragrah.charAt(i);
                if (index == -1 || newLineChar != NEW_LINE_CHAR && newLineChar != RETURN_CHAR && newLineChar!= 0){
                    headIndexList.add(i);
                }
            }


            if (isWord(desChar) && (((i + 1) < offsetXArr.length) && isWord(paragrah.charAt(i + 1)))) {
                totalOffsetX += desCharWidth;
            } else {
                totalOffsetX += (desCharWidth + readSetting.getHorizontalExtra());
            }

            if (i + 1 < offsetXArr.length){
                if (i + 1 < firstIndexNotBlank){
                    totalOffsetX = 0;
                }else if (i +1 == firstIndexNotBlank){
                    totalOffsetX = initialOffsetX ;
                }
                offsetXArr[i + 1] = totalOffsetX;
            }
        }

    }

    /**
     * 计算TxtParagraph里面的每行的Y轴偏移量
     * @param headIndexList    每行的头部索引集合
     * @param startOffsetY     该段落可绘制在屏幕的第一行的y坐标
     * @param displayHeight    View高度
     * @param readSetting      设置信息
     * @param txtParagraph     要计算的段落TxtParagraph
     * @return                 下一个可以绘制的屏幕的y坐标
     */
    public static float calcParagraphOffsetY( List<Integer> headIndexList, float startOffsetY, int displayHeight, IReadSetting readSetting, TxtParagraph txtParagraph){
        Paint.FontMetrics fontMetrics = readSetting.getContentPaint().getFontMetrics();
        float baseCharHeight = fontMetrics.descent - fontMetrics.ascent;

        float oneLineHeight = baseCharHeight + readSetting.getLineSpaceExtra();

        float[] offsetY = txtParagraph.getOffsetY();
        int lastCanDrawLine ;
        int firstCanDrawLine = txtParagraph.getFirstCanDrawLine();

        if (firstCanDrawLine >= headIndexList.size()){
            firstCanDrawLine = headIndexList.size() - 1;
            txtParagraph.setFirstCanDrawLine(firstCanDrawLine);
        }

        lastCanDrawLine = headIndexList.size() - 1;
        offsetY[firstCanDrawLine] = startOffsetY;

        for (int i = firstCanDrawLine+1 ; i < headIndexList.size(); i ++){

            float nextDrawLine = offsetY[i - 1] +oneLineHeight;
            if (nextDrawLine > displayHeight - readSetting.getPaddingBottom() - (fontMetrics.descent- fontMetrics.ascent)){
                lastCanDrawLine = i - 1;
                txtParagraph.setLastCanDrawLine(lastCanDrawLine);
                return nextDrawLine;
            }
            offsetY[i] = nextDrawLine;
        }
        txtParagraph.setLastCanDrawLine(lastCanDrawLine);
        return offsetY[lastCanDrawLine] + oneLineHeight;
    }

    /**
     * 同样是计算TxtParagraph里面的每行的y坐标，只不过是从底下往上计算
     * @param headIndexList     每行的头部索引集合
     * @param startOffsetY      该段落可绘制在屏幕的最后一行的y坐标
     * @param displayHeight     View高度
     * @param readSetting       设置信息
     * @param txtParagraph      要计算的TxtParagraph
     * @return                  y方向上还剩余的空间
     */
    public static float calcParagraphOffsetYReserve(List<Integer> headIndexList, float startOffsetY, int displayHeight, IReadSetting readSetting, TxtParagraph txtParagraph){
        Paint.FontMetrics fontMetrics = readSetting.getContentPaint().getFontMetrics();
        float baseCharHeight = fontMetrics.descent - fontMetrics.ascent;

        float oneLineHeight = baseCharHeight + readSetting.getLineSpaceExtra();

        float[] offsetY = txtParagraph.getOffsetY();

        int lastCanDrawLine = txtParagraph.getLastCanDrawLine();
        if (txtParagraph.getLastCanDrawLine() < 0){
            lastCanDrawLine = headIndexList.size() - 1;
            txtParagraph.setLastCanDrawLine(lastCanDrawLine);
        }

        int firstCanDrawLine = 0;

        offsetY[lastCanDrawLine] = startOffsetY;

        for (int i = lastCanDrawLine -1 ; i >= 0; i --){

            float preDrawLine = offsetY[i + 1] - oneLineHeight;
            if (preDrawLine < (readSetting.getPaddingTop()+ fontMetrics.descent - fontMetrics.ascent)){
                firstCanDrawLine = i + 1;
                txtParagraph.setFirstCanDrawLine(firstCanDrawLine);
                return preDrawLine;
            }
            offsetY[i] = preDrawLine;
        }
        txtParagraph.setFirstCanDrawLine(firstCanDrawLine);
        return offsetY[firstCanDrawLine] - oneLineHeight;
    }

    /**
     * 处理段落是空行的情况下
     * @param headIndexList 段落里面的头部索引
     */
    private static void handleHeadIndexList(List<Integer> headIndexList, String paragraph){
        if (headIndexList != null && headIndexList.size() > 1){
            int startIndex ;
            int endIndex = 0;
            for (int i = 0; i < headIndexList.size() ; i++){

                if (i == -1){
                    continue;
                }

                startIndex = (i == 0 ? headIndexList.get(0) : endIndex);
                if (i +1 < headIndexList.size()){
                    endIndex = headIndexList.get(i +1);
                }else {
                    endIndex = paragraph.length() - 1;
                }

                boolean isEmptyLine = true;
                for (int j = startIndex ; j < endIndex; j ++){
                    char curChar = paragraph.charAt(j);
                    if (curChar != BLANK_CHAR && curChar != NEW_LINE_CHAR && curChar != RETURN_CHAR && curChar != 0 && curChar != BLANK_CHINESE_CHAR){
                        isEmptyLine = false;
                        break;
                    }
                }

                if (isEmptyLine){
                    headIndexList.remove(i);
                    i--;
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
        }else if (desChar ==0 || desChar == ILLEGAL_CHAR){
            return 1;
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
            if (!(chars[i] == '　' || chars[i] == ' ' || chars[i] == 0x00
               || chars[i] == 0x00a0|| chars[i] == 0x0020|| (chars[i] >= 0x2002 && chars[i] <= 0x200D)
                    || chars[i] == 0x3000 || chars[i] == 0x202f || chars[i] == 0xfeff)){
                return i ;
            }
        }
        return -1;
    }

    /**
     * 是不是字符
     * 0x30 是数字0  0x7a是小写字母z
     * 参考ASCII码
     * @param c 检测的char
     * @return true false
     */
    private static boolean isWord(char c) {
        return ((c >= 0x30 && c <= 0x7a));
    }

    private static int handleReturnByChar(String paragrah, int index , List<Integer> headIndexList, float remainingWidth
    , float baseChineseWidth, Paint contentPaint, IReadSetting readSetting, float[] offsetXArr, int displayWidth){
        if (index == 0 && index == paragrah.length()-1){
            return -1;
        }
        char curChar = paragrah.charAt(index);
        if (isInvalidHeadChar(curChar) && isInvalidEndChar(paragrah.charAt(index-1))){
            return -1;
        }

        int startIndex = headIndexList.get(headIndexList.size()-1);
        int prevIndex = -1;
        int nextIndex = -1;
        for (int i = index - 1; i > (index - MAX_OFFSET) && i > startIndex; i--){
            if (isInvalidEndChar(paragrah.charAt(i)) &&(i -1 == startIndex || isInvalidHeadChar(paragrah.charAt(i+1)))){
                prevIndex = i;
                break;
            }
        }
        for (int i = index; i < (index + MAX_OFFSET) && i < paragrah.length() ; i++){
            if (isInvalidEndChar(paragrah.charAt(i)) && ((i + 1 == paragrah.length())|| isInvalidHeadChar(paragrah.charAt(i+1)))){
                nextIndex = i;
                break;
            }
        }
        if (prevIndex == nextIndex){
            return -1;
        }

        float punctuationWidth = 0f;
        float oneCharWidth;
        int replaceCharCount = 0;
        if (nextIndex != -1){

            for (int i = startIndex; i <= nextIndex ; i++){

                //计算从index到nextIndex字符还需要的宽度空间
                if (nextIndex > index){
                    remainingWidth -= (getOneCharWidth(baseChineseWidth, contentPaint, paragrah.charAt(i)) + readSetting.getHorizontalExtra());
                }

                //计算字符压缩成原本宽度的三分之一看节省出来的宽度
                oneCharWidth = getReplaceCharOffset(baseChineseWidth, contentPaint, paragrah.charAt(i));
                if (oneCharWidth != 0){
                    replaceCharCount ++;
                    punctuationWidth += oneCharWidth;
                }
            }
        }

        remainingWidth = Math.abs(remainingWidth);
        int type ;
        if (punctuationWidth >= remainingWidth){ //压缩字符之后多出来的空间 比所需要的空间要大，说明可以放得下
            type = 1;
        }else if (nextIndex != -1 && readSetting.getHorizontalExtra() > 1 && (punctuationWidth + (readSetting.getHorizontalExtra()-1)*(nextIndex - index -1))>= remainingWidth){
            //字符压缩一下，字间距压缩一下
            type = 2;
        }else {
            if (prevIndex == -1){
                return -1;
            }
            type = 3;
        }
        int realEndIndex;
        float charAddWidth = 0f;
        float xOffset = 0f;
        if (type == 1 || type == 2 ){
            realEndIndex = nextIndex;
        }else {
            realEndIndex = prevIndex;
            charAddWidth = (displayWidth - offsetXArr[realEndIndex + 1] - readSetting.getPaddingRight()) / (realEndIndex - startIndex);
        }
        int firstIndexNotBlank = getFirstIndexNotBlank(paragrah);
        for (int i = startIndex; i <= realEndIndex; i++){
            if (type == 3) {
                offsetXArr[i] += (i - startIndex) * charAddWidth;
            } else {
                if (type == 2) {
                    if (i + 1 <= realEndIndex && i + 1 < firstIndexNotBlank) {
                        continue;
                    }
                }
                if (offsetXArr[i] > readSetting.getPaddingLeft() + 1) {    // 首行缩进填充不需要改变
                    offsetXArr[i] -= xOffset;
                }
                oneCharWidth = getReplaceCharOffset(baseChineseWidth, contentPaint, paragrah.charAt(i));
                if (type == 2) {
                    xOffset += (remainingWidth - punctuationWidth) / (realEndIndex - startIndex );
                }
                if (oneCharWidth != 0) {
                    oneCharWidth -= (punctuationWidth - remainingWidth) / replaceCharCount;
                }
                xOffset += oneCharWidth;
            }
        }

        return realEndIndex;
    }

    /**
     * 以下这些不能在行首出现
     * @param c 要检测的字符char
     * @return true可以在行首出现 ；false不可以在行首出现
     */
    private static boolean isInvalidHeadChar(char c) {
        switch (c) {
            case '；':
            case ';':
            case '、':
            case '‘':
            case ':':
            case '：':
            case '。':
            case '，':
            case '？':
            case '！':
            case '”':
            case '）':
            case '}':
            case '》':
            case '%':
            case ',':
            case '?':
            case '!':
            case ')':
            case ']':
                return false;
            default:
                return true;
        }
    }

    /**
     * 以下这些不能在行尾出现
     * @param c  要检测的字符插入
     * @return true可以在行尾出现；false不可以在行尾出现
     */
    private static boolean isInvalidEndChar(char c) {
        switch (c) {
            case '、':
            case '’':
            case ':':
            case '：':
            case '{':
            case '（':
            case '《':
            case '“':
            case '(':
            case '[':
               return false;
            default:
                return true;
        }
    }

    /**
     * 如果c为下列字符，得到字符宽度的三分之一
     * @param baseChineseWidth 汉字宽度
     * @param paint 画笔
     * @param c 字符
     * @return  下列字符宽度的三分之一 其他字符不处理
     */
    private static float getReplaceCharOffset(float baseChineseWidth, Paint paint, char c) {
        char charReplace = 0;
        switch (c) {
            case '，':
                charReplace = ',';
                break;
            case '？':
                charReplace = '?';
                break;
            case '！':
                charReplace = '!';
                break;
            case '：':
                charReplace = ':';
                break;
            case '。':
                charReplace = '。';
                break;
            case '、':
                charReplace = '、';
                break;
            default:
                break;
        }
        if (charReplace != 0) {
            return getOneCharWidth(baseChineseWidth, paint, c) / 3;
        }
        return 0f;
    }
}
