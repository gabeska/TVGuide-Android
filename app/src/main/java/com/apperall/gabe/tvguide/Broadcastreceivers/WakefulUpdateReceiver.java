package com.apperall.gabe.tvguide.Broadcastreceivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.apperall.gabe.tvguide.UpdateService;

public class WakefulUpdateReceiver extends WakefulBroadcastReceiver {
    public WakefulUpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("wakefulupdatereceiver", "onReceive");
        Intent service = new Intent(context, UpdateService.class);
        startWakefulService(context, service);
    }
}
