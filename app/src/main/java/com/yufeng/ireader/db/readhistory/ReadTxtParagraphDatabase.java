package com.yufeng.ireader.db.readhistory;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.yufeng.ireader.base.ReadApplication;

import static com.yufeng.ireader.db.base.DBConstants.DB_NAME_READ_HISTORY;
import static com.yufeng.ireader.db.base.DBConstants.DB_VERSION_READ_TEXT_PARAGRAPH;

/**
 * Created by yufeng on 2018/4/24-0024.
 *
 */
@Database(entities = {ReadTxtParagraph.class}, version = DB_VERSION_READ_TEXT_PARAGRAPH )
public abstract class ReadTxtParagraphDatabase extends RoomDatabase{

    private static class ReadTxtParagraphDatabaseHolder{
        private static ReadTxtParagraphDatabase instance = null;
        private static ReadTxtParagraphDatabase getInstance(){
            if (instance == null){
                instance = Room.databaseBuilder(ReadApplication.baseApplication.getBaseContext(), ReadTxtParagraphDatabase.class,DB_NAME_READ_HISTORY)
                       .build();
            }
            return instance;
        }
    }

    public static ReadTxtParagraphDatabase getInstance(){
        return ReadTxtParagraphDatabaseHolder.getInstance();
    }
    public abstract ReadTxtParagraphDao getReadTxtParagraphDao();

}
