package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.view.MenuSetView;
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

    private int topViewHeight ;
    private int bottomViewHeight;
    private int displayHeight;

    public ReadMenuSetView(Context context) {
        super(context);
        mContext = context;
        setContentView(R.layout.layout_read_menu);
        initView();
        initListener();
    }

    private void initView(){
        topView = findViewById(R.id.read_menu_top);
        blankView = findViewById(R.id.read_menu_blank);
        bottomView = findViewById(R.id.read_menu_bottom);
    }

    private void initListener(){
        blankView.setOnClickListener(this);

        topViewHeight = DisPlayUtil.dp2px(mContext, 50);
        bottomViewHeight = DisPlayUtil.dp2px(mContext, 125);
        displayHeight = DisplayConstant.DISPLAY_HEIGHT;
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.read_menu_blank:
                hide();
                break;
        }
    }
}
