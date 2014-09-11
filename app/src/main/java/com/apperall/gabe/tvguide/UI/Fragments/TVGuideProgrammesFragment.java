package com.apperall.gabe.tvguide.UI.Fragments;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.apperall.gabe.tvguide.Constants;
import com.apperall.gabe.tvguide.Model.Programme;
import com.apperall.gabe.tvguide.Adapters.ProgrammeCursorAdapter;
import com.apperall.gabe.tvguide.R;
import com.apperall.gabe.tvguide.Contentproviders.TVGuideProvider;
import com.apperall.gabe.tvguide.UI.Activities.TVGuideMainActivity;
import com.apperall.gabe.tvguide.UpdateService;
import com.apperall.gabe.tvguide.Broadcastreceivers.WakefulUpdateReceiver;

import java.util.Date;

/**
 * A fragment representing a single ProgrammeSchedule detail screen.
 * This fragment is either contained in a {@link com.apperall.gabe.tvguide.UI.Activities.TVGuideMainActivity}
 * in two-pane mode (on tablets) or a {@link com.apperall.gabe.tvguide.UI.Activities.TVGuideSelectionsActivity}
 * on handsets.
 */
public class TVGuideProgrammesFragment extends ListFragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        TVGuideMainActivity.QueryArguments, SearchView.OnQueryTextListener{


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_SELECTION_TYPE = "selection_type";
    private static final String TAG = TVGuideProgrammesFragment.class.getSimpleName();
    //private ProgressDialog pd;
    private static final int DATA_LOADER = 1;
    private String sortKey = "start ASC";

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private ProgrammeCursorAdapter mAdapter;

    /**
     * The dummy content this fragment is presenting.
     */
    private String mItem;
    private String mSelectionType="Genres";

    private Menu mMenu;
    private ListView mListView;
    private SearchView mSearchView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TVGuideProgrammesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = (getArguments().getString(ARG_ITEM_ID));
        }
        if (getArguments().containsKey(ARG_SELECTION_TYPE)) {
            mSelectionType = (getArguments().getString(ARG_SELECTION_TYPE));
        }
        setHasOptionsMenu(true);

    }

    private Bundle makeLoaderBundle() {
        Bundle bundle = new Bundle();

        if (mSelectionType.equals(Constants.CHANNELS)) {
            bundle.putString("channel", mItem);
            Log.i(TAG, "Channel = "+mItem);
        } else if (mSelectionType.equals(Constants.GENRES)) {
            bundle.putString("category", mItem);
            Log.i(TAG, "Genre = "+mItem);
        } else if (mSelectionType.equals(Constants.QUERIES)) {
            bundle.putString("query", mItem);
            Log.i(TAG, "Query = "+mItem);
        } else if (mSelectionType.equals(Constants.NOW)) {
            bundle.putString(Constants.NOW, Constants.NOW);
        }
        return bundle;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(DATA_LOADER, makeLoaderBundle(), this);

        this.setListAdapter(mAdapter);

        this.getListView().setAdapter(mAdapter);

        getListView().setOnItemClickListener(TVGuideProgrammesFragment.this);
        setHasOptionsMenu(true);



//        mListView.setOnItemClickListener(this);
        //refreshProgrammes();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new ProgrammeCursorAdapter(getActivity(), null,0);


        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(DATA_LOADER, makeLoaderBundle(), this);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {

        Date now = new Date();
        Log.i(TAG, "onCreateLoader");
        String selection="";
        if (bundle.containsKey("channel")) {
            selection = "channel = '"+bundle.getString("channel")+"'";
        } else if (bundle.containsKey("category")) {
            selection = "category = '"+bundle.getString("category")+ "'";
        } else if (bundle.containsKey("query")) {
            selection = "(title LIKE '%"+bundle.getString("query")+"%' OR desc LIKE '%"+bundle.getString("query")+"%')";
        } else if (bundle.containsKey(Constants.NOW)) {
            selection = "start < "+now.getTime();
            sortKey = "channel ASC";
        }

        selection = selection + " AND stop > "+now.getTime();

        Log.i(TAG, "selection = "+selection);

        return new CursorLoader(
                getActivity(),
                TVGuideProvider.PROGRAMME_CONTENT_URI,
                null, //projection
                selection, // selection
                null, // selectionargs
                sortKey // sort
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.i(TAG, "onLoadFinished");
        mAdapter.swapCursor(cursor);
        ListView view = getListView();
        if (view!= null) {
            view.setSelection(0);
        } else {
            Log.i(TAG, "listview is null!");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.i(TAG, "onLoaderReset");
        mAdapter.swapCursor(null);

    }
    @Override
    public void onArgumentsChanged(Bundle bundle) {
        Log.i(TAG, "onArgumentsChanged");

        mItem = bundle.getString(ARG_ITEM_ID);
        mSelectionType = bundle.getString(ARG_SELECTION_TYPE);
        getActivity().getActionBar().setTitle(mItem);

        getLoaderManager().restartLoader(DATA_LOADER, makeLoaderBundle(), this);
        setSortOptionsVisibility();
        if (!(mSearchView==null) && !mSearchView.isIconified()) {

            mSearchView.setIconified(true);
            //MenuItem searchItem = mMenu.findItem(R.id.search);
            //searchItem.collapseActionView(searchItem.getActionView());
            mSearchView.onActionViewCollapsed();
        }


    }
    private void setSortOptionsVisibility() {
        if (mSelectionType.equals("Channels")) {
            if (mMenu!=null) {
                mMenu.findItem(R.id.action_sort_Channel).setVisible(false);
            }
        } else  if (mMenu!=null) {
            mMenu.findItem(R.id.action_sort_Channel).setVisible(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_menu,menu);
        mMenu = menu;

        setSortOptionsVisibility();
        mSearchView = (SearchView)menu.findItem(R.id.search).getActionView();
        mSearchView.setOnQueryTextListener(this);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Log.i("detailfragment", "onListItemClick()");
        // This never gets called!

     //((ProgrammeScheduleDetailActivity)getActivity()).setProgramme(programme);
/*

        ProgrammeDialogFragment dialogFragment = new ProgrammeDialogFragment();
        // Programme programme = mAdapter.getItem(position);
        //dialogFragment.setProgramme(programme);
        FragmentManager fm = getFragmentManager();

        dialogFragment.show(fm, "programmeDialog");
*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.action_sort_Channel:
                sortKey = "channel ASC";
                getLoaderManager().restartLoader(DATA_LOADER, makeLoaderBundle(), this);
                break;
            case R.id.action_sort_Time:
                //sortScheduleProgrammes("Start");
                sortKey = "start ASC";
                getLoaderManager().restartLoader(DATA_LOADER, makeLoaderBundle(), this);

                break;
            case R.id.action_sort_Title:
                //sortScheduleProgrammes("Title");
                sortKey = "title ASC";
                getLoaderManager().restartLoader(DATA_LOADER, makeLoaderBundle(), this);

                break;

            case R.id.action_refresh:
                //refreshProgrammes();

                // update the data now
                Intent nowIntent = new Intent(getActivity(), UpdateService.class);
                getActivity().startService(nowIntent);


                // and schedule half-daily updates
                Intent intent = new Intent(getActivity(), WakefulUpdateReceiver.class);
                AlarmManager am = (AlarmManager)getActivity().getSystemService(getActivity().ALARM_SERVICE);
                PendingIntent updateIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+
                //30*1000, updateIntent);
                am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_HALF_DAY, AlarmManager.INTERVAL_HALF_DAY, updateIntent );

                Toast.makeText(getActivity(), "update scheduled", Toast.LENGTH_SHORT);
                Log.i(TAG, "update scheduled");


                break;

        }

        //mAdapter.notifyDataSetChanged();

        return true;
    }





    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i(TAG, "onQueryTextSubmit: "+query);
        if (query.length()<3) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(),0);


        mSelectionType = Constants.QUERIES;
        mItem = query;

        getLoaderManager().restartLoader(DATA_LOADER, makeLoaderBundle(), this);

        getActivity().getActionBar().setTitle(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //Log.i(TAG, "onQueryTextChange");
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }
    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("PsDetailFragment", "onItemCLick");


        Cursor cursor = (Cursor)  mAdapter.getItem(position);

        Programme programme = new Programme();
        programme.setTitle(cursor.getString(cursor.getColumnIndex(ProgrammeCursorAdapter.C_PROGRAMME_TITLE)));


        Date date = new Date();

        date.setTime(cursor.getLong(cursor.getColumnIndex(ProgrammeCursorAdapter.C_PROGRAMME_START)));

        programme.setStart((Date)date.clone());
        date.setTime(cursor.getLong(cursor.getColumnIndex(ProgrammeCursorAdapter.C_PROGRAMME_STOP)));
        programme.setStop(date);

        programme.setChannel(cursor.getString(cursor.getColumnIndex(ProgrammeCursorAdapter.C_PROGRAMME_CHANNEL_NAME)));
        programme.setDesc(cursor.getString(cursor.getColumnIndex(ProgrammeCursorAdapter.C_PROGRAMME_DESC)));


        programme.setUriStr(cursor.getString(cursor.getColumnIndex(ProgrammeCursorAdapter.C_PROGRAMME_URI)));
        Log.i(TAG,"uri = "+programme.getUriStr());

        ProgrammeDialogFragment dialogFragment = new ProgrammeDialogFragment();
        // Programme programme = mAdapter.getItem(position);
        dialogFragment.setProgramme(programme);
        FragmentManager fm = getFragmentManager();
        //Dialog dialog =  dialogFragment.getDialog();
        dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE,0);

        // dialog.setTitle(programme.getTitle());
        dialogFragment.show(fm, "programmeDialog");

    }


}










