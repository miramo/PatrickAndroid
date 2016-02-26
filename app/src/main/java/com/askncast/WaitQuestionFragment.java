package com.askncast;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

    private String mQuestioner = "";
    private int mSkipVisibility = View.GONE;

    public WaitQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wait_question, container, false);

        ((TextView)view.findViewById(R.id.wait_question_text_view)).setText(String.format(getString(R.string.wait_question), mQuestioner));
        view.findViewById(R.id.skip_button).setVisibility(mSkipVisibility);
        return view;
    }

    @Override
    public void onStateChanged(GameManagerState newState) {
        try {
            if (newState.getGameData().has("questioner")) {
                mQuestioner = newState.getGameData().getString("questioner");
                if (getView() != null) {
                    ((TextView) getView().findViewById(R.id.wait_question_text_view)).setText(String.format(getString(R.string.wait_question), mQuestioner));
                }
            }
            if (newState.getGameData().has("skip_avail") && newState.getGameData().getBoolean("skip_avail"))
                mSkipVisibility = View.VISIBLE;
            else
                mSkipVisibility = View.GONE;

            if (getView() != null) {
                getView().findViewById(R.id.skip_button).setVisibility(mSkipVisibility);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
