package com.yufeng.ireader.reader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.bean.ReadChapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/5/8.
 * 书籍目录适配器
 */

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.MyHolder>{

    private List<ReadChapter> mData;

    public void setData(List<ReadChapter> list){
        if (mData == null){
            mData = new ArrayList<>();
        }else {
            mData.clear();
        }
        if (list != null){
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.orderTv.setText(String.valueOf(position + 1));
        holder.chapterTv.setText(mData.get(position).getChapterName());
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size():0;
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        private TextView orderTv;
        private TextView chapterTv;
        MyHolder(View view){
            super(view);
            orderTv = view.findViewById(R.id.item_catalog_order_tv);
            chapterTv = view.findViewById(R.id.item_catalog_chapter_tv);
        }
    }
}
