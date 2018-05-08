package com.yufeng.ireader.reader.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.adapter.CatalogAdapter;
import com.yufeng.ireader.reader.db.ReadChapter;
import com.yufeng.ireader.reader.service.ChapterService;
import com.yufeng.ireader.reader.viewinterface.OnChapterSplitListener;
import com.yufeng.ireader.ui.base.BaseActivity;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by yufeng on 2018/5/8.
 * 书籍目录页面
 */

public class CatalogActivity extends BaseActivity implements OnChapterSplitListener {
    private RecyclerView catalogRecyclerView;
    private ChapterService chapterService;
    private TextView percentTv;
    private CatalogAdapter catalogAdapter;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    @Override
    public int getLayoutRes() {
        return R.layout.activity_catalog;
    }

    @Override
    public void initView() {
        catalogRecyclerView = findViewById(R.id.catalog_recycler_view);
        percentTv = findViewById(R.id.catalog_percent_tv);
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

    @Override
    public void onError(String msg) {

    }

    @Override
    public void onCompleted(final List<ReadChapter> readChapterList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (percentTv.getVisibility() == View.VISIBLE){
                    percentTv.setVisibility(View.GONE);
                }
                catalogAdapter.setData(readChapterList);
            }
        });
    }

    @Override
    public void onSplitting(final float percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (percentTv.getVisibility() != View.VISIBLE){
                    percentTv.setVisibility(View.VISIBLE);
                }
                String result = "正在解析章节-" + percent * 100 + "%";
                percentTv.setText(result);
            }
        });
    }

    ServiceConnection chapterConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChapterService.ChapterBinder chapterBinder = (ChapterService.ChapterBinder) service;
            chapterService = chapterBinder.getService();
            chapterService.setOnChapterSplitListener(CatalogActivity.this);
            chapterService.startSplitChapter();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            chapterService = null;
        }
    };

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
