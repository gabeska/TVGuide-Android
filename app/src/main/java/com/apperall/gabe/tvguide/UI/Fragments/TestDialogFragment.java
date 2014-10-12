package com.apperall.gabe.tvguide.UI.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.apperall.gabe.tvguide.R;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gabe on 10-10-14.
 */
public class TestDialogFragment extends DialogFragment {
    private String mProgrammeId;
    private String mTitle;
    private String mChannel;
    private Date mStart;
    private Date mStop;
    private String mDesc;

    private DbxAccountManager mAccountManager;
    private DbxDatastoreManager mDatastoreManager;
    private DbxDatastore mDataStore;

    private static final String APP_KEY = "1gcb7qc9cejlxml";
    private static final String APP_SECRET = "8627e2gpg6reb40";


    public static TestDialogFragment newInstance(String programmeId, String title,
                                                 String channel, Date start, Date stop,  String desc) {
        TestDialogFragment fragment = new TestDialogFragment();
        Bundle args = new Bundle();
       // args.putString("programmeId",programmeId);
        args.putString("title", title);
        args.putString("channel", channel);
        args.putSerializable("start", start);
        args.putSerializable("stop", stop);
        args.putString("desc",desc);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder timeDialog = new AlertDialog.Builder(getActivity());

        mAccountManager = DbxAccountManager.getInstance(getActivity().getApplicationContext(), APP_KEY, APP_SECRET);

        if (mAccountManager.hasLinkedAccount()) {
            try {
                mDatastoreManager = DbxDatastoreManager.forAccount(mAccountManager.getLinkedAccount());
            } catch (DbxException.Unauthorized e) {
                Log.i("dropboxerror", "Account was unlinked remotely");
            }
        }
        if (mDatastoreManager==null) {
            mDatastoreManager = DbxDatastoreManager.localManager(mAccountManager);
        }

        try {
            mDataStore = mDatastoreManager.openDefaultDatastore();
        } catch (DbxException e ) {
            Log.e("dropbox", "error opening datastore: "+e.getMessage());
        }


            timeDialog.setTitle("Programme Details");

        mStart = (Date)getArguments().getSerializable("start");
        mStop = (Date)getArguments().getSerializable("stop");
        mProgrammeId = getArguments().getString("programmeId");
        mTitle = getArguments().getString("title");
        mChannel = getArguments().getString("channel");
        mDesc = getArguments().getString("desc");

        View v = getActivity().getLayoutInflater().inflate(R.layout.testdialoglayout, null);
        timeDialog.setView(v).setPositiveButton(android.R.string.ok, null);

        TextView tvTitle = (TextView) v.findViewById(R.id.progDlgTvTitle);
        TextView tvDesc = (TextView) v.findViewById(R.id.progDlgTvDesc);

        TextView tvChannel = (TextView) v.findViewById(R.id.progDlgTvChannel);

        TextView tvStart = (TextView) v.findViewById(R.id.progDlgTvStart);
        TextView tvStop = (TextView) v.findViewById(R.id.progDlgTvStop);
        TextView tvStartDate = (TextView) v.findViewById(R.id.progDlgTvDate);

        tvTitle.setText(mTitle);
        tvDesc.setText(mDesc);
        tvDesc.setMovementMethod(new ScrollingMovementMethod());
        tvChannel.setText(mChannel);
        DateFormat format = new SimpleDateFormat("EE dd MMM");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        tvStart.setText(timeFormat.format(mStart));
        tvStop.setText(timeFormat.format(mStop));
        tvStartDate.setText(format.format(mStart));

        Button scheduleButton = (Button)v.findViewById(R.id.progDlgBtnSchedule);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, mStart)
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, mStop)
                        .putExtra(CalendarContract.Events.TITLE,mTitle)
                        .putExtra(CalendarContract.Events.DESCRIPTION, mDesc)
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, mChannel)
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
                startActivity(intent);
            }
        });

        Button hideButton = (Button)v.findViewById(R.id.progDlgBtnHide);
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("hide","onclick");

                try {

                    DbxTable programmesToHideTbl = mDataStore.getTable("programmesToHide");
                    DbxFields fields = new DbxFields();
                    fields.set("title", mTitle);
                    // DbxRecord programme = programmesToHideTbl. insert().set("title", mTitle);
                    if (!programmesToHideTbl.query(fields).hasResults()) { // don't insert if already in table

                        DbxRecord programme = programmesToHideTbl.insert(fields);

                        mDataStore.sync();
                        Log.i("propbox", " inserted: "+mTitle);

                    } else {
                        Log.i("propbox", "already in table, not inserted: "+mTitle);
                    }
                        Log.i("hide", "synced?");
                } catch (DbxException e) {
                    Log.e("dropbox","error storing programme: "+e.getMessage());
                }

            }
        });

        Button infoButton = (Button)v.findViewById(R.id.progDlgBtnMoreInfo);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info","onclick");

                try {
                    DbxTable programmesToHideTbl = mDataStore.getTable("programmesToHide");
                    DbxTable.QueryResult results = programmesToHideTbl.query();

                    //DbxRecord programmeRecord = results.iterator();
                    for (DbxRecord record: results.asList()) {
                        Log.i("programme", record.getString("title"));
                    }

                }  catch (DbxException e) {
                    Log.e("dropbox","error storing programme: "+e.getMessage());
                }
            }
        });
        //timeDialog.setMessage(getArguments().getString("bla"));
        return timeDialog.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("propbox", "onDestroyView");
       if (mDataStore!=null) mDataStore.close();

    }

/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.testdialoglayout, container, false);

   //     TextView text = (TextView)view.findViewById(R.id.progDlgTvTitle);
    //    text.setText("Poep in je hoofd!");
        return view;
    }
    */
}
