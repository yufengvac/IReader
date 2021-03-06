package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.bean.PageManager;
import com.yufeng.ireader.reader.utils.ReadExteriorHelper;
import com.yufeng.ireader.reader.view.MenuSetView;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.reader.viewinterface.OnReadMenuClickListener;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.ReadPreferHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by yufeng on 2018/4/27-0027.
 * 阅读器的菜单view
 */

public class ReadMenuSetView extends MenuSetView implements View.OnClickListener, Animator.AnimatorListener,Animation.AnimationListener{
    private View topView;
    private View blankView;
    private View bottomView;
    private Context mContext;

    private LinearLayout categoryLayout, brightnessLayout, listenLayout, settingLayout;
    private ImageView dayNightModeIv;

    private TextView prevChapterTv, nextChapterTv;
    private SeekBar progressSeekbar;

    private OnReadMenuClickListener onReadMenuClickListener;

    private int topViewHeight ;
    private int bottomViewHeight;

    private TextView bookNameTv;

    private Animation appearTranslateAnim;
    private Animation dismissRotateScaleAnim;
    private Animation changeModeDismissAnim;
    private Animation changeModeAppearAnim;

    private Animator bottomShowAnimator;

    public ReadMenuSetView(Context context, IReadSetting readSetting) {
        super(context, readSetting);
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

        prevChapterTv = (TextView) findViewById(R.id.read_menu_prev_chapter_tv);
        nextChapterTv = (TextView) findViewById(R.id.read_menu_next_chapter_tv);
        progressSeekbar = (SeekBar) findViewById(R.id.read_menu_chapter_progress_seek_bar);
        progressSeekbar.setMax(1000);

        setDayNightImageView();
    }

    private void initListener(){
        blankView.setOnClickListener(this);

        topViewHeight = DisPlayUtil.dp2px(mContext, 50);
        bottomViewHeight = DisPlayUtil.dp2px(mContext, 125);

        categoryLayout.setOnClickListener(this);
        brightnessLayout.setOnClickListener(this);
        listenLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        dayNightModeIv.setOnClickListener(this);

        //这是日夜间模式的出现动画
        appearTranslateAnim = AnimationUtils.loadAnimation(mContext, R.anim.overshoot_jump_bottom_anim);
        appearTranslateAnim.setAnimationListener(this);

        //这是日夜间模式的消失动画
        dismissRotateScaleAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_inhale);
        dismissRotateScaleAnim.setDuration(DURATION);
        dismissRotateScaleAnim.setAnimationListener(this);

        //改变日夜间模式消失的动画
        changeModeDismissAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_inhale);
        changeModeDismissAnim.setDuration(DURATION);
        changeModeDismissAnim.setAnimationListener(this);

        //改变日夜间模式出现的动画
        changeModeAppearAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_spit);
        changeModeAppearAnim.setDuration(DURATION);
        changeModeAppearAnim.setAnimationListener(this);

        prevChapterTv.setOnClickListener(this);
        nextChapterTv.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();

        if (progressSeekbar != null){
            progressSeekbar.setProgress((int)(PageManager.getInstance().getCurPercent() * 1000));
        }
    }

    @Override
    protected void startShowAnimation() {
        topView.setVisibility(View.GONE);
        Single.timer(150, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        topView.setVisibility(View.VISIBLE);
                        getTopShowAnimation().start();
                    }
                });
        bottomShowAnimator = getBottomShowAnimation();
        bottomShowAnimator.addListener(this);
        bottomShowAnimator.start();
    }
    @Override
    protected void startHideAnimation() {
        dayNightModeIv.startAnimation(dismissRotateScaleAnim);
        getTopHideAnimation().start();

        Animator bottomHideAnimator = getBottomHideAnimation();
        bottomHideAnimator.addListener(this);
        bottomHideAnimator.start();
    }


    /****************AnimatorListener*************/
    @Override
    public void onAnimationEnd(Animator animator) {
        if (animator == bottomShowAnimator){
            dayNightModeIv.setVisibility(View.VISIBLE);
            dayNightModeIv.startAnimation(appearTranslateAnim);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }




    /****************AnimationListener*************/

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (dayNightModeIv != null){
            if (animation == appearTranslateAnim){
                dayNightModeIv.setVisibility(View.VISIBLE);
            }else if (animation == dismissRotateScaleAnim){
                dayNightModeIv.setVisibility(View.GONE);
            }else if (animation == changeModeDismissAnim){
                setDayNightImageView();
                dayNightModeIv.startAnimation(changeModeAppearAnim);
            }else if (animation == changeModeAppearAnim){
                dayNightModeIv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    /****************AnimationListener*************/


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

    /**
     * 设置日夜间模式图片
     */
    private void setDayNightImageView(){
        if (ReadPreferHelper.getInstance().isDayMode()){
            dayNightModeIv.setImageResource(R.drawable.read_mode_night);
        }else {
            dayNightModeIv.setImageResource(R.drawable.read_mode_day);
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
                hide();
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onCategoryClick(categoryLayout);
                }
                break;
            case R.id.read_menu_brightness_layout:
                hide();
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onBrightnessClick(brightnessLayout);
                }
                break;
            case R.id.read_menu_listen_layout:
                hide();
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onListenClick(listenLayout);
                }
                break;
            case R.id.read_menu_setting_layout:
                hide();
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onSettingClick(settingLayout);
                }
                break;
            case R.id.read_menu_day_night_mode:
                ReadExteriorHelper.getInstance().changeDayNightMode();
                dayNightModeIv.startAnimation(changeModeDismissAnim);
                if (onReadViewChangeListener != null){
                    onReadViewChangeListener.onReadViewChange(false);
                }
                break;
            case R.id.read_menu_prev_chapter_tv:
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onTurnPreChapter(prevChapterTv);
                }
                break;
            case R.id.read_menu_next_chapter_tv:
                if (onReadMenuClickListener != null){
                    onReadMenuClickListener.onTurnNextChapter(nextChapterTv);
                }
                break;
        }
    }
}
