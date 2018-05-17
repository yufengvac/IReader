package com.yufeng.ireader.reader.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yufeng.ireader.R;
import com.yufeng.ireader.db.readchapter.ReadChapter;
import com.yufeng.ireader.ui.home.callback.onItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/5/8.
 * 书籍目录适配器
 */

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.MyHolder>{

    private List<ReadChapter> mData;
    private int selectedPos = -1;
    private onItemClickListener onItemClickListener;

    private int orderNotSelectColor;
    private int selectColor, contentNotSelectColor;

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

    public void setSelectedPos(int pos){
        this.selectedPos = pos;
    }

    public void setOnItemClickListener(onItemClickListener listener){
        onItemClickListener = listener;
    }

    public ReadChapter getItem(int position){
        return (mData!= null && position >=0 && position < mData.size() )? mData.get(position):null;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        orderNotSelectColor = ContextCompat.getColor(context, R.color.sub_title);

        selectColor = ContextCompat.getColor(context, R.color.book_shelf_not_read);
        contentNotSelectColor = ContextCompat.getColor(context, R.color.catalog_text_color);

        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog, parent, false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.orderTv.setText(String.valueOf(position + 1));
        holder.chapterTv.setText(mData.get(position).getChapterName());
        if (selectedPos == position){
            holder.orderTv.setTextColor(selectColor);
            holder.chapterTv.setTextColor(selectColor);
        }else {
            holder.orderTv.setTextColor(orderNotSelectColor);
            holder.chapterTv.setTextColor(contentNotSelectColor);
        }

    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size():0;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private TextView orderTv;
        private TextView chapterTv;
        MyHolder(View view){
            super(view);
            orderTv = view.findViewById(R.id.item_catalog_order_tv);
            chapterTv = view.findViewById(R.id.item_catalog_chapter_tv);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null){
                        onItemClickListener.onItemClick(getLayoutPosition());
                    }
                }
            });
        }
    }
}
