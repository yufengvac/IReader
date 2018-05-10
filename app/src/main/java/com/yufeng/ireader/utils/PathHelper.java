package com.yufeng.ireader.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.yufeng.ireader.reader.bean.Chapter;
import com.yufeng.ireader.db.book.Book;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/11.
 *
 */

public class PathHelper {
    private static final String TAG = PathHelper.class.getSimpleName();
    private static final String APP_PATH = "aireader";
    private static final String IMG_PATH = "img";
    private static final String READER_COVER_PATH = "cover";
    private static final String READER_FONT_PATH = "font";
    private static final String ASSETS_READ_COVER = "readCover";
    private static final String[] ASSETS_BG_NAMES = new String[]{"read_skin_darkgray_bg.jpg", "read_skin_gray_bg.jpg", "read_skin_kraftpaper_bg.jpg"};

    /**
     * 创建项目根目录
     * @return true 成功； false 失败
     */
    private static boolean ensureRootPath() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File rootDirectory = new File(rootPath, APP_PATH);
            return rootDirectory.exists() || rootDirectory.mkdirs();
        }
        return false;
    }

    /**
     * 创建项目根目录下的某个子目录
     * @param directoryName 子目录名称
     * @return true 成功； false 失败
     */
    private static boolean ensureDirectoryPath(String directoryName){
        if (ensureRootPath()){
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_PATH;
            File fontDirectory = new File(rootPath, directoryName);
            return fontDirectory.exists() || fontDirectory.mkdirs();
        }
        return false;
    }

    /**
     * 获取根目录下的某个子目录完整路径
     * @param directoryName 子目录名称
     * @return 该子目录的完整路径
     */
    private static String getDirectoryPath(String directoryName){
        if (ensureDirectoryPath(directoryName)){
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_PATH + File.separator
                    + directoryName + File.separator;
        }
        return "";
    }


    public static String getImgPath() {
        return getDirectoryPath(IMG_PATH);
    }
    public static String getCoverPath(){
        return getDirectoryPath(READER_COVER_PATH);
    }
    private static String getFontPath(){
        return getDirectoryPath(READER_FONT_PATH);
    }

    /**
     * 获取默认的在assets里面的阅读器背景图片
     * @return 背景图片list
     */
    public static List<String> getDefaultReadBgInAssets(){
        List<String> readBgList = new ArrayList<>();
        String assetsPath = ASSETS_READ_COVER + File.separator;
        for (String name: ASSETS_BG_NAMES){
            readBgList.add(assetsPath + name);
        }
        return readBgList;
    }

    /**
     * 根据选项获取阅读器背景在sd卡下面的全路径
     * @param options 背景选项
     * @return 该背景选项在sd卡下的全路径
     */
    public static String getReadBgPathByOption(int options){
        if (options >=0 && options < ASSETS_READ_COVER.length()){
            return getCoverPath() + ASSETS_BG_NAMES[options];
        }
        return "";
    }

    /**
     * 获取根目录下"ireader"下的后缀为txt的文件
     *
     * @return List<Book>
     */
    public static List<Book> getBooksInDirectory() {
        ArrayList<Book> bookList = new ArrayList<>();
        if (ensureRootPath()) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), APP_PATH);
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return getExensionByName(pathname.getName()).toLowerCase().equals("txt");
                }
            });
            for (File bookFile : files) {
                String bookName = getBookNameByPath(bookFile.getAbsolutePath());
                Book book = Book.createBook(bookName, "", bookFile.getAbsolutePath(), bookFile.lastModified(), bookFile.length(), -1);
                if (book != null) {
                    bookList.add(book);
                }
            }
        }
        return bookList;
    }

    /**
     * 根据文件名字取文件后缀 . 以后的
     * @param name  文件路径名称带后缀的
     * @return 文件后缀
     */
    private static String getExensionByName(String name) {
        if (!TextUtils.isEmpty(name) && name.contains(".")) {
            return name.substring(name.lastIndexOf(".") + 1);
        }
        return "";
    }

    /**
     * 根据文件地址取 文件名字
     * @param path 文件地址
     * @return     文件名字 /和.之间的
     */
    public static String getBookNameByPath(String path) {
        int firstIndex = path.lastIndexOf("/");
        int lastIndex = path.lastIndexOf(".");
        return path.substring(firstIndex + 1, lastIndex);
    }

    /**
     * 获取带有后缀的文件名
     * @param path 文件地址
     * @return     文件名带有后缀
     */
    public static String getFileNameWithExtension(String path){
        int firstIndex = path.lastIndexOf("/");
        return path.substring(firstIndex + 1);
    }

    public static ArrayList<String> getContentByPath(String path) {
        BufferedReader bufferedReader = null;
        try {
            ArrayList<Chapter> chapterList = new ArrayList<>();
            if (TextUtils.isEmpty(path)) {
                Log.e("PathHelper", "path 为 null");
                return null;
            }
            File file = new File(path);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            ArrayList<String> contentList = new ArrayList<>();
            int curIndex = 0;
            StringBuilder chapterSb = new StringBuilder();
            ArrayList<String> paragraphList = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
//                line = CodeUtil.ToDBC(line);
                if (BookHelper.isChapterParagraph(line)) {

                    Chapter chapter = new Chapter();
                    chapter.setChapterName(line);
                    chapter.setChapterIndex(curIndex);
                    chapter.setType(Chapter.Type.NORMAL);
                    if (chapterList.size() > 0) {
                        chapterList.get(chapterList.size() - 1).setTotalContent(chapterSb.toString());
                        chapterList.get(chapterList.size() - 1).setParagraphList(paragraphList);
                    }
                    chapterList.add(chapter);
                    chapterSb = new StringBuilder();
                    paragraphList.clear();
                } else {
                    chapterSb.append(line).append("\n");
                    paragraphList.add(line);
                }

                if (curIndex == 0 && !BookHelper.isChapterParagraph(line)) {//文章第一段落不是标题，那都视作简介引言等
                    Chapter chapter = new Chapter();
                    chapter.setChapterName(getBookNameByPath(path));
                    chapter.setType(Chapter.Type.INTRODUCE);
                    chapterList.add(chapter);
                }

                curIndex++;
                contentList.add(line);

            }
            if (chapterList.size() > 1) {
                chapterList.get(chapterList.size() - 1).setTotalContent(chapterSb.toString());
                chapterList.get(chapterList.size() - 1).setParagraphList(paragraphList);
            }
            Log.e("PathHelper", "共有" + chapterList.size() + "章节");
            for (int i = 0; i < chapterList.size(); i++) {
                Log.e(TAG, chapterList.get(i).toString());
            }
            return contentList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean saveBitmapToSDCard(Bitmap bitmap, String fileName) {
        String path = getImgPath() + fileName + ".jpg";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);

            return bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}
