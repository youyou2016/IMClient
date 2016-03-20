package com.liangzhenyou.imclient.ui.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.ui.fragment.ChatFragment;
import com.liangzhenyou.imclient.ui.fragment.PersonalFragment;
import com.liangzhenyou.imclient.ui.fragment.RosterFragment;


public class IndexActivity extends FragmentActivity {

    private static final String TAG = "IndexActivity";

    private ImageView chatImage; //会话的图片

    private ImageView rosterImage; //联系人的图片

    private ImageView personalImage; //“我”的图片

    private TextView chatText; //会话的文本

    private TextView rosterText; //联系人的文本

    private TextView personalText; //“我”的文本

    private static final int INDEX_CHAT = 0;  //当前选中的tab为chat时对应的index

    private static final int INDEX_ROSTER = 1;  //当前选中的tab为roster时对应的index

    private static final int INDEX_PERSONAL = 2;  //当前选中的tab为personal时对应的index

    private Fragment chatFragment;  //聊天界面

    private Fragment rosterFragment;  //联系人界面

    private Fragment personalFragment;  //个人设置页面

    private FragmentManager fragmentManager; //fragment的管理类，用来控制fragment的显示与隐藏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_index);

        initView();

        fragmentManager = getSupportFragmentManager();
        //如果savedInstanceState为空，那么程序上一次是正常关闭
        //如果savedInstanceState不为空，那么程序上一次是异常关闭，对象还没有被回收，
        //如果直接创建新的fragment，那么有可能会造成之前的fragment没有隐藏，画面重叠
        if (savedInstanceState != null) {
            chatFragment = fragmentManager.findFragmentByTag("ChatFragment");
            rosterFragment = fragmentManager.findFragmentByTag("RosterFragment");
            personalFragment = fragmentManager.findFragmentByTag("PersonalFragment");
        }

        setTabSelection(INDEX_CHAT);

    }

    /**
     * 初始化控件
     */
    private void initView() {

        chatImage = (ImageView) findViewById(R.id.image_chat);
        rosterImage = (ImageView) findViewById(R.id.image_roster);
        personalImage = (ImageView) findViewById(R.id.image_personal);

        chatText = (TextView) findViewById(R.id.text_chat);
        rosterText = (TextView) findViewById(R.id.text_roster);
        personalText = (TextView) findViewById(R.id.text_personal);
    }


    /**
     * 设置tab的选中状态和外观修改
     *
     * @param index tab对应的序号
     */
    private void setTabSelection(int index) {
        clearSelection();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);

        switch (index) {
            case INDEX_CHAT:
                chatImage.setSelected(true);
                chatText.setTextColor(0xff45c01a);
                if (chatFragment == null) {
                    chatFragment = new ChatFragment();
                    transaction.add(R.id.fragment_container, chatFragment, "ChatFragment");
                }
                transaction.show(chatFragment);
                break;
            case INDEX_ROSTER:
                rosterImage.setSelected(true);
                rosterText.setTextColor(0xff45c01a);
                if (rosterFragment == null) {
                    rosterFragment = new RosterFragment();
                    transaction.add(R.id.fragment_container, rosterFragment, "RosterFragment");
                }
                transaction.show(rosterFragment);
                break;
            case INDEX_PERSONAL:
                personalImage.setSelected(true);
                personalText.setTextColor(0xff45c01a);
                if (personalFragment == null) {
                    personalFragment = new PersonalFragment();
                    transaction.add(R.id.fragment_container, personalFragment, "PersonalFragment");
                }
                transaction.show(personalFragment);
                break;
            default:
        }
        transaction.commit();
    }

    /**
     * 清除tab的选中状态
     */
    private void clearSelection() {
        chatImage.setSelected(false);
        chatText.setTextColor(0xf5181717);
        rosterImage.setSelected(false);
        rosterText.setTextColor(0xf5181717);
        personalImage.setSelected(false);
        personalText.setTextColor(0xf5181717);
    }


    /**
     * 隐藏所有的fragment
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (chatFragment != null) {
            transaction.hide(chatFragment);
        }
        if (rosterFragment != null) {
            transaction.hide(rosterFragment);
        }
        if (personalFragment != null) {
            transaction.hide(personalFragment);
        }
    }


    /**
     * 响应屏幕下方tab的点击事件
     *
     * @param view
     */
    public void onClickTab(View view) {

        switch (view.getId()) {
            case R.id.relative_layout_chat:
                setTabSelection(INDEX_CHAT);

                Log.d(TAG, "huihuaRlayoug is onclick");
                break;
            case R.id.relative_layout_roster:
                setTabSelection(INDEX_ROSTER);

                Log.d(TAG, "rosterRlayout is onclick");
                break;
            case R.id.relative_layout_personal:
                setTabSelection(INDEX_PERSONAL);

                Log.d(TAG, "personalRlayout is onclick");
                break;

            default:
        }
    }

}
