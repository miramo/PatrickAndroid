package com.askncast;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.cast.games.GameManagerState;

import butterknife.ButterKnife;

public class InitFragment extends StateAwareFragment {

//    private MediaRouterCallback mMediaRouterCallback;
//    MediaRouteButton mMediaRouteButton;

    public InitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_init, container, false);
        // Init button binding
        ButterKnife.bind(this, view);

//        mMediaRouterCallback = new MediaRouterCallback();
//        // Set the MediaRouteButton selector for device discovery.
//        mMediaRouteButton = (MediaRouteButton) view.findViewById(R.id.start_button);
//        mMediaRouteButton.setRouteSelector(AskNCastApplication.getInstance().getMediaRouteSelector());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        ((EditText)getView().findViewById(R.id.name_edit_text)).setText(pref.getString(getString(R.string.prerence_name), ""));
    }

    @Override
    public void onResume() {
        super.onResume();
//        AskNCastApplication.getInstance().getMediaRouter().addCallback(AskNCastApplication.getInstance().getMediaRouteSelector(), mMediaRouterCallback,
//                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    public void onPause() {
//        AskNCastApplication.getInstance().getMediaRouter().removeCallback(mMediaRouterCallback);
        super.onPause();
    }

    @Override
    public void onStop() {
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        pref.edit()
                .putString(getString(R.string.prerence_name), ((EditText)getView().findViewById(R.id.name_edit_text)).getText().toString())
                .apply();
        super.onDestroyView();
    }

    @Override
    public void onStateChanged(GameManagerState newState) {

    }

    //
//    private class MediaRouterCallback extends MediaRouter.Callback {
//        private int mRouteCount = 0;
//
//        @Override
//        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
//            if (++mRouteCount == 1) {
//                // Show the button when a device is discovered.
//                mMediaRouteButton.setVisibility(View.VISIBLE);
//            }
//        }
//
//        @Override
//        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
//            if (--mRouteCount == 0) {
//                // Hide the button if there are no devices discovered.
//                mMediaRouteButton.setVisibility(View.GONE);
//            }
//        }
//
//        @Override
//        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
//            // Handle route selection.
//            CastDevice device = CastDevice.getFromBundle(info.getExtras());
//
//            AskNCastApplication.getInstance().setCastDevice(device);
//        }
//
//        @Override
//        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
//            AskNCastApplication.getInstance().setCastDevice(null);
//        }
//    }
}
