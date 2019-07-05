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

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{ // reference to TweetAdapter
    private List<Tweet> mTweets;
    Context context;

    // pass in tweets array into constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }
    // for each row, inflate the layout and pass into viewholder class

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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // get the data according to the position
        Tweet tweet = mTweets.get(position);

        // populate views according to this data
        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvTime.setText(tweet.relativeTime);
        viewHolder.tvAtName.setText('@' + tweet.user.screenName);
        Glide.with(context).load(tweet.user.profileImageUrl).into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() { return mTweets.size(); }

    // create ViewHolder class, contain all findviewbyid lookups
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvTime;
        public TextView tvAtName;

        ImageButton ibComment;

        public ViewHolder(View itemView) {
            super(itemView);
            // perform findViewById
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            // rounding imageView
//            // TODO -- rounded image view
//            Bitmap bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_compose);
//            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap);
//            roundedBitmapDrawable.setCircular(true);
//            ivProfileImage.setImageDrawable(roundedBitmapDrawable);


            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
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
            // put extra for user_handle, tweet.user.screen_name
            // put extra

            }
        }

    /* Within the RecyclerView.Adapter class */

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
