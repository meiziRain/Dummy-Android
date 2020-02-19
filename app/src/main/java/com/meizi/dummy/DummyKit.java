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

    public static void init(Context context) {
        sAppContext = context;
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
