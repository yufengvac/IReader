package com.yufeng.ireader.utils;

/**
 * Created by yufeng on 2018/4/18-0018.
 *
 */

public class DisplayConstant {
    public static int DISPLAY_WIDTH = 0;
    public static int DISPLAY_HEIGHT = 0;
    public static int STATUS_BAR_HEIGHT = 0;

    public static int DISPLAY_HEIGHT_SIMULATION = 0;
    public static void init(int width, int height){
        DISPLAY_WIDTH = width;
        DISPLAY_HEIGHT = height;
    }
    public static void initStatusBarHeight(int statusBarHeight){
        STATUS_BAR_HEIGHT = statusBarHeight;
    }

    public static void setDisplayHeightSimulation(int height){
        DISPLAY_HEIGHT_SIMULATION = height;
    }
}
