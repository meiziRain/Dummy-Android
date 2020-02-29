package com.meizi.dummy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.meizi.dummy.ui.conversation.ConversationFragment;
import com.meizi.dummy.ui.base.ConversationRefreshListener;
import com.meizi.dummy.ui.base.GetFragmentListener;
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
import com.tencent.imsdk.session.SessionWrapper;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import cn.hutool.core.codec.Base64;

public class MainActivity extends AppCompatActivity implements GetFragmentListener {


    private String activity_tag = "MainActivity:";
    private static final String TIM_TAG = "TIM:";
    private static final int REQUEST_SIGNUP = 0;


    /**
     * 腾讯云 SDKAppId，需要替换为您自己账号下的 SDKAppId。
     * <p>
     * 进入腾讯云云通信[控制台](https://console.cloud.tencent.com/avc ) 创建应用，即可看到 SDKAppId，
     * 它是腾讯云用于区分客户的唯一标识。
     */
    public static final int SDKAPPID = 1400295156;

    private ConversationRefreshListener conversationRefreshListener;
    private ConversationFragment conversationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar背景颜色
        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(drawable);
        getSupportActionBar().setElevation(0);//去除标题栏阴影.
        // TODO: 资料说ActionBar不如ToolBar方便，
        //  但是不知道怎么解决原生BottomNavigationView与设置NoActionBar的闪退Bug
        //  所以此处直接隐藏ActionBar
//        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_conversation, R.id.navigation_contact, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        // 腾讯IM SDK 初始化
        if (SessionWrapper.isMainProcess(getApplicationContext())) {
            initIM(getApplicationContext());
        }

        // 判断是否已经登录过
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        Boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        if (!rememberMe) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            String email = sharedPreferences.getString("email", "");
            String sig = sharedPreferences.getString("sig", "");
            TIMManager.getInstance().login(email, Base64.decodeStr(sig), new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    // 错误码 code 和错误描述 desc，可用于定位请求失败原因
                    // 错误码 code 列表请参见错误码表
                    Log.d(activity_tag, "TIM登录失败. code: " + code + " errorMsg: " + desc);
                }

                @Override
                public void onSuccess() {
                    Log.d(activity_tag, "TIM登录成功!");
                    DummyKit.user = email;
                }
            });
        }
    }

    private void initIM(Context context) {
        TIMSdkConfig sdkConfig = new TIMSdkConfig(SDKAPPID);
        //初始化 SDK
        TIMManager.getInstance().init(context, sdkConfig);
        //基本用户配置
        TIMUserConfig userConfig = new TIMUserConfig()
                //设置用户状态变更事件监听器
                .setUserStatusListener(new TIMUserStatusListener() {
                    @Override
                    public void onForceOffline() {
                        //被其他终端踢下线
                        Log.i(TIM_TAG, "onForceOffline");
                    }

                    @Override
                    public void onUserSigExpired() {
                        //用户签名过期了，需要刷新 userSig 重新登录 IM SDK
                        Log.i(TIM_TAG, "onUserSigExpired");
                    }
                })
                //设置连接状态事件监听器
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                        Log.i(TIM_TAG, "onConnected");
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                        Log.i(TIM_TAG, "onDisconnected");
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                        Log.i(TIM_TAG, "onWifiNeedAuth");
                    }
                })
                //设置群组事件监听器
                .setGroupEventListener(new TIMGroupEventListener() {
                    @Override
                    public void onGroupTipsEvent(TIMGroupTipsElem elem) {
                        Log.i(TIM_TAG, "onGroupTipsEvent, type: " + elem.getTipsType());
                    }
                })
                //设置会话刷新监听器
                .setRefreshListener(new TIMRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TIM_TAG, "onRefresh");
                        Log.d(activity_tag, "onRefresh--->");
                    }

                    @Override
                    public void onRefreshConversation(List<TIMConversation> conversations) {
                        if (null != conversationFragment) {
                            conversationFragment.refreshConversationList();
                        }
                    }
                });

        //禁用本地所有存储
//        userConfig.disableStorage();
        //开启消息已读回执
        userConfig.enableReadReceipt(true);
        //将用户配置与通讯管理器进行绑定
        TIMManager.getInstance().setUserConfig(userConfig);

    }

    @Override
    public void sendValue(ConversationFragment cf) {
        conversationFragment = cf;
    }
}
