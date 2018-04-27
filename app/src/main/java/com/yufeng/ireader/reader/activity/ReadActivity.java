package com.yufeng.ireader.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.utils.HardWareManager;
import com.yufeng.ireader.reader.utils.ReadExteriorHelper;
import com.yufeng.ireader.reader.view.MenuSetView;
import com.yufeng.ireader.reader.viewimpl.ReadMenuSetView;
import com.yufeng.ireader.reader.viewimpl.ReadSetting;
import com.yufeng.ireader.reader.view.ReadView;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.reader.viewinterface.OnMenuListener;
import com.yufeng.ireader.reader.viewinterface.OnReadMenuClickListener;
import com.yufeng.ireader.ui.base.BaseActivity;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.DisplayConstant;
import com.yufeng.ireader.utils.PathHelper;

/**
 * Created by yufeng on 2018/4/11.
 *
 */

public class ReadActivity extends BaseActivity implements OnMenuListener, OnReadMenuClickListener{
    private static final String TAG = ReadActivity.class.getSimpleName();
    private String path;
    private static final String KEY_PATH = "path";

    private ReadView readView;
    private IReadSetting readSetting;
    private ReadMenuSetView readMenuSetView;

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
        DisplayConstant.init(DisPlayUtil.getDisplayWidth(this),DisPlayUtil.getDisplayHeight(this));

        if (HardWareManager.canOpenHardware()){
            readView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        if (readMenuSetView == null){
            readMenuSetView = new ReadMenuSetView(this);
        }
    }

    @Override
    public void initListener() {
        path = getIntent().getStringExtra(KEY_PATH);

        readSetting = new ReadSetting();
        readSetting.setContentPaint(readView.getContentPaint());

        readView.setOnMenuListener(this);
        readMenuSetView.setBookName(PathHelper.getBookNameByPath(path));
    }

    @Override
    public void initData() {
        readView.prepare(this,readSetting,path);
    }

    @Override
    public void onClickMenu() {
        if (readMenuSetView == null){
            readMenuSetView = new ReadMenuSetView(this);
        }
        if (readMenuSetView.isMenuShowing()){
            readMenuSetView.hide();
        }else {
            readMenuSetView.show();
        }
    }

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

    }

    @Override
    public void onDayNightClick(View view) {
        readView.changeDayNightMode();
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
    }
}
