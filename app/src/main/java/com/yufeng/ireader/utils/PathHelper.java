package com.yufeng.ireader.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.yufeng.ireader.reader.bean.Chapter;

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
    private static final String TAG = PathHelper.class.getSimpleName();
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
    private static String getBookNameByPath(String path){
        int firstIndex = path.lastIndexOf("/");
        int lastIndex = path.lastIndexOf(".");
        return path.substring(firstIndex+1,lastIndex);
    }

    public static ArrayList<String> getContentByPath(String path){
        try {
            ArrayList<Chapter> chapterList = new ArrayList<>();
            if (TextUtils.isEmpty(path)){
                Log.e("PathHelper","path 为 null");
                return null;
            }
            File file = new File(path);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            ArrayList<String> contentList = new ArrayList<>();
            int startIndex = 0;
            StringBuilder chapterSb = new StringBuilder();
            while ((line = bufferedReader.readLine())!=null){

                if (BookHelper.isChapterParagraph(line)){

                    Chapter chapter = new Chapter();
                    chapter.setChapterName(line);
                    chapter.setChapterIndex(startIndex);
                    chapter.setType(Chapter.Type.NORMAL);
                    if (chapterList.size() > 0){
                        chapterList.get(chapterList.size()-1).setTotalContent(chapterSb.toString());
                    }
                    chapterList.add(chapter);
                    chapterSb = new StringBuilder();
                }else {
                    chapterSb.append(line).append("\n");
                }

                if (startIndex == 0 && !BookHelper.isChapterParagraph(line)){//文章第一段落不是标题，那都视作简介引言等
                    Chapter chapter = new Chapter();
                    chapter.setChapterName(getBookNameByPath(path));
                    chapter.setType(Chapter.Type.INTRODUCE);
                    chapterList.add(chapter);
                }

                startIndex++;
                contentList.add(line);

            }
            if (chapterList.size() > 1){
                chapterList.get(chapterList.size()-1).setTotalContent(chapterSb.toString());
            }
            Log.e("PathHelper","共有"+chapterList.size()+"章节");
            for (int i = 0 ; i < chapterList.size(); i++){
                Log.e(TAG,chapterList.get(i).toString());
            }
            return contentList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
