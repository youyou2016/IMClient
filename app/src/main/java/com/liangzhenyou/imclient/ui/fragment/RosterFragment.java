package com.liangzhenyou.imclient.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liangzhenyou.imclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RosterFragment extends Fragment {

    private final static String TAG = "RosterFragment";

    public RosterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roster, container, false);
    }

}
