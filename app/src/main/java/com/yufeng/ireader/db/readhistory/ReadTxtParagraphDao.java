package com.yufeng.ireader.db.readhistory;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by yufeng on 2018/4/24-0024.
 *
 */

@Dao
public interface ReadTxtParagraphDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertReadBookHistory(ReadTxtParagraph readTxtParagraph);

    @Query("SELECT * FROM book_read_history WHERE book_path = :bookPath ORDER BY last_read_time")
    Single<List<ReadTxtParagraph>> getAllReadBookHistory(String bookPath);

    @Query("DELETE FROM book_read_history WHERE book_path = :bookPath AND last_read_time <= :deleteLastTime")
    int deleteReadBookHistory(String bookPath, long deleteLastTime);
}
