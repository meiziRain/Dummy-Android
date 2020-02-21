package com.meizi.dummy.ui.conversation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;

import com.alibaba.fastjson.JSON;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.meizi.dummy.ChatActivity;
import com.meizi.dummy.LoginActivity;
import com.meizi.dummy.R;
import com.meizi.dummy.ui.chat.Msg;
import com.meizi.dummy.ui.conversation.base.ConversationAdapter;
import com.meizi.dummy.ui.conversation.base.ConversationInfo;
import com.meizi.dummy.ui.conversation.base.ConversationRefreshListener;
import com.meizi.dummy.ui.conversation.base.GetFragmentListener;
import com.simple.bubbleviewlibrary.BubbleView;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMGroupSystemElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.friendship.TIMFriend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConversationFragment extends Fragment {
    public ConversationAdapter mAdapter;
    public int mUnreadTotal;
    public ArrayList<ConversationInfo> conversationInfoList;
    private GetFragmentListener getFragmentListener;
    SwipeMenuListView swipeMenuListView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conversation, container, false);

        // 向MainActivity 传递 Fragment
        getFragmentListener.sendValue(ConversationFragment.this);
        swipeMenuListView = root.findViewById(R.id.listView);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("conversation", Context.MODE_PRIVATE);
        String conversationJSONStr = sharedPreferences.getString("conversationJSONStr", "");
        if (!conversationJSONStr.equals("")) {
            conversationInfoList = (ArrayList<ConversationInfo>) JSON.parseArray(conversationJSONStr, ConversationInfo.class);
        }
        initSwipeMenuListView(swipeMenuListView);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getFragmentListener = (GetFragmentListener) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void refreshConversationList() {
        List<TIMConversation> TIMConversations = TIMManager.getInstance().getConversationList();
        System.out.println("Sign:" + TIMConversations.size());
        conversationInfoList = new ArrayList<>();
        for (int i = 0; i < TIMConversations.size(); i++) {
            TIMConversation conversation = TIMConversations.get(i);
            //将imsdk TIMConversation转换为UIKit ConversationInfo
            ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
            if (conversationInfo != null) {
                mUnreadTotal = mUnreadTotal + conversationInfo.getUnRead();
                conversationInfo.setType(ConversationInfo.TYPE_COMMON); //
                conversationInfoList.add(conversationInfo);
            }
        }
        initSwipeMenuListView(getActivity().findViewById(R.id.listView));
        // Put in local
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("conversation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("conversationJSONStr", JSON.toJSONString(conversationInfoList));
        editor.commit();
    }

    public void initSwipeMenuListView(SwipeMenuListView listView) {
        System.out.println("Sign:" + "initSwipeMenuListView:");
        if (null != conversationInfoList) {
            mAdapter = new ConversationAdapter(conversationInfoList);
            listView.setAdapter(mAdapter);
            SwipeMenuCreator creator = menu -> {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("置顶");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            };

            // set creator
            listView.setMenuCreator(creator);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(ConversationFragment.this.getContext(), ChatActivity.class);
                    // 传入用户昵称，
                    intent.putExtra("target", mAdapter.getItem(i).getId());
                    intent.putExtra("title", mAdapter.getItem(i).getTitle());
                    ConversationFragment.this.startActivity(intent);
                }
            });

            listView.setOnMenuItemClickListener((position, menu, index) -> {
                ConversationInfo item = conversationInfoList.get(position);
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        // delete
//					delete(item);
                        conversationInfoList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            });

            listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

                @Override
                public void onSwipeStart(int position) {
                    // swipe start
                    Toast.makeText(getContext(), "swipe start!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSwipeEnd(int position) {
                    // swipe end
                    Toast.makeText(getContext(), "swipe end!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    /**
     * TIMConversation转换为ConversationInfo
     *
     * @param conversation
     * @return
     */
    public ConversationInfo TIMConversation2ConversationInfo(final TIMConversation conversation) {
        if (conversation == null) {
            return null;
        }
        TIMMessage message = conversation.getLastMsg();
        if (message == null) {
            return null;
        }
        final ConversationInfo info = new ConversationInfo();
        TIMConversationType type = conversation.getType();
        if (type == TIMConversationType.System) { // 如果是系统消息
            if (message.getElementCount() > 0) {
                TIMElem ele = message.getElement(0);
                TIMElemType eleType = ele.getType();
                if (eleType == TIMElemType.GroupSystem) {
                    TIMGroupSystemElem groupSysEle = (TIMGroupSystemElem) ele;
//                    groupSystMsgHandle(groupSysEle);
                }
            }
            return null;
        }

        boolean isGroup = type == TIMConversationType.Group;
        info.setLastMessageTime(message.timestamp());
//        List<MessageInfo> list = MessageInfoUtil.TIMMessage2MessageInfo(message, isGroup);
//        if (list != null && list.size() > 0) {
//            info.setLastMessage(list.get(list.size() - 1));
//        }

        if (isGroup) { //
//            fillConversationWithGroupInfo(conversation, info);
        } else {
            // FIXME: 这两个方法的回调方法中会执行updateAdapter()重新构造listview的数据，但是总感觉有哪里怪怪的。
            fillConversationWithUserProfile(conversation, info);
            System.out.println("Sign:8" + info.toString());
        }
        info.setId(conversation.getPeer());
        info.setGroup(conversation.getType() == TIMConversationType.Group);
        if (conversation.getUnreadMessageNum() > 0) {
            info.setUnRead((int) conversation.getUnreadMessageNum());
        }
        return info;
    }


    public void fillConversationWithUserProfile(final TIMConversation conversation, final ConversationInfo info) {
        String title = conversation.getPeer();
        final ArrayList<String> ids = new ArrayList<>();
        ids.add(conversation.getPeer());
        TIMUserProfile profile = TIMFriendshipManager.getInstance().queryUserProfile(conversation.getPeer());
        if (profile == null) {
            TIMFriendshipManager.getInstance().getUsersProfile(ids, false, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int code, String desc) {
                    Log.e("fillUser", "getUsersProfile failed! code: " + code + " desc: " + desc);
                }

                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    System.out.println("Sign:TF:" + Thread.currentThread().getName());
                    if (timUserProfiles == null || timUserProfiles.size() != 1) {
                        Log.i("fillUser", "No TIMUserProfile");
                        return;
                    }

                    TIMUserProfile profile = timUserProfiles.get(0);
                    List<Object> face = new ArrayList<>();
                    if (profile != null && !TextUtils.isEmpty(profile.getFaceUrl())) {
                        face.add(profile.getFaceUrl());
                    } else {
                        face.add(R.drawable.default_head);
                    }
                    String title = conversation.getPeer();
                    if (profile != null && !TextUtils.isEmpty(profile.getNickName())) {
                        title = profile.getNickName();
                    }
                    info.setTitle(title);
                    info.setIconUrlList(face);
                    // 异步数据返回后刷新list
                    initSwipeMenuListView(getView().findViewById(R.id.listView));
                    System.out.println("Sign:1" + info.toString());
                    System.out.println("Sign:1");
                }
            });
        } else {
            List<Object> face = new ArrayList<>();
            if (!TextUtils.isEmpty(profile.getNickName())) {
                title = profile.getNickName();
            }
            if (TextUtils.isEmpty(profile.getFaceUrl())) {
                face.add(R.drawable.default_head);
            } else {
                face.add(profile.getFaceUrl());
            }
            info.setTitle(title);
            info.setIconUrlList(face);
            System.out.println("Sign:2");
        }
        System.out.println("Sign:TO:" + Thread.currentThread().getName());
        TIMFriend friend = TIMFriendshipManager.getInstance().queryFriend(conversation.getPeer());
        if (friend == null) {
            System.out.println("Sign:7" + info.toString());
            System.out.println("Sign:7");
            TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMFriend>>() {
                @Override
                public void onError(int code, String desc) {
                    Log.e("fillUser", "getFriendList failed! code: " + code + " desc: " + desc);
                }

                @Override
                public void onSuccess(List<TIMFriend> timFriends) {
                    if (timFriends == null || timFriends.size() == 0) {
                        Log.i("fillUser", "No Friends");
                        return;
                    }
                    for (TIMFriend friend : timFriends) {
                        if (!TextUtils.equals(conversation.getPeer(), friend.getIdentifier())) {
                            continue;
                        }
                        if (TextUtils.isEmpty(friend.getRemark())) {
                            continue;
                        }
                        info.setTitle(friend.getRemark());
                        System.out.println("Sign:3" + info.getTitle());
                        initSwipeMenuListView(getView().findViewById(R.id.listView));
                        return;
                    }
                    Log.i("fillUser", conversation.getPeer() + " is not my friend");
                }
            });
        } else {
            if (!TextUtils.isEmpty(friend.getRemark())) {
                title = friend.getRemark();
                info.setTitle(title);
                System.out.println("Sign:4" + info.getTitle());
            }
            System.out.println("Sign:5" + info.getTitle());
        }
    }
}