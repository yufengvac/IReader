package com.yufeng.ireader.db.base;

/**
 * Created by yufeng on 2018/5/10-0010.
 *
 */

public class DBConstants {


    private static final int DB_VERSION_1 = 1;

    /**
     * 书架书籍数据库
     */
    public static final String DB_NAME_BOOK = "book.db";
    public static final int DB_VERSION_BOOK = DB_VERSION_1;


    /**
     * 阅读历史记录数据库
     */
    public static final String DB_NAME_READ_HISTORY = "book_read_history.db";
    public static final int DB_VERSION_READ_TEXT_PARAGRAPH = DB_VERSION_1;


    /**
     * 书籍章节数据库
     */
    public static final String DB_NAME_CHAPTER = "book_chapter.db";
    public static final int DB_VERSION_CHAPTER = DB_VERSION_1;
}
