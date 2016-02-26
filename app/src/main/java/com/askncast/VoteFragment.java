package com.askncast;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.cast.games.GameManagerClient;
import com.google.android.gms.cast.games.GameManagerState;

import org.json.JSONException;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class VoteFragment extends StateAwareFragment {

    private String mQuestion;
    private int mNbPlayers;
    private boolean mSkipAvail = false;

    public VoteFragment() {
        // Required empty public constructor
        mQuestion = "";
        mNbPlayers = 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vote, container, false);

        ((TextView)view.findViewById(R.id.question_text_view)).setText(mQuestion);
        ((NumberPicker)view.findViewById(R.id.prognosis_number_picker)).setMaxValue(mNbPlayers);

        // Init button binding
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.vote_send)
    public void onVoteSend() {
        getView().findViewById(R.id.answer_switch).setEnabled(false);
        getView().findViewById(R.id.prognosis_number_picker).setEnabled(false);
        getView().findViewById(R.id.vote_send).setVisibility(View.GONE);
        AskNCastApplication.getInstance().sendVote(((Switch)getView().findViewById(R.id.answer_switch)).isChecked(), ((NumberPicker)getView().findViewById(R.id.prognosis_number_picker)).getValue());
    }

    @Override
    public void onStateChanged(GameManagerState newState) {
        try {
            mQuestion = newState.getGameData().getString("question");
            mNbPlayers = newState.getPlayersInState(GameManagerClient.PLAYER_STATE_PLAYING).size();
            mSkipAvail = newState.getGameData().getBoolean("skip_avail");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getView() != null) {
            ((TextView)getView().findViewById(R.id.question_text_view)).setText(mQuestion);
            ((NumberPicker)getView().findViewById(R.id.prognosis_number_picker)).setMaxValue(mNbPlayers);
        }
    }
}
