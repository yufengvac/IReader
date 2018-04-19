package com.yufeng.ireader.reader.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisplayConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/18-0018.
 *
 */

public class Page {
    private static final String TAG = Page.class.getSimpleName();

    private List<TxtParagraph> txtParagraphList = new ArrayList<>();


    /***
     * 画出当前Pager里面的txtParagraphList段落
     * @param canvas canvas
     * @param paint  画笔
     */
    public int drawTxtParagraph(Canvas canvas, Paint paint){
        if (txtParagraphList != null && txtParagraphList.size() > 0){
            for (int i =0 ; i < txtParagraphList.size(); i++){
                TxtParagraph txtParagraph = txtParagraphList.get(i);
                txtParagraph.drawTxtParagraph(canvas, paint);
            }
            TxtParagraph lastTxtParagraph = txtParagraphList.get(txtParagraphList.size()-1);
            if (lastTxtParagraph.getLastCanDrawLine() + 1 >= lastTxtParagraph.getHeadIndexList().size()){//表示该段落可以绘制完
                return -1;
            }else {
                return txtParagraphList.get(txtParagraphList.size()-1).getLastCanDrawLine();//表示该段落不能完全绘制完，返回最后那个段落的绘制的最后一行
            }
        }
        return -1;
    }


    static Page createPager(TxtParagraph lastPagerTxtParagraph, int lastCanDrawLine, IReadSetting readSetting, ReadRandomAccessFile readRandomAccessFile){
        Page pager = new Page();
        int displayWidth = DisplayConstant.DISPLAY_WIDTH;
        int displayHeight= DisplayConstant.DISPLAY_HEIGHT;
        try {
            Paint.FontMetrics fontMetrics = readSetting.getContentPaint().getFontMetrics();
            float startOffsetY = readSetting.getPaddingTop() -  fontMetrics.ascent ;
            Log.e(TAG,"startOffsetY="+startOffsetY);

            List<TxtParagraph> drawTxtParaList = new ArrayList<>();
            boolean needCalcNewTxtParagraph = true;

            if (lastPagerTxtParagraph != null){
                lastPagerTxtParagraph.setFirstCanDrawLine(lastCanDrawLine+1);
                needCalcNewTxtParagraph = false;
            }

            TxtParagraph txtParagraph = lastPagerTxtParagraph;
            while (true){
                if ( needCalcNewTxtParagraph ){
                     txtParagraph = TxtParagraph.createTxtParagraph(readRandomAccessFile, displayWidth, readSetting);
                }

                drawTxtParaList.add(txtParagraph);

                startOffsetY =  txtParagraph.calculatorOffsetY(readSetting, startOffsetY, displayHeight);
                needCalcNewTxtParagraph = true;

                Log.i(TAG,"startOffsetY = "+startOffsetY);
                if (startOffsetY >= displayHeight - readSetting.getPaddingBottom() -( fontMetrics.descent- fontMetrics.ascent)){
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

    public List<TxtParagraph> getTxtParagraphList() {
        return txtParagraphList;
    }
}
