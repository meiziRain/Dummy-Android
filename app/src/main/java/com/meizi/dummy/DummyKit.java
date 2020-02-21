package com.meizi.dummy;

import android.content.Context;

/**
 * @Classname DummyKit
 * @Description TODO
 * @Date 2020/2/19 15:40
 * @Created by jion
 */
public class DummyKit {

    private static Context sAppContext;

    // 用户当前登录用户建立单独文件夹，存储改登录账户数据
    public static String user="";

    public static void init(Context context) {
        sAppContext = context;
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
