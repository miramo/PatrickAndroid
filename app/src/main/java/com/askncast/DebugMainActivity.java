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

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DebugMainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_main);

        // Init database
        SugarContext.init(this);
        // Init button binding
        ButterKnife.bind(this);

        AskNCastApplication.getInstance().setListener(new AskNCastListener());

        NumberPicker np = (NumberPicker)findViewById(R.id.prognosis);
        np.setMinValue(0);
        np.setMaxValue(10);
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

    @OnClick(R.id.ready_button)
    public void onReadyClick() {
        AskNCastApplication.getInstance().startPlaying();
    }

    @OnClick(R.id.question_send)
    public void onSendQuestionClick() {
        String question = ((EditText)findViewById(R.id.question)).getText().toString();
        AskNCastApplication.getInstance().sendQuestion(question);
    }

    @OnClick(R.id.vote_send)
    public void onSendVoteClick() {
        boolean vote = ((CheckBox)findViewById(R.id.vote)).isChecked();
        int progno = ((NumberPicker)findViewById(R.id.prognosis)).getValue();
        AskNCastApplication.getInstance().sendVote(vote, progno);
    }

    @OnClick(R.id.skip)
    public void onSkipClick() {
        AskNCastApplication.getInstance().skip();
    }

    @OnClick(R.id.open_debug)
    public void onOpenDebugClick()
    {
        AskNCastApplication.getInstance().openDebug(true);
    }

    @OnClick(R.id.close_debug)
    public void onCloseDebugClick()
    {
        AskNCastApplication.getInstance().openDebug(false);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    class AskNCastListener implements AskNCastApplication.Listener {

        @Override
        public String getPlayerName() {
            return ((EditText)findViewById(R.id.player_name)).getText().toString();
        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onConnected(GameManagerState state) {

        }

        @Override
        public void onStateChanged(GameManagerState newState, GameManagerState oldState) {
            if (newState.hasGameDataChanged(oldState)) {
                new AlertDialog.Builder(DebugMainActivity.this)
                        .setMessage("Game data changed: " + newState.getGameData())
                        .create()
                        .show();
            }
        }

        @Override
        public void onGameMessageReceived(String s, JSONObject jsonObject) {

        }
    }
}
