package com.meizi.dummy.utils;

import android.os.Handler;

/**
 * @Classname BackgroundTasks
 * @Description TODO
 * @Date 2020/2/12 14:28
 * @Created by meizi
 */
public class BackgroundTasks {

    private static BackgroundTasks instance;
    private Handler mHandler = new Handler();

    public static BackgroundTasks getInstance() {
        return instance;
    }

    // 需要在主线程中初始化
    public static void initInstance() {
        instance = new BackgroundTasks();
    }

    public void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    public boolean postDelayed(Runnable r, long delayMillis) {
        return mHandler.postDelayed(r, delayMillis);
    }

    public Handler getHandler() {
        return mHandler;
    }


}
