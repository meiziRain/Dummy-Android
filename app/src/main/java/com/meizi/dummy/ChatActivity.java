package com.meizi.dummy;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class ChatActivity extends AppCompatActivity {


//    @BindView(R.id.input)
//    MessageInput message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActionBar背景颜色
        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(drawable);
        getSupportActionBar().setElevation(0);//去除标题栏阴影.
        setContentView(R.layout.activity_chat);


//
//        MessagesListAdapter<Message> adapter = new MessagesListAdapter<>(senderId, imageLoader);
//        messagesList.setAdapter(adapter);
//
//        inputView.setInputListener(new MessageInput.InputListener() {
//            @Override
//            public boolean onSubmit(CharSequence input) {
//                //validate and send message
//                adapter.addToStart(message, true);
//                return true;
//            }
//        });
    }
}
