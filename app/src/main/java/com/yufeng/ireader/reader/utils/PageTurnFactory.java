package com.yufeng.ireader.reader.utils;

import com.yufeng.ireader.reader.viewimpl.LeftRightTranslatePageTurn;
import com.yufeng.ireader.reader.viewimpl.LeftRightCoveragePageTurn;
import com.yufeng.ireader.reader.viewimpl.NonePageTurn;
import com.yufeng.ireader.reader.viewimpl.SimulationPageTurn;
import com.yufeng.ireader.reader.viewimpl.TopBottomCoveragePageTurn;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.reader.viewinterface.PageTurn;

/**
 * Created by yufeng on 2018/4/25-0025.
 * 创建翻页器
 */

public class PageTurnFactory {

    public static PageTurn createPageTurn(IReadSetting readSetting) {
        int pageTurnType = readSetting.getPageTurnType();
        PageTurn pageTurn;
        switch (pageTurnType) {
            case PageTurn.PageTurnType.LEFT_RIGHT_COVERAGE:
                pageTurn = new LeftRightCoveragePageTurn();
                break;
            case PageTurn.PageTurnType.LEFT_RIGHT_TRANSLATION:
                pageTurn = new LeftRightTranslatePageTurn();
                break;
            case PageTurn.PageTurnType.SIMULATION:
                pageTurn = new SimulationPageTurn();
                break;
            case PageTurn.PageTurnType.TOP_BOTTOM_COVERAGE:
                pageTurn = new TopBottomCoveragePageTurn();
                break;
            case PageTurn.PageTurnType.NONE:
                default:
                    pageTurn = new NonePageTurn();
        }
        return pageTurn;
    }
}
