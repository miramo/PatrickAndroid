package com.askncast;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.games.GameManagerState;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickQuestionFragment extends StateAwareFragment {


    public PickQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pick_question, container, false);
    }

    @Override
    public void onStateChanged(GameManagerState newState) {
        
    }
}
