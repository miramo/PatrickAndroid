package com.askncast;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.google.android.gms.cast.games.GameManagerState;
import com.orm.SugarContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private HashMap<String, StateAwareFragment> mFragments;
    private StateAwareFragment mFragment = null;

    private static final String FRAG_INIT = "Init";
    private static final String FRAG_WAIT_PHASE = "WaitPhase";
    private static final String FRAG_PICK_QUESTION = "PickQuestion";
    private static final String FRAG_WAIT_FOR_QUESTION = "WaitQuestion";
    private static final String FRAG_VOTE = "Vote";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init database
        SugarContext.init(this);
        // Init button binding
        ButterKnife.bind(this);

        AskNCastApplication.getInstance().setListener(new AskNCastListener());

        mFragments = new HashMap<>();
        mFragments.put(FRAG_INIT, new InitFragment());
        mFragments.put(FRAG_WAIT_PHASE, new WaitPhaseFragment());
        mFragments.put(FRAG_PICK_QUESTION, new PickQuestionFragment());
        mFragments.put(FRAG_WAIT_FOR_QUESTION, new WaitQuestionFragment());
        mFragments.put(FRAG_VOTE, new VoteFragment());

        this.moveToFragment(FRAG_INIT);
    }

    private boolean mOpenDebug = true;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.debug_menu_item:
                AskNCastApplication.getInstance().openDebug(mOpenDebug);
                mOpenDebug = !mOpenDebug;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void moveToFragment(String fragName) {
        moveToFragment(fragName, null);
    }

    private void moveToFragment(String fragName, GameManagerState state) {
        StateAwareFragment to = mFragments.get(fragName);
        if (mFragment == null || mFragment != to) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, to)
                    .commit();
            mFragment = to;
        }
        if (state != null) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Game state: " + state.getGameData())
                    .create()
                    .show();
            mFragment.onStateChanged(state);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        // Link icon and chromecast target to our Ask'n'cast receiver app
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(AskNCastApplication.getInstance().getMediaRouteSelector());
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AskNCastApplication.getInstance().startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AskNCastApplication.getInstance().stopScan();
    }
//
//    @OnClick(R.id.ready_button)
//    public void onReadyClick() {
//        AskNCastApplication.getInstance().startPlaying();
//    }
//
//    @OnClick(R.id.question_send)
//    public void onSendQuestionClick() {
//        String question = ((EditText)findViewById(R.id.question)).getText().toString();
//        AskNCastApplication.getInstance().sendQuestion(question);
//    }
//
//    @OnClick(R.id.vote_send)
//    public void onSendVoteClick() {
//        boolean vote = ((CheckBox)findViewById(R.id.vote)).isChecked();
//        int progno = ((NumberPicker)findViewById(R.id.prognosis)).getValue();
//        AskNCastApplication.getInstance().sendVote(vote, progno);
//    }
//
//    @OnClick(R.id.skip)
//    public void onSkipClick() {
//        AskNCastApplication.getInstance().skip();
//    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    class AskNCastListener implements AskNCastApplication.Listener {

        @Override
        public String getPlayerName() {
            String playerName = ((EditText)findViewById(R.id.name_edit_text)).getText().toString();

            if (playerName.isEmpty())
            {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(getString(R.string.empty_name_err))
                        .create()
                        .show();
            }
            return playerName;
        }

        @Override
        public void onDisconnected() {
            moveToFragment(FRAG_INIT);
        }

        @Override
        public void onConnected(GameManagerState state) {
            try {
                if (state.getGameData().has("phase") && state.getGameData().getString("phase").equals("choosing")) {
                    AskNCastApplication.getInstance().startPlaying();

                    if (state.getGameData().has("questioner_id") && state.getGameData().getString("questioner_id").equals(AskNCastApplication.getInstance().getPlayerId()))
                        moveToFragment(FRAG_PICK_QUESTION, state);
                    else
                        moveToFragment(FRAG_WAIT_FOR_QUESTION, state);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            moveToFragment(FRAG_WAIT_PHASE, state);
        }

        @Override
        public void onStateChanged(GameManagerState newState, GameManagerState oldState) {
            if (AskNCastApplication.getInstance().isConnected() && newState.hasGameDataChanged(oldState)) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Game data changed: " + newState.getGameData())
                        .create()
                        .show();
                boolean stop = false;
                try {
                    if (!newState.getGameData().has("phase"))
                        stop = true;
                    else
                    {
                        if (newState.getGameData().getString("phase").equals("voting"))
                            moveToFragment(FRAG_VOTE, newState);
                        else if (newState.getGameData().getString("phase").equals("choosing"))
                        {
                            if (!AskNCastApplication.getInstance().isPlaying())
                                AskNCastApplication.getInstance().startPlaying();
                            if (!newState.getGameData().has("questioner_id"))
                                stop = true;
                            else
                            {
                                if (newState.getGameData().getString("questioner_id").equals(AskNCastApplication.getInstance().getPlayerId()))
                                    moveToFragment(FRAG_PICK_QUESTION, newState);
                                else
                                    moveToFragment(FRAG_WAIT_FOR_QUESTION, newState);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    stop = true;
                }
                if (stop)
                    AskNCastApplication.getInstance().stopPlaying();
            }
        }

        @Override
        public void onGameMessageReceived(String s, JSONObject jsonObject) {

        }
    }
}
