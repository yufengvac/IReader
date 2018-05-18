package com.yufeng.ireader.ui.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yufeng.ireader.R;
import com.yufeng.ireader.db.book.Book;
import com.yufeng.ireader.ui.home.callback.onItemClickListener;
import com.yufeng.ireader.utils.BookHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yufeng on 2018/4/14.
 *
 */

public class BookShelfAdapter extends RecyclerView.Adapter<BookShelfAdapter.ViewHolder>{
    private List<Book> mData ;
    private onItemClickListener listener;
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

    public Book getItem(int position){
        return mData.get(position);
    }

    @Override
    public BookShelfAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_shelf, parent, false));
    }

    @Override
    public void onBindViewHolder(BookShelfAdapter.ViewHolder holder, int position) {
        Book book = mData.get(position);
        holder.bookNameTv.setText(book.getBookName());
        String desc = "\u3000\u3000"+book.getBookDesc();
        holder.bookDescTv.setText(desc);

        holder.sizeTv.setText(BookHelper.transFormFromByte(book.getSize()));

        if (book.getLastReadTime() == -1){
            holder.readTimeTv.setText("未阅读");
        }else {
            String percentStr = BookHelper.tranFormFromReadPercent(book.getPercent());
            if (TextUtils.isEmpty(percentStr)){
                holder.readTimeTv.setText("未阅读");
            }else {
                String str = "已阅读"+ percentStr;
                holder.readTimeTv.setText(str);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView bookNameTv;
        private TextView bookDescTv;
        private TextView sizeTv;
        private TextView readTimeTv;
        private ViewHolder(View itemView) {
            super(itemView);
            bookNameTv = itemView.findViewById(R.id.item_book_shelf_book_name_tv);
            bookDescTv = itemView.findViewById(R.id.item_book_shelf_book_desc_tv);
            sizeTv = itemView.findViewById(R.id.item_book_size_tv);
            readTimeTv = itemView.findViewById(R.id.item_book_read_time_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClick(getLayoutPosition());
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }
}
