package com.yufeng.ireader.ui.home.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yufeng.ireader.R;
import com.yufeng.ireader.reader.ReaderActivity;
import com.yufeng.ireader.ui.base.BaseActivity;
import com.yufeng.ireader.ui.beans.Book;
import com.yufeng.ireader.ui.home.adapter.BookShelfAdapter;
import com.yufeng.ireader.utils.BookHelper;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView bookRecyclerView;
    private BookShelfAdapter bookShelfAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        bookRecyclerView = findViewById(R.id.book_recycler_view);
    }

    @Override
    public void initListener() {
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookShelfAdapter = new BookShelfAdapter();
        bookRecyclerView.setAdapter(bookShelfAdapter);
    }

    @Override
    public void initData() {
        List<Book> bookList = BookHelper.getLocalBooksInDirectory();
        bookShelfAdapter.setData(bookList);
    }

    public void openBook(View view){
        startActivity(new Intent(this, ReaderActivity.class));
    }
}
