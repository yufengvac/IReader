package com.yufeng.ireader.reader.utils;

import com.yufeng.ireader.reader.viewinterface.PageTurn;

/**
 * Created by yfueng on 2018/5/2-0002.
 *
 */

public class ReadExteriorConstants {

    public static final int DEFAULT_TEXT_SIZE = 22;
    public static final int MAX_TEXT_SIZE = 30;
    public static final int MIN_TEXT_SIZE = 9;

    public static class ReadTypeFace{
        public static final int TYPEFACE_DEFAULT = 0;
        public static final int TYPEFACE_ITALIC = 1;
        public static final int TYPEFACE_XU = 2;
    }

    public static class PageTurnType{
        public static final int PAGE_TURN_COVERAGE = PageTurn.PageTurnType.LEFT_RIGHT_COVERAGE;
        public static final int PAGE_TURN_TOPBOTTOM = PageTurn.PageTurnType.TOP_BOTTOM_COVERAGE;
        public static final int PAGE_TURN_SIMULATION = PageTurn.PageTurnType.SIMULATION;
        public static final int PAGE_TURN_ALPHA = PageTurn.PageTurnType.ALPHA;
        public static final int PAGE_TURN_NONE = PageTurn.PageTurnType.NONE;
    }

    public static class ThemeOption{
        public static final int IMG = 0;
        public static final int COLOR = 1;
    }

    public static class ThemeBgColor{
        public static final String COLOR_1 = "#EBF0D3";
        public static final String COLOR_2 = "#DCEFF4";
        public static final String COLOR_3 = "#F8E7E7";
    }

    public static class ThemeBgImg{
        public static final int IMG_DARK_GRAY = 0;
        public static final int IMG_GRAY = 1;
        public static final int IMG_KRAFT_PAPER = 2;
    }
}
