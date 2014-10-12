package com.apperall.gabe.tvguide.UI.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.apperall.gabe.tvguide.Constants;
import com.apperall.gabe.tvguide.Model.Query;
import com.apperall.gabe.tvguide.R;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A list fragment representing a list of ProgrammeSchedules. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link TVGuideProgrammesFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class TVGuideSelectionsFragment extends ListFragment {
    public static final String TAG = TVGuideSelectionsFragment.class.getSimpleName();
    private DbxAccountManager mAccountManager;
    private DbxDatastoreManager mDatastoreManager;
    private DbxDatastore mDataStore;

    private static final String APP_KEY = "1gcb7qc9cejlxml";
    private static final String APP_SECRET = "8627e2gpg6reb40";
    private ArrayAdapter<String> mAdapter;
    private String mDisplaymode= Constants.GENRES;
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private static final String[]genreStrs = {
            "Animals",
            "Arts/Culture",
            "Children",
            "Comedy",
            "Drama",
            "Educational",
            "Entertainment",
            "Factual",
            "Film",
            "Lifestyle",
            "Music",
            "News",
            "Religion",
            "Science/Nature",
            "Sports",
            "Talk",
            "Unknown"
    };

    private static final String[]channelStrs = {
            "BBC 1",
            "BBC 2",
            "BBC 3",
            "BBC 4",
            "NPO 1",
            "NPO 2",
            "NPO 3",
            "NPO Cultura",
            "NPO Doc",
            "NostalgieNet",
            "RTL 4",
            "RTL 5",
            "RTL 7",
            "Discovery Channel",
            "Travel Channel",
            "RTV Rijnmond"
    };

    private static final String[]queryStrs = {
            "Rotterdam",
            "history",
            "clarkson",
            "london",
            "chicago",
            "fry",
            "travel",
            "klokhuis",
            "rail"

    };


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TVGuideSelectionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TVGuideSelectionsFragment.class.getName(), "onCreate");
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

        List<String>genres = new ArrayList<String>(Arrays.asList(genreStrs));
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                genres);

        setListAdapter(mAdapter);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = mAdapter.getItem(position);

                Log.i(TAG, "onItemLongClick");

                if (!mDisplaymode.equals(Constants.QUERIES)) {
                    return false;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage("Delete this query?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ParseQuery<Query> query = Query.getQuery();
                                query.fromLocalDatastore();
                                query.whereEqualTo("keyword", item);
                                try {
                                    Query queryItem = query.getFirst();
                                    queryItem.deleteEventually();

                                    queryItem.unpin();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                mAdapter.remove(item);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
                return true;
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDataStore.close();

    }

    public void setDisplaymode(String displaymode) {
        Log.i(TAG, "setDisplaymode: "+displaymode);
        mDisplaymode = displaymode;
        getListView().setLongClickable(false);

        if (mDisplaymode.equals(Constants.GENRES)) {

            mAdapter.clear();
            mAdapter.addAll(genreStrs);
            mAdapter.notifyDataSetChanged();
        } else if (mDisplaymode.equals(Constants.CHANNELS)) {
            mAdapter.clear();
            mAdapter.addAll(channelStrs);
            mAdapter.notifyDataSetChanged();
        } else if (mDisplaymode.equals(Constants.QUERIES)) {
            // TODO: for now, enable deleting queries by a long click listener
            this.getListView().setLongClickable(true);

            mAdapter.clear();
            /*
            ParseQuery<Query> query = Query.getQuery();
            query.fromLocalDatastore();

            query.findInBackground(new FindCallback<Query>() {
              @Override
              public void done(List<Query> queries, ParseException e) {

                  if (e==null) {
                      for (Query item : queries) {
                          mAdapter.add(item.getKeyword());
                      }
                      mAdapter.notifyDataSetChanged();
                  } else {
                      Log.d(TAG, "parse error: "+e.getMessage());
                  }
              }
          });
*/
            try {



            DbxTable queriesTable = mDataStore.getTable("storedQueries");
            DbxTable.QueryResult results = queriesTable.query();

            List<DbxRecord> resultsList = results.asList();
            Collections.sort(resultsList, new Comparator<DbxRecord>() {
                @Override
                public int compare(DbxRecord lhs, DbxRecord rhs) {
                    return (lhs.getString("query").compareTo(rhs.getString("query")));
                }
            });

                Iterator<DbxRecord> resultsIterator = results.iterator();
            while (resultsIterator.hasNext()) {
                mAdapter.add(resultsIterator.next().getString("query"));
            }


            //mAdapter.addAll(queryStrs);
            mAdapter.notifyDataSetChanged();
        } catch (DbxException e) {
                Log.e(TAG, "error retrieving stored queries: "+e.getMessage());
            }
        } else if (mDisplaymode.equals(Constants.NOW)) {
            mCallbacks.onItemSelected(Constants.NOW);
        }

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       inflater.inflate(R.menu.queries_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        if (item.getItemId()==R.id.action_add_query) {
            Log.i(TAG, "addQuery");
            displayAddQueryDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Log.i("ScheduleListFragment", "onListItemClick");


        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected((String)getListAdapter().getItem(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }
    private void displayAddQueryDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View promptView = inflater.inflate(R.layout.addquerydialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.userInput);


        builder
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        DbxFields queryFields = new DbxFields();
                        queryFields.set("query", input.getText().toString());
                        // todo: query-bouw-dialog maken
                        // todo: query aparte naam en selectie geven (als uitgeslapen)
                        DbxTable queriesTable = mDataStore.getTable("storedQueries");

                        try {
                            if (queriesTable.query(queryFields).hasResults()) {
                                // ?
                                Log.i(TAG,"keyword already in searches, not inserted");
                            } else {
                                queriesTable.insert(queryFields);
                                mAdapter.add( input.getText().toString());
                                mDataStore.sync();
                                mAdapter.notifyDataSetChanged();
                            }



                        } catch (DbxException e) {

                        }
  /*
                        Query query = new Query();
                        query.setUuidString();
                        final String item = input.getText().toString();
                        query.setKeyword(item);
                        query.pinInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    mAdapter.add(item);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        query.saveEventually();
*/
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();


    }
    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
