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
    private static byte[] tempBuf = new byte[MAX_TEMP_BYTE_SIZE];

    private float[] offsetX;//每个字符的x偏移量
    private float[] offsetY;//每行的y偏移量
    private List<Integer> headIndexList;//段落中的首行的在段落中的索引
    private String paragraph;//段落完整内容
    private int firstCanDrawLine = 0;//在该页面下，该段落可以绘制的第一行索引
    private int lastCanDrawLine = -1;//在改页面下，改段落可以绘制的最后一行索引

    private TxtParagraph(String contentPara){
        this.paragraph = contentPara;
    }

    public static TxtParagraph createTxtParagraph(ReadRandomAccessFile readRandomAccessFile, int displayWidth, IReadSetting readSetting){
        TxtParagraph txtParagraph = null;
        try {
            readRandomAccessFile.read(tempBuf);
            String paragraphStr = getParagraphString(readRandomAccessFile, tempBuf);
            Log.e(TAG,"段落为="+paragraphStr);

            txtParagraph = new TxtParagraph(paragraphStr);
            CharCalculator.calcCharOffsetX(paragraphStr, displayWidth, readSetting, txtParagraph);

        }catch (Exception e){
            e.printStackTrace();
        }

        return txtParagraph;
    }

    private static String getParagraphString(ReadRandomAccessFile readRandomAccessFile, byte[] bytes) throws IOException{
        int count = 0;

        for (int i= 0; i < bytes.length; i++){
            if (bytes[i] == CharCalculator.RETURN_CHAR){//回车
                bytes[i] = CharCalculator.BLANK_CHAR;
            }else if (bytes[i] == CharCalculator.NEW_LINE_CHAR){//换行
                count = i + 1 ;
                break;
            }
        }
        readRandomAccessFile.setCurPosition( readRandomAccessFile.getCurPosition() + count);
        readRandomAccessFile.seek(readRandomAccessFile.getCurPosition());
        return new String(bytes,0,count, CodeUtil.getEncodingByCode(readRandomAccessFile.getCode()));
    }


    public float calculatorOffsetY(IReadSetting readSetting, float startOffsetY, int displayHeight){
        if (offsetX == null || headIndexList == null){
            return startOffsetY;
        }

        float[] offsetY = new float[headIndexList.size()];
        setOffsetY(offsetY);

        return CharCalculator.calcParagraphOffsetY(headIndexList, startOffsetY, displayHeight, readSetting, this);
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

    @Override
    public String toString() {
        return "TxtParagraph{" +
                "offsetX=" + Arrays.toString(offsetX) +
                ", headIndexList=" + getHeadIndexListToString() +
                ", paragraph='" + paragraph + '\'' +
                ", offsetY='" + Arrays.toString(offsetY) + '\'' +
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
