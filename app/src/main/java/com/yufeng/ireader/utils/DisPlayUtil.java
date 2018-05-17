package com.yufeng.ireader.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.yufeng.ireader.reader.utils.YLog;

import java.lang.reflect.Field;

/**
 * Created by yufeng on 2018/4/15.
 *
 */

public class DisPlayUtil {

    public static int getDisplayWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDisplayHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * convert px to its equivalent dp
     *
     * 将px转换为与之相等的dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale =  context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * convert dp to its equivalent px
     *
     * 将dp转换为与之相等的px
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * convert px to its equivalent sp
     *
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * convert sp to its equivalent px
     */
    public static int sp2px(Context context , int sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,context.getResources().getDisplayMetrics());
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        return result;
    }

    public static void setRecyclerViewScrollThumb(Context context, RecyclerView recyclerView, int id) {
        try {
            Field f;
            try {
                f = ScrollView.class.getDeclaredField("mFastScroller");
            } catch (Exception e) {
                f = ScrollView.class.getDeclaredField("mFastScroll");
            }
            f.setAccessible(true);
            Object o = f.get(recyclerView);
            try {
                int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
                if (targetSdkVersion >= Build.VERSION_CODES.KITKAT &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    try {
                        setFastScrollReflectImageView(context, f, o, id);
                    } catch (Exception e) {
                        setFastScrollReflectDrawable(context, f, o, id);
                    }
                } else {
                    setFastScrollReflectDrawable(context, f, o, id);
                }
            } catch (Exception e) {
                setFastScrollReflectImageView(context, f, o, id);
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private static void setFastScrollReflectImageView(Context context, Field f, Object o, int id)
            throws Exception {
        Field f1 = f.getType().getDeclaredField("mThumbImage");
        f1.setAccessible(true);
        ImageView imageView = (ImageView) f1.get(o);
        imageView.setImageDrawable(context.getResources().getDrawable(id));

        Field f2 = f.getType().getDeclaredField("mTrackImage");
        f2.setAccessible(true);
        ImageView trackImageView = (ImageView) f2.get(o);
        trackImageView.setVisibility(View.GONE);
    }

    private static void setFastScrollReflectDrawable(Context context, Field f, Object o, int id)
            throws Exception {
        Field f1 = f.getType().getDeclaredField("mThumbDrawable");
        f1.setAccessible(true);
        Drawable drawable = (Drawable) f1.get(o);
        drawable = context.getResources().getDrawable(id);
        f1.set(o, drawable);

        Field f2 = f.getType().getDeclaredField("mTrackDrawable");
        f2.setAccessible(true);
        f2.set(o, null);
    }

}
