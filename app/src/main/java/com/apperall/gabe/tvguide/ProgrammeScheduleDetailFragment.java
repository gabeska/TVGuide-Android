package com.apperall.gabe.tvguide;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a single ProgrammeSchedule detail screen.
 * This fragment is either contained in a {@link ProgrammeScheduleListActivity}
 * in two-pane mode (on tablets) or a {@link ProgrammeScheduleDetailActivity}
 * on handsets.
 */
public class ProgrammeScheduleDetailFragment extends ListFragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_SELECTION_TYPE = "selection_type";
    List<Programme> programmes;
    private static final String TAG = ProgrammeScheduleDetailFragment.class.getName();
    private ProgressDialog pd;
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
   // private  ProgrammeArrayAdapter mAdapter;
    private ProgrammeCursorAdapter mAdapter;
    //private ArrayAdapter<String> mAdapter;
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
        }



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

    private void refreshProgrammes() {
        if(isNetworkAvailable()) {
            GetProgrammesTask getTask = new GetProgrammesTask();
            getTask.execute(mItem);
        } else {
            Toast.makeText(getActivity(), "Network is unavailable", Toast.LENGTH_LONG).show();
        }
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


    private void sortScheduleProgrammes(String key) {

        final String sortKey=key;

        Collections.sort(programmes, new Comparator<Programme>() {
            @Override
            public int compare(Programme lhs, Programme rhs) {
                if (sortKey.equals("Channel")) {
                    return lhs.getChannel().compareTo(rhs.getChannel());
                } else if (sortKey.equals("Title")) {
                    return lhs.getTitle().compareTo(rhs.getTitle());

                } else if (sortKey.equals("Start")) {
                    return lhs.getStart().compareTo(rhs.getStart());

                }
                Log.e(TAG, "error: wrong sort key");
                return 0;
            }

        });
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
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

    private ArrayList<Programme> getData(String query) {

        ArrayList<Programme> programmes=new ArrayList<Programme>();
        Log.i(TAG,"getting data for "+query);
        try {


               URL programmesURL = new URL("http://192.168.0.42:4000/programmes/genre/"+query);

                if (mSelectionType.equals("Genres")) {
                    programmesURL = new URL("http://192.168.0.42:4000/programmes/genre/"+query);
                } else if (mSelectionType.equals("Channels")) {

                    String channelID;
                    // TODO: dit via db
                    if (query.equals("BBC 1")) {
                        channelID = "cbbh";
                    } else if (query.equals("BBC 2")) {
                        channelID = "cbbg";
                    } else if (query.equals("BBC 3")) {
                        channelID = "cbbp";
                    } else if (query.equals("BBC 4")) {
                        channelID = "cbbq";
                    } else if (query.equals("Nederland 1")) {
                            channelID = "1";
                    } else if (query.equals("Nederland 2")) {
                            channelID = "2";
                    } else if (query.equals("Nederland 3")) {
                            channelID = "3";

                    } else {
                        channelID = "error";
                    }


                    programmesURL = new URL("http://192.168.0.42:4000/programmes/channelid/"+channelID);
                    Log.i(TAG, programmesURL.toString());
                }



            HttpURLConnection connection = (HttpURLConnection) programmesURL.openConnection();

            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.i(TAG, "Code: "+responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder builder = new StringBuilder();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = reader.readLine())!=null) {
                    builder.append(line);
                }

                String responseData = builder.toString();
                //Log.v(TAG, responseData);
                //JSONObject jsonObject = new JSONObject(responseData);
                JSONArray programmeArray =  new JSONArray(responseData);

                Date now = new Date();

                for (int i=0; i<programmeArray.length(); i++) {

                    JSONObject jsonObject = programmeArray.getJSONObject(i);
                    if (jsonObject.getBoolean("show")==true) {

                        Programme programme = new Programme();

                        programme.setCategory(jsonObject.getString("category"));
                        programme.setTitle(jsonObject.getString("title"));
                        programme.setStart(jsonObject.getString("start"));
                        programme.setStop(jsonObject.getString("stop"));
                        programme.set_id(jsonObject.getString("_id"));
                        programme.setDesc(jsonObject.getString("desc"));
                        programme.setLength(jsonObject.getInt("length"));
                        programme.setShow(jsonObject.getBoolean("show"));
                        programme.setChannel(jsonObject.getString("channel"));
                        //programme.setUriStr(jsonObject.getString("uri"));
                        programme.setSource(jsonObject.getString("source"));


                        if(programme.getStop().after(now)) {

                            programmes.add(programme);
                        }
                    }
                }


            } else {
                Log.i(TAG, "unsuccessful HTTP response: "+responseCode);
            }

            connection.disconnect();


        } catch (MalformedURLException e) {
            Log.e("detail", "Exception: "+e.getMessage());
        } catch (IOException e) {
            Log.e("detail", "Exception: "+e.getMessage());

        } catch (Exception e) {
            Log.e("detail", "Exception: "+e.getMessage());

        }

        return programmes;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("PsDetailFragment", "onItemCLick");


       // ProgrammeDialogFragment dialogFragment = new ProgrammeDialogFragment();
       // Programme programme = mAdapter.getItem(position);
       // dialogFragment.setProgramme(programme);
       // FragmentManager fm = getFragmentManager();
        //Dialog dialog =  dialogFragment.getDialog();
        //dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE,0);

       // dialog.setTitle(programme.getTitle());
      //  dialogFragment.show(fm, "programmeDialog");



    }

    private class GetProgrammesTask extends AsyncTask<String,Void,ArrayList<Programme>> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setIndeterminate(true);
            pd.setTitle("getting data");
            pd.setMessage("please wait...");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected ArrayList<Programme> doInBackground(String... params) {
            return getData(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Programme> retrievedProgrammes) {
            //Log.i(TAG, strings.get(0));
            if(pd!=null) {
                pd.dismiss();
            }
           programmes.clear();
           programmes.addAll(retrievedProgrammes);
           mAdapter.notifyDataSetChanged();
        }
    }


    private class ProgrammeArrayAdapter extends  ArrayAdapter<Programme> {
        private final Context context;
        private final List<Programme>programmes;

        public ProgrammeArrayAdapter(Context context, List<Programme> programmes) {
            super(context, R.layout.programme_list_item_layout, programmes);
            this.context = context;
            this.programmes = programmes;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            if(convertView==null) {
                convertView = inflater.inflate(R.layout.programme_list_item_layout, parent, false);
          }

            TextView textStartDate = (TextView)convertView.findViewById(R.id.textStartDate);
            TextView textStart = (TextView)convertView.findViewById(R.id.textStart);
            TextView textStop = (TextView)convertView.findViewById(R.id.textStop);
            TextView textChannel = (TextView)convertView.findViewById(R.id.textChannel);
            TextView textTitle = (TextView)convertView.findViewById(R.id.textTitle);
            TextView textDesc = (TextView)convertView.findViewById(R.id.textDesc);

            Programme programme = programmes.get(position);

            textStartDate.setText(programme.getStartDateStr());
            textStart.setText(programme.getStartTime());
            textStop.setText(programme.getStopTime());
            textChannel.setText(programme.getChannel());
            textTitle.setText(programme.getTitle());
            textDesc.setText(programme.getDesc());
            convertView.setTag(programme);

/*
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Programme p = (Programme) v.getTag();
                    if (p != null) {
                        Log.i("onclicklistener", p.getTitle());
                    }

                    Log.i("programmearrayadapter", "onclicklistener");
                }
            });

*/
            return convertView;

        }


    }
}










