package com.apperall.gabe.tvguide;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

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


        JSONArray programmeArray = dataSource.refreshProgrammes();

        Date now = new Date();
        //ContentValues[] values = new ContentValues[]{};

        ContentResolver resolver = getContentResolver();
        resolver.delete(TVGuideProvider.CONTENT_URI, null, null);


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


                        resolver.insert(TVGuideProvider.CONTENT_URI, programme.asContentValues());

                    }
                }
            } catch (JSONException e) {
                    Log.e(TAG, "json error: "+e.getMessage());
            }
        }

        //dataSource.deleteProgrammes();
        // insert new programmes in the db




        Log.i(TAG, "update complete");
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
