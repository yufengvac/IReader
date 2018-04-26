/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.yufeng.ireader.reader.utils;

import android.os.Build;
import android.view.View;
import android.webkit.WebView;

import java.util.Locale;

/**
 * 判断当前手机是否支持硬件加速
 * <p/>
 * Created by LukeSkywalker on 2015/9/19.
 */
public class HardWareManager {

    public static void handleAcceleration(WebView webView) {
        if (!canOpenHardware()) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public static boolean canOpenHardware() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return false;
        }

        if (CpuUtil.isSamsungCpu()
                || "SM-N9006".equals(Build.MODEL)
                || "GT-I9300".equals(Build.MODEL)
                || "HM NOTE 1LTE".equals(Build.MODEL)
                || "M351".equals(Build.MODEL)
                || "Coolpad 5892".equals(Build.MODEL)
                || "5876".equals(Build.MODEL)
                || "MI 2A".equals(Build.MODEL)
                || "MI 2SC".equals(Build.MODEL)
                || "vivo S7t".equals(Build.MODEL)
                || "H30-T00".equals(Build.MODEL)
                || "R7t".equals(Build.MODEL)
                || "MX4 Pro".equals(Build.MODEL)
                || "mt6589".equals(Build.HARDWARE.toLowerCase(Locale.ENGLISH))
                || "HM2013022".equals(Build.DEVICE)
                || "X9077".equals(Build.MODEL)
                || "SM-A5000".equals(Build.MODEL)
                || "SM-A5009".equals(Build.MODEL)
                || "hwC8817D".equals(Build.DEVICE)
                || "MI PAD".equals(Build.MODEL)
                || "HUAWEI P7-L07".equals(Build.MODEL)
                || disableAllHardware()) {

            return false;
        }

        return true;
    }

    /**
     * 禁止所有情况(包括左右滑动翻页，上下滑动翻页等)下的硬件加速
     */
    public static boolean disableAllHardware() {
        if ("nubia".equals(Build.BRAND.toLowerCase())) {//中兴nubia
            return true;
        }
        return false;
    }

}
