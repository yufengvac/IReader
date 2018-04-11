package com.yufeng.ireader.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by yufeng on 2018/4/11.
 *
 */

public class PathHelper {
    private static final String APP_PATH = "ireader";
    public static boolean ensurePath(){

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File rootDirectory = new File(rootPath,APP_PATH);
            return rootDirectory.exists() || rootDirectory.mkdirs();
        }
        return false;
    }

    public static String getBookPath(){
        if (ensurePath()){
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), APP_PATH);
            File[] files  = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return getExensionByName(pathname.getName()).toLowerCase().equals("txt");
                }
            });
            if (files.length > 0){
                return files[0].getAbsolutePath();
            }
        }
        return "";
    }

    private static String getExensionByName(String name){
        if (!TextUtils.isEmpty(name)&&name.contains(".")){
            return name.substring(name.lastIndexOf(".")+1);
        }
        return "";
    }

    public static ArrayList<String> getContentByPath(String path){
        try {
            File file = new File(path);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            ArrayList<String> contentList = new ArrayList<>();
            while ((line = bufferedReader.readLine())!=null){
                contentList.add(line);
            }
            return contentList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
