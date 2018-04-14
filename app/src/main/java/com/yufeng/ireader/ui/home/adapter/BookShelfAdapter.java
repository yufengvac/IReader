package com.yufeng.ireader.ui.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yufeng.ireader.R;
import com.yufeng.ireader.ui.beans.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/14.
 *
 */

public class BookShelfAdapter extends RecyclerView.Adapter<BookShelfAdapter.ViewHolder>{
    private List<Book> mData ;
    public BookShelfAdapter(){
        if (mData == null){
            mData = new ArrayList<>();
        }
    }

    public void setData(List<Book> list){
        if (mData == null){
            mData = new ArrayList<>();
        }
        mData.clear();
        if (list != null){
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public BookShelfAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_shelf, parent, false));
    }

    @Override
    public void onBindViewHolder(BookShelfAdapter.ViewHolder holder, int position) {
        holder.bookNameTv.setText(mData.get(position).getBookName());
        holder.bookDescTv.setText(mData.get(position).getBookDesc());
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size():0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView bookNameTv;
        private TextView bookDescTv;
        private ViewHolder(View itemView) {
            super(itemView);
            bookNameTv = itemView.findViewById(R.id.item_book_shelf_book_name_tv);
            bookDescTv = itemView.findViewById(R.id.item_book_shelf_book_desc_tv);
        }
    }
}
