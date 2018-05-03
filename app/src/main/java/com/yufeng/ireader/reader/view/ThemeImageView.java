package com.yufeng.ireader.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.yufeng.ireader.R;

/**
 * Created by yufeng on 2018/5/3-0003.
 *
 */

public class ThemeImageView extends AppCompatImageView {

    private Drawable selectedDrawable;
    private Drawable imgDrawable;
    private int size;
    private Rect rect;
    private Rect selectedRect;
    private boolean isSelected = false;
    public ThemeImageView(Context context) {
        this(context, null);
    }

    public ThemeImageView(Context context, AttributeSet attrs) {
       this(context, attrs, 0);
    }

    public ThemeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        selectedDrawable = ContextCompat.getDrawable(context, R.drawable.read_menu_theme_selected);

        imgDrawable = getDrawable();
        if (imgDrawable == null){
            imgDrawable = getBackground();
        }

        rect = new Rect();
        selectedRect = new Rect();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        rect.left = 0;
        rect.top = 0;
        rect.right = size;
        rect.bottom = size;
        imgDrawable.setBounds(rect);

        imgDrawable.draw(canvas);

        if (isSelected){
            selectedRect.left = size/2-selectedDrawable.getIntrinsicWidth()/2;
            selectedRect.top = size/2- selectedDrawable.getIntrinsicHeight()/2;
            selectedRect.right = size/2 + selectedDrawable.getIntrinsicWidth()/2;
            selectedRect.bottom = size/2 + selectedDrawable.getIntrinsicHeight()/2;
            selectedDrawable.setBounds(selectedRect);
            selectedDrawable.draw(canvas);
        }

    }

    public void setSelected(){
        isSelected = true;
        invalidate();
    }
    public void clearSelected(){
        isSelected = false;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        size = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size,size);
    }
}
