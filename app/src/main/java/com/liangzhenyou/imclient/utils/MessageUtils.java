package com.liangzhenyou.imclient.utils;

import com.liangzhenyou.imclient.dao.MyMessage;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by youyou on 2016/4/11.
 */
public class MessageUtils {

    public static MyMessage getMyMessageByMessage(Message message) {
        MyMessage myMessage = new MyMessage();
        String data = message.getBody();
        int type = data.charAt(0) - '0';
        String newBody = data.substring(1, data.length());
        myMessage.setTYPE(type);
        String[] str = message.getFrom().split("/");
        myMessage.setUserJid(str[0]);
        myMessage.setBody(newBody);
        myMessage.setORIGIN(MyMessage.ORIGIN_REMOTE);
        myMessage.setDateFormat(String.valueOf(System.currentTimeMillis()));

        return myMessage;
    }

    public static Message getMessageByMyMessage(MyMessage myMessage) {
        Message message = new Message();
        message.setTo(myMessage.getUserJid());
        message.setBody(myMessage.getTYPE() + myMessage.getBody());

        return message;
    }


}
