package com.meizi.dummy;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;

import java.util.List;

import cn.hutool.core.codec.Base64;

/**
 * @Classname DummyApplication
 * @Description TODO
 * @Date 2020/2/18 19:41
 * @Created by jion
 */
public class DummyApplication extends Application {
    // AndroidManifest.xml中引入
    private static final String TAG = DummyApplication.class.getSimpleName();
    private static DummyApplication instance;

    public static DummyApplication instance() {
        return instance;
    }

    private String activity_tag = "DummyApplication:";
    private static final String TIM_TAG = "TIM:";
    private static final int REQUEST_SIGNUP = 0;


    /**
     * 腾讯云 SDKAppId，需要替换为您自己账号下的 SDKAppId。
     * <p>
     * 进入腾讯云云通信[控制台](https://console.cloud.tencent.com/avc ) 创建应用，即可看到 SDKAppId，
     * 它是腾讯云用于区分客户的唯一标识。
     */
    public static final int SDKAPPID = 1400295156;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DummyKit.init(getApplicationContext());
    }


}
