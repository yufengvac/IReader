package com.yufeng.ireader.reader.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.yufeng.ireader.reader.bean.TxtParagraph;
import com.yufeng.ireader.reader.bean.ReadChapter;
import com.yufeng.ireader.reader.utils.ChapterUtil;
import com.yufeng.ireader.reader.utils.CodeUtil;
import com.yufeng.ireader.reader.utils.ReadRandomAccessFile;
import com.yufeng.ireader.reader.viewinterface.OnChapterSplitListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
    private boolean isStopSplit = false;
    private float curPercent = 0f;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            bookPath = intent.getStringExtra(KEY_BOOK_PATH);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        isStopSplit = false;
        return new ChapterBinder();
    }

    public class ChapterBinder extends Binder{
        public ChapterService getService(){
            return ChapterService.this;
        }
    }

    public void startSplitChapter(){
//        Single.create(new SingleOnSubscribe<Void>() {
//            @Override
//            public void subscribe(SingleEmitter<Void> singleEmitter) throws Exception {
//
//                boolean isPrepared = prepareWork();
//                beginSplitChapter(isPrepared);
//            }
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).toFuture();

        Observable.create(new ObservableOnSubscribe<Float>() {
            @Override
            public void subscribe(ObservableEmitter<Float> observableEmitter) throws Exception {
                boolean isPrepared = prepareWork();
                float percent = beginSplitChapter(isPrepared, observableEmitter);
                if (percent == -1){
                    observableEmitter.onError(null);
                }else if (percent < 100){
                    observableEmitter.onNext(percent);
                }else if (percent == 100){
                    observableEmitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                curPercent = 0f;
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Float>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(Float aFloat) {
                curPercent = aFloat;
                if (onChapterSplitListener != null){
                    onChapterSplitListener.onSplitting(curPercent);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                curPercent = 0f;
            }

            @Override
            public void onComplete() {
                curPercent = 100f;
                if (onChapterSplitListener != null){
                    onChapterSplitListener.onCompleted(readChapterList);
                }
            }
        });

    }

    public void setOnChapterSplitListener(OnChapterSplitListener listener){
        this.onChapterSplitListener = listener;
        if (curPercent == 100){
            onChapterSplitListener.onCompleted(readChapterList);
        }else {
            onChapterSplitListener.onSplitting(curPercent);
        }
    }

    public void endSplitChapter(){
        isStopSplit = true;
        ChapterUtil.reset();
        stopSelf();
    }


    private boolean prepareWork(){
        try {

            if (TextUtils.isEmpty(bookPath)){
                if (onChapterSplitListener != null){
                    onChapterSplitListener.onError(OnChapterSplitListener.ERROR_MSG.PATH_NULL);
                }
            }

            if (!new File(bookPath).exists()){
                if (onChapterSplitListener != null){
                    onChapterSplitListener.onError(OnChapterSplitListener.ERROR_MSG.BOOK_NULL);
                }
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

    private float beginSplitChapter(boolean isPrepared, ObservableEmitter<Float> observableEmitter){
        if (!isPrepared){
            return -1;
        }
        try {
            long maxLength = readRandomAccessFile.getSize();
            long seekStart;
            while (readRandomAccessFile.getCurPosition() < maxLength && !isStopSplit){

                seekStart = readRandomAccessFile.getCurPosition();
                byte[] tempBuf = new byte[MAX_TEMP_BYTE_SIZE];
                readRandomAccessFile.read(tempBuf);
                String curLine = TxtParagraph.getParagraphString(readRandomAccessFile , seekStart, tempBuf);

                if (TextUtils.isEmpty(curLine)){
                    break;
                }

                ChapterUtil.prepareStartSplitChapter(readRandomAccessFile.getCurPosition(), curLine, maxLength, readChapterList, observableEmitter);
                ChapterUtil.startSplitChapter();

            }
            Log.e(TAG,"章节解析完成");
            return 100f;
        }catch (IOException e){
            e.printStackTrace();
            return  -1;
        }
    }

}
