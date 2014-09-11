package com.apperall.gabe.tvguide;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.apperall.gabe.tvguide.Contentproviders.TVGuideProvider;
import com.apperall.gabe.tvguide.Model.Programme;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Vector;

public class UpdateService extends IntentService {

    static final String TAG = "UpdateService";


    public UpdateService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent for action "+intent.getAction());

        TVGuideDataSource dataSource = ((TVGuideApplication)getApplicationContext()).getDataSource();
        // TODO: check if network available & home wifi
        if (!isNetworkAvailable()) {
            Log.i(TAG, "network not available, can't update");
            return;
        }

        JSONArray programmeArray = dataSource.refreshProgrammes();

        Date now = new Date();
        //ContentValues[] values = new ContentValues[]{};

        ContentResolver resolver = getContentResolver();
        resolver.delete(TVGuideProvider.PROGRAMME_CONTENT_URI, null, null);
        Vector<ContentValues> cvVector = new Vector<ContentValues>(programmeArray.length());

        for (int i=0; i<programmeArray.length(); i++) {
            try {
                JSONObject jsonObject = programmeArray.getJSONObject(i);
                if (jsonObject.getBoolean("show")==true) {

                    Programme programme = new Programme();

                    programme.setFromJSON(jsonObject);
/*
                    programme.setCategory(jsonObject.getString("category"));
                    programme.setTitle(jsonObject.getString("title"));
                    programme.setStart(jsonObject.getString("start"));
                    programme.setStop(jsonObject.getString("stop"));
                    programme.set_id(jsonObject.getString("_id"));
                    programme.setDesc(jsonObject.getString("desc"));
                    programme.setLength(jsonObject.getInt("length"));
                    programme.setShow(jsonObject.getBoolean("show"));
                    programme.setChannel(jsonObject.getString("channel"));
                    //programme.setUriStr(jsonObject.getString("uri"));
                    programme.setSource(jsonObject.getString("source"));

*/
                    if (programme.getStop().after(now)) {

                        //programmes.add(programme);
                        //Log.i(TAG, programme.getTitle());


                       // resolver.insert(TVGuideProvider.CONTENT_URI, programme.asContentValues());
                        cvVector.add(programme.asContentValues());
                    }
                }
            } catch (JSONException e) {
                    Log.e(TAG, "json error: "+e.getMessage());
            }
        }
        if (cvVector.size()>0) {
            ContentValues[] cvArray = new ContentValues[cvVector.size()];
            cvVector.toArray(cvArray);
            int rowsInserted = resolver.bulkInsert(TVGuideProvider.PROGRAMME_CONTENT_URI, cvArray);
            Log.v(TAG, "inserted "+rowsInserted+" programmes into db");
        }
        //dataSource.deleteProgrammes();
        // insert new programmes in the db


        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        Log.i(TAG, "update complete");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected() ) {

            NetworkInfo wifiInfo = manager.getNetworkInfo(manager.TYPE_WIFI);
            if (wifiInfo.isConnected()) {

                isAvailable = true;
            } else {
                Log.d(TAG, "not on wifi, update cancelled");
            }
        }

        return isAvailable;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");

    }
}
