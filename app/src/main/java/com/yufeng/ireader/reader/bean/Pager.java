package com.yufeng.ireader.reader.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisplayConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/18-0018.
 *
 */

public class Pager {
    private static final String TAG = Pager.class.getSimpleName();

    private List<TxtParagraph> txtParagraphList = new ArrayList<>();


    /***
     * 画出当前Pager里面的txtParagraphList段落
     * @param canvas canvas
     * @param paint  画笔
     */
    public void drawTxtParagraph(Canvas canvas, Paint paint){
        if (txtParagraphList != null && txtParagraphList.size() > 0){
            for (int i =0 ; i < txtParagraphList.size(); i++){
                TxtParagraph txtParagraph = txtParagraphList.get(i);
                txtParagraph.drawTxtParagraph(canvas, paint);
            }
        }
    }


    static Pager createNextPager(IReadSetting readSetting, ReadRandomAccessFile readRandomAccessFile){
        Pager pager = new Pager();
        int displayWidth = DisplayConstant.DISPLAY_WIDTH;
        int displayHeight= DisplayConstant.DISPLAY_HEIGHT;
        try {


            Paint.FontMetrics fontMetrics = readSetting.getContentPaint().getFontMetrics();
            float startOffsetY = readSetting.getPaddingTop() + fontMetrics.bottom - fontMetrics.top;
            List<TxtParagraph> drawTxtParaList = new ArrayList<>();
            while (true){
                TxtParagraph txtParagraph = TxtParagraph.createTxtParagraph(readRandomAccessFile, displayWidth, readSetting);
                drawTxtParaList.add(txtParagraph);

                startOffsetY =  txtParagraph.calculatorOffsetY(readSetting, startOffsetY, displayHeight);

                Log.i(TAG,"startOffsetY = "+startOffsetY);
                if (startOffsetY >= displayHeight - readSetting.getPaddingBottom()){
                    Log.e(TAG,"页面已经全部获取完了");
                    break;
                }
            }
            if (pager.txtParagraphList == null){
                pager.txtParagraphList = new ArrayList<>();
            }else {
                pager.txtParagraphList.clear();
            }
            pager.txtParagraphList.addAll(drawTxtParaList);
        }catch (Exception e){
            e.printStackTrace();
        }

        return pager;
    }
}
