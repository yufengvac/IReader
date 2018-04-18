package com.yufeng.ireader.reader.bean;

import android.util.Log;
import android.util.SparseArray;

import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;

import java.io.IOException;

/**
 * Created by yufeng on 2018/4/18-0018.
 *
 */

public class PagerManager {
    private static final String TAG = PagerManager.class.getSimpleName();
    private SparseArray<Pager> pagerSparseArray ;
    private ReadRandomAccessFile readRandomAccessFile;

    private PagerManager(){
        if (pagerSparseArray == null){
            pagerSparseArray = new SparseArray<>();
        }
    }

    private static class PagerManagerHolder {
        private static PagerManager INSTANCE  = null;
        private static PagerManager getINSTANCE(){
            if (INSTANCE == null){
                INSTANCE = new PagerManager();
            }
            return INSTANCE;
        }
    }

    public static PagerManager getInstance(){
        return PagerManagerHolder.getINSTANCE();
    }


    public void initPagers(IReadSetting readSetting, String path){
        if (pagerSparseArray == null){
            pagerSparseArray = new SparseArray<>();
        }
        if (pagerSparseArray.get(0) == null){
            initReadRandomAccessFile(path);

            pagerSparseArray.setValueAt(0,Pager.createNextPager(readSetting, readRandomAccessFile));

        }
    }

    public void drawNextPager(){

    }

    private void initReadRandomAccessFile(String path) {
        try {
            if (readRandomAccessFile == null) {
                readRandomAccessFile = new ReadRandomAccessFile(path, "r");
                int code = CodeUtil.regCode(path);
                Log.i(TAG, "字符编码是：" + CodeUtil.getEncodingByCode(code));
                readRandomAccessFile.setCode(code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
