package com.apperall.gabe.tvguide;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Date;

/**
 * A fragment representing a single ProgrammeSchedule detail screen.
 * This fragment is either contained in a {@link ProgrammeScheduleListActivity}
 * in two-pane mode (on tablets) or a {@link ProgrammeScheduleDetailActivity}
 * on handsets.
 */
public class ProgrammeScheduleDetailFragment extends ListFragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        ProgrammeScheduleListActivity.QueryArguments{


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_SELECTION_TYPE = "selection_type";
    private static final String TAG = ProgrammeScheduleDetailFragment.class.getName();
    //private ProgressDialog pd;
    private static final int DATA_LOADER = 1;
    private String sortKey = "title ASC";

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProgrammeScheduleDetailFragment() {
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

        if (mSelectionType.equals("Channels")) {
            bundle.putString("channel", mItem);
            Log.i(TAG, "Channel = "+mItem);
        } else if (mSelectionType.equals("Genres")) {
            bundle.putString("category", mItem);
            Log.i(TAG, "Genre = "+mItem);
        }
        return bundle;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(DATA_LOADER, makeLoaderBundle(), this);

        this.setListAdapter(mAdapter);

        this.getListView().setAdapter(mAdapter);

        getListView().setOnItemClickListener(ProgrammeScheduleDetailFragment.this);
//        mListView.setOnItemClickListener(this);
        //refreshProgrammes();

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
        Log.i(TAG, "onCreateLoader");
        String selection="";
        if (bundle.containsKey("channel")) {
            selection = "channel = '"+bundle.getString("channel")+"'";
        } else if (bundle.containsKey("category")) {
            selection = "category = '"+bundle.getString("category")+ "'";
        }

        //TODO: don't include programmes that have already finished


        return new CursorLoader(
                getActivity(),
                TVGuideProvider.CONTENT_URI,
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
        getActivity().setTitle(mSelectionType);
        getLoaderManager().restartLoader(DATA_LOADER, makeLoaderBundle(), this);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_menu,menu);
        mMenu = menu;


        if (mSelectionType.equals("Channels")) {
            if (mMenu!=null) {
                mMenu.findItem(R.id.action_sort_Channel).setVisible(false);
            }
        } else  if (mMenu!=null) {
            mMenu.findItem(R.id.action_sort_Channel).setVisible(true);
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Log.i("detailfragment", "onListItemClick()");
     //((ProgrammeScheduleDetailActivity)getActivity()).setProgramme(programme);


        ProgrammeDialogFragment dialogFragment = new ProgrammeDialogFragment();
        // Programme programme = mAdapter.getItem(position);
        //dialogFragment.setProgramme(programme);
        FragmentManager fm = getFragmentManager();

        dialogFragment.show(fm, "programmeDialog");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                Intent intent = new Intent(getActivity(), UpdateService.class);
                getActivity().startService(intent);

                break;

        }

        mAdapter.notifyDataSetChanged();

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










