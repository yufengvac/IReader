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
    private static final String KEY_THEME_OPTION = "theme_option";
    private static final String KEY_THEME_BG_COLOR = "theme_bg_color";
    private static final String KEY_THEME_BG_IMG = "theme_bg_img";
    private static final String KEY_PAGE_TURN_TYPE = "page_turn_type";
    private static final String KEY_IMMERSIVE_READ = "immersive_read";

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


    public void setThemeOption(int option){
        sp.edit().putInt(KEY_THEME_OPTION, option).apply();
    }
    public int getThemeOption(){
        return sp.getInt(KEY_THEME_OPTION, ReadExteriorConstants.ThemeOption.COLOR);
    }


    public void setThemeColor(String color){
        sp.edit().putString(KEY_THEME_BG_COLOR, color).apply();
    }
    public String getThemeColor(){
        return sp.getString(KEY_THEME_BG_COLOR,ReadExteriorConstants.ThemeBgColor.COLOR_1);
    }

    public void setThemeImg(int imgOption){
        sp.edit().putInt(KEY_THEME_BG_IMG, imgOption).apply();
    }
    public int getThemeImg(){
        return sp.getInt(KEY_THEME_BG_IMG, ReadExteriorConstants.ThemeBgImg.IMG_GRAY);
    }


    public void setPageTurnType(int pageTurnType){
        sp.edit().putInt(KEY_PAGE_TURN_TYPE, pageTurnType).apply();
    }
    public int getPageTurnType(){
        return sp.getInt(KEY_PAGE_TURN_TYPE, ReadExteriorConstants.PageTurnType.PAGE_TURN_COVERAGE);
    }


    public void setIsImmersiveRead(boolean isImmersiveRead){
        sp.edit().putBoolean(KEY_IMMERSIVE_READ,isImmersiveRead).apply();
    }
    public boolean getImmersiveRead(){
        return sp.getBoolean(KEY_IMMERSIVE_READ, true);
    }
}
