//package com.meizi.dummy.ui.chat;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.meizi.dummy.DummyKit;
//import com.meizi.dummy.R;
//import com.meizi.dummy.ui.conversation.base.ConversationAdapter;
//import com.simple.bubbleviewlibrary.BubbleView;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * @Classname MessageListAdapter
// * @Description TODO
// * @Date 2020/2/20 13:32
// * @Created by jion
// */
//public class MessageListAdapter extends ArrayAdapter {
//
//    private ArrayList<HashMap<Integer, Object>> items = null;
//    private LayoutInflater layoutInflater;
//    private final int VIEW_TYPE = 0xb01;
//    private final int VIEW_TYPE_LEFT = -10;
//    private final int VIEW_TYPE_RIGHT = -11;
//    private final int MESSAGE = 0xb02;
//
//    public MessageListAdapter(Context context, int resource, ArrayList<HashMap<Integer, Object>> i) {
//        super(context, resource);
//        layoutInflater = LayoutInflater.from(context);
//        items = i;
//    }
//
//    @Override
//    public View getView(int pos, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = View.inflate(DummyKit.getAppContext(),
//                    R.layout.chat_right, null);
//            new ViewHolder(convertView);
//        }
//
//        int type = getItemViewType(pos);
//        String msg = getItem(pos);
//
//        switch (type) {
//            case VIEW_TYPE_LEFT:
//                convertView = layoutInflater.inflate(R.layout.chat_left, null);
//                TextView textLeft = (TextView) convertView.findViewById(R.id.textView);
//                textLeft.setText(msg);
//                break;
//
//            case VIEW_TYPE_RIGHT:
//                convertView = layoutInflater.inflate(R.layout.chat_right, null);
//                TextView textRight = (TextView) convertView.findViewById(R.id.textView);
//                textRight.setText(msg);
//                break;
//        }
//
//        return convertView;
//    }
//
//    @Override
//    public String getItem(int pos) {
//        String s = items.get(pos).get(MESSAGE) + "";
//        return s;
//    }
//
//    @Override
//    public int getCount() {
//        return items.size();
//    }
//
//    @Override
//    public int getItemViewType(int pos) {
//        int type = (Integer) items.get(pos).get(VIEW_TYPE);
//        return type;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    class ViewHolder {
//        ImageView iv_icon;
//        Button audioBtn;
//
//        public ViewHolder(View view) {
//            iv_icon = view.findViewById(R.id.iv_icon);
//            tv_name = view.findViewById(R.id.tv_name);
//            msg_time = view.findViewById(R.id.msg_time);
//            bubbleView = view.findViewById(R.id.bubbleView);
//            view.setTag(this);
//        }
//    }
//}
