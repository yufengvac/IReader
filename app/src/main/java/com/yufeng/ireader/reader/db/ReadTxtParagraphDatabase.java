package com.yufeng.ireader.reader.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.yufeng.ireader.base.ReadApplication;

/**
 * Created by yufeng on 2018/4/24-0024.
 *
 */
@Database(entities = {ReadTxtParagraph.class}, version = 1)
public abstract class ReadTxtParagraphDatabase extends RoomDatabase{
    private static final String DB_NAME = "book.db";

    private static class ReadTxtParagraphDatabaseHolder{
        private static ReadTxtParagraphDatabase instance = null;
        private static ReadTxtParagraphDatabase getInstance(){
            if (instance == null){
                instance = Room.databaseBuilder(ReadApplication.baseApplication.getBaseContext(), ReadTxtParagraphDatabase.class,DB_NAME).build();
            }
            return instance;
        }
    }

    public static ReadTxtParagraphDatabase getInstance(){
        return ReadTxtParagraphDatabaseHolder.getInstance();
    }
    public abstract ReadTxtParagraphDao getReadTxtParagraphDao();
}
