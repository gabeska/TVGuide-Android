package com.apperall.gabe.tvguide;

import android.app.Application;

import com.apperall.gabe.tvguide.Model.Query;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by gabe on 29/08/14.
 */
public class TVGuideApplication extends Application {
    public static final String TAG = TVGuideApplication.class.getName();

    private TVGuideDataSource dataSource;

    @Override
    public void onCreate() {
        super.onCreate();


        ParseObject.registerSubclass(Query.class);
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(this, "f2mPir2KTxZTEFYKWMEbO6KWc6cJE7pzxR5hcNdw", "qTXEgIj2dxFpQHpPuOe0JQVnuT9Rgvr8NCvqSuBg");
        ParseFacebookUtils.initialize("698958980179850");

        ParseUser.enableAutomaticUser();
//        ParseACL defaultACL = new ParseACL();
//        ParseACL.setDefaultACL(defaultACL,true);

//        ParseAnalytics analytics;


       // ParseObject testObject = new ParseObject("TestObject");
       // testObject.put("foo", "bar");
       // testObject.saveInBackground();
    }

    public synchronized TVGuideDataSource getDataSource() {
        if (dataSource==null) {
            dataSource = new TVGuideDataSource(getApplicationContext());
        }


        return dataSource;
    }



}
