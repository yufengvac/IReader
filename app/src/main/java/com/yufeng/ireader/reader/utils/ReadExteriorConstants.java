package com.yufeng.ireader.reader.utils;

import com.yufeng.ireader.reader.viewinterface.PageTurn;

/**
 * Created by yfueng on 2018/5/2-0002.
 *
 */

public class ReadExteriorConstants {

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
}
