package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.view.MenuSetView;
import com.yufeng.ireader.reader.viewinterface.OnReadMenuClickListener;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.DisplayConstant;

/**
 * Created by yufeng on 2018/4/27-0027.
 * 阅读器的菜单view
 */

public class ReadMenuSetView extends MenuSetView implements View.OnClickListener{
    private View topView;
    private View blankView;
    private View bottomView;
    private Context mContext;

    private LinearLayout categoryLayout, brightnessLayout, listenLayout, settingLayout;
    private ImageView dayNightModeIv;

    private OnReadMenuClickListener onReadMenuClickListener;

    private int topViewHeight ;
    private int bottomViewHeight;

    private TextView bookNameTv;

    public ReadMenuSetView(Context context) {
        super(context);
        mContext = context;
        if (! (context instanceof OnReadMenuClickListener)){
            throw new ClassCastException("the activity must implement OnReadMenuClickListener ");
        }
        onReadMenuClickListener = (OnReadMenuClickListener) context;
        setContentView(R.layout.layout_read_menu);
        initView();
        initListener();
    }

    private void initView(){
        topView = findViewById(R.id.read_menu_top);
        blankView = findViewById(R.id.read_menu_blank);
        bottomView = findViewById(R.id.read_menu_bottom);

        categoryLayout = (LinearLayout) findViewById(R.id.read_menu_category_layout);
        brightnessLayout = (LinearLayout) findViewById(R.id.read_menu_brightness_layout);
        listenLayout = (LinearLayout) findViewById(R.id.read_menu_listen_layout);
        settingLayout = (LinearLayout) findViewById(R.id.read_menu_setting_layout);

        bookNameTv = (TextView) findViewById(R.id.read_menu_book_name_tv);

        dayNightModeIv = (ImageView) findViewById(R.id.read_menu_day_night_mode);
    }

    private void initListener(){
        blankView.setOnClickListener(this);

        topViewHeight = DisPlayUtil.dp2px(mContext, 50);
        bottomViewHeight = DisPlayUtil.dp2px(mContext, 125);

        categoryLayout.setOnClickListener(this);
        brightnessLayout.setOnClickListener(this);
        listenLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    protected void startShowAnimation() {
        getTopShowAnimation().start();
        getBottomShowAnimation().start();
    }
    @Override
    protected void startHideAnimation() {
        getTopHideAnimation().start();
        getBottomHideAnimation().start();
    }

    private Animator getTopShowAnimation(){
        Animator animator = ObjectAnimator.ofFloat(topView,"translationY",-topViewHeight,0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(DURATION);
        return animator;
    }

    private Animator getBottomShowAnimation(){
        Animator animator = ObjectAnimator.ofFloat(bottomView,"translationY", bottomViewHeight, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(DURATION);
        return animator;
    }

    private Animator getTopHideAnimation(){
        Animator animator = ObjectAnimator.ofFloat(topView,"translationY", 0, -topViewHeight);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(DURATION);
        return animator;
    }
    private Animator getBottomHideAnimation(){
        Animator animator = ObjectAnimator.ofFloat(bottomView,"translationY", 0,bottomViewHeight);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(DURATION);
        return animator;
    }

    public void setBookName(String name){
        if (bookNameTv != null && name != null){
            bookNameTv.setText(name);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.read_menu_blank:
                hide();
                break;
            case R.id.read_menu_category_layout:
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onCategoryClick(categoryLayout);
                }
                break;
            case R.id.read_menu_brightness_layout:
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onBrightnessClick(brightnessLayout);
                }
                break;
            case R.id.read_menu_listen_layout:
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onListenClick(listenLayout);
                }
                break;
            case R.id.read_menu_setting_layout:
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onSettingClick(settingLayout);
                }
                break;
            case R.id.read_menu_day_night_mode:
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onDayNightClick(settingLayout);
                }
                break;
        }
    }
}
