/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.yufeng.ireader.reader.utils;

import android.os.Build;

import java.io.FileInputStream;
import java.util.Locale;
import java.util.Scanner;

/**
 * 判断当前手机型号
 * <p/>
 * Created by LukeSkywalker on 2015/3/18.
 */
public class CpuUtil {
    private static final boolean mIsSamsungCpu;

    static {
        boolean isSamsungCpu = false;
        Scanner cpuInfoScanner = null;

        try {
            cpuInfoScanner = new Scanner(new FileInputStream("/proc/cpuinfo"));

            String hardWare1 = Build.HARDWARE.toLowerCase(Locale.ENGLISH);
            String board = Build.BOARD.toLowerCase(Locale.ENGLISH);

            String hardWare2 = "";
            String modelName = "";
            while (cpuInfoScanner.hasNextLine()) {
                if (cpuInfoScanner.findInLine("model name\t:") != null) {
                    modelName = cpuInfoScanner.nextLine();
                    modelName = modelName == null ? "" : modelName;
                } else if (cpuInfoScanner.findInLine("Hardware\t:") != null) {
                    hardWare2 = cpuInfoScanner.nextLine();
                    hardWare2 = hardWare2 == null ? "" : hardWare2.toLowerCase(Locale.ENGLISH);
                }
                cpuInfoScanner.nextLine();
            }

            if (hardWare1.contains("universal") || hardWare1.contains("smdk") || board.contains("smdk") || board.contains("manta") || hardWare2.contains("universal")
                    || modelName.contains("smdk") || modelName.contains("manta") || modelName.contains("universal")) {
                isSamsungCpu = true;
            } else {
                isSamsungCpu = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSamsungCpu = false;
        } finally {
            if (cpuInfoScanner != null) {
                cpuInfoScanner.close();
            }
            mIsSamsungCpu = isSamsungCpu;
        }
    }

    public static boolean isSamsungCpu() {
        return mIsSamsungCpu;
    }
}
