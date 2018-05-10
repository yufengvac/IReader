package com.yufeng.ireader.db.readhistory;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.yufeng.ireader.base.ReadApplication;

import static com.yufeng.ireader.db.base.DBConstants.DB_NAME;
import static com.yufeng.ireader.db.base.DBConstants.DB_VERSION_READ_TEXTPARAGRAPH;

/**
 * Created by yufeng on 2018/4/24-0024.
 *
 */
@Database(entities = {ReadTxtParagraph.class}, version = DB_VERSION_READ_TEXTPARAGRAPH)
public abstract class ReadTxtParagraphDatabase extends RoomDatabase{

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

    static final Migration MIGRATION_1_2 = new Migration(1, DB_VERSION_READ_TEXTPARAGRAPH) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {

        }
    };
}
