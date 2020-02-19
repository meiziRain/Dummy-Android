package com.meizi.dummy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import pub.devrel.easypermissions.EasyPermissions;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dewarder.holdinglibrary.HoldingButtonLayout;
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener;
import com.dewarder.holdinglibrary.SimpleHoldingButtonLayoutListener;
import com.meizi.dummy.utils.Constants;
import com.meizi.dummy.utils.ToastUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSNSChangeInfo;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMMessageReceipt;
import com.tencent.imsdk.ext.message.TIMMessageReceiptListener;
import com.tencent.imsdk.friendship.TIMFriendPendencyInfo;
import com.tencent.imsdk.friendship.TIMFriendshipListener;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ChatActivity extends AppCompatActivity implements HoldingButtonLayoutListener, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.input_holder)
    HoldingButtonLayout holdingButtonLayout;

    private final String ACTIVITY_TAG = "ChatActivity:";
    // 录音计时
    private long recordStartTime;
    private long recordEndTime;

    // 录音
    private MediaRecorder mMediaRecorder = null;

    // 录音文件名
    private String recordName;
    // 录音文件路径
    private String recordFilePath;

    private static boolean record = false;// 是否录音

    // 权限申请，不知道啥用。
    private static final String TAG = "UseEasyPermission";
    private static final int RC_CAMERA_AND_RECORD_AUDIO = 10000;
    String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    /**
     * 去申请权限
     */
    private void requestPermissions() {


        //判断有没有权限
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 如果有权限了, 就做你该做的事情
//            openCamera();
            startRecord();
            recordStartTime = System.currentTimeMillis();
        } else {
            // 如果没有权限, 就去申请权限
            // this: 上下文
            // Dialog显示的正文
            // RC_CAMERA_AND_RECORD_AUDIO 请求码, 用于回调的时候判断是哪次申请
            // perms 就是你要申请的权限
            EasyPermissions.requestPermissions(this, "录音功能需要手机授予权限", RC_CAMERA_AND_RECORD_AUDIO, perms);
        }
    }

    /**
     * 权限申请成功的回调
     *
     * @param requestCode 申请权限时的请求码
     * @param perms       申请成功的权限集合
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsGranted: ");
//        openCamera();
    }

    /**
     * 权限申请拒绝的回调
     *
     * @param requestCode 申请权限时的请求码
     * @param perms       申请拒绝的权限集合
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsDenied: ");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActionBar背景颜色
        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(drawable);
        getSupportActionBar().setElevation(0);//去除标题栏阴影.
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);


        // HoldingButtonLayout 设置取消颜色绿色
        holdingButtonLayout.setCancelColor(Color.parseColor("#2cb044"));
        holdingButtonLayout.addListener(this);


    }


    private static void sendVoice(String recordPath, long duration) {
        //构造一条消息
        TIMMessage msg = new TIMMessage();
        //添加语音
        String filePath = recordPath;
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(filePath); //填写语音文件路径
        elem.setDuration(duration);  //填写语音时长
        //将 elem 添加到消息
        if (msg.addElement(elem) != 0) {
            Log.d("Chat", "addElement failed");
            return;
        }
        //发送消息
        String peer = "meizirain";  //获取与用户 "meizirain" 的会话
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C,    //会话类型：单聊
                peer);
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 含义请参见错误码表
                Log.d("Chat", "send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e("Chat", "SendMsg ok");
            }
        });
    }


    private static void sendText(String tag) {
        TIMMessage msg = new TIMMessage();
        //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText("a new msg");
        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Log.d(tag, "addElement failed");
            return;
        }
        //获取单聊会话
        String peer = "meizirain";  //获取与用户 "sample_user_1" 的会话
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C,    //会话类型：单聊
                peer);
        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 含义请参见错误码表
                Log.d(tag, "send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e(tag, "SendMsg ok");
            }
        });
    }


    public void startRecord() {
        //  https://www.jianshu.com/p/de779d509e6c
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        try {
            /* ②setAudioSource/setVedioSource */
            // Fixme 需要修改收集的权限许可，待申请
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            recordName = DateUtil.format(DateUtil.date(), "yyyy-MM-dd HH:mm:ss") + ".m4a";
            if (!FileUtil.exist(FileUtil.getName(Constants.RECORD_DIR))) {
                FileUtil.mkdir(Constants.RECORD_DIR);
            }
            recordFilePath = Constants.RECORD_DIR + recordName;
            /* ③准备 */
            mMediaRecorder.setOutputFile(recordFilePath);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            Log.e("ChatActivity", "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IOException e) {
            Log.e("ChatActivity", "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    public void stopRecord() {
        try {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            recordName = "";
        } catch (RuntimeException e) {
            Log.e("ChatActivity", e.toString());
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            File file = new File(recordFilePath);
            if (file.exists())
                file.delete();
            recordFilePath = "";
        }
    }

    @Override
    public void onBeforeExpand() {
        Log.d("Record", "onBeforeExpand: ");

    }

    @Override
    public void onExpand() {
        requestPermissions();
    }

    @Override
    public void onBeforeCollapse() {
//        Log.d("Record", "onBeforeCollapse: ");
    }

    @Override
    public void onCollapse(boolean b) {
//        Log.d("Record","onCollapse:"+ String.valueOf(holdingButtonLayout.getCancelColor()));
        if (EasyPermissions.hasPermissions(this, perms)) {
            if (record) {
                stopRecord();
                recordEndTime = System.currentTimeMillis();
                long duration = (long) Math.ceil((recordEndTime - recordStartTime) / 1000);
                sendVoice(recordFilePath, duration);

            } else {
                stopRecord();
            }
        }
    }

    @Override
    public void onOffsetChanged(float v, boolean b) {
        // 绿色为True
        record = b;
    }
}