package com.yufeng.ireader.ui.home.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yufeng.ireader.R;
import com.yufeng.ireader.db.book.Book;
import com.yufeng.ireader.reader.activity.ReadActivity;
import com.yufeng.ireader.ui.base.BaseActivity;
import com.yufeng.ireader.ui.home.adapter.BookShelfAdapter;
import com.yufeng.ireader.ui.home.callback.OnBookQueryListener;
import com.yufeng.ireader.ui.home.callback.onItemClickListener;
import com.yufeng.ireader.utils.BookHelper;

import java.util.List;

public class MainActivity extends BaseActivity implements onItemClickListener , OnBookQueryListener{

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
        bookShelfAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        BookHelper.getLocalBooksInDirectory(this);
    }

    @Override
    public void onBookQuery(List<Book> bookList) {
        bookShelfAdapter.setData(bookList);
    }

    @Override
    public void onItemClick(int position) {
        String path = bookShelfAdapter.getItem(position).getPath();
        ReadActivity.startActivity(this, path);
    }
}
