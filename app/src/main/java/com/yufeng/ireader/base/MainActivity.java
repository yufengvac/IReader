package com.yufeng.ireader.base;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.yufeng.ireader.R;
import com.yufeng.ireader.utils.PathHelper;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (1001 == requestCode){
            if (!PathHelper.ensurePath()){
                finish();
            }
        }
    }

    public void openBook(View view){
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
}
