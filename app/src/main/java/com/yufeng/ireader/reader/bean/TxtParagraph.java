package com.yufeng.ireader.reader.bean;

import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;

/**
 * Created by yufeng on 2018/4/16-0016.
 *
 */

public class TxtParagraph {


    public TxtParagraph(){

    }

    public static TxtParagraph createTxtParagraph(ReadRandomAccessFile readRandomAccessFile){
        return new TxtParagraph();
    }
}
