package com.askncast;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.cast.games.GameManagerState;

import org.json.JSONException;


/**
 * A simple {@link Fragment} subclass.
 */
public class WaitQuestionFragment extends StateAwareFragment {


    public WaitQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wait_question, container, false);
    }

    @Override
    public void onStateChanged(GameManagerState newState) {
        try {
            if (newState.getGameData().has("questioner"))
                ((TextView)getView().findViewById(R.id.wait_question_text_view)).setText(String.format(getString(R.string.wait_question), newState.getGameData().getString("questioner")));
            if (newState.getGameData().has("skip_avail") && newState.getGameData().getBoolean("skip_avail"))
                getView().findViewById(R.id.skip_button).setVisibility(View.VISIBLE);
            else
                getView().findViewById(R.id.skip_button).setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
