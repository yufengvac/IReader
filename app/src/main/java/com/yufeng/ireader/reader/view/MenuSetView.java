package com.yufeng.ireader.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.viewinterface.IMenuSetView;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.reader.viewinterface.OnReadViewChangeListener;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.DisplayConstant;

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
    protected OnReadViewChangeListener onReadViewChangeListener;
    protected IReadSetting readSetting;

    private ViewGroup decorView;
    private View statusBarView;
    private Context mContext;

    public MenuSetView(Context context, IReadSetting readSetting){
        this.readSetting = readSetting;
        init(context);
    }

    private void init(Context context){
        this.mContext = context;
        Dialog dialog = new Dialog(context, R.style.Theme_PopupMenu_Fullscreen);
        mWindow = dialog.getWindow();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (mWindow != null){
            mWindow.setWindowManager(windowManager,null , null);
            mWindow.setType(WindowManager.LayoutParams.TYPE_APPLICATION);
        }


        if (context instanceof OnReadViewChangeListener){
            onReadViewChangeListener = (OnReadViewChangeListener)context;
        }
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

        decorView = (ViewGroup) mWindow.getDecorView();
//        addSimulatedStatusBarView();

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

        windowManager.addView(decorView,params);

        startShowAnimation();
        isShow = true;
    }

    private void addSimulatedStatusBarView(){
        statusBarView = new View(mContext);
        statusBarView.setBackgroundColor(Color.parseColor("#000000"));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(DisplayConstant.DISPLAY_WIDTH, DisPlayUtil.getStatusBarHeight(mContext));
        decorView.addView(statusBarView, params);
    }
    private void removeSimulatedStatusBarView(){
        if (decorView != null){
            decorView.removeView(statusBarView);
            statusBarView = null;
        }
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
//                        removeSimulatedStatusBarView();
                        windowManager.removeView(decorView);
                        decorView = null;
                        mWindow.closeAllPanels();
                    }
                });

    }

    public boolean isMenuShowing(){
        return isShow;
    }
}
