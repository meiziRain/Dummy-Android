package com.meizi.dummy.ui.conversation.base;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meizi.dummy.DummyKit;
import com.meizi.dummy.MainActivity;
import com.meizi.dummy.R;
import com.meizi.dummy.ui.conversation.ConversationFragment;
import com.meizi.dummy.ui.conversation.base.ConversationInfo;
import com.simple.bubbleviewlibrary.BubbleView;
import com.tencent.imsdk.conversation.Conversation;

import java.util.ArrayList;

import cn.hutool.core.date.DateUtil;

public class ConversationAdapter extends BaseAdapter {

    public ArrayList<ConversationInfo> conversationInfoList;

    public ConversationAdapter(ArrayList<ConversationInfo> list) {
        this.conversationInfoList = list;
    }

    @Override
    public int getCount() {
        return conversationInfoList.size();
    }

    @Override
    public ConversationInfo getItem(int position) {
        return conversationInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position % 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(DummyKit.getAppContext(),
                    R.layout.conversation_item, null);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        ConversationInfo item = getItem(position);
        // 不同的set处理图像
        holder.iv_icon.setImageResource(R.drawable.default_head);
        holder.tv_name.setText(item.getTitle());
        String lastMsgTime = DateUtil.formatTime(DateUtil.date(item.getLastMessageTime() * 1000)).substring(0, 5);
        holder.msg_time.setText(lastMsgTime);
        if (item.getUnRead() > 0) {
            holder.bubbleView.setTextColor(Color.WHITE);
            holder.bubbleView.setText(String.valueOf(item.getUnRead()));
            holder.bubbleView.setCircleColor(Color.RED);
            holder.bubbleView.bringToFront();
        }
        return convertView;
    }


    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView msg_time;
        BubbleView bubbleView;

        public ViewHolder(View view) {
            iv_icon = view.findViewById(R.id.iv_icon);
            tv_name = view.findViewById(R.id.tv_name);
            msg_time = view.findViewById(R.id.msg_time);
            bubbleView = view.findViewById(R.id.bubbleView);
            view.setTag(this);
        }
    }
}