package com.yufeng.ireader.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yufeng.ireader.base.ReadApplication;
import com.yufeng.ireader.reader.utils.ReadExteriorConstants;

/**
 * Created by yufeng on 2018/4/27.
 * 阅读器设置SharePreference类
 */

public class ReadPreferHelper {

    private SharedPreferences sp = null;
    private static final String SHARE_PREFRERFERENCES_NAME = "read_setting";

    private static final String KEY_IS_DAY_MODE = "is_day_mode";
    private static final String KEY_TYPE_FACE = "type_face";
    private static final String KEY_TEXT_SIZE = "text_size";

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
        return sp.getBoolean(KEY_IS_DAY_MODE, true);
    }
    public void setIsDayMode(boolean isDayMode){
        sp.edit().putBoolean(KEY_IS_DAY_MODE, isDayMode).apply();
    }


    public void setTypeface(int typeface){
        sp.edit().putInt(KEY_TYPE_FACE, typeface).apply();
    }
    public int getTypeface(){
        return sp.getInt(KEY_TYPE_FACE, ReadExteriorConstants.ReadTypeFace.TYPEFACE_DEFAULT);
    }

    public void setFontTextSize(int textSize){
        sp.edit().putInt(KEY_TEXT_SIZE, textSize).apply();
    }

    public int getFontTextSize(){
        return sp.getInt(KEY_TEXT_SIZE, ReadExteriorConstants.DEFAULT_TEXT_SIZE);
    }

}
