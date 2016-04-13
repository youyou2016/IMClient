package com.liangzhenyou.imclient.utils;

import android.text.format.DateFormat;
import android.util.Log;

import com.liangzhenyou.imclient.dao.MyMessage;

import org.jivesoftware.smack.packet.Message;

import java.io.File;
import java.util.UUID;

/**
 * Created by youyou on 2016/4/11.
 */
public class MessageUtils {

    private static final String TAG = "MessageUtils";

    public static MyMessage getMyMessageByMessage(Message message) {
        if (message == null) {
            return null;
        }
        MyMessage myMessage = new MyMessage();
        String data = message.getBody();
        int type = data.charAt(0) - '0';
        String newdata = data.substring(1, data.length());
        myMessage.setTYPE(type);
        String[] str = message.getFrom().split("/");
        myMessage.setUserJid(str[0]);
        myMessage.setORIGIN(MyMessage.ORIGIN_REMOTE);
        myMessage.setDateFormat((DateFormat.format("MM/dd/yy h:mmaa", System.currentTimeMillis()).toString()));

        if (type == MyMessage.TYPE_TEXT) {
            myMessage.setBody(newdata);
        } else if (type == MyMessage.TYPE_AUDIO) {
            File dir = new File(FileUtils.getExternalFilesDir() + "/audio");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File fileName = new File(dir + "/" + UUID.randomUUID()+".amr");
            FileUtils.base64ToFile(newdata, fileName.getPath());
            myMessage.setBody(fileName.toString());
        } else if (type == MyMessage.TYPE_IMAGE) {

        }
        return myMessage;

    }

    public static Message getMessageByMyMessage(MyMessage myMessage) {
        if (myMessage == null) {
            return null;
        }
        Message message = new Message();
        message.setTo(myMessage.getUserJid());
        if (myMessage.getTYPE() == MyMessage.TYPE_TEXT) {
            message.setBody(MyMessage.TYPE_TEXT + myMessage.getTYPE() + myMessage.getBody());
        } else if (myMessage.getTYPE() == MyMessage.TYPE_AUDIO) {
            Log.d(TAG, myMessage.getBody());
            message.setBody(MyMessage.TYPE_AUDIO + FileUtils.encodeBase64FromFile(myMessage.getBody()));
        } else if (myMessage.getTYPE() == MyMessage.TYPE_IMAGE) {

        }

        return message;
    }

    public static MyMessage createMyMessage(int type, String userJid, String data) {
        MyMessage myMessage = new MyMessage();
        switch (type) {
            case MyMessage.TYPE_TEXT:
                myMessage.setTYPE(MyMessage.TYPE_TEXT);
                break;
            case MyMessage.TYPE_AUDIO:
                myMessage.setTYPE(MyMessage.TYPE_AUDIO);
                break;
            case MyMessage.TYPE_IMAGE:
                myMessage.setTYPE(MyMessage.TYPE_IMAGE);
                break;
            default:

        }
        myMessage.setBody(data);
        myMessage.setORIGIN(MyMessage.ORIGIN_LOCAL);
        myMessage.setUserJid(userJid);
        myMessage.setDateFormat((DateFormat.format("MM/dd/yy h:mmaa", System.currentTimeMillis()).toString()));
        return myMessage;
    }


}
