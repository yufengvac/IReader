package com.yufeng.ireader.db.readchapter;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.yufeng.ireader.base.ReadApplication;
import com.yufeng.ireader.db.base.DBConstants;

/**
 * Created by yufeng on 2018/5/17-0017.
 * 书籍章节内容数据库
 */

@Database(entities = ReadChapter.class ,version = DBConstants.DB_VERSION_CHAPTER)
public abstract class ReadChapterDatabase extends RoomDatabase{

    public abstract ReadChapterDao getReadChapterDao();

    private static class ReadChapterDatabaseHolder{
        private static ReadChapterDatabase instance = null;
        private static ReadChapterDatabase getInstance(){
            if (instance == null){
                instance = Room.databaseBuilder(ReadApplication.baseApplication.getBaseContext(),ReadChapterDatabase.class,DBConstants.DB_NAME_CHAPTER).build();
            }
            return instance;
        }
    }


    public static ReadChapterDatabase getInstance(){
        return ReadChapterDatabaseHolder.getInstance();
    }
}
