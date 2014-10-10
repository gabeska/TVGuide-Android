package com.apperall.gabe.tvguide.UI.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.apperall.gabe.tvguide.R;

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

        //timeDialog.setMessage(getArguments().getString("bla"));
        return timeDialog.create();
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
