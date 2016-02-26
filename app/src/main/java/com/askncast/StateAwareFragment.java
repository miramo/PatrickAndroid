package com.askncast;

import android.app.Fragment;

import com.google.android.gms.cast.games.GameManagerState;

/**
 * Created by Julien on 25/02/2016.
 */
public abstract class StateAwareFragment extends Fragment {
    public abstract void onStateChanged(GameManagerState newState);
}
