package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimeLineActivity extends AppCompatActivity {

    private TwitterClient client;
    SwipeRefreshLayout swipeContainer;
    private Long lastID = 0L;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        swipeContainer = findViewById(R.id.swipeContainer);

        // Find the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_bird);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        client = TwitterApp.getRestClient(this);

        // find the recycleview
        rvTweets = findViewById(R.id.rvTweet);
        // init the arraylist (data source)
        tweets = new ArrayList<>();
        // contruct the adapter from the data source
        tweetAdapter = new TweetAdapter(tweets);
        // RecycleView setup (layout manager, use adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);

        // setup refresh listener
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                lastID = 0L;

//            }
//        });

//        // Retain an instance so that you call you can call resetState() for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        } ;
//          Add the scrollListener to the recyclerView
         rvTweets.addOnScrollListener(scrollListener);
            populateTimeLineActivity();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        return true;
    }

    public void populateTimeLineActivity() {
        client.getHomeTimeLine(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                Log.d("TwitterClient", response.toString());
                // iterate through the JSON Array
                // for each entry, deserialize the JSON object

                for (int i = 0; i<response.length(); i++) {
                    // convert each object to a tweet model
                    // add that tweet model to our data source
                    // notify the adapter that we've addedan item

                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemChanged(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

//            private boolean isNetworkAvailable() {
//                ConnectivityManager connectivityManager
//                        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }



//    public void fetchTimelineAsync(int page) {
//        client.getHomeTimeline(new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                // Remember to CLEAR OUT old items before appending in the new ones
//                tweetAdapter.clear();
//                tweets.clear();
//
//                for (int i = 0; i < response.length(); i++) {
//                    // Convert each object to a tweet model
//                    // Add that tweet model to our data source
//                    // Notify the adapter that we've added an item
//                    try {
//                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
//                        tweets.add(tweet);
//                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                // Now we call setRefreshing(false) to signal refresh has finished
//                swipeContainer.setRefreshing(false);
//            }
//        });
//    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.myCompose){
            Toast.makeText(this, "Compose butoon click", Toast.LENGTH_SHORT).show();
            showAlertDialog();
        }
        
        return super.onOptionsItemSelected(item);
    }

    // Loading the data on scroll indefinitely
    public void loadNextDataFromApi(int offset) {
        Toast.makeText(this, "more", Toast.LENGTH_SHORT).show();
        client.Connect(offset , new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //iterate through the JSON array
                //For each entry, deserialize the json
                for (int i =0; i< response.length();i++) {
                    // convert each object ti a Tweet model
                    // Add that tweet model to our data source
                    //notify the adapter that we added an item
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        int position = tweets.size();
                        tweets.add(tweet);
                        tweetAdapter.notifyItemRangeInserted(position,1);
                        //tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient",responseString);
                throwable.printStackTrace();
            }
        } );

    }

    private void showAlertDialog() {
        FragmentManager fragment = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance("Some title");
        composeFragment.show(fragment, "fragment_compose");
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
