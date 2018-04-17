package com.yufeng.ireader.reader.bean;

import android.graphics.Paint;
import android.util.Log;

import com.yufeng.ireader.reader.utils.CharCalculator;
import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.view.ReadView;
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

    private float[] offsetX;
    private List<Integer> headIndexList;
    private String paragraph;

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


    public float calculatorOffsetY(IReadSetting readSetting, float startOffsetY, int displayHeight, ReadView readView){
        if (offsetX == null || headIndexList == null){
            return -1f;
        }
        Paint.FontMetrics fontMetrics = readSetting.getContentPaint().getFontMetrics();
        float baseLineHeight = fontMetrics.bottom - fontMetrics.top;
        int calcResult = CharCalculator.calcParagraphOffsetY(headIndexList, startOffsetY, displayHeight, readSetting);
        readView.setCurLineCut(calcResult);
        if (calcResult == -1){
            return headIndexList.size() * ( baseLineHeight + readSetting.getLineSpaceExtra());
        }else if (calcResult == 0){
            return -1;
        }else if (calcResult > 0){
            return calcResult * (baseLineHeight + readSetting.getLineSpaceExtra());
        }else {
            return -1;
        }
    }

    public float[] getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float[] offsetX) {
        this.offsetX = offsetX;
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

    @Override
    public String toString() {
        return "TxtParagraph{" +
                "offsetX=" + Arrays.toString(offsetX) +
                ", headIndexList=" + getHeadIndexListToString() +
                ", paragraph='" + paragraph + '\'' +
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
