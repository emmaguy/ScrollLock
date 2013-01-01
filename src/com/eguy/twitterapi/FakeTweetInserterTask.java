package com.eguy.twitterapi;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import com.eguy.SettingsManager;
import com.eguy.db.TweetProvider;

import java.util.Random;

public class FakeTweetInserterTask extends AsyncTask<Void, Void, Void>
{
    private Context context;
    private SettingsManager settingsManager;
    private static int counter = 0;

    public FakeTweetInserterTask(Context context, SettingsManager settingsManager)
    {
        this.context = context;
        this.settingsManager = settingsManager;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        ContentValues[] values = new ContentValues[20];
        for (int i = 0; i < 20; i++)
        {
            ContentValues tweetValue = new ContentValues();
            tweetValue.put(TweetProvider.TWEET_ID, Long.MAX_VALUE - counter);
            tweetValue.put(TweetProvider.TWEET_TEXT, "Generated tweet: " + counter++);
            tweetValue.put(TweetProvider.TWEET_CREATED_AT, "Sun Dec 31 23:35:11 +0000 2012");
            tweetValue.put(TweetProvider.TWEET_USER_ID, "7762342");
            tweetValue.put(TweetProvider.TWEET_USERNAME, "Nieuwsblad_be");
            tweetValue.put(TweetProvider.TWEET_PROFILE_PIC_URL, "http://a0.twimg.com/profile_images/26110972/avatarnbo_normal.jpg");
            values[i] = tweetValue;
        }
        context.getContentResolver().bulkInsert(TweetProvider.TWEET_URI, values);

        return null;
    }
}
