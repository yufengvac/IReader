package com.yufeng.ireader.reader.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisplayConstant;

import java.util.ArrayList;
import java.util.Collections;
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
    public void drawTxtParagraph(Canvas canvas, Paint paint){
        if (txtParagraphList != null && txtParagraphList.size() > 0 ){
            for (int i =0 ; i < txtParagraphList.size(); i++){
                TxtParagraph txtParagraph = txtParagraphList.get(i);
                txtParagraph.drawTxtParagraph(canvas, paint);
            }
            TxtParagraph lastTxtParagraph = txtParagraphList.get(txtParagraphList.size()-1);
            if (lastTxtParagraph.getLastCanDrawLine() + 1 >= lastTxtParagraph.getHeadIndexList().size()){//表示该段落可以绘制完
                lastTxtParagraph.setCanDrawCompleted(true);
            }else {
                lastTxtParagraph.setCanDrawCompleted(false);//表示该段落不能完全绘制完，返回最后那个段落的绘制的最后一行
            }
        }
    }


    static Page createNextPager(TxtParagraph lastPagerTxtParagraph, int lastCanDrawLine, IReadSetting readSetting, ReadRandomAccessFile readRandomAccessFile, boolean forcedAdd){
        Page pager = new Page();
        int displayWidth = DisplayConstant.DISPLAY_WIDTH;
        int displayHeight= DisplayConstant.DISPLAY_HEIGHT;
        try {
            Paint.FontMetrics fontMetrics = readSetting.getContentPaint().getFontMetrics();
            float startOffsetY = readSetting.getPaddingTop() +  (fontMetrics.descent - fontMetrics.ascent);
            Log.e(TAG,"startOffsetY="+startOffsetY);

            List<TxtParagraph> drawTxtParaList = new ArrayList<>();
            boolean needCalcNewTxtParagraph = false;
            long startSeek = 0;

            if (lastPagerTxtParagraph != null){
                if (! lastPagerTxtParagraph.isCanDrawCompleted()){
                    needCalcNewTxtParagraph = true;
                    lastPagerTxtParagraph.setFirstCanDrawLine(lastCanDrawLine+1);
                }else {
                    startSeek = lastPagerTxtParagraph.getSeekEnd() + 1;
                }
            }
            if (forcedAdd && lastPagerTxtParagraph != null){
                needCalcNewTxtParagraph = true;
            }

            TxtParagraph txtParagraph = lastPagerTxtParagraph;
            while (true){
                if ( !needCalcNewTxtParagraph ){
                     txtParagraph = TxtParagraph.createTxtParagraphBySeekStart(readRandomAccessFile, displayWidth, readSetting, startSeek);
                }

                startSeek = txtParagraph.getSeekEnd() + 1;
                drawTxtParaList.add(txtParagraph);

                startOffsetY =  txtParagraph.calculatorOffsetY(readSetting, startOffsetY, displayHeight, txtParagraph.getOffsetY());
                needCalcNewTxtParagraph = false;

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
            for (TxtParagraph txtParagraph1: pager.txtParagraphList){
                Log.i(TAG,"最终该下个页面page的所有内容为："+txtParagraph1.toString());
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return pager;
    }
    static Page createPrePager(TxtParagraph startPagerTxtParagraph, int startCanDrawLine, IReadSetting readSetting, ReadRandomAccessFile readRandomAccessFile){
        Page page = new Page();

        int displayWidth = DisplayConstant.DISPLAY_WIDTH;
        int displayHeight= DisplayConstant.DISPLAY_HEIGHT;
        try {
            Paint.FontMetrics fontMetrics = readSetting.getContentPaint().getFontMetrics();
            float startOffsetY = displayHeight - readSetting.getPaddingBottom() - (fontMetrics.descent - fontMetrics.ascent) - readSetting.getLineSpaceExtra();
            Log.e(TAG,"startOffsetY="+startOffsetY);

            List<TxtParagraph> drawTxtParaList = new ArrayList<>();
            boolean needCalcNewTxtParagraph = true;
            long endSeek = 0;

            if (startCanDrawLine == 0){//说明这个page的第一段startPagerTxtParagraph可以完全绘制完成
                needCalcNewTxtParagraph = false;
                endSeek = startPagerTxtParagraph.getSeekStart() - 1;

                if (endSeek <= 0){//已经是第一章了
                    return null;
                }
            }else {
                startPagerTxtParagraph.setFirstCanDrawLine(0);
                startPagerTxtParagraph.setLastCanDrawLine(startCanDrawLine - 1);
            }


            TxtParagraph txtParagraph = startPagerTxtParagraph;
            while (true){
                if ( !needCalcNewTxtParagraph ){
                    txtParagraph = TxtParagraph.createTxtParagraphBySeekEnd(readRandomAccessFile, displayWidth, readSetting, endSeek);
                }

                endSeek = txtParagraph.getSeekStart() - 1 ;
                drawTxtParaList.add(txtParagraph);

                startOffsetY =  txtParagraph.calculatorOffsetYReserve(readSetting, startOffsetY, displayHeight, txtParagraph.getOffsetY());
                needCalcNewTxtParagraph = false;

                Log.i(TAG,"startOffsetYReserve = "+startOffsetY);
                if (startOffsetY <= (readSetting.getPaddingTop()+ fontMetrics.descent - fontMetrics.ascent) || endSeek <= CodeUtil.getBeginOffset(readRandomAccessFile.getCode())){
                    Log.e(TAG,"前一页面已经全部获取完了");
                    break;
                }
            }
            if (page.txtParagraphList == null){
                page.txtParagraphList = new ArrayList<>();
            }else {
                page.txtParagraphList.clear();
            }
            Collections.reverse(drawTxtParaList);
            page.txtParagraphList.addAll(drawTxtParaList);

            //重新对前一页的y轴偏移量进行计算，使得第一行始终保持相同
            int firstTxtCanDrawLine = page.getFirstTxtParagraph().getFirstCanDrawLine();
            float[] pageFirstStartOffsetYArray = page.getFirstTxtParagraph().getOffsetY();
            float pageFirstStartOffsetY = pageFirstStartOffsetYArray[firstTxtCanDrawLine];
            float deviation = pageFirstStartOffsetY - (readSetting.getPaddingTop() + fontMetrics.descent - fontMetrics.ascent);
            for (TxtParagraph txtParagraph1: page.txtParagraphList){
                Log.i(TAG,"最终该前个页面page的所有内容为："+txtParagraph1.toString());
                float[] oneOffsetY = txtParagraph1.getOffsetY();
                for (int i = txtParagraph1.getFirstCanDrawLine() ; i <= txtParagraph1.getLastCanDrawLine(); i++){
                    oneOffsetY[i] -= deviation;
                }
                txtParagraph1.setOffsetY(oneOffsetY);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return page;
    }

    /**
     * 获取page对象里面的段落集合的第一条
     * @return txtParagraphList的第一条
     */
    public TxtParagraph getFirstTxtParagraph(){
        if (txtParagraphList != null && txtParagraphList.size() > 0){
            return txtParagraphList.get(0);
        }
        return null;
    }

    /**
     * 获取page对象里面的段落集合的最后一条
     * @return txtParagraphList的最后一条
     */
    public TxtParagraph getLastTxtParagraph(){
        if (txtParagraphList != null && txtParagraphList.size() > 0){
            return txtParagraphList.get(txtParagraphList.size()-1);
        }
        return null;
    }

}
