package com.yufeng.ireader.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.viewinterface.IMenuSetView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by yufeng on 2018/4/26.
 *
 */

public abstract class MenuSetView implements IMenuSetView{

    protected static final int DURATION = 200;

    private Window mWindow;
    private WindowManager windowManager;
    private boolean isShow = false;
    public MenuSetView(Context context){
        init(context);
    }

    private void init(Context context){
        Dialog dialog = new Dialog(context, R.style.Theme_PopupMenu_Fullscreen);
        mWindow = dialog.getWindow();
        if (mWindow != null){
//            mWindow.setType(WindowManager.LayoutParams.TYPE_APPLICATION);
        }

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public void setContentView(int layoutRes) {
        mWindow.setContentView(layoutRes);
    }

    @Override
    public View findViewById(int viewId) {
        return mWindow.findViewById(viewId);
    }

    protected abstract void startShowAnimation();
    protected abstract void startHideAnimation();

    @Override
    public void show() {

        if (isShow){
            return;
        }

        WindowManager.LayoutParams params = mWindow.getAttributes();
        if ((params.softInputMode & WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION) == 0) {
            WindowManager.LayoutParams paramsCopy = new WindowManager.LayoutParams();
            paramsCopy.copyFrom(params);
            paramsCopy.softInputMode |= WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;

            // 菜单充满屏幕（非全屏），不设置的话，菜单不显示
            paramsCopy.width = WindowManager.LayoutParams.MATCH_PARENT;
            paramsCopy.height = WindowManager.LayoutParams.MATCH_PARENT;

            params = paramsCopy;
        }

        windowManager.addView(mWindow.getDecorView(),params);

        startShowAnimation();
        isShow = true;
    }

    @Override
    public void hide() {
        if (!isShow) {
            return;
        }
        startHideAnimation();
        isShow = false;
        Observable.timer(DURATION, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        windowManager.removeView(mWindow.getDecorView());
                    }
                });

    }

    public boolean isMenuShowing(){
        return isShow;
    }
}
