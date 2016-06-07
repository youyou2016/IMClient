package com.liangzhenyou.imclient.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.manager.XmppconnectionManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.liangzhenyou.imclient.R.id.activity_user_search_searchbutton;

public class UserSearchActivity extends AppCompatActivity {


    private XMPPTCPConnection xmpptcpConnection;
    private UserSearchManager userSearchManager;

    private EditText searchEdittext;
    private ListView listView;
    private Button searchButton;
    private ArrayList searchResults;
    private SimpleAdapter simpleAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        initView();

        xmpptcpConnection = XmppconnectionManager.getXmpptcpConnection();
        userSearchManager = new UserSearchManager(xmpptcpConnection);
        searchResults = new ArrayList();


    }

    public void initView() {
        searchEdittext = (EditText) findViewById(R.id.activity_user_search_edittext);
        searchButton = (Button) findViewById(activity_user_search_searchbutton);
        listView = (ListView) findViewById(R.id.search_results_listview);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = searchEdittext.getText().toString();
                searchResults = new ArrayList(XmppconnectionManager.searchUsers(content));
                simpleAdapter = new SimpleAdapter(UserSearchActivity.this, searchResults, R.layout.roster_listview_item, new String[]{"Jid"}, new int[]{R.id.roster_listview_item_text});
                listView.setAdapter(simpleAdapter);
            }
        });
    }


}
