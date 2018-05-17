package com.yufeng.ireader.reader.viewinterface;

import com.yufeng.ireader.db.readchapter.ReadChapter;

import java.util.List;

/**
 * Created by yufeng on 2018/5/7-0007.
 *
 */

public interface OnChapterSplitListener {
    final class ERROR_MSG{
        public static final String PATH_NULL = "book path is null";
        public static final String BOOK_NULL = "book file is not exist";
    }
    void onError(String msg);
    void onCompleted(List<ReadChapter> readChapterList);
    void onSplitting(float percent);
}
