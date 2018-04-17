package com.yufeng.ireader.reader.bean;

import android.util.Log;

import com.yufeng.ireader.reader.utils.CharCalculator;
import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;

import java.io.IOException;
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


    public static TxtParagraph createTxtParagraph(ReadRandomAccessFile readRandomAccessFile, int displayWidth, IReadSetting readSetting){
        TxtParagraph txtParagraph = null;
        try {
            readRandomAccessFile.read(tempBuf);
            String paragraphStr = getParagraphString(readRandomAccessFile, tempBuf);
            Log.e(TAG,"段落为="+paragraphStr);

            txtParagraph = new TxtParagraph();
            CharCalculator.calcCharOffsetX(paragraphStr, displayWidth, readSetting, txtParagraph.getOffsetX(), txtParagraph.getHeadIndexList());

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
        readRandomAccessFile.setCurPosition( readRandomAccessFile.getLocation() + count);
        readRandomAccessFile.seek(readRandomAccessFile.getCurPosition());
        return new String(bytes,0,count, CodeUtil.getEncodingByCode(readRandomAccessFile.getCode()));
    }


    public float calculatorOffsetY(IReadSetting readSetting, float startOffsetY){
        if (offsetX == null || headIndexList == null){
            return 0;
        }

        return CharCalculator.calcParagraphOffsetY();
    }

    public float[] getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float[] offsetX) {
        this.offsetX = offsetX;
    }

    public List<Integer> getHeadIndexList() {
        return headIndexList;
    }

    public void setHeadIndexList(List<Integer> headIndexList) {
        this.headIndexList = headIndexList;
    }
}
