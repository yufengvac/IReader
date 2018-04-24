package com.yufeng.ireader.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by yufeng on 2018/4/11.
 *
 */

public class ReadApplication extends Application{
    public static ReadApplication baseApplication ;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }


    public Context getBaseContext(){
        return baseApplication.getApplicationContext();
    }

}
