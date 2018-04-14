package com.yufeng.ireader.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.yufeng.ireader.ui.base.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/14.
 *
 */

public class PermissionHelper {
    private WeakReference<BaseActivity> weakReference;
    private static final int REQUEST_CODE = 0x1000;
    public static final String PERMISSION_WRITE = Manifest.permission.ACCESS_COARSE_LOCATION;
    private permissionCallback callback;

    private PermissionHelper(BaseActivity activity) {
        weakReference = new WeakReference<>(activity);
    }

    private static class PermissionHelperFactory {
        private static PermissionHelper instance = null;

        private static PermissionHelper getPermissionHelper(BaseActivity activity) {
            if (instance == null) {
                instance = new PermissionHelper(activity);
            }
            return instance;
        }
    }

    public static PermissionHelper getInstance(BaseActivity activity) {
        return PermissionHelperFactory.getPermissionHelper(activity);
    }

    public void checkOnePermission(String permission) {
        Activity activity = weakReference.get();
        if (activity == null) {
            return;
        }

        callback = (permissionCallback)activity;

        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            callback.grantedAll();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
            boolean isGrantedAll = true;
            List<String> notGrantedList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isGrantedAll = false;
                    notGrantedList.add(permissions[i]);
                }
            }
            if (!isGrantedAll) {
                if (callback != null) {
                    callback.notGranted(notGrantedList);
                }
            } else {
                if (callback != null) {
                    callback.grantedAll();
                }
            }
        }
    }

    public interface permissionCallback {
        void grantedAll();

        void notGranted(List<String> permissionList);
    }
}
