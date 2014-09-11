package com.apperall.gabe.tvguide.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by gabe on 11/09/14.
 */
public class TVGuideSyncService extends Service {


    private static final Object sSyncAdapterLock = new Object();
    private static TVGuideSyncAdapter sTVGuideSyncAdapter = null;


    @Override
    public void onCreate() {
        Log.d("TVGuideSyncService", "onCreate - TVGuideSyncService");
        synchronized (sSyncAdapterLock) {
            if(sTVGuideSyncAdapter==null) {
                sTVGuideSyncAdapter = new TVGuideSyncAdapter(getApplicationContext(),true);
            }

        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return sTVGuideSyncAdapter.getSyncAdapterBinder();
    }


}
