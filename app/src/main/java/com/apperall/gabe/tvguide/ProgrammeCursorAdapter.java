package com.apperall.gabe.tvguide;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by gabe on 30/08/14.
 */
public class ProgrammeCursorAdapter extends CursorAdapter {


    public static final String C_PROGRAMME_ID = "_id";
    public static final String C_PROGRAMME_TITLE = "title";
    public static final String C_PROGRAMME_START = "start";
    public static final String C_PROGRAMME_STOP = "stop";
    public static final String C_PROGRAMME_DESC = "desc";
    public static final String C_PROGRAMME_CHANNEL_ID = "channelId";
    public static final String C_PROGRAMME_CHANNEL_NAME = "channel";
    public static final String C_PROGRAMME_CATEGORY = "category";
    public static final String C_PROGRAMME_LENGTH = "length";
    public static final String C_PROGRAMME_SHOW = "show";
    public static final String C_PROGRAMME_URI = "uri";



    public static class ViewHolder {
        public final TextView titleView;
        public final TextView startDateView;
        public final TextView startView;
        public final TextView stopView;
        public final TextView channelView;
        public final TextView descView;

        public ViewHolder(View view) {
            titleView = (TextView)view.findViewById(R.id.textTitle);
            startDateView = (TextView)view.findViewById(R.id.textStartDate);
            startView = (TextView)view.findViewById(R.id.textStart);
            stopView = (TextView)view.findViewById(R.id.textStop);
            channelView = (TextView)view.findViewById(R.id.textChannel);
            descView = (TextView)view.findViewById(R.id.textDesc);

        }

    }

    public ProgrammeCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.programme_list_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        viewHolder.titleView.setText(cursor.getString(cursor.getColumnIndex(C_PROGRAMME_TITLE)));

        //viewHolder.startDateView.setText(cursor.getString(cursor.getColumnIndex(C_PROGRAMME_TITLE)));
        //viewHolder.startView.setText(cursor.getString(cursor.getColumnIndex(C_PROGRAMME_TITLE)));
        //viewHolder.stopView.setText(cursor.getString(cursor.getColumnIndex(C_PROGRAMME_TITLE)));
        viewHolder.channelView.setText(cursor.getString(cursor.getColumnIndex(C_PROGRAMME_CHANNEL_NAME)));
        viewHolder.descView.setText(cursor.getString(cursor.getColumnIndex(C_PROGRAMME_DESC)));





    }
}
