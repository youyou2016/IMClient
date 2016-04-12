package com.liangzhenyou.imclient.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.manager.XmppconnectionManager;

import org.jivesoftware.smack.util.StringUtils;

public class LoginAcitivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private XmppconnectionManager xmppconnectionManager;
    private static Handler handler;

    private EditText usernameEdittext;
    private EditText passwordEdittext;
    private Button loginButton;
    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        xmppconnectionManager = XmppconnectionManager.getNewInstance();
        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0:
                        login();
                        break;
                    case -1:
                        Toast.makeText(LoginAcitivity.this, "请连接网络", Toast.LENGTH_LONG).show();
                        break;

                    default:
                }
            }
        };

        usernameEdittext = (EditText) findViewById(R.id.username_edittext);
        passwordEdittext = (EditText) findViewById(R.id.password_edittext);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = xmppconnectionManager.connect();
                        if (flag) {
                            handler.sendEmptyMessage(0);
                        } else {
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }).start();
            }
        });


    }

    private void login() {
        String username = usernameEdittext.getText().toString();
        String password = passwordEdittext.getText().toString();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            Toast.makeText(LoginAcitivity.this, "账号或者密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, username);
            Log.d(TAG, password);
            boolean isLogged = xmppconnectionManager.login(username, password);
            if (isLogged) {
                Intent intent = new Intent(LoginAcitivity.this, IndexActivity.class);
                startActivity(intent);
                Toast.makeText(LoginAcitivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(LoginAcitivity.this, "账号密码错误", Toast.LENGTH_SHORT).show();
            }

        }

    }
}