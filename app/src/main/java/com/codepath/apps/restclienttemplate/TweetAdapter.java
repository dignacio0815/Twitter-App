package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{ // reference to TweetAdapter
    private List<Tweet> mTweets;
    Context context;
    private RestClient mClient;

    // pass in tweets array into constructor
    public TweetAdapter(List<Tweet> tweets, RestClient client) {
        mTweets = tweets;
        mClient = client;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { //evokes a new row when needed
        // inflate the layout
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        // get the data according to the position
        final Tweet tweet = mTweets.get(position);

        // populate views according to this data
        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvTime.setText(tweet.relativeTime);
        viewHolder.tvAtName.setText('@' + tweet.user.screenName);

        viewHolder.tvFavorite.setText(Integer.toString(tweet.favoriteCount));
        viewHolder.tvRetweet.setText(Integer.toString(tweet.favoriteRetweet));

        if(tweet.isRetweeted) {
            viewHolder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
        } else {
            viewHolder.ibRetweet.setImageResource(R.drawable.ic_retweet);
        }

        if(tweet.isFavorited) {
            viewHolder.ibFavorite.setImageResource(R.drawable.ic_vector_heart_dark);
        } else {
            viewHolder.ibFavorite.setImageResource(R.drawable.ic_like);
        }

        viewHolder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.isFavorited) {
                    mClient.unFavoriteTweet(tweet.uid, new JsonHttpResponseHandler());
                    viewHolder.ibFavorite.setImageResource(R.drawable.ic_vector_heart_dark);
                    tweet.favoriteCount++;
                    viewHolder.tvFavorite.setText(Integer.toString(tweet.favoriteCount));
                    tweet.isFavorited = false;
                }
                else {
                    mClient.favoriteTweet(tweet.uid, new JsonHttpResponseHandler());
                    viewHolder.ibFavorite.setImageResource(R.drawable.ic_like);
                    tweet.favoriteCount--;
                    viewHolder.tvFavorite.setText(Integer.toString(tweet.favoriteCount));
                    tweet.isFavorited = true;
                }
            }
        });

        viewHolder.ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.isRetweeted) {
                    mClient.unRetweet(tweet.uid, new JsonHttpResponseHandler());
                    viewHolder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                    tweet.favoriteRetweet++;
                    viewHolder.tvRetweet.setText(Integer.toString(tweet.favoriteRetweet));
                    tweet.isRetweeted = false;
                }
                else {
                    mClient.retweet(tweet.uid, new JsonHttpResponseHandler());
                    viewHolder.ibRetweet.setImageResource(R.drawable.ic_retweet);
                    tweet.favoriteRetweet--;
                    viewHolder.tvRetweet.setText(Integer.toString(tweet.favoriteRetweet));
                    tweet.isRetweeted = true;

                }
            }
        });

        Glide.with(context).load(tweet.user.profileImageUrl).into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() { return mTweets.size(); }

    // create ViewHolder class, contain all lookups
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvBody;
        public TextView tvTime;
        public TextView tvAtName;
        public TextView tvUsername;

        public ImageButton ibRetweet;
        public ImageButton ibFavorite;
        public ImageButton ibReply;

        public TextView tvFavorite;
        public TextView tvRetweet;


        ImageButton ibComment;

        public ViewHolder(View itemView) {
            super(itemView);
            // perform findViewById
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);

            ibFavorite = itemView.findViewById(R.id.ibLike);
            ibRetweet = itemView.findViewById(R.id.ibRetweet);
            ibReply = itemView.findViewById(R.id.ibComment);

            tvFavorite = itemView.findViewById(R.id.tvFavoriteCount);
            tvRetweet = itemView.findViewById(R.id.tvRetweet);

            tvAtName = itemView.findViewById(R.id.tvAtName);
            ibComment = itemView.findViewById(R.id.ibComment);
            ibComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tweet tweet = mTweets.get(getAdapterPosition());
                    Intent comment = new Intent(v.getContext(), ComposeActivity.class);
                    comment.putExtra("Comment", tweet.uid);
                    comment.putExtra("User Handle", tweet.user.screenName);
                    v.getContext().startActivity(comment);
                }
            });
            }
        }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }


}
