package com.yufeng.ireader.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.bean.TxtParagraph;
import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.utils.ReadSetting;
import com.yufeng.ireader.reader.view.ReadView;
import com.yufeng.ireader.reader.viewinterface.IReadSetting;
import com.yufeng.ireader.ui.base.BaseActivity;
import com.yufeng.ireader.utils.DisPlayUtil;

import java.io.IOException;

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
    }

    @Override
    public void initListener() {
        path = getIntent().getStringExtra(KEY_PATH);

        readSetting = new ReadSetting();
        readSetting.setContentPaint(readView.getContentPaint());
        readView.setReadSetting(readSetting);
    }

    @Override
    public void initData() {
        new Thread(){
            @Override
            public void run() {
                Log.e(TAG,"realPath="+path);
//                final ArrayList<String> contentList = PathHelper.getContentByPath(path);
//                if (contentList != null && contentList.size() > 0){
//                    for (int i = 0 ; i < 20 ; i++){
//                        Log.e(TAG,contentList.get(i));
//                    }
//                    Log.e(TAG,"大小是："+contentList.size());
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        readView.setParagraphList(contentList);
//                    }
//                });
                try {
                    ReadRandomAccessFile randomAccessFile = new ReadRandomAccessFile(path,"r");
                    int code = CodeUtil.regCode(path);
                    Log.i(TAG,"字符编码是："+ CodeUtil.getEncodingByCode(code));
//                    while (true){
                    randomAccessFile.setCode(code);
                        TxtParagraph txtParagraph = TxtParagraph.createTxtParagraph(randomAccessFile, DisPlayUtil.getDisplayWidth(ReadActivity.this), readSetting);
//                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }.start();
    }

}
