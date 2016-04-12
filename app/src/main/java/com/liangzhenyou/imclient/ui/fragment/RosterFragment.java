package com.liangzhenyou.imclient.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.manager.XmppconnectionManager;
import com.liangzhenyou.imclient.ui.activity.ChatContentActivity;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RosterFragment extends Fragment {

    private final static String TAG = "RosterFragment";

    private ListView rosterListView;

    private Handler myHandler;

    private XMPPTCPConnection xmpptcpConnection;

    private Roster roster;

    public RosterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_roster, container, false);

        xmpptcpConnection = XmppconnectionManager.getXmpptcpConnection();

        rosterListView = (ListView) view.findViewById(R.id.roster_listview);

        roster = Roster.getInstanceFor(xmpptcpConnection);

        final List<HashMap<String, String>> nameList = new ArrayList<>();

        Collection<RosterEntry> rosterEntries = roster.getEntries();
        Iterator<RosterEntry> iterator = rosterEntries.iterator();
        while (iterator.hasNext()) {
            RosterEntry rosterEntry = iterator.next();
            if (rosterEntry.getUser() != null) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("name", rosterEntry.getUser());
                Log.d(TAG, rosterEntry.getUser());
                nameList.add(hashMap);

                RosterEntry r = roster.getEntry(rosterEntry.getUser());
                String str = r.getName();
                if (str != null) {
                    Log.d(TAG, str);
                }

            }
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), nameList, R.layout.roster_listview_item, new String[]{"name"}, new int[]{R.id.roster_listview_item_text});

        rosterListView.setAdapter(simpleAdapter);

        rosterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = nameList.get(position).get("name");
                Intent intent = new Intent(getActivity(), ChatContentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        return view;
    }

}
