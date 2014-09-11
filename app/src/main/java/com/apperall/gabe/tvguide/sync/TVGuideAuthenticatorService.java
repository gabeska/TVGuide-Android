package com.apperall.gabe.tvguide.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by gabe on 11/09/14.
 */
public class TVGuideAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private TVGuideAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new TVGuideAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}