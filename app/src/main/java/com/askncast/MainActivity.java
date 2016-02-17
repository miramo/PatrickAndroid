package com.askncast;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;

public class MainActivity extends AppCompatActivity {
    private MediaRouter mediaRouter;
    private MediaRouteSelector mediaRouteSelector;

    /*
    Init media router callback (triggered when selecting a device from chromecast icon)
     */
    private final MediaRouter.Callback mediaRouterCallback = new MediaRouter.Callback()
    {
        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route)
        {
            CastDevice device = CastDevice.getFromBundle(route.getExtras());
            //setSelectedDevice(device);
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route)
        {
            //setSelectedDevice(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mediaRouter = MediaRouter.getInstance(getApplicationContext());
        mediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(getString(R.string.APP_ID))).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        // Link icon and chromecast target to our Ask'n'cast receiver app
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mediaRouteSelector);
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mediaRouter.addCallback(mediaRouteSelector, mediaRouterCallback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    @Override
    protected void onStop()
    {
        //setSelectedDevice(null);
        mediaRouter.removeCallback(mediaRouterCallback);
        super.onStop();
    }
}
