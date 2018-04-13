package com.yufeng.ireader.reader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yufeng.ireader.utils.PathHelper;

import java.util.ArrayList;

/**
 * Created by yufeng on 2018/4/11.
 *
 */

public class ReaderActivity extends AppCompatActivity{
    private static final String TAG = ReaderActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openBook();
    }

    private void openBook(){
        new Thread(){
            @Override
            public void run() {
                String realPath = PathHelper.getBookPath();
                Log.e(TAG,"realPath="+realPath);
                ArrayList<String> contentList = PathHelper.getContentByPath(realPath);
                if (contentList != null && contentList.size() > 0){
                    for (int i = 0 ; i < 20 ; i++){
                        Log.e(TAG,contentList.get(i));
                    }
                    Log.e(TAG,"大小是："+contentList.size());
                }
            }
        }.start();
    }
}
