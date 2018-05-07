package com.yufeng.ireader.reader.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.db.ReadChapter;
import com.yufeng.ireader.reader.service.ChapterService;
import com.yufeng.ireader.reader.utils.HardWareManager;
import com.yufeng.ireader.reader.view.ReadView;
import com.yufeng.ireader.reader.viewimpl.ReadMenuSetView;
import com.yufeng.ireader.reader.viewimpl.ReadMenuSettingView;
import com.yufeng.ireader.reader.viewimpl.ReadSetting;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.reader.viewinterface.OnChapterSplitListener;
import com.yufeng.ireader.reader.viewinterface.OnMenuListener;
import com.yufeng.ireader.reader.viewinterface.OnReadMenuClickListener;
import com.yufeng.ireader.reader.viewinterface.OnReadViewChangeListener;
import com.yufeng.ireader.ui.base.BaseActivity;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.PathHelper;
import com.yufeng.ireader.utils.ReadPreferHelper;

import java.util.List;

/**
 * Created by yufeng on 2018/4/11.
 *
 */

public class ReadActivity extends BaseActivity implements OnMenuListener, OnReadMenuClickListener, OnReadViewChangeListener, OnChapterSplitListener {
    private static final String TAG = ReadActivity.class.getSimpleName();
    private String path;
    private static final String KEY_PATH = "path";

    private ReadView readView;
    private IReadSetting readSetting;
    private ReadMenuSetView readMenuSetView;
    private ReadMenuSettingView readMenuSettingView;

    public static void startActivity(Context context, String path){
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra(KEY_PATH,path);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_read;
    }

    @Override
    public void initView() {
        readView = findViewById(R.id.activity_read_view);

        DisplayConstant.init(DisPlayUtil.getDisplayWidth(this), DisPlayUtil.getDisplayHeight(this));
        if (ReadPreferHelper.getInstance().getImmersiveRead()){
            DisplayConstant.initStatusBarHeight(DisPlayUtil.getStatusBarHeight(this));
        }

        if (HardWareManager.canOpenHardware()){
            readView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

    }

    @Override
    public void initListener() {
        path = getIntent().getStringExtra(KEY_PATH);

        readSetting = new ReadSetting();
        readSetting.setContentPaint(readView.getContentPaint());

        readView.setOnMenuListener(this);

    }

    @Override
    public void initData() {
        readView.prepare(this,readSetting,path);
        readMenuSetView = new ReadMenuSetView(this,readSetting);
        readMenuSetView.setBookName(PathHelper.getBookNameByPath(path));

        readMenuSettingView = new ReadMenuSettingView(this,readSetting);

        Intent intent = new Intent(this, ChapterService.class);
        intent.putExtra(ChapterService.KEY_BOOK_PATH, path);
        bindService(intent,chapterConn, BIND_AUTO_CREATE);
    }

    ServiceConnection chapterConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChapterService.ChapterBinder chapterBinder = (ChapterService.ChapterBinder) service;
            ChapterService chapterService = chapterBinder.getService();
            chapterService.setOnChapterSplitListener(ReadActivity.this);
            chapterService.startSplitChapter();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    /***************************OnMenuListener**********************************************/
    @Override
    public void onClickMenu() {
        if (readMenuSetView == null){
            readMenuSetView = new ReadMenuSetView(this,readSetting);
        }
        if (readMenuSetView.isMenuShowing()){
            readMenuSetView.hide();
        }else {
            readMenuSetView.show();
        }
    }



    /***************************OnReadMenuClickListener******************************************/
    @Override
    public void onCategoryClick(View view) {

    }

    @Override
    public void onBrightnessClick(View view) {

    }

    @Override
    public void onListenClick(View view) {

    }

    @Override
    public void onSettingClick(View view) {
        if (readMenuSettingView == null){
            readMenuSettingView = new ReadMenuSettingView(this,readSetting);
        }
        if (!readMenuSettingView.isMenuShowing()){
            readMenuSettingView.show();
        }else {
            readMenuSettingView.hide();
        }
    }



    /*************************OnReadViewChangeListener*******************************************/
    @Override
    public void onReadViewChange(boolean isForcedCalc) {
        readView.refreshReadView(isForcedCalc);
    }

    @Override
    public void onReadViewPageTurnChange() {
        readView.recreatePageTurn(readSetting);
    }




    /*************************OnChapterSplitListener**********************************/
    @Override
    public void onError(String msg) {

    }

    @Override
    public void onCompleted(List<ReadChapter> readChapterList) {
        for (int i = 0 ; i < readChapterList.size() ; i++){
            Log.e(TAG,readChapterList.get(i).toString());
        }
    }

    @Override
    public void onSplitting(float percent) {
        Log.i(TAG,"正在进行解析章节-进度"+percent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        readView.saveHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        readView.onDestroy();
        if (chapterConn != null){
            unbindService(chapterConn);
            chapterConn = null;
        }
    }
}
