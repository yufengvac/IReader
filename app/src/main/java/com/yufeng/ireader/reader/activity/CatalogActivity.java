
package com.yufeng.ireader.reader.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import com.yufeng.ireader.R;
import com.yufeng.ireader.db.readchapter.ReadChapter;
import com.yufeng.ireader.db.readchapter.ReadChapterHelper;
import com.yufeng.ireader.reader.adapter.CatalogAdapter;
import com.yufeng.ireader.reader.service.ChapterService;
import com.yufeng.ireader.reader.utils.YLog;
import com.yufeng.ireader.reader.viewinterface.OnChapterSplitListener;
import com.yufeng.ireader.ui.base.BaseActivity;
import com.yufeng.ireader.ui.home.callback.onItemClickListener;
import com.yufeng.ireader.ui.view.SplitChapterProgressView;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.PathHelper;

import java.util.List;

/**
 * Created by yufeng on 2018/5/8.
 * 书籍目录页面
 */

public class CatalogActivity extends BaseActivity implements OnChapterSplitListener , onItemClickListener {

    public static final String KEY_HAS_CATALOG = "hasCatalog";
    public static final String KEY_BOOK_PATH = "book_path";
    public static final String KEY_CUR_POSITION = "cur_position";

    private TextView bookNameTv;
    private RecyclerView catalogRecyclerView;
    private ChapterService chapterService;
    private CatalogAdapter catalogAdapter;
    private SplitChapterProgressView progressView;
    private boolean hasCatalog = false;
    private String bookPath ;
    private long curPosition;
    private ServiceConnection chapterConn;
    @Override
    public int getLayoutRes() {
        return R.layout.activity_catalog;
    }

    @Override
    public void initView() {
        catalogRecyclerView = findViewById(R.id.catalog_recycler_view);
        DisPlayUtil.setRecyclerViewScrollThumb(this,catalogRecyclerView, R.drawable.icon_scroll_bar);

        hasCatalog = getIntent().getBooleanExtra(KEY_HAS_CATALOG,false);
        bookPath = getIntent().getStringExtra(KEY_BOOK_PATH);
        curPosition = getIntent().getLongExtra(KEY_CUR_POSITION, 0);

        progressView = new SplitChapterProgressView(this,"正在智能断章...",R.style.Theme_AppCompat_Dialog_Alert);

        bookNameTv = findViewById(R.id.catalog_book_name_tv);
    }

    @Override
    public void initData() {

        bookNameTv.setText(PathHelper.getBookNameByPath(bookPath));

        if (hasCatalog && !TextUtils.isEmpty(bookPath)){
            List<ReadChapter> readChapterList = ReadChapterHelper.getAllReadChapterList(bookPath);
            if (readChapterList != null && readChapterList.size() > 0){
                YLog.i(CatalogActivity.this, "路径"+bookPath+",目录条数为"+readChapterList.size());

                setSelectedPosition(readChapterList);
                catalogAdapter.setData(readChapterList);
            }
        }else {

            chapterConn = new ServiceConnection() {
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

            Intent intent = new Intent(this, ChapterService.class);
            bindService(intent,chapterConn, BIND_AUTO_CREATE);
        }

    }

    /**
     * 设置当前的章节高亮
     * @param readChapters 章节列表
     */
    private void setSelectedPosition(List<ReadChapter> readChapters){
        int selected = 0;
        for (int i = 0 ;i < readChapters.size() ; i++){
            if (readChapters.get(i).getCurPosition() <= curPosition){
                selected = i;
            }else {
                break;
            }
        }
        catalogRecyclerView.getLayoutManager().scrollToPosition(selected >0 ?(selected -1):0);
        catalogAdapter.setSelectedPos(selected);
    }

    @Override
    public void initListener() {
        catalogAdapter = new CatalogAdapter();
        catalogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        catalogRecyclerView.setAdapter(catalogAdapter);
        catalogAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        ReadChapter readChapter = catalogAdapter.getItem(position);
        if (readChapter != null){
            Intent intent = new Intent();
            intent.putExtra(KEY_CUR_POSITION, readChapter.getCurPosition());
            setResult(RESULT_OK, intent);
            finish();
        }
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
        setSelectedPosition(readChapterList);
        catalogAdapter.setData(readChapterList);

    }

    @Override
    public void onSplitting(final float percent) {

        if (progressView != null && !progressView.isShowing()){
            progressView.show();
        }
        String result = "正在解析章节-" + percent * 100 + "%";
        YLog.i(this,result);
//        leafLoadingView.setProgress((int) (percent * 100));
        progressView.setProgress((int) (percent * 100));

    }


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
