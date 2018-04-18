package com.yufeng.ireader.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.graphics.PaintCompat;
import android.util.Log;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.bean.PagerManager;
import com.yufeng.ireader.reader.bean.TxtParagraph;
import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.utils.ReadSetting;
import com.yufeng.ireader.reader.view.ReadView;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.ui.base.BaseActivity;
import com.yufeng.ireader.utils.DisPlayUtil;
import com.yufeng.ireader.utils.DisplayConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/11.
 *
 */

public class ReadActivity extends BaseActivity{
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
    }

    @Override
    public void initListener() {
        path = getIntent().getStringExtra(KEY_PATH);

        readSetting = new ReadSetting();
        readSetting.setContentPaint(readView.getContentPaint());
    }

    @Override
    public void initData() {
        readView.prepare(readSetting,path);
        readView.refresh();
    }

}
