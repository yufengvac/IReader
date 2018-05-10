package com.yufeng.ireader.ui.home.callback;

import com.yufeng.ireader.db.book.Book;

import java.util.List;

/**
 * Created by yufeng on 2018/5/10-0010.
 *
 */

public interface OnBookQueryListener {
    void onBookQuery(List<Book> bookList);
}
