package com.apperall.gabe.tvguide;

import android.app.Application;

/**
 * Created by gabe on 29/08/14.
 */
public class TVGuideApplication extends Application {
    public static final String TAG = TVGuideApplication.class.getName();

    private TVGuideDataSource dataSource;


    public synchronized TVGuideDataSource getDataSource() {
        if (dataSource==null) {
            dataSource = new TVGuideDataSource(getApplicationContext());
        }


        return dataSource;
    }



}
