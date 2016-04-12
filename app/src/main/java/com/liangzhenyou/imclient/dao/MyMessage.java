package com.liangzhenyou.imclient.dao;

import android.text.format.DateFormat;

/**
 * Created by youyou on 2016/4/10.
 * 消息的封装类
 */
public class MyMessage {

    public static final int TYPE_TEXT = 0;  //文本
    public static final int TYPE_IMAGE = 1;  //图片
    public static final int TYPE_AUDIO = 2;  //语音

    public static final int ORIGIN_LOCAL = 0;  //本地
    public static final int ORIGIN_REMOTE = 1; //远程


    public int getTYPE() {
        return TYPE;
    }

    public void setTYPE(int TYPE) {
        this.TYPE = TYPE;
    }

    //消息类型，包括TYPE_TEXT, TYPE_IMAGE, TYPE_AUDIO
    private int TYPE;

    public int getORIGIN() {
        return ORIGIN;
    }

    public void setORIGIN(int ORIGIN) {
        this.ORIGIN = ORIGIN;
    }

    //消息的来源，来自本地（发送消息给别人）ORIGIN_LOCAL, 来自远程（接收别人发来的消息）ORIGIN_ROMOTE
    private int ORIGIN;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    //消息的主体
    private String body;

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    //消息的时间
    private String dateFormat;


    public String getUserJid() {
        return userJid;
    }

    public void setUserJid(String userJid) {
        this.userJid = userJid;
    }

    //消息的目标用户jid
    private String userJid;


}
