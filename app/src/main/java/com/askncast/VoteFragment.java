package com.askncast;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.cast.games.GameManagerClient;
import com.google.android.gms.cast.games.GameManagerState;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class VoteFragment extends StateAwareFragment {

    private String mQuestion;
    private int mNbPlayers;
    private boolean mSkipAvail = false;

    private boolean mAnswer = false;

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

//        ((TextView)view.findViewById(R.id.question_text_view)).setText(mQuestion);
        ((NumberPicker)view.findViewById(R.id.prognosis_number_picker)).setMaxValue(mNbPlayers);
        view.findViewById(R.id.skip_button).setVisibility(mSkipAvail ? View.VISIBLE : View.GONE);

//        view.findViewById(R.id.question_card_view).setOnTouchListener(new OnSwipeTouchListener(container.getContext())
//        {
//            @Override
//            public void onSwipeLeft() {
//                super.onSwipeLeft();
//                onVoteNoClicked();
//            }
//
//            @Override
//            public void onSwipeRight() {
//                super.onSwipeRight();
//                onVoteYesClicked();
//            }
//        });


        //add the view via xml or programmatically
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView)view.findViewById(R.id.question_swipe_view);
        ArrayList<String> al = new ArrayList<String>();
        al.add(mQuestion);

        //choose your favorite adapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.question_view, R.id.question_text_view, al);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object o) {
                onVoteNoClicked();
            }

            @Override
            public void onRightCardExit(Object o) {
                onVoteYesClicked();
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {

            }

            @Override
            public void onScroll(float v) {

            }
        });

        // Init button binding
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.vote_yes_button)
    public void onVoteYesClicked() {
        setVote(true);
    }

    @OnClick(R.id.vote_no_button)
    public void onVoteNoClicked() {
        setVote(false);
    }

    @OnClick(R.id.prognosis_ok_button)
    public void onPrognosisOkButtonClicked() {
        AskNCastApplication.getInstance().sendVote(mAnswer, ((NumberPicker)getView().findViewById(R.id.prognosis_number_picker)).getValue());
        getView().findViewById(R.id.prognosis_frame).setVisibility(View.GONE);
        getView().findViewById(R.id.wait_frame).setVisibility(View.VISIBLE);
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
//            ((TextView)getView().findViewById(R.id.question_text_view)).setText(mQuestion);
            ((NumberPicker)getView().findViewById(R.id.prognosis_number_picker)).setMaxValue(mNbPlayers);
            getView().findViewById(R.id.skip_button).setVisibility(mSkipAvail ? View.VISIBLE : View.GONE);
        }
    }

    public void setVote(boolean vote) {
        mAnswer = vote;
        getView().findViewById(R.id.answer_frame).setVisibility(View.GONE);
        getView().findViewById(R.id.prognosis_frame).setVisibility(View.VISIBLE);
    }
}
