package com.apperall.gabe.tvguide.UI.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.apperall.gabe.tvguide.Model.Programme;
import com.apperall.gabe.tvguide.R;
import com.apperall.gabe.tvguide.UI.Fragments.TVGuideProgrammesFragment;


/**
 * An activity representing a single ProgrammeSchedule detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TVGuideMainActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link com.apperall.gabe.tvguide.UI.Fragments.TVGuideProgrammesFragment}.
 */
public class TVGuideSelectionsActivity extends Activity {

    private static final String TAG = TVGuideSelectionsActivity.class.getSimpleName();
    private Programme mProgramme;

    public Programme getProgramme() {
        return mProgramme;
    }

    public void setProgramme(Programme mProgramme) {
        this.mProgramme = mProgramme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmeschedule_detail);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TVGuideProgrammesFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(TVGuideProgrammesFragment.ARG_ITEM_ID));

            arguments.putString(TVGuideProgrammesFragment.ARG_SELECTION_TYPE,
                    getIntent().getStringExtra(TVGuideProgrammesFragment.ARG_SELECTION_TYPE));

            TVGuideProgrammesFragment fragment = new TVGuideProgrammesFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.programmeschedule_detail_container, fragment)
                    .commit();


            getActionBar().setTitle(getIntent().getStringExtra(TVGuideProgrammesFragment.ARG_ITEM_ID));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, TVGuideMainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
