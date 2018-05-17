package com.yufeng.ireader.reader.utils;

import android.app.Activity;
import android.app.Service;

import java.util.Map;

import static com.yufeng.ireader.BuildConfig.debug;

/**
 * Created by yufeng on 2018/5/17-0017.
 *
 */

public class YLog {

    private YLog() {
    }

    public static void v(Activity activity, String msg) {
        if (debug) {
            android.util.Log.v(activity.getClass().getSimpleName(), buildMessage(msg));
        }
    }

    public static void d(Activity activity,String msg) {
        if (debug) {
            android.util.Log.d(activity.getClass().getSimpleName(), buildMessage(msg));
        }
    }

    public static void i(Activity activity,String msg) {
        if (debug) {
            android.util.Log.i(activity.getClass().getSimpleName(), buildMessage(msg));
        }
    }

    public static void i(Service service, String msg) {
        if (debug) {
            android.util.Log.i(service.getClass().getSimpleName(), buildMessage(msg));
        }
    }

    public static void w(Activity activity,String msg) {
        if (debug) {
            android.util.Log.w(activity.getClass().getSimpleName(), buildMessage(msg));
        }
    }

    public static void e(Activity activity,String msg) {
        if (debug) {
            android.util.Log.e(activity.getClass().getSimpleName(), buildMessage(msg));
        }
    }
    

    public static void es(Activity activity,String... msg) {
        StringBuilder sb = new StringBuilder();
        for (String s : msg) {
            sb.append(s).append("=");
        }
        String result = "";
        if (sb.length() > 1) {
            result = sb.substring(0, sb.length() - 1);
        }
        android.util.Log.e(activity.getClass().getSimpleName(), buildMessage(result));
    }

    public static void log_request(Activity activity,String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        for (String s : params.keySet()) {
            sb.append(s).append("=").append(params.get(s)).append("&");
        }
        String s = sb.substring(0, sb.length() - 1);
        if (debug) {
            android.util.Log.w(activity.getClass().getSimpleName(), s);
        }
    }

    private static String buildMessage(String msg) {
		/*StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];

		return new StringBuilder()
		        .append(caller.getClassName())
		        .append(".")
		        .append(caller.getMethodName())
		        .append("(): ")
		        .append(msg).toString();*/
        return msg;
    }
}
