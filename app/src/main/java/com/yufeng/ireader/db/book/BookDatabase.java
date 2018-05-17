package com.yufeng.ireader.db.book;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.yufeng.ireader.base.ReadApplication;
import com.yufeng.ireader.db.base.DBConstants;

/**
 * Created by yufeng on 2018/5/10-0010.
 * 书架书籍数据库
 */
@Database(entities = Book.class, version = DBConstants.DB_VERSION_BOOK)
public abstract class BookDatabase extends RoomDatabase{

    private static class BookDatabaseHolder {
        private static BookDatabase instance = null;
        private static BookDatabase getInstance(){
            if (instance == null){
                instance = Room.databaseBuilder(ReadApplication.baseApplication.getBaseContext(), BookDatabase.class, DBConstants.DB_NAME_BOOK).build();
            }
            return instance;
        }
    }

    public abstract BookDao getBookDao();

    public static BookDatabase getInstance(){
        return BookDatabaseHolder.getInstance();
    }

}
