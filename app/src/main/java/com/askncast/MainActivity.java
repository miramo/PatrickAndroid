package com.askncast;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.games.GameManagerClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.orm.SugarContext;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init database
        SugarContext.init(this);
        // Init button binding
        ButterKnife.bind(this);
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

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @OnClick(R.id.msg_send)
    public void onSendClicked() {
        try {
            JSONObject obj = new JSONObject(((EditText)findViewById(R.id.msg_edit_text)).getText().toString());
            AskNCastApplication.getInstance().sendMessage(obj);
        } catch (JSONException e) {
            new AlertDialog.Builder(this)
                    .setMessage("Failed to parse JSON")
                    .setNeutralButton("OK", null)
                    .create()
                    .show();
        }
    }
}
