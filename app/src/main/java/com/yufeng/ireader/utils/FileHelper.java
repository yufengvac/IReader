package com.yufeng.ireader.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yufeng on 2018/4/24-0024.
 *
 */

public class FileHelper {

    public static void copyToSDFromAssets(Context context, String assetsPath, String directoryName){
        try {
            InputStream in = context.getAssets().open(assetsPath);
            File file = new File(directoryName, PathHelper.getFileNameWithExension(assetsPath));
            if (file.exists()){
                return;
            }else {
                boolean result = file.createNewFile();
                if (!result){
                    return;
                }
            }
            FileOutputStream fs = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) != -1) {
                fs.write(buffer, 0, n);
            }
            fs.flush();
            in.close();
            fs.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
