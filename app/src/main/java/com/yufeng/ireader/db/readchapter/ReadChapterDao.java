package com.yufeng.ireader.db.readchapter;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by yufeng on 2018/5/17-0017.
 * 书籍目录操作Dao
 */

@Dao
public interface ReadChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertChapter(ReadChapter readChapter);

    @Query("SELECT * FROM chapter WHERE book_path = :bookPath")
    Single<List<ReadChapter>> getCatalogList(String bookPath);

    @Query("SELECT COUNT(*) FROM chapter WHERE book_path = :bookPath")
    Single<Integer> getChapterCount(String bookPath);
}
