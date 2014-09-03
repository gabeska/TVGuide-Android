package com.apperall.gabe.tvguide.UI.Activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.apperall.gabe.tvguide.Constants;
import com.apperall.gabe.tvguide.R;
import com.apperall.gabe.tvguide.UI.Fragments.ProgrammeScheduleDetailFragment;
import com.apperall.gabe.tvguide.UI.Fragments.ProgrammeScheduleListFragment;


/**
 * An activity representing a list of ProgrammeSchedules. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ProgrammeScheduleDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link com.apperall.gabe.tvguide.UI.Fragments.ProgrammeScheduleListFragment} and the item details
 * (if present) is a {@link com.apperall.gabe.tvguide.UI.Fragments.ProgrammeScheduleDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link com.apperall.gabe.tvguide.UI.Fragments.ProgrammeScheduleListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ProgrammeScheduleListActivity extends Activity
        implements ProgrammeScheduleListFragment.Callbacks, ActionBar.TabListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private String mSelectionType="Channels";
    private QueryArguments mCallbacks;
    private final static int NUM_TABS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmeschedule_list);
        if (savedInstanceState!= null) {
            if (savedInstanceState.containsKey("selectionType"))
                mSelectionType = savedInstanceState.getString("selectionType");
        }
        Log.i(ProgrammeScheduleListActivity.class.getName(), "onCreate, selection type = "+mSelectionType);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayShowTitleEnabled(false);


        boolean selected = false;


        if (mSelectionType.equals(Constants.CHANNELS)) {
            selected= true;
        }

        Tab tab = actionBar.newTab().setText(Constants.CHANNELS).setTabListener(this);
        actionBar.addTab(tab,0,selected);
        selected = false;
        if (mSelectionType.equals(Constants.GENRES)) {
            selected= true;
        }
        tab = actionBar.newTab().setText(Constants.GENRES).setTabListener(this);
        actionBar.addTab(tab,1,selected);

        selected = false;
        if (mSelectionType.equals(Constants.QUERIES)) {
            selected= true;

        }

        tab = actionBar.newTab().setText(Constants.QUERIES).setTabListener(this);
        actionBar.addTab(tab,2,selected);

        selected = false;
        if (mSelectionType.equals(Constants.NOW)) {
            selected= true;

        }

        tab = actionBar.newTab().setText(Constants.NOW).setTabListener(this);
        actionBar.addTab(tab,3,selected);


        if (findViewById(R.id.programmeschedule_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ProgrammeScheduleListFragment) getFragmentManager()
                    .findFragmentById(R.id.programmeschedule_list))
                    .setActivateOnItemClick(true);


            Bundle arguments = new Bundle();
            arguments.putString(ProgrammeScheduleDetailFragment.ARG_ITEM_ID, "BBC 1");
            arguments.putString(ProgrammeScheduleDetailFragment.ARG_SELECTION_TYPE, Constants.CHANNELS);
            ProgrammeScheduleDetailFragment fragment = new ProgrammeScheduleDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.programmeschedule_detail_container, fragment)
                    .commit();

            mCallbacks = (QueryArguments) fragment;
        }


        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("selectionType", mSelectionType);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Log.i("ScheduleListActivity, onTabSelected", tab.getText().toString());
        String tabText = tab.getText().toString();


        FragmentManager fm = getFragmentManager();
        ProgrammeScheduleListFragment listFragment = (ProgrammeScheduleListFragment) fm.findFragmentById(R.id.programmeschedule_list);
        boolean shouldShowFragment = true;
        if (tabText.equals(Constants.GENRES)) {
            mSelectionType = Constants.GENRES;
        } else if (tabText.equals(Constants.CHANNELS)) {
            mSelectionType = Constants.CHANNELS;
        } else if (tabText.equals(Constants.QUERIES)) {
            mSelectionType = Constants.QUERIES;
        } else if (tabText.equals(Constants.NOW)) {
            mSelectionType = Constants.NOW;
            shouldShowFragment = false;
        }

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        if (listFragment.isHidden() && shouldShowFragment==true) {
            fragmentTransaction.show(listFragment);
        } else if (!listFragment.isHidden() && !shouldShowFragment) {
            fragmentTransaction.hide(listFragment);
        }

        fragmentTransaction.commit();


        if (listFragment!= null) {
            listFragment.setDisplaymode(mSelectionType);
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }
    public interface QueryArguments {
        /**
         * Callback for when an item has been selected.
         */
        public void onArgumentsChanged(Bundle bundle);
    }


   // public void onArgumentsChanged(Bundle bundle) {
   //
   // }

    /**
     * Callback method from {@link ProgrammeScheduleListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        Log.i("ScheduleListActivity", "onItemSelected");
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ProgrammeScheduleDetailFragment.ARG_ITEM_ID, id);
            arguments.putString(ProgrammeScheduleDetailFragment.ARG_SELECTION_TYPE, mSelectionType);
            //ProgrammeScheduleDetailFragment fragment = new ProgrammeScheduleDetailFragment();
            //getFragmentManager().beginTransaction()
            //        .replace(R.id.programmeschedule_detail_container, fragment)
            //        .commit();

           // ProgrammeScheduleDetailFragment fragment = (ProgrammeScheduleDetailFragment)getFragmentManager().findFragmentById(R.id.programmeschedule_detail_container);
           // fragment.setArguments(arguments);
            if (mCallbacks!= null) {
                mCallbacks.onArgumentsChanged(arguments);
            }

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ProgrammeScheduleDetailActivity.class);
            detailIntent.putExtra(ProgrammeScheduleDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra(ProgrammeScheduleDetailFragment.ARG_SELECTION_TYPE, mSelectionType);
            startActivity(detailIntent);
        }
    }




}
