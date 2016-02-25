package com.askncast;

import com.google.android.gms.cast.games.GameManagerState;

/**
 * Created by Julien on 25/02/2016.
 */
public interface IStateAwareFragment {
    public void onStateChanged(GameManagerState newState, GameManagerState oldState);
}
