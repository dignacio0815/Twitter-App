package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    public static final String RESULT_TWEET_KEY = "result_tweet";
    EditText etTweetInput;
    Button btnSend;
    RestClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etTweetInput = findViewById(R.id.etTweetInput);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    client.sendTweet(etTweetInput.getText().toString(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                try {
                                    // parsing response
                                    JSONObject responseJson = new JSONObject(new String(responseBody));
                                    Tweet resultTweet = Tweet.fromJSON(responseJson);

                                    // return result to calling activity
                                    Intent resultData = new Intent();
                                    resultData.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(resultTweet));

                                    // This is wrong!! Don't do it! Please!
//                            resultData.putExtra(RESULT_TWEET_KEY, resultTweet.toString());

                                    setResult(RESULT_OK, resultData);
                                    finish();
                                } catch (JSONException e) {
                                    Log.e("ComposeActivity", "Error parsing response", e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }
        });
        client = RestApplication.getRestClient(this);
    }
}
