package com.askncast;

import android.app.Application;
import android.os.Bundle;
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
    private String mPlayerId;

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
                mCastSessionId = applicationConnectionResult.getSessionId();
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
                mGameManagerClient.sendPlayerAvailableRequest(null).setResultCallback(new PlayerAvailableRequestResultCallback());
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
        mGameManagerClient.sendGameRequest(obj).setResultCallback(new GameRequestResultCallback());
    }

    private class GameRequestResultCallback implements ResultCallback<GameManagerClient.GameManagerResult> {
        @Override
        public void onResult(@NonNull GameManagerClient.GameManagerResult gameManagerResult) {
            if (!gameManagerResult.getStatus().isSuccess()) {
                setCastDevice(null);
            }
        }
    }
}
