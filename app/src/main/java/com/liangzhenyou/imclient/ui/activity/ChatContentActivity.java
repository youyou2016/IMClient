package com.liangzhenyou.imclient.ui.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.adapters.ChatContentListviewAdapter;
import com.liangzhenyou.imclient.dao.CustomMessage;
import com.liangzhenyou.imclient.utils.DataBaseUtils;
import com.liangzhenyou.imclient.db.SQLiteDatabaseHelper;
import com.liangzhenyou.imclient.manager.XmppconnectionManager;
import com.liangzhenyou.imclient.utils.FileUtils;
import com.liangzhenyou.imclient.utils.MessageUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import static com.liangzhenyou.imclient.R.id.chat_content_audio_send;

public class ChatContentActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "ChatContentActivity";


    private Button sendTextButton;

    private EditText editText;

    private Button sendAudioButton;

    private Button sendImageButton;

    private ListView contentListview;


    private SQLiteDatabaseHelper sqLiteDatabaseHelper;

    private SQLiteDatabase sqLiteDatabase;

    private DataBaseUtils dataBaseUtils;

    public static ArrayList<CustomMessage> arrayList = new ArrayList<>();

    public static ChatContentListviewAdapter adapter = null;

    private XMPPTCPConnection xmpptcpConnection;

    private MyHandler myHandler;

    /**
     * 用户的jid
     */
    private String userJid;

    /**
     * 聊天目标的jid
     */
    private String rosterJid;

    /**
     * 聊天对象
     */
    private Chat mChat;

    /**
     * 用于完成录音
     */
    private MediaRecorder mRecorder = null;

    private long recorderStartTime = 0;

    private long recorderStopTime = 0;

    private String audioFileDir = null;

    private String fileName = null;


    //private List<HashMap>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_content);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        rosterJid = b.getString("name");

        myHandler = new MyHandler(this);


        mRecorder = new MediaRecorder();
        audioFileDir = new File(getExternalFilesDir(null) + "/audio").toString();

        xmpptcpConnection = XmppconnectionManager.getXmpptcpConnection();


        initView();

        initData();

        initChat();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
    }


    @Override
    protected void onDestroy() {
        adapter.stopMediaPlayer();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = FileUtils.getRealFilePath(this, uri);
            Log.d(TAG, "path is " + path);

            CustomMessage customMessage = MessageUtils.createCustomMessage(CustomMessage.TYPE_IMAGE, userJid, rosterJid, path);
            try {
                mChat.sendMessage(MessageUtils.getMessageByCustomMessage(customMessage));
                dataBaseUtils.setMyMessageToDatabase(customMessage);
                arrayList.add(customMessage);
                myHandler.sendEmptyMessage(1);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initView() {
        sendTextButton = (Button) findViewById(R.id.chat_content_text_send);
        editText = (EditText) findViewById(R.id.chat_content_edit);
        sendAudioButton = (Button) findViewById(chat_content_audio_send);
        sendImageButton = (Button) findViewById(R.id.chat_content_imag_send);
        contentListview = (ListView) findViewById(R.id.chat_content_listview);


        sendImageButton.setOnTouchListener(this);
        sendAudioButton.setOnTouchListener(this);
        sendTextButton.setOnTouchListener(this);

    }

    public void initData() {

        String str = xmpptcpConnection.getUser();
        userJid = str.split("/")[0];

        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
        dataBaseUtils = new DataBaseUtils(sqLiteDatabase);
        arrayList = dataBaseUtils.getMyMessageFromDatabase(userJid, rosterJid);
        adapter = new ChatContentListviewAdapter(this, arrayList);

        contentListview.setAdapter(adapter);
    }

    public void initChat() {
        ChatManager manager = ChatManager.getInstanceFor(xmpptcpConnection);
        Set<ChatManagerListener> set = manager.getChatListeners();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            manager.removeChatListener((ChatManagerListener) iterator.next());
        }
        manager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                        CustomMessage customMessage = MessageUtils.getCustomMessageByMessage(message);
                        Log.d(TAG, "!createLocally, add message " + customMessage.getBody());
                        arrayList.add(customMessage);
                        myHandler.sendEmptyMessage(1);
                        dataBaseUtils.setMyMessageToDatabase(customMessage);
                    }
                });
            }
        });
        mChat = manager.createChat(rosterJid);
    }

/*
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
                            CustomMessage myMessage = MessageUtils.getCustomMessageByMessage(message);
                            Log.d(TAG, "!createLocally, add message " + myMessage.getBody());
                            arrayList.add(myMessage);
                            myHandler.sendEmptyMessage(1);
                            dataBaseUtils.setMyMessageToDatabase(myMessage);
                        }
                    });
                }
            }
        });*/
        /*mChat = manager.createChat(rosterJid, new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                CustomMessage myMessage = MessageUtils.getCustomMessageByMessage(message);
                Log.d(TAG, "createChat, add message " + myMessage.getBody());
                arrayList.add(myMessage);
                myHandler.sendEmptyMessage(1);
                dataBaseUtils.setMyMessageToDatabase(myMessage);
            }
        });*/


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        CustomMessage customMessage = null;
        String data = null;
        switch (v.getId()) {
            case R.id.chat_content_text_send:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "motion down");
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    data = editText.getText().toString();
                    if (StringUtils.isEmpty(data)) {
                        return true;
                    }
                    customMessage = MessageUtils.createCustomMessage(CustomMessage.TYPE_TEXT, userJid, rosterJid, data);
                }
                break;
            case R.id.chat_content_audio_send:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    File dir = new File(audioFileDir);
                    fileName = audioFileDir + "/" + UUID.randomUUID() + ".amr";
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    startRecorder(fileName);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!stopRecorder()) {
                        return true;
                    }
                    customMessage = MessageUtils.createCustomMessage(CustomMessage.TYPE_AUDIO, userJid, rosterJid, fileName);
                }
                break;

            case R.id.chat_content_imag_send:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);
                }
                break;

            default:
        }

        try {
            if (customMessage == null) {
                return true;
            }
            mChat.sendMessage(MessageUtils.getMessageByCustomMessage(customMessage));
            arrayList.add(customMessage);
            myHandler.sendEmptyMessage(0);
            dataBaseUtils.setMyMessageToDatabase(customMessage);

            Log.d(TAG, "sendMessage");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return false;
    }


    public void startRecorder(String fileName) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();

        recorderStartTime = System.currentTimeMillis();
    }

    public boolean stopRecorder() {
        if (mRecorder != null) {
            recorderStopTime = System.currentTimeMillis();
            if (recorderStopTime - recorderStartTime >= 1000) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                return true;
            } else {
                mRecorder.release();
                Toast.makeText(this, "录音时间太短", Toast.LENGTH_SHORT).show();
                mRecorder = null;
            }
        }
        return false;
    }

    private static class MyHandler extends Handler {
        private WeakReference<ChatContentActivity> mActivity;

        public MyHandler(ChatContentActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mActivity.get().editText.setText("");
                    break;
                case 1:
                    break;
                default:
            }
            adapter.notifyDataSetChanged();
        }
    }


}

