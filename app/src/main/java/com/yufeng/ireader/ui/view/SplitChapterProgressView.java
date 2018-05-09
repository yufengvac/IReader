package com.yufeng.ireader.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yufeng.ireader.R;

/**
 * Created by yufeng on 2018/5/9.
 * 自定义解析章节进度view
 */

public class SplitChapterProgressView extends AlertDialog{
    private LeafLoadingView leafLoadingView;
    private View contentView;

    public SplitChapterProgressView(Context context, String message, int themeResId){
        super(context, themeResId);
        init(context, message);
    }

    private void init(Context context, String message){
        contentView = LayoutInflater.from(context).inflate(R.layout.layout_split_chapter_progress_view, null);
        TextView messageTv = contentView.findViewById(R.id.progress_message_tv);
        messageTv.setText(message);
        leafLoadingView = contentView.findViewById(R.id.progress_leaf_loading_view);
        leafLoadingView.setProgress(0);

    }


    public void setProgress(int progress){
        leafLoadingView.setProgress(progress);
    }

    @Override
    public void show() {
        super.show();
        setContentView(contentView);
    }

    @Override
    public void hide() {
        super.hide();
        if (leafLoadingView != null){
            leafLoadingView.onDestroy();
        }
    }
}
