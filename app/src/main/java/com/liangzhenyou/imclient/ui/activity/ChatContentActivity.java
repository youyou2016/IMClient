package com.liangzhenyou.imclient.ui.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.adapters.ChatContentListviewAdapter;
import com.liangzhenyou.imclient.dao.DataBaseUtils;
import com.liangzhenyou.imclient.dao.MyMessage;
import com.liangzhenyou.imclient.db.SQLiteDatabaseHelper;
import com.liangzhenyou.imclient.manager.XmppconnectionManager;
import com.liangzhenyou.imclient.utils.MessageUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class ChatContentActivity extends AppCompatActivity {

    private static final String TAG = "ChatContentActivity";


    private Button sendTextButton;

    private EditText editText;

    private ListView contentListview;

    private SQLiteDatabaseHelper sqLiteDatabaseHelper;

    private SQLiteDatabase sqLiteDatabase;

    private DataBaseUtils dataBaseUtils;

    public static ArrayList<MyMessage> arrayList = new ArrayList<>();

    public static ChatContentListviewAdapter adapter = null;

    private XMPPTCPConnection xmpptcpConnection;

    private Handler mHandler;

    //private List<HashMap>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_content);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        final String uerJid = b.getString("name");

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        editText.setText("");
                        break;
                    default:
                }
                adapter.notifyDataSetChanged();
            }
        };

        sendTextButton = (Button) findViewById(R.id.chat_content_send);
        editText = (EditText) findViewById(R.id.chat_content_edit);

        contentListview = (ListView) findViewById(R.id.chat_content_listview);

        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
        dataBaseUtils = new DataBaseUtils(sqLiteDatabase);
        arrayList = dataBaseUtils.getMyMessageFromDatabase(uerJid);

        adapter = new ChatContentListviewAdapter(this, arrayList);

        contentListview.setAdapter(adapter);

        contentListview.setSelection(adapter.getCount() - 1);

        xmpptcpConnection = XmppconnectionManager.getXmpptcpConnection();

        ChatManager manager = ChatManager.getInstanceFor(xmpptcpConnection);

        manager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                if (!createdLocally) {
                    Set set = chat.getListeners();
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        chat.removeMessageListener((ChatMessageListener) iterator.next());
                    }
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                            MyMessage myMessage = MessageUtils.getMyMessageByMessage(message);
                            Log.d(TAG, "!createLocally, add message " + myMessage.getBody());
                            arrayList.add(myMessage);
                            mHandler.sendEmptyMessage(1);
                            dataBaseUtils.setMyMessageToDatabase(myMessage);
                        }
                    });
                }
            }
        });

        final Chat chat = manager.createChat(uerJid, new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                MyMessage myMessage = MessageUtils.getMyMessageByMessage(message);
                Log.d(TAG, "createChat, add message " + myMessage.getBody());
                arrayList.add(myMessage);
                mHandler.sendEmptyMessage(1);
                dataBaseUtils.setMyMessageToDatabase(myMessage);
            }
        });

        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String data = editText.getText().toString();
                    if(StringUtils.isEmpty(data)){
                        return;
                    }
                    MyMessage myMessage = new MyMessage();
                    myMessage.setORIGIN(MyMessage.ORIGIN_LOCAL);
                    myMessage.setDateFormat((DateFormat.format("MM/dd/yy h:mmaa", System.currentTimeMillis()).toString()));
                    myMessage.setUserJid(uerJid);
                    myMessage.setBody(data);
                    myMessage.setTYPE(MyMessage.TYPE_TEXT);
                    chat.sendMessage(myMessage.getTYPE() + myMessage.getBody());
                    arrayList.add(myMessage);
                    mHandler.sendEmptyMessage(0);
                    dataBaseUtils.setMyMessageToDatabase(myMessage);

                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });


    }


}
