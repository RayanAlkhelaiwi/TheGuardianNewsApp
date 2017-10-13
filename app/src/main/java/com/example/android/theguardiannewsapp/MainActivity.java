package com.example.android.theguardiannewsapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rean on 10/13/2017.
 */

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    //Loader ID
    private static final int NEWS_LOADER_ID = 1;
    private static final String SAVED_INSTANCE = "NewsListing";
    /**
     * JSON response for the TheGuardian news API
     */
    private static final String THE_GUARDIAN_API_URL = "https://content.guardianapis.com/search?api-key=test&q=";
    private ListView newsListView;
    private View progressBar;
    private String theGuardianJSONurl;
    private EditText editText;
    private Button searchButton;
    private List<News> newsList;
    private TextView emptyTextView;
    private InputMethodManager inputManager;
    private ConnectivityManager cm;
    private NetworkInfo activeNetwork;
    private LoaderManager loaderManager;
    /**
     * Adapter for the list of news articles
     */
    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To remove the title text in the action bar
        setTitle("");

        Drawable drawableFetcher = getResources().getDrawable(R.drawable.theguardian);
        Bitmap bitmapDrawable = ((BitmapDrawable) drawableFetcher).getBitmap();
        Drawable theGuardianLogo = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmapDrawable, 880, 185, true));

        try {

            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(theGuardianLogo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        /**
         * Initializing the variables inside the onCreate method
         *
         */

        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Get connection information
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Initializing progress bar with its ID
        progressBar = findViewById(R.id.progress_bar);

        //Initializing edit text with its ID
        editText = (EditText) findViewById(R.id.search_edit_text);

        // Find a reference to the ListView in the layout
        newsListView = (ListView) findViewById(R.id.list);

        //Assigning List to an ArrayList of tybe News
        newsList = new ArrayList<News>();

        // Create a new adapter that takes an empty list of News as input
        mAdapter = new NewsAdapter(this, newsList);

        // Set the adapter on the ListView so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        //Set empty for the listView if there is no results
        newsListView.setEmptyView(findViewById(android.R.id.empty));

        //Set the search button to be clickable to do the search for the entered word
        searchButton = (Button) findViewById(R.id.search_button);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);

        //To get the saved instance of the app, that keep its data upon rotation
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE)) {
            newsList = savedInstanceState.getParcelableArrayList(SAVED_INSTANCE);
        } else {

            //Populate the data
            newsList = new ArrayList<News>();
            mAdapter = new NewsAdapter(this, newsList);
            newsListView.setAdapter(mAdapter);
        }

        //Set onClick for the search button to initiate the search in the query
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //Set progress bar visible to indicate the search is in process
                progressBar.setVisibility(View.VISIBLE);

                //To hide the keyboard when the Search button is clicked (Better UX?)
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                //Add the user's search to the JSON response
                theGuardianJSONurl = THE_GUARDIAN_API_URL + editText.getText().toString().trim().replace(" ", "%20");

                try {
                    activeNetwork = cm.getActiveNetworkInfo();
                    //Check if there is connection
                    if (activeNetwork != null && activeNetwork.isConnected()) {

                        //Clear the adapter when an attempt to re-search is an option
                        mAdapter.clear();
                        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                        // because this activity implements the LoaderCallbacks interface).
                        loaderManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                    } else {

                        //Clear the adapter when an attempt to re-search is an option
                        mAdapter.clear();

                        View progressBar = findViewById(R.id.progress_bar);
                        progressBar.setVisibility(View.GONE);

                        emptyTextView = (TextView) findViewById(android.R.id.empty);
                        emptyTextView.setText(R.string.no_internet);
                    }
                } catch (NetworkOnMainThreadException e) {
                    e.printStackTrace();
                }
            }
        });

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News NewURL = mAdapter.getItem(position);
                String newsURL = NewURL.getUrl();
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(newsURL));
                startActivity(urlIntent);
            }
        });
    }

    //To save the instance of the app, to keep its data upon rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_INSTANCE, (ArrayList<? extends Parcelable>) newsList);
    }

    //Used loader instead of AsyncTask to prevent the continuous fetch of the data from JSON response and avoid memory leaks
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, theGuardianJSONurl);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {

        progressBar.setVisibility(View.GONE);

        emptyTextView = (TextView) findViewById(android.R.id.empty);
        emptyTextView.setText(R.string.empty_view_text);

        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of news, then add them to the adapter's data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
            emptyTextView.setText(null);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }


    private static class NewsLoader extends AsyncTaskLoader<List<News>> {

        /**
         * Query URL
         */
        private String mUrl;

        public NewsLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public List<News> loadInBackground() {
            if (mUrl == null) {
                return null;
            }

            List<News> result = QueryUtils.fetchNewsData(mUrl);
            return result;
        }
    }
}