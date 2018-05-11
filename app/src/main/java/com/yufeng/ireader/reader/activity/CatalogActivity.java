package com.yufeng.ireader.reader.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.adapter.CatalogAdapter;
import com.yufeng.ireader.reader.bean.ReadChapter;
import com.yufeng.ireader.reader.service.ChapterService;
import com.yufeng.ireader.reader.viewinterface.OnChapterSplitListener;
import com.yufeng.ireader.ui.base.BaseActivity;
import com.yufeng.ireader.ui.view.SplitChapterProgressView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by yufeng on 2018/5/8.
 * 书籍目录页面
 */

public class CatalogActivity extends BaseActivity implements OnChapterSplitListener {
    private RecyclerView catalogRecyclerView;
    private ChapterService chapterService;
    private CatalogAdapter catalogAdapter;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private SplitChapterProgressView progressView;
    @Override
    public int getLayoutRes() {
        return R.layout.activity_catalog;
    }

    @Override
    public void initView() {
        catalogRecyclerView = findViewById(R.id.catalog_recycler_view);

        progressView = new SplitChapterProgressView(this,"正在智能断章...",R.style.Theme_AppCompat_Dialog_Alert);
    }

    @Override
    public void initData() {
        Intent intent = new Intent(this, ChapterService.class);
        bindService(intent,chapterConn, BIND_AUTO_CREATE);
    }

    @Override
    public void initListener() {
        catalogAdapter = new CatalogAdapter();
        catalogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        catalogRecyclerView.setAdapter(catalogAdapter);
    }

    public int setEnterAnimation(){
        return R.anim.left_in;
    }


    @Override
    public void onError(String msg) {

    }

    @Override
    public void onCompleted(final List<ReadChapter> readChapterList) {

        if (progressView != null && progressView.isShowing()){
            progressView.hide();
        }
        catalogAdapter.setData(readChapterList);

    }

    @Override
    public void onSplitting(final float percent) {

        if (progressView != null && !progressView.isShowing()){
            progressView.show();
        }
        String result = "正在解析章节-" + percent * 100 + "%";
        Log.i("CatalogActivity",result);
//        leafLoadingView.setProgress((int) (percent * 100));
        progressView.setProgress((int) (percent * 100));

    }

    ServiceConnection chapterConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChapterService.ChapterBinder chapterBinder = (ChapterService.ChapterBinder) service;
            chapterService = chapterBinder.getService();
            chapterService.setOnChapterSplitListener(CatalogActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            chapterService = null;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !progressView.isShowing()){
            finish();
//            overridePendingTransition(R.anim.hold, R.anim.left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chapterConn != null){
            unbindService(chapterConn);
            chapterConn = null;
        }
    }
}
