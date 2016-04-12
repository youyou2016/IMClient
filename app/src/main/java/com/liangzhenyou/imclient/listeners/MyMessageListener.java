package com.liangzhenyou.imclient.listeners;

import com.liangzhenyou.imclient.dao.MyMessage;
import com.liangzhenyou.imclient.ui.activity.ChatContentActivity;
import com.liangzhenyou.imclient.utils.MessageUtils;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by youyou on 2016/4/10.
 */
public class MyMessageListener implements ChatMessageListener {

    @Override
    public void processMessage(Chat chat, Message message) {
        MyMessage myMessage = MessageUtils.getMyMessageByMessage(message);
        ChatContentActivity.arrayList.add(myMessage);
        ChatContentActivity.adapter.notifyDataSetChanged();
    }
}
