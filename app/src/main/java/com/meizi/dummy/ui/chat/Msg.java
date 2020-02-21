package com.meizi.dummy.ui.chat;

/**
 * @Classname Msg
 * @Description TODO
 * @Date 2020/2/20 17:21
 * @Created by jion
 */
public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;

    public Msg(String content,int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }
    public int getType() {
        return type;
    }
}