package com.yufeng.ireader.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yufeng.ireader.base.ReadApplication;

/**
 * Created by yufeng on 2018/4/27.
 * 阅读器设置SharePreference类
 */

public class ReadPreferHelper {

    private SharedPreferences sp = null;
    private static final String SHARE_PREFRERFERENCES_NAME = "read_setting";

    private static final String IS_DAY_MODE = "is_day_mode";

    private ReadPreferHelper(){
        if (sp == null){
            sp = ReadApplication.baseApplication.getApplicationContext().getSharedPreferences(SHARE_PREFRERFERENCES_NAME, Context.MODE_PRIVATE);
        }
    }

    private static class ReadPreferHelperHolder {
        private static ReadPreferHelper instance = null;
        private static ReadPreferHelper init(){
            if (instance == null){
                instance = new ReadPreferHelper();
            }
            return instance;
        }
    }

    public static ReadPreferHelper getInstance(){
        return ReadPreferHelperHolder.init();
    }

    public boolean isDayMode(){
        return sp.getBoolean(IS_DAY_MODE, true);
    }
    public void setIsDayMode(boolean isDayMode){
        sp.edit().putBoolean(IS_DAY_MODE, isDayMode).apply();
    }
}
