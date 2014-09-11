package com.apperall.gabe.tvguide.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.apperall.gabe.tvguide.Contentproviders.TVGuideProvider;
import com.apperall.gabe.tvguide.Model.Channel;
import com.apperall.gabe.tvguide.Model.Programme;
import com.apperall.gabe.tvguide.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by gabe on 11/09/14.
 */
public class TVGuideSyncAdapter extends AbstractThreadedSyncAdapter{
    private static final String TAG = "TVGuideSyncAdapter";

    public static final int SYNC_INTERVAL = 24*60*60; // 24 uur
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/2;



    public TVGuideSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync");

        //updateChannels(); // TODO: nadenken over waar/wanneer dit moet gebeuren

        JSONArray programmeArray = refreshProgrammes();
        if (programmeArray != null && programmeArray.length()>0) {


            Date now = new Date();

            ContentResolver resolver = getContext().getContentResolver();
            resolver.delete(TVGuideProvider.PROGRAMME_CONTENT_URI, null, null);
            Vector<ContentValues> cvVector = new Vector<ContentValues>(programmeArray.length());

            for (int i = 0; i < programmeArray.length(); i++) {
                try {
                    JSONObject jsonObject = programmeArray.getJSONObject(i);
                    if (jsonObject.getBoolean("show") == true) {

                        Programme programme = new Programme();

                        programme.setFromJSON(jsonObject);

                        if (programme.getStop().after(now)) {

                            cvVector.add(programme.asContentValues());
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json error: " + e.getMessage());
                }
            }
            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                int rowsInserted = resolver.bulkInsert(TVGuideProvider.PROGRAMME_CONTENT_URI, cvArray);
                Log.v(TAG, "inserted " + rowsInserted + " programmes into db");
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong("lastSync",new Date().getTime());
            edit.commit();


            Log.i(TAG, "onPerformSync: update complete");
        } else {
            Log.e(TAG, "onPerformSync: update failed");
        }
    }


    public void updateChannels() {
        Log.i(TAG, "updateChannels");
        ParseQuery<ParseObject>query = ParseQuery.getQuery("Channel");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // TODO: hierover nadenken!

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                Log.i(TAG, "updateChannels:done");

                if (e==null) {
                    ContentResolver resolver = getContext().getContentResolver();
                    resolver.delete(TVGuideProvider.CHANNEL_CONTENT_URI, null, null);
                    int channelsInserted = 0;
                    for (ParseObject po:parseObjects) {
                        channelsInserted++;
                        Channel ch = Channel.fromParseObject(po);
                        resolver.insert(TVGuideProvider.CHANNEL_CONTENT_URI, ch.asContentValues());

                        Log.i(TAG, ch.toString());
                    }
                    Log.i(TAG, "inserted "+channelsInserted+" channels");
                } else {
                    Log.e(TAG, "error updating channels");

                }
            }
        });


    }


    public static JSONArray refreshProgrammes() {
        Log.i(TAG,"refreshProgrammes");
        try {

            URL programmesURL = new URL("http://192.168.0.42:4000/programmes");

            HttpURLConnection connection = (HttpURLConnection) programmesURL.openConnection();

            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.i(TAG, "Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder builder = new StringBuilder();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = reader.readLine())!=null) {
                    builder.append(line);
                }

                String responseData = builder.toString();
                //Log.v(TAG, responseData);
                //JSONObject jsonObject = new JSONObject(responseData);
                JSONArray programmeArray =  new JSONArray(responseData);
                reader.close();
                return programmeArray;

            } else {
                Log.i(TAG, "unsuccessful HTTP response: "+responseCode);
            }

            connection.disconnect();

        } catch (MalformedURLException e) {
            Log.e("detail", "Exception: "+e.getMessage());
        } catch (IOException e) {
            Log.e("detail", "Exception: "+e.getMessage());

        } catch (Exception e) {
            Log.e("detail", "Exception: "+e.getMessage());


        }

        return null;
    }

    /**
     * Helper method to get the fake accounts to be used with SyncAdapter
     */
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            /* If you don't set android:syncable="true" in your <provider> element in the manifest
             * then call context.setIsSyncable(account, AUTHORITY) here
             */
            onAccountCreated(newAccount,context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        TVGuideSyncAdapter.configurePeriodicSync(context,SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            // enable inexact timers for sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
