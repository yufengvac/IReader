package com.yufeng.ireader.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yufeng.ireader.R;
import com.yufeng.ireader.utils.PermissionHelper;

import java.util.List;

/**
 * Created by yufeng on 2018/4/14.
 *
 */

public abstract class BaseActivity extends AppCompatActivity implements PermissionHelper.permissionCallback{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        overridePendingTransition(setEnterAnimation(),setExitAnimation());
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        initView();
        initListener();
        PermissionHelper.getInstance(this).checkOnePermission(PermissionHelper.PERMISSION_WRITE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void grantedAll() {
        initData();
    }

    @Override
    public void notGranted(List<String> permissionList) {
        PermissionHelper.getInstance(this).checkOnePermission(PermissionHelper.PERMISSION_WRITE);
    }

    public abstract int getLayoutRes();
    public abstract void initView();
    public abstract void initData();
    public abstract void initListener();

    public int setEnterAnimation(){
        return R.anim.right_in;
    }

    public int setExitAnimation(){
        return R.anim.hold;
    }
}
