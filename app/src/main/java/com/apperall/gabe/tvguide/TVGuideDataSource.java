package com.apperall.gabe.tvguide;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gabe on 29/08/14.
 */
public class TVGuideDataSource {
    private SQLiteDatabase database;
    private static final String TAG = TVGuideDataSource.class.getName();

    private Context mContext;


    public TVGuideDataSource(Context context) {
        mContext = context;
    }


    public static JSONArray refreshProgrammes() {

        try {

        URL programmesURL = new URL("http://192.168.0.42:4000/programmes");

        HttpURLConnection connection = (HttpURLConnection) programmesURL.openConnection();

        connection.connect();

        int responseCode = connection.getResponseCode();
        Log.i(TAG, "Code: "+responseCode);

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

            return programmeArray;
            /*


            Date now = new Date();

            for (int i=0; i<programmeArray.length(); i++) {

                JSONObject jsonObject = programmeArray.getJSONObject(i);
                if (jsonObject.getBoolean("show")==true) {

                    Programme programme = new Programme();

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


                    if(programme.getStop().after(now)) {

                        programmes.add(programme);
                    }
                }
            }
            */

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






}
