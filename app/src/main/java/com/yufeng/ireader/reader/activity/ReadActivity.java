package com.yufeng.ireader.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.graphics.PaintCompat;
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
                    randomAccessFile.setCode(code);
                    int displayHeight = DisPlayUtil.getDisplayHeight(ReadActivity.this);

                    Paint.FontMetrics fontMetrics = readSetting.getContentPaint().getFontMetrics();
                    float startOffsetY = readSetting.getPaddingTop() + fontMetrics.bottom - fontMetrics.top;
                    List<TxtParagraph> drawTxtParaList = new ArrayList<>();
                    while (true){
                        TxtParagraph txtParagraph = TxtParagraph.createTxtParagraph(randomAccessFile, DisPlayUtil.getDisplayWidth(ReadActivity.this), readSetting);
                        drawTxtParaList.add(txtParagraph);
                        float resultOffsetY =  txtParagraph.calculatorOffsetY(readSetting, startOffsetY, displayHeight, readView);
                        if (resultOffsetY == -1){
                            Log.e(TAG,"第一个页面已经全部获取完了 resultOffsetY==-1");
                            break;
                        }
                        startOffsetY += resultOffsetY;
                        if (startOffsetY >= displayHeight - readSetting.getPaddingBottom()){
                            Log.e(TAG,"第一个页面已经全部获取完了");
                            break;
                        }
                    }
                    readView.setTxtParagraphList(drawTxtParaList);
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }.start();
    }

}
