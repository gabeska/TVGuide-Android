package com.apperall.gabe.tvguide;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A list fragment representing a list of ProgrammeSchedules. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ProgrammeScheduleDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ProgrammeScheduleListFragment extends ListFragment {
    private ArrayAdapter<String> mAdapter;
    private String mDisplaymode="Genres";
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
            "Nederland 1",
            "Nederland 2",
            "Nederland 3",
            "Cultura 24",
            "HollandDoc 24",
            "NostalgieNet",
            "RTL 4",
            "RTL 5",
            "RTL 7",
            "Discovery Channel",
            "Travel Channel"
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
    public ProgrammeScheduleListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(ProgrammeScheduleListFragment.class.getName(), "onCreate");

       /* final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);



        actionBar.addTab(actionBar.newTab().setText("Channels").setTabListener(this));

        actionBar.addTab(actionBar.newTab().setText("Genres").setTabListener(this));

        actionBar.addTab(actionBar.newTab().setText("Queries").setTabListener(this));
*/



        List<String>genres = new ArrayList<String>(Arrays.asList(genreStrs));
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                genres);

        setListAdapter(mAdapter);
    }

    public void setDisplaymode(String displaymode) {
        mDisplaymode = displaymode;

        if (mDisplaymode.equals("Genres")) {
            mAdapter.clear();
            mAdapter.addAll(genreStrs);
            mAdapter.notifyDataSetChanged();
        } else if (mDisplaymode.equals("Channels")) {
            mAdapter.clear();
            mAdapter.addAll(channelStrs);
            mAdapter.notifyDataSetChanged();
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
