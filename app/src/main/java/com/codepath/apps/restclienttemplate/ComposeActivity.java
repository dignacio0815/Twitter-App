package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    TextView tvCharCount;
    long uid;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etTweetInput = findViewById(R.id.etTweetInput);
        btnSend = findViewById(R.id.btnSend);
        // find view
        tvCharCount = findViewById(R.id.tvCharacterCounter);
        // add text changed listener
        etTweetInput.addTextChangedListener(inputCharacterCounter);
        uid = getIntent().getLongExtra("Comment", 0);
        String userHandle = getIntent().getStringExtra("User Handle");
        if (uid != 0) {
            etTweetInput.setText('@' + userHandle);
        }
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid != 0) {
                    client.sendComment(etTweetInput.getText().toString(), uid, new AsyncHttpResponseHandler() {
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
                } else {
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
            }
        });
        client = RestApplication.getRestClient(this);
    }

    private final TextWatcher inputCharacterCounter = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a text view to the current length
            tvCharCount.setText(String.valueOf(280 - s.length()));
            if (280 - s.length() == 0) {
                // when text reaches less than 0 characters, color turns red to signal too many characters
                tvCharCount.setTextColor(Color.parseColor("#FF0000"));
            }
        }
        public void afterTextChanged(Editable s) {
        }
    };
}
