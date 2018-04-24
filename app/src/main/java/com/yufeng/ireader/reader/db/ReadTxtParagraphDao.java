package com.yufeng.ireader.reader.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import io.reactivex.Single;

/**
 * Created by yufeng on 2018/4/24-0024.
 *
 */

@Dao
public interface ReadTxtParagraphDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertReadBookHistory(ReadTxtParagraph readTxtParagraph);

    @Query("SELECT * FROM book_read_history ORDER BY last_read_time DESC LIMIT 20")
    Single<ReadTxtParagraph> getAllReadBookHistory();
}
