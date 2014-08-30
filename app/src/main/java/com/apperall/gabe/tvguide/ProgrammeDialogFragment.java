package com.apperall.gabe.tvguide;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by gabe on 29/08/14.
 */
public class ProgrammeDialogFragment extends DialogFragment implements View.OnClickListener {

    private Programme mProgramme;

    public Programme getProgramme() {
        return mProgramme;
    }

    public void setProgramme(Programme mProgramme) {
        this.mProgramme = mProgramme;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.programme_dialog_fragment, container, false);

        View moreinfoBtn = fragmentView.findViewById(R.id.progDlgBtnMoreInfo);
        View hideBtn = fragmentView.findViewById(R.id.progDlgBtnHide);
        View scheduleBtn = fragmentView.findViewById(R.id.progDlgBtnSchedule);

        moreinfoBtn.setOnClickListener(this);
        hideBtn.setOnClickListener(this);
        scheduleBtn.setOnClickListener(this);

        TextView tvTitle = (TextView) fragmentView.findViewById(R.id.progDlgTvTitle);
        TextView tvDesc = (TextView) fragmentView.findViewById(R.id.progDlgTvDesc);

        TextView tvChannel = (TextView) fragmentView.findViewById(R.id.progDlgTvChannel);

        TextView tvStart = (TextView) fragmentView.findViewById(R.id.progDlgTvStart);
        TextView tvStop = (TextView) fragmentView.findViewById(R.id.progDlgTvStop);
        TextView tvStartDate = (TextView) fragmentView.findViewById(R.id.progDlgTvDate);


        //mProgramme = ((ProgrammeScheduleDetailActivity)getActivity()).getProgramme();
        if (mProgramme!=null) {
            tvTitle.setText(mProgramme.getTitle());
            tvDesc.setText(mProgramme.getDesc());
            tvChannel.setText(mProgramme.getChannel());
            tvStart.setText(mProgramme.getStartTime());
            tvStop.setText(mProgramme.getStopTime());
            tvStartDate.setText(mProgramme.getStartDateStr());

        }


      return fragmentView;
    }

    @Override
    public void onClick(View v) {
        Log.i("dialog", "onClick");

        Calendar cal = Calendar.getInstance();

        cal.setTime(mProgramme.getStart());

        long start = cal.getTimeInMillis();

        cal.setTime(mProgramme.getStop());
        long stop = cal.getTimeInMillis();



        if (v.getId() == R.id.progDlgBtnSchedule) {
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, stop)
                    .putExtra(CalendarContract.Events.TITLE, mProgramme.getTitle())
                    .putExtra(CalendarContract.Events.DESCRIPTION, mProgramme.getDesc())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, mProgramme.getChannel())
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
            startActivity(intent);

        }




        dismiss();
    }
}
