package com.yufeng.ireader.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.utils.HardWareManager;
import com.yufeng.ireader.reader.viewimpl.ReadSetting;
import com.yufeng.ireader.reader.view.ReadView;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.reader.viewinterface.OnMenuListener;
import com.yufeng.ireader.ui.base.BaseActivity;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.DisplayConstant;

/**
 * Created by yufeng on 2018/4/11.
 *
 */

public class ReadActivity extends BaseActivity implements OnMenuListener{
    private static final String TAG = ReadActivity.class.getSimpleName();
    private String path;
    private static final String KEY_PATH = "path";

    private ReadView readView;
    private IReadSetting readSetting;

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
    }

    @Override
    public void onClickMenu() {

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
