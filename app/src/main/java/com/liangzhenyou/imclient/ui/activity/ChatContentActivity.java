package com.liangzhenyou.imclient.ui.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.adapters.ChatContentListviewAdapter;
import com.liangzhenyou.imclient.dao.DataBaseUtils;
import com.liangzhenyou.imclient.dao.MyMessage;
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
import java.io.FileInputStream;
import java.io.IOException;
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

    public static ArrayList<MyMessage> arrayList = new ArrayList<>();

    public static ChatContentListviewAdapter adapter = null;

    private XMPPTCPConnection xmpptcpConnection;

    private Handler mHandler;

    /**
     * 聊天目标的jid
     */
    private String userJid;

    /**
     * 聊天对象
     */
    private Chat mChat;

    /**
     * 用于语音播放
     */
    private MediaPlayer mPlayer = null;
    /**
     * 用于完成录音
     */
    private MediaRecorder mRecorder = null;

    private String audioFileDir = null;

    private String fileName = null;
    //private List<HashMap>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_content);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        userJid = b.getString("name");
        mPlayer = new MediaPlayer();

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

        mPlayer = new MediaPlayer();
        mRecorder = new MediaRecorder();
        audioFileDir = new File(getExternalFilesDir(null) + "/audio").toString();

        xmpptcpConnection = XmppconnectionManager.getXmpptcpConnection();


        initView();

        initData();

        initChat();

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

        contentListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyMessage myMessage = arrayList.get(position);
                if (myMessage.getTYPE() == MyMessage.TYPE_AUDIO) {
                    try {
                        mPlayer.reset();
                        File file = new File(myMessage.getBody());
                        FileInputStream fis = new FileInputStream(file);
                        mPlayer.setDataSource(fis.getFD());
                        mPlayer.prepare();
                        mPlayer.start();
                        Log.d(TAG, "mPlayer is playing");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void initData() {
        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();
        dataBaseUtils = new DataBaseUtils(sqLiteDatabase);
        arrayList = dataBaseUtils.getMyMessageFromDatabase(userJid);
        adapter = new ChatContentListviewAdapter(this, arrayList);

        contentListview.setAdapter(adapter);
    }

    public void initChat() {
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
        mChat = manager.createChat(userJid, new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                MyMessage myMessage = MessageUtils.getMyMessageByMessage(message);
                Log.d(TAG, "createChat, add message " + myMessage.getBody());
                arrayList.add(myMessage);
                mHandler.sendEmptyMessage(1);
                dataBaseUtils.setMyMessageToDatabase(myMessage);
            }
        });


    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        MyMessage myMessage = null;
        String data = null;
        switch (v.getId()) {
            case R.id.chat_content_text_send:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return false;
                }
                data = editText.getText().toString();
                if (StringUtils.isEmpty(data)) {
                    return false;
                }
                myMessage = MessageUtils.createMyMessage(MyMessage.TYPE_TEXT, userJid, data);
                break;
            case R.id.chat_content_audio_send:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    File dir = new File(audioFileDir);
                    fileName = audioFileDir + "/" + UUID.randomUUID() + ".amr";
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    startRecorder(fileName);
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecorder();
                    myMessage = MessageUtils.createMyMessage(MyMessage.TYPE_AUDIO, userJid, fileName);
                }
                break;

            case R.id.chat_content_imag_send:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return false;
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
            if (myMessage == null) {
                return true;
            }
            mChat.sendMessage(MessageUtils.getMessageByMyMessage(myMessage));
            arrayList.add(myMessage);
            mHandler.sendEmptyMessage(0);
            dataBaseUtils.setMyMessageToDatabase(myMessage);
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
    }

    public void stopRecorder() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

    }
}

