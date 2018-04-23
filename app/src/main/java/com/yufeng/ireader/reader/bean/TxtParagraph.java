package com.yufeng.ireader.reader.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.yufeng.ireader.reader.utils.CharCalculator;
import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yufeng on 2018/4/16-0016.
 *
 */

public class TxtParagraph {

    private static final String TAG = TxtParagraph.class.getSimpleName();
    private static final int MAX_TEMP_BYTE_SIZE = 1<<17;

    private float[] offsetX;//每个字符的x偏移量
    private float[] offsetY;//每行的y偏移量
    private List<Integer> headIndexList;//段落中的首行的在段落中的索引
    private String paragraph;//段落完整内容
    private int firstCanDrawLine = 0;//在该页面下，该段落可以绘制的第一行索引
    private int lastCanDrawLine = -1;//在改页面下，改段落可以绘制的最后一行索引
    private long seekStart = 0;
    private long seekEnd = 0;
    private boolean isCanDrawCompleted;//能否被完全绘制完

    private TxtParagraph(String contentPara, long seekStart, long seekEnd){
        this.paragraph = contentPara;
        this.seekStart = seekStart;
        this.seekEnd = seekEnd;
    }

    /**
     * 正序进行文件的读取
     * @param readRandomAccessFile readRandomAccessFile
     * @param displayWidth         view宽度
     * @param readSetting          设置信息
     * @param seekStart            开始读取的位置
     * @return                     TxtParagraph
     */
    public static TxtParagraph createTxtParagraphBySeekStart(ReadRandomAccessFile readRandomAccessFile, int displayWidth, IReadSetting readSetting, long seekStart){
        TxtParagraph txtParagraph = null;
        try {

            readRandomAccessFile.setCurPosition(seekStart);

            byte[] tempBuf = new byte[MAX_TEMP_BYTE_SIZE];
            readRandomAccessFile.read(tempBuf);
            String paragraphStr = getParagraphString(readRandomAccessFile,seekStart, tempBuf);

            txtParagraph = new TxtParagraph(paragraphStr, seekStart, readRandomAccessFile.getCurPosition()-1);
            Log.e(TAG,"段落为="+txtParagraph);
            CharCalculator.calcCharOffsetX(paragraphStr, displayWidth, readSetting, txtParagraph);

        }catch (Exception e){
            e.printStackTrace();
        }

        return txtParagraph;
    }

    /**
     * 倒序进行文件的读取，所以传的为下一段落的头读取位置
     * @param readRandomAccessFile readRandomAccessFile
     * @param displayWidth         view宽度
     * @param readSetting          设置信息
     * @param seekEnd              下一段落的头读取位置，即这一段落读取的结束位置
     * @return                      txtParagraph
     */
    public static TxtParagraph createTxtParagraphBySeekEnd(ReadRandomAccessFile readRandomAccessFile, int displayWidth, IReadSetting readSetting, long seekEnd){
        TxtParagraph txtParagraph = null;
        try {
            byte[] tempBuf = new byte[MAX_TEMP_BYTE_SIZE];
            String paragraphStr = getParagraphStringReverse(readRandomAccessFile, seekEnd, tempBuf);
            Log.e(TAG,"逆序读取字节，段落为="+paragraphStr);
            txtParagraph = new TxtParagraph(paragraphStr, readRandomAccessFile.getCurPosition() + 2, seekEnd);
            CharCalculator.calcCharOffsetX(paragraphStr, displayWidth, readSetting, txtParagraph);
        }catch (Exception e){
            e.printStackTrace();
        }
        return txtParagraph;
    }

    private static String getParagraphString(ReadRandomAccessFile readRandomAccessFile,long seekStart, byte[] bytes) throws IOException{
        int count = 0;

        for (int i= 0; i < bytes.length; i++){
            if (bytes[i] == CharCalculator.RETURN_CHAR){//回车
                bytes[i] = CharCalculator.BLANK_CHAR;
            }else if (bytes[i] == CharCalculator.NEW_LINE_CHAR){//换行
                count = i + 1 ;
                break;
            }
        }
        readRandomAccessFile.setCurPosition( seekStart + count);
        return new String(bytes,0,count, CodeUtil.getEncodingByCode(readRandomAccessFile.getCode()));
    }

    private static String getParagraphStringReverse(ReadRandomAccessFile readRandomAccessFile, long seekEnd, byte[] bytes) throws IOException{
//        int startSeek = 0;
//        for (int i = bytes.length -1; i >=0 ; i --){
//            if (bytes[i] == CharCalculator.RETURN_CHAR){
//                bytes[i] = CharCalculator.BLANK_CHAR;
//            }else if (bytes[i] == CharCalculator.NEW_LINE_CHAR){
//                startSeek = i + 1;
//                break;
//            }
//        }
//
//        readRandomAccessFile.setCurPosition(startSeek);
//        return new String(bytes, startSeek, bytes.length - startSeek, CodeUtil.getEncodingByCode(readRandomAccessFile.getCode()));
        int count = bytes.length -1;
        readRandomAccessFile.setCurPosition(seekEnd);
        byte curChar = 0;

        int num = 0;

        while ((curChar != CharCalculator.NEW_LINE_CHAR || num <= 1 ) && seekEnd >=0){
            curChar = (byte) readRandomAccessFile.read();

            seekEnd -- ;
            readRandomAccessFile.setCurPosition(seekEnd);
            if (curChar == CharCalculator.RETURN_CHAR){
                curChar = CharCalculator.BLANK_CHAR;
            }
            bytes[count] = curChar;
            count--;
            num ++;
        }
        return new String(bytes, count+2, num -1, CodeUtil.getEncodingByCode(readRandomAccessFile.getCode()));
    }


    public float calculatorOffsetY(IReadSetting readSetting, float startOffsetY, int displayHeight, float[] offsetY){
        if (offsetX == null || headIndexList == null){
            return startOffsetY;
        }
        if (offsetY == null){
            offsetY = new float[headIndexList.size()];
            setOffsetY(offsetY);
        }

        return CharCalculator.calcParagraphOffsetY(headIndexList, startOffsetY, displayHeight, readSetting, this);
    }

    public float calculatorOffsetYReserve(IReadSetting readSetting, float startOffsetY, int displayHeight, float[] offsetY){
        if (offsetX == null || headIndexList == null){
            return startOffsetY;
        }
        if (offsetY == null){
            offsetY = new float[headIndexList.size()];
            setOffsetY(offsetY);
        }
        return CharCalculator.calcParagraphOffsetYReserve(headIndexList, startOffsetY, displayHeight, readSetting, this);
    }


    public void drawTxtParagraph(Canvas canvas, Paint contentPaint){
        float[] offsetX = getOffsetX();
        float[] offsetY = getOffsetY();
        List<Integer> headIndexList = getHeadIndexList();
        Log.e(TAG,"绘制段落->"+getParagraph()+"，firstCanDrawLine="+firstCanDrawLine+",lastCanDrawLine="+lastCanDrawLine+"，该段落最多有"+headIndexList.size()+"行");
        for (int i = firstCanDrawLine ;i <= lastCanDrawLine; i++){
            int startIndex = headIndexList.get(i);
            int endIndex;
            if ( i+1 <= lastCanDrawLine){
                endIndex = headIndexList.get(i+1);
            }else {
                if (lastCanDrawLine+1 < headIndexList.size()){
                    endIndex = headIndexList.get(lastCanDrawLine + 1);
                }else {
                    endIndex = getParagraph().length() ;
                }
            }
            float drawBaseLineY = offsetY[i];
            for (int j = startIndex; j < endIndex; j++){
                canvas.drawText(this.getParagraph(), j, j+1, offsetX[j],drawBaseLineY,contentPaint);
            }
        }
    }

    public float[] getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float[] offsetX) {
        this.offsetX = offsetX;
    }

    public float[] getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float[] offsetY) {
        this.offsetY = offsetY;
    }

    public void setHeadIndexList(List<Integer> headIndexList) {
        this.headIndexList = headIndexList;
    }

    public List<Integer> getHeadIndexList() {
        return headIndexList;
    }

    public String getParagraph() {
        return paragraph;
    }

    public int getFirstCanDrawLine() {
        return firstCanDrawLine;
    }

    public void setFirstCanDrawLine(int firstCanDrawLine) {
        this.firstCanDrawLine = firstCanDrawLine;
    }

    public int getLastCanDrawLine() {
        return lastCanDrawLine;
    }

    public void setLastCanDrawLine(int lastCanDrawLine) {
        this.lastCanDrawLine = lastCanDrawLine;
    }

    public long getSeekEnd() {
        return seekEnd;
    }

    public long getSeekStart() {
        return seekStart;
    }

    public boolean isCanDrawCompleted() {
        return isCanDrawCompleted;
    }

    public void setCanDrawCompleted(boolean canDrawCompleted) {
        isCanDrawCompleted = canDrawCompleted;
    }

    @Override
    public String toString() {
        return "TxtParagraph{" +
                "offsetX=" + Arrays.toString(offsetX) +
                ", headIndexList=" + getHeadIndexListToString() +
                ", paragraph='" + paragraph + '\'' +
                ", offsetY='" + Arrays.toString(offsetY) + '\'' +
                ", seekStart='" + seekStart + '\'' +
                ", seekEnd='" + seekEnd + '\'' +
                ", firstCanDrawLine='" + firstCanDrawLine + '\'' +
                ", lastCanDrawLine='" + lastCanDrawLine + '\'' +
                '}';
    }

    private String getHeadIndexListToString(){
        if (headIndexList != null){
            StringBuilder stringBuilder = new StringBuilder();
            for (Integer index : headIndexList){
                stringBuilder.append(index).append(",");
            }
            return stringBuilder.toString();
        }
        return "";
    }
}
