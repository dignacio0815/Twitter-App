package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel
public class Tweet { //purpose of class to translate data we receive into data we can use
    // list out attributes
    public String body;
    public long uid; // database ID for tweet
    public User user; //declaration of User class
    public String createdAt;
    public String relativeTime;
    public boolean isFavorited;
    public boolean isRetweeted;
    public int favoriteCount;
    public int favoriteRetweet;
    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract values from JSON into data we can use
         tweet.body = jsonObject.getString("text");
         tweet.uid = jsonObject.getLong("id");
         tweet.createdAt = jsonObject.getString("created_at");
         tweet.user = User.fromJSON(jsonObject.getJSONObject("user")); // calling User class to unpack user json object
         tweet.relativeTime = (String) getRelativeTimeAgo(tweet.createdAt);

         tweet.isFavorited = jsonObject.getBoolean("favorited");
         tweet.isRetweeted = jsonObject.getBoolean("retweeted");

         tweet.favoriteCount = jsonObject.getInt("favorite_count");
         tweet.favoriteRetweet = jsonObject.getInt("retweet_count");

         return tweet;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
