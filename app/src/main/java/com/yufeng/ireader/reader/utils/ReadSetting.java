package com.yufeng.ireader.reader.utils;

import com.yufeng.ireader.reader.viewinterface.IReadSetting;

/**
 * Created by Administrator on 2018/4/16-0016.
 *
 */

public class ReadSetting implements IReadSetting {
    @Override
    public int getPaddingLeft() {
        return 45;
    }

    @Override
    public int getPaddingTop() {
        return 45;
    }

    @Override
    public int getPaddingRight() {
        return 45;
    }

    @Override
    public int getPaddingBottom() {
        return 45;
    }

    @Override
    public int getLineSpaceExtra() {
        return 10;
    }

    @Override
    public int getHorizontalExtra() {
        return 10;
    }
}
