package com.yufeng.ireader.reader.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;

import com.yufeng.ireader.R;
import com.yufeng.ireader.db.readchapter.ReadChapterHelper;
import com.yufeng.ireader.reader.service.ChapterService;
import com.yufeng.ireader.reader.utils.HardWareManager;
import com.yufeng.ireader.reader.view.ReadView;
import com.yufeng.ireader.reader.viewimpl.ReadMenuSetView;
import com.yufeng.ireader.reader.viewimpl.ReadMenuSettingView;
import com.yufeng.ireader.reader.viewimpl.ReadSetting;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.reader.viewinterface.OnMenuListener;
import com.yufeng.ireader.reader.viewinterface.OnReadMenuClickListener;
import com.yufeng.ireader.reader.viewinterface.OnReadViewChangeListener;
import com.yufeng.ireader.ui.base.BaseActivity;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.PathHelper;
import com.yufeng.ireader.utils.ReadPreferHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

/**
 * Created by yufeng on 2018/4/11.
 *
 */

public class ReadActivity extends BaseActivity implements OnMenuListener, OnReadMenuClickListener, OnReadViewChangeListener {
    private String path;
    private static final String KEY_PATH = "path";
    public static final int REQUEST_CODE = 1000;

    private ReadView readView;
    private IReadSetting readSetting;
    private ReadMenuSetView readMenuSetView;
    private ReadMenuSettingView readMenuSettingView;
    private ChapterService chapterService;
    private boolean hasCatalog = false;
    private ServiceConnection chapterConn ;

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

        ReadChapterHelper.getChapterCountAnyc(path, new ReadChapterHelper.Callback<Integer>() {
            @Override
            public void onCallback(Integer integer) {
                if (integer <= 0){
                    hasCatalog = false;
                    startChapterSplitService();
                }else {
                    hasCatalog = true;
                }
            }
        });

    }

    private void startChapterSplitService(){

        chapterConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ChapterService.ChapterBinder chapterBinder = (ChapterService.ChapterBinder) service;
                chapterService = chapterBinder.getService();
                chapterService.startSplitChapter();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                chapterService = null;
            }
        };

        Intent intent = new Intent(this, ChapterService.class);
        intent.putExtra(ChapterService.KEY_BOOK_PATH, path);
        startService(intent);
        bindService(intent,chapterConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            long curSeekStart = data.getLongExtra(CatalogActivity.KEY_CUR_POSITION, -1);
            readView.refreshReadView(true, curSeekStart);
        }
    }

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
        Single.timer(200, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Intent intent = new Intent(ReadActivity.this, CatalogActivity.class);
                intent.putExtra(CatalogActivity.KEY_HAS_CATALOG,hasCatalog);
                intent.putExtra(CatalogActivity.KEY_BOOK_PATH,path);
                intent.putExtra(CatalogActivity.KEY_CUR_POSITION,readView.getCurPosition());
                startActivityForResult(intent,REQUEST_CODE);
//                overridePendingTransition(R.anim.left_in, R.anim.hold);
            }
        });
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

    @Override
    public void onTurnPreChapter(View view) {
        readView.refreshReadView(true, ReadChapterHelper.getPreChapterPosition(path, readView.getCurPosition()));
    }

    @Override
    public void onTurnNextChapter(View view) {
        readView.refreshReadView(true, ReadChapterHelper.getNextChapterPosition(path, readView.getCurPosition()));
    }

    /*************************OnReadViewChangeListener*******************************************/
    @Override
    public void onReadViewChange(boolean isForcedCalc) {
        readView.refreshReadView(isForcedCalc, -1);
    }

    @Override
    public void onReadViewPageTurnChange() {
        readView.recreatePageTurn(readSetting);
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
        if (chapterService != null){
            chapterService.endSplitChapter();
            chapterService = null;
        }
    }
}
