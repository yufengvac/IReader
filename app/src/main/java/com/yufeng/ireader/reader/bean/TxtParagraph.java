package com.yufeng.ireader.reader.bean;

import android.util.Log;

import com.yufeng.ireader.reader.utils.CharCalculator;
import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;

import java.io.IOException;

/**
 * Created by yufeng on 2018/4/16-0016.
 *
 */

public class TxtParagraph {

    private static final String TAG = TxtParagraph.class.getSimpleName();
    private static final int MAX_TEMP_BYTE_SIZE = 2000;
    private static byte[] tempBuf = new byte[MAX_TEMP_BYTE_SIZE];

    public TxtParagraph(){

    }

    public static TxtParagraph createTxtParagraph(ReadRandomAccessFile readRandomAccessFile, int displayWidth, IReadSetting readSetting){
        try {
            readRandomAccessFile.read(tempBuf);
            String paragraphStr = getParagraphString(readRandomAccessFile, tempBuf);
            Log.e(TAG,"段落为="+paragraphStr);
            CharCalculator.calcCharOffset(paragraphStr, displayWidth, readSetting);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new TxtParagraph();
    }

    private static String getParagraphString(ReadRandomAccessFile readRandomAccessFile, byte[] bytes) throws IOException{
        int count = 0;

        for (int i= 0; i < bytes.length; i++){
            if (bytes[i] == CharCalculator.ILLEGAL_CHAR){//非法字符去掉
                bytes[i] = CharCalculator.BLANK_CHAR;
            }else if (bytes[i] == CharCalculator.RETURN_CHAR){//回车
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
}
