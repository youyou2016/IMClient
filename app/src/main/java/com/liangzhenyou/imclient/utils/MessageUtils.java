package com.liangzhenyou.imclient.utils;

import android.text.format.DateFormat;
import android.util.Log;

import com.liangzhenyou.imclient.dao.CustomMessage;

import org.jivesoftware.smack.packet.Message;

import java.io.File;
import java.util.UUID;

/**
 * Created by youyou on 2016/4/11.
 */
public class MessageUtils {

    private static final String TAG = "MessageUtils";

    public static CustomMessage getCustomMessageByMessage(Message message) {
        if (message == null) {
            return null;
        }
        CustomMessage customMessage = new CustomMessage();
        String data = message.getBody();
        int type = data.charAt(0) - '0';
        customMessage.setTYPE(type);
        String newdata = data.substring(1, data.length());
        if (type == CustomMessage.TYPE_TEXT) {
            customMessage.setBody(newdata);
        } else if (type == CustomMessage.TYPE_AUDIO) {
            File dir = new File(FileUtils.getExternalFilesDir() + "/audio");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File fileName = new File(dir + "/" + UUID.randomUUID() + ".amr");
            FileUtils.base64ToFile(newdata, fileName.getPath());
            customMessage.setBody(fileName.toString());
        } else if (type == CustomMessage.TYPE_IMAGE) {
            File dir = new File(FileUtils.getExternalFilesDir() + "/image");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File fileName = new File(dir + "/" + UUID.randomUUID() + ".jpg");
            FileUtils.base64ToFile(newdata, fileName.getPath());
            customMessage.setBody(fileName.toString());
        }



        String[] str = message.getFrom().split("/");
        customMessage.setRosterJid(str[0]);
        customMessage.setORIGIN(CustomMessage.ORIGIN_REMOTE);
        customMessage.setDateFormat((DateFormat.format("MM/dd/yy h:mmaa", System.currentTimeMillis()).toString()));
        customMessage.setUserJid(message.getTo());




        return customMessage;

    }

    public static Message getMessageByCustomMessage(CustomMessage customMessage) {
        if (customMessage == null) {
            return null;
        }
        Message message = new Message();
        message.setTo(customMessage.getRosterJid());
        if (customMessage.getTYPE() == CustomMessage.TYPE_TEXT) {
            message.setBody(CustomMessage.TYPE_TEXT + customMessage.getBody());
        } else if (customMessage.getTYPE() == CustomMessage.TYPE_AUDIO) {
            Log.d(TAG, customMessage.getBody());
            message.setBody(CustomMessage.TYPE_AUDIO + FileUtils.encodeBase64FromFile(customMessage.getBody()));
        } else if (customMessage.getTYPE() == CustomMessage.TYPE_IMAGE) {
            message.setBody(CustomMessage.TYPE_IMAGE + FileUtils.getImageBase64(customMessage.getBody()));
        }

        return message;
    }

    public static CustomMessage createCustomMessage(int type, String userjid, String rosterJid, String data) {
        CustomMessage customMessage = new CustomMessage();
        switch (type) {
            case CustomMessage.TYPE_TEXT:
                customMessage.setTYPE(CustomMessage.TYPE_TEXT);
                break;
            case CustomMessage.TYPE_AUDIO:
                customMessage.setTYPE(CustomMessage.TYPE_AUDIO);
                break;
            case CustomMessage.TYPE_IMAGE:
                customMessage.setTYPE(CustomMessage.TYPE_IMAGE);
                break;
            default:

        }
        customMessage.setUserJid(userjid);
        customMessage.setBody(data);
        customMessage.setORIGIN(CustomMessage.ORIGIN_LOCAL);
        customMessage.setRosterJid(rosterJid);
        customMessage.setDateFormat((DateFormat.format("MM/dd/yy h:mmaa", System.currentTimeMillis()).toString()));
        return customMessage;
    }


}
