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
import com.yufeng.ireader.reader.view.ThemeImageView;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.ReadPreferHelper;

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

    private ThemeImageView themeGrayIv,theme1Iv,theme2Iv,theme3Iv,themeKraftPaperIv;

    private TextView immersiveReadTv;

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
        bottomViewHeight = DisPlayUtil.dp2px(mContext, 270);

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


        themeGrayIv = (ThemeImageView) findViewById(R.id.read_menu_setting_gray_theme_iv);
        theme1Iv = (ThemeImageView) findViewById(R.id.read_menu_setting_1_theme_iv);
        theme2Iv = (ThemeImageView) findViewById(R.id.read_menu_setting_2_theme_iv);
        theme3Iv = (ThemeImageView) findViewById(R.id.read_menu_setting_3_theme_iv);
        themeKraftPaperIv = (ThemeImageView) findViewById(R.id.read_menu_setting_kraft_paper_theme_iv);

        pageTurnCoverageTv = (TextView) findViewById(R.id.read_menu_setting_page_turn_coverage_tv);
        pageTurnTopBottomTv = (TextView) findViewById(R.id.read_menu_setting_page_turn_top_bottom_tv);
        pageTurnSimulationTv = (TextView) findViewById(R.id.read_menu_setting_page_turn_simulation_tv);
        pageTurnAlphaTv = (TextView) findViewById(R.id.read_menu_setting_page_turn_alpha_tv);
        pageTurnNoneTv = (TextView) findViewById(R.id.read_menu_setting_page_turn_none_tv);

        immersiveReadTv = (TextView) findViewById(R.id.read_menu_setting_immersive_read_tv);

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

        themeGrayIv.setOnClickListener(this);
        theme1Iv.setOnClickListener(this);
        theme2Iv.setOnClickListener(this);
        theme3Iv.setOnClickListener(this);
        themeKraftPaperIv.setOnClickListener(this);

        pageTurnCoverageTv.setOnClickListener(this);
        pageTurnTopBottomTv.setOnClickListener(this);
        pageTurnSimulationTv.setOnClickListener(this);
        pageTurnAlphaTv.setOnClickListener(this);
        pageTurnNoneTv.setOnClickListener(this);

        immersiveReadTv.setOnClickListener(this);
    }

    private void initData(){
        if (readSetting == null){
            return;
        }

        //设置默认字号
        setTextSizeValue(null, false);

        //设置字体
        int fontFaceOption = readSetting.getFontFace();
        if (fontFaceOption == ReadExteriorConstants.ReadTypeFace.TYPEFACE_DEFAULT){
            setSelectedTextView(fontDefaultTv);
        }else if (fontFaceOption == ReadExteriorConstants.ReadTypeFace.TYPEFACE_ITALIC){
            setSelectedTextView(fontItalicTv);
        }else if (fontFaceOption == ReadExteriorConstants.ReadTypeFace.TYPEFACE_XU){
            setSelectedTextView(fontSongTv);
        }


        //设置翻页方式
        int pageTurnType = readSetting.getPageTurnType();
        if (pageTurnType == ReadExteriorConstants.PageTurnType.PAGE_TURN_COVERAGE){
            setSelectedTextView(pageTurnCoverageTv);
        }else if (pageTurnType == ReadExteriorConstants.PageTurnType.PAGE_TURN_TOPBOTTOM){
            setSelectedTextView(pageTurnTopBottomTv);
        }else if (pageTurnType == ReadExteriorConstants.PageTurnType.PAGE_TURN_SIMULATION){
            setSelectedTextView(pageTurnSimulationTv);
        }else if (pageTurnType == ReadExteriorConstants.PageTurnType.PAGE_TURN_ALPHA){
            setSelectedTextView(pageTurnAlphaTv);
        }else if (pageTurnType == ReadExteriorConstants.PageTurnType.PAGE_TURN_NONE){
            setSelectedTextView(pageTurnNoneTv);
        }

        //设置主题背景
        if (readSetting.getCanvasBgOptions() == ReadExteriorConstants.ThemeOption.IMG){
            if (readSetting.getCanvasImgOptions() == ReadExteriorConstants.ThemeBgImg.IMG_GRAY){
                setSelectedTheme(themeGrayIv, false);
            }else if (readSetting.getCanvasImgOptions() == ReadExteriorConstants.ThemeBgImg.IMG_KRAFT_PAPER){
                setSelectedTheme(themeKraftPaperIv, false);
            }
        }else if (readSetting.getCanvasBgOptions() == ReadExteriorConstants.ThemeOption.COLOR){
            if (readSetting.getCanvasBgColor().equals(ReadExteriorConstants.ThemeBgColor.COLOR_1)){
                setSelectedTheme(theme1Iv,false);
            }else if (readSetting.getCanvasBgColor().equals(ReadExteriorConstants.ThemeBgColor.COLOR_2)){
                setSelectedTheme(theme2Iv,false);
            }else if (readSetting.getCanvasBgColor().equals(ReadExteriorConstants.ThemeBgColor.COLOR_3)){
                setSelectedTheme(theme3Iv,false);
            }
        }

        //设置沉浸阅读
        setImmersiveReadTv();
    }

    /**
     * 设置选中的TextView字体颜色样式
     * @param textView 选中的TextView
     */
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

    /**
     * 设置翻页方式TextView
     * @param view 需要设置的翻页的TextView
     */
    private void setPageTurnTextView(View view){
        int id = view.getId();

        int notSelectedColor = ContextCompat.getColor(mContext, R.color.read_menu_title_color);
        int selectedColor = ContextCompat.getColor(mContext, R.color.read_menu_seek_green);


        pageTurnCoverageTv.setTextColor(notSelectedColor);
        pageTurnTopBottomTv.setTextColor(notSelectedColor);
        pageTurnSimulationTv.setTextColor(notSelectedColor);
        pageTurnAlphaTv.setTextColor(notSelectedColor);
        pageTurnNoneTv.setTextColor(notSelectedColor);

        if (id == pageTurnCoverageTv.getId()){
            pageTurnCoverageTv.setTextColor(selectedColor);
            ReadPreferHelper.getInstance().setPageTurnType(ReadExteriorConstants.PageTurnType.PAGE_TURN_COVERAGE);
        }else if (id == pageTurnTopBottomTv.getId()){
            pageTurnTopBottomTv.setTextColor(selectedColor);
            ReadPreferHelper.getInstance().setPageTurnType(ReadExteriorConstants.PageTurnType.PAGE_TURN_TOPBOTTOM);
        }else if (id == pageTurnSimulationTv.getId()){
            pageTurnSimulationTv.setTextColor(selectedColor);
            ReadPreferHelper.getInstance().setPageTurnType(ReadExteriorConstants.PageTurnType.PAGE_TURN_SIMULATION);
        }else if (id == pageTurnAlphaTv.getId()){
            pageTurnAlphaTv.setTextColor(selectedColor);
            ReadPreferHelper.getInstance().setPageTurnType(ReadExteriorConstants.PageTurnType.PAGE_TURN_ALPHA);
        }else if (id == pageTurnNoneTv.getId()){
            pageTurnNoneTv.setTextColor(selectedColor);
            ReadPreferHelper.getInstance().setPageTurnType(ReadExteriorConstants.PageTurnType.PAGE_TURN_NONE);
        }
        if (onReadViewChangeListener != null ){
            onReadViewChangeListener.onReadViewPageTurnChange();
        }
    }

    /**
     * 设置当前阅读器的字体大小
     * @param view 操作的view
     */
    private void setTextSizeValue(View view, boolean needInvalidate){
        if (view != null){
            int id = view.getId();
            boolean isMinus = false;
            boolean isDefault = false;
            if (id == fontSizeMinusTv.getId()){
                isMinus = true;
            }else if (id == fontSizePlusTv.getId()){
                isMinus = false;
            }else if (id == fontSizeDefaultTv.getId()){
                isDefault = true;
            }
            ReadExteriorHelper.getInstance().changeTextSize(mContext, isMinus,isDefault);
        }


        int textSize = DisPlayUtil.px2sp(mContext,readSetting.getContentPaint().getTextSize());
        if (textSize == ReadExteriorConstants.MAX_TEXT_SIZE){
            fontSizePlusTv.setEnabled(false);
        }else {
            fontSizePlusTv.setEnabled(true);
        }

        if (textSize == ReadExteriorConstants.MIN_TEXT_SIZE){
            fontSizeMinusTv.setEnabled(false);
        }else {
            fontSizeMinusTv.setEnabled(true);
        }
        fontSizeValueTv.setText(String.valueOf(textSize));
        if (onReadViewChangeListener != null && needInvalidate){
            onReadViewChangeListener.onReadViewChange(true);
        }
    }

    /**
     * 设置主题背景的勾选样式
     * @param view 需要设置的主题背景ThemeImageView对象
     */
    private void setSelectedTheme(View view, boolean needInvalidate){
        themeGrayIv.clearSelected();
        theme1Iv.clearSelected();
        theme2Iv.clearSelected();
        theme3Iv.clearSelected();
        themeKraftPaperIv.clearSelected();
        int id = view.getId();
        if (id == themeGrayIv.getId()){
            ReadPreferHelper.getInstance().setThemeOption(ReadExteriorConstants.ThemeOption.IMG);
            ReadPreferHelper.getInstance().setThemeImg(ReadExteriorConstants.ThemeBgImg.IMG_GRAY);
            themeGrayIv.setSelected();
        }else if (id == theme1Iv.getId()){
            ReadPreferHelper.getInstance().setThemeOption(ReadExteriorConstants.ThemeOption.COLOR);
            ReadPreferHelper.getInstance().setThemeColor(ReadExteriorConstants.ThemeBgColor.COLOR_1);
            theme1Iv.setSelected();
        }else if (id == theme2Iv.getId()){
            ReadPreferHelper.getInstance().setThemeOption(ReadExteriorConstants.ThemeOption.COLOR);
            ReadPreferHelper.getInstance().setThemeColor(ReadExteriorConstants.ThemeBgColor.COLOR_2);
            theme2Iv.setSelected();
        }else if (id == theme3Iv.getId()){
            ReadPreferHelper.getInstance().setThemeOption(ReadExteriorConstants.ThemeOption.COLOR);
            ReadPreferHelper.getInstance().setThemeColor(ReadExteriorConstants.ThemeBgColor.COLOR_3);
            theme3Iv.setSelected();
        }else if (id == themeKraftPaperIv.getId()){
            ReadPreferHelper.getInstance().setThemeOption(ReadExteriorConstants.ThemeOption.IMG);
            ReadPreferHelper.getInstance().setThemeImg(ReadExteriorConstants.ThemeBgImg.IMG_KRAFT_PAPER);
            themeKraftPaperIv.setSelected();
        }

        if (onReadViewChangeListener != null && needInvalidate){
            onReadViewChangeListener.onReadViewChange(false);
        }
    }

    private void setImmersiveReadTv(){
        if (readSetting.isImmersiveRead()){
            immersiveReadTv.setTextColor(ContextCompat.getColor(mContext, R.color.read_menu_seek_green));
        }else {
            immersiveReadTv.setTextColor(ContextCompat.getColor(mContext, R.color.read_menu_title_color));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.read_menu_setting_blank_view:
                hide();
                break;
            case R.id.read_menu_setting_font_size_minus_tv:
            case R.id.read_menu_setting_font_size_plus_tv:
            case R.id.read_menu_setting_font_size_default_tv:
                setTextSizeValue(v, true);
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
            case R.id.read_menu_setting_gray_theme_iv:
            case R.id.read_menu_setting_1_theme_iv:
            case R.id.read_menu_setting_2_theme_iv:
            case R.id.read_menu_setting_3_theme_iv:
            case R.id.read_menu_setting_kraft_paper_theme_iv:
                setSelectedTheme(v,true);
                break;
            case R.id.read_menu_setting_page_turn_coverage_tv:
            case R.id.read_menu_setting_page_turn_top_bottom_tv:
            case R.id.read_menu_setting_page_turn_simulation_tv:
            case R.id.read_menu_setting_page_turn_alpha_tv:
            case R.id.read_menu_setting_page_turn_none_tv:
                setPageTurnTextView(v);
                break;
            case R.id.read_menu_setting_immersive_read_tv:
                ReadExteriorHelper.getInstance().changeImmersiveRead();
                setImmersiveReadTv();
                if (onReadViewChangeListener != null){
                    onReadViewChangeListener.onReadViewChange(true);
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
