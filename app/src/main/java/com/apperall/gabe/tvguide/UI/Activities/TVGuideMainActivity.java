package com.apperall.gabe.tvguide.UI.Activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.apperall.gabe.tvguide.Constants;
import com.apperall.gabe.tvguide.R;
import com.apperall.gabe.tvguide.UI.Fragments.TVGuideProgrammesFragment;
import com.apperall.gabe.tvguide.UI.Fragments.TVGuideSelectionsFragment;
import com.apperall.gabe.tvguide.sync.TVGuideSyncAdapter;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


/**
 * An activity representing a list of ProgrammeSchedules. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TVGuideSelectionsActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link com.apperall.gabe.tvguide.UI.Fragments.TVGuideSelectionsFragment} and the item details
 * (if present) is a {@link com.apperall.gabe.tvguide.UI.Fragments.TVGuideProgrammesFragment}.
 * <p>
 * This activity also implements the required
 * {@link com.apperall.gabe.tvguide.UI.Fragments.TVGuideSelectionsFragment.Callbacks} interface
 * to listen for item selections.
 */
public class TVGuideMainActivity extends Activity
        implements TVGuideSelectionsFragment.Callbacks, ActionBar.TabListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private String mSelectionType="Channels";
    private QueryArguments mCallbacks;
    private final static int NUM_TABS = 3;
    private final static String TAG = TVGuideMainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmeschedule_list);
        if (savedInstanceState!= null) {
            if (savedInstanceState.containsKey("selectionType"))
                mSelectionType = savedInstanceState.getString("selectionType");
        }
        Log.i(TVGuideMainActivity.class.getName(), "onCreate, selection type = "+mSelectionType);
       //actionBar.setDisplayShowTitleEnabled(false);

        initTabs();


        if (findViewById(R.id.programmeschedule_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((TVGuideSelectionsFragment) getFragmentManager()
                    .findFragmentById(R.id.programmeschedule_list))
                    .setActivateOnItemClick(true);


            Bundle arguments = new Bundle();
            arguments.putString(TVGuideProgrammesFragment.ARG_ITEM_ID, "BBC 1");
            arguments.putString(TVGuideProgrammesFragment.ARG_SELECTION_TYPE, Constants.CHANNELS);
            TVGuideProgrammesFragment fragment = new TVGuideProgrammesFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.programmeschedule_detail_container, fragment)
                    .commit();

            mCallbacks = (QueryArguments) fragment;
        }


        TVGuideSyncAdapter.initializeSyncAdapter(this);
    }
    private void initTabs() {
        boolean selected = false;
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


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

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("selectionType", mSelectionType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_login:
            {
                ParseFacebookUtils.logIn(this, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser == null) {
                            Log.d(TAG, "user cancelled fb login");
                        } else if (parseUser.isNew()) {
                            Log.d(TAG, "user signed up and logged in through fb");
                        } else {
                            Log.d(TAG, "user logged in through fb");
                        }
                    }
                });

                return true;
            }
            case R.id.action_refresh:
                TVGuideSyncAdapter.syncImmediately(this);
                return true;

            default:
                return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Log.i("ScheduleListActivity, onTabSelected", tab.getText().toString());
        String tabText = tab.getText().toString();


        FragmentManager fm = getFragmentManager();
        TVGuideSelectionsFragment listFragment = (TVGuideSelectionsFragment) fm.findFragmentById(R.id.programmeschedule_list);
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
     * Callback method from {@link com.apperall.gabe.tvguide.UI.Fragments.TVGuideSelectionsFragment.Callbacks}
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
            arguments.putString(TVGuideProgrammesFragment.ARG_ITEM_ID, id);
            arguments.putString(TVGuideProgrammesFragment.ARG_SELECTION_TYPE, mSelectionType);
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
            Intent detailIntent = new Intent(this, TVGuideSelectionsActivity.class);
            detailIntent.putExtra(TVGuideProgrammesFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra(TVGuideProgrammesFragment.ARG_SELECTION_TYPE, mSelectionType);
            startActivity(detailIntent);
        }
    }




}
