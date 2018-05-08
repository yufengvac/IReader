package com.yufeng.ireader.reader.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.yufeng.ireader.reader.bean.Chapter;
import com.yufeng.ireader.reader.bean.TxtParagraph;
import com.yufeng.ireader.reader.db.ReadChapter;
import com.yufeng.ireader.reader.utils.ChapterUtil;
import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.OnChapterSplitListener;
import com.yufeng.ireader.utils.BookHelper;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yufeng on 2018/5/7-0007.
 *
 */

public class ChapterService extends Service{
    private static final String TAG = ChapterService.class.getSimpleName();
    private static final int MAX_TEMP_BYTE_SIZE = 1<<17;

    String bookPath;
    public static final String KEY_BOOK_PATH = "book_path";
    private OnChapterSplitListener onChapterSplitListener;
    private ReadRandomAccessFile readRandomAccessFile;
    private List<ReadChapter> readChapterList;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        bookPath = intent.getStringExtra(KEY_BOOK_PATH);
        return new ChapterBinder();
    }

    public class ChapterBinder extends Binder{
        public ChapterService getService(){
            return ChapterService.this;
        }
    }

    public void startSplitChapter(){
        Single.create(new SingleOnSubscribe<Void>() {
            @Override
            public void subscribe(SingleEmitter<Void> singleEmitter) throws Exception {
                boolean isPrepared = prepareWork();
                beginSplitChapter(isPrepared);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).toFuture();
    }

    public void setOnChapterSplitListener(OnChapterSplitListener listener){
        this.onChapterSplitListener = listener;
    }


    private boolean prepareWork(){
        try {
            if (onChapterSplitListener == null){
                return false;
            }

            if (TextUtils.isEmpty(bookPath)){
                onChapterSplitListener.onError(OnChapterSplitListener.ERROR_MSG.PATH_NULL);
            }

            if (!new File(bookPath).exists()){
                onChapterSplitListener.onError(OnChapterSplitListener.ERROR_MSG.BOOK_NULL);
            }

            if (readRandomAccessFile != null){
                readRandomAccessFile.close();
                readRandomAccessFile = null;
            }

            readRandomAccessFile = new ReadRandomAccessFile(bookPath, "r");
            readRandomAccessFile.setCurPosition(0);
            int code = CodeUtil.regCode(bookPath);
            readRandomAccessFile.setCode(code);
            readRandomAccessFile.setSize(readRandomAccessFile.length());

            readChapterList = new ArrayList<>();

            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private void beginSplitChapter(boolean isPrepared){
        if (!isPrepared){
            return;
        }
        try {
            long maxLength = readRandomAccessFile.getSize();
            long seekStart;
            while (readRandomAccessFile.getCurPosition() < maxLength){

                seekStart = readRandomAccessFile.getCurPosition();
                byte[] tempBuf = new byte[MAX_TEMP_BYTE_SIZE];
                readRandomAccessFile.read(tempBuf);
                String curLine = TxtParagraph.getParagraphString(readRandomAccessFile , seekStart, tempBuf);

                if (TextUtils.isEmpty(curLine)){
                    break;
                }

                ChapterUtil.prepareStartSplitChapter(readRandomAccessFile.getCurPosition(), curLine, maxLength, readChapterList, onChapterSplitListener);
                ChapterUtil.startSplitChapter();

            }
            if (onChapterSplitListener != null){
                onChapterSplitListener.onCompleted(readChapterList);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
