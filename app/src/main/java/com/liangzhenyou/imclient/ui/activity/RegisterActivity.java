package com.liangzhenyou.imclient.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.manager.XmppconnectionManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText userNameEdittext;

    private EditText passwordEdittext;

    private EditText passwordRepeatEdittext;

    private EditText nickNameEdittext;

    private EditText emailEdittext;

    private Button registerButton;

    private XMPPTCPConnection xmpptcpConnection;

    private AccountManager accountManager;

    private MyHandler myHandler;

    private String userName;
    private String password;
    private String passwordRepeat;
    private String nickName;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();

        myHandler = new MyHandler(this);

        xmpptcpConnection = XmppconnectionManager.getXmpptcpConnection();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = userNameEdittext.getText().toString();
                password = passwordEdittext.getText().toString();
                passwordRepeat = passwordRepeatEdittext.getText().toString();
                nickName = nickNameEdittext.getText().toString();
                email = emailEdittext.getText().toString();


                if (StringUtils.isEmpty(userName)) {
                    Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(password) || StringUtils.isEmpty(passwordRepeat)) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(nickName)) {
                    Toast.makeText(RegisterActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(passwordRepeat)) {
                    Toast.makeText(RegisterActivity.this, "两次密码不同", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!xmpptcpConnection.isConnected()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                xmpptcpConnection.connect();
                                myHandler.sendEmptyMessage(1);
                                return;
                            } catch (SmackException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    myHandler.sendEmptyMessage(1);
                }

            }
        });

    }

    public void initView() {
        userNameEdittext = (EditText) findViewById(R.id.register_username_edittext);
        passwordEdittext = (EditText) findViewById(R.id.register_password_edittext);
        passwordRepeatEdittext = (EditText) findViewById(R.id.register_password_edittext_repeat);
        nickNameEdittext = (EditText) findViewById(R.id.register_nickname_edittext);
        registerButton = (Button) findViewById(R.id.register_register_button);
        emailEdittext = (EditText) findViewById(R.id.register_email_edittext);
    }

    public void register() {
        accountManager = AccountManager.getInstance(xmpptcpConnection);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("name", nickName);
        attributes.put("email", email);
        try {
            accountManager.createAccount(userName, password, attributes);
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            Toast.makeText(this, "用户名已注册", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            Toast.makeText(this, "请连接网络", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    private static class MyHandler extends Handler {
        private WeakReference<RegisterActivity> mActivity;

        public MyHandler(RegisterActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    Toast.makeText(mActivity.get(), "注册失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    mActivity.get().register();
                    break;
                default:

            }

        }
    }

}
