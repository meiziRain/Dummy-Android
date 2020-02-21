package com.meizi.dummy.ui.chat;

/**
 * @Classname MsgAdapter
 * @Description TODO
 * @Date 2020/2/20 17:23
 * @Created by jion
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meizi.dummy.R;
import com.meizi.dummy.component.AudioPlayer;
import com.meizi.dummy.ui.conversation.base.ConversationInfo;
import com.meizi.dummy.ui.conversation.base.MessageInfo;
import com.meizi.dummy.utils.ToastUtil;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<MessageInfo> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        Button leftMsg;
        Button rihgtMsg;

        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            leftMsg = (Button) view.findViewById(R.id.left_msg);

            rihgtMsg = (Button) view.findViewById(R.id.right_msg);

        }
    }

    public MsgAdapter(List<MessageInfo> msgList) {
        mMsgList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageInfo messageInfo = mMsgList.get(position);
        AudioPlayer audioPlayer=AudioPlayer.getInstance();
        holder.leftMsg.setOnClickListener(view -> {
            System.out.println("Sign:click left Msg");
            audioPlayer.startPlay(messageInfo.getDataPath(), new AudioPlayer.Callback() {
                @Override
                public void onCompletion(Boolean success) {
                    System.out.println("Sign:audio play onCompletion");
                }
            });
        });
        holder.rihgtMsg.setOnClickListener(view -> {
            System.out.println("Sign:click right Msg");
            audioPlayer.startPlay(messageInfo.getDataPath(), new AudioPlayer.Callback() {
                @Override
                public void onCompletion(Boolean success) {
                    System.out.println("Sign:audio play onCompletion");
                }
            });
        });
        if (!messageInfo.isSelf()) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText("left Btn");

        } else {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rihgtMsg.setText("Right Btn");
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}