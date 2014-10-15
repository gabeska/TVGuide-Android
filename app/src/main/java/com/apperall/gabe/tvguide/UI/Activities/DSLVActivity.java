package com.apperall.gabe.tvguide.UI.Activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.apperall.gabe.tvguide.R;
import com.apperall.gabe.tvguide.UI.DSLV.DragSortItemViewCheckable;
import com.apperall.gabe.tvguide.UI.DSLV.DragSortListView;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import java.util.ArrayList;
import java.util.Iterator;

public class DSLVActivity extends ListActivity {


    ArrayAdapter<String> adapter;

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener()
    {
        @Override
        public void drop(int from, int to)
        {
            if (from != to)
            {
                DragSortListView list = (DragSortListView)getListView();
                String item = adapter.getItem(from);
                adapter.remove(item);
                adapter.insert(item, to);

                list.moveCheckState(from,to);

            }
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener()
    {
        @Override
        public void remove(int which)
        {
            adapter.remove(adapter.getItem(which));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dslv);

        //String[] names = new String[];
        ArrayList<String> names = new ArrayList<String>();
        final String APP_KEY = "1gcb7qc9cejlxml";
        final String APP_SECRET = "8627e2gpg6reb40";
        DbxAccountManager accountManager =  DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);

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


        adapter = new ArrayAdapter<String>(this,
                R.layout.list_item_checkable, R.id.text, names);

        setListAdapter(adapter);

        final DragSortListView list= (DragSortListView)getListView();
        LayoutInflater inflater = getLayoutInflater();
        TextView headerView = (TextView) inflater.inflate(R.layout.header_footer, null);
        headerView.setText("Channels");

        list.addHeaderView(headerView);
        //listView.setAdapter(adapter);
        list.setDropListener(onDrop);
        list.setRemoveListener(onRemove);

        Button goBtn = (Button)findViewById(R.id.testDslv);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i=0; i< list.getCount() ; i++) {
                  //  Log.i("dslv", list.getItem(i));
                    if (list.getChildAt(i) instanceof  DragSortItemViewCheckable) {
                        DragSortItemViewCheckable view = (DragSortItemViewCheckable) list.getChildAt(i);
                        CheckedTextView tv = (CheckedTextView) view.findViewById(R.id.text);
                        Log.i("dslv", "item:" + tv.getText() + " tv_checked: " + tv.isChecked() + " dragsortitem  checked: " + view.isChecked());
                    }
                }
                Log.i("dslv", "#checked: " +list.getCheckedItemCount());


            }
        });

    }



}
