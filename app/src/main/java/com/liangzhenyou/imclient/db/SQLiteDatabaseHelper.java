package com.liangzhenyou.imclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by youyou on 2016/4/7.
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "imclient.db";

    private static final String ROSTER_TABLE = "roster";

    private static final String MESSAGE_TABLE = "message";

    //对话列表的缓存
    private static final String CHAT_TABLE = "chat";


    private static final int DATABASE_VERSION = 1;

    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + ROSTER_TABLE + "("
                        + "id integer primary key,"
                        + "user_jid varchar,"
                        + "roster_jid varchar,"
                        + "user_name varchar,"
                        + "user_icon image)"
        );
        db.execSQL("create table if not exists " + MESSAGE_TABLE + "("
                        + "id integer primary key,"
                        + "user_jid varchar,"
                        + "roster_jid varchar,"
                        + "origin integer,"
                        + "message_type integer,"
                        + "message_body text,"
                        + "time integer)"
        );

        db.execSQL("create table if not exists " + CHAT_TABLE + "("
                        + "id integer primary key,"
                        + "user_jid varchar,"
                        + "roster_jid varchar)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




}
