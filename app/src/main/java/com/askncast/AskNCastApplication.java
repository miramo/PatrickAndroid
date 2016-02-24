package com.askncast;

import android.app.Application;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.games.GameManagerClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Julien on 22/02/2016.
 */
public class AskNCastApplication extends Application {

    private static AskNCastApplication mInstance;

    public static AskNCastApplication getInstance() {
        return mInstance;
    }

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouterSelector;
    private MediaRouterCallback mMediaRouterCallback;
    private CastDevice mCastDevice;
    private GoogleApiClient mApiClient;
    private String mCastSessionId;
    private GameManagerClient mGameManagerClient;
    private String mPlayerId = null;

    public interface Listener extends GameManagerClient.Listener
    {
        String getPlayerName();
    }

    private Listener mListener = null;

    public AskNCastApplication() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaRouter = MediaRouter.getInstance(this.getApplicationContext());
        mMediaRouterSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(getString(R.string.APP_ID)))
                .build();
        mMediaRouterCallback = new MediaRouterCallback();
    }

    public MediaRouteSelector getMediaRouteSelector() {
        return this.mMediaRouterSelector;
    }

    public void setCastDevice(CastDevice device) {
        mCastDevice = device;
        disconnectApiClient();

        if (mCastDevice != null) {
            connectToApi();
        }
        else {
            mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());
        }
    }

    private void disconnectApiClient() {
        if (mGameManagerClient != null) {
            mGameManagerClient.dispose();
            mGameManagerClient = null;
        }
        if (mApiClient != null && mApiClient.isConnected()) {
            mApiClient.disconnect();
        }
        mApiClient = null;
    }

    private void connectToApi() {
        Cast.CastOptions.Builder appOptionsBuilder = new Cast.CastOptions.Builder(mCastDevice, new CastListener());
        ApiConnectionCallback callback = new ApiConnectionCallback();
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Cast.API, appOptionsBuilder.build())
                .addConnectionCallbacks(callback)
                .addOnConnectionFailedListener(callback)
                .build();
        mApiClient.connect();
    }

    public void startScan() {
        mMediaRouter.addCallback(mMediaRouterSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    public void stopScan() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    private class MediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteSelected(router, route);
            setCastDevice(CastDevice.getFromBundle(route.getExtras()));
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
            setCastDevice(null);
        }
    }

    private class CastListener extends Cast.Listener {
        @Override
        public void onApplicationDisconnected(int statusCode) {
            super.onApplicationDisconnected(statusCode);
            setCastDevice(null);
        }
    }

    private class ApiConnectionCallback implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (mApiClient == null || !mApiClient.isConnected()) {
                setCastDevice(null);
            }
            else {
                Cast.CastApi.launchApplication(mApiClient, getString(R.string.APP_ID))
                        .setResultCallback(new LaunchApplicationResultCallback());
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    }

    private final class LaunchApplicationResultCallback implements ResultCallback<Cast.ApplicationConnectionResult> {
        @Override
        public void onResult(@NonNull Cast.ApplicationConnectionResult applicationConnectionResult) {
            Status status = applicationConnectionResult.getStatus();
            if (status.isSuccess()) {
                String sessionId = applicationConnectionResult.getSessionId();
                if (!sessionId.equals(mCastSessionId))
                    mPlayerId = null;
                mCastSessionId = sessionId;
                GameManagerClient.getInstanceFor(mApiClient, mCastSessionId)
                        .setResultCallback(new GameManagerGetInstanceResultCallback());
            }
            else {
                setCastDevice(null);
            }
        }
    }

    private class GameManagerGetInstanceResultCallback implements ResultCallback<GameManagerClient.GameManagerInstanceResult> {
        @Override
        public void onResult(@NonNull GameManagerClient.GameManagerInstanceResult gameManagerInstanceResult) {
            if (!gameManagerInstanceResult.getStatus().isSuccess()) {
                setCastDevice(null);
            }
            else {
                mGameManagerClient = gameManagerInstanceResult.getGameManagerClient();
                mGameManagerClient.setListener(mListener);

                JSONObject extra = new JSONObject();
                try {
                    extra.put("name", mListener.getPlayerName());
                } catch (JSONException e) {
                    // Ugly, but shouldn't happen
                    e.printStackTrace();
                    System.exit(1);
                }

                mGameManagerClient.sendPlayerAvailableRequest(mPlayerId, extra).setResultCallback(new PlayerAvailableRequestResultCallback());
            }
        }
    }

    private class PlayerAvailableRequestResultCallback implements ResultCallback<GameManagerClient.GameManagerResult> {
        @Override
        public void onResult(@NonNull GameManagerClient.GameManagerResult gameManagerResult) {
            if (gameManagerResult.getStatus().isSuccess()) {
                mPlayerId = gameManagerResult.getPlayerId();
            } else {
                setCastDevice(null);
            }
        }
    }

    public void sendMessage(JSONObject obj) {
        mGameManagerClient.sendGameRequest(mPlayerId, obj).setResultCallback(new GameRequestResultCallback());
    }

    private class GameRequestResultCallback implements ResultCallback<GameManagerClient.GameManagerResult> {
        @Override
        public void onResult(@NonNull GameManagerClient.GameManagerResult gameManagerResult) {
            if (!gameManagerResult.getStatus().isSuccess()) {
                setCastDevice(null);
            }
        }
    }

    public void setListener(Listener listener)
    {
        this.mListener = listener;
        if (this.mGameManagerClient != null)
            this.mGameManagerClient.setListener(listener);
    }

    public void startPlaying()
    {
        mGameManagerClient.sendPlayerPlayingRequest(null).setResultCallback(new GameRequestResultCallback());
    }

    public void sendQuestion(String question)
    {
        JSONObject msg = new JSONObject();

        try {
            msg.put("type", "question");
            msg.put("question", question);
        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(2);
        }
        this.sendMessage(msg);
    }

    public void sendVote(boolean yes, int prognosis)
    {
        JSONObject msg = new JSONObject();

        try {
            msg.put("type", "vote");
            msg.put("vote", (yes ? "yes" : "no"));
            msg.put("prognosis", prognosis);
        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(3);
        }
        this.sendMessage(msg);
    }

    public void skip() {
        JSONObject msg = new JSONObject();

        try {
            msg.put("type", "skip");
        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(4);
        }
        this.sendMessage(msg);
    }
}
