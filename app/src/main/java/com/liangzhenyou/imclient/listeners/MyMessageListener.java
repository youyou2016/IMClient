package com.liangzhenyou.imclient.listeners;

import com.liangzhenyou.imclient.dao.CustomMessage;
import com.liangzhenyou.imclient.ui.activity.ChatContentActivity;
import com.liangzhenyou.imclient.utils.MessageUtils;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by youyou on 2016/4/10.
 */
public class MyMessageListener implements ChatMessageListener {

    @Override
    public void processMessage(Chat chat, Message message) {
        CustomMessage customMessage = MessageUtils.getCustomMessageByMessage(message);
        ChatContentActivity.arrayList.add(customMessage);
        ChatContentActivity.adapter.notifyDataSetChanged();
    }
}
