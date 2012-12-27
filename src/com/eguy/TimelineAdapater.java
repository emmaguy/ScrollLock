package com.eguy;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.eguy.db.SavedTweet;

import java.util.List;

public class TimelineAdapater extends ArrayAdapter<SavedTweet>
{
    private Context context;
    private int timelineLayoutId;
    private List<SavedTweet> tweets;

    public TimelineAdapater(Context context, int timelineLayoutId, List<SavedTweet> tweets)
    {
        super(context, timelineLayoutId, tweets);

        this.context = context;
        this.timelineLayoutId = timelineLayoutId;
        this.tweets = tweets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(timelineLayoutId, parent, false);
        TextView tweetText = (TextView) rowView.findViewById(R.id.tweetText);
        TextView tweetUser = (TextView) rowView.findViewById(R.id.username);
        //ImageView userAvatar = (ImageView) rowView.findViewById(R.id.avatar);

        SavedTweet savedTweet = tweets.get(position);

        tweetText.setText(savedTweet.getTweetText());
        tweetUser.setText(String.valueOf(savedTweet.getId()));

        return rowView;
    }
}
