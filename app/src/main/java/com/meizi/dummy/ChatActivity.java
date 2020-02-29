package com.meizi.dummy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.core.io.FileUtil;
import pub.devrel.easypermissions.EasyPermissions;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.dewarder.holdinglibrary.HoldingButtonLayout;
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener;
import com.meizi.dummy.component.AudioPlayer;
import com.meizi.dummy.ui.chat.MessageInfoUtil;
import com.meizi.dummy.ui.chat.MsgAdapter;
import com.meizi.dummy.ui.base.MessageInfo;
import com.meizi.dummy.utils.Constants;
import com.meizi.dummy.utils.NetWorkUtils;
import com.meizi.dummy.utils.ToastUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.ufreedom.floatingview.FloatingElement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity implements HoldingButtonLayoutListener, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.input_holder)
    HoldingButtonLayout holdingButtonLayout;
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
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
    //    String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    String[] perms = {Manifest.permission.RECORD_AUDIO};

    private FloatingElement builder;

    // MessageList
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    // 消息数据
    private List<MessageInfo> mMsgList;

    // list顶部的TIMMessage
    TIMMessage topTimMessage = null;

    // 消息返送目标id
    String target;
    // 标题栏文字: C2C昵称或者群聊昵称
    String title;


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


    public void loadLocalChatMessages(TIMMessage top) {
        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, target);
        //获取此会话的消息
        con.getLocalMessage(10, //获取此会话最近的 10 条消息
                top, //不指定从哪条消息开始获取 - 等同于从最新的消息开始往前
                new TIMValueCallBack<List<TIMMessage>>() {//回调接口
                    @Override
                    public void onError(int code, String desc) {//获取消息失败
                        //接口返回了错误码 code 和错误描述 desc，可用于定位请求失败原因
                        //错误码 code 含义请参见错误码表
                        Log.d("initMsgs:", "get message failed. code: " + code + " errmsg: " + desc);
                    }

                    @Override
                    public void onSuccess(List<TIMMessage> msgs) {//获取消息成功
                        //遍历取得的消息
                        List<MessageInfo> list = MessageInfoUtil.TIMMessages2MessageInfos(msgs, false);
                        for (MessageInfo messageInfo : list) {
                            System.out.println("Chat:" + messageInfo.getMsgTime());
                        }
                        Collections.reverse(list);
                        mMsgList.addAll(list);
                        for (MessageInfo messageInfo : list) {
                            System.out.println("Chat add:" + messageInfo.getMsgTime());
                        }
                        topTimMessage = msgs.get(0);
                        swipeRefreshLayout.setRefreshing(false);
                        adapter = new MsgAdapter(mMsgList);
                        msgRecyclerView.setAdapter(adapter);

//                        for(TIMMessage msg : msgs) {
//                            lastMsg = msg;
//                            //可以通过 timestamp()获得消息的时间戳, isSelf()是否为自己发送的消息
//                            Log.e("initMsgs:", "get msg: " + msg.timestamp() + " self: " + msg.isSelf() + " seq: " + msg.getSeq());
//                        }
                    }
                });
    }

    private void initMsgs() {
        mMsgList = new ArrayList<>();
        adapter = new MsgAdapter(mMsgList);
        //获取会话扩展实例
        // Todo
        loadLocalChatMessages(topTimMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化聊天对象
        Intent intent = getIntent();
        target = intent.getStringExtra("target");
        title = intent.getStringExtra("title");

        // ActionBar背景颜色
        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(drawable);
        getSupportActionBar().setElevation(0);//去除标题栏阴影.
        getSupportActionBar().setTitle(title);

        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);


        // HoldingButtonLayout 设置取消颜色绿色
        holdingButtonLayout.setCancelColor(Color.parseColor("#2cb044"));
        holdingButtonLayout.addListener(this);

        // messageList
        msgRecyclerView = (RecyclerView) findViewById(R.id.chat_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);


        initMsgs();


        // TIM 开始收听消息
        initTIMListener();
        setIsRead();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initSwipeRefreshLayout();
    }

    void initSwipeRefreshLayout() {
        swipeRefreshLayout.setRefreshing(true);
        //设置下拉时圆圈的颜色（可以由多种颜色拼成）
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);

        //设置下拉时圆圈的背景颜色（这里设置成白色）
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setProgressViewOffset(true, 100, 200);
        swipeRefreshLayout.setProgressViewEndTarget(true, 380);
        //设置触发刷新的距离
        swipeRefreshLayout.setDistanceToTriggerSync(200);
        //设置下拉刷新时的操作
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //具体操作
            loadLocalChatMessages(topTimMessage);
        });
    }

    void setIsRead() {
        //对单聊会话已读上报
        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                target);                      //会话对方用户帐号
        //将此会话的所有消息标记为已读
        conversation.setReadMessage(null, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                Log.e("isRead", "setReadMessage failed, code: " + code + "|desc: " + desc);
            }

            @Override
            public void onSuccess() {
                Log.d("isRead", "setReadMessage succ");
            }
        });

        //对群聊会话已读上报
//        String groupId = "TGID1EDABEAEO";  //获取与群组 "TGID1LTTZEAEO" 的会话
//        conversation = TIMManager.getInstance().getConversation(
//                TIMConversationType.Group,      //会话类型：群组
//                groupId);                       //群组 Id
//        //将此会话的 lastMsg 代表的消息及这个消息之前的所有消息标记为已读
//        conversation.setReadMessage(lastMsg, new TIMCallBack() {
//            @Override
//            public void onError(int code, String desc) {
//                Log.e(tag, "setReadMessage failed, code: " + code + "|desc: " + desc);
//            }
//
//            @Override
//            public void onSuccess() {
//                Log.d(tag, "setReadMessage succ");
//            }
//        });
    }

    void updateMessageListView() {
        adapter = new MsgAdapter(mMsgList);
        msgRecyclerView.setAdapter(adapter);
        adapter.notifyItemInserted(mMsgList.size() - 1);
        msgRecyclerView.scrollToPosition(mMsgList.size() - 1);
    }


    public void initTIMListener() {
        TIMManager.getInstance().addMessageListener(msgs -> {//收到新消息

            //消息的内容解析请参考消息收发文档中的消息解析说明
            /* 消息 */
            for (int j = 0; j < msgs.size(); j++) {
                TIMMessage msg = msgs.get(j);
                for (int i = 0; i < msg.getElementCount(); ++i) {
                    TIMElem elem = msg.getElement(i);
                    //获取当前元素的类型
                    TIMElemType elemType = elem.getType();
                    Log.d("Chat", "elem type: " + elemType.name());
                    if (elemType == TIMElemType.Text) {
                        //处理文本消息
                        runOnUiThread(() -> {
                            TIMTextElem timTextElem = (TIMTextElem) elem;
                        });
                    } else if (elemType == TIMElemType.Sound) {
                        //处理语音消息
                        TIMSoundElem timSoundElem = (TIMSoundElem) elem;
                        final String filePath = Constants.RECORD_DIR + ((TIMSoundElem) elem).getUuid();
                        ((TIMSoundElem) elem).getSoundToFile(filePath, new TIMCallBack() {
                            @Override
                            public void onError(int code, String desc) {
                                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                                //错误码 code 含义请参见错误码表
                                Log.d("Chat", "setSound failed. code: " + code + " errmsg: " + desc);
                            }

                            @Override
                            public void onSuccess() {
                                //doSomething
                                Log.d("Chat", "getSound success.");
                                MessageInfo messageInfo = new MessageInfo();
                                messageInfo.setSelf(false);
                                messageInfo.setDataPath(filePath);
                                mMsgList.add(messageInfo);
                                // you need update msg list
                                updateMessageListView();
                            }
                        });

                    }//...处理更多消息
                }
            }
            return false; //返回true将终止回调链，不再调用下一个新消息监听器
        });
    }

    private void sendVoice(String recordPath, long duration) {
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
        TIMConversation conversation = TIMManager.getInstance()
                .getConversation(TIMConversationType.C2C,    //会话类型：单聊
                        target);
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

//            此处使用时间命名，  AudioPlayer会报错
//            recordName = DateUtil.format(DateUtil.date(), "yyyy-MM-dd HH:mm:ss") + ".m4a";
            recordName = UUID.randomUUID() + ".m4a";
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
                MessageInfo messageInfo = MessageInfoUtil.buildAudioMessage(recordFilePath, (int) (duration) * 1000);
                mMsgList.add(messageInfo);
                // update msg list
                updateMessageListView();
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