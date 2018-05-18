package com.yufeng.ireader.db.readchapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yufeng on 2018/5/18-0018.
 *
 */

public class ReadChapterHelper {

    private static Map<String,List<ReadChapter>> chapterMap= new HashMap<>();

    /**
     * 获取某本书的数据库的全部目录
     * @param bookPath  书的路径
     * @return          该书籍的所有目录
     */
    public static List<ReadChapter> getAllReadChapterList(String bookPath){
        if (chapterMap.get(bookPath) != null){
            return chapterMap.get(bookPath);
        }
        try {
            List<ReadChapter> chapterList = ReadChapterDatabase.getInstance().getReadChapterDao().getCatalogList(bookPath)
                    .subscribeOn(Schedulers.io()).toFuture().get();
            chapterMap.put(bookPath, chapterList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return chapterMap.get(bookPath);
    }

    /**
     * 获取某本书的下一章位置
     * @param bookPath     书的路径
     * @param curPosition  当前位置
     * @return             -2是没找到； -1是最后一章无法翻到下一页； 其它正常
     */
    public static long getNextChapterPosition(String bookPath, long curPosition){
       List<ReadChapter> readChapterList = getAllReadChapterList(bookPath);
       if (readChapterList == null){
           return -2;
       }

       int index = 0;
       for (int i = 0; i < readChapterList.size() ; i++){
           if (readChapterList.get(i).getCurPosition() <= curPosition){
               index = i;
           }else {
               break;
           }
       }
       if (index < readChapterList.size()-1){
           return readChapterList.get(index+1).getCurPosition();
       }else if (index == readChapterList.size() -1){ //表示是最后一章了
           return -1;
       }
       return -2;
    }

    /**
     * 获取某本书的上一章位置
     * @param bookPath     书的路径
     * @param curPosition  当前位置
     * @return             -2是没找到；-1是第一章，无法翻到上一章；其它是正常
     */
    public static long getPreChapterPosition(String bookPath, long curPosition){
        List<ReadChapter> readChapterList = getAllReadChapterList(bookPath);
        if (readChapterList == null){
            return -2;
        }

        int index = 0;
        for (int i= 0; i < readChapterList.size() ; i ++){
            if (readChapterList.get(i).getCurPosition() <= curPosition){
                index = i;
            }else {
                break;
            }
        }
        if (index > 1){
            return readChapterList.get(index-1).getCurPosition();
        }else if (index == 0){//表示是第一章
            return -1;
        }
        return -2;
    }

    /**
     * 获取某本书的数据库下的目录总数，异步
     * @param path      书的路径
     * @param callback  回调
     */
    public static void getChapterCountAync(String path, final Callback<Integer> callback){
        ReadChapterDatabase.getInstance().getReadChapterDao().getChapterCount(path)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (callback != null){
                            callback.onCallback(integer);
                        }
                    }
                });
    }

    public interface Callback<T>{
        void onCallback(T t);
    }
}
