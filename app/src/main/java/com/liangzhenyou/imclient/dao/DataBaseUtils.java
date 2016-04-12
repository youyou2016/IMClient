package com.liangzhenyou.imclient.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;

import com.liangzhenyou.imclient.db.SQLiteDatabaseHelper;

import java.util.ArrayList;


/**
 * Created by youyou on 2016/4/11.
 */
public class DataBaseUtils {

    SQLiteDatabase sqLiteDatabase;

    public DataBaseUtils(SQLiteDatabase db) {
        sqLiteDatabase = db;
    }

    public boolean setMyMessageToDatabase(MyMessage myMessage) {
        sqLiteDatabase.execSQL("insert into message values(null,?,?,?,?,?)",
                new Object[]{myMessage.getORIGIN(),
                        myMessage.getUserJid(),
                        myMessage.getTYPE(),
                        myMessage.getBody(),
                        myMessage.getDateFormat()});
        return true;
    }

    public ArrayList<MyMessage> getMyMessageFromDatabase(String userJid) {
        MyMessage myMessage;
        ArrayList<MyMessage> arrayList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from message where roster_jid=?", new String[]{userJid});
        //Cursor cursor = sqLiteDatabase.query("message", null, null, null, null, null, null,null);
        while (cursor.moveToNext()) {
            myMessage = new MyMessage();
            myMessage.setORIGIN(cursor.getInt(1));
            myMessage.setUserJid(cursor.getString(2));
            myMessage.setTYPE(cursor.getInt(3));
            myMessage.setBody(cursor.getString(4));
            myMessage.setDateFormat(DateFormat.format("MM/dd/yy h:mmaa", cursor.getLong(5)).toString());
            arrayList.add(myMessage);
        }
        return arrayList;
    }

}
