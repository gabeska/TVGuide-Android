package com.apperall.gabe.tvguide.UI.Activities;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.apperall.gabe.tvguide.Contentproviders.TVGuideProvider;
import com.apperall.gabe.tvguide.Model.Channel;
import com.apperall.gabe.tvguide.R;
import com.apperall.gabe.tvguide.UI.DSLV.DragSortListView;

import java.util.ArrayList;

public class DSLVActivity extends ListActivity {


    ArrayAdapter<String> adapter;

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        DragSortListView list = getListView();
                        String item = adapter.getItem(from);
                        adapter.remove(item);
                        adapter.insert(item, to);
                        list.moveCheckState(from, to);
                    }
                }
            };




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dslv);

        //String[] names = new String[];
        ArrayList<String> names = new ArrayList<String>();
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Cursor channelCursor =  resolver.query(TVGuideProvider.CHANNEL_CONTENT_URI, null, null, null, null);

       if (channelCursor.moveToFirst()) {
           do {

               // testje
               names.add(channelCursor.getString(channelCursor.getColumnIndex(Channel.C_CHANNEL_NAME)));


           } while (channelCursor.moveToNext());
       }
        if(channelCursor!=null) channelCursor.close();



        //DbxAccountManager accountManager =  DbxAccountManager.getInstance(getApplicationContext(),
         //       TVGuideApplication. APP_KEY,TVGuideApplication.APP_SECRET);
/*
        DbxDatastore datastore=null;
        try {
            datastore = DbxDatastoreManager.localManager(accountManager).openDefaultDatastore();
            DbxTable channelsTable = datastore.getTable("channels");
            DbxTable.QueryResult channels = channelsTable.query();
            Iterator<DbxRecord> recordIterator =channels.iterator();

            while (recordIterator.hasNext()) {
                names.add(recordIterator.next().getString("name"));
            }



        } catch (DbxException e) {
            Log.e("dslv","error getting channels: "+e.getMessage());
        } finally {
            if (datastore!=null) datastore.close();
        }
*/

        adapter = new ArrayAdapter<String>(this,
                R.layout.list_item_checkable, R.id.text, names);

        setListAdapter(adapter);

        final DragSortListView list= getListView();
        LayoutInflater inflater = getLayoutInflater();
       // TextView headerView = (TextView) inflater.inflate(R.layout.header_footer, null);
       // headerView.setText("Channels");

        //list.addHeaderView(headerView);
        //listView.setAdapter(adapter);
        list.setDropListener(onDrop);

        Button goBtn = (Button)findViewById(R.id.testDslv);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SparseBooleanArray checkedItemPositions = list.getCheckedItemPositions();
                int key=0;
                for (int i=0; i < checkedItemPositions.size();  i++) {

                    if (checkedItemPositions.valueAt(i)) {
                    key = checkedItemPositions.keyAt(i);

                        String item = adapter.getItem(key);
                        Log.i("dslv", item + " is at pos: "+(key));
                    }
                }

                Log.i("dslv", "#checked: " +list.getCheckedItemCount());

            }
        });

    }

    @Override
    public DragSortListView getListView() {
        return (DragSortListView) super.getListView();
    }

}
