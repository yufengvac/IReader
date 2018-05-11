package com.yufeng.ireader.db.book;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by yufeng on 2018/5/10-0010.
 * z
 */

@Dao
public interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertBook(Book book);

    @Query("SELECT * FROM book ORDER BY last_read_time DESC")
    Single<List<Book>> getAllBookList();

    @Query("UPDATE book SET last_read_time = :lastReadTime WHERE book_path = :bookPath")
    long updateBookLastReadTime(String bookPath, long lastReadTime);

    @Query("UPDATe book SET read_percent = :readPercent WHERE book_path = :bookPath")
    long updateBookReadPercent(String bookPath, float readPercent);
}
