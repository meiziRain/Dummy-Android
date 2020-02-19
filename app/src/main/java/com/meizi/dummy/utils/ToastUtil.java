package com.meizi.dummy.utils;


import android.app.Application;
import android.content.Context;
import android.widget.Toast;


/**
 * @Classname BackgroundTasks
 * @Description UI通用方法类
 * @Date 2020/2/12 14:28
 * @Created by meizi
 */
public class ToastUtil {
    private static Toast mToast;

    public static final void longMessage(Context context, final String message) {
        BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                mToast = Toast.makeText(context, message,
                        Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }

    public static final void shortMessage(Context context, final String message) {
        BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                mToast = Toast.makeText(context, message,
                        Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }
}
