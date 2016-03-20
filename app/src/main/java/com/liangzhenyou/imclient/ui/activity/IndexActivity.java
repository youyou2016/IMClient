package com.liangzhenyou.imclient.ui.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liangzhenyou.imclient.R;

import org.w3c.dom.Text;

public class IndexActivity extends FragmentActivity {

    private static final String TAG = "IndexActivity";

    private RelativeLayout huihuaRlayout;

    private RelativeLayout rosterRlayout;

    private RelativeLayout personalRlayout;

    private ImageView chatImage; //会话的图片

    private ImageView rosterImage; //联系人的图片

    private ImageView personalImage; //“我”的图片

    private TextView chatText; //会话的文本

    private TextView rosterText; //联系人的文本

    private TextView personalText; //“我”的文本

    private static int currentTabIndex = 0;   //当前的tab的index

    private static final int INDEX_CHAT = 0;  //当前选中的tab为chat时对应的index

    private static final int INDEX_ROSTER = 1;  //当前选中的tab为roster时对应的index

    private static final int INDEX_PERSONAL = 2;  //当前选中的tab为personal时对应的index


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_index);

        huihuaRlayout = (RelativeLayout) findViewById(R.id.relative_layout_chat);
        rosterRlayout = (RelativeLayout) findViewById(R.id.relative_layout_roster);
        personalRlayout = (RelativeLayout) findViewById(R.id.relative_layout_personal);

        chatImage = (ImageView) findViewById(R.id.image_chat);
        rosterImage = (ImageView) findViewById(R.id.image_roster);
        personalImage = (ImageView) findViewById(R.id.image_personal);

        chatText = (TextView) findViewById(R.id.text_chat);
        rosterText = (TextView) findViewById(R.id.text_roster);
        personalText = (TextView) findViewById(R.id.text_personal);


    }

    /**
     * 响应屏幕下方tab的点击事件
     *
     * @param view
     */
    public void onClickTab(View view) {

        ImageView[] imageViews = {chatImage, rosterImage, personalImage};   //存放tab对应的imageview的数组

        TextView[] textViews = {chatText, rosterText, personalText};   //存放tab对应的textview的数组

        switch (view.getId()) {
            case R.id.relative_layout_chat:
                //把之前选中的tab状态修改
                imageViews[currentTabIndex].setSelected(false);
                textViews[currentTabIndex].setTextColor(0xf5181717);

                //修改tab的index
                currentTabIndex = INDEX_CHAT;

                //修改选中tab的外观
                imageViews[currentTabIndex].setSelected(true);
                textViews[currentTabIndex].setTextColor(0xff45c01a);


                Log.d(TAG, "huihuaRlayoug is onclick");
                break;
            case R.id.relative_layout_roster:
                //把之前选中的tab状态修改
                imageViews[currentTabIndex].setSelected(false);
                textViews[currentTabIndex].setTextColor(0xf5181717);

                //修改tab的index
                currentTabIndex = INDEX_ROSTER;

                //修改选中tab的外观
                imageViews[currentTabIndex].setSelected(true);
                textViews[currentTabIndex].setTextColor(0xff45c01a);

                Log.d(TAG, "rosterRlayout is onclick");
                break;
            case R.id.relative_layout_personal:
                //把之前选中的tab状态修改
                imageViews[currentTabIndex].setSelected(false);
                textViews[currentTabIndex].setTextColor(0xf5181717);

                //修改tab的index
                currentTabIndex = INDEX_PERSONAL;

                //修改选中tab的外观
                imageViews[currentTabIndex].setSelected(true);
                textViews[currentTabIndex].setTextColor(0xff45c01a);

                Log.d(TAG, "personalRlayout is onclick");
                break;

            default:
        }
    }

}
