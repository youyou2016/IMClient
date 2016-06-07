package com.liangzhenyou.imclient.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.manager.XmppconnectionManager;

import org.jivesoftware.smack.util.StringUtils;

import java.lang.ref.WeakReference;

import static com.liangzhenyou.imclient.R.id.radio_record_password;
import static com.liangzhenyou.imclient.R.id.useLogo;

public class LoginAcitivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";


    private MyHandler myHandler;

    private RadioButton recordPasswordRadioButton;
    private EditText usernameEdittext;
    private EditText passwordEdittext;
    private Button loginButton;
    private Button registerButton;
    public ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myHandler = new MyHandler(this);


        usernameEdittext = (EditText) findViewById(R.id.login_username_edittext);
        passwordEdittext = (EditText) findViewById(R.id.login_password_edittext);
        loginButton = (Button) findViewById(R.id.login_login_button);
        registerButton = (Button) findViewById(R.id.login_register_button);
        recordPasswordRadioButton = (RadioButton) findViewById(radio_record_password);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在登陆中");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myHandler.sendEmptyMessage(0);
                        boolean flag = XmppconnectionManager.connect();
                        if (flag) {
                            login();
                        } else {
                            myHandler.sendEmptyMessage(1);
                        }
                    }
                }).start();

                //如果选择记住密码
                if (recordPasswordRadioButton.isChecked()) {
                    String username = usernameEdittext.getText().toString();
                    String password = passwordEdittext.getText().toString();
                    if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                        SharedPreferences sharedPreferences = LoginAcitivity.this.getSharedPreferences("account", MODE_ENABLE_WRITE_AHEAD_LOGGING);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.commit();
                    }
                }

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAcitivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = LoginAcitivity.this.getSharedPreferences("account", MODE_ENABLE_WRITE_AHEAD_LOGGING);
        String username = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);
        if (username != null && password != null) {
            usernameEdittext.setText(username);
            passwordEdittext.setText(password);
            usernameEdittext.setSelection(username.length());
        }
    }

    private void login() {
        String username = usernameEdittext.getText().toString();
        String password = passwordEdittext.getText().toString();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            myHandler.sendEmptyMessage(4);
        } else {
            Log.d(TAG, username);
            Log.d(TAG, password);
            boolean isLogged = XmppconnectionManager.login(username, password);
            myHandler.sendEmptyMessage(0);
            if (isLogged) {
                Intent intent = new Intent(LoginAcitivity.this, IndexActivity.class);
                startActivity(intent);
                myHandler.sendEmptyMessage(2);
                finish();
            } else {
                myHandler.sendEmptyMessage(3);
            }
        }

    }


    private static class MyHandler extends Handler {
        private WeakReference<LoginAcitivity> mActivity;

        public MyHandler(LoginAcitivity activity) {
            mActivity = new WeakReference<>(activity);

        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mActivity.get().progressDialog.show();
                    break;
                case 1:
                    mActivity.get().progressDialog.dismiss();
                    Toast.makeText(mActivity.get(), "请连接网络", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(mActivity.get(), "登陆成功", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    mActivity.get().progressDialog.dismiss();
                    Toast.makeText(mActivity.get(), "账号密码错误", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    mActivity.get().progressDialog.dismiss();
                    Toast.makeText(mActivity.get(), "账号或者密码不能为空", Toast.LENGTH_LONG).show();
                    break;
                default:
            }
        }
    }
}