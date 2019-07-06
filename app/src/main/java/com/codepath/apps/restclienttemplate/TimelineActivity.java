package com.codepath.apps.restclienttemplate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeContainer;

    public static final int COMPOSE_TWEET_REQUEST_CODE = 100;
    private RestClient client; // Creating our own twitter client
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    FloatingActionButton fabComposeTweet;
    // Instance of the progress action-view
    MenuItem miActionProgressItem;
    ConstraintLayout clTweet;
    ImageView image;
    private EndlessRecyclerViewScrollListener scrollListener;
    long maxId = 0;

    // progress indicator appears at top right of Twitter app when screen is refreshed
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Setup SwipeRefreshLayout
        // Only ever call `setContentView` once right at the top
        setContentView(R.layout.activity_timeline);

        client = RestApplication.getRestClient(this); // accessing REST client
        // find the recyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);

        //init array list
        tweets = new ArrayList<>();

        // construct adapter from data source
        tweetAdapter = new TweetAdapter(tweets, client);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(linearLayoutManager);
        // set the adaptergbtjbrknhljeihtuibhviccnhdktndug
        rvTweets.setAdapter(tweetAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
        fabComposeTweet = (FloatingActionButton) findViewById(R.id.fabComposeTweet);
        clTweet = (ConstraintLayout) findViewById(R.id.ibComment);
        image = findViewById(R.id.ivProfileImage);
        setUpBtnListener();
        populateTimeline(maxId);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void loadNextDataFromApi(int offset) {
        maxId = tweets.get(tweets.size() - 1).uid;
        populateTimeline(maxId);
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

    public void fetchTimelineAsync(final int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        showProgressBar();
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Remember to CLEAR OUT old items before appending in the new ones
                tweetAdapter.clear();
//                 ...the data has come back, add new items to your adapter...
                // tweets.addAll(tweets.addAll(response.getJSONArray(page)));
                // for each entry, deserialize json object
                for (int i = 0; i < response.length(); i++) {
                    // convert each Twitter object
                    // add that Tweet model to data source
                    // notify the adapter that we added a new source
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                hideProgressBar();
                tweetAdapter.notifyDataSetChanged();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                composeMessage();
                return true;

            case R.id.miLogout:
                signout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signout() {
        client.clearAccessToken();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == COMPOSE_TWEET_REQUEST_CODE && resultCode == RESULT_OK) {
            // this is mock code, refer to your previous code to extract Parcelable
            Tweet resultTweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            tweets.add(0, resultTweet);
            //notify adapter
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
            Toast.makeText(this, "Tweet post successful", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void composeMessage() {
        // open ComposeActivity to create a new tweet
        Intent composeTweet = new Intent(this, ComposeActivity.class);
        startActivityForResult(composeTweet, COMPOSE_TWEET_REQUEST_CODE);
    }

    private void composeReply() {
        // open compose activity to create a new comment/reply
        Intent composeReply = new Intent(this, ComposeActivity.class);
        startActivityForResult(composeReply, 200);
    }

    // method to populate timeline on feed
    private void populateTimeline(long maxId) {
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() { // creating an anonymous class to handle network call
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Twitter Client", response.toString()); // logs success if client responds
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // iterate through the json array

                // for each entry, deserialize json object
                for (int i = 0; i < response.length(); i++) {
                    // convert each Twitter object
                    // add that Tweet model to data source
                    // notify the adapter that we added a new source
                    Tweet tweet = null;
                    try {
                        tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Twitter Client", responseString);
                throwable.printStackTrace(); // prints out the error if API call fails
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("Twitter Client", errorResponse.toString());
                throwable.printStackTrace(); // prints out the error if API call fails
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Twitter Client", errorResponse.toString());
                throwable.printStackTrace(); // prints out the error if API call fails
            }
        });
    }

    public void setUpBtnListener() {
        fabComposeTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fabComposeTweet:
                        composeMessage();
                }
            }

            private void detailedTweet() {
                Toast.makeText(TimelineActivity.this, "constraint layout click", Toast.LENGTH_LONG).show();
            }
        });
    }
}


