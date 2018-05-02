package com.yufeng.ireader.reader.viewimpl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.utils.ReadExteriorConstants;
import com.yufeng.ireader.reader.utils.ReadExteriorHelper;
import com.yufeng.ireader.reader.view.MenuSetView;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisPlayUtil;

/**
 * Created by yufeng on 2018/5/2-0002.
 * 阅读器菜单点击设置页面
 */

public class ReadMenuSettingView extends MenuSetView implements View.OnClickListener{

    private int bottomViewHeight;
    private View bottomView;
    private View blankView;
    private Context mContext;

    private TextView fontSizeMinusTv,fontSizeValueTv,fontSizePlusTv,fontSizeDefaultTv;

    private TextView fontDefaultTv, fontItalicTv, fontSongTv, fontMoreTv;

    private TextView pageTurnCoverageTv, pageTurnTopBottomTv, pageTurnSimulationTv, pageTurnAlphaTv, pageTurnNoneTv;

    public ReadMenuSettingView(Context context, IReadSetting readSetting) {
        super(context,readSetting);
        mContext = context;
        setContentView(R.layout.layout_read_setting_menu);
        initView();
        initListener();
        initData();
    }

    private void initView(){
        bottomView = findViewById(R.id.read_menu_setting_bottom_view);
        bottomViewHeight = DisPlayUtil.dp2px(mContext, 200);

        blankView = findViewById(R.id.read_menu_setting_blank_view);

        fontSizeMinusTv = (TextView) findViewById(R.id.read_menu_setting_font_size_minus_tv);
        fontSizeValueTv = (TextView) findViewById(R.id.read_menu_setting_font_size_value_tv);
        fontSizePlusTv = (TextView) findViewById(R.id.read_menu_setting_font_size_plus_tv);
        fontSizeDefaultTv = (TextView) findViewById(R.id.read_menu_setting_font_size_default_tv);

        fontDefaultTv = (TextView) findViewById(R.id.read_menu_setting_font_default);
        fontItalicTv = (TextView) findViewById(R.id.read_menu_setting_font_italics_tv);
        fontSongTv = (TextView) findViewById(R.id.read_menu_setting_font_song_tv);
        fontMoreTv = (TextView) findViewById(R.id.read_menu_setting_font_more_tv);

        Typeface italic = Typeface.createFromAsset(mContext.getAssets(), "font/italic.ttf");
        fontItalicTv.setTypeface(italic);

        Typeface song = Typeface.createFromAsset(mContext.getAssets(), "font/xujinglei.ttf");
        fontSongTv.setTypeface(song);

        pageTurnCoverageTv = (TextView) findViewById(R.id.read_menu_setting_page_turn_coverage_tv);
        pageTurnTopBottomTv = (TextView) findViewById(R.id.read_menu_setting_page_turn_top_bottom_tv);
        pageTurnSimulationTv = (TextView) findViewById(R.id.read_menu_setting_page_turn_simulation_tv);
        pageTurnAlphaTv = (TextView) findViewById(R.id.read_menu_setting_page_turn_alpha_tv);
        pageTurnNoneTv = (TextView) findViewById(R.id.read_menu_setting_page_turn_none_tv);

    }

    private void initListener(){
        bottomView.setOnClickListener(this);
        blankView.setOnClickListener(this);

        fontSizeMinusTv.setOnClickListener(this);
        fontSizePlusTv.setOnClickListener(this);
        fontSizeDefaultTv.setOnClickListener(this);

        fontDefaultTv.setOnClickListener(this);
        fontItalicTv.setOnClickListener(this);
        fontSongTv.setOnClickListener(this);
        fontMoreTv.setOnClickListener(this);

        pageTurnCoverageTv.setOnClickListener(this);
        pageTurnTopBottomTv.setOnClickListener(this);
        pageTurnSimulationTv.setOnClickListener(this);
        pageTurnAlphaTv.setOnClickListener(this);
        pageTurnNoneTv.setOnClickListener(this);
    }

    private void initData(){
        if (readSetting == null){
            return;
        }

        setTextSizeValue();

        int fontfaceOption = readSetting.getFontface();
        if (fontfaceOption == ReadExteriorConstants.ReadTypeFace.TYPEFACE_DEFAULT){
            setSelectedTextView(fontDefaultTv);
        }else if (fontfaceOption == ReadExteriorConstants.ReadTypeFace.TYPEFACE_ITALIC){
            setSelectedTextView(fontItalicTv);
        }else if (fontfaceOption == ReadExteriorConstants.ReadTypeFace.TYPEFACE_XU){
            setSelectedTextView(fontSongTv);
        }


        int pageTurnType = readSetting.getPageTurnType();
        if (pageTurnType == 0){
            setSelectedTextView(pageTurnCoverageTv);
        }else if (pageTurnType == 1){
            setSelectedTextView(pageTurnTopBottomTv);
        }else if (pageTurnType == 2){
            setSelectedTextView(pageTurnSimulationTv);
        }else if (pageTurnType == 3){
            setSelectedTextView(pageTurnAlphaTv);
        }else if (pageTurnType == 4){
            setSelectedTextView(pageTurnNoneTv);
        }

    }


    private void setSelectedTextView(TextView textView){
        int notSelectedColor = ContextCompat.getColor(mContext, R.color.read_menu_title_color);
        int selectedColor = ContextCompat.getColor(mContext, R.color.read_menu_seek_green);

        if (textView == fontDefaultTv){
            fontDefaultTv.setTextColor(selectedColor);
            fontItalicTv.setTextColor(notSelectedColor);
            fontSongTv.setTextColor(notSelectedColor);
        }else if (textView == fontItalicTv){
            fontDefaultTv.setTextColor(notSelectedColor);
            fontItalicTv.setTextColor(selectedColor);
            fontSongTv.setTextColor(notSelectedColor);
        }else if (textView == fontSongTv){
            fontDefaultTv.setTextColor(notSelectedColor);
            fontItalicTv.setTextColor(notSelectedColor);
            fontSongTv.setTextColor(selectedColor);
        }


        if (textView == pageTurnCoverageTv){
            pageTurnCoverageTv.setTextColor(selectedColor);
        }else if (textView == pageTurnTopBottomTv){
            pageTurnTopBottomTv.setTextColor(selectedColor);
        }else if (textView == pageTurnSimulationTv){
            pageTurnSimulationTv.setTextColor(selectedColor);
        }else if (textView == pageTurnAlphaTv){
            pageTurnAlphaTv.setTextColor(selectedColor);
        }else if (textView == pageTurnNoneTv){
            pageTurnNoneTv.setTextColor(selectedColor);
        }
    }

    private void setTextSizeValue(){
        float textSize = readSetting.getContentPaint().getTextSize();
        fontSizeValueTv.setText(String.valueOf(DisPlayUtil.px2sp(mContext,textSize)));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.read_menu_setting_blank_view:
                hide();
                break;
            case R.id.read_menu_setting_font_size_minus_tv:
                ReadExteriorHelper.getInstance().changeTextSize(mContext, true);
                setTextSizeValue();
                if (onReadViewChangeListener != null){
                    onReadViewChangeListener.onReadViewChange(true);
                }
                break;
            case R.id.read_menu_setting_font_size_plus_tv:
                ReadExteriorHelper.getInstance().changeTextSize(mContext, false);
                setTextSizeValue();
                if (onReadViewChangeListener != null){
                    onReadViewChangeListener.onReadViewChange(true);
                }
                break;
            case R.id.read_menu_setting_font_default:
                setSelectedTextView(fontDefaultTv);
                ReadExteriorHelper.getInstance().changeTypeface(mContext, ReadExteriorConstants.ReadTypeFace.TYPEFACE_DEFAULT);
                if (onReadViewChangeListener != null){
                    onReadViewChangeListener.onReadViewChange(false);
                }
                break;
            case R.id.read_menu_setting_font_italics_tv:
                setSelectedTextView(fontItalicTv);
                ReadExteriorHelper.getInstance().changeTypeface(mContext, ReadExteriorConstants.ReadTypeFace.TYPEFACE_ITALIC);
                if (onReadViewChangeListener != null){
                    onReadViewChangeListener.onReadViewChange(false);
                }
                break;
            case R.id.read_menu_setting_font_song_tv:
                setSelectedTextView(fontSongTv);
                ReadExteriorHelper.getInstance().changeTypeface(mContext, ReadExteriorConstants.ReadTypeFace.TYPEFACE_XU);
                if (onReadViewChangeListener != null){
                    onReadViewChangeListener.onReadViewChange(false);
                }
                break;
        }
    }

    @Override
    protected void startShowAnimation() {
        getBottomShowAnimation().start();
    }

    @Override
    protected void startHideAnimation() {
        getBottomHideAnimation().start();
    }

    private Animator getBottomShowAnimation(){
        Animator animator = ObjectAnimator.ofFloat(bottomView,"translationY", bottomViewHeight, 0);
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
}
