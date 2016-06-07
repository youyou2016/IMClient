package com.liangzhenyou.imclient.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liangzhenyou.imclient.dao.CustomMessage;

import java.util.ArrayList;


/**
 * Created by youyou on 2016/4/11.
 */
public class DataBaseUtils {

    SQLiteDatabase sqLiteDatabase;

    public DataBaseUtils(SQLiteDatabase db) {
        sqLiteDatabase = db;
    }

    public boolean setMyMessageToDatabase(CustomMessage customMessage) {
        sqLiteDatabase.execSQL("insert into message values(null,?,?,?,?,?,?)",
                new Object[]{
                        customMessage.getUserJid(),
                        customMessage.getRosterJid(),
                        customMessage.getORIGIN(),
                        customMessage.getTYPE(),
                        customMessage.getBody(),
                        customMessage.getDateFormat()});
        return true;
    }

    public ArrayList<CustomMessage> getMyMessageFromDatabase(String userJid, String rosterJid) {
        CustomMessage customMessage;
        ArrayList<CustomMessage> arrayList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from message where user_jid=? and roster_jid=?", new String[]{userJid, rosterJid});
        //Cursor cursor = sqLiteDatabase.query("message", null, null, null, null, null, null,null);
        while (cursor.moveToNext()) {
            customMessage = new CustomMessage();
            customMessage.setUserJid(cursor.getString(1));
            customMessage.setRosterJid(cursor.getString(2));
            customMessage.setORIGIN(cursor.getInt(3));
            customMessage.setTYPE(cursor.getInt(4));
            customMessage.setBody(cursor.getString(5));
            customMessage.setDateFormat(cursor.getString(6));
            arrayList.add(customMessage);
        }
        return arrayList;
    }

}
