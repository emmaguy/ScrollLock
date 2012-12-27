package com.eguy;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.eguy.db.SavedTweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        TextView tweetCreatedAt = (TextView) rowView.findViewById(R.id.timestamp);

        SavedTweet savedTweet = tweets.get(position);

        tweetText.setText(savedTweet.getTweetText());
        tweetUser.setText(String.valueOf(savedTweet.getTweetUserId()));

        tweetCreatedAt.setText(getFormattedDateTime(savedTweet.getTweetCreatedAt()));

        return rowView;
    }

    private String getFormattedDateTime(String datetime)
    {
        String formattedDateTime = datetime;
        SimpleDateFormat desiredFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        try
        {
            Date dateObj = twitterFormat.parse(datetime);
            formattedDateTime = desiredFormat.format(dateObj);
        }
        catch (ParseException e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }
        return formattedDateTime;
    }
}
